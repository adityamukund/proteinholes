NTHREADS=int32(16);

A.theta=.5;
A.numlevels=int32(20);
A.minlevel=int32(0);
A.NTHREADS=NTHREADS;
A.BLOCKSIZE=int32(10);

for i=1:100
  i
  X=rand(2,500);
  B=covertree(A,X)
  Y=rand(2,200);
  D=findnearest(B,X,Y,int32(20),A.NTHREADS);
  size(D.distances)
end
