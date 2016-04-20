function [ varargout ] = testSuite_regression()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testSuiteName = 'Regression';


totalTestsRun = 0;
totalTestsPassed = 0;


disp( [ 10 'Running Test Suite "' testSuiteName '"' ] );


%
% Run Tests test_rca1swissroll
%

% Subset of tests in 'test_rca1swissroll'

[ testName, testsPassed, testsRun ] = test_rca1swissroll();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );





%
% Run Tests test_rca1diamond
%


% Subset of tests in 'test_rca1diamond'

[ testName, testsPassed, testsRun ] = test_rca1diamond();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );





%
% Run Tests test_rca1diamond
%


% Subset of tests in 'test_rca1diamond'

[ testName, testsPassed, testsRun ] = test_rca1diamond();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );






%
% Run Tests test_rca1swissroll
%


% Subset of tests in 'test_rca1swissroll'

[ testName, testsPassed, testsRun ] = test_rca1swissroll();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );




%
% Run Tests test_rca0diamond
%


% Subset of tests in 'test_rca0diamond'

[ testName, testsPassed, testsRun ] = test_rca0diamond();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );



%
% Run Tests test_rca0cross
%


% Subset of tests in 'test_rca0cross'

[ testName, testsPassed, testsRun ] = test_rca0cross();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );






%
% Run Tests test_rca0heightFunction
%


% Subset of tests in 'test_rca0heightFunction'

[ testName, testsPassed, testsRun ] = test_rca0heightFunction();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );







%
% Run Tests test_rca0HarmonicPoint
%


% Subset of tests in 'test_rca0HarmonicPoint'

[ testName, testsPassed, testsRun ] = test_rca0HarmonicPoint();
totalTestsPassed = totalTestsPassed + testsPassed;
totalTestsRun = totalTestsRun + testsRun;
disp( [ '*** Test ' testName ': ' num2str( testsPassed ) ' of ' num2str( testsRun ) ' tests passed.' ] );











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

