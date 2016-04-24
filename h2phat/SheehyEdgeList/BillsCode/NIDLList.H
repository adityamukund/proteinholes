#ifndef NIDLLIST_H
#define NIDLLIST_H 
#include <iostream>
#include "FastSeg.H"

template<class T> class NIDLListNode {
  template<class> friend class NIDLList;
private:
  T t;
  NIDLListNode* pr;
  NIDLListNode* ne;
public:
  NIDLListNode(T tt) : t(tt), pr(0), ne(0) {}
  ~NIDLListNode() {}
  T getPtr() { return t; }
  NIDLListNode* prev() const { return pr; }
  NIDLListNode* next() const { return ne; }
  NIDLListNode* remove();
  /*
  NIDLListNode* selfConnect() { return ne=pr=this; }
  NIDLListNode* placeBefore(NIDLListNode *n) {
      ne=n; pr=n->pr; n->pr->ne=this; return n->pr=this;
    }
  NIDLListNode* placeAfter(NIDLListNode *n) {
    ne=n->ne; pr=n; n->ne->pr=this; return n->ne=this;
  }
  NIDLListNode* replace(NIDLListNode *n) {
    n->pr->ne=this; n->ne->pr=this; pr=n->pr; ne=n->ne; return n;
  }
  void switchBackConnectionWith(NIDLListNode *n) {
  NIDLListNode<T> *t=n->pr; pr->ne=n; n->pr=pr; pr=t; t->ne=this;
  }
  NIDLListNode* disconnect() {
  pr->ne=ne; ne->pr=pr; return this;
  }
  void reverseConnections(NIDLListNode *&p,NIDLListNode *&n) {
  p=ne; n=pr; ne=n; pr=p;
  }
  void switchWithPrious() {
  if (pr!=ne) {
  NIDLListNode *prpr=pr->pr; ne->pr=pr; prpr->ne=this;
  pr->pr=this; pr->ne=ne; ne=pr; pr=prpr;
  }
  }
  void switchWithNe() {
  if (pr!=ne) {
  NIDLListNode *nene=ne->ne; pr->ne=ne; nene->pr=this;
  ne->ne=this; ne->pr=pr; pr=ne; ne=nene;
  }
  }
  void switchWith(NIDLListNode *n) {
  NIDLListNode *b=pr; NIDLListNode *f=ne; ne->pr=pr->ne=n; pr=n->pr;
  ne=n->ne; n->ne->pr=n->pr->ne=this; n->pr=b; n->ne=f;
  }
  */
  virtual void printOn(ostream& = cerr) const ;
};
                                                                      
template<class T> ostream& operator<<(ostream&,const NIDLListNode<T>&);

template<class T> class NIDLList {
private:
  SegList<NIDLListNode<T> >* pseglist;
  NIDLListNode<T>* fi;
  NIDLListNode<T>* la;
  int count;
public:
  //NIDLList() : pseglist(0), fi(0), la(0), count(0) {}
  NIDLList(SegList<NIDLListNode<T> >* p) :  
    pseglist(p), fi(0), la(0), count(0) {}
  ~NIDLList() {}
  void reset() { fi=0; la=0; }
  bool empty() const { return count==0; }
  bool notEmpty() const { return count!=0; }
  int getCount() const { return count; }
  NIDLListNode<T>* first() const { return fi; }
  NIDLListNode<T>* last() const { return la; }
  NIDLListNode<T>* next(const NIDLListNode<T> *p) const {
    return p->ne;
  }
  NIDLListNode<T>* prev(const NIDLListNode<T> *p) const {
    return p->pr;
  }
  NIDLListNode<T>* prepend(T*);
  NIDLListNode<T>* append(T*);
  /*
    void insAfter(NIDLListNode<T>*,T);
    void insBefore(NIDLListNode<T>*,T);
    T* replace(NIDLListNode<T>*&,T);
    void join(NIDLList&);
    void joinAtFront(NIDLList&);
    T* delAfter(NIDLListNode<T>*);
    T* delBefore(NIDLListNode<T>*);
    T* delAndNe(NIDLListNode<T>*&);
    T* delAndPr(NIDLListNode<T>*&);
  */
    T remove(NIDLListNode<T>*);
  /*
    void reverse();
    void swapWithPrev(NIDLListNode<T>*);
    void swapWithNext(NIDLListNode<T>*);
    void swap(NIDLListNode<T>*,NIDLListNode<T>*);
    BOOLEAN owns(const NIDLListNode<T>*) const;
    BOOLEAN progress(const NIDLListNode<T>*,const NIDLListNode<T>*) 
      const ;
    BOOLEAN regress(const NIDLListNode<T>*,const NIDLListNode<T>*) 
      const ;
    BOOLEAN ok() const;
  */
    void printOn(ostream& os = cerr) const;
};
template<class T> ostream& operator<<(ostream&,const NIDLList<T>&);


#endif
