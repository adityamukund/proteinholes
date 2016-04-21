# proteinholes

To get this to work (after opening matlab):

1. Add h2phatclique and all subfolders to your path
2. Run h2phatclique/SheehyEdgeList/init.m 
3. Navigate to the directory with the protein you care about. Then, run `python get_pdb_coords.py [file].pdb` to get the csv file for the atomic coordinates. 
4. In matlab, run:
    1. X = csvread('[file].pdb')
    2. WS = ctree(X, theta, numlevels, epsilon) 
    3. makedimacs(WS, numvertices, filename)
5. In the command line, run h2phatclique/build/phatclique -i file.dimacs -m 4 -o file.out
6. In matlab, run `plotpdiags('outputfilename')`

Note that increasing theta decreases the number of points in your tree; this reduces the overall accuracy of the results, but sometimes you have to do that so the thing actually finishes. 
