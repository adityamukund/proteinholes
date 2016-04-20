#include "NIDLList.H"
#include <limits.h>
#include <stdlib.h>
#ifdef MEM_DEBUG
#include "MemoryDebugger.H"
#endif
#include <assert.h>

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> ostream& operator<<(ostream& strm ,
const NIDLListNode<T> &node) {
  strm << "NIDLListNode: &node = " << &node
       << " bk = " << node.prev() 
       << " fd = " << node.next(); 
  return strm;
}

template<class T> void NIDLListNode<T>::printOn(ostream &os) const {
  os << "NIDLListNode: this =" << this 
     << " pr=" << pr << " ne =" << ne << endl; 
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> NIDLListNode<T>* NIDLList<T>::prepend(T* item) {
  NIDLListNode<T>* pn = new(pseglist->getPtr()) NIDLListNode<T>(item);
  if(fi==0) {
    fi=la=pn;
    return pn;
  }
  pn->ne=fi;
  fi->pr=pn;
  fi=pn;
  count--;
  return pn;
}

template<class T> NIDLListNode<T>* NIDLList<T>::append(T* item) {
  NIDLListNode<T> *pn = new(pseglist->getPtr()) NIDLListNode<T>(item);
  if(fi==0) {
    fi=la=pn;
    return pn;
  }
  pn->pr=la;
  la->ne=pn;
  la=pn;
  count++;
  return pn;
}

/*
  template<class T> void NIDLList<T>::insAfter(NIDLListNode<T> *p,T *item)
{
  //NIDLListNode<T> *t=OPERATOR_NEW NIDLListNode<T>(item);
  NIDLListNode<T> *t = new(pseglist->getPtr()) NIDLListNode<T>(item);
  if (!h) h=t->selfConnect();
  else if (!p) h=t->placeBefore(h);
  else t->placeAfter(p);
}

template<class T> void NIDLList<T>::insBefore(NIDLListNode<T> *p,
T *item) {
  //NIDLListNode<T> *t=OPERATOR_NEW NIDLListNode<T>(item);
  NIDLListNode<T> *t = new(pseglist->getPtr()) NIDLListNode<T>(item);
  if (!h) h=t->selfConnect();
  else if (!p) t->placeAfter(h->prev());
  else { 
    t->placeBefore(p);
    if (p==h) h=t;
  }
}

template<class T> T* NIDLList<T>::replace(NIDLListNode<T> *&p, T *item){
  //NIDLListNode<T> *t = OPERATOR_NEW NIDLListNode<T>(item);
  NIDLListNode<T> *t = new(pseglist->getPtr()) NIDLListNode<T>(item);
  if (p->ne() == p) t->selfConnect();
  else t->replace(p);
  if (h == p) h = t;
  T *d=p->getData();
  //delete p;
  p = t;
  return d;
}

template<class T> void NIDLList<T>::join(NIDLList<T>& b) {
  NIDLListNode<T>* t = b.h;
  if (!h) h = t;
  else if (t) h->switchBackConnectionWith(t);
  b.h = 0;
}

template<class T> void NIDLList<T>::joinAtFront(NIDLList<T> &b) {
  NIDLListNode<T>* t = b.h;
  if (!h) h=t;
  else if (t) {
    h->switchBackConnectionWith(t);
    h=t;
  }
  b.h=0;
}

template<class T> T* NIDLList<T>::delAfter(NIDLListNode<T> *p) {
  //CHECK_POINTER(h);
//cannot delete after last
  //CHECK_TEST(p != h->prev());
//make sure if p exists that it belongs to a list
  //CHECK_TEST(p == 0 || p->ne() != 0);

  if (!p) p = h->prev();
  NIDLListNode<T>* t = p->ne();
  if (t==p) h=0;
  else {
    if (t == h) h = t->ne();
    t->disconnect();
  }
  T *d=t->getData();
  //delete t;
  return d;
}

template<class T> T* NIDLList<T>::delBefore(NIDLListNode<T> *p) {
  //CHECK_POINTER(h);
//cannot delete before first
  //CHECK_TEST(p != h);
//make sure if p exists that it belongs to a list
  //CHECK_TEST(p == 0 || p->prev() != 0);

  if (!p) p=h;
  NIDLListNode<T>* t = p->prev();
  if (t==p) h=0;
  else {
    if (t == h) h = t->ne();
    t->disconnect();
  }
  T *d=t->getData();
  //delete t;
  return d;
}

template<class T> T* NIDLList<T>::delAndNe(NIDLListNode<T> *&p) {
  //CHECK_POINTER(h);
  //CHECK_POINTER(p);

  NIDLListNode<T>* q = 0;
  NIDLListNode<T>* t = p->ne();
  if (t == p) h = 0;
  else {
    if (p != h->prev()) q = t;
    if (p == h) h = t;
    p->disconnect();
  }
  T *d=p->getData();
  //delete p;
  p = q;
  return d;
}

template<class T> T* NIDLList<T>::delAndPrev(NIDLListNode<T> *&p) {
  //CHECK_POINTER(h);
  //CHECK_POINTER(p);

  NIDLListNode<T>* q = 0;
  NIDLListNode<T>* t = p->prev();
  if (t == p) h = 0;
  else {
    if (p != h) q = t;
    if (p == h) h = p->ne();
    p->disconnect();
  }
  T *d=p->getData();
  //delete p;
  p = q;
  return d;
}
*/
template<class T> NIDLListNode<T>* NIDLListNode<T>::remove() {
  if(pr==0) { //this if first
    ne->pr=0;
    ne=0;
    pr=0;
    return this;
  }

  if(ne==0) { //this is last
    pr->ne=0;
    ne=0;
    pr=0;
    return this;
  }

  pr->ne=ne;
  ne->pr=pr;
  ne=0;
  pr=0;
  return this;
}

template<class T> T NIDLList<T>::remove(NIDLListNode<T> *p) {
  if(p==fi) {
    fi=p->ne;
    p->ne->pr=0;
    p->ne=0;
    p->pr=0;
    return p->get();
  }

  if(p==la) {
    la=p->pr;
    p->pr->ne=0;
    p->ne=0;
    p->pr=0;
    return p->get();
  }

  p->pr->ne=p->ne;
  p->ne->pr=p->pr;
  p->ne=0;
  p->pr=0;
  return p->get();
}

template<class T> ostream& operator<<(ostream& strm,
const NIDLList<T> &list) {
  NIDLListNode<T>* p = list.first();
  if (p) {
    for( int l=0; p; p=list.ne(p), l++ ){
      strm << l << " : " << *p << "\n";
    }
  } else strm << "list is empty" << endl;
  return strm;
}


  template<class T> void NIDLList<T>::printOn(ostream& os) const {
    int l=0;
    os << "NIDLList<T>: " << this << endl;
    for (NIDLListNode<T> *p = first();p;p=next(p), l++) {
      //os << l << " : " << p << " p.t=" << p->get() << endl;
    }
    os << "\n";
}

/*
template<class T> void NIDLList<T>::reverse() {
  INTEGER l=length();
  NIDLListNode<T> *tbk=0;
  NIDLListNode<T> *tfd=0;
  NIDLListNode<T> *t=h;
  for (INTEGER i=0;i<l;i++,t=tbk) t->reverseConnections(tbk,tfd);
  h=h->ne();
}

template<class T> void NIDLList<T>::swapWithPrev(NIDLListNode<T> *p) {
  //CHECK_TEST(!empty());
  //CHECK_POINTER(p);
  //CHECK_TEST(owns(p));

//if p==h we are swapping first with last
  NIDLListNode<T> *pbk=p->prev();
  if (pbk==h) h=p;
  else if (p==h) h=pbk;
  p->switchWithPrevious();
}

template<class T> void NIDLList<T>::swapWithNe(NIDLListNode<T> *p) {
  //CHECK_TEST(!empty());
  //CHECK_POINTER(p);
  //CHECK_TEST(owns(p));
//if p==h->prev() we are swapping first with last
  //CHECK_TEST(p!=h->prev());

  NIDLListNode<T> *pfd=p->ne();
  if (p==h) h=pfd;
  else if (pfd==h) h=p;
  p->switchWithNe();
}

template<class T> void NIDLList<T>::swap(NIDLListNode<T> *p,
NIDLListNode<T> *q) {
  if (p==q) return;
  //CHECK_TEST(!empty());
  //CHECK_TEST(p!=0 && q!=0);
  //CHECK_TEST(owns(p) && owns(q));

  NIDLListNode<T> *pbk=p->prev();
  if (pbk==q) {
    swapWithPrev(p);
    q=p->ne();
    return;
  }
  NIDLListNode<T> *pfd=p->ne();
  if (pfd==q) {
    swapWithNe(p);
    q=p->prev();
    return;
  }
  if (p==h) h=q;
  else if (q==h) h=p;
  p->switchWith(q);
}

template<class T> int NIDLList<T>::length() const {
  int l = 0;
  NIDLListNode<T>* t = h;
  if (t != 0) do { ++l; t = t->ne(); } while (t != h);
  return l;
}

template<class T> BOOLEAN NIDLList<T>::owns(const NIDLListNode<T> *p) 
const {
  if (h != 0 && p != 0) {
    NIDLListNode<T>* t = h;
    do {
      if (t == p) return TRUE;
      t = t->ne();
    } while (t != h);
  }
  return FALSE;
}

template<class T> BOOLEAN NIDLList<T>::progress(
const NIDLListNode<T> *p,const NIDLListNode<T> *q) const {
  //CHECK_TEST(!empty());
  //CHECK_TEST(owns(p));

  do {
    if (p==q) return TRUE;
    p=p->ne();
  } while (p!=h);
  return FALSE;
}

template<class T> BOOLEAN NIDLList<T>::regress(const NIDLListNode<T> *p,
const NIDLListNode<T> *q) const {
  //CHECK_TEST(!empty());
  //CHECK_TEST(owns(p));

  do {
    if (p==q) return TRUE;
    p=p->prev();
  } while (p!=h->prev());
  return FALSE;
}

template<class T> BOOLEAN NIDLList<T>::ok() const {
  BOOLEAN v = TRUE;
  if (h) {
    NIDLListNode<T> *t = h;
    long count = LONG_MAX;
    do {
      count--;
      v &= t->prev()->ne() == t;
      v &= t->ne()->prev() == t;
      t = t->next();
    } while (v && count > 0 && t != h);
    v &= count > 0;
  }
//make sure we found the end of the list
  assert(v);
  return v;
}
*/
