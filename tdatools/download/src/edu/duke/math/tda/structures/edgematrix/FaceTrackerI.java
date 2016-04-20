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

import edu.duke.math.tda.structures.FaceI;

/**
 * FaceTrackerI is an interface for the FaceTracker hierarchy
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

public interface FaceTrackerI {
	
	public abstract FaceI getFace( final int _vertexIndex1, 
			final int _vertexIndex2, 
			final int _vertexIndex3 ) throws Exception;

	public abstract int getFaceIndex( final int _vertexIndex1, 
			final int _vertexIndex2, 
			final int _vertexIndex3 ) throws Exception;
	
	public abstract void addFace( final int _vertexIndex1, 
							final int _vertexIndex2,
							final int _vertexIndex3,
							final FaceI _face ) throws Exception;

	public abstract int getFaceCount();
}