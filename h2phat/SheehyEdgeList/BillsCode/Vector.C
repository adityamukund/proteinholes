#include <cmath>
#include "Vector.H"
#include <assert.h>
#include "MemoryDebugger.H"

REAL Vector::getDist(const Point* q) const {
  const Vector* w=dynamic_cast<const Vector*>(q);
  const REAL* y=w->x;
  REAL distsq=0.0;
  for(int k=0;k<dim;k++) {
    REAL diff=x[k]-y[k];
    distsq+=diff*diff;
  }
  return sqrt(distsq);
}  

void Vector::write(ofstream& ofs) const {
  ofs.write((char*)x,dim*sizeof(REAL));
}

void Vector::printOn(ostream& os) const {
  //os << "i=" << i << " x=(";
  os << "(";
  for(int j=0;j<dim-1;j++) {
    os << x[j] << ",";
  }
  os << x[dim-1] << ")";
}


Vectors::Vectors(const REAL* pX,int iN) :
  Points(), N(iN), current(0), vectors(0), X(pX), may_delete(false) {

#ifdef MEM_DEBUG
  vectors=(Vector*)OPERATOR_NEW_BRACKET(char,N*sizeof(Vector));
#else
  vectors=(Vector*)new char[N*sizeof(Vector)];
#endif

  for(int i=0;i<N;i++) {
    new((void*)(vectors+i)) Vector(X+i*dim);
    // mem_check();
  }
}

Vectors::Vectors(const char* filename,int start,int iN) :
  Points(), N(iN), current(0), vectors(0), X(0) {
#ifdef MEM_DEBUG
  X=OPERATOR_NEW_BRACKET(REAL,N);
#else
  X=new REAL[N];
#endif

  ifstream ifs;
  ifs.open(filename,ios::binary);
  ifs.seekg(start*dim*sizeof(REAL),ios::beg);
  long int nbytes=N*dim*sizeof(REAL);
  ifs.read((char*)X,nbytes);
  long int count=ifs.gcount();
  cout << "count=" << count << endl;
  if(N*dim*sizeof(REAL)!=count)
    assert(0);

#ifdef MEM_DEBUG
  vectors=(Vector*)OPERATOR_NEW_BRACKET(char,N*sizeof(Vector));
#else
  vectors=(Vector*)new char[N*sizeof(Vector)];
#endif

  for(int i=0;i<N;i++) {
    new((void*)(vectors+i)) Vector(X+i*dim);
  }
}


Vectors::~Vectors() { 
  delete [] (char*)vectors;

  if(may_delete) {
    delete [] X;
  }
}

const Vector* Vectors::next() {
  if(current==N) {
    return 0;
  } else {
    return vectors+current++;
  }
}


int Vectors::getIndex(const Point* p) const {
  const Vector* v=dynamic_cast<const Vector*>(p);
  const REAL* x=v->x;
  return (x-X)/dim;
}


void Vectors::printOn(ostream& os) const {
  for(int i=0;i<N;i++) {
    vectors[i].printOn(os);
  }
}

