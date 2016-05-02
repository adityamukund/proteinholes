#!/usr/bin/python

import numpy as np 
import sys
import os
import csv
from sklearn.ensemble import RandomForestClassifier

sr=0

print("Loading data...")
ell = np.loadtxt(open("persistences/persist_ell.csv", "rb"), delimiter=",", skiprows=sr)
vox = np.loadtxt(open("persistences/persist_vox.csv", "rb"), delimiter=",", skiprows=sr)
wtr = np.loadtxt(open("persistences/persist_wtr.csv", "rb"), delimiter=",", skiprows=sr)
rig = np.loadtxt(open("persistences/persist_rig.csv", "rb"), delimiter=",", skiprows=sr)
con = np.vstack((ell, vox)).tolist()

label_e = ["ell"]*ell.shape[0]
label_v = ["vox"]*vox.shape[0]
label_w = ["wtr"]*wtr.shape[0]
label_r = ["rig"]*rig.shape[0]
label_c = ["con"]*(ell.shape[0]+vox.shape[0])

# truncate data
ell = ell[:,100:]
vox = vox[:,100:]
wtr = wtr[:,100:]
rig = rig[:,100:]

clf = RandomForestClassifier(n_estimators=1000)


counts = [0, 0, 0]

print("Euclidean distances:")
for i in range(40-sr):
    e = np.asarray(ell[i])
    v = np.asarray(vox[i])
    w = np.asarray(wtr[i])
    r = np.asarray(rig[i])

    ew = np.linalg.norm(e-w)
    vw = np.linalg.norm(v-w)
    rw = np.linalg.norm(r-w)
    mw = min([ew, vw, rw])

    if (mw==ew):
        counts[0] += 1
    elif (mw==vw):
        counts[1] += 1
    elif (mw==rw):
        counts[2] += 1
print("wtr ==> {'ell': "+str(counts[0])+", 'vox': "+str(counts[1])+", 'rig': "+str(counts[2])+"}")

def test_clf(a, b, t, la, lb, lt):
    print()
    print("Training data: "+la[0]+" vs "+lb[0])
    trainingSet = np.vstack((a,b)).tolist()
    labels = la + lb
    clf = RandomForestClassifier(random_state=2)
    clf.fit(trainingSet, labels)
    predictions = clf.predict(wtr.tolist())
    counts = dict.fromkeys([la[0], lb[0]], 0)
    for prediction in predictions:
        counts[prediction] += 1
    print(lt[0] + " ==> "+str(counts))


test_clf(ell, vox, wtr, label_e, label_v, label_w)
test_clf(rig, vox, ell, label_r, label_v, label_e)
test_clf(ell, rig, vox, label_e, label_r, label_v)
test_clf(vox, ell, rig, label_v, label_e, label_r)
test_clf(np.vstack((ell, vox)), rig, wtr, label_c, label_r, label_w)

# print()
# print("CONTINUOUS VS. RIGID")
# print("Training data: ellipse/voxel vs rigid...")
# trainingSet = np.vstack((ell, vox, rig)).tolist()
# labels = label_c + label_r
# clf = RandomForestClassifier()
# clf.fit(trainingSet, labels)
# print("Testing on wild type...")
# predictions = clf.predict(wtr.tolist())
# count = 0
# for prediction in predictions:
    # if (prediction=="con"):
        # count+=1
# print("Number of continuous predictions: "+str(count)+"/"+str(wtr.shape[0]))

# print()
# print("ELLIPSE VS. RIGID")
# print("Training data: ellipse vs. rigid...")
# trainingSet = np.vstack((ell, rig)).tolist()
# labels = label_e + label_r
# clf.fit(trainingSet, labels)
# print("Testing on voxels...")
# predictions = clf.predict(vox.tolist())
# count = 0
# for prediction in predictions:
    # if (prediction=="ell"):
        # count+=1
# print("Number of ellipse predictions: "+str(count)+"/"+str(vox.shape[0]))

# print()
# print("VOXEL VS. RIGID")
# print("Training data: voxel vs. rigid...")
# trainingSet = np.vstack((vox, rig)).tolist()
# labels = label_v + label_r
# clf.fit(trainingSet, labels)
# print("Testing on ellipses...")
# predictions = clf.predict(ell.tolist())
# count=0
# for prediction in predictions:
    # if (prediction=="vox"):
        # count+=1
# print("Number of voxel predictions: "+str(count)+"/"+str(ell.shape[0]))

# print()
# print("VOXEL VS ELLIPSE")
# print("Training data: voxel vs. ellipse...")
# trainingSet = np.vstack((ell, vox)).tolist()
# labels = label_e + label_v
# clf.fit(trainingSet, labels)
# print("Testing on rigid...")
# predictions = clf.predict(rig.tolist())
# count=0
# for prediction in predictions:
    # if (prediction=="vox"):
        # count+=1
# print("Number of voxel predictions: "+str(count)+"/"+str(rig.shape[0]))

# print()
# print("WILD TYPE SIMILARITY")
# print("Training data: voxel vs. ellipse vs. rigid...")
# trainingSet = np.vstack((ell, vox, rig)).tolist()
# labels = label_e + label_v + label_r
# clf.fit(trainingSet, labels)
# print("Testing on wild type...")
# predictions = clf.predict(wtr.tolist())
# count_e = 0
# count_v = 0
# for prediction in predictions:
    # if (prediction=="vox"):
        # count_v += 1
    # elif (prediction=="ell"):
        # count_e += 1
# print("Number of voxel predictions: "+str(count_v)+"/"+str(wtr.shape[0]))
# print("Number of ellipse predictions: "+str(count_e)+"/"+str(wtr.shape[0]))

# print()
# print("THE CONTINUOUS SHOWDOWN")
# print("Training data: voxel vs. ellipse...")
# trainingSet = np.vstack((ell, vox)).tolist()
# labels = label_e + label_v
# clf.fit(trainingSet, labels)
# print("Testing on wild type...")
# predictions = clf.predict(wtr.tolist())
# count=0
# for prediction in predictions:
    # if (prediction=="ell"):
        # count+=1
# print("Number of ellipse predictions: "+str(count)+"/"+str(rig.shape[0]))

