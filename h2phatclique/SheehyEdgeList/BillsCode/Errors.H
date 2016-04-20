#ifndef ERRORS_H
#define ERRORS_H "$Header: /home/faculty/johnt/cvs/memdebug/Errors.H,v 1.6 2001/11/15 21:20:27 wka Exp $"
#include <assert.h>
#include <stdlib.h>

# ifdef DEBUG
#include <iostream>

//if we throw but don't catch, then all destructors are called,
//  and the debugger traces back to the Memory Debugger
//THANK YOU, BJARNE

//don't use throw for copy constructors: Stroustrup p 373 
#   define CHECK_TEST(s) assert(s);
#   define CHECK_POINTER(p) assert(p);
#   define CHECK_DIRECTION(d) assert(d>=0 && d<SPACEDIM); 
#   define CHECK_SAME_DIRECTIONS(d1,d2) assert(d1==d2);
#   define CHECK_DIFFERENT_DIRECTIONS(d1,d2) assert(d1!=d2);
#   define CHECK_LEVELS(d1,d2) assert(d1==d2);
#   define CHECK_NONNEGATIVE(r) assert(r>=0);
#   define CHECK_POSITIVE(r) assert(r>0);
#   define CHECK_BOUNDS(v,l,u) assert(v>=l && v<u);
#   define CHECK_NONZERO(q) assert(q!=0);
#   define CHECK_SAME(v1,v2) assert((v1)==(v2));
#   define CHECK_CONTAINS(T,b,v) assert(b.contains(v));
#   define CHECK_STRICTLY_CONTAINS(T,b,v) assert((b).strictlyContains(v));
# else
#   define CHECK_TEST(s)
#   define CHECK_POINTER(p)
#   define CHECK_DIRECTION(d)
#   define CHECK_SAME_DIRECTIONS(d1,d2)
#   define CHECK_DIFFERENT_DIRECTIONS(d1,d2)
#   define CHECK_LEVELS(d1,d2)
#   define CHECK_NONNEGATIVE(r)
#   define CHECK_POSITIVE(r)
#   define CHECK_BOUNDS(v,l,u)
#   define CHECK_NONZERO(q)
#   define CHECK_SAME(v1,v2)
#   define CHECK_CONTAINS(T,b,v)
#   define CHECK_STRICTLY_CONTAINS(T,b,v)
# endif
# define OBSOLETE(s) assert(0);
#endif
