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

import java.util.List;
import java.util.SortedMap;

/**
 * Documents the interface for the Edge implementations
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 30, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public interface EdgeI extends Comparable<EdgeI> {

    // getter and setter methods
	
    public abstract int getVertexIndex1();
    
    public abstract int getVertexIndex2();
    
    public abstract boolean hasVertex( final int _vertexToCheck );
    
    public abstract double getEdgeLength();
    
    public abstract boolean isUPositive();
    
    public abstract boolean isPPositive();
    
    public abstract boolean isXPositive();
    
    public abstract int getUnionFindVertexIndex();

	public abstract void setUnionFindVertexIndex( final int _unionFindVertexIndex );

    public abstract int getDagType();

    public abstract SortedMap<Integer, Integer> getDagList();
    public abstract void addToDagList( final Integer edgeIndex1, 
    		final Integer edgeIndex2 );
    public abstract void mergeIntoDagList( 
    		final SortedMap<Integer, Integer> _dagListToMerge );
    public abstract void mergeIntoDagList( 
    		final SortedMap<Integer, Integer> _dagListToMerge,
    		final int _edgeOrientation );
    
    public abstract int getEdgeListIndex();
    
    public abstract void setEdgeListIndex( final int _edgeListIndex );
    
    public abstract void setDistance( final double _distance );
    
    public abstract void setUPositive( final boolean _uPositive );
    
    public abstract void setPPositive( final boolean _pPositive );
    
    public abstract void setXPositive( final boolean _xPositive );
    
    public abstract void setDagType( final int _dagType );
    
    public abstract int getOtherVertex( final int _vertexIndex );

    public abstract StringBuffer asString();
    public abstract StringBuffer asStringComplete();
    public abstract StringBuffer asStringIndex();
}
