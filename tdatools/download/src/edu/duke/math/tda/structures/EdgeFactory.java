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

import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaErrorHandler;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * EdgeFactory class 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 30, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class EdgeFactory {
	
	// actually: may simply use the Zp-value!!
	private int edgeType_ = -1;
	private int EDGETYPEZ2_ = 1;
	private int EDGETYPEZP_ = 2;
	
	Settings processData_;

	public EdgeFactory( final Settings _processData ) {

		TdaErrorHandler errorHandler = new TdaErrorHandler();
		
    	try {

    	    this.processData_ = _processData;
    	    
    		// Validate the required settings
    		boolean isDataValid = validateRequiredData();
    		
    		// TODO: may simply want to default to Z2??
    		// We check if there were any problems. If we found any, we cannot continue
    		// setting up.
    		if ( !isDataValid ) {
    		    
    		    throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER, 
    		            processData_.compileErrorMessages().toString() );
    		}
    		

    	    int Zp_value = 0;
    		
    	    // Note: any algorithm that uses the Zp parameter will have validated
    	    // this setting already, so there's no need to validate here
    		String strZpValue = processData_.getValidatedProcessParameter(
    	            TDA.SETTING_ZP_VALUE );

    		if ( !strZpValue.equalsIgnoreCase( TDA.DATA_SETTINGNOTFOUND ) ) {
    		
    			try {
    			
    				Zp_value = Integer.parseInt( strZpValue );
    			}
    			catch ( Exception e ) {
    				
    				// really nothing to do, since default Zp-value is already set to 0
    			}
    		}
    		
    		if ( Zp_value > 0 ) {

    			edgeType_ = EDGETYPEZP_;
    		}
    		else {
    			
    			edgeType_ = EDGETYPEZ2_;
    		}
		}
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
	}

	// hjs 12/16/2014 Make EdgeFactory available for some testing, by defaulting to Z2
	public EdgeFactory() {

		edgeType_ = EDGETYPEZ2_;
	}
	
	public EdgeI createEdge( final int _vertexIndex1, 
			final int _vertexIndex2 ) {
		
		EdgeI edgeToCreate;
		
		if ( edgeType_ == EDGETYPEZ2_ ) {
		
			edgeToCreate = new EdgeZ2( _vertexIndex1, _vertexIndex2 );
		}
		else {
			

			edgeToCreate = new EdgeZp( _vertexIndex1, _vertexIndex2 );
		}
		
		return edgeToCreate;
	}
 
	public EdgeI createEdge( final int _vertexIndex1, 
			final int _vertexIndex2, 
			final double _distance  ) {
		
		EdgeI edgeToCreate;
		
		if ( edgeType_ == EDGETYPEZ2_ ) {

			edgeToCreate = new EdgeZ2( _vertexIndex1, _vertexIndex2, _distance );
		}
		else {
			

			edgeToCreate = new EdgeZp( _vertexIndex1, _vertexIndex2, _distance );
		}
				
		return edgeToCreate;
	}

	public EdgeI createEdge( final int _vertexIndex1, 
			final int _vertexIndex2, 
			final double _distance,
			final boolean _edgeType,
			final int _dagType  ) {
		
		EdgeI edgeToCreate;
		
		if ( edgeType_ == EDGETYPEZ2_ ) {

			edgeToCreate = new EdgeZ2( 
					_vertexIndex1, _vertexIndex2, _distance, _edgeType, _dagType );
		}
		else {
			

			edgeToCreate = new EdgeZp( 
					_vertexIndex1, _vertexIndex2, _distance, _edgeType, _dagType );
		}
		
		return edgeToCreate;
	}

	public EdgeI createEdge( final EdgeI _orig ) {
		
		EdgeI edgeToCreate;
		
		if ( edgeType_ == EDGETYPEZ2_ ) {

			edgeToCreate = new EdgeZ2( ( EdgeZ2 ) _orig );
		}
		else {

			edgeToCreate = new EdgeZp( ( EdgeZp ) _orig );
		}
				
		return edgeToCreate;
	}

	protected boolean validateRequiredData() throws Exception {

	    boolean isDataValid = true;

	    
	    return isDataValid;
	}
}
