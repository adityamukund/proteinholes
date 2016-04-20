function [ varargout ] = test_rca2pc_1()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca2pc_1';

testIndex = 1;
testsPassed = 0;

% input values:

pointCloud = load( 'D2.txt' );


%
% Run Test 1
%

distanceBoundOnEdges = 0.5;
birthDeathGivers = 1;


%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );

[I2, I0, e, v] = rca2pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' }, 2000 );


% Check on some random values
if e == 1339 && I2( 1, 4 ) == 196 && I2( 2, 4 ) == 187

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( '* Test 1 FAILED' );
end


%
% Run Test 2
%

testIndex = testIndex + 1;

distanceBoundOnEdges = 0.4;
birthDeathGivers = 1;

[I2, I0, e, v] = rca2pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' }, 2000 );


% Check on some random values
if e == 932 && abs( I2( 1, 1 ) - 0.2631 ) < 0.0001

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

