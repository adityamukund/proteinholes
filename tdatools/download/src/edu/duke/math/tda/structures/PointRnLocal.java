/*
 * Created February 2013
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

/**
 * {Point in Rn, with local distance info} class
 * 
 * <p><strong>Details:</strong> <br>
 * Based on the standard Point in Rn ("PointRn") class, this subclass
 * stores the distance to a reference point (unknown to this class, at
 * this time). By providing the compareTo method (to implement the
 * Comparable interface), based on this distance, we are able to easily
 * construct sorted collections of objects of this class.
 *  
 * <p><strong>Change History:</strong> <br>
 * Created February 2013
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class PointRnLocal extends PointRn implements Comparable<PointRnLocal> {

	protected double distanceToReferencePoint_;
	
	public PointRnLocal( final PointRnLocal _orig ){
    	
		super( _orig );
		
		this.distanceToReferencePoint_ = _orig.distanceToReferencePoint_;
    }
	
	public PointRnLocal( final PointRn _point, final double _distanceToReferencePoint ){
    	
		super( _point );
		
		this.distanceToReferencePoint_ = _distanceToReferencePoint;
    }
	
	void setDistanceToReferencePoint( double _distanceToReferencePoint ) {
		
		this.distanceToReferencePoint_ = _distanceToReferencePoint;
	}
	
	public double getDistanceToReferencePoint() {
		
		return this.distanceToReferencePoint_;
	}

	@Override
	public int compareTo( PointRnLocal _otherPoint ) {
		
		double diff = this.distanceToReferencePoint_ - 
						_otherPoint.distanceToReferencePoint_;
    	
    	return ( diff < 0 ) ? -1:( diff > 0 ) ? 1: 0;
	}
}
