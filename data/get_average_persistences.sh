#!/bin/bash

while read line
do
  pdb=${line:0:4}
  cd $pdb

  echo
  echo "${pdb}"

  # wtr
  echo "WILD TYPE:"
  cd wtr
  strw=$(python ../../../scripts/get_average_persistences.py ${pdb}.ph)
  cd ..

  # rig
  echo "RIGID:"
  cd rig
  strr=$(python ../../../scripts/get_average_persistences.py ${pdb}.ph)
  cd ..

  # vox
  echo "VOXELS:"
  cd vox
  strv=$(python ../../../scripts/get_average_persistences.py ${pdb}.ph)
  cd ..

  # ell
  echo "ELLIPSES:"
  cd ell
  stre=$(python ../../../scripts/get_average_persistences.py ${pdb}.ph)
  cd ..

  echo $strw >> ../average_wtr.txt
  echo $strr >> ../average_rig.txt
  echo $strv >> ../average_vox.txt
  echo $stre >> ../average_ell.txt

  cd ..
done < muts.txt
