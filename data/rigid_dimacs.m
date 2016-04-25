init;
cd('../../data');

files = dir;
directoryNames = {files([files.isdir]).name};
directoryNames = directoryNames(~ismember(directoryNames,{'.','..'}));

cell2mat(directoryNames);
for k=1:length(directoryNames)
    dir = char(directoryNames(k));
    cd(dir);
    fprintf(strcat(dir,'\n'));
    
    cd('vox');  
    X = csvread(strcat(pwd,'/',dir,'.csv'));
    WS = ctree(X, 0.5, 20, 1/3);
    makedimacs(WS, size(X,1), strcat(dir,'.dm'));
    fprintf('\n');
    cd('../');
    
    cd('ell');  
    X = csvread(strcat(pwd,'/',dir,'.csv'));
    WS = ctree(X, 0.5, 20, 1/3);
    makedimacs(WS, size(X,1), strcat(dir,'.dm'));
    fprintf('\n');
    cd('../');
    
    cd('wtr');  
    X = csvread(strcat(pwd,'/',dir,'.csv'));
    WS = ctree(X, 0.5, 20, 1/3);
    makedimacs(WS, size(X,1), strcat(dir,'.dm'));
    fprintf('\n');
    cd('../');
    
    cd('rig');  
    X = csvread(strcat(pwd,'/',dir,'.csv'));
    WS = ctree(X, 0.5, 20, 1/3);
    makedimacs(WS, size(X,1), strcat(dir,'.dm'));
    fprintf('\n');
    cd('../');
    
    cd('../');    
end