NTHREADS=int32(4);

A.theta=.5;
A.numlevels=int32(20);
A.minlevel=int32(0);
A.NTHREADS=NTHREADS;
A.BLOCKSIZE=int32(32);

for i=1:1000
  i
  X=rand(4,5000);
  B=covertree(A,X);
  Y=rand(4,200);
  E.numlevels=int32(10);
  E.distances=.25;
  D=findwithin(B,X,E,Y,A.NTHREADS);
  D=findnearest(B,X,Y,int32(20),A.NTHREADS);
  size(D.distances)
end
