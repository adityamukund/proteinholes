// g++ -g -o cube -DFLOAT -DMEM_DEBUG cube.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C findWithin.C Timer.C Point.C CoverNode.C EnlargeData.C FindWithinData.C MemoryDebugger.C -lpthread

#include "Cover.H"
//#include "DisjointLists.H"
#include "EnlargeData.H"
#ifdef MEM_DEBUG
#include "MemoryDebugger.H"
#endif
#include <iostream>
#include <fstream>
#include <stdlib.h>

#include <stdlib.h>
#include <sys/times.h>
#include <sys/param.h>

#include "Vector.H"
#include "Timer.H"

using namespace std;

//ThreadsWithCounter* threads=0;

REAL theta=0.0;
REAL mu=0.0;
int dim=0;

int main(int argc,char* argv[]) {

#ifdef MEM_DEBUG
  MemoryDebugger md;
#endif

  /*
  int M=atoi(argv[1]);
  int N=2*M;
  */

  //Point::dim=5;

  dim=atoi(argv[1]);
  int N=atoi(argv[2]);
  theta=atof(argv[3]);
  mu=1.0/(1.0-theta);

  int numlevels=atoi(argv[4]);
  int NTHREADS=atoi(argv[5]);
  int BLOCKSIZE=atoi(argv[6]);

  /*

#ifdef MEM_DEBUG
  threads=OPERATOR_NEW_BRACKET(ThreadsWithCounter,NTHREADS);
#else
  threads=new ThreadsWithCounter(NTHREADS);
#endif
  */

  ThreadsWithCounter threads(NTHREADS);
  
  if(N>10000000) {
    cerr << "N is too big" << endl;
    return 0;
  }

#ifdef MEM_DEBUG  
  REAL* X=OPERATOR_NEW_BRACKET(REAL,dim*N);
#else
  REAL* X=new REAL[dim*N];
#endif

  for(int i=0;i<dim*N;i++) {
    X[i]=(REAL)drand48();
  }
  Vectors vectors(X,N);
  
  SegList<DLPtrListNode<CoverNode> > seglist(1024);

  const Vector* vector=(const Vector*)(vectors.next());
  Cover cover(vector,seglist,numlevels);
  EnlargeData enlargedata(&threads,BLOCKSIZE,vectors.getRemaining());

  Timer timer;
  timer.on();
  cover.enlargeBy(enlargedata,vectors);
  timer.off();
  timer.printOn(cout);

  cout << "cover constructed" << endl;
  //cout << "ncallstogetdist=" << cover.getNCallsToGetDist() << endl;

  cover.printLevelCounts();
  enlargedata.printNCalls();

  const char* outfile=argv[7];
  CoverIndices coverindices(&cover,&vectors);
  coverindices.write(outfile,theta,&vectors);//cover.printOn(cout);

  //cover.check(&vectors);


  /*
  for(CoverNode* node=cover.first();node;node=cover.next(node)) {
    cout << "index=" << vectors.getIndex(node->getPoint()) 
	 << " level=" << node->getLevel() << endl;
  }
  */

  //cover.check();

  //cover.printDiagnostics();

  /*
  cout << "printing cover with coords" << endl;
  cover.printOn(&vectors,cout);

  cover.printOn(cout);

  cover.check();

  cout << "cover checked" << endl;
  */
  
  /*
  vectors.reset();
  CoverIndices coverindices(&cover,&vectors);
  Cover cover2(vectors,coverindices);
  
  /*
  cout << "printing cover2" << endl;
  cover2.printOn(cout);
  cout << "printed cover2" << endl;
  */

  /*
  cover2.check();

  
  const char* outfile="outfile";
  cover.write(outfile,&vectors);
   
  CoverIndices coverindices3(outfile);
  vectors.reset();
  Cover cover3(vectors,coverindices3);
  */

  //cout << "printing cover3" << endl;
  //cover3.printOn(cout);
  //cout << "printed cover3" << endl;
  
  /*
  cover3.check(cout);
  cout << "cover3 checked" << endl;
  */

  //coverindices.printOn(cout);
 

  //cover3.printOn(cout);
  
  //cover.getImmediateDescendents();

  //cover.counts();
  
  //cover.printLP(cout);
  
  //cover.printOn(cout);
  //cout << endl << endl;
  
  /*
  const REAL* radii=cover.getRadii();
  int maxlevel=cover.getMinLevel()+cover.getNumLevels()-1;

  Cover::DescendList descend_list(&cover);
  for(CoverNode* node=cover.first();node;node=cover.next(node)) {
    int l=node->getLevel();
    if(l<maxlevel) {
      cover.findShadow(node,descend_list,radii);
    }
  }
  

  delete radii;
  */
  /*
  REAL x(.5);
  Vector v(0,&x);

  double dr=atof(argv[5]);
  REAL r=(REAL)dr;
  
  int numfindlevels=atoi(argv[6]);
  Cover::DescendList descend_list(&cover);

  cover.findWithin(&v,r,numfindlevels,descend_list);
  */


  //cout << "findWithin " << descend_list.getCount() << endl;

  /*
  bool val=cover.checkFindWithin(&v,r,numfindlevels,descend_list);
  if(val) {
    cout << "findWithin passed" << endl;
  } else {
    cout << "findWithin failed" << endl;
  }

  /*
   int v=cover.getNumberInserted();
#ifdef MEM_DEBUG
  int* plevel_counters=OPERATOR_NEW_BRACKET( int,numlevels);;
  int* plevel_offsets=OPERATOR_NEW_BRACKET(int,numlevels);
  int* plevels=OPERATOR_NEW_BRACKET(int,v);
#else
  int* plevel_counters= new int[numlevels];
  int* plevel_offsets= new int[numlevels];
  int* plevels= new int[v];
#endif
  
  REAL* radii=new REAL[cover.getNumLevels()];
  if(cover.checkDistances(radii,cout) && cover.checkChildren(cout)) {
    cout << "checkDistances passed" << endl;
  } else {
    cout << "checkDistances failed" << endl;
  }
  delete [] radii;

  if(cover.checkChildren()) {
    cout << "checkChildren passed" << endl;
  } else {
    cout << "checkChildren failed" << endl;
  }
  
  Levels levels(&cover,plevel_counters,plevel_offsets,plevels);
  //levels.printOn(&cover,cout);
  if(levels.checkLevels(cout)) {
    cout << "checkLevels passed" << endl;
  } else {
    cout << "checkLevels failed" << endl;
  }
 
  */

  //cout << "entering Cover::checkParents" << endl;
  //cover.checkParents(cout);
  //cout << "entering Cover::checkLevels" << endl;
  //cover.checkLevels(&levels,cout); 
  //cout << "entering Cover::check" << endl;
  //cover.check();
  
  
  /*if(levels.checkLevels()) {
    cout << "levels.checkLevels passed" << endl;
  } else {
    cout << "levels.checkLevels failed" << endl;
  }
  */
   /*   if(cover.check()) {
      cout << "Cover::check passed" << endl;
mp    } else {
      cout << "Cover::check failed" << endl;
    }
  
   */

  /*   
  CoverParams new_params(&cover);
  
  DisjointLists children(N,pch,false);

  Cover cover2(new_params,&vectors,plp,children);

  //cover2.getChildren()->printOn(cout);

  cout << "cover2 constructed" << endl;
  //cover2.printOn(N,cout);
  cout << endl;

   //int index=atoi(argv[4]);
   REAL r=atof(argv[4]);
   int mf=atoi(argv[5]);
   if(mf<0) mf=INT_MAX;

   int* arr=new int[N];
   int count=0;
   Cover::DescendList descend_list(&cover);

   for(int n=0;n<N;n++) {
     count=0;
     cover2.findWithin(&vectors,n,descend_list,r,mf,arr,count);
     bool check=cover2.checkFindWithin(&vectors,n,r,mf,arr,count);
     if(check==false) {
       cout << "checkFindWithin failed" << endl;
     } else {
       cout << "checkFindWithin passed" << endl;
     }
     cout << n << endl;
   }
  */

   /*
#ifdef MEM_DEBUG
    int* level_keys=OPERATOR_NEW_BRACKET(int,numlevels);
    int* b=OPERATOR_NEW_BRACKET(int,2*N);
#else
  int* level_keys=new int[nuCmlevels];
  int* b=new int[2*N];
#endif

  Nearest nearest(&cover,N,b,level_keys);
  mem_check();
  int index=-1;
  REAL mindist=DBL_MAX;
  nearest.findNearest(&cover,&vect,0.0,index,mindist);

  mem_check();
  cout << "findNearest: index=" << index
       << " mindist=" << mindist << endl;

  nearest.check(&vect);
  mem_check();
  */
  //cout << "Cover.nearest_ctr=" << cover.getNearestDistCtr() << endl;

  //mem_check();

  delete [] X;
  //delete [] plp;
  //delete [] pch;
  //delete [] arr;
  
  
  //delete [] plevel_counters;
  //delete [] plevel_offsets;
  //delete [] plevels;
  

  //delete [] b;
  //delete [] level_keys;

  //cout << "lcount=" << cover.getLcount() << endl;
  //mem_check();

  /*
  if(threads) {
    delete threads;
  }
  */

  return 0;
  
}

#include "DLPtrList.C"
template class DLPtrListNode<CoverNode>;
#include "FastSeg.C"
template class  SegList<DLPtrListNode<CoverNode> >;
