#!/usr/bin/python

import sys
import numpy as np

args = sys.argv[1:]

pers = {}
pers[0] = []
pers[1] = []
pers[2] = []

f = open(args[0])
for line in iter(f):
    split = [float(x) for x in line.split(" ")]
    persistence = split[3]-split[4] if (split[4]>0) else float('inf')
    pers[split[0]].append([split[3], split[4]-split[3]])

pers[0] = sorted(pers[0], key=lambda x: -x[1])[0:100]
pers[1] = sorted(pers[1], key=lambda x: -x[1])[0:100]
pers[2] = sorted(pers[2], key=lambda x: -x[1])[0:100]

pers[0] = np.asmatrix(pers[0])
pers[1] = np.asmatrix(pers[1])
pers[2] = np.asmatrix(pers[2])

avgs = [np.mean(pers[i], axis=0).tolist()[0][1] for i in xrange(3)]
print(" ".join([str(x) for x in avgs]))

f.close()
