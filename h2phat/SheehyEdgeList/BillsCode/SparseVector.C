//Programmer: Chris Tralie (modified from Bill Allard)
#include <cmath>
#include "SparseVector.H"
#include <assert.h>
#include "MemoryDebugger.H"
#include "mex.h"
#include <vector>
#include <algorithm>




REAL SparseVector::getDist(const Point* q) const {
  const SparseVector* w=dynamic_cast<const SparseVector*>(q);
  const REAL* y=w->x;
  const int* yis=w->is;
  int yK = w->K;
  REAL distsq=0.0;
  
  int i1 = 0, i2 = 0;
  while (i1 < K || i2 < yK) {
  	if (is[i1] < yis[i2] || i2 == yK) {
  		distsq += x[i1]*x[i1];
  		i1++;
  	}
  	else if (yis[i2] < is[i1] || i1 == K) {
  		distsq += y[i2]*y[i2];
  		i2++;
  	}
  	else {
  		REAL diff = x[i1] - y[i2];
  		distsq += diff*diff;
  		i1++;
  		i2++;
  	}
  }
  return sqrt(distsq);
}  

//TODO
void SparseVector::write(ofstream& ofs) const {
  ;//ofs.write((char*)x,dim*sizeof(REAL));
}

//TODO
void SparseVector::printOn(ostream& os) const {
 	os << "TODO";
}

SparseVectors::SparseVectors(const int* pI, const int* pJ, const REAL* pS, int iN,int d0) :
  Points(), N(iN), current(0), I(pI), J(pJ), S(pS), may_delete(false) {
 
  //Step 1: Allocate space for all sparse vectors (indices and distances)
  mexPrintf("N = %i, d0 = %i\n", N, d0);
  mexEvalString("drawnow");
  std::vector<SVInfo>* VsIn = new vector<SVInfo>[N];
  SVInfo info;
  for (int row = 0; row < d0; row++) {
	info.idx = J[row];
	info.val = S[row];
	VsIn[I[row]].push_back(info); //NOTE: Expecting 0-indexing
	mexPrintf("row %i of %i\n", row, d0);
	mexEvalString("drawnow");
  }
  //Step 2: Sort the sparse elements in each vector in ascending order of index
  for (int i = 0; i < N; i++) {
  	sort(VsIn[i].begin(), VsIn[i].end(), SVInfo_DistComparator());
  }
  //Step 3: Copy over sparse vectors from the vector object to C arrays
  for(int i = 0; i < N; i++) {
  	mexPrintf("Point %i of %i\n", i, N);
  	mexEvalString("drawnow");
  	SparseVector* s = new SparseVector(&VsIn[i]);
    vectors.push_back(s);
  }
  
  delete[] VsIn;
}

SparseVectors::~SparseVectors() { 
  while (vectors.size() > 0) {
    delete[] vectors.back();
  	vectors.pop_back();
  }
  if(may_delete) {
    delete [] I;
    delete [] J;
    delete [] S;
  }
}

SparseVector::SparseVector(std::vector<SVInfo>* info) : Point() {
	K = (int)info->size();
	is = new int[K];
	x = new double[K];
	for (int i = 0; i < K; i++) {
		is[i] = (*info)[i].idx;
		x[i] = (*info)[i].val;
	}
}

const SparseVector* SparseVectors::next() {
  if(current==N) {
    return 0;
  } else {
    SparseVector* ret = vectors[current];
    current++;
    return ret;
  }
}

int SparseVectors::getIndex(const Point* p) const {
  const SparseVector* v=dynamic_cast<const SparseVector*>(p);
  return v->getIndex();
}

//TODO
void SparseVectors::printOn(ostream& os) const {
  os << "TODO";
}

