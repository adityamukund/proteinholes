#!/bin/bash

while read line; do
  pdb=${line:0:4}
  cd $pdb

  echo "${pdb}"

  # wtr
  #cd wtr
  #pymol -qrc ../../../scripts/save_molec.py -- ${line:0:4} ${line:5}
  #python ../../../scripts/save_coords.py ${pdb}
  #matlab -nodisplay -nosplash -nodesktop -r "pdb='${pdb}';dir='$PWD'; run('../../../scripts/save_dimacs.m'); exit;"
  #cd ..

  # rig
  cd rig
  pymol -qrc ../../../scripts/save_molec.py -- ${line:0:4} ${line:5}
  python ../../../scripts/save_coords.py ${pdb}
  matlab -nodisplay -nosplash -nodesktop -r "pdb='${pdb}';dir='$PWD'; run('../../../scripts/save_dimacs.m'); exit;"
  cd ..

  # vox
  #cd vox
  #pymol -qrc ../../../scripts/save_molec.py -- ${line:0:4} ${line:5}
  #python ../../../scripts/save_coords.py ${pdb}
  #matlab -nodisplay -nosplash -nodesktop -r "pdb='${pdb}';dir='$PWD'; run('../../../scripts/save_dimacs.m'); exit;"
  #cd ..

  # ell
  #cd ell
  #pymol -qrc ../../../scripts/save_molec.py -- ${line:0:4} ${line:5}
  #python ../../../scripts/save_coords.py ${pdb}
  #matlab -nodisplay -nosplash -nodesktop -r "pdb='${pdb}';dir='$PWD'; run('../../../scripts/save_dimacs.m'); exit;"
  #cd ..

  cd ..
done < muts.txt
