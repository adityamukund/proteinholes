function [ weightedsimplicies ] = ctree( X,theta,numlevels, epsilon )
%CTREE Outputs the edges of a cover tree for a point cloud X with
%user-inputs theta and numlevels
%   Detailed explanation goes here

addpath(genpath('SheehyEdgeList/'));

NTHREADS=int32(feature('numCores'));
A.theta=theta;
A.numlevels=int32(numlevels);
A.minlevel=int32(0);
A.NTHREADS=NTHREADS;
A.BLOCKSIZE=int32(32);

numvert = size(X,1);

disp('computing cover tree for point cloud')
B = covertree(A, X');
rootLevel = B.outparams(2);
B.levels(:, 1) = min(B.levels(:, 1), A.numlevels);

disp('getting edge list from cover tree and sorting by weight')
[S, ~] = SlowSparseEdgeList(X, B.radii, B.levels, B.theta, rootLevel, epsilon);
%[S, ~] = SlowSparseEdgeListOld(X, B.radii, B.levels, B.theta, rootLevel);

edges=S(numvert+1:end,:);
edges=sortrows(edges,3);
S(numvert+1:end,:)=edges;

weightedsimplicies=S;

maxedges = numvert*(numvert-1)/2;
actualedges = size(weightedsimplicies,1)-numvert;
str=sprintf('Cover tree reduced the number of edges from %d to %d, a reduction of %.3g%%.',maxedges,actualedges,100*(maxedges-actualedges)/maxedges);
disp(str)
end

