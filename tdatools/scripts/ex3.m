%% Data Expeditions - Part 3

% Now we vary the sliding amount. How did I do it originally? Which
% parameters specifically were used?

delay=1;
swShift=1;

embDim = 360;

maxSlide = 800; %in days, what is the final total slide amount?
start = 367; %left endpoint of first window


maxNumPoints = maxSlide; 

%create the maximal SWPC
mySWPC = zeros(maxNumPoints,embDim);

for jj=1:maxNumPoints
    ii=swShift*(jj-1);
    mySWPC(jj,:)=tMax((start+ii):start+ii+(embDim-1));    
end

init;

% Choosing a window size means choosing number of initial columns to keep.
% Choosing a slide amount means choosing number of inital rows to keep.

% for each pair of choices, compute (birth,death) time of the 
% most persistent class.   
% Alternatively, compute the scalar quantity of lifetime=death-birth
% this requires computing 1-persistence, sorting, and taking the top one.

% consider rewriting code so that one big distance matrix is computed ahead
% of time for each embedding dimension, and then varying the slide amount
% means taking an increasing number of initial rows (and corresp. init cols) 

minSlide = 80;
slideIncrement=20;
slideList = minSlide:slideIncrement:maxSlide;
topLifetimes1 = zeros(1,length(slideList));
topL = zeros(3,length(slideList));
%each column of topL stores birth,death, death-birth of top pers class.

P = squareform(pdist(mySWPC));

bdCell = cell(1,length(slideList));

%loop varies the slide amount.    
for jj=1:length(slideList)
    kk=slideList(jj);
    subP = P(1:kk,1:kk);%takes first kk points of mySWPC.
    
    distanceBoundOnEdges = 0.9*max(subP(:));
    [I,J]=rca1dm(subP,distanceBoundOnEdges);
    clf;
    plotpersistencediagram(I);
    title(sprintf('Persistence Diagram, totalSlide: %i',kk));
    print('-dpng','-r100',sprintf('totalSlide%i.png',kk));
    %you may want to modify this to print to a different path
    
    I = sortbypersistence(I);

    bdCell{1,jj}=I;%(optional)long-term storage of 1-persistences

    if size(I,1)>0
        topLifetimes1(1,jj)=I(1,3);
        topL(:,jj) = I(1,:);
    end

end

%plot persistence vs. totalSlide
figure;
    plot(slideList,topL(3,:));
    title('Persistence of the Most Persistent 1-Cycle');
    xlabel('Total Slide');
    ylabel('Persistence');
%choose appropriate titles and axis labels

%plot individual birth times and death times on same graph
figure;
    a1 = plot(slideList,topL(1,:),'x'); M1='Birth Time ';
    title('Birth, Death, and Lifetime of the Most Persistent 1-Cycle')
    xlabel('Total Slide');
    ylabel('Time');
    hold on;
    a2 = plot(slideList,topL(2,:),'o'); M2='Death Time ';
    a3 = plot(slideList,topL(3,:)); M3='Lifetime   ';
    legend([a1;a2;a3],[M1;M2;M3]);
    hold off;
%choose approriate titles/axis labels

    
