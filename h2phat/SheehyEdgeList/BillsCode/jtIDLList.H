#ifndef JTIDLLIST_H
#define JTIDLLIST_H 
//#include "Types.H"
#include "jtIDLListNode.H"
//do not use Tracer in this class: we use this for MemoryDebugger

//the instantiation will fail unless T is derived from jtIDLListNode
template<class T> class jtIDLList {
  private:
    jtIDLListNode *h;
  public:
    jtIDLList() : h(0) {;}
    ~jtIDLList() {;}
    bool empty() const { return h==0; }
    bool notEmpty() const { return h!=0; }

    T* first() const { return dynamic_cast<T*>(h); }
    T* next(const jtIDLListNode *p) const {
      return dynamic_cast<T*>((p==0||h==0||p==h->prev())?0 :p->next());
    }
    T* prev(const jtIDLListNode *p) const {
      return dynamic_cast<T*>((p == 0 || p == h) ? 0 : p->prev());
    }
    T* last() const { 
      return dynamic_cast<T*>(h == 0 ? 0 : h->prev());
    }

    void prepend(T*);
    void append(T*);
    void insAfter(T*,T*);
    void insBefore(T*,T*);
    void replace(T*,T*);
    void join(jtIDLList&);
    void joinAtFront(jtIDLList&);
    T* delAfter(jtIDLListNode*);
    T* delBefore(jtIDLListNode*);
    T* delAndNext(T*&);
    T* delAndPrev(T*&);
    void remove(T*);
    void reverse();
    void swapWithPrev(jtIDLListNode*);
    void swapWithNext(jtIDLListNode*);
    void swap(jtIDLListNode*,jtIDLListNode*);
    int length() const;
    bool owns(const jtIDLListNode*) const;
    bool progress(const jtIDLListNode*,const jtIDLListNode*) const ;
    bool regress(const jtIDLListNode*,const jtIDLListNode*) const ;
    bool ok() const;

    void printOn(ostream& os = cerr) const;
};
template<class T> ostream& operator<<(ostream&,const jtIDLList<T> &);
#endif
