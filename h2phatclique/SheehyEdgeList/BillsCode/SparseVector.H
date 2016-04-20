//Programmer: Chris Tralie (modified from Bill Allard)
#ifndef VECTOR_H
#define VECTOR_H

#include "Points.H"
#include "Types.H"
#include <iostream>
#include <vector>
#include "mex.h"

using namespace std;

extern int dim;

typedef struct svinf {
	int idx;
	REAL val;
} SVInfo;

struct SVInfo_DistComparator {
	bool operator()(const SVInfo& s1, const SVInfo& s2) const {
		if (s1.idx < s2.idx) { //Sort in order by index
			return true;
		}
		return false;
	}
};

class SparseVector : public Point {
  friend class SparseVectors;
private:
  int* is; //Sorted list of nonzero indices
  REAL* x; //Element values at those indices
  int K;
  int index;
public:
  SparseVector(std::vector<SVInfo>* info);
  ~SparseVector() { delete[] x; delete[] is; }
  int getDim() const { return dim; }
  int getIndex() const { return index; }
  const REAL* getPtr() const { mexPrintf("Warning: Calling unimplemented pointer function in SparseVector class\n"); return x; } 
  REAL getDist(const Point*) const;
  REAL getBlasDist(const Point*,REAL*) const;
  //REAL getDist(const Point*) const;
  void write(ofstream&) const;
  void printOn(ostream& = cout) const;
};


class SparseVectors : public Points {
private:
  int N;
  int dim;
  int current;
  vector<SparseVector*> vectors;
  const int* I;
  const int* J;
  const REAL* S;
  bool may_delete;
public:
  SparseVectors(const int* pI, const int* pJ, const REAL* pS, int iN,int d0); //d0 is the total number of nonzero elements in the sparse representation
  ~SparseVectors();
  const SparseVector* getSparseVector(int i) const { return vectors[i]; }
  int getDim() const { return dim; }
  int getCurrent() const { return current; }
  int getCount() const { return N; }
  int getRemaining() const { return N-current; }
  const SparseVector* next();
  int getIndex(const Point*) const;
  const Point* getPoint(int i) const { return vectors[i]; }
  void reset() { current=0; }
  void printOn(ostream& = cout) const;
};


#endif
