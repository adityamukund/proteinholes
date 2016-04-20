function [ varargout ] = rca2pc( pc, distanceBoundOnEdges, BirthDeathGivers, extraInputParams, limitForPopupDisplay )

% RCA12PC This function computes the persistence diagrams (in dimension two) of a point cloud
% pc with given distance bound.  It relies on the Java TDA set.  
%
%The output is both the 0 and 2-D persistence diagrams, as well as the number of edges
%and the number of columns in the reduction matrix:
%
%If only one object is captured in output, it is the 1-D diagram.  
%
%If two, they are the 2-D and 0-D diagrams.  
%
%If four, the are the 2-D and 0-D diagrams, followed by the number of edges
%and the number of columns in the reduction matrix.

%ZeroGen is an optional Boolean input (default false). If set to true, then
%the zero-dimensional diagram will contain more information. Specifically,
%each row will have four values: birth, death, index of edge thtat killed
%the component, index of vertex that birthed the component.

% extraInputParams is an optional input, for providing additional settings values to the Tda application

% Written by Jurgen Sladeczek, based on rca1pc written by Rann Bar-On, 
% Department of Mathematics, Duke University

% When the number of edges exceeds this number, we display a popup to the user
% to ask whether we should continue ( because the execution time may be very long):
% hjs 11/10/2014 Added an override by providing an input for this value in the function definition
% (because we need to be able to run, e.g., in test mode, without the popup)
if nargin < 5
	limitForPopupDisplay = 1000;
end

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


% first run RCA0 to find out how many edges (over 1k, ask user if they really want to continue:
tda.RCA0( { 'taskChoice=RCA0',  'suppressAllOutput=yes', 'supplyDataAs=pointCloud', ...
	param2 }, pc);  % compute 0-D persistence, using supplied parameters
edges = tda.getResults(2); % number of edges


if edges > limitForPopupDisplay
	str1 = strcat( 'Your data contains n=', num2str( edges ), ...
		' edges, which may result in a very long execution time of the RCA2 algorithm. ', ...
		'Do you want to continue?.' );

	% Construct a questdlg, to give user option to bail out (RCA2 may take a long time, and Matlab is not
	% kind when it comes to interupting a running program
	choice = questdlg( str1, ...
		'Alert', ...
		'Continue','Stop','Stop' );
	% Handle response
	switch choice
	    case 'Stop'
	        boolContinue = false;
	    case 'Continue'
	        disp(['Continuing...'])
	        boolContinue = true;
	end
else 
	boolContinue = true;
end

if boolContinue

	% reset the Tda object
	tda = initializejavaTDA();  % create a new Java TDA object
	
	% Assign the extra settings parameters
	if nargin > 3
	
	  tda.assignData( extraInputParams );
	end

	%tda.assignData( { 'suppressAllOutput=yes' } );
	% User wants to execute RCA2/M23, so here it goes:
	tda.RCA2( { 'taskChoice=M23', 'supplyDataAs=pointCloud', ...
		 param2 }, pc);  % compute 2-D persistence, using supplied parameters
	
	% Select whether to include the extra data in the return values
	if BirthDeathGivers
		I0 = tda.getResults(0).getIntervalsAndBirthDeathGiversDim0;  % 0-D intervals with extra info
		I2 = tda.getResults(1).getIntervalsAndBirthDeathGiversDim1;  % 2-D intervals with extra info
	else 
		I0 = tda.getResults(0).getIntervals;  % 0-D intervals
		I2 = tda.getResults(1).getIntervals;  % 2-D intervals
	end
	
	% Get the remaining data from the tda object
	edges = tda.getResults(2); % number of edges
	redcols = tda.getResults(3); % number of columns in the reduction matrix
	
	% Assign the data based on the return paramaters that the user supplied when calling this method
	varargout{1}=I2;
	
	if nargout>1
	    varargout{2}=I0;
	end
	
	if nargout==4
	    varargout{3}=edges;
	    varargout{4}=redcols;
	end
else

	% User cancelled the execution, so we will have no results, hence:
	% set all return data to 0 (except edges)

	varargout{1}=0;

	if nargout>1
	    varargout{2}=0;
	end
	
	if nargout==4
	    varargout{3}=edges;
	    varargout{4}=0;
	end
end


end

