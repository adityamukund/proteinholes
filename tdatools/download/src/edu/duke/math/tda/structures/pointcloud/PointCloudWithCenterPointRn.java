/*
 * Created Mar 28, 2013
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

import java.util.ArrayList;
import edu.duke.math.tda.structures.PointRn;

/**
 * Point cloud in Rn, with added center point. 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Mar 28, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class PointCloudWithCenterPointRn extends PointCloud {

	protected final PointRn centerPoint_;

	protected int numberOfPoints_; // TODO: replace by checking array size, unless used 
	// for cross-checking?
	protected ArrayList<PointRn> points_ = new ArrayList<PointRn>();
	
	public PointCloudWithCenterPointRn( 
			final String _directory, 
			final String _fileName ) throws Exception {
		
		super( _directory, _fileName );
		
		double[][] dblLoadedPoints;
		PointRn pointToAdd;
		
		// Note: all set-up is done within the load method
		dblLoadedPoints = loadPointCloudFromFile();

		centerPoint_ = new PointRn(	dblLoadedPoints[ 0 ] );
		
		this.points_ = new ArrayList<PointRn>( points_ );
		// Now create the array of points from the remaining loaded data:
		for ( int j=1; j<dblLoadedPoints.length; j++ ) {
			
			pointToAdd = new PointRn( dblLoadedPoints[ j ] );
			this.points_.add( pointToAdd );

		}

		this.numberOfPoints_ = this.points_.size();	
	}
	
	public PointRn getCenterPoint() {
		
		return this.centerPoint_;
	}
	    
	public int getNumberOfPoints() {
		
		return points_.size();
	}

	public PointRn getPoint( int index_ ) {
		
		return points_.get( index_ );
	}
}
