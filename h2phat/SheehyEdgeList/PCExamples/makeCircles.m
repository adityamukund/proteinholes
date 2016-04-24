function [X] = makeCircles(N)
% this samples uniformly (by angle) from two circles, one of radius 1
% centered at (0,0) and one of radius 0.1 centered at (2,2).
    if nargin < 1
        N = 100;
    end
    t = linspace(0, 2*pi, N+1);
    t = t(1:end-1);
    X = zeros(length(t), 2);
    X(:, 1) = cos(t);
    X(:, 2) = sin(t);
    %X = [X ; X/10 + repmat([2 2], length(t), 1)];
end