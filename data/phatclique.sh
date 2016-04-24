#!/bin/bash
# ---------------------------
#$ -cwd
#$ -N phatclique
#$ -o phatclique.out -j y
#$ -l mem_free=64g
# ---------------------------

bash compute_persistences.sh
