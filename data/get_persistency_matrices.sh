#!/bin/bash

wtf=${PWD}/persist_wtr.csv
rtf=${PWD}/persist_rig.csv
vtf=${PWD}/persist_vox.csv
etf=${PWD}/persist_ell.csv

while read line
do
  pdb=${line:0:4}
  cd $pdb

  echo
  echo "${pdb}"

  # wtr
  echo "WILD TYPE:"
  cd wtr
  python ../../../scripts/get_persistency_matrices.py ${pdb}.ph $wtf 
  cd ..

  # rig
  echo "RIGID:"
  cd rig
  python ../../../scripts/get_persistency_matrices.py ${pdb}.ph $rtf
  cd ..

  # vox
  echo "VOXELS:"
  cd vox
  python ../../../scripts/get_persistency_matrices.py ${pdb}.ph $vtf
  cd ..

  # ell
  echo "ELLIPSES:"
  cd ell
  python ../../../scripts/get_persistency_matrices.py ${pdb}.ph $etf
  cd ..

  cd ..
done < muts.txt
