function [ varargout ] = test_rca1pc_1()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca1pc_1';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

load( 'data.mat' );
pointCloud = data;
distanceBoundOnEdges = 20;
birthDeathGivers = 1;

%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );

[I1, I0, e, v] = rca1pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


% Check on some random values
if e == 19900 && I0( 1, 4 ) == 145 && I0( 20, 4 ) == 163

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( '* Test 1 FAILED' );
end


%
% Run Test 2
%

testIndex = testIndex + 1;

distanceBoundOnEdges = 1;
birthDeathGivers = 1;

[I1, I0, e, v] = rca1pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


% Check on some random values
if e == 6584

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( '* Test 2 FAILED' );
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

