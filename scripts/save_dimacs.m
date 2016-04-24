addpath(genpath('~/Dropbox/MATH412/FPROJ/code/'));
run('~/Dropbox/MATH412/FPROJ/code/h2phatclique/SheehyEdgeList/init.m');
X = csvread(strcat(pdb,'.csv'));
WS = ctree(X, 0.5, 20, 1/3);
makedimacs(WS, size(X,1), strcat(dir,'/',pdb,'.dm'));
