function [ varargout ] = test_rca1pc_2()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testName = 'lsd_1';

%
% Run Test 1
%

testIndex = 1;
testsPassed = 0;

% input values:

X = load( 'CrossSample.txt' );

DM = LSD( X, [0 0], 0.3, { 'suppressAllOutput=yes' } );



% Check on some random values
if abs( DM( 1, 1 ) - 0.0831 ) < 0.0001 && abs( DM( 5, 5 ) - 0.0937 ) < 0.0001

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

DM = LSD( X, [0.3 0.3], 0.5, { 'suppressAllOutput=yes' } );


% Check on some random values
if abs( DM( 1, 1 ) - 0.2460 ) < 0.0001 && abs( DM( 5, 5 ) - 0.1857 ) < 0.0001

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

