/*
 * Created Jan 10, 2014
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
 * FaceFactory class 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jan 10, 2014
 * <p>
 * 4/18/2014 hjs	Add 'switch' between Z2 and Zp handling
 * 
 * <p>
 *  
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class FaceFactory {
	
	private int faceListIndex_ = 0;
	
	private int faceType = -1;
	// Listing of actual valid face types
	private int FACETYPE_Z2_ = 1;
	private int FACETYPE_Zp_ = 2;
	
	Settings processData_;

	public FaceFactory( final Settings _processData ) {

		TdaErrorHandler errorHandler = new TdaErrorHandler();
		
    	try {

    	    this.processData_ = _processData;
    	    
    		// Validate the required settings
    		boolean isDataValid = validateRequiredData();

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

        		faceType = FACETYPE_Zp_;
    		}
    		else {

        		faceType = FACETYPE_Z2_;
    		}
		}
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
	}

	// hjs 1/9/2015
	public FaceI createFace( final EdgeI _currentEdge, 
			final int _vertexIndex3 ) {
		
		FaceI faceToCreate;
		
		if ( faceType == FACETYPE_Z2_ ) {
		
			faceToCreate = new FaceZ2( 
					_currentEdge,
					_vertexIndex3, 
					getCurrentFaceIndex()  );
		}
		else {

			faceToCreate = new FaceZp( 
					_currentEdge,
					_vertexIndex3, 
					getCurrentFaceIndex() );
		}
		
		return faceToCreate;
	}

	// keep only for legacy test code
	// Note that the faceFactory only requires the vertices, as it tracks the value
	// of the faceListIndex, and inserts the value
	public FaceI createFace( final int _vertexIndex1, 
			final int _vertexIndex2, 
			final int _vertexIndex3 ) {
		
		FaceI faceToCreate;
		
		if ( faceType == FACETYPE_Z2_ ) {
		
			faceToCreate = new FaceZ2( 
					_vertexIndex1, 
					_vertexIndex2,
					_vertexIndex3, 
					getCurrentFaceIndex()  );
		}
		else {

			faceToCreate = new FaceZp( 
					_vertexIndex1, 
					_vertexIndex2,
					_vertexIndex3, 
					getCurrentFaceIndex() );
		}
		
		return faceToCreate;
	}
	
	public FaceI createFace( final FaceI _orig ) {
		
		FaceI faceToCreate;
		
		if ( faceType == FACETYPE_Z2_ ) {
		
			faceToCreate = new FaceZ2( (FaceZ2) _orig );
		}
		else {			
			
			faceToCreate = new FaceZp( (FaceZ2) _orig );
		}
		
		return faceToCreate;
	}
	
	// wrapper around the faceListIndex_: this is the only place where we change it,
	// because it is a "global" counter for all the faces we create with this factory.
	private int getCurrentFaceIndex() {
		
		int currentFaceIndex = this.faceListIndex_;

		// increment the faceListIndex
		this.faceListIndex_++;
		
		return currentFaceIndex;
	}

	protected boolean validateRequiredData() throws Exception {

	    boolean isDataValid = true;

	    
	    return isDataValid;
	}
	
	protected int getFaceListIndex() {
		
		return faceListIndex_;
	}
}
