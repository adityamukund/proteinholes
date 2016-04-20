#include "ThreadsWithCounter.H"
#include <iostream>
#include <assert.h>

using namespace std;

class EnlargeArg;

ThreadsWithCounter::ThreadsWithCounter(int iNTHREADS) : 
  NTHREADS(iNTHREADS),sem_start(0),sem_end(0),thread_ids(0), counter(0) {
  if(NTHREADS>0) {
    pthread_mutex_init(&lock,NULL);
    thread_ids=new pthread_t[NTHREADS];
#ifdef NAMED_SEMAPHORES
    sem_start=new sem_t*[NTHREADS];
    sem_end=new sem_t*[NTHREADS];
#else
    sem_start=new sem_t[NTHREADS];
    sem_end=new sem_t[NTHREADS];
#endif    
    pthread_t thread_ids=0;
  }
}    

ThreadsWithCounter::~ThreadsWithCounter() {
  if(NTHREADS>0) {
    pthread_mutex_destroy(&lock);
#ifdef NAMED_SEMAPHORES
    for(int t=0;t<NTHREADS;t++) {
      sem_close(sem_start[i]);
      sem_close(sem_end[i]);
    }
#endif
    delete [] sem_start;
    delete [] sem_end;
    delete [] thread_ids;
  }
}

void ThreadsWithCounter::initializeSemaphores() {

#ifdef NAMED_SEMAPHORES
char p[3];
    p[0]='/';
    p[2]=0;
    for(int i=0;i<NTHREADS;i++) {
      p[1]=2*i;
      sem_unlink(p);
      sem_start[i]=sem_open(p,O_CREAT,0644,0);
      if(sem_start[i]==SEM_FAILED) {
	cout << "Couldn't create sem_start[" << i << "]" << endl;
	assert(0);
      } 
      p[1]=2*i+1;
      sem_unlink(p);
      sem_end[i]=sem_open(p,O_CREAT,0644,0);
      if(sem_end[i]==SEM_FAILED) {
	cout << "Couldn't create sem_end[" << i << "]" << endl;
	assert(0);
      }
    }
#else
  for(int i=0;i<NTHREADS;i++) {
    sem_init(&sem_start[i],0,0);
    sem_init(&sem_end[i],0,0);
  }
#endif
  
}

void ThreadsWithCounter::create(void * (*start_routine)(void*),void* args[]) {
//int pthread_create(pthread_t *thread, const pthread_attr_t *attr,
//                        void *(*start_routine) (void *), void *arg);

for(int t=0;t<NTHREADS;t++) {
    int val=pthread_create(&thread_ids[t],NULL,start_routine,
			   args[t]);
    if(val) {
      cerr << "failed to create thread " << t << endl;
      assert(0);
    }
    //semStartPost(t);
 }
}
  
 int ThreadsWithCounter::semStartPost() {
   int val=0;
   for(int t=0;t<NTHREADS;t++) {
     int val=semStartPost(t);
     if(val!=0) 
       return val;
   }
   return val;
 }
   
 int ThreadsWithCounter::semEndPost() {
   int val=0;
   for(int t=0;t<NTHREADS;t++) {
     int val=semEndPost(t);
     if(val!=0)
       return val;
   }
   return val;
 }
   
 int ThreadsWithCounter::semStartWait() {
   int val=0;
   for(int t=0;t<NTHREADS;t++) {
     int val=semStartWait(t);
     if(val!=0) 
       return val;
   }
   return val;
 }
   
 int ThreadsWithCounter::semEndWait() {
   int val=0;
   for(int t=0;t<NTHREADS;t++) {
     int val=semEndWait(t);
     if(val!=0)
       return val;
   }
   return val;
 }
   


void ThreadsWithCounter::join() {
  
    void* exit_status;
    for(int t=0;t<NTHREADS;t++) {
      pthread_join(thread_ids[t],&exit_status);
    }
}
