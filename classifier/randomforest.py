#!/usr/bin/python

import numpy as np
import sys
import os
import csv
from sklearn.ensemble import RandomForestClassifier


print("Loading data...")
ell = np.loadtxt(open("persist_ell.csv", "rb"), delimiter=",", skiprows=1)
vox = np.loadtxt(open("persist_vox.csv", "rb"), delimiter=",", skiprows=1)
wtr = np.loadtxt(open("persist_wtr.csv", "rb"), delimiter=",", skiprows=1)
rig = np.loadtxt(open("persist_rig.csv", "rb"), delimiter=",", skiprows=1)

label_e = ["ell"]*ell.shape[0]
label_v = ["vox"]*vox.shape[0]
label_w = ["wtr"]*wtr.shape[0]
label_r = ["rig"]*rig.shape[0]
label_c = ["con"]*(ell.shape[0]+vox.shape[0])


print()
print("CONTINUOUS VS. RIGID")
print("Training data: ellipse/voxel vs rigid...")
trainingSet = np.vstack((ell, vox, rig)).tolist()
labels = label_c + label_r
clf = RandomForestClassifier()
clf.fit(trainingSet, labels)
print("Testing on wild type...")
predictions = clf.predict(wtr.tolist())
count = 0
for prediction in predictions:
    if (prediction=="con"):
        count+=1
print("Number of continuous predictions: "+str(count)+"/"+str(wtr.shape[0]))

print()
print("ELLIPSE VS. RIGID")
print("Training data: ellipse vs. rigid...")
trainingSet = np.vstack((ell, rig)).tolist()
labels = label_e + label_r
clf = RandomForestClassifier()
clf.fit(trainingSet, labels)
print("Testing on voxels...")
predictions = clf.predict(vox.tolist())
count = 0
for prediction in predictions:
    if (prediction=="ell"):
        count+=1
print("Number of ellipse predictions: "+str(count)+"/"+str(vox.shape[0]))

print()
print("VOXEL VS. RIGID")
print("Training data: voxel vs. rigid...")
trainingSet = np.vstack((vox, rig)).tolist()
labels = label_v + label_r
clf = RandomForestClassifier()
clf.fit(trainingSet, labels)
print("Testing on ellipses...")
predictions = clf.predict(ell.tolist())
count=0
for prediction in predictions:
    if (prediction=="vox"):
        count+=1
print("Number of voxel predictions: "+str(count)+"/"+str(ell.shape[0]))

print()
print("VOXEL VS ELLIPSE")
print("Training data: voxel vs. ellipse...")
trainingSet = np.vstack((ell, vox)).tolist()
labels = label_e + label_v
clf = RandomForestClassifier()
clf.fit(trainingSet, labels)
print("Testing on rigid...")
predictions = clf.predict(rig.tolist())
count=0
for prediction in predictions:
    if (prediction=="vox"):
        count+=1
print("Number of voxel predictions: "+str(count)+"/"+str(rig.shape[0]))

print()
print("WILD TYPE SIMILARITY")
print("Training data: voxel vs. ellipse vs. rigid...")
trainingSet = np.vstack((ell, vox, rig)).tolist()
labels = label_e + label_v + label_r
clf = RandomForestClassifier()
clf.fit(trainingSet, labels)
print("Testing on wild type...")
predictions = clf.predict(wtr.tolist())
count_e = 0
count_v = 0
for prediction in predictions:
    if (prediction=="vox"):
        count_v += 1
    elif (prediction=="ell"):
        count_e += 1
print("Number of voxel predictions: "+str(count_v)+"/"+str(wtr.shape[0]))
print("Number of ellipse predictions: "+str(count_e)+"/"+str(wtr.shape[0]))
