function [ varargout ] = testSuite_tda()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University

% Main test script (all other tests or suites are called from here, across different levels)
testSuiteName = 'TDA';


totalTestsRun = 0;
totalTestsPassed = 0;

disp( [ 10 10 '********************************' ] );
disp( [ 'Running Test Suite "' testSuiteName '"' ] );
disp( [ '********************************' 10 10 ] );


%
% Run Tests testSuite_regression
%

% Subset of tests in 'testSuite_regression'

[ testName, testsPassed, testsRun ] = testSuite_regression();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );



%
% Run Tests testSuite_rca
%

% Subset of tests in 'testSuite_rca'

[ testName, testsPassed, testsRun ] = testSuite_rca();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );





%
% Run Tests testSuite_rca1 (just as placeholder)
%


% Subset of tests in 'testSuite_lsd'

[ testName, testsPassed, testsRun ] = testSuite_lsd();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Summary from ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );








if nargout<2

	% Summary of test results over all subtests of this test suite
	disp( [ 10 '*** Summary of all tests ' testSuiteName ': ' num2str( totalTestsPassed ) ' of ' num2str( totalTestsRun ) ' tests passed.' ] );
end


% pass summary results (numbers) up the chain

varargout{1} = [ 'testSuite_' testSuiteName ];
if nargout>1
    varargout{2} = totalTestsPassed;
end
if nargout>2
    varargout{3} = totalTestsRun;
end

end

