function [ SortedI ] = sortbypersistence( I )
%sortbypersistence sort rows of I by 2nd minus 1st value in row
%   Input a k x 2 matrix I, which represents birth death pairs
%   Sort rows by values of I(:,2)-I(:,1) from biggest to smallest
%   output is k x 3 matrix, where the first two cols are cols of input I,
%   and third col is the lifetime.

%   Created by Hamza Ghadyali

if size(I,1)>0
    P = I(:,2)-I(:,1);
    [~,perm]=sort(P,'descend');
    SortedI = [I P];
    SortedI = SortedI(perm,:);
else
    SortedI=I;
    disp('warning:input is empty')
end

end

