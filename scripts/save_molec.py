#!/usr/bin/python

import os
import sys
import pymol

args = sys.argv[1:]
pdb = args[0]+".pdb"
nums = "+".join(args[1:])

pymol.cmd.load(pdb)
print("loaded pdb")
pymol.cmd.select("muts", "resi "+nums)
print("selected muts")
pymol.cmd.select("near", "muts around 3.3")
print("selected near")
pymol.cmd.select("core", "muts or near")
print("selected core")
pymol.cmd.select("rest", "not core")
print("selected rest")
pymol.cmd.remove("rest")
print("removed rest")
pymol.cmd.save(pdb, "core")
print("saved file")
