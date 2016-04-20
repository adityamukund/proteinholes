#ifndef ENLARGEDATA_H
#define ENLARGEDATA_H

#include <vector>
//#include "CoverNode.H"
#include <iostream>
#include "Points.H"

using namespace std;

class ThreadsWithCounter;
class PointLevelParent;
class CoverNode;
class Cover;

class EnlargeData {
private:
  ThreadsWithCounter* threads;
  int BLOCKSIZE;
  int remaining;
  //Points& points;
  vector<PointLevelParent> vplp;
  vector<long int> vthreadncallstogetdist;
  long int mergencallstogetdist;
  CoverNode** mergecovernodes;
public:
  /*
  EnlargeData() : threads(0), BLOCKSIZE(0), vplp(0), 
		  vthreadncallstogetdist(0), mergencallstogetdist(0),
		  covernodes(0) {}
  */
  EnlargeData(ThreadsWithCounter* pthreads,int iBLOCKSIZE,int remaining);
  ~EnlargeData();
  //Points& getPoints() { return points; }
  int setPoints(Points& points);
  ThreadsWithCounter* getThreads() const { return threads; }
  int getBlockSize() const { return BLOCKSIZE; }
  int getRemaining() const { return remaining; }
  void decrementRemaining(int counter) { remaining-=counter; }
  vector<PointLevelParent>& getPointLevelParent() { return vplp; }
  PointLevelParent* getPointLevelParent(int i) { return &vplp[i]; }
  /*
  vector<long int>& getThreadNCallsToGetDist() { 
    return vthreadncallstogetdist;
  }
  
  long int getThreadNCallsToGetDist(int t) { 
    return vthreadncallstogetdist[t];
  }
  */
  long int getMergeNCallsToGetDist() const { 
    return mergencallstogetdist;
  }
  long int getThreadNCallsToGetDist() const; 
  void incrThreadNCallsToGetDist(int t,long int ncalls) {
    vthreadncallstogetdist[t]+=ncalls;
  }
  void incrMergeNCallsToGetDist(long int ncalls) {
    mergencallstogetdist+=ncalls;
  }
  CoverNode** getMergeCoverNodes() { return mergecovernodes; }
  void printNCalls(ostream& os = cout) const;
};

class EnlargeArg {
private:
  int tid;
  ThreadsWithCounter* threads;
  Cover* cover;
  EnlargeData* enlargedata;
  int counter;
public:
  EnlargeArg() : tid(-1), threads(0), cover(0), enlargedata(0), counter(-1) {} 
  EnlargeArg(int itid,ThreadsWithCounter* pthreads,Cover* pcover,
    EnlargeData* penlargedata) : tid(itid), threads(pthreads), cover(pcover),
				 enlargedata(penlargedata) {}
  ~EnlargeArg() {}
  ThreadsWithCounter* getThreads() { return threads; }
  int getTid() const { return tid; }
  Cover* getCover() { return cover; }
  EnlargeData* getEnlargeData() { return enlargedata; }
  /*
  PointLevelParent* getPointLevelParent(int i) { 
    return enlargedata->getPointLevelParent(i);
  }
  vector<PointLevelParent>& getPointLevelParent() { 
    return enlargedata->getPointLevelParent();
  }
  vector<long int>& getThreadNCallsToGetDist() { 
    return enlargedata->getThreadNCallsToGetDist(); }
  long int* getThreadNCallsToGetDist(int i) { 
    return enlargedata->getThreadNCallsToGetDist(i); }
  */
};


#endif
