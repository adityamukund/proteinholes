#include <clique.h>
#include <unordered_map>
#include <algorithm>
#ifndef _OPENMP
#include <omp.h>
#endif // _OPENMP
#include <parallel/algorithm>
#include <fstream>

class bmatrix
{
    public:
        typedef std::unordered_map<cliques::boundary, cliques::clique*, cliques::bHash> bmatrixmap;
        typedef std::vector<cliques::clique*> bmatrixvector;

    private:
        bmatrixmap      bmat;
        bmatrixvector   bvec;
        std::vector<cliques::index> dimborders;

        // Constructors and operators

    public:
        bmatrix():bmat({}),bvec({}) {};

        // TODO: can the next two constructors be templated?
        // TODO: For map, there only needs to be space reserved for cliques of size (maxdim-1).
        // If called with a vector of pointers to cliques, add all of the cliques
        explicit bmatrix(bmatrixvector& cliquematrix):bmat({}),bvec(cliquematrix.begin(),cliquematrix.end())
            {
                bmat.reserve(bvec.size());
                createmap( bvec, bmat);
            }

        // If called with a brace-enclosed list of pointers to cliques, add them all.
        explicit bmatrix(std::initializer_list<cliques::clique*> cliquematrix):bmat({}),bvec({})
            {
                for ( auto c: cliquematrix )
                    {
                        bmat[c->get_boundary()] = c;
                        bvec.push_back(c);
                    }
            }

        // TODO: can the next two operators be templated?
        // locate clique by a boundary, specified as a vector
        cliques::clique* operator[](cliques::boundary& b)
            { return ( bmat[b] ); }

        // locate clique by a boundary, specified by a brace-enclosed list of indices
        cliques::clique* operator[](std::initializer_list<cliques::index> b)
            { return ( bmat[b] ); }

        // locate clique by its index
        cliques::clique* operator[](cliques::index i)
            { return ( bvec[i] ); }

        bmatrixvector& get_boundaryvector ()
            { return bvec; }

        void output_bmatrix(std::string filename)
        {
            std::ofstream output_stream( filename.c_str() );

            for ( auto c : bvec )
            {
                output_stream << (c->get_size())-1 << " ";
                if ( c->get_size() > 1 )
                {
                    for ( auto i : c->get_boundary() )
                        { output_stream << i << " "; }
                }
                output_stream << std::endl;
            }
        }


	void set_dimborders(std::vector<cliques::index> db)
	    { dimborders = std::move(db); }

	std::vector<cliques::index> get_dimborders()
	    { return dimborders; }

        cliques::index size()
            { return bvec.size(); }

        void destroymap()
            { bmatrixmap().swap (bmat); }

    // Methods
    private:

        void createmap(bmatrixvector& cliquematrix, bmatrixmap& bmat_priv)
        {
            // NOTE: this only works if our vectors are sorted by size!
            cliques::sz_t maxdim = cliquematrix.back()->get_size();
            #pragma omp parallel for schedule(static)
            for ( cliques::index i=0; i<cliquematrix.size(); ++i )
            {
                cliques::clique* c = cliquematrix[i];
                if ( c->get_size() != maxdim)
                { 
			    const cliques::boundary b = c->get_boundary();
			    // For some reason, the following doesn't add all cliques when run without critical
			    // More weirdly, this only occurs with Chris's new cover tree code
			    //#pragma omp critical
			    bmat_priv[b] = c; }
		}
        }

        void verticestofaces(cliques::clique*& c)
        {

            cliques::sz_t sz = c->get_size();

            if ( sz > 2 )
            {
                double wt = 0;

                cliques::boundary vbdry = c->get_boundary();

                // facebdry will contain the faces of c
                cliques::boundary facebdry(sz);

                // temporary holder for faces of c listed as vertices
                cliques::boundary t(sz-1);

                // compute the boundary of c as faces, one face at a time
                for ( cliques::sz_t i=0; i<sz; ++i )
                {
                    // These for loops remove the i'th element from vbdry,
                    // resulting in a list of vertices of a face of c.
                    for ( cliques::sz_t j=0; j<i; ++j )
                            { t[j]=vbdry[j]; }
                    for ( cliques::sz_t j=i+1; j<sz; ++j )
                            { t[j-1]=vbdry[j]; }

                    // Look up the clique corresponding to the face
                    cliques::clique* face = bmat[t];

                    // Check if this face has greater weight.  If so, make it the new weight.
                    double faceweight = face->get_weight();
                    if ( faceweight > wt )
                            { wt = faceweight; }

                    // Store the face's clique number in temp boundary vector
                    facebdry[sz-i-1] = face->get_cliquenum();
                }

                // Set properties for clique
                c->set_boundary(facebdry);
                c->set_weight(wt);
            }
        }

        // This gives an ordering on pointers to cliques.  It's dangerous and should not be used externally.
        struct CliquePointerComparerByWeight
        {
            bool operator() ( const cliques::clique* lhs, const cliques::clique* rhs ) const
                { return (lhs->get_weight()) < (rhs->get_weight()); }
        };

    public:
        // This function assumes that bvec is sorted by dimension.
        void transformboundarymatrix()
        {
            cliques::sz_t maxsize = bvec.back()->get_size();

            // start the transformation from triangles, as edges are already set
            cliques::index mincliquenum = dimborders[0]+dimborders[1];

            for (cliques::sz_t i = 1; i<(maxsize-1); ++i )
            {
                cliques::index maxcliquenum = mincliquenum+dimborders[i+1];

                // Rewrite cliques of size (i+2) with faces instead of vertices
                // This also computes the weights
                #pragma omp parallel for schedule(dynamic)
                for ( auto c = bvec.begin()+mincliquenum; c < bvec.begin()+maxcliquenum; c++ )
                    { verticestofaces(*c); }

                // Sort vector of pointers to cliques of size (i+2) by weight of cliques pointed to
                __gnu_parallel::sort(bvec.begin()+mincliquenum,bvec.begin()+maxcliquenum,CliquePointerComparerByWeight());

                // Reset clique numbers for sorted cliques of size (i+2)
                // TODO: is this needed for top dim?
                if ( i != (maxsize-2) )
                {
                #pragma omp parallel for schedule(dynamic)
                for ( cliques::index j=mincliquenum; j<maxcliquenum; ++j )
                    { bvec[j]->set_cliquenum(j); }

                mincliquenum = maxcliquenum;
            }

            // For debugging purposes only
            #ifndef NDEBUG
            //for ( auto c: bvec )
            //{
             //   std::cout << "Clique " << c->get_cliquenum() << " has boundary: ";
              //  c->print_boundary();
               // std::cout << " and weight: " << c->get_weight() << "\n";
            //}
            #endif // NDEBUG
        }
    }
};
