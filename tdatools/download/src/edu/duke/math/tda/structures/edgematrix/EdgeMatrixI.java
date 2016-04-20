/*
 * Created January 2013
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

import edu.duke.math.tda.structures.EdgeI;

/**
 * EdgeMatrixI is an interface for the EdgeMatrix hierarchy
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created January 2013
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public interface EdgeMatrixI {

	public abstract EdgeI getEdge( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;

	public abstract int getEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;
	
	public abstract double getEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;
	
	public abstract void setEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2,
							final int _edgeIndex ) throws Exception;
	
	public abstract void setEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2,
							final double _edgeLength ) throws Exception;

	public abstract int getDimension();
	
	public abstract int getNumberOfConnections( final int _vertexIndex );
}