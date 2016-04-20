function [ varargout ] = test_rca1swissroll()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca1swissroll';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

pointCloud = load( 'data_SwissRollSample.txt' );
distanceBoundOnEdges = 20;
%[m n] = rca1pc( pc, distanceBoundOnEdges );

birthDeathGivers = 1;

%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );

[I1, I0, e, v] = rca1pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


%disp( I0 );
%disp( I1 );
%disp( size( I1 ) );

sizeI0 = size( I0 );
%disp( sizeI0 );

% Check on some random values
if e == 109406 && abs( I0( 1, 1 ) - 0.044206 ) < 0.0001  &&  abs( I0( 3, 2 ) - 0.051463 ) < 0.0001

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( [ 10 '* Test ' num2str( testIndex ) ' FAILED.' ] );
end


%
% Run Test 2
%

testIndex = testIndex + 1;


% Check on some random values
if e == 109406 && sizeI0 == 500

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( [ 10 '* Test ' num2str( testIndex ) ' FAILED.' ] );
end



%
% Run Test 3
%

testIndex = testIndex + 1;


% Check on some random values
if e == 109406 && abs( I0( 500, 1 ) + 1 ) < 0.0001  &&  abs( I0( 498, 2 ) - 1.6316 ) < 0.0001

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

