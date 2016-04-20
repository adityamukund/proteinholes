function [ varargout ] = testSuite_rca()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testSuiteName = 'RCA';


totalTestsRun = 0;
totalTestsPassed = 0;


if nargout<2

	disp( [ 10 10 '********************************' ] );
	disp( [ 'Running Test Suite "' testSuiteName '"' ] );
	disp( [ '********************************' 10 10 ] );
end

%
% Run Tests testSuite_rca0
%

% Subset of tests in 'testSuite_rca0'

[ testName, testsPassed, testsRun ] = testSuite_rca0();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );





%
% Run Tests testSuite_rca1 (just as placeholder)
%


% Subset of tests in 'testSuite_rca1'

[ testName, testsPassed, testsRun ] = testSuite_rca1();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );








%
% Run Tests testSuite_rca2 (just as placeholder)
%


% Subset of tests in 'testSuite_rca2'

[ testName, testsPassed, testsRun ] = testSuite_rca2();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );












if nargout<2

	% Summary of test results over all subtests of this test suite
	disp( [ 10 '*** Summary of all tests ' testSuiteName ': ' num2str( totalTestsPassed ) ' of ' num2str( totalTestsRun ) ' tests passed.' ] );
end


% pass summary results (numbers) up the chain

varargout{1} = [ 'testSuite_' testSuiteName];
if nargout>1
    varargout{2} = totalTestsPassed;
end
if nargout>2
    varargout{3} = totalTestsRun;
end

end

