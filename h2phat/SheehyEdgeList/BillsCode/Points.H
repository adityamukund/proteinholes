#ifndef POINTS_H
#define POINTS_H

#include "Types.H"
#include "Point.H"
#include <iostream>

using namespace std;

//class Point;

class Points {
  //friend class IndexedPoints;
  //private:
  //int count;
public:
  //Points(int icount) : count(icount) {}
  Points() {}
  ~Points() {}
  virtual int getCount() const = 0;
  virtual const Point* next() = 0;
  virtual int getIndex(const Point*) const = 0;
  virtual int getCurrent() const = 0;
  virtual int getRemaining() const = 0;
  virtual const Point* getPoint(int) const = 0;
  virtual void reset() = 0;
  //void setMinLevelAndRadius(REAL theta,int& level,REAL& maxradius);
  virtual void printOn(ostream& = cout) const = 0;
};


class IndexedPoints : public Points {
private:
  const IndexedPoint* base;
  int count;
  int current;
public:
  IndexedPoints() : Points(), base(0),current(-1) {}
  IndexedPoints(const IndexedPoint* pbase,int icount) : Points(), base(pbase), 
  count(icount),current(0) {}
  ~IndexedPoints() {}
  int getCount() const { return count; }
  int getRemaining() const { return count-current; }
  int getIndex(const Point* p) const {
    const IndexedPoint* q=(const IndexedPoint*)p;
    return q-base;
  }
  const Point* getPoint(int i) const { return base+i; }
  void printOn(ostream& = cout) const {}
  void reset() { current=0; }
  int getCurrent() const { return current; }
  const Point* next() { 
    if(current==count) {
      return 0;
    } else {
      return base+current++; 
    }
  }
};

#endif
