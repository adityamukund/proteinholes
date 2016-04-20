function [ varargout ] = rca1pc( pc, distanceBoundOnEdges, BirthDeathGivers, extraInputParams )

%RCA1PC This function computes the persistence diagrams (in dimensions zero and one) of a point cloud
%pc with given distance bound.  It relies on the Java TDA set.  
%
%The output is both the 0 and 1-D persistence diagrams, as well as the number of edges
%and the number of columns in the reduction matrix:
%
%If only one object is captured in output, it is the 1-D diagram.  
%
%If two, they are the 1-D and 0-D diagrams.  
%
%If four, the are the 1-D and 0-D diagrams, followed by the number of edges
%and the number of columns in the reduction matrix.

%ZeroGen is an optional Boolean input (default false). If set to true, then
%the zero-dimensional diagram will contain more information. Specifically,
%each row will have four values: birth, death, index of edge thtat killed
%the component, index of vertex that birthed the component.

% hjs 11/5/2014
% extraInputParams is an optional input, for providing additional settings values to the Tda application

%Written by Rann Bar-On, Department of Mathematics, Duke University

if nargin < 3
  BirthDeathGivers = false;
end

if nargout==3
    err = MException('OutputChk:OutOfRange', 'You may not call this function with 3 output variables!  Use 1, 2, or 4!');
    throw(err);
end

if size(pc,2)>size(pc,1)
    disp('WARNING: matrix has more rows than columns.  Do you want to transpose it?')
end

if isempty(pc) 

    err = MException('ResultChk:OutOfRange', 'Point cloud is empty!  Perhaps you forgot to transpose an array somewhere?');
    throw(err);
end

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

param2 = strcat( 'distanceBoundOnEdges=', num2str( distanceBoundOnEdges ) ); % set distancebound as string

% hjs 11/4/14 test override of 'suppressAllOutput' setting
%tda.RCA1( { 'taskChoice=M12', 'supplyDataAs=pointCloud', 'suppressAllOutput=no', param2 }, pc);  % compute 1-D persistence, using supplied parameters
% better: this does a local override of setting 'suppressAllOutput':
%tda.assignData( 'suppressAllOutput=yes' );
tda.RCA1( { 'taskChoice=M12', 'supplyDataAs=pointCloud', param2 }, pc);  % compute 1-D persistence, using supplied parameters

% hjs 10/21/2014 Add expanded I1 results, and tie them to zeroGen input parameter
if BirthDeathGivers
	I0 = tda.getResults(0).getIntervalsAndBirthDeathGiversDim0;  % 0-D intervals with extra info
	I1 = tda.getResults(1).getIntervalsAndBirthDeathGiversDim1;  % 1-D intervals with extra info
else 
	I0 = tda.getResults(0).getIntervals;  % 0-D intervals
	I1 = tda.getResults(1).getIntervals;  % 1-D intervals
end

edges = tda.getResults(2); % number of edges
redcols = tda.getResults(3); % number of columns in the reduction matrix

computationTimeM12 = tda.getResults(4); % timing info (formatted) for computation of matrix M12
reductionTimeM12 = tda.getResults(5); % % timing info (formatted) for reduction of matrix M12
computationTimeRawM12 = tda.getResults(6); % timing info (raw) for computation of matrix M12
reductionTimeRawM12 = tda.getResults(7); % % timing info (raw) for reduction of matrix M12
memoryUseBeforeComputingM12 = tda.getResults(8); % memory use before computation of matrix M12
memoryUseAfterComputingM12 = tda.getResults(9); % % memory use after computation (before reduction) of matrix M12

varargout{1}=I1;

if nargout>1
    varargout{2}=I0;
end

if nargout>2
    varargout{3}=edges;
    varargout{4}=redcols;
end

if nargout>4
    varargout{5}=computationTimeM12;
    varargout{6}=reductionTimeM12;
end

if nargout>6
    varargout{7}=computationTimeRawM12;
    varargout{8}=reductionTimeRawM12;
end

if nargout>8
    varargout{9}=memoryUseBeforeComputingM12;
    varargout{10}=memoryUseAfterComputingM12;
end
end

