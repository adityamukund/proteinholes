/*
 * Created 2012
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */
package edu.duke.math.tda.utility.errorhandling;

import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Combines the exception handling for various front-end classes.
 *
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * <p>
 * 2005/10/20 (v2.0) hjs	Add more detail to the error messages.
 * 
 * <p>
 * hjs (v2.1)               Add condition around validateData() call, to avoid errors
 *                          due to bootstrap timing.
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class TdaErrorHandler {
        
    protected Settings processData_;
    protected StringBuffer additionalInfo_ = new StringBuffer( TDA.BUFFERLENGTH_STAT );

    public TdaErrorHandler() {
        // nothing to do
    };
    
    public TdaErrorHandler( final Settings _processData ) {
        
        processData_ = _processData;
        
        try {
        
            if ( processData_ != null ) {
            
                validateRequiredData();
            }
        }
        catch ( Exception ex ) {
            
            System.out.println( "[TdaErrorHandler] Could not validate " +
                "the required data for setting up the error handler." +
                "\nThe TDA developers apologize for this problem." +
                "\nPlease record all information pertinent to this error " +
                "and contact the TDA developers. " +
                "\nThank you for your cooperation.\n\n" +
                "Original error message:\n" +
                ex.getMessage() + "\n\n" 
                );
            
            if ( TDA.DEBUG || 
                    processData_.getSettingItemValueAsValidated( 
                            TDA.SETTING_DISPLAYDEBUGINFO ).equals( 
                                    TDA.UI_DEBUGINFO_STACKTRACE ) ) 
                ex.printStackTrace();
        }
    }
    
	/**
	 * Validates the settings values for the TdaErrorHandler.
	 * 
	 * @return Returns the boolean flag that indicates whether a crucial setting
	 * could not be validated.
	 */
	private boolean validateRequiredData() throws Exception {
	    
	    boolean isDataValid = true;

	    // utility variables for validating
	    String settingNameCanonical;
	    String settingNameDescriptive;
	    String settingNameForDisplay;
        String settingDataType;
		SettingItem settingItem;
		int validationType;
		
	    // Validate
	    settingNameCanonical = TDA.SETTING_DISPLAYDEBUGINFO;
	    settingNameDescriptive = TDA.SETTING_DISPLAYDEBUGINFO_DESCR;
	    settingNameForDisplay = TDA.SETTING_DISPLAYDEBUGINFO_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    settingItem = processData_.processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validChoices(), 
	            TDA.SETTING_DEFAULT_DEBUGINFO );

	    return isDataValid;
	}

    /**
     * Provides the valid choices for validation relevant to this class
     */
    public Object validChoices() {
	    
		Set<String> validValues = new HashSet<String>();
		
	    validValues.add( TDA.UI_DEBUGINFO_STACKTRACE );
	    validValues.add( TDA.UI_DEBUGINFO_NONE );
	    
	    return validValues;
	}
    
    /**
     * Process any encountered TDA exception  <br>
     *  
     * @param e The TdaException to process.
     */
    public void handleApplicationException( final TdaException e ) {
        
        StringBuffer errorMessage = new StringBuffer( TDA.BUFFERLENGTH_STAT );
        String inEclipseStr;
        
//        try {
//            errorMessage.append( processData.getOptionalThreadInfo() );
//        }
//        catch ( Exception ex ) {
//            
//            // Swallow any exception
//        } 
        
        try {
            
	        String strMessage;
			//DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
			DateFormat timeFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, 
			        DateFormat.MEDIUM );
			String timeStamp = timeFormat.format(new Date());
			
			if ( processData_ != null ) {
				
				inEclipseStr = processData_.getValidatedProcessParameter( 
	            		TDA.SETTING_RUNNINGINECLIPSE );
			}
			else {
				
				processData_ = new Settings();
				inEclipseStr = TDA.UI_RUNNINGINECLIPSE_NO;
			}
	

	        int lineLength = TDA.FEEDBACK_LINELENGTH;
            
            errorMessage.append( composeErrorSignature () );
		    
		    int exceptionType = e.getExceptionType();
		    
		    // Note: for exceptions where the associated message provides exhaustive
		    // information, a separate case is listed. Generally, such errors can likely
		    // be "fixed" by the user by correcting some input value, etc.
		    // All other errors are combined in the default case, since they will likely
		    // require developer intervention.
		    
		    switch ( exceptionType )
		    {	    	    
	    	    		    
	    	case TDA.ERROR_APP_DEV:
	    	    
	    	    errorMessage.append( 
	    	            "[Development-related error: This message may be " +
	    	            "generated to remind the TDA developers to complete " +
	    	            "or restructure a section of code] " +
                        "\nError details -- " );
                
                strMessage = e.getMessage();
                if ( strMessage == null || strMessage.length() == 0 ) {
        
                    errorMessage.append(
                        "If possible, please notify the developer that " +
                        "TDA provided the following info: \n" +
                        "  'exceptionType=' " + exceptionType );
                }
                else {
                	
                    errorMessage.append(
                        "If possible, please notify the developer that " +
                        "TDA provided the following info: \n" );
                    errorMessage.append( e.getMessage() );
                }
		    
	    	    break;
	    	    
    		    
	    	case TDA.ERROR_APP_USERINPUT:
	    	    
	    	    errorMessage.append( 
	    	            "[Input-related error] Details: " );
                
                strMessage = e.getMessage();
                if ( strMessage == null || strMessage.length() == 0 ) {
        
                    errorMessage.append(
                        "For some reason, the code that generated the error message did not " +
                        "supply eny description of what caused the problem, so we cannot provide " +
                        "much insight. " +
                        "If possible, please notify the developer that " +
                        "TDA provided the following info: \n" +
                        "  'exceptionType=' " + exceptionType );
                }
                else {
//                    errorMessage.append(
//                        "If possible, please notify the developer that " +
//                        "TDA provided the following info: \n" );
                    errorMessage.append( e.getMessage() );
                }
		    
	    	    break;
	    	    		    	  
	    	default: 
	    	    
		    	strMessage = e.getMessage();
				if ( strMessage == null || strMessage.length() == 0 ) {
		
					errorMessage.append(
                        "The following info [possibly relevant only to a developer]" +
                        " is provided to assist in trouble-shooting: \n" +
                        "  'exceptionType=' " + exceptionType );
				}
				else {
				    errorMessage.append(
                        "The following info is provided to assist in trouble-shooting: \n");
				    errorMessage.append( e.getMessage() );
				}
    	        	
		       	break;
		    }

            errorMessage.append( TDA.FEEDBACK_NEWLINE );
            errorMessage.append( processData_.compileWarningMessages() );  

            if ( inEclipseStr.equalsIgnoreCase( TDA.UI_RUNNINGINECLIPSE_YES ) ) {
            	
	            errorMessage.append( TDA.FEEDBACK_NEWLINE );
	            errorMessage.append( "\nStack trace info:" );
	            errorMessage.append( TDA.FEEDBACK_NEWLINE );
	            errorMessage.append( displayBasicStackTrace( e ) );
            }
            
            errorMessage.append( TDA.FEEDBACK_NEWLINE );
            errorMessage.append( additionalInfo_ );
            errorMessage.append( composeErrorClosing() );      
            
		    // Record the error message to the error log file
		    try {
                
		        processData_.recordError( errorMessage.toString() );
		    }
	        catch ( Exception ex ) {
	            
	            System.out.println( "[TdaErrorHandler][1] " +
	            		"Oops! For some reason we were not able to properly " + 
	            		"record an error that we encountered while running TDA." +
	            		"\nThe TDA developers apologize for this problem." +
	            		"\nPlease record all information pertinent to this error " +
	            		"and contact the TDA developers. " +
	            		"\nThank you very much for your help!\n\n" +
                        "Original error message:\n" +
                        errorMessage.toString() +
                        "\n[Exception.getMessage]: \n" +
                        ex.getMessage() + "\n\n"
                      );
	        }
	    }
        catch ( Exception ex ) {
            
            System.out.println( "[TdaErrorHandler][2] " +
            		"Oops! This is embarrassing! " +
            		"TDA encountered an error while trying to process " +
	        		"an error that was encountered while running Tda." +
	        		"\nThe TDA developers apologize for this problem." +
	        		"\nPlease record all information pertinent to this error " +
	        		"and contact the TDA developers. " +
	        		"\nThank you very much for your help!\n\n" +
	                "Original error message:\n" +
	                errorMessage.toString() +
                    "\n[Exception.getMessage]: \n" +
                    ex.getMessage() + "\n\n"
              );
            
			if ( TDA.DEBUG || 
			        processData_.getSettingItemValueAsValidated( 
			                TDA.SETTING_DISPLAYDEBUGINFO ).equals( 
			                        TDA.UI_DEBUGINFO_STACKTRACE ) ) 
			    e.printStackTrace();
        }
    }
    
    /**
     * Process any encountered Tda exception  <br>
     *  
     * @param e The TdaException to process.
     * @param _additionalInfo Additional info, e.g., the settings for the search.
     */
    public void handleApplicationException( 
            final TdaException e, final Object _additionalInfo ) {
        
        if ( _additionalInfo instanceof Settings ){
            
            processData_ = ( Settings ) _additionalInfo;
            
            if ( TDA.DEBUG ){
                
                additionalInfo_.append( "\n\n" );
                additionalInfo_.append( _additionalInfo.toString() );
            }
        }

        handleApplicationException( e );
    }

    /**
     * Process any unexpected exception  <br>
     *  
     * @param _exception The TdaException to process.
     */
    public void handleGeneralException( final Exception _exception ) {

        StringBuffer errorMessage = new StringBuffer( TDA.BUFFERLENGTH_STAT );

        String strRunningInEclipse = processData_.getValidatedProcessParameter( 
        		TDA.SETTING_RUNNINGINECLIPSE );
        
//        try {
//            errorMessage.append( processData.getOptionalThreadInfo() );
//        }
//        catch ( Exception e ) {
//            
//            // Swallow any exception
//        }
        
        errorMessage.append( composeErrorSignature () );
        
        errorMessage.append( 
                "Execution has stopped due to the following exception: \n'" +
                _exception.toString() + "'\n" );
        
        errorMessage.append( additionalInfo_ );

//        if ( strRunningInEclipse.equalsIgnoreCase( TDA.UI_RUNNINGINECLIPSE_YES ) ) {
        	
            errorMessage.append( "\nStack trace info:\n" );
            errorMessage.append( displayBasicStackTrace( _exception ) );
//        }        

        errorMessage.append( composeErrorClosing() );

        try {
            
            processData_.recordError( errorMessage.toString() );
        }
        catch ( Exception ex ) {
            
            System.out.println( "[TdaErrorHandler.handleGeneralException] " +
            		"Oops! This is embarrassing!" +
            		"\nIt looks like TDA encountered an unexpected error, " +
                    "one that the code was not able to handle using its internal " +
                    "exception handling. This console message may be all that we " +
                    "can provide (though there may be [duplicate] information added " +
                    "to an error file named 'error.txt' in the directory " +
                    "where TDA was executed)." +
                    "\nThe TDA developers apologize for this problem!" +
                    "\nIf posssible, please record all information pertinent " +
                    "to this error, and forward it to the TDA developers " +
                    "(for the up-to-date address, please see the user guide). " +
                    "\nThank you very much for your help!\n" +
                    "\nOriginal TDA error message:\n" +
                    errorMessage.toString() +
                    "\n[Exception.getMessage]: \n" +
                    ex.getMessage() + "\n\n" );
        }
    }

    /**
     * Process any unexpected exception  <br>
     *  
     * @param _exception The TdaException to process.
     * @param _additionalInfo Additional info, e.g., the settings for the search.
     */
    public void handleGeneralException( final Exception _exception, 
            final Object _additionalInfo ) {

 		if ( TDA.DEBUG && _additionalInfo instanceof Settings ){
            
            additionalInfo_.append( _additionalInfo.toString() );
		}

        handleGeneralException( _exception );
   }

    /**
     * Process any out-of-memory errors  <br>
     *  
     * @param _exception The TdaException to process.
     * @param _additionalInfo Additional info, e.g., the settings for the search.
     */

    public void handleOutOfMemoryError( final OutOfMemoryError _exception, 
            final Object _additionalInfo ) {

        StringBuffer errorMessage = new StringBuffer( TDA.BUFFERLENGTH_STAT );
        errorMessage.append( composeErrorSignature () );
        
        errorMessage.append(
                "Execution has stopped because TDA has run out of available memory. \n'" +
                _exception.toString() + "'\n" );
        
        if ( TDA.DEBUG && _additionalInfo instanceof Settings ){
            
            errorMessage.append( _additionalInfo.toString() );
        }
        
        errorMessage.append( composeErrorClosing() );

        try {
            
            processData_.recordError( errorMessage.toString() );
        }
        catch ( Exception ex ) {
            
            System.out.println( "[TdaErrorHandler.handleOutOfMemoryError] " +
            		"Oops! This doesn't look good!" +
            		"\nIt seems like TDA ran out of memory, and some or " +
                    "all of its internal information accumulated up to this point " +
                    "may be lost, because we may not be able to save anything " +
                    "to disk. " +
                    "\nThis console message may be all that we can " +
                    "provide (though there may be [duplicate] information added " +
                    "to an error file named 'error.txt' in the directory " +
                    "where TDA was executed)." + 
                    "\nThe TDA developers apologize for this problem." +
                    "\nIf posssible, please record all information pertinent " +
                    "to this error, and forward it to the TDA developers " +
                    "(for the up-to-date address, please see the user guide). " +
                    "\nThank you very much for your help!\n" +
                    "\nOriginal TDA error message:\n" +
                    errorMessage.toString() +
                    "\n[Exception.getMessage]: \n" +
                    ex.getMessage() + "\n\n" );
        }

        if ( TDA.DEBUG || 
                processData_.getSettingItemValueAsValidated( 
                        TDA.SETTING_DISPLAYDEBUGINFO ).equals( 
                                TDA.UI_DEBUGINFO_STACKTRACE ) ) 
            _exception.printStackTrace();
    }
    
    public StringBuffer composeErrorSignature() {

        //DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        DateFormat timeFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, 
                DateFormat.MEDIUM );
        String timeStamp = timeFormat.format(new Date());

        StringBuffer errorMessage = new StringBuffer( TDA.BUFFERLENGTH_STAT );
        StringBuffer optionalThreadInfo = new StringBuffer();

        try {
            optionalThreadInfo.append( processData_.getOptionalThreadInfo() );
        }
        catch ( Exception ex ) {
            
            // Swallow any exception
        } 
        
        int lineLength = TDA.FEEDBACK_LINELENGTH;
        
        errorMessage.append( 
                TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));
        errorMessage.append( TDA.FEEDBACK_NEWLINE );
        // When there's no thread info, we need to adjust the blanks before the
        // text that follows
        if ( optionalThreadInfo.length() > 0 ) {
        
            errorMessage.append( optionalThreadInfo );
        }
        else {
            
            errorMessage.append( "          " );
        }
        errorMessage.append( "                       ERROR DETAILS" );
        errorMessage.append( 
                TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));

        // Start with the TDA header
        errorMessage.append( StringUtil.getTdaSignature() );
        errorMessage.append( TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));
        
        // Add the "job" info
        // hjs 5/19/2014: omit for Matlab api  mode
//        if ( processData != null ) {
//        
//            errorMessage.append( StringUtil.getJobSignature( processData ) );
//            errorMessage.append( TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));
//        }
        
        // Add the error info
        errorMessage.append( "\n\n" );
        errorMessage.append( "[" );
        errorMessage.append( "ERROR: " );
        errorMessage.append( TDA.APPLICATION_NAME );
        errorMessage.append( " " );
        errorMessage.append( TDA.APPLICATION_VERSIONNUMBER );
        errorMessage.append( ", " );
        errorMessage.append( timeStamp );
        errorMessage.append( "]" );
        errorMessage.append( "\n" );
        
        return errorMessage;
    }
    
    public StringBuffer composeErrorClosing() {

        StringBuffer errorMessage = new StringBuffer( TDA.BUFFERLENGTH_STAT );
        int lineLength = TDA.FEEDBACK_LINELENGTH;
        StringBuffer optionalThreadInfo = new StringBuffer();

        try {
            optionalThreadInfo.append( processData_.getOptionalThreadInfo() );
        }
        catch ( Exception ex ) {
            
            // Swallow any exception
        } 
        
        errorMessage.append( 
                TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));
        errorMessage.append( TDA.FEEDBACK_NEWLINE );
        // When there's no thread info, we need to adjust the blanks before the
        // text that follows
        if ( optionalThreadInfo.length() > 0 ) {
        
            errorMessage.append( optionalThreadInfo );
        }
        else {
            
            errorMessage.append( "          " );
        }
        errorMessage.append( "                 End of error notification" );
        errorMessage.append( 
                TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.substring( 0, lineLength ));
        errorMessage.append( "\n" );
        
        return errorMessage;
    }
    
    public StringBuffer displayBasicStackTrace( Throwable _exception ) {

        StringBuffer basicStackTrace = new StringBuffer( TDA.BUFFERLENGTH_STAT );

        StackTraceElement[] stackElements = _exception.getStackTrace();
        
        for (int i = 0; i < stackElements.length; i++) {
            
            basicStackTrace.append( TDA.FEEDBACK_NEWLINE );
            basicStackTrace.append( stackElements[i].toString() );
        }
        return basicStackTrace;
    }
}
