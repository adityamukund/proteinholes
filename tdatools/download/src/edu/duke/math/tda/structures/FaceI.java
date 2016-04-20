/*
 * Created Dec 4, 2013
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 * License Info:
 * 
 * 
 */
package edu.duke.math.tda.structures;

import java.util.SortedMap;

/**
 * Documents the interface for the Face implementations
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Dec 4, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public interface FaceI extends Comparable<FaceI> {

	// vertex-related methods
	
	// Get any of the 3 vertices that make up the face (in sorted order for 0,1,2)
    public abstract int getVertexIndex( final int _i );
    // Returns whether the face contains the specified vertex
    public abstract boolean hasVertex( final int _vertexToCheck );
    
    // edge-related methods
    
    // Get any of the 3 edges that make up the face  (in sorted order for i=0,1,2;
    // note that the higher an edge's index, the longer its length, by construction)
    public abstract int getEdgeIndex( final int _i );
    
    // returns whether the face contains the specified edge
    public abstract boolean hasEdge( final EdgeI _edgeToCheck );
    
    // Get to the 'other edges' (relative to the specified one)
    public abstract int[] getIndexesForOtherEdges( final EdgeI _edge );
    public abstract EdgeI[] getOtherEdges( final EdgeI _edge );
    
    // face-related methods
    
    // Get the index of the face in the 'global' face list
    public abstract int getFaceListIndex();

    // set the index of the face
    public abstract void setFaceListIndex( final int _faceListIndex );
    
    // Get the 'other vertices' (relative to the specified one)
    public abstract int[] getOtherVertices( final int _vertexIndex );
    
    // daglist related methods
    
    public abstract SortedMap<Integer, Integer> getDagList();
    public abstract void addToDagList( final Integer edgeIndex1, 
    		final Integer edgeIndex2 );
    public abstract void mergeIntoDagList( 
    		final SortedMap<Integer, Integer> _dagListToMerge );
    public abstract void mergeIntoDagList( 
    		final SortedMap<Integer, Integer> _dagListToMerge,
    		final int _edgeOrientation );

    // Utility methods
    public abstract StringBuffer asString();
    public abstract StringBuffer asStringComplete();
    public abstract StringBuffer asStringIndex();
}
