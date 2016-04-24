#include <iostream>
#include <bmatrix.h>
#include <string>
#include <istream>
#include <sstream>
#include <fstream>
#ifndef _OPENMP
#include <omp.h>
#endif // _OPENMP

#ifdef __APPLE__
	typedef unsigned int uint;
#endif

// wrapper algorithm that computes the persistence pairs of a given boundary matrix using a specified algorithm
#include <phat/compute_persistence_pairs.h>

// main data structure (choice affects performance)
#include <phat/representations/vector_vector.h>
#include <phat/representations/bit_tree_pivot_column.h>

// algorithm (choice affects performance)
#include <phat/algorithms/standard_reduction.h>
#include <phat/algorithms/chunk_reduction.h>
#include <phat/algorithms/row_reduction.h>
#include <phat/algorithms/twist_reduction.h>

#include <phat/helpers/dualize.h>
extern "C" {
#include "cliquer.h"
}

#include <boost/program_options.hpp>
#include <exception>
#include <chrono>

// We're not interested in clique sizes below 3 (triangles), as we already have them.
static int MinSize = 3;

//  This global will hold cliques of dim > 2
std::vector<std::vector<cliques::clique>> highercliques;

// This global will hold the number of simpicies of each dimension
std::vector<cliques::index> dimborders;

struct CliqueComparerByWeight
{
    bool operator() ( const cliques::clique& lhs, const cliques::clique& rhs ) const
        { return (lhs.get_weight()) < (rhs.get_weight()); }
};

void add_vertices_to_vector(cliques::index& vertices, std::vector<cliques::clique>& cliquevector)
{
    for ( cliques::index i=0; i<vertices; ++i )
    {
        cliques::clique c({i},i);
        cliquevector.emplace_back(std::move(c));
    }
}

boolean record_clique_direct(set_t s, graph_t *g, clique_options *opts)
{
    int curcliquedim = 0;

    cliques::boundary bdry;

    for (cliques::index j = 0; j < SET_MAX_SIZE(s); ++j)
    {
        // If the vertex 'j' is in the clique, add j to the current clique, and increase its dimension.
        if (SET_CONTAINS(s, j))
        {
            bdry.emplace_back(j);
            ++curcliquedim;
        }
    }

    // create clique with found boundary
    // TODO: move bdry instead of copying?  Or create clique earlier and set boundary dynamically?
    cliques::clique c(bdry,1);
    highercliques[curcliquedim-MinSize].emplace_back(std::move(c));

    return TRUE;
}

graph_t* read_extdimacs_file(std::istream& dimacs, std::string& dimacs_err, std::vector<cliques::clique>& cliquevector) {

    graph_t *gr;
    cliques::index vertices = 0, edges = 0, cliques_added=0;
    double weight = 0;

    std::string line;
    while (getline(dimacs, line))
    {
        std::istringstream iss(line);
        char ch;
        if (iss >> ch)
        {

            std::string format;

            switch(ch) {
                case 'c': break;
                case 'p':
                    if (vertices||edges)
                        { dimacs_err = "Corrupt file: More than one line starting with p!"; break; }
                    if (iss >> format >> vertices >> edges)
                    {
                        if ("edge" != format)
                            { dimacs_err = "Corrupt file: Line beginning with p does not have 'edge' as its next word!"; }
                        else
                            {
                                gr = graph_new(vertices);
                                cliquevector.reserve(vertices+edges);
                                add_vertices_to_vector(vertices,cliquevector);
                                cliques_added += vertices;
				dimborders.push_back(vertices);
				dimborders.push_back(edges);
                            }
                    }
                    break;
                case 'e':
                    if ( edges-- )
                    {
                        cliques::index from, to;
                        if (iss >> from >> to >> weight )
                            {
                                // for each edge in the file, add it both the graph and the clique vector
                                GRAPH_ADD_EDGE(gr, from, to);
                                cliques::clique c({from,to},cliques_added,weight);
                                cliquevector.emplace_back(std::move(c));
                                ++cliques_added;
                            }
                        else
                            { dimacs_err = "Corrupt file: Line beginning with e does not contain two vertices and a weight!"; }
                    }
                    break;

                default:
                    dimacs_err = "Corrupt file: Line begins with unknown character (not c, p, or e)!";
            }
            if ( dimacs_err != "" )
                { break; }
        }
    }

    // Just to be cautious, ensure that we've produced a valid graph.
    graph_test(gr, NULL);

    // Sort edges by weight and rewrite clique numbers accordindly
    __gnu_parallel::sort(cliquevector.begin()+vertices,cliquevector.end(),CliqueComparerByWeight());
    for ( cliques::index i = vertices; i<cliques_added; ++i )
        { cliquevector[i].set_cliquenum(i); }

    return gr;
}

cliques::index FindCliques(graph_t *gr, cliques::sz_t MaxSize, clique_options &localopts)
{
    cliques::index NumCliques;

    // Find all of the maximal (argument 4) cliques in this graph of
    // minimum size MinSize (argument 2) and maximum size MaxSize (argument 3).
    NumCliques = clique_find_all(gr, MinSize, MaxSize, 0, &localopts);

    graph_free(gr);

    return NumCliques;
}

void find_inf_pers( bmatrix& boundarymatrix, phat::persistence_pairs& pairs )
{
    std::vector<std::vector<cliques::index>> finpers(dimborders.size()-1,std::vector<cliques::index>({}));
    std::vector<std::vector<cliques::index>> infpers(dimborders.size()-1,std::vector<cliques::index>({}));

    std::vector<cliques::index> dimbordersext = dimborders;
    dimbordersext.insert(dimbordersext.begin(),0);

    for (cliques::index i = 0; i<pairs.get_num_pairs(); ++i )
    {
        auto p = pairs.get_pair(i);
        cliques::sz_t d = (boundarymatrix[p.first]->get_size()) - 1;
        finpers[d].push_back(p.first);
        if ( d<(dimborders.size()-2) ) { finpers[d+1].push_back(p.second); }
    }

    for ( int i=0; i<finpers.size(); ++i )
    {
        __gnu_parallel::sort(finpers[i].begin(),finpers[i].end());
        std::vector<cliques::index> range(dimborders[i]);
        std::iota(range.begin(),range.end(),std::accumulate(dimbordersext.begin(),dimbordersext.begin()+i+1,0));

        infpers[i].reserve(range.size() + finpers[i].size());
        auto it=std::set_difference(range.begin(),range.end(),finpers[i].begin(),finpers[i].end(),infpers[i].begin());
        infpers[i].resize(it-infpers[i].begin());

    }

    for ( auto i : infpers )
        { for ( auto j : i) { pairs.append_pair(j,-1); }}
}

void run_phat(bmatrix& boundarymatrix,phat::persistence_pairs& pairs)
{
    phat::boundary_matrix< phat::bit_tree_pivot_column > phatboundarymatrix;

    phatboundarymatrix.set_num_cols( boundarymatrix.size() );

    std::vector< phat::index > temp_col;
    std::vector< phat::index > zero_col = temp_col;

    cliques::index count=0;

    for ( auto c : boundarymatrix.get_boundaryvector() )
    {
        phatboundarymatrix.set_dim(count,(c->get_size())-1);
        if ( c->get_weight() == 0 ) { phatboundarymatrix.set_col(count,zero_col); }

        else {phatboundarymatrix.set_col(count,c->bdry); }

        // This clears the boundary so that the clique takes less memory....
        // It saves ~12% memory at a cost of adding 50% time to the PHAT compuation
        //cliques::boundary().swap(c->bdry);

        count++;
    }

    // TODO: make the algorithm user-selectable
    // Is there any point in allowing dualization to be user-selectable?  Seems to always be faster.
    dualize( phatboundarymatrix );
    phat::compute_persistence_pairs< phat::chunk_reduction >( pairs, phatboundarymatrix );

    dualize_persistence_pairs( pairs, boundarymatrix.size() );

    // Compute infinite persistence pairs
    find_inf_pers(boundarymatrix,pairs);

    pairs.sort();
}

void output_pairs(bmatrix& boundarymatrix, phat::persistence_pairs& pairs, std::string filename)
{
    std::ofstream output_stream( filename.c_str() );

    for ( cliques::index i=0; i<pairs.get_num_pairs(); ++i )
    {
        if ( pairs.get_pair(i).second != -1 )
            { output_stream << (boundarymatrix[pairs.get_pair(i).first]->get_size())-1 << " " <<  pairs.get_pair(i).first << " " << pairs.get_pair(i).second << " "
                << boundarymatrix[pairs.get_pair(i).first]->get_weight() << " " << boundarymatrix[pairs.get_pair(i).second]->get_weight() << std::endl; }
        else
            { output_stream << (boundarymatrix[pairs.get_pair(i).first]->get_size())-1 << " " <<  pairs.get_pair(i).first << " " << pairs.get_pair(i).second << " "
                << boundarymatrix[pairs.get_pair(i).first]->get_weight() << " -1" << std::endl; }
    }
}

void parse_command_line( int argc, char** argv, std::string& infile, int& maxsize, std::string& outputfile, std::string& boundaryfile )
{
    using namespace boost::program_options;

    options_description desc("Command-line switches");
    desc.add_options()
        ("input,i", value< std::string >()->required(), "input file")
        ("maxsize,m", value<int>()->required(), "maximum size of clique (i.e. max dimension + 1)")
        ("output,o", value< std::string >()->required(), "output file (pairs)")
        ("boundary,b", value< std::string >(), "boundary matrix output file");

    variables_map vm;
    try
    {
        store(command_line_parser(
        argc, argv).options(desc).run(), vm);
        notify(vm);
    } catch (std::exception &e)
        {
            std::cout << "Program usage: " << argv[0] << " -i <input file> -m <max size> -o <pairs output file> [-b <boundary output file>]" << std::endl;
            exit(1);
        }

    if (vm.count("input"))
    {
        infile = vm["input"].as<std::string>();
    }

    if (vm.count("output"))
    {
        outputfile = vm["output"].as<std::string>();
    }

    if ( vm.count("maxsize") )
    {
        maxsize=vm["maxsize"].as<int>();
    }

    if ( vm.count("boundary") )
    {
        boundaryfile=vm["boundary"].as<std::string>();
    }

}

int main(int argc, char **argv)
{

    std::string infile, outputfile, boundaryfile;
    int maxsize;

    parse_command_line(argc, argv, infile,maxsize,outputfile,boundaryfile);

    std::ifstream inputfile(infile);

    std::string dimacs_error;

    std::vector<cliques::clique> cliquevector;

    bmatrix::bmatrixvector cliquepointers;


    // Read DIMACS file (with weights)
    std::cout << "Reading input file...." << std::flush;
    std::chrono::high_resolution_clock::time_point t1 = std::chrono::high_resolution_clock::now();
    graph_t* gr = read_extdimacs_file(inputfile, dimacs_error, cliquevector);

    if ( dimacs_error != "")
    {
        std::cerr << dimacs_error << "\n";
        return 1;
    }

    std::chrono::high_resolution_clock::time_point t2 = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> time_elapsed =  t2-t1;
    std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;

    // Compute how many dim's above 2 we're working on
    int numhigherdim=maxsize-MinSize+1;

    // TODO: think about preallocating these roughly (approx how many cliques of each dim are there?)
    for ( int i=0; i<numhigherdim; ++i)
        { std::vector<cliques::clique> v; highercliques.emplace_back(std::move(v)); }

    // Set the clique_options (for Cliquer).
    std::cout << "Using Cliquer to compute cliques...." << std::flush;
    t1 = std::chrono::high_resolution_clock::now();
    clique_options localopts;
    localopts.time_function      = NULL;
    localopts.reorder_function   = reorder_by_greedy_coloring;
    localopts.reorder_map        = NULL;
    localopts.clique_list        = NULL;
    localopts.clique_list_length = 0;
    localopts.user_function      = record_clique_direct;
    localopts.user_data          = NULL;

    // Compute cliques using Cliquer, store in cliques
    cliques::index numcliques = FindCliques(gr, maxsize, localopts);

    // Concatenate the higher dimensional vectors
    for ( cliques::sz_t i=0; i<highercliques.size(); ++i )
    {
        cliquevector.reserve(cliquevector.size() + highercliques[i].size());
        cliquevector.insert(cliquevector.end(),std::make_move_iterator(highercliques[i].begin()),std::make_move_iterator(highercliques[i].end()));
        dimborders.push_back(highercliques[i].size());
    }

    // release memory allocated to highercliques, as it's now in cliquevector.
    highercliques.clear();

    // Create vectors of pointers to cliques (and set clique numbers)
    cliquepointers.reserve(cliquevector.size());
    cliques::index counter = 0;
    for (auto it=cliquevector.begin(); it!=cliquevector.end(); ++it )
    {
        it->set_cliquenum(counter);
        cliquepointers.emplace_back(&(*it));
        counter++;
    }
    t2 = std::chrono::high_resolution_clock::now();
    time_elapsed =  t2-t1;
    std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;

    // Initialize boundary matrix structure
    std::cout << "Transforming boundary matrix from vertices to faces...." << std::flush;
    t1 = std::chrono::high_resolution_clock::now();
    bmatrix boundarymatrix(cliquepointers);

    boundarymatrix.set_dimborders(dimborders);
    // Rewrite matrix using faces instead of vertices, compute weights, and sort by them.
    boundarymatrix.transformboundarymatrix();

    boundarymatrix.destroymap();

    t2 = std::chrono::high_resolution_clock::now();
    time_elapsed =  t2-t1;
    std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;

    if ( !boundaryfile.empty() )
    {
        std::cout << "Outputting boundary matrix to " << boundaryfile << "...." << std::flush;
        t1 = std::chrono::high_resolution_clock::now();
        boundarymatrix.output_bmatrix(boundaryfile);
        t2 = std::chrono::high_resolution_clock::now();
        time_elapsed =  t2-t1;
        std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;
    }

    std::cout << "Reducing boundary matrix using PHAT...." << std::flush;
    t1 = std::chrono::high_resolution_clock::now();
    phat::persistence_pairs pairs;
    boundarymatrix.set_dimborders(dimborders);
    run_phat(boundarymatrix, pairs);
    t2 = std::chrono::high_resolution_clock::now();
    time_elapsed =  t2-t1;
    std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;

    // output pairs to file
    std::cout << "Outputting persistence pairs to " << outputfile << "...." << std::flush;
    t1 = std::chrono::high_resolution_clock::now();
    output_pairs(boundarymatrix, pairs, outputfile);
    t2 = std::chrono::high_resolution_clock::now();
    time_elapsed =  t2-t1;
    std::cout << "DONE!" << " (time taken: " << time_elapsed.count() << " seconds)" << std::endl;

    return 0;

}
