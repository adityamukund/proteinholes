function [ varargout ] = testSuite_rca2()

% Written by Jurgen Sladeczek
% Department of Mathematics, Duke University


testSuiteName = 'rca2';


totalTestsRun = 0;
totalTestsPassed = 0;


disp( [ 10 'Running Test Suite "' testSuiteName '"' ] );


%
% Run Tests test_rca2pc
%

% Subset of tests in 'test_rca2pc_1'

[ testName, testsPassed, testsRun ] = test_rca2pc_1();
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

