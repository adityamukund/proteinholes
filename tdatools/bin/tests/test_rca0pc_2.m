function [ varargout ] = test_rca0pc_2()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'rca0pc_2';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

pointCloud = load( 'D2.txt' );
distanceBoundOnEdges = 3;
birthDeathGivers = 1;

%disp( pointCloud );
%disp( distanceBoundOnEdges );
%disp( birthDeathGivers );

[I0,e] = rca0pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


% Check on some random values
if e == 19900

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( '* Test 1 FAILED' );
end


%
% Run Test 2
%

testIndex = testIndex + 1;

distanceBoundOnEdges = 0.8;
birthDeathGivers = 1;

[I0,e] = rca0pc( pointCloud, distanceBoundOnEdges, birthDeathGivers, { 'suppressAllOutput=yes' } );


% Check on some random values
if e == 4152

	disp( [ 10 '* Test ' num2str( testIndex ) ' passed.' ] );
	testsPassed = testsPassed + 1;
else

	disp( '* Test 2 FAILED' );
end


% Summary of test results


if nargout<2

	disp( [ 10 'Test Summary for test_' testName ] );
	disp( [ '*** ' num2str( testsPassed ) ' of ' num2str( testIndex ) ' tests passed.' 10 ] );
end



varargout{1} = [ 'test_' testName ];
if nargout>1
    varargout{2} = testsPassed;
end
if nargout>2
    varargout{3} = testIndex;
end

end

