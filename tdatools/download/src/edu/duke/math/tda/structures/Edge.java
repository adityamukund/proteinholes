/*
 * Created Jul 30, 2013
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
import java.util.TreeMap;

/**
 * Documents 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 30, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public abstract class Edge implements EdgeI  {

	// hjs 10/27/13
	// Note: this variable can be used for tracking the adjustment of the minimum
	// index (where the user supplied data that doesn't start at index 0). 
	// Currently, we translate such data (only relevant for the sparse matrix case)
	// to start at index 0, and display all subsequent results with the adjusted
	// indexes. I.e., we don't translate any [vertex] indexes back to their original
	// "as supplied" values.
	protected static int minIndexAdjustment_;
	
    protected int vertexIndex1_ = -1;
    protected int vertexIndex2_ = -1;
    protected double edgeLength_ = -1;
    protected boolean uPositive_ = true;
    // added for M23 (may be able to use uPos or pPos)?
    protected boolean xPositive_ = true;
    protected int unionFindVertexIndex_ = -1;

	protected boolean pPositive_ = true; // used for finding representatives
    protected int dagType_ = -1; //  special to algorithm (actually deprec.)

    // this index allows us to quickly find the location of this edge 
    // in the edgeList that it is part of
    protected int edgeListIndex_ = -1;

    protected SortedMap<Integer, Integer> dagList_ = new TreeMap<Integer, Integer>();


    public Edge( final Edge _orig ) {

    	// deep copy of all properties
    	this.vertexIndex1_ = _orig.getVertexIndex1();
    	this.vertexIndex2_ = _orig.getVertexIndex2();
    	this.edgeLength_ = _orig.getEdgeLength();
    	this.uPositive_ = _orig.isUPositive();
    	this.dagType_ = _orig.getDagType();
    	
    	// this can be tricky?
    	this.edgeListIndex_ = _orig.getEdgeListIndex();
    }
    
    public Edge( final int _vertexIndex1, 
			final int _vertexIndex2 ) {
	
		this.vertexIndex1_ = _vertexIndex1;
		this.vertexIndex2_ = _vertexIndex2;
	}

    public Edge( final int _vertexIndex1, 
    			final int _vertexIndex2, 
    			final double _distance ) {
    	
    	this.vertexIndex1_ = _vertexIndex1;
    	this.vertexIndex2_ = _vertexIndex2;
    	this.edgeLength_ = _distance; 
    	this.uPositive_ = true;
    	this.dagType_ = -1;
    }

    public Edge( final int _vertexIndex1, 
    			final int _vertexIndex2, 
    			final double _distance,
    			final boolean _edgeType,
    			final int _dagType ) {
    	
    	this.vertexIndex1_ = _vertexIndex1;
    	this.vertexIndex2_ = _vertexIndex2;
    	this.edgeLength_ = _distance;
    	this.uPositive_ = _edgeType;
    	this.dagType_ = _dagType;
    }
    
    
    // Getter methods.
    public int getVertexIndex1() {
    	
    	return this.vertexIndex1_;
    }
    
    public int getVertexIndex2() {
    	
    	return this.vertexIndex2_;
    }
    
    public int getUnionFindVertexIndex() {
    	
		return unionFindVertexIndex_;
	}
    
    
    // Handle the display of a vertex that has been adjusted for non-zero
    // starting index
    public int getVertexIndexForDisplay1() {

    	return this.vertexIndex1_;
//    	return this.vertexIndex1_ + this.minIndexAdjustment_;
    }
    
    public int getVertexIndexForDisplay2() {

    	return this.vertexIndex2_;
//    	return this.vertexIndex2_ + this.minIndexAdjustment_;
    }
    
    public int getUnionFindVertexIndexForDisplay() {
    	
		return unionFindVertexIndex_;
//		return unionFindVertexIndex_ + this.minIndexAdjustment_;
	}

	public void setUnionFindVertexIndex( int _unionFindVertexIndex ) {
		
		this.unionFindVertexIndex_ = _unionFindVertexIndex;
	}

    public boolean hasVertex( int _vertexToCheck ) {
    	
    	return ( this.vertexIndex1_ == _vertexToCheck ) ? true:
    				( this.vertexIndex2_ == _vertexToCheck ) ? true: false;
    }
    
    public double getEdgeLength() {
    	
    	return this.edgeLength_;
    }
    
    public boolean isUPositive() {
    	
    	return this.uPositive_;
    }
    
    public boolean isPPositive() {
    	
    	return this.pPositive_;
    }
    
    public boolean isXPositive() {
    	
    	return this.xPositive_;
    }
    
    public int getDagType() {
    	
    	return this.dagType_;
    }

    public StringBuffer asString() {

    	return new StringBuffer( "" );
    }
    public StringBuffer asStringComplete() {

    	return new StringBuffer( "" );
    }
    public StringBuffer asStringIndex() {
    	
    	return new StringBuffer( "" );
    }    
    
    public int getEdgeListIndex() {
    	
    	return edgeListIndex_;
    }

    
    // setter methods
    
    public void setEdgeListIndex( final int _edgeListIndex ) {
    	
    	this.edgeListIndex_  = _edgeListIndex;
    }
    
    public void setDistance( final double _distance ) {
    	
    	this.edgeLength_ = _distance;
    }
    
    public void setUPositive( final boolean _uPositive ) {
    	
    	this.uPositive_ = _uPositive;
    }
    
    public void setPPositive( final boolean _pPositive ) {
    	
    	this.pPositive_ = _pPositive;
    }
    
    public void setXPositive( final boolean _xPositive ) {
    	
    	this.xPositive_ = _xPositive;
    }
    
    public void setDagType( final int _dagType ) {
    	
    	this.dagType_ = _dagType;
    }
    
    public int getOtherVertex( final int _vertexIndex ) {
    	
    	if ( _vertexIndex == this.vertexIndex1_ ) {
    		
    		return this.vertexIndex2_;
    	}
    	else {
    	
    		return this.vertexIndex1_;
    	}
    }

    /*
     * Compare 2 edges: they are equal exactly when they are the same class AND
     * their vertex indices match
     * 
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object _otherObj ) {
		
        // Note that as long as we don't have specific data as part of neither the mandatory 
        // nor the optional setting item, we can have this method as part of the SettingItem
        // base class.
        // In addition, by comparing items within the base class, we don't allow mandatory
        // and optional items associated with the same name, which would likely cause unexpected
        // behaviour.
		
		// Quick check for identity
		if (this == _otherObj) return true;
		
		// Need to be an actual object
		if (_otherObj == null) return false;
		
		// and need to be of the same class
		if (_otherObj.getClass() != this.getClass() ) return false;
		
		Edge otherEdge = (Edge) _otherObj;
		
		// Match if the vertex indices are the same	
		if ( otherEdge.getVertexIndex1() == this.getVertexIndex1() 
				&& ( otherEdge.getVertexIndex2() == this.getVertexIndex2() ) ) {
		    
			return true;
		}
		    
	    return false;
    }
    
    public int compareTo( final EdgeI _edge ) {

    	double diff = this.getEdgeLength() - _edge.getEdgeLength();
    	
    	return ( diff < 0 ) ? -1:( diff > 0 ) ? 1: 0;
    }
}
