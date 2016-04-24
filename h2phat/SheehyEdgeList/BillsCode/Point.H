#ifndef POINT_H
#define POINT_H
#include "Types.H"
#include <cfloat>

#include <iostream>
#include <fstream>

using namespace std;

class Point {
private:
public:
  Point() {}
  ~Point() {}
  //virtual void* getPtr() const = 0;
  //virtual int getIndex() const = 0;
  virtual REAL getDist(const Point*) const = 0;
  virtual void write(ofstream&) const = 0;
  virtual void printOn(ostream& = cout) const = 0;
};

class IndexedPoint : public Point {
 private:
  const Point* point;
  int index;
 public:
  IndexedPoint() : point(0),index(-1) {}
  IndexedPoint(const Point* ppoint,int iindex) : point(ppoint), index(iindex) {}
  ~IndexedPoint() {}
  int getIndex() const { return index; }
  //void* getPtr() const { return point->getPtr(); }
  const Point* getPoint() const { return point; }
  REAL getDist(const Point* q) const { // use dynamic cast if -DDEBUG
    //const Point* Q=((IndexedPoint*)q)->getPoint();
    const IndexedPoint* Q=dynamic_cast<const IndexedPoint*>(q);
    return point->getDist(Q->getPoint()); }
  void write(ofstream& ofs) const { point->write(ofs); }
  void printOn(ostream& os = cout) const {
    point->printOn(os); os << " index=" << index << endl;
  }
};

#endif

