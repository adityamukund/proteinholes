function [ varargout ] = test_rca0diamond()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca0diamond';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

pointCloud = load( 'data_Diamond.txt' );
distanceBoundOnEdges = 3;

birthDeathGivers = 1;

%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );

[I1, I0, e, v] = rca1pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );

sizeI0 = size( I0 );

% Check on some random values

if e == 6 && abs( I0( 1, 2 ) - 1.4142 ) < 0.0001  &&  abs( I0( 4, 2 ) + 1 ) < 0.0001 && sizeI0(1,1) == 4

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

