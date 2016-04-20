#ifndef DLPTRLIST_H
#define DLPTRLIST_H 
#include <iostream>
#include "FastSeg.H"

template<class T> class DLPtrListNode {
  template<class> friend class DLPtrList;
private:
  T* t;
  DLPtrListNode* pr;
  DLPtrListNode* ne;
public:
  DLPtrListNode(T* tt) : t(tt), pr(0), ne(0) {}
  ~DLPtrListNode() {}
  T* getPtr() { return t; }
  DLPtrListNode* prev() const { return pr; }
  DLPtrListNode* next() const { return ne; }
  //DLPtrListNode* remove();
  /*
  DLPtrListNode* selfConnect() { return ne=pr=this; }
  DLPtrListNode* placeBefore(DLPtrListNode *n) {
      ne=n; pr=n->pr; n->pr->ne=this; return n->pr=this;
    }
  DLPtrListNode* placeAfter(DLPtrListNode *n) {
    ne=n->ne; pr=n; n->ne->pr=this; return n->ne=this;
  }
  DLPtrListNode* replace(DLPtrListNode *n) {
    n->pr->ne=this; n->ne->pr=this; pr=n->pr; ne=n->ne; return n;
  }
  void switchBackConnectionWith(DLPtrListNode *n) {
  DLPtrListNode<T> *t=n->pr; pr->ne=n; n->pr=pr; pr=t; t->ne=this;
  }
  DLPtrListNode* disconnect() {
  pr->ne=ne; ne->pr=pr; return this;
  }
  void reverseConnections(DLPtrListNode *&p,DLPtrListNode *&n) {
  p=ne; n=pr; ne=n; pr=p;
  }
  void switchWithPrious() {
  if (pr!=ne) {
  DLPtrListNode *prpr=pr->pr; ne->pr=pr; prpr->ne=this;
  pr->pr=this; pr->ne=ne; ne=pr; pr=prpr;
  }
  }
  void switchWithNe() {
  if (pr!=ne) {
  DLPtrListNode *nene=ne->ne; pr->ne=ne; nene->pr=this;
  ne->ne=this; ne->pr=pr; pr=ne; ne=nene;
  }
  }
  void switchWith(DLPtrListNode *n) {
  DLPtrListNode *b=pr; DLPtrListNode *f=ne; ne->pr=pr->ne=n; pr=n->pr;
  ne=n->ne; n->ne->pr=n->pr->ne=this; n->pr=b; n->ne=f;
  }
  */
  virtual void printOn(ostream& = cerr) const ;
};
                                                                      
template<class T> ostream& operator<<(ostream&,const DLPtrListNode<T>&);

template<class T> class DLPtrList {
private:
  SegList<DLPtrListNode<T> >* pseglist;
  DLPtrListNode<T>* fi;
  DLPtrListNode<T>* la;
  int count;
public:
  DLPtrList() : pseglist(0), fi(0), la(0), count(0) {}
  DLPtrList(SegList<DLPtrListNode<T> >* p) :  
    pseglist(p), fi(0), la(0), count(0) {}
  ~DLPtrList() {}
  void setSegList(SegList<DLPtrListNode<T> >* ppseglist) {
    pseglist=ppseglist; }
  void reset() { fi=0; la=0; count=0; }
  bool empty() const { return count==0; }
  bool notEmpty() const { return count!=0; }
  int getCount() const { return count; }
  DLPtrListNode<T>* first() const { return fi; }
  DLPtrListNode<T>* last() const { return la; }
  DLPtrListNode<T>* next(const DLPtrListNode<T> *p) const {
    return p->ne;
  }
  DLPtrListNode<T>* prev(const DLPtrListNode<T> *p) const {
    return p->pr;
  }
  DLPtrListNode<T>* prepend(T*);
  DLPtrListNode<T>* append(T*);

  DLPtrListNode<T>* insertAfter(DLPtrListNode<T>*,T*);
  DLPtrListNode<T>* insertBefore(DLPtrListNode<T>*,T*);
  
  /*
    T* replace(DLPtrListNode<T>*&,T);
    void join(DLPtrList&);
    void joinAtFront(DLPtrList&);
    T* delAfter(DLPtrListNode<T>*);
    T* delBefore(DLPtrListNode<T>*);
    T* delAndNe(DLPtrListNode<T>*&);
    T* delAndPr(DLPtrListNode<T>*&);
  */
    T* remove(DLPtrListNode<T>*);
  /*
    void reverse();
    void swapWithPrev(DLPtrListNode<T>*);
    void swapWithNext(DLPtrListNode<T>*);
    void swap(DLPtrListNode<T>*,DLPtrListNode<T>*);
    BOOLEAN owns(const DLPtrListNode<T>*) const;
    BOOLEAN progress(const DLPtrListNode<T>*,const DLPtrListNode<T>*) 
      const ;
    BOOLEAN regress(const DLPtrListNode<T>*,const DLPtrListNode<T>*) 
      const ;
    BOOLEAN ok() const;
  */

  void printOn(ostream& os = cerr) const;

};



#endif
