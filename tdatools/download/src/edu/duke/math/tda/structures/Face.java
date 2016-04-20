/*
 * Created April 18, 2014
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 * License Info:
 * 
 * 
 */package edu.duke.math.tda.structures;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Abstract base class for Face hierarchy
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created April 18, 2014
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public abstract class Face implements FaceI {

	protected int arrVertices_[];
	protected int faceListIndex_ = -1;
	protected boolean fPositive = true;

	// may not be used
	protected EdgeI arrEdges_[];
	
	// dag list for the face (need to decide whether to store actual faces or their
	// indexes
	protected SortedMap<Integer, Integer> dag2List_ = new TreeMap<Integer, Integer>();
		
	@Override
	public int compareTo( final FaceI _faceToCompareTo ) {
		
		// It's sufficient to check that the vertices are the same:
		if ( _faceToCompareTo.hasVertex( arrVertices_[ 0 ] ) && 
				_faceToCompareTo.hasVertex( arrVertices_[ 0 ] ) && 
				_faceToCompareTo.hasVertex( arrVertices_[ 0 ] ) ) {
			
			return 1;
		}
		else {
			
			return 0;
		}
	}
	
	@Override
	public int getVertexIndex( final int _i ) {
		
		return this.arrVertices_[ _i ];
	}

	@Override
	public boolean hasVertex( final int _vertexToCheck ) {
		
		if ( _vertexToCheck == this.arrVertices_[ 0 ] || 
				_vertexToCheck == this.arrVertices_[ 1 ] || 
				_vertexToCheck == this.arrVertices_[ 2 ] ) {

			return true;
		}
		else {
		
			return false;
		}
	}

	@Override
	public boolean hasEdge( final EdgeI _edgeToCheck ) {

		if ( arrEdges_[ 0 ].equals( _edgeToCheck ) || 
				arrEdges_[ 1 ].equals( _edgeToCheck ) || 
				arrEdges_[ 2 ].equals( _edgeToCheck ) ) {
			
			return true;
		}
		else {
		
			return false;
		}
	}

	@Override
	public int getEdgeIndex( final int _i ) {
		
		if ( _i<0 || _i>3 ) {
			
			// TODO: throw an exception to make sure devs are not messing up
		}
		
		return arrEdges_[ _i ].getEdgeListIndex();
	}

	@Override
	public SortedMap<Integer, Integer> getDagList() {
		
		return dag2List_;
	}

	@Override
	public void addToDagList( final Integer edgeIndex1, final Integer edgeIndex2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mergeIntoDagList( final SortedMap<Integer, Integer> _dagListToMerge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mergeIntoDagList( final SortedMap<Integer, Integer> _dagListToMerge,
			final int _edgeOrientation) {
		// TODO Auto-generated method stub

	}
	@Override
	public int getFaceListIndex() {
		
		return faceListIndex_;
	}

	@Override
	public void setFaceListIndex( final int _faceListIndex ) {

		faceListIndex_ = _faceListIndex;
	}

	@Override
	public int[] getOtherVertices( final int _vertexIndex ) {
		
		int[] arrOtherVertices = new int[ 2 ];
		
		if ( _vertexIndex == arrVertices_[ 0 ] ) {

			arrOtherVertices[ 0 ] = arrVertices_[ 1 ];
			arrOtherVertices[ 1 ] = arrVertices_[ 2 ];
		}
		else if ( _vertexIndex == arrVertices_[ 1 ] ) {

			arrOtherVertices[ 0 ] = arrVertices_[ 0 ];
			arrOtherVertices[ 1 ] = arrVertices_[ 2 ];
			
		}
		else if ( _vertexIndex == arrVertices_[ 2 ] ) {

			arrOtherVertices[ 0 ] = arrVertices_[ 0 ];
			arrOtherVertices[ 1 ] = arrVertices_[ 1 ];
			
		}
		else {
			
			// throw an exception?
			// otherwise the array values will be both 0 (and the consumer
			// would have to check -- not desirable)
		}
		
		return arrOtherVertices;
	}

	@Override
	public int[] getIndexesForOtherEdges( final EdgeI _edge ) {
		
		int[] arrOtherEdgeListIndices = new int[ 2 ];
		int _edgeListIndex = _edge.getEdgeListIndex();
		
		if ( _edgeListIndex == arrEdges_[ 0 ].getEdgeListIndex() ) {

			arrOtherEdgeListIndices[ 0 ] = arrEdges_[ 1 ].getEdgeListIndex();
			arrOtherEdgeListIndices[ 1 ] = arrEdges_[ 2 ].getEdgeListIndex();
		}
		else if ( _edgeListIndex == arrEdges_[ 1 ].getEdgeListIndex() ) {

			arrOtherEdgeListIndices[ 0 ] = arrEdges_[ 0 ].getEdgeListIndex();
			arrOtherEdgeListIndices[ 1 ] = arrEdges_[ 2 ].getEdgeListIndex();
			
		}
		else if ( _edgeListIndex == arrEdges_[ 2 ].getEdgeListIndex() ) {

			arrOtherEdgeListIndices[ 0 ] = arrEdges_[ 0 ].getEdgeListIndex();
			arrOtherEdgeListIndices[ 1 ] = arrEdges_[ 1 ].getEdgeListIndex();
			
		}
		else {
			
			// throw an exception?
			// otherwise the array values will be both 0 (and the consumer
			// would have to check -- not desirable)
		}
		
		return arrOtherEdgeListIndices;
	}

	@Override
	public EdgeI[] getOtherEdges( final EdgeI _edge ) {
		
		EdgeI[] arrOtherEdges = new EdgeI[ 2 ];
		
		// may need to modify the '==', because this is comparing actual
		// object (not 'edge' in math sense) equality
		if ( _edge == arrEdges_[ 0 ] ) {

			arrOtherEdges[ 0 ] = arrEdges_[ 1 ];
			arrOtherEdges[ 1 ] = arrEdges_[ 2 ];
		}
		else if ( _edge == arrEdges_[ 1 ] ) {

			arrOtherEdges[ 0 ] = arrEdges_[ 0 ];
			arrOtherEdges[ 1 ] = arrEdges_[ 2 ];
			
		}
		else if ( _edge == arrEdges_[ 2 ] ) {

			arrOtherEdges[ 0 ] = arrEdges_[ 0 ];
			arrOtherEdges[ 1 ] = arrEdges_[ 1 ];
			
		}
		else {
			
			// throw an exception?
			// otherwise the array values will be both null
		}
		
		return arrOtherEdges;
	}
	
	public String toString() {

		return asStringComplete().toString();
//		return asString().toString();
	}

	@Override
	public StringBuffer asString() {
		
		StringBuffer faceAsString = new StringBuffer();
		
		faceAsString.append( "Face i="  + this.faceListIndex_ + 
				", v1=" + this.arrVertices_[ 0 ] +
				", v2=" + this.arrVertices_[ 1 ] +
				", v3=" + this.arrVertices_[ 2 ] +
				", isPos=" + this.fPositive
				);
		
		return faceAsString;
	}

	@Override
	public StringBuffer asStringComplete() {
		
		StringBuffer faceAsString = new StringBuffer();
		
		faceAsString.append( "Face i="  + this.faceListIndex_ + 
				", v1=" + this.arrVertices_[ 0 ] +
				", v2=" + this.arrVertices_[ 1 ] +
				", v3=" + this.arrVertices_[ 2 ] +
				", isPos=" + this.fPositive + 
				", dag2List of edges = "
				);
		
		if ( dag2List_ != null && this.dag2List_.size() > 0 ) {
		
				faceAsString.append( dag2List_.toString() );
		}
		else {
			
			faceAsString.append( "--empty--" );
		}
		
		// for easierto-read debugging statements
		faceAsString.append( "\n" );
		
		return faceAsString;
	}

	@Override
	public StringBuffer asStringIndex() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isfPositive() {
		
		return fPositive;
	}

	public void setfPositive( boolean fPositive ) {
		
		this.fPositive = fPositive;
	}
}
