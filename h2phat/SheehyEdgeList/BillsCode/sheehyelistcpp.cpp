//Programmer: Chris Tralie
//Purpose: To chain Bill's C++ cover tree code to Sheehy's warped metric output
//TO COMPILE, RUN:
//g++ -g -o sheehyelistcpp -DFLOAT sheehyelistcpp.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C Timer.C Point.C CoverNode.C EnlargeData.C -lpthreadg++ -g -o sheehyelistcpp -DFLOAT sheehyelistcpp.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C Timer.C Point.C CoverNode.C EnlargeData.C -lpthread

#include "Cover.H"
#include "string.h"
#include "Vector.H"
#include "ThreadsWithCounter.H"
#include "EnlargeData.H"
#include "Timer.H"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <iostream>
#include <fstream>
#include <vector>

using namespace std;

int dim=0;
REAL theta=0.0;
REAL mu=0.0;

//Outputs:
//Returns rootLevel by reference
//Returns radii by reference
//Returns an N x 5 array with all of the cover tree info
int* makeCoverTree(REAL* X, int N, int d, REAL theta, int numlevels, int minlevel, int NTHREADS, int BLOCKSIZE, int *rootLevel, std::vector<REAL>& radii) {
    mu=1.0/(1.0-theta);
    dim=d;
    Vectors vectors(X,N);

    ThreadsWithCounter threads(NTHREADS);

    SegList<DLPtrListNode<CoverNode> > seglist(1024);
    const Vector* vector=(const Vector*)(vectors.next());

    cout << "before cover\n";
    Cover cover(vector,seglist,numlevels,minlevel);
    cout << "after cover\n";
    EnlargeData enlargedata(&threads,BLOCKSIZE,vectors.getRemaining());
    Timer timer;
    timer.on();
    cover.enlargeBy(enlargedata,vectors);
    timer.off();
    timer.printOn(cout);

    *rootLevel = cover.getMinLevel();

    int* ret = new int[5*N];
    CoverIndices coverindices(&cover,&vectors,ret);

    //*radii = new REAL[numlevels];
    radii[0]=cover.getMaxRadius();
    for (int i=1;i<numlevels;i++) {
        radii[i]=theta*(radii[i-1]);
    }
    return ret;
}

//Return the squared distance between X_i and X_j
REAL getSqrDist(REAL* X, int N, int D, int i, int j) {
    REAL ret = 0.0;
    REAL diff = 0.0;
    for (size_t k = 0; k < D; k++) {
        diff = X[D*i + k] - X[D*j + k];
        ret += diff*diff;
    }
    return ret;
}

//Return by value e1, e2, ed.  Set e1 and e2 to -1 if the edge shouldn't be added
//This function figures out if and when an edge between vp and vq should be included
void getEdge(int p, int q, REAL* X, int N, int D, REAL* ts, int* e1, int* e2, REAL* ed, REAL eps) {
    REAL dpq = sqrt(getSqrDist(X, N, D, p, q));
    REAL tp = ts[p];
    REAL tq = ts[q];
    REAL alpha;

    //SEE SHEEHY PAGE 8
    if (tq < tp) {
        //Make sure tp indexes the earlier of the two death times
        REAL temp = tq;
        tq = tp;
        tp = temp;
    }
    //Case a (no warping has occurred yet)
    if (dpq < tp*(1-2*eps)) {
        *e1 = p;
        *e2 = q;
        *ed = dpq;
        return;
    }
    //Case b: alpha is between (1-2eps)tp and tp but is less than (1-2eps)tq
    alpha = 2*dpq - (1-2*eps)*tp;
    if (alpha >= (1-2*eps)*tp && alpha <= tp && alpha <= (1-2*eps)*tq) {
        *e1 = p;
        *e2 = q;
        *ed = dpq + 0.5*(alpha - (1-2*eps)*tp);//Relaxed distance
        return;
    }

    //Otherwise, it's never added
    *e1 = -1;
    *e2 = -1;
}

#include "FastSeg.C"
template class SegList<DLPtrListNode<CoverNode> >;

//Inputs: Point cloud filename, theta, epsilon, numLevels
int main(int argc, char** argv) {
    if (argc < 7) {
        cout << "Usage: sheehyelistcpp <pcfilename> <outfilename> <theta> <epsilon> <numLevels> <minlevel>";
        return 0;
    }
    char* infilename = argv[1];
    char* outfilename = argv[2];
    REAL theta = atof(argv[3]);
    REAL epsilon = atof(argv[4]);
    int numlevels = atoi(argv[5]);
    int minlevel = atoi(argv[6]);

    std::vector<REAL> radii(numlevels);
    int* levels; //Cover tree info
    int rootLevel;

    int N, D, NLevels;
    //Load in X matrix
    fstream fin;
    fin.open(infilename, fstream::in);
    fin >> N;
    fin >> D;
    REAL* X = new REAL[N*D];
    //Bill's code expects point clouds the opposite way from Matlab
    for (int i = 0; i < N; i++) {
        for (int k = 0; k < D; k++) {
            fin >> X[i*D+k];
        }
    }
    fin.close();

    //Call Bill's cover tree code
    levels = makeCoverTree(X, N, D, theta, numlevels, minlevel, 4, 32, &rootLevel, &radii);

    ///////////////ALGORITHM/////////////////
    vector<int> e1List;//Edges endpoint 1
    vector<int> e2List;//Edges endpoint 2
    vector<REAL> edList;//Edges relaxed distance

    //Calculate deletion times based on cover tree info
    REAL* ts = new REAL[N];
    for (int i = 0; i < N; i++) {
        int l = levels[i] - rootLevel;
        ts[i] = 9*radii[l]/theta;//9*radius of parent
    }

    //Check all pairs of edges (slow version)
    for (int i = 0; i < N; i++) {
        for (int j = i+1; j < N; j++) {
            int e1, e2;
            REAL ed;
            getEdge(i, j, X, N, D, ts, &e1, &e2, &ed, epsilon);
            if (e1 != -1 && e2 != -1) {
                e1List.push_back(e1);
                e2List.push_back(e2);
                edList.push_back(ed);
            }
        }
    }
    //Output edges with their weights to a file
    ofstream fout;
    fout.open(outfilename, fstream::out);
    for (size_t i = 0; i < e1List.size(); i++) {
        cout << e1List[i] << " " << e2List[i] << " " << edList[i];
    }
    fout.close();

    ///////////////CLEANUP/////////////////
    delete[] ts;
    delete[] levels;
    delete[] radii;
}
