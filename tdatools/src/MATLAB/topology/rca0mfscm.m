function [ varargout ] = rca0mfscm( mfscm, distanceBoundOnEdges, birthDeathGivers, extraInputParams )

% rca0mfscm computes the 0-D persistence diagrams associated to a simplex-constant 
% function F on a simplicial complex K.
%
% It relies on the Java TDA set.  The output is the 0-D persistence diagrams, as well as the number of edges.
%
% If only one object is captured in output, it is the 0-D diagram.  
%
% If two, they are the 0-D diagram and the edges.  

% birthDeathGivers is an optional Boolean input (default false). If set to true, then
% the zero-dimensional diagram will contain more information. Specifically,
% each row will have four values: birth, death, index of edge thtat killed
% the component, index of vertex that birthed the component.

% More precisely:
% - The F-values on the vertices may be arbitrary.
%
% - The F-values on an edge must not be smaller than the F-value of its two vertices.
%
% - The F-value on a higher simplex must be the maximum F-value
%   on any of its boundary edges.
%
% The input to this code is a distance bound distanceBoundOnEdges, and an N by 3 array mfscm.
%
% The rows in mfscm are of two forms, subject to conditions 1 and 3 below
%
% The code attempts to aggressively fix the matrix so that condition 2 is
% fulfilled.  The user should not have to think about vertex numbering...
%
% Condition 1: For each vertex i, S must have a row of the form 
%
%               i   i   F(i)
%
% Condition 2: The vertices must be indexed from 0 to (n-1), where n is the
% number of vertices.  Additionally, F(i) <= F(i+1) for all i.
%
% Condition 3: The other rows in S are of the form
%
%               i  j  F(i,j)
%
% where F(i,j) is defined to be the F-value on the edge between vertex i and vertex j.
% F(i,j) must be greater than or equal to F(i) and F(j).

% extraInputParams is an optional input, for providing additional settings values to the Tda application

% Written by Jurgen Sladeczek, based on rca1pc written by Rann Bar-On, 
% Department of Mathematics, Duke University


if nargin < 3
  birthDeathGivers = false;
end

if nargout>2
    err = MException('OutputChk:OutOfRange', 'This function supports only (a maximum of) 2 output variables.');
    throw(err);
end

if isempty(mfscm) 
    err = MException('ResultChk:OutOfRange', 'Array is empty!');
    throw(err);
end

if size(mfscm,2) ~= 3
    err = MException('ResultChk:OutOfRange', 'Matrix is not an n x 3 matrix!');
    throw(err);
end    

s=size(mfscm,1);          % Edges + vertices

coords = mfscm(:,1:2);
numverts=max(coords(:))+1;  % total number of vertices
numedges=s-numverts;


diags=zeros(numverts,3);          % initialize diagonal part of array
offdiags=zeros(numedges,3);      % initialize off diagonal part of array

j=1;                   % counter of diagonal entries (i.e. vertices)
k=1;                   % counter for off diagonals

for i=1:s              % create valid array for TDA Java
    if mfscm(i,1)==mfscm(i,2)
        diags(j,:)=mfscm(i,:);  % this is a diagonal entry, so put it in diags
        j=j+1;           % next diagonal
    else
        offdiags(k,:)=mfscm(i,:);  % Non-diagonal entry, put it in offdiags
        k=k+1;          % next off diagonal
    end
end

diags = sortrows(diags,1); % put diagonal entries in order of original vertex numbering

% Condition 1: check if we have function values for all vertices
if ~(isequal(diags(:,1)' + 1, 1:numverts))
    err = MException('ResultChk:OutOfRange', 'Matrix does not contain function values for all vertices! See condition 1 in documentation for rca1sm!');
    throw(err);
end

% Condition 2: fix matrix to make sure it's fulfiled.  No need for use to
% do this manually!
[sorteddiags,ind]=sortrows(diags,3); % sort diagonal entries in order of F value, storing the permutation from the previous sort

m=vertcat(sorteddiags,offdiags); % Create an initial matrix with the sorted diagonals and the off-diagonal entries

m(:,1:2)=changem(m(:,1:2),0:(numverts-1),ind-1);   % renumber the vertices using the sort index.  
                                                   % This ensures condition 2 is fulfiled without
                                                   % requiring it from the user.


% Condition 3: check if function value on edges is at least the function value on their vertices.                                                   
diags=m(1:numverts,:); % get new diagonal entries
offdiags=m(numverts+1:end,:); % get new off-diagonal entries

for i=1:numedges
    
    r=offdiags(i,1);        % row number
    c=offdiags(i,2);        % column number
    
    if offdiags(i,3)<diags(r+1,3) || offdiags(i,3)<diags(c+1,3)  % check if function on this edge is at least the function value on its vertices.
        err = MException('ResultChk:OutOfRange', 'Value on edge is less than value of vertices!  This is not a monotonic function on a simplex!  See condition 3 in documentation for rca1sm!');
        throw(err);
    end
    
end

% We got here without error, so the matrix m should be correct.  Pass it on.


%
% Run the TDA app
%

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

param2 = strcat( 'distanceBoundOnEdges=', num2str( distanceBoundOnEdges ) ); % set distancebound as string
    
tda.RCA0( { 'taskChoice=RCA0', 'supplyDataAs=monotonicFunctionOnSimplicialComplexMatrix', param2 }, m);  % compute 0-D persistence, using supplied parameters


%
% Collect the results
%

if birthDeathGivers
	I0 = tda.getResults(0).getIntervalsAndBirthDeathGiversDim0;  % 0-D intervals with extra info
else 
	I0 = tda.getResults(0).getIntervals;  % 0-D intervals
end

edges = tda.getResults(2); % number of edges

varargout{1} = I0;

if nargout>1
    varargout{2} = edges;
end

end

