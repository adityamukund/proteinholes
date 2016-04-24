#ifndef VECTOR_H
#define VECTOR_H

#include "Points.H"
#include "Types.H"
#include <iostream>

using namespace std;

extern int dim;

class Vector : public Point {
  friend class Vectors;
private:
  const REAL* x;
public:
  Vector(const REAL* px) : Point(), x(px) { }
  ~Vector() {}
  int getDim() const { return dim; }
  const REAL* getPtr() const {return x; } 
  REAL getDist(const Point*) const;
  REAL getBlasDist(const Point*,REAL*) const;
  //REAL getDist(const Point*) const;
  void write(ofstream&) const;
  void printOn(ostream& = cout) const;
};


class Vectors : public Points {
private:
  int N;
  int current;
  Vector* vectors;
  const REAL* X;
  bool may_delete;
public:
  Vectors(const REAL* pX,int iN);
  Vectors(const char*,int,int);
  ~Vectors();
  const Vector* getVector(int i) const { return vectors+i; }
  int getDim() const { return dim; }
  int getCurrent() const { return current; }
  int getCount() const { return N; }
  int getRemaining() const { return N-current; }
  const Vector* next();
  int getIndex(const Point*) const;
  const Point* getPoint(int i) const { return vectors+i; }
  void reset() { current=0; }
  void printOn(ostream& = cout) const;
};


#endif
