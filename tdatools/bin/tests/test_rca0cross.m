function [ varargout ] = test_rca0cross()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca0cross: Cross sample';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

pointCloud = load( 'data_CrossSample.txt' );
distanceBoundOnEdges = 3;

birthDeathGivers = 0;

%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );


D = LSD( pointCloud, [ 0 0 ], 0.3 );
[I1, I0] = rca1dm( D, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


%disp( I0 );
%disp( I1 );
%disp( size( I1 ) );

sizeI0 = size( I0 );
%disp( sizeI0 );

% Check on some random values

% Don't know e until I can find the parameters to reproduce the result:
if abs( I0( 1, 1 ) - 0.00755 ) < 0.0001  &&  abs( I0( 1, 2 ) - 0.21216 ) < 0.0001 && sizeI0(1,1) == 4

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( [ 10 '* Test ' num2str( testIndex ) ' FAILED.' ] );
end



% Summary of test results


disp( [ 10 'Test Summary for test_' testName ] );
disp( [ '*** ' num2str( testsPassed ) ' of ' num2str( testIndex ) ' tests passed.' 10 ] );



varargout{1} = [ 'test_' testName ];
if nargout>1
    varargout{2} = testsPassed;
end
if nargout>2
    varargout{3} = testIndex;
end

end

