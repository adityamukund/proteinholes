#!/usr/bin/python

from __future__ import print_function
import sys
import numpy as np


args = sys.argv[1:]

pers0 = []
pers1 = []
pers2 = []

f = open(args[0])
for line in iter(f):
    split = [float(x) for x in line.split(" ")]
    persistence = split[4] - split[3] if (split[4] > 0) else float('inf')
    if (int(split[0])==1):
        pers1.append(persistence)
    elif (int(split[0])==2):
        pers2.append(persistence)
    elif (int(split[0])==0):
        pers0.append(persistence)
f.close()
pers0 = sorted(pers0, key=lambda x:-x)[0:100]
pers1 = sorted(pers1, key=lambda x:-x)[0:100]
pers2 = sorted(pers2, key=lambda x:-x)[0:100]
pers = [str(x) for x in pers0+pers1+pers2]

outfile = open(args[1], 'a')
print(",".join(pers), file=outfile)
outfile.close()
