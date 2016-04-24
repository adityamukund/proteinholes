// g++ -g -o testfindnearest -DDOUBLE testfindnearest.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C findWithin.C Timer.C Point.C CoverNode.C EnlargeData.C FindWithinData.C findNearest.C -lpthread

#include "Cover.H"
//#include "DisjointLists.H"
#include "EnlargeData.H"
#include "FindWithinData.H"
#include "FindNearestData.H"
#include "ThreadsWithCounter.H"
#ifdef MEM_DEBUG
#include "MemoryDebugger.H"
#endif
#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <assert.h>

#include <sys/times.h>
#include <sys/param.h>

#include "Vector.H"
#include "Timer.H"

#include <vector>

#include <mcheck.h>

using namespace std;

ThreadsWithCounter* threads=0;


REAL theta=0.0;
REAL mu=0.0;

int dim=1;

void f(double* X,double* Y) {
  for(int i=0;i<2*500;i++) {
    X[i]=drand48();
  }
  for(int i=0;i<2*200;i++) {
    Y[i]=drand48();
  }
}

int main(int argc,char* argv[]) {

  /*
  mtrace();
  */



#ifdef MEM_DEBUG
  MemoryDebugger md;
#endif

  /*
  int* x=new int[20];
  x[21]=5;
  */

  dim=2;
  theta=0.5;
  mu=1.0/(1.0-theta);
  int numlevels=20;

  int niter=atoi(argv[1]);

  int NTHREADS=0;
  int BLOCKSIZE=0;

  if(argc==4) {
    NTHREADS=atoi(argv[2]);
    BLOCKSIZE=atoi(argv[3]);
  }

  ThreadsWithCounter threads(NTHREADS);
  
  int N=500;
  int M=200;
#ifdef MEM_DEBUG
  REAL* X=OPERATOR_NEW_BRACKET(REAL,2*N);
  REAL* Y=OPERATOR_NEW_BRACKET(REAL,2*M);
#else
  REAL* X=new REAL[2*N];
  REAL* Y=new REAL[2*M];
#endif

  SegList<DLPtrListNode<CoverNode> > seglist(1024);

  int tofind=20;
#ifdef MEM_DEBUG
  const Point** ptarr=OPERATOR_NEW_BRACKET(const Point*,(tofind+1)*N);
  REAL* distances=OPERATOR_NEW_BRACKET(REAL,tofind*M);
#else
  const Point** ptarr=new const Point*[(tofind+1)*N];
  REAL* distances=new REAL[tofind*M];
#endif

  cout<< "ptarr=" << ptarr << " distances=" << distances << endl;

  for(int n=0;n<niter;n++) {
    cout << "n=" << n << endl;
    {
      f(X,Y);
      Vectors treevects(X,N);
      Vectors nearvects(Y,M);
      
      const Vector* vect=(const Vector*)(treevects.next());
      Cover cover(vect,seglist,numlevels,0);
      EnlargeData enlargedata(&threads,BLOCKSIZE,treevects.getRemaining());
      cover.enlargeBy(enlargedata,treevects);

      int L=cover.getMaxLevelPresent();
      FindNearestData findnearestdata(&threads,nearvects,tofind,L,ptarr,distances);
      treevects.reset();
      cover.findNearest(nearvects,findnearestdata,ptarr,distances);
      
      int* indices=new int[tofind*M];
      double* outdistances=new double[tofind*M];
      cout << indices << " " << outdistances << endl;

      for(int j=0;j<M;j++) {
	//cout << "j=" << j << endl;
	const Point* querypoint=ptarr[j*(tofind+1)];
	int queryindex=nearvects.getIndex(querypoint);
	for(int i=0;i<tofind;i++){
	  //cout << "i=" << i << endl;
	  const Point* foundpoint=ptarr[j*(tofind+1)+i+1];
	  //cout << vectorsY.getIndex(foundpoint) << endl;
	  indices[queryindex*tofind+i]=treevects.getIndex(foundpoint);
	  outdistances[queryindex*tofind+i]=distances[queryindex*tofind+i];
	}
      }
      delete [] indices;
      delete [] outdistances;
    }
  }

  delete [] X;
  delete [] Y;
  delete [] ptarr;
  delete [] distances;

  return 0;

}

#include "DLPtrList.C"
template class DLPtrListNode<CoverNode>;
#include "FastSeg.C"
template class  SegList<DLPtrListNode<CoverNode> >;
