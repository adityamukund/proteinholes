%% Data Expeditions - Part 2

%Create SW point cloud of the tMAX data

start = 367 + 365 + 365 + 365; %left-endpoint of first window
startDate = dataRDUTemperatures(start,1);

totalSlide = 365;

final = start+totalSlide;%left-endpoint of final window
finalDate = dataRDUTemperatures(final,1);

delay = 1;
embDim = 360; %embedding dimension

swShift = 1; %difference between left-endpoint of consecutive windows.

numPoints = length(start:swShift:final);

mySWPC = zeros(numPoints,embDim);

%construct SWPC row-by-row where each row is a window
for jj = 1:numPoints
    
    ii=swShift*(jj-1);
    mySWPC(jj,:)=tMax((start+ii):start+ii+(embDim-1));   

end

distances = pdist(mySWPC);
dm = squareform(distances);

%compute persistence from distance matrix
distanceBoundOnEdges = max(distances);
init;
[I,J] = rca1dm(dm,distanceBoundOnEdges);

%plot 0-dimensional persistence diagram
figure;
plotpersistencediagram(J);
%choose approriate title and axis labels
title(sprintf('0-Dimensional Persistence Diagram, Start Date: %i', startDate));
xlabel('Birth');
ylabel('Death');

%plot 1-dimensional persistence diagram
figure;plotpersistencediagram(I);
%choose appropriate title and axis labels
title(sprintf('1-Dimensional Persistence Diagram, Start Date: %i', startDate));
xlabel('Birth');
ylabel('Death');

%plot sorted list of persistence
sortedI = sortbypersistence(I);
figure;plot(sortedI(:,3),'r*');
%choose appropriate title and axis labels
title(sprintf('Sorted List of Persistence of 1-D Persistence Classes, Start Date: %i', startDate));
xlabel('Index');
ylabel('Persistence');


%% Choosing other startDates and repeating
%the following command might come in handy for labelling your figures
%modify it as you see fit
%title(sprintf('myTitle Start Date: %i',startDate));
