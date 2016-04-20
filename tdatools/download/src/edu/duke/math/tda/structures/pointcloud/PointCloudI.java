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

package edu.duke.math.tda.structures.pointcloud;

import java.util.*;

import edu.duke.math.tda.structures.PointRn;

/**
 * PointCloudI is an interface for the PointCloud hierarchy
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

public interface PointCloudI {

	public abstract ArrayList<PointRn> getPoints() throws Exception;
	
	
}
