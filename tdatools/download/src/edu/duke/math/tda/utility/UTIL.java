/*
 * Created 2013
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */
package edu.duke.math.tda.utility;


/**
 * Basic utility code.
 *
 * <p><strong>Details:</strong> <br>
 * 
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2013
 * <p>
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 * 
 */

public final class UTIL {

    
    // Utility functions for converting arrays to strings
    public static String convertToString( final double[] arrayToConvert )  throws Exception {
    	
    	StringBuffer arrayAsString = new StringBuffer( "" );

    	if ( arrayToConvert != null ) {
    				
			if ( arrayToConvert.length > 1 ) { 
				
		    	for ( int i=0; i<arrayToConvert.length; i++ ) {
		    	
		    		arrayAsString.append( arrayToConvert[ i ] + "\n" );
		    	}
			}
    	}
    	
    	return arrayAsString.toString();
    }
    
    public static String convertToString( final double[][] arrayToConvert ) throws Exception {
    	
    	// Note: we generally expect a rectangular array (matrix) to be passed in,
    	// but we don't enforce it -- passing back a meaningful error message is an issue
    	
    	StringBuffer arrayAsString = new StringBuffer( "" );

    	if ( arrayToConvert != null ) {
    		
			// translate the passed-in array into a string (using tabs as white-space 
			// delimiters, and inserting crlf's) 
			for ( int i=0; i<arrayToConvert.length; i++ ) {
				
				for ( int j=0; j<arrayToConvert[ i ].length; j++ ) {

					arrayAsString.append( arrayToConvert[ i ][ j ] + "\t" );
				}

				arrayAsString.append( "\n" );
			}
    	}
    	
    	return arrayAsString.toString();
    }
}
