#!/bin/bash

while read line
do
  pdb=${line:0:4}
  cd $pdb

  echo
  echo "${pdb}"

  # wtr
  #echo "WILD TYPE:"
  #cd wtr
  #../../../scripts/phatclique2 -i "${pdb}".dm -m 4 -o "${pdb}".ph
  #cd ..

  # rig
  #echo "RIGID:"
  #cd rig
  #../../../scripts/phatclique2 -i "${pdb}".dm -m 4 -o "${pdb}".ph
  #cd ..

  # vox
  #echo "VOXELS:"
  #cd vox
  #../../../scripts/phatclique2 -i "${pdb}".dm -m 4 -o "${pdb}".ph
  #cd ..

  # ell
  echo "ELLIPSES:"
  cd ell
  ../../../scripts/phatclique2 -i "${pdb}".dm -m 4 -o "${pdb}".ph
  cd ..

  cd ..
done < muts_small.txt
