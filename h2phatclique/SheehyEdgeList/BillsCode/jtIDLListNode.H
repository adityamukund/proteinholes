#ifndef JTIDLLIST_NODE_H
#define JTIDLLIST_NODE_H
#include <iostream>

using namespace std;

class jtIDLListNode {
  private:
    jtIDLListNode* bk;
    jtIDLListNode* fd;
  public:
    jtIDLListNode() : bk(0), fd(0) {;}
    jtIDLListNode* prev() const { return bk; }
    jtIDLListNode* next() const { return fd; }
    jtIDLListNode* selfConnect() { return fd=bk=this; }
    jtIDLListNode* placeBefore(jtIDLListNode *n) {
      fd=n; bk=n->bk; n->bk->fd=this; return n->bk=this;
    }
    jtIDLListNode* placeAfter(jtIDLListNode *n) {
      fd=n->fd; bk=n; n->fd->bk=this; return n->fd=this;
    }
    jtIDLListNode* replace(jtIDLListNode *n) {
      n->bk->fd=this; n->fd->bk=this; bk=n->bk; fd=n->fd; return n;
    }
    void switchBackConnectionWith(jtIDLListNode *n) {
      jtIDLListNode *t=n->bk; bk->fd=n; n->bk=bk; bk=t; t->fd=this;
    }
    jtIDLListNode* disconnect() {
      bk->fd=fd; fd->bk=bk; return this;
    }
    void reverseConnections(jtIDLListNode *&p,jtIDLListNode *&n) {
      p=fd; n=bk; fd=n; bk=p;
    }
    void switchWithPrevious() {
      if (bk!=fd) {
	jtIDLListNode *bkbk=bk->bk; fd->bk=bk; bkbk->fd=this; 
	bk->bk=this; bk->fd=fd; fd=bk; bk=bkbk;
      }
    }
    void switchWithNext() {
      if (bk!=fd) {
	jtIDLListNode *fdfd=fd->fd; bk->fd=fd; fdfd->bk=this;
	fd->fd=this; fd->bk=bk; bk=fd; fd=fdfd;
      }
    }
    void switchWith(jtIDLListNode *n) {
      jtIDLListNode *b=bk; jtIDLListNode *f=fd; fd->bk=bk->fd=n; bk=n->bk;
      fd=n->fd; n->fd->bk=n->bk->fd=this; n->bk=b; n->fd=f;
    }
    virtual void printOn(ostream &os = cerr) const {
      os << "jtIDLListNode: this = " << this
         << " bk = " << bk << " fd = " << fd;
    }
};   
inline ostream& operator<<(ostream &os,const jtIDLListNode &node) {
  node.printOn(os); return os;
}
#endif
