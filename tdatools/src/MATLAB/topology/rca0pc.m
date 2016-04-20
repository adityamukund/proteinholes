function [ varargout ] = rca0pc( pc, distanceBoundOnEdges, birthDeathGivers, extraInputParams )
%
% RCA0PC This function computes the persistence diagrams (in dimensions zero) of a point cloud
% pc with given distance bound.  It relies on the Java TDA set.  
%
% The output is the 0-D persistence diagram, and the number of edges
%
% If only one object is captured in output, it is the 0-D diagram.  
%
% If there are two outputs, the first one is the 0-D diagram, followed by the number of edges.
%
% birthDeathGivers is an optional Boolean input (default false). If set to true, then
% the zero-dimensional diagram will contain more information. Specifically,
% each row will have four values: birth, death, index of edge thtat killed
% the component, index of vertex that birthed the component.

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

if size(pc,2)>size(pc,1)
    disp('WARNING: matrix has more rows than columns.  Do you want to transpose it?')
end

if isempty(pc)
    err = MException('ResultChk:OutOfRange', 'Point cloud is empty!  Perhaps you forgot to transpose an array somewhere?');
    throw(err);
end


%
% Run the TDA app
%

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

param2 = strcat( 'distanceBoundOnEdges=', num2str( distanceBoundOnEdges ) ); % set distancebound as string


% hjs 11/4/14 test override of 'suppressAllOutput' setting
%tda.RCA0( { 'taskChoice=RCA0', 'supplyDataAs=pointCloud', 'suppressAllOutput=no', param2 }, pc);  % compute 0-D persistence, using supplied parameters
% better: this does a local override of setting 'suppressAllOutput':
%tda.assignData( 'suppressAllOutput=yes' );
tda.RCA0( { 'taskChoice=RCA0', 'supplyDataAs=pointCloud', param2 }, pc);  % compute 0-D persistence, using supplied parameters


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

