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

package edu.duke.math.tda.structures.metric;

import edu.duke.math.tda.structures.PointI;

/**
 * Interface for Metric classes
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

public interface MetricI {

	public abstract double computeDistance( 
			final PointI _p1, final PointI _p2 ) throws Exception;

	public abstract double compute( 
			final PointI _p ) throws Exception;
}
