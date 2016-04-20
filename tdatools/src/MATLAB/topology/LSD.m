function [ DM ] = LSD( pc, center, radius, extraInputParams )

%LSD This function computes a local spherical distance matrix given a point
%cloud, radius, and center point
%If you feed DM to rca1dm, you will obtain (an approximation) of the local
%homology of pc at center and radius. 

% hjs 11/11/2014
% extraInputParams is an optional input, for providing additional settings values to the Tda application

%Written by Rann Bar-On, Dept of Mathematics, Duke University

tda = initializejavaTDA();  % create a new Java TDA object

% Assign the extra settings parameters
if nargin > 3

  tda.assignData( extraInputParams );
end

if isempty(pc) 
    err = MException('ResultChk:OutOfRange', 'Point cloud is empty!');
    throw(err);
end


tda.LSD(pc, radius, center);
DM = tda.getResults( 0 );

end

