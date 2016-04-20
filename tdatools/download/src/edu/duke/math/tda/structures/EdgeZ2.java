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

import java.util.*;

/**
 * Edge implementation class EdgeZ2
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

public class EdgeZ2 extends Edge {

    public EdgeZ2( final EdgeZ2 _orig ) {

    	super ( _orig );
    	
    	// copy the dagList
    	Integer key;
    	Integer value;
		for( SortedMap.Entry<Integer,Integer> dagListItem : _orig.getDagList().entrySet() ) {
			  
			key = dagListItem.getKey();
			value = dagListItem.getValue();
			dagList_.put( key, value );
		}
    }

    public EdgeZ2( final int _vertexIndex1, 
    			final int _vertexIndex2, 
    			final double _distance ) {

    	super( _vertexIndex1, _vertexIndex2, _distance );
    	
    	this.uPositive_ = true;
    	this.dagType_ = -1;
    }
    
    public EdgeZ2( final int _vertexIndex1, 
			final int _vertexIndex2 ) {
    	
    	super( _vertexIndex1, _vertexIndex2 );
	}
    
    public EdgeZ2( final int _vertexIndex1, 
    			final int _vertexIndex2, 
    			final double _distance,
    			final boolean _edgeType,
    			final int _dagType ) {
    	
    	super( _vertexIndex1, _vertexIndex2, _distance, _edgeType, _dagType );
    }
    
    public SortedMap<Integer, Integer> getDagList() {
    	
    	return dagList_;
    }
    
    // Note: need to use Integer instead of int, so that we add a proper 'object'
    public void addToDagList( final Integer _index, final Integer _index2 ) {
    	
    	if ( _index != null && _index.intValue() >= 0 ) {
    	
    		dagList_.put( _index, _index2 );
    	}
    	else {
    		
    		// throw exception?
    	}
    }

    public void mergeIntoDagList( 
    		final SortedMap<Integer, Integer> _dagListToMerge, 
    		final int _edgeOrientation ) {

    	SortedMap<Integer, Integer> itemsToAdd = new TreeMap<Integer, Integer>();
    	SortedMap<Integer, Integer> itemsToRemove = new TreeMap<Integer, Integer>();

    	Integer key;
		Integer value;
    	
    	// if daglist is empty, just add all items in the toMerge list
    	if ( dagList_.size() == 0 ) {
    		
    		for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
    			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    			dagList_.put(key, value);
    		}
    	}
    	
	    // here both lists are non-empty
	    else {    	
		    if ( _dagListToMerge != null && _dagListToMerge.size() > 0 ) {
		    	
		    	for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
	    			  
	    			key = dagListItem.getKey();
	    			value = dagListItem.getValue();
	    			
	    			if ( dagList_.containsKey( key ) ) {
		    			
		    			// since any adding is done 'modulo 2', we need to remove the item (from both lists)
		    			itemsToRemove.put( key, value );
		    		}
		    		else {
		    			
		    			itemsToAdd.put( key, value );
		    		}
	    		}
		    }
		    else {
		    	
		    	// may or may NOT want to throw an exception?
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToRemove.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dagList_.remove( key );
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToAdd.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dagList_.put( key, value );
		    }
	    }
    }

    // experimental..
    // For the Z/Zp computation, we need to include the orientation of the edge, leading
    // to either adding or subtracting the dag list
    public void mergeIntoDagList_actualZp_stuff( 
    		final SortedMap<Integer, Integer> _dagListToMerge, 
    		final int _edgeOrientation ) {

    	Integer key;
		Integer value;
		Integer prevValue;
		Integer tmpValue;
    	
    	// if daglist is empty, just add all items in the toMerge list
    	if ( dagList_.size() == 0 ) {
    		
    		for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
    			  
    			key = dagListItem.getKey();
    			value = _edgeOrientation * dagListItem.getValue();
    			dagList_.put(key, value);
    		}
    	}
    	
	    // here both lists are non-empty
	    else {    	
		    if ( _dagListToMerge != null && _dagListToMerge.size() > 0 ) {
		    	
		    	for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
	    			  
	    			key = dagListItem.getKey();
	    			value = _edgeOrientation * dagListItem.getValue();
	    			
	    			if ( dagList_.containsKey( key ) ) {
		    			
	    				prevValue = dagList_.get( key );
	    				dagList_.remove( key );
	    				tmpValue = ( prevValue + value )%5;
	    				// update only if not 0
	    				if ( tmpValue != 0 ) {
	    				
	    					dagList_.put( key, tmpValue );
	    				}
		    		}
		    		else {
		    			
		    			// add the merged item
		    			dagList_.put( key, value );
		    		}
	    		}
		    }
		    else {
		    	
		    	// may or may NOT want to throw an exception?
		    }
	    }
    }
    
    public void mergeIntoDagList( final SortedMap<Integer, Integer> _dagListToMerge ) {

    	SortedMap<Integer, Integer> itemsToAdd = new TreeMap<Integer, Integer>();
    	SortedMap<Integer, Integer> itemsToRemove = new TreeMap<Integer, Integer>();

    	Integer key;
		Integer value;
    	
    	// if daglist is empty, just add all items in the toMerge list
    	if ( dagList_.size() == 0 ) {
    		
    		for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
    			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    			dagList_.put(key, value);
    		}
    	}
    	
	    // here both lists are non-empty
	    else {    	
		    if ( _dagListToMerge != null && _dagListToMerge.size() > 0 ) {
		    	
		    	for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
	    			  
	    			key = dagListItem.getKey();
	    			value = dagListItem.getValue();
	    			
	    			if ( dagList_.containsKey( key ) ) {
		    			
		    			// since any adding is done 'modulo 2', we need to remove the item (from both lists)
		    			itemsToRemove.put( key, value );
		    		}
		    		else {
		    			
		    			itemsToAdd.put( key, value );
		    		}
	    		}
		    }
		    else {
		    	
		    	// may or may NOT want to throw an exception?
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToRemove.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dagList_.remove( key );
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToAdd.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dagList_.put( key, value );
		    }
	    }
	}
    
    public String toString() {

    	return this.asStringComplete().toString();
    }

    // util only
    public String printArrayList( final TreeMap<Integer, Integer> _L ) {

		StringBuffer arrayListAsString = new StringBuffer( "") ;//"ArrayList = " );
		
		for ( Integer key : _L.keySet() ) {

			arrayListAsString.append( _L.get( key ).toString() );
		}
    	
    	return arrayListAsString.toString();
    }
    
    public StringBuffer asString() {
		
		StringBuffer edgeAsString = new StringBuffer( "( edge_i=" );
		edgeAsString.append( Integer.toString( this.edgeListIndex_ ) );
		edgeAsString.append( " )" );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringOrig() {
		
		StringBuffer edgeAsString = new StringBuffer( "( " );
		edgeAsString.append( Integer.toString( this.vertexIndex1_+ this.minIndexAdjustment_ ) );
		edgeAsString.append( ", " );		
		edgeAsString.append( Integer.toString( this.vertexIndex2_+ this.minIndexAdjustment_ ) );
		edgeAsString.append( " )" );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringIndex() {
		
		StringBuffer edgeAsString = new StringBuffer();
		edgeAsString.append( Integer.toString( this.edgeListIndex_ ) );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringComplete() {
		
		StringBuffer edgeAsString = new StringBuffer( "( eIdx=" );
		edgeAsString.append( Integer.toString( this.edgeListIndex_ ) );
		edgeAsString.append( ", xPos=" );
		edgeAsString.append( Boolean.toString( this.xPositive_ ) );
		edgeAsString.append( ", uPos=" );
		edgeAsString.append( Boolean.toString( this.uPositive_ ) );
//		edgeAsString.append( ", pPos=" );
//		edgeAsString.append( Boolean.toString( this.pPositive_ ) );
//		edgeAsString.append( ", ufvIdx=" );
//		if ( this.unionFindVertexIndex_ != -1 ) {
//		
//			edgeAsString.append( Integer.toString( getUnionFindVertexIndexForDisplay() ) );
//		}
//		else {
//		
//			edgeAsString.append( Integer.toString( this.unionFindVertexIndex_ ) );
//		}
//		edgeAsString.append( ", " );		
//		edgeAsString.append( Integer.toString( this.dagList_.size() ) );
		edgeAsString.append( ", " );		
		edgeAsString.append( "{" );		
		edgeAsString.append( this.printArrayList( 
				(TreeMap<Integer, Integer>) this.dagList_ ) );
		edgeAsString.append( "}" );
		edgeAsString.append( ", L=" );		
		edgeAsString.append( Double.toString( this.edgeLength_ ) );
//		edgeAsString.append( " ), vIdxAdj=" );
//		edgeAsString.append( Integer.toString( this.minIndexAdjustment_ ) );		
		edgeAsString.append( ", (v1,v2)=( " );
		edgeAsString.append( Integer.toString( getVertexIndexForDisplay1() ) );
		edgeAsString.append( ", " );
		edgeAsString.append( Integer.toString( getVertexIndexForDisplay2() ) );
		edgeAsString.append( " )" );
		edgeAsString.append( " )" );
		
		return edgeAsString;
    }
    
    public StringBuffer asStringCompleteOrig() {
		
		StringBuffer edgeAsString = new StringBuffer( "( " );
		edgeAsString.append( Integer.toString( this.vertexIndex1_ ) );
		edgeAsString.append( ", " );		
		edgeAsString.append( Integer.toString( this.vertexIndex2_ ) );
		edgeAsString.append( ", " );		
		edgeAsString.append( Boolean.toString( this.uPositive_ ) );
//		edgeAsString.append( ", " );		
//		edgeAsString.append( Integer.toString( this.dagList_.size() ) );
		edgeAsString.append( ", " );		
		edgeAsString.append( "{" );		
		edgeAsString.append( this.printArrayList( 
				(TreeMap<Integer, Integer>) this.dagList_ ) );
		edgeAsString.append( "}" );
		edgeAsString.append( ", " );		
		edgeAsString.append( Double.toString( this.edgeLength_ ) );
		edgeAsString.append( " )" );
		
		return edgeAsString;
    }
}
