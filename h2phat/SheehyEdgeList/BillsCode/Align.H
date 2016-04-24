#ifndef ALIGN_H
#define ALIGN_H

//#include "Types.H"

#ifdef ALPHA
# define ALIGN_SIZE 8
# define ALIGN_EXPONENT 3    //2^ALIGN_EXPONENT=ALIGN_SIZE
# define INT_EXPONENT 2  //2^INT_EXPONENT=sizeof(int)
#else
# define ALIGN_SIZE 8
# define ALIGN_EXPONENT 3    //2^ALIGN_EXPONENT=ALIGN_SIZE
# define INT_EXPONENT 2  //2^INT_EXPONENT=sizeof(int)
#endif

inline void Align(size_t &n) {
  size_t q=n>>ALIGN_EXPONENT;
  size_t m=q*ALIGN_SIZE;
  n=  m<n ? m+ALIGN_SIZE : m;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

//Assumes ptr_in and ptr_out are aligned on a word boundary

inline void rawCopy(const void* ptr_in,void* ptr_out,
size_t nbytes) {
  size_t count=nbytes>>INT_EXPONENT;
  const int *iin=reinterpret_cast<const int*>(ptr_in);
  int *iout=reinterpret_cast<int*>(ptr_out);
  for (int i=0;i<count;i++) iout[i]=iin[i];

  const char *chin=reinterpret_cast<const char*>(ptr_in);
  char *chout=reinterpret_cast<char*>(ptr_out);
//should use memcpy here
  for (int j=count*sizeof(int);j<nbytes;j++) chout[j]=chin[j];
}

#undef ALIGN_SIZE
#undef ALIGN_EXPONENT
#undef INT_EXPONENT
    
#endif
