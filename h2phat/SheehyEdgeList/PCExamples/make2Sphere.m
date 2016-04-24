%Make a sphere of radius R with as close to N points as possible
function [ X ] = make2Sphere( R, N )
    k = round(sqrt(N));
    t = linspace(0, 2*pi, k+1);
    [theta, phi] = meshgrid(t, t);
    x = R*cos(phi) .* cos(theta);
    y = R*cos(phi) .* sin(theta);
    z = R*sin(phi);
    X = [x(:) y(:) z(:)];    
end