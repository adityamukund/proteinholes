#!/bin/bash

rgdir="/usr/project/dlab/Users/am439/ellipses/bounds/wt_rec/test_osprey/43_nowtr_7/test_rig"
vxdir="/usr/project/dlab/Users/am439/ellipses/bounds/wt_rec/test_osprey/43_nowtr_7/test_vox"
eldir="/usr/project/dlab/Users/am439/ellipses/bounds/wt_rec/test_osprey/43_nowtr_7/test_ell"


while read pdb; do
  mkdir ${pdb}
  cd ${pdb}

  mkdir wtr
  cd wtr
  scp am439@login.cs.duke.edu:${rgdir}/${pdb}/${pdb}_cleaned.pdb ./${pdb}.pdb
  cd ../
 
  mkdir rig
  cd rig
  scp am439@login.cs.duke.edu:${rgdir}/${pdb}/${pdb}.GMEC.pdb ./${pdb}.pdb
  cd ..

  mkdir vox
  cd vox
  scp am439@login.cs.duke.edu:${vxdir}/${pdb}/${pdb}.GMEC.pdb ./${pdb}.pdb
  cd ..

  mkdir ell
  cd ell
  scp am439@login.cs.duke.edu:${eldir}/${pdb}/${pdb}.GMEC.pdb ./${pdb}.pdb
  cd ..

  cd ..
done < pdbs_small.txt
