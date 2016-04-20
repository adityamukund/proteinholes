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

import java.util.regex.Pattern;

import edu.duke.math.tda.utility.TDA;

/**
 * Point class for representing points in cartesian space R^n
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * Once a point is created, its dimension is fixed; however, its coordinates can be changed.
 *  
 * <p><strong>Change History:</strong> <br>
 * Created October 2012
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class PointRn implements PointI {

	final protected double[] coordinates_;
	    
    public PointRn( final PointRn _orig ) {
    	    	
    	// deep copy
    	int dimension = _orig.coordinates_.length;
    	coordinates_ = new double[ dimension ];
    	for ( int i=0; i< coordinates_.length; i++ ) {
    		
    		coordinates_[ i ] = _orig.getCoordinates()[ i ];
    	}
    }
    
    public PointRn( final double[] _coords ) {

    	// deep copy
    	int dimension = _coords.length;
    	coordinates_ = new double[ dimension ];
    	for ( int i=0; i< coordinates_.length; i++ ) {
    		
    		coordinates_[ i ] = _coords[ i ];
    	}
    }
    
    // hjs 11/14/2013 Add constructor for creating PointRn from a string
    // (of its coordinate values)0
    public PointRn( final String _pointAsString ) {
    	

        // Set the pattern for the data parsing
        // Note we allow comments at the end of a line (via Pattern.COMMENTS)
        Pattern pattern = Pattern.compile( 
                TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );

	    double dblCoordValue;
	    String coordsAsString;
        int pointDimension;

        coordsAsString = _pointAsString.trim();
	    String[] pointCoords = pattern.split( coordsAsString );
	    pointDimension = pointCoords.length;
    	coordinates_ = new double[ pointDimension ];

    	for ( int i=0; i<pointDimension; i++ ) {

            coordinates_[i] = Double.parseDouble( pointCoords[ i ] );
    	}
	}
    
    public double[] getCoordinates() {
    	
    	return coordinates_;
    }
    
    public int getDimension() {
    	
    	return coordinates_.length;
    }
    
    public PointRn scalarMultiply( final double _factor ) {
    	
    	PointRn newPoint = new PointRn( this );

    	for ( int i=0; i< newPoint.coordinates_.length; i++ ) {
			
    		newPoint.coordinates_[ i ] = newPoint.coordinates_[ i ] * _factor;
		}
    	
    	return newPoint;
    }
    
    // THIS call is dangerous: want to deprecate:
//    public boolean hasSameCoordinates( final PointRn _pointToCompareTo ) {
//    	
//    	boolean hasSameCoords = true;
//    	
//    	for ( int i=0; i< this.coordinates_.length; i++ ) {
//			
//    		if ( this.coordinates_[ i ] != _pointToCompareTo.coordinates_[ i ] ) {
//    		
//    			hasSameCoords = false;
//    			i = this.coordinates_.length;
//    		}
//		}
//    	
//    	return hasSameCoords;
//    }
    
    public boolean hasSameCoordinates( final PointRn _pointToCompareTo,
    		final double _epsBound ) {
    	
    	boolean hasSameCoords = true;
    	double eps = Math.abs( _epsBound );
    	
    	for ( int i=0; i<this.coordinates_.length; i++ ) {
			
    		if ( Math.abs( this.coordinates_[ i ] - 
    				_pointToCompareTo.coordinates_[ i ] ) > eps ) {
    		
    			hasSameCoords = false;
    			i = this.coordinates_.length;
    		}
		}
    	
    	return hasSameCoords;
    }
    
    // Apply a coordinate-wise translation of the original point by the supplied point
    public PointRn translate( final PointRn _translationPoint ) {
    	
    	PointRn newPoint = new PointRn( this );

    	for ( int i=0; i< newPoint.coordinates_.length; i++ ) {
			
    		newPoint.coordinates_[ i ] = newPoint.coordinates_[ i ] 
    		                             - _translationPoint.coordinates_[ i ];
		}
    	
    	return newPoint;
    }
    
    public double dotProduct( final PointRn _otherPoint ) {
    	
    	double[] coordinates = new double[ this.getDimension() ];
    	double sum = 0;
    	

    	for ( int i=0; i< this.getDimension(); i++ ) {
			
    		sum += this.coordinates_[ i ] * _otherPoint.coordinates_[ i ];
		}
    	
    	return sum;
    }
    
    // Setter methods
    public void setCoordinates( final double[] _coords ) {
    	
    	// Note: point is defined and has unchangeable dimension
    	
    	for ( int i=0; i< coordinates_.length; i++ ) {
    	
    		coordinates_[ i ] = _coords[ i ];
    	}
    }
    
    // Basic string representation
	public String toString() {
		
		return this.asString().toString();
	}
    
    // Formatted string representation
	public StringBuffer asString() {
		
		StringBuffer pointAsString = new StringBuffer( "( " );
		
		for ( int i=0; i< coordinates_.length-1; i++ ) {
			
			pointAsString.append( Double.toString( coordinates_[ i ]) );
			pointAsString.append( ", " );
		}
		
		pointAsString.append( Double.toString( coordinates_[ coordinates_.length-1 ]) );
		pointAsString.append( " )" );
		
		return pointAsString;
	}
    
    // Basic string representation
	public StringBuffer asStringBasic() {
		
		StringBuffer pointAsString = new StringBuffer( "" );
		
		for ( int i=0; i< coordinates_.length-1; i++ ) {
			
			pointAsString.append( Double.toString( coordinates_[ i ]) );
			pointAsString.append( ", " );
		}
		
		pointAsString.append( Double.toString( coordinates_[ coordinates_.length-1 ]) );
		
		return pointAsString;
	}
    
    // Basic string representation, but TAB (white-space) delimited (for point cloud output)
	public StringBuffer asStringBasicTab() {
		
		StringBuffer pointAsString = new StringBuffer( "" );
		
		for ( int i=0; i< coordinates_.length-1; i++ ) {
			
			pointAsString.append( Double.toString( coordinates_[ i ]) );
			pointAsString.append( "\t" );
		}
		
		pointAsString.append( Double.toString( coordinates_[ coordinates_.length-1 ]) );
		
		return pointAsString;
	}
}
