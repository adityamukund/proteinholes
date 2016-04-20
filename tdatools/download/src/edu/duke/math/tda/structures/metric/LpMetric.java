/*
 * Created Mar 1, 2013
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
import edu.duke.math.tda.structures.PointRn;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * 
 * Subclass of Metric class, implementing the Lp-metric
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Mar 1, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class LpMetric extends Metric {

	final protected double power_;
	
	public LpMetric( final double _power ) throws Exception {
		
		if ( _power < 1 ) {
			
			// Note that we throw a DEV (instead of USERINPUT exception,
			// because the power value needs to have been validated way
			// before this constructor is ever called
            throw new TdaException( 
            		TDA.ERROR_APP_DEV, 
                    "(LpMetric constructor) " +
                    "Need to supply a p-value greater or equal than one." );
        }
		
		this.power_ = _power;
	}
	
	@Override
	public double computeDistance( 
			final PointI _p1, final PointI _p2 ) throws Exception {

		PointRn p1;
		PointRn p2;

		// Implement for each specific class that implements the metricI interface, and
		// for which we can compute the distance
		if ( StringUtil.getClassName( _p1 ).equals( "PointRn" )
				&& StringUtil.getClassName( _p2 ).equals( "PointRn" ) ) {

			// it's valid to cast to PointRn, so we compute the distance
			p1 = ( PointRn ) _p1;
			p2 = ( PointRn ) _p2;
			
			double distance = 0;
			int dimension1 = p1.getDimension();
			int dimension2 = p2.getDimension();
			
			if ( dimension1 != dimension2 ) {
				
				throw new TdaException( 
				        TDA.ERROR_APP_USERINPUT, 
                        "(L2metric.computeDistance) " +
				        "Two provided points in Rn, p1 (=" + p1.toString() +
                        ") and p2 (= " + p2.toString() + ") " +
				        "have different dimension." );
			}
		
			for ( int i=0; i< dimension1; i++ ) {
				
				distance += Math.pow( Math.abs( 
						p1.getCoordinates()[ i ] - p2.getCoordinates()[ i ] ), this.power_ );
			}

			return Math.pow( distance, 1/power_ );
		}
		else {
			
			// throw an exception
			throw new TdaException( 
			        TDA.ERROR_APP_DEV, 
                    "(L2metric.computeDistance) " +
			        "Two provided points p1 and p2 cannot be cast to specific classes " +
			        "for which we can compute the Lp distance." );
		}
	}

	@Override
	public double compute( 
			final PointI _p ) throws Exception {

		PointRn p;

		// Implement for each specific class that implements the metricI interface, and
		// for which we can compute the distance
		if ( StringUtil.getClassName( _p ).equals( "PointRn" ) ) {

			// it's valid to cast to PointRn, so we compute the distance
			p = ( PointRn ) _p;
			
			double distance = 0;
			int dimension = p.getDimension();
		
			for ( int i=0; i< dimension; i++ ) {
				
				distance += Math.pow( Math.abs( p.getCoordinates()[ i ] ), this.power_ );
			}
			
			return Math.pow( distance, 1/power_ );
		}
		else {
			
			// throw an exception
			throw new TdaException( 
			        TDA.ERROR_APP_DEV, 
                    "(L2metric.computeDistance) " +
			        "Two provided point p cannot be cast to specific classes " +
			        "for which we can compute the Lp distance." );
		}
	}
}
