#include <vector>
#include <cstdlib>
#include <iostream>
#include <boost/functional/hash.hpp>


namespace cliques
{

// Type definitions
typedef int64_t index; // int64 to handle over a billion cliques
                        // change int32 to save memory
typedef uint8_t sz_t;   // size of column = (dim + 1)
typedef std::vector<index> boundary;

class clique
{
public:
    boundary bdry;
private:
    index cliquenum;
    double weight;
    sz_t sz;

    // Constructors and operators
public:
    clique(): bdry(std::vector<index>{}),cliquenum(0),weight(0),sz(0) {};

    // Construct clique without weight
    clique( boundary& v, index cnum ):bdry(v),cliquenum(cnum),weight(0) { sz=bdry.size(); }

    // Construct clique with weight
    clique( boundary& v, index cnum, double wt ): bdry(v),cliquenum(cnum),weight(wt) { sz=bdry.size(); }

    // Construct clique with weight from brace-enclosed list
    clique( std::initializer_list<index> l, index cnum, double wt ): bdry(l),cliquenum(cnum),weight(wt) { sz=bdry.size(); }

    // Construct clique without weight from brace-enclosed list
    clique( std::initializer_list<index> l, index cnum ): bdry(l),cliquenum(cnum),weight(0) { sz=bdry.size(); }

    // Destructor
    ~clique() {}

    // Copy Constructor
    clique( const clique& other ): bdry(other.bdry),cliquenum(other.cliquenum),weight(other.weight),sz(other.sz) {}

    // Copy assignment operator
    clique& operator=(clique& other)
    {
        if ( *this != other )
        {
            bdry=other.bdry;
            cliquenum=other.cliquenum;
            weight=other.weight;
            sz=other.sz;
        }

        return *this;
    }

    // Move constructor
    clique(clique&& other): bdry(std::vector<index>{}),cliquenum(0),weight(0),sz(0)
    {
        bdry=other.bdry;
        cliquenum=other.cliquenum;
        weight=other.weight;
        sz=other.sz;
    }

    // Move assignment operator
    clique& operator=(clique&& other)
    {
        if ( *this != other )
        {
            bdry={};
            cliquenum=0;
            weight=0;
            sz=0;

            bdry=other.bdry;
            cliquenum=other.cliquenum;
            weight=other.weight;
            sz=other.sz;

            other.bdry={};
            other.cliquenum=0;
            other.weight=0;
            other.sz=0;
        }

        return *this;
    }

    // Two cliques are equal if their boundaries are equal.
    bool operator==( const clique& other_clique ) const
    {
        return ( bdry == other_clique.bdry );
    }

    bool operator!=( const clique& other_clique ) const
    {
        return !( *this == other_clique );
    }

    // For sorting purposes, cliques are ordered by weight.
    bool operator<(const clique& other_clique) const
    {
        return ( weight < other_clique.weight );
    }

    bool operator>(const clique& other_clique) const
    {
        return ( other_clique < *this );
    }

    bool operator<=(const clique& other_clique) const
    {
        return !( *this > other_clique );
    }

    bool operator>=(const clique& other_clique) const
    {
        return !( *this < other_clique );
    }

    // Functions to get and set clique attributes
public:
    sz_t get_size() const
    {
        return sz;
    }

    void print_boundary() const
    {
        for ( const index i: bdry )
        {
            std::cout << i << ' ';
        }
    }

    const boundary& get_boundary() const
    {
        return bdry;
    }

    void set_boundary(boundary b)
    {
        bdry = b;
    }

    double get_weight() const
    {
        return weight;
    }

    void set_weight(double& w)
    {
        weight = w;
    }

    index get_cliquenum() const
    {
        return cliquenum;
    }

    void set_cliquenum(index i)
    {
        cliquenum = i;
    }
};

class bHash
{
public:
    // Uses Boost to hash a boundary
    std::size_t operator()( const boundary& bdry ) const
    {
        return boost::hash_range(bdry.begin(), bdry.end());
    }

};

}
