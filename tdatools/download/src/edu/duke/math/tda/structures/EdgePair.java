/*
 * Created October 2012
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

import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * EdgePair class for representing pairs of edges that share a common vertex.
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created October 2012
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class EdgePair {

	protected final EdgeI edge1_;
	protected final EdgeI edge2_;
	protected final int commonVertexIndex_;
	
	protected SortedMap<Integer, Integer> orientationForEdge_ = 
		new TreeMap<Integer, Integer>();
	
	public EdgePair( final EdgeI _edge1, 
					 final EdgeI _edge2, 
					 final int _commomVertexIndex ) throws Exception {
		
		this.edge1_ = _edge1;
		this.edge2_ = _edge2;
		this.commonVertexIndex_ = _commomVertexIndex;
		
		// Make sure that _commomVertexIndex is indeed a common vertex:
		if ( !_edge1.hasVertex( _commomVertexIndex ) || !_edge2.hasVertex( _commomVertexIndex ) ) {

            throw new TdaException( 
                    TDA.ERROR_APP_DEV,
                    "(EdgePair constructor) " +
                    "DEV: Attempting to create an edgePair with specified common vertex " +
                    "that is not shared between the edges: edge1='" + _edge1.asString() +
                    "', edge2='" + _edge2.asString() + "', commonVertex='" + _commomVertexIndex + "'." );
		}
	}
	
	// special constructor for specifying 2 edges only (which must share a common vertex, though,
	// otherwise we throw an exception)
	public EdgePair( final EdgeI _edge1, 
			 final EdgeI _edge2 ) throws Exception {

		this.edge1_ = _edge1;
		this.edge2_ = _edge2;
		
		if ( _edge1.getVertexIndex1() == _edge2.getVertexIndex1() 
				|| _edge1.getVertexIndex1() == _edge2.getVertexIndex2() ) {
			
			this.commonVertexIndex_ = _edge1.getVertexIndex1();	
		}
		else if ( _edge1.getVertexIndex1() == _edge2.getVertexIndex1() 
				|| _edge1.getVertexIndex1() == _edge2.getVertexIndex2() ) {

			this.commonVertexIndex_ = _edge1.getVertexIndex1();	
		}
		else {

			this.commonVertexIndex_ = -1;
			
			// Throw Exception: no common vertex!
            throw new TdaException( 
                    TDA.ERROR_APP_DEV,
                    "(EdgePair constructor) " +
                    "DEV: Attempting to create an edgePair from 2 edges that do not share " +
                    "a common vertex: edge1='" + _edge1.asString() +
                    "', edge2='" + _edge2.asString() + "'." );
		}
	}

	public EdgeI getEdge1() {
		
		return this.edge1_;
	}

	public EdgeI getEdge2() {
		
		return this.edge2_;
	}
	
	// Note: since we refer to the edges that are part of an edgePair as edge '1' and '2', 
	// we shift the supplied index, so the 'user' of the code can refer to them as '1' and '2'
	// (instead of 0 and 1).
	public int getEdgeOrientation( int _edgeIndex ) throws Exception {
		
		return orientationForEdge_.get( _edgeIndex );
	}
	
	public void setEdgeOrientation( final int _edgeIndex, 
									final int _orientation ) throws Exception {
				 
		if ( orientationForEdge_.containsKey( _edgeIndex ) ) {
			
			// remove any existing entry with the same key (there shouldn't be any, but 
			// can't take a chance)
			orientationForEdge_.remove( _edgeIndex );
		}
		
		orientationForEdge_.put( _edgeIndex, _orientation );
	}

	public int getCommonVertexIndex() {
		
		return this.commonVertexIndex_;
	}

    public String toString() {
    	
    	return asString().toString();
    }
    
	// output the edge index instead:
    public StringBuffer asString() {
		
		StringBuffer edgeAsString = new StringBuffer( "EdgePair = " );
		edgeAsString.append( this.edge1_.asStringIndex() );
		edgeAsString.append( ", " );		
		edgeAsString.append(this.edge2_.asStringIndex() );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringOrig() {
		
		StringBuffer edgeAsString = new StringBuffer( "EdgePair = " );
		edgeAsString.append( this.edge1_.asString() );
		edgeAsString.append( ", " );		
		edgeAsString.append( this.edge2_.asString() );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringComplete() {
		
		StringBuffer edgeAsString = new StringBuffer( "EdgePair = " );
		edgeAsString.append( this.edge1_.asStringComplete() );
		edgeAsString.append( ", 	" );		
		edgeAsString.append( this.edge2_.asStringComplete() );
		
		return edgeAsString;
    }    
}
