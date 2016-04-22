# proteinholes

Final project for Math 412, taught by Paul Bendich.

h2phatclique is the folder with all the code

scripts contains a couple of scripts, plus a top-level phatclique wrapper (that
I think works, but cannot guarantee works)

tdatools contains the tdatools code, which is needed by h2phatclique

test contains systems we've tested on -- as of 2016/04/21, these include:

1. a single alpha helix (helix)
2. a single beta sheet (beta_sheet)
3. a protein backbone from 1uw3 (glob)
4. a small protein (1fsd) -- this one ran out of memory 
5. a small portion of a peptide (small) -- I forget which peptide we got this
from...
6. a redesign of core residues of a scorpion protein toxin (1aho)
7. a redesign of core residues of rous sarcoma virus capsid protein (3g21)

---

To get this to work (after opening matlab):

1. Add h2phatclique and all subfolders to your path
2. Run `h2phatclique/SheehyEdgeList/init.m`
3. Navigate to the directory with the protein you care about. Then, run `python
get_pdb_coords.py [file].pdb` to get the csv file for the atomic coordinates.  
4. In matlab, run:
    1. `X = csvread('[file].csv')`
    2. `WS = ctree(X, theta, numlevels, epsilon) `
    3. `makedimacs(WS, numvertices, filename)`
5. In the command line, run `h2phatclique/build/phatclique -i file.dimacs -m 4
-o file.out` 
6. In matlab, run `plotpdiags('outputfilename')`

Note that increasing theta decreases the number of points in your tree; this
reduces the overall accuracy of the results, but sometimes you have to do that
so the thing actually finishes.  

I've only been able to get this thing to not crash when WS is less than 10,000
rows long, but YMMV.  
