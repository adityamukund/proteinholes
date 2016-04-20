#include "jtIDLList.H"
#include <limits.h>
#include <values.h>
#include <stdlib.h>
#include "Errors.H"
//do not use Tracer in this class: we use this for MemoryDebugger
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::prepend(T *item) {
  if (!h) h=item->selfConnect();
  else h=item->placeBefore(h);
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::append(T *item) {
  if (!h) h=item->selfConnect();
  else item->placeAfter(h->prev());
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::insAfter(T *p,T *item) {
  if (!h) h=item->selfConnect();
  else if (!p) h=item->placeBefore(h);
  else item->placeAfter(p);
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::insBefore(T *p, T *item) {
  if (!h) h=item->selfConnect();
  else if (!p) item->placeAfter(h->prev());
  else {
    item->placeBefore(p);
    if (p==h) h=item;
  }
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::replace(T *oldp, T *newt) {
  CHECK_TEST(owns(oldp));

  if (oldp->next() == oldp) newt->selfConnect();
  else newt->replace(oldp);
  if (h == oldp) h = newt;
  oldp = newt;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::join(jtIDLList<T>& b) {
  jtIDLListNode *t=b.h;
  if (!h) h = t;
  else if (t) h->switchBackConnectionWith(t);
  b.h = 0;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::joinAtFront(jtIDLList<T> &b) {
  jtIDLListNode *t=b.h;
  if (!h) h=t;
  else if (t) {
    h->switchBackConnectionWith(t);
    h=t;
  }
  b.h=0;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> T* jtIDLList<T>::delAfter(jtIDLListNode *p) {
  CHECK_POINTER(h);
//cannot delete after last
  CHECK_TEST(p != h->prev());
//make sure if p exists that it belongs to a list
  CHECK_TEST(p == 0 || p->next() != 0);

  if (!p) p = h->prev();
#ifdef __GNUC__
  T *t = static_cast<T*>(p->next());
#else
  T *t = (T*)(p->next());
#endif
  if (t==p) h=0;
  else {
    if (t == h) h = t->next();
    t->disconnect();
  }
  return t;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> T* jtIDLList<T>::delBefore(jtIDLListNode *p) {
  CHECK_POINTER(h);
//cannot delete before first
  CHECK_TEST(p != h);
//make sure if p exists that it belongs to a list
  CHECK_TEST(p == 0 || p->prev() != 0);

  if (!p) p=h;
#ifdef __GNUC__
  T *t = static_cast<T*>(p->prev());
#else
  T *t = (T*)(p->prev());
#endif
  if (t==p) h=0;
  else {
    if (t == h) h = t->next();
    t->disconnect();
  }
  return t;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//p->next(p), return deleted (ie, original p)
template<class T> T* jtIDLList<T>::delAndNext(T *&p) {
  CHECK_POINTER(h);

#ifdef __GNUC__
  if (!p) p = static_cast<T*>(h);
  T *next_node = static_cast<T*>(p->next());
#else
  if (!p) p = (T*) h;
  T *next_node = (T*)(p->next());
#endif
  T* deleted_node = p;
  if (p == h->prev()) next_node = 0;
  if (next_node == p) h = 0;
  else if (p == h) h = next_node;
  p->disconnect();
  p=next_node;
  return deleted_node;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//p->prev(p), return deleted (ie, original p)
template<class T> T* jtIDLList<T>::delAndPrev(T *&p) {
  CHECK_POINTER(h);

#ifdef __GNUC__
  if (!p) p = static_cast<T*>(h->prev());
  T *prev_node = static_cast<T*>(p->prev());
#else
  if (!p) p = (T*) h->prev();
  T *prev_node = (T*)(p->prev());
#endif
  T* deleted_node = p;
  if (prev_node == p) h = 0;
  else if (p == h) { h = p->next(); prev_node=0; }
  p->disconnect();
  p=prev_node;
  return deleted_node;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::remove(T *p) {
  CHECK_TEST(owns(p));

#ifdef __GNUC__
  if (p==h) h = (h->next()==h ? static_cast<T*>(0) : h->next());
#else
  if (p==h) h = (h->next()==h ? (T*)(0) : h->next());
#endif
  p->disconnect();
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::reverse() {
  int l=length();
  jtIDLListNode *tbk=0;
  jtIDLListNode *tfd=0;
  jtIDLListNode *t=h;
  for (int i=0;i<l;i++,t=tbk) t->reverseConnections(tbk,tfd);
  h=h->next();
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::swapWithPrev(jtIDLListNode *p) {
  CHECK_TEST(!empty());
  CHECK_POINTER(p);
  CHECK_TEST(owns(p));

//if p==h we are swapping first with last
  jtIDLListNode *pbk=p->prev();
  if (pbk==h) h=p;
  else if (p==h) h=pbk;
  p->switchWithPrevious();
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::swapWithNext(jtIDLListNode *p) {
  CHECK_TEST(!empty());
  CHECK_POINTER(p);
  CHECK_TEST(owns(p));
//if p==h->prev() we are swapping first with last
  CHECK_TEST(p!=h->prev());

  jtIDLListNode *pfd=p->next();
  if (p==h) h=pfd;
  else if (pfd==h) h=p;
  p->switchWithNext();
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::swap(jtIDLListNode *p,jtIDLListNode *q) {
  if (p==q) return; 

  CHECK_TEST(!empty());
  CHECK_TEST(p!=0 && q!=0);
  CHECK_TEST(owns(p) && owns(q));

  jtIDLListNode *pbk=p->prev();
  if (pbk==q) {
    swapWithPrev(p);
    q=p->next();
    return; 
  } 
  jtIDLListNode *pfd=p->next();
  if (pfd==q) {
    swapWithNext(p);
    q=p->prev();
    return; 
  } 
  if (p==h) h=q;
  else if (q==h) h=p;
  p->switchWith(q);
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> int jtIDLList<T>::length() const {
  int l = 0;
  jtIDLListNode *t = h;
  if (t != 0) do { ++l; t = t->next(); } while (t != h);
  return l;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> bool jtIDLList<T>::owns(const jtIDLListNode *p) const {
  if (h != 0 && p != 0) {
    jtIDLListNode *t = h;
    do {
      if (t == p) return true;
      t = t->next();
    } while (t != h);
  }
  return false;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> bool jtIDLList<T>::progress(const jtIDLListNode *p,
const jtIDLListNode *q) const {
  CHECK_TEST(!empty());
  CHECK_TEST(owns(p));

  do {
    if (p==q) return true;
    p=p->next();
  } while (p!=h);
  return false;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> bool jtIDLList<T>::regress(const jtIDLListNode *p,
const jtIDLListNode *q) const {
  CHECK_TEST(!empty());
  CHECK_TEST(owns(p));

  do {
    if (p==q) return true;
    p=p->prev();
  } while (p!=h->prev());
  return false;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> bool jtIDLList<T>::ok() const {
  bool v = true;
  if (h) {
    jtIDLListNode *t = h;
    long count = LONG_MAX;      // Lots of chances to find h!
    do {
      count--;
      v &= t->prev()->next() == t;
      v &= t->next()->prev() == t;
      t = t->next();
    } while (v && count > 0 && t != h);
    v &= count > 0;
  }
  return v;
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> void jtIDLList<T>::printOn(ostream& os) const {
  if (ok()) {
    int l=0;
    os << "jtIDLList: " << this << endl;
    for (T *p = first();p;p=next(p), l++) {
      os << l << " : "; p->printOn(os);
    }
  } else {
    os << "IDLLIst: list is not ok" << endl;
  }
  os << "\n";
}
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
template<class T> ostream& operator<<(ostream& s,const jtIDLList<T> &dl) {
  T *p=dl.first();
  if (p) {
    for (int l=0; p; p=dl.next(p), l++) {
      s << l << " : "; p->printOn(s); s << endl;
    }
  } else s << "list is empty" << endl;
  return s;
}
