addpath('../PointClouds');
NTHREADS=int32(4);

%Create a set of random sparse vectors (seeded)
rng(120);
X = sprandn(10, 50, 0.1);

A.theta=.5;
A.numlevels=int32(20);
A.minlevel=int32(0);
A.NTHREADS=NTHREADS;
A.BLOCKSIZE=int32(32);
A.N = int32(size(X, 1));
A.dim = int32(size(X, 2));


N = size(X, 2);
A.numlevels = int32(N);

[I, J, S] = find(X);
I = int32(I - 1);
J = int32(J - 1);

B = covertreesparse(A, I, J, S);

rootLevel = B.outparams(2);
