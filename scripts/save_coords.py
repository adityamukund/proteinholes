#!/usr/bin/python

from Bio.PDB import *
from Bio.PDB.PDBParser import PDBParser
from Bio.PDB.Polypeptide import PPBuilder
from sys import argv
import numpy as np

args = argv[1:]

ppb = PPBuilder()
pdbname = args[0]
structure = PDBParser().get_structure(pdbname, pdbname+'.pdb')

coords = []

model = structure[0]
for chain in model:
    for residue in chain:
        for atom in residue:
            coords.append(np.asarray(atom.get_coord()).tolist())

coords = np.asmatrix(coords)

np.savetxt(pdbname+".csv", coords, delimiter=',')


