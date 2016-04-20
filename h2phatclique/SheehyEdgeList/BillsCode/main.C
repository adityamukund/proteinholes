// g++ -g -o main -DFLOAT main.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C findWithin.C Timer.C Point.C CoverNode.C EnlargeData.C FindWithinData.C findNearest.C -lpthread

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

using namespace std;

//ThreadsWithCounter* threads=0;


REAL theta=0.0;
REAL mu=0.0;

int dim=1;

int main(int argc,char* argv[]) {


#ifdef MEM_DEBUG
  MemoryDebugger md;
#endif

  /*
  int M=atoi(argv[1]);
  int N=2*M;
  */

  //Point::dim=5;

  /*
  if(!((argc==4)||(argc==6))) {
    cerr << "requires 3 or 5 args" << endl;
    return 0;
  }
  */

  int N=atoi(argv[1]);
  theta=atof(argv[2]);
  if((theta<=0.0)||(theta>=1.0)) {
      assert(0);
  }
  mu=1.0/(1.0-theta);
  int numlevels=atoi(argv[3]);

  int NTHREADS=0;
  int BLOCKSIZE=0;

  if(argc==6) {
    NTHREADS=atoi(argv[4]);
    BLOCKSIZE=atoi(argv[5]);
  }
  
  ThreadsWithCounter threads(NTHREADS);
  
#ifdef MEM_DEBUG
  REAL* X=OPERATOR_NEW_BRACKET(REAL,4*N);
#else
  REAL* X=new REAL[4*N];
#endif

  //X[0]=0.0;
  for(int i=0;i<4*N;i++) {
    //X[i]=X[i+M]=(REAL)i;
    X[i]=i/REAL(4*N); 
    //X[i+N/2]=i/REAL(N);
    //X[i]=drand48();
    
    /*
    if(i%2) {
	X[i+N]=X[i]=50.0*drand48();
      } else {
	X[i+N]=X[i]=drand48();
    }
    */
   
    //X[i]=(N-i)/REAL(N);
    
    /*
    if(i%2) {
      X[i+N]=X[i]=REAL(i*i);
    } else {
      X[i+N]=X[i]=(REAL)drand48();
    }
    */

    /*
    if(i%2) {
      X[i]=REAL(i*i);
    } else {
      X[i]=(REAL)drand48();
    }
    */

    //X[i]=i*i;
   
  //X[i]=1/(i+.00001);
  }
   			    
  //CoverParams params(theta,numlevels,0,NTHREADS,BLOCKSIZE);
  Vectors vectors(X,N);
  
  /*
  for(int i=0;i<N;i++) {
    cout << vectors.getIndex(vectors.getVector(i)) << endl;
  }
  cout << endl;
  */


  SegList<DLPtrListNode<CoverNode> > seglist(1024);

  const Vector* vect=(const Vector*)(vectors.next());
  Cover cover(vect,seglist,numlevels,0);
  EnlargeData enlargedata(&threads,BLOCKSIZE,vectors.getRemaining());

  Timer timer;
  timer.on();
  cover.enlargeBy(enlargedata,vectors);
  timer.off();
  timer.printOn(cout);

  enlargedata.printNCalls();
  
  cover.printLevelCounts(cout);
  
  vector<DescendNodePtr> vdn(N);
  
  int k=atoi(argv[6]);

  k= k>N ? N : k;

  const Point** ptarr=new const Point*[(k+1)*N];
  REAL* distances=new REAL[k*N];

  REAL* Y=new REAL[4*N/2];
  for(int i=0;i<4*N/2;i++) {
    Y[i]=drand48();
  }

  Vectors vectorsY(Y,N/2);
  int L=cover.getMaxLevelPresent();
  FindNearestData findnearestdata(&threads,vectorsY,k,L,ptarr,distances);
  vectorsY.reset();

  timer.on();
  cover.findNearest(vectorsY,findnearestdata,ptarr,distances);
  timer.off();
  timer.printOn(cout);

  cout << "after findNearest" << endl;

  cover.printFindNearest(vectorsY,ptarr,k);

  bool val=cover.checkFindNearest(vectorsY,ptarr,k);
  if(val) {
    cout << "checkFindNearest passed" << endl;
  } else {
     cout << "checkFindNearest failed" << endl;
  }  


  int NY=vectorsY.getCount();
  int* found=new int[k*NY];
  for(int j=0;j<NY;j++) {
    //cout << "j=" << j << endl;
    const Point* querypoint=ptarr[j*(k+1)];
    int queryindex=vectorsY.getIndex(querypoint);
    for(int i=0;i<k;i++){
      //cout << "i=" << i << endl;
      const Point* foundpoint=ptarr[j*(k+1)+i+1];
      cout << vectors.getIndex(foundpoint) << endl;
      found[queryindex*k+i]=vectors.getIndex(foundpoint);
    }
  }


  delete [] ptarr;

  delete [] Y;
  
  delete [] found;

  /*
  cout << "ncallstogetdist=" << cover.getNCallsToGetDist() << endl;

  cout << "cover constructed" << endl;

  cover.setCounts();
  cover.printCounts(cout);
  */

  //cover.printOn(cout);

  /*
  for(CoverNode* node=cover.first();node;node=cover.next(node)) {
    cout << "index=" << vectors.getIndex(node->getPoint()) 
	 << " level=" << node->getLevel() << endl;
  }
  */

  //cover.check(&vectors);

  //cover.printDiagnostics();

  
  //cout << "printing cover with coords" << endl;
  //cover.printOn(cout);

  /*
  cover.printOn(cout);

  cover.check();

  cout << "cover checked" << endl;
  
  */
 

  /*
  vectors.reset();
  CoverIndices coverindices(&cover,&vectors);
  coverindices.printOn(cout);

  Cover cover2(vectors,seglist,coverindices);
  cout << "printing cover2" << endl;
  cover2.printOn(cout);
  cout << "printed cover2" << endl;
  
  cover2.check(&vectors);
  
  const char* outfile="outfile";
  coverindices.write(outfile,theta,&vectors);
   
  CoverIndices coverindices3(outfile);
  coverindices.printOn(cout);
  vectors.reset();
  Cover cover3(vectors,seglist,coverindices3);
  
  
  cout << "printing cover3" << endl;
  cover3.printOn(cout);
  cout << "printed cover3" << endl;
  
  
  cover3.check(&vectors);
  cout << "cover3 checked" << endl;
  
  coverindices.printOn(cout);
  */

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
  REAL x(.0);
  Vector v(&x);

  double dr=.25;
  REAL r=(REAL)dr;
  
  int numfindlevels=10;
  Cover::DescendList descend_list;
  descend_list.reset(&cover,&v,r);

  cover.findWithin(&v,r,numfindlevels,descend_list);
  bool test=cover.checkFindWithin(&v,.25,numfindlevels,descend_list);
  if(test) {
    cout << "passed" << endl;
  } else {
    cout << "failed" << endl;
  }

  
  REAL y(.7);
  Vector w(&y);
  numfindlevels=4;
  descend_list.reset(&cover,&w,r);
  cover.findWithin(&w,r,numfindlevels,descend_list);
  test=cover.checkFindWithin(&w,.25,numfindlevels,descend_list);
   if(test) {
    cout << "passed" << endl;
  } else {
    cout << "failed" << endl;
  }

  */
  /*
#ifdef MEM_DEBUG
  REAL* Y=OPERATOR_NEW_BRACKET(REAL,N);
#else
  REAL* Y=new REAL[N];
#endif

  for(int i=0;i<N;i++) {
    Y[i]=drand48();
  }

  Vectors findvectors(Y,N);
  
  findvectors.reset();
  N=findvectors.getCount();
  REAL* r=new REAL[N];
  int* numfindlevels=new int[N];
  Cover::DescendList* descendlists=new Cover::DescendList[N];
  for(int i=0;i<N;i++) {
    r[i]=.25;
    numfindlevels[i]=numlevels;
  }

  FindWithinData findwithindata(&threads,findvectors,r,numfindlevels,descendlists);

  findvectors.reset();
  int totalfound=cover2.findWithin(findvectors,findwithindata,descendlists);
  findvectors.reset();
  
  bool test=true;
  for(int i=0;i<N;i++) {
    test=cover2.checkFindWithin(findvectors.getPoint(i),r[i],numfindlevels[i],descendlists[i]);
    //cout << "i=" << i << endl;
    if(test==false) {
      cout << i << " failed" << endl;
    }
  }

  cout << "test=" << test << endl;
  
  int* found=new int[totalfound];
  int* pi=new int[2*N];
  flattenDescendLists(totalfound,found,N,pi,vectors,descendlists);
  test=checkFlattenDescendList(totalfound,found,N,pi,vectors,descendlists);

  cout << "test=" << test << endl;

  delete [] Y;
  delete [] found;
  delete [] pi;
  */
  /*
  int* offsets=0;
  int* counts=0;
  const Point** pout=0;
  cover2.findWithin(&vectors,.25,numlevels,offsets,counts,pout);

  vectors.reset();
  printFromFindWithin(&vectors,offsets,counts,pout);
  */
  //cout << "findWithin " << descend_list.getCount() << endl;

  /*
  bool val=cover.checkFindWithin(&v,r,numfindlevels,descend_list);
  if(val) {
    cout << "findWithin passed" << endl;
  } else {
    cout << "findWithin failed" << endl;
  }
  */

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
    int* b=OPERATOR_NEW_BRACKET(int,N);
#else
  int* level_keys=new int[nuCmlevels];
  int* b=new int[N];
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

  /*
  delete [] r;
  delete [] numfindlevels;
  delete [] descendlists;
  */
  //delete [] indices;
  //delete [] arr;

  //delete [] indices;
  //delete [] plp;
  //delete [] pch;
  //delete [] arr;
  
  
  //delete [] plevel_counters;
  //delete [] plevel_offsets;
  //delete [] plevels;
  

  //delete [] b;
  //delete [] level_keys;

  //cout << "lcount=" << cover.getLcount() << endl;
  
  return 0;

}

#include "DLPtrList.C"
template class DLPtrListNode<CoverNode>;
#include "FastSeg.C"
template class  SegList<DLPtrListNode<CoverNode> >;
