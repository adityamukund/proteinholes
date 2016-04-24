#ifndef TYPES_H
#define TYPES_H


#ifdef DOUBLE
#define REAL double
#define REAL_MAX DBL_MAX
#define COPY dcopy
#define AXPY daxpy
#define GESVD dgesvd
#define NRM2 dnrm2
#endif

#ifdef FLOAT
#define REAL float
#define REAL_MAX FLT_MAX
#define COPY scopy
#define AXPY saxpy
#define GESVD sgesvd
#define NRM2 snrm2
#endif

#endif

