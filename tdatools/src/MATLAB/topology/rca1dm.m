function [ varargout ] = rca1dm( dm, distanceBoundOnEdges, BirthDeathGivers, extraInputParams )

%RCA1PC This function computes the 1-D persistence diagram from a pairwise
%distance matrix dm with given distance bound.  It relies on the Java TDA set.  The output
%is both the 0 and 1-D persistence diagrams, as well as the number of edges
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

%
%The input matrix dm may not strictly represent a pairwise
%distance matrix for a metric.  It must, however, satisfy the following:
%
% Condition 1: dm(i,j) = dm(j,i) for all i, j (dm has to be symmetric!)
%
% Condition 2: dm(i,i) <= dm (i,j), for all i for all j (diagonal entry cannot be greater than other entries in its row!)
%   (Since the matrix must be symmetric, this implies dm(i,i) <= dm (j,i)
%   as well)
%
%This allows us to compute persistent local homology, using the output
%matrices from LSD, for example.
%
% Alternately, the function can accept an upper- or lower-triangular
% matrix.

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
    
if isempty(dm) 
    err = MException('ResultChk:OutOfRange', 'Distance matrix is empty!');
    throw(err);
end

if size(dm,2)~=size(dm,1)
    err = MException('ResultChk:OutOfRange', 'Distance matrix is not square!');
    throw(err);
end

% The function below returns TRUE if its input is a symmetric matrix
issym=@(x) isequal(x,x');

% The function below returns TRUE if its input is an upper triangular matrix
isutri=@(x) isequal(x,triu(x));

% The function below returns TRUE if its input is an lower triangular matrix
isltri=@(x) isequal(x,tril(x));

% Check for symmetry or triangularity
if ~issym(dm)
    utri=isutri(dm);
    ltri=isltri(dm);        % These will be reused, so store
    if ~(utri || ltri)
        err = MException('ResultChk:OutOfRange', 'Distance matrix is not symmetric or triangular!');
        throw(err);
    else
        disp('WARNING: matrix is triangular.  Make sure this is what you intend!')
        if ltri
            dm=dm';  % If matrix is lower triangular, transpose it to make it upper triangular
        end        
    end
end

% Check condition 2
for i=1:size(dm,1)
    x=dm(i,i);
    for j=(i+1):size(dm,1)  % only need to check j>i, since matrix is symmetric (or upper-triangular)
        if ( x > dm(i,j) && dm(i,j) ~= -1 )
            if ltri
                err = MException('ResultChk:OutOfRange', 'Distance matrix does fulfil Condition 2! Entry (%d,%d) > entry (%d,%d)',i,i,j,i);
                    % if matrix was lower-triangular, we should output the
                    % original coordinates, not the transposed ones.
            else
                err = MException('ResultChk:OutOfRange', 'Distance matrix does fulfil Condition 2! Entry (%d,%d) > entry (%d,%d)',i,i,i,j);
            end            
            throw(err);
        end
    end
end

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

param2 = strcat( 'distanceBoundOnEdges=', num2str( distanceBoundOnEdges ) ); % set distancebound as string


tda.RCA1( { 'taskChoice=M12', 'supplyDataAs=distanceMatrix', param2 }, dm );  % compute 1-D persistence, using supplied parameters


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

% hjs 11/17/2014 Add extra outputs for timing and memory info
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