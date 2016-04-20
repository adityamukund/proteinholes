%% Data Expeditions - Part 1

%load the dataset
load ('dataRDUTemperatures.mat');

%extract daily max/min temperatures as two separate signals
tMax = dataRDUTemperatures(:,2);
tMin = dataRDUTemperatures(:,3);

maxMax = max(tMax);
maxMin = min(tMax);
minMax = max(tMin);
minMin = min(tMin);

%plot the tMAX time-series
plot(tMax);axis tight;
 ax=gca;
 data_decades = [1 3654 7306 10959]; %true indices of 1980,1990,2000,2010.
 ax.XTick = data_decades;
 ax.XTickLabel= {'1980' '1990' '2000' '2010'};

 %choose appropriate title and axis labels
 xlabel('Decade');
 ylabel('Daily Maximum Temperature');
 title('Daily Maximum Temperatures, 1980-2010');

%plot the tMin time-series
plot(tMin);axis tight;
  ax=gca;
  ax.XTick = data_decades;
  ax.XTickLabel = {'1980', '1990', '2000', '2010'};
  xlabel('Decade');
  ylabel('Daily Minimum Temperature');
  title('Daily Minimum Temperatures, 1980-2010');

for i = 1:12510
   max1 = dataRDUTemperatures(i,2);
   min1 = dataRDUTemperatures(i,3);
   max2 = dataRDUTemperatures(i+365, 2);
   min2 = dataRDUTemperatures(i+365, 3);
   if (abs(max1-max2) > 50) && (abs(min1-min2) > 50)
       s = sprintf('%d, %d', i, i+365);
       disp(s);
   end
end
    
