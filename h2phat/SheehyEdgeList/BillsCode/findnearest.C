//Compile command: mex -O -DDOUBLE findnearest.C ThreadsWithCounter.C IDLList.C IDLListNode.C Vector.C Cover.C Point.C CoverNode.C EnlargeData.C FindWithinData.C findNearest.C -lpthread

/* findnearest take five arguments:
The first argument is what was returned by covertree
The second argument is the array that was passed as the second argument to 
covertree
The third argument is the array of query vectors
The fourth argument the number of nearest neighbors to find
The fifth argument, of class int32, is the number of threads (0 for serial).

findnearest returns an k x M matrix of class int32 whose j-th column
is the indices of k nearest nearest neighbors of the j-th queryvector
*/

#include "mex.h"
#include "Cover.H"
#include "Vector.H"
#include "ThreadsWithCounter.H"
#include "FindNearestData.H"

int dim=0;
double theta=0.0;
double mu=0.0;

const char* fnames_in[]={
  "theta",
  "outparams",
  "radii",
  "levels",
  "ncallstogetdist"
};

const char* fnames_out[]={
  "indices",
  "distances"
};


mwSize dims[2];
int ndims=2;

void mexFunction(int nlhs, mxArray *plhs[], int nrhs,
                 const mxArray *prhs[]) {


  /* Check for proper number of arguments. */
  if (nrhs != 5) {
    mexErrMsgTxt("Five inputs required.");
  } 
 
  /* Check the first argument */
  //the first argument is what was returned by covertree

  int nelements=mxGetNumberOfFields(prhs[0]);
  if(nelements!=5) {
    mexErrMsgTxt("First argument should have five fields.");
  }
  
  /* Get first field of first input. */

  const mxArray* tmp=mxGetField(prhs[0],0,fnames_in[0]);
  if(!mxIsClass(tmp,"double")) {
    mexErrMsgTxt("First field of first input must be double\n");
  }
  double* ptheta=(double*)mxGetData(tmp);  
  theta=*ptheta;
  mu=1.0/(1.0-theta);
  
  /* Get second field of first input */

  tmp=mxGetField(prhs[0],0,fnames_in[1]);
  if(!mxIsClass(tmp,"int32")) {
    mexErrMsgTxt("Second field of first input must be int32\n");
  }

  /* Get dimensions of second field of first input */

  mwSize ndims_in=mxGetNumberOfDimensions(tmp);
  const mwSize* dims_in=mxGetDimensions(tmp);
  bool val=(ndims_in==2)&&(dims_in[0]==1)&&(dims_in[1]==7);
  if(!val) {
    mexErrMsgTxt("Second field of first input has bad size\n");
  }
  int* outparams=(int*)mxGetData(tmp);
  int minlevel=outparams[1];
  int numlevels=outparams[2];

  tmp=mxGetField(prhs[0],0,fnames_in[3]);
  if(!mxIsClass(tmp,"int32")) {
    mexErrMsgTxt("Fourth field of first input should be int32\n");
  }
  mwSize ndims_indices=mxGetNumberOfDimensions(tmp);
  const mwSize* dims_indices=mxGetDimensions(tmp);
  if(ndims_indices!=2) {
    mexErrMsgTxt("Fourth field of first input has bad size\n");
  }
  int* indices=(int*)mxGetData(tmp);

/* Get dimensions of second field of input */
  
  tmp=prhs[1];
  if(!mxIsClass(tmp,"double")) {
    mexErrMsgTxt("Second input must be double\n");
  }
  mwSize ndims_X=mxGetNumberOfDimensions(tmp);
  const mwSize* dims_X=mxGetDimensions(tmp);
  if(!mxIsClass(tmp,"double")) {
    mexErrMsgTxt("Second input must be double\n");
  }

  int NX=dims_X[ndims_in-1];
  int d=dims_indices[0];
  if(d!=NX) {
    mexErrMsgTxt("Mismatch between first and second inputs\n");
  }
  dim=1;
  for(int i=0;i<ndims_X-1;i++) {
    dim*=dims_X[i];
  }

  REAL* X=(REAL*)mxGetData(tmp);

  mexPrintf("dim=%d number NX=%d\n",dim,NX);

  Vectors vectorsX(X,NX);
  CoverIndices coverindices(theta,numlevels,minlevel,NX,indices);

  SegList<DLPtrListNode<CoverNode> > seglist(1024);
  Cover cover(vectorsX,seglist,coverindices);

  tmp=prhs[2];
  mwSize ndims_Y=mxGetNumberOfDimensions(tmp);
  const mwSize* dims_Y=mxGetDimensions(tmp);
  if(!mxIsClass(tmp,"double")) {
    mexErrMsgTxt("Third argument should be double\n");
  }

  int NY=dims_Y[ndims_Y-1];
  int dimY=1;
  for(int i=0;i<ndims_Y-1;i++) {
    dimY*=dims_Y[i];
  }

  if(dimY!=dim) {
    mexErrMsgTxt("Dimension mismatch\n");
  }

  double* Y=(double*)mxGetData(tmp); 
  Vectors vectorsY(Y,NY);

  tmp=prhs[3];
  ndims_in=mxGetNumberOfDimensions(tmp);
  dims_in=mxGetDimensions(tmp);
  if(!mxIsClass(tmp,"int32")) {
    mexErrMsgTxt("Fifth argument should be int32\n");
  }
  int* pk=(int*)mxGetData(tmp);
  int k=*pk;
  
  tmp=prhs[4];
  ndims_in=mxGetNumberOfDimensions(tmp);
  dims_in=mxGetDimensions(tmp);
  if(!mxIsClass(tmp,"int32")) {
    mexErrMsgTxt("Fifth argument should be int32\n");
  }
  int NTHREADS=*(int*)mxGetData(tmp);

  ThreadsWithCounter threads(NTHREADS);

  const Point** ptarr=new const Point*[(k+1)*NY];
  double* distances=new double[k*NY];

  //const Point** ptarr=(const Point**)mxMalloc((k+1)*NY*sizeof(const Point*));
  //double* distances=(double*)mxMalloc(k*NY*sizeof(double));

  int L=cover.getMaxLevelPresent();
  
  FindNearestData findnearestdata(&threads,vectorsY,k,L,ptarr,distances);
  vectorsY.reset();

  cout << "before findNearest" << endl;
  cover.findNearest(vectorsY,findnearestdata,ptarr,distances);

  cout << "after findNearest" << endl;
  
  bool test=cover.checkFindNearest(vectorsY,ptarr,k);
  for(int i=0;i<NY;i++) {
    if(test==false) {
      cout << "checkFindNearest failed" << endl;
    }
  }
  
/* Create matrix for the return argument. */
  plhs[0] = mxCreateStructMatrix(1, 1, 2, fnames_out);
  
  mxArray* fout;

  dims[0]=k;
  dims[1]=NY;
  fout=mxCreateNumericArray(ndims,dims,mxINT32_CLASS,mxREAL);
  indices=(int*)mxGetData(fout);
  mxSetField(plhs[0],0,fnames_out[0], fout);

  dims[0]=k;
  dims[1]=NY;
  fout=mxCreateNumericArray(ndims,dims,mxDOUBLE_CLASS,mxREAL);
  double* outdistances=(double*)mxGetData(fout);
  mxSetField(plhs[0],0,fnames_out[1], fout);

  /*
  for(int j=0;j<NY;j++) {
    //cout << "j=" << j << endl;
    const Point* querypoint=ptarr[j*(k+1)];
    int queryindex=vectorsY.getIndex(querypoint);
    for(int i=0;i<k;i++){
      //cout << "i=" << i << endl;
      const Point* foundpoint=ptarr[j*(k+1)+i+1];
      //cout << vectorsY.getIndex(foundpoint) << endl;
      indices[queryindex*k+i]=vectorsX.getIndex(foundpoint);
      outdistances[queryindex*k+i]=distances[queryindex*k+i];
    }
  }
  */

  for(int j=0;j<NY;j++) {
    //cout << "j=" << j << endl;
    //const Point* querypoint=ptarr[j*(k+1)];
    //int queryindex=vectorsY.getIndex(querypoint);
    for(int i=0;i<k;i++){
      //cout << "i=" << i << endl;
      const Point* foundpoint=ptarr[j*(k+1)+i+1];
      if(foundpoint) {
	indices[j*k+i]=vectorsX.getIndex(foundpoint);
	outdistances[j*k+i]=distances[j*k+i];
      } else {
	indices[j*k+i]=-1;
	outdistances[j*k+i]=-1.0;
      } 	
    }
  }

  delete [] ptarr;
  delete [] distances;
  
  //mxFree(ptarr);
  //mxFree(distances);

}

#include "FastSeg.C"
template class SegList<DLPtrListNode<CoverNode> >;
