/*
 * Created Jan 13, 2014
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

package edu.duke.math.tda.structures.edgematrix;

import java.util.SortedMap;
import java.util.TreeMap;

import edu.duke.math.tda.structures.FaceI;

/**
 * FaceTrackerAsSortedMap is an implementation for the FaceTrackerI interface
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created Jan 13, 2014
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class FaceTrackerAsSortedMap implements FaceTrackerI {

	// number of vertices -- used as basis for the key computation for placing
	// faces into this container
	protected final int numberOfVertices_;
	
	// map the (sorted) faces from their vertex (1,2, and 3)-based index 
	// to their sequential number
	protected int[] edgeIndexes_;
	protected SortedMap<Integer, FaceI> sparseFaceList_ = new TreeMap<Integer, FaceI>();
	// not sure if we will actually need this
	protected SortedMap<Integer, Integer> sparseFaceIndexes_ = new TreeMap<Integer, Integer>();

	// temp util variables
	private int key;
	private FaceI face;

	// utility vars: declare once
	private int tmpV1, tmpV2, tmpV3;
	
	// Initialize the map with the number of vertices in the underlying data
	public FaceTrackerAsSortedMap( final int _numberOfVertices ) {
		
		numberOfVertices_ = _numberOfVertices; 
	}

	@Override
	public FaceI getFace( final int _vertexIndex1,
			final int _vertexIndex2,
			final int _vertexIndex3 ) throws Exception {

		key = computeKey( _vertexIndex1, _vertexIndex2, _vertexIndex3 );
		
		if ( sparseFaceList_.containsKey( key ) ) {
		
			return sparseFaceList_.get( key );
		}
		else {
			
			return null;
		}
	}


	@Override
	public int getFaceIndex( final int _vertexIndex1,
			final int _vertexIndex2,
			final int _vertexIndex3 ) throws Exception {

	    int faceIndexToReturn = -1;
	    
//    	System.out.println( "\nGetting faceIndex F3( " + _vertexIndex1 + ", " +
//    			_vertexIndex2 + ", " +
//    			_vertexIndex3 + " )"
//    			);
    	
		// for easier debugging
		key = computeKey( _vertexIndex1, _vertexIndex2, _vertexIndex3 );
//		System.out.println( "key = " + key );
//		System.out.println( this );

		if ( sparseFaceList_.containsKey( key ) ) {
			
			face = sparseFaceList_.get( key );
			faceIndexToReturn = face.getFaceListIndex();

//			System.out.println( "\nface found: index= " + faceIndexToReturn + 
//					", face= " + face );
		}
		
		return faceIndexToReturn;

//		return sparseFaceList_.get( 
//				computeKey( _vertexIndex1, _vertexIndex2, _vertexIndex3 ) )
//					.getFaceListIndex();
	}

	@Override
	public void addFace( final int _vertexIndex1, 
			final int _vertexIndex2,
			final int _vertexIndex3, 
			final FaceI _face ) throws Exception {

		// compute the key
		key = computeKey( _vertexIndex1, _vertexIndex2, _vertexIndex3 );

//		System.out.println( "\nFor key = " + key + ", add face = " + _face );

		
		// add the key-faceListIndex pair to the faceIndex values
		if ( sparseFaceIndexes_.containsKey( key ) ) {
			
			// throw an exception, because we already have this face in our set
		}
		else {
			
			// add the key-face pair to the faceList
			sparseFaceList_.put( key, _face );
		
			// and the list of indexes
			sparseFaceIndexes_.put( key, _face.getFaceListIndex() );
		}
		
//		System.out.println( "after adding the face:\n" + this );
	}
	
	// compute a single value to be used for the lookup, based on the values
	// of the 3 vertices that make up the face
	protected int computeKey( final int _vertexIndex1, 
			final int _vertexIndex2,
			final int _vertexIndex3 ) {
		
		
		// To make the lookup "safe", we need to make sure that the vertices are ordered,
		// else we likely end up with either multiple entries, or failed lookups
		// (use: ascending)
		
		// Make sure that the vertices are ordered:
		if ( _vertexIndex1 < _vertexIndex2 ) {
			
			if ( _vertexIndex2 < _vertexIndex3 ) {

				tmpV1 = _vertexIndex1;
				tmpV2 = _vertexIndex2;
				tmpV3 = _vertexIndex3;				
			}
			else if ( _vertexIndex3 < _vertexIndex1 ) {

				tmpV1 = _vertexIndex3;
				tmpV2 = _vertexIndex1;
				tmpV3 = _vertexIndex2;			
			}
			else {

				tmpV1 = _vertexIndex1;
				tmpV2 = _vertexIndex3;
				tmpV3 = _vertexIndex2;			
			}
		}
		else { // v1 > v2
			
			if ( _vertexIndex2 > _vertexIndex3 ) {

				tmpV1 = _vertexIndex3;
				tmpV2 = _vertexIndex2;	
				tmpV3 = _vertexIndex1;			
			}
			else if ( _vertexIndex3 > _vertexIndex1 ) {

				tmpV1 = _vertexIndex2;
				tmpV2 = _vertexIndex1;
				tmpV3 = _vertexIndex3;			
			}
			else {

				tmpV1 = _vertexIndex1;
				tmpV2 = _vertexIndex3;
				tmpV3 = _vertexIndex2;			
			}
		}
		
		// simple computation to ensure uniqueness, but:
		// TODO: may yield too large int's for large data
		return _vertexIndex1 * numberOfVertices_ * numberOfVertices_ + 
				_vertexIndex2 * numberOfVertices_ + 
				_vertexIndex3;
	}

	@Override
	public int getFaceCount() {

		return sparseFaceList_.size();
	}

	public String toString() {
		
		StringBuffer faceList = new StringBuffer( "\nFaceList:" );
		
		for ( int i=0; i<this.sparseFaceList_.size(); i++ ) {
			
			faceList.append( "\n   i=" + i + ", face=" + sparseFaceList_.get( i ) );
		}
		
		return faceList.toString();
	}
}
