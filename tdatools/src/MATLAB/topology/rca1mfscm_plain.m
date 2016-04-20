function [ varargout ] = rca1mfscm_plain( mfscm, distanceBoundOnEdges, birthDeathGivers, extraInputParams )

% rca1mfscm(mfscm, distanceBoundOnEdges) This code computes the 1- and 0-D 
% persistence diagrams associated to a simplex-constant function F on a simplicial complex K.
%
% It relies on the Java TDA set.  The output
% is both the 0 and 1-D persistence diagrams, as well as the number of edges
% and the number of columns in the reduction matrix:
%
% If only one object is captured in output, it is the 1-D diagram.  
%
% If two, they are the 1-D and 0-D diagrams.  
%
% If four, the are the 1-D and 0-D diagrams, followed by the number of edges
% and the number of columns in the reduction matrix.
%
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

% hjs 11/11/2014
% removed extra checks (thus "_plain" version)
% extraInputParams is an optional input, for providing additional settings values to the Tda application

% Written by Rann Bar-On, Department of Mathematics, Duke University

if nargin < 3
  birthDeathGivers = false;
end


if nargout==3
    err = MException('OutputChk:OutOfRange', 'You may not call this function with 3 output variables!  Use 1, 2, or 4!');
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

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

param2 = strcat( 'distanceBoundOnEdges=', num2str( distanceBoundOnEdges ) ); % set distancebound as string


%tda.assignData( 'suppressAllOutput=yes' );
tda.RCA1( { 'taskChoice=M12', 'supplyDataAs=monotonicFunctionOnSimplicialComplexMatrix', param2 }, mfscm );  % compute 1-D persistence, using supplied parameters


% hjs 10/21/2014 Add expanded I1 results, and tie them to birthDeathGivers input parameter
if birthDeathGivers
	I0 = tda.getResults(0).getIntervalsAndBirthDeathGiversDim0;  % 0-D intervals with extra info
	I1 = tda.getResults(1).getIntervalsAndBirthDeathGiversDim1;  % 1-D intervals with extra info
else 
	I0 = tda.getResults(0).getIntervals;  % 0-D intervals
	I1 = tda.getResults(1).getIntervals;  % 1-D intervals
end


edges = tda.getResults(2); % number of edges
redcols = tda.getResults(3); % number of columns in the reduction matrix

varargout{1}=I1;

if nargout>1
    varargout{2}=I0;
end

if nargout==4
    varargout{3}=edges;
    varargout{4}=redcols;
end

end

