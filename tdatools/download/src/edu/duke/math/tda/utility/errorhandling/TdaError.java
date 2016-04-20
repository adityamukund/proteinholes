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

/**
 * Used for tracking multiple internal "errors", e.g., while trying to validate
 * the user input.
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class TdaError {

    private String errorMessageText;
    private final int errorType;
    private String settingName;
    private Object errorInfo;
 
    //  
    public TdaError( String _errorMessageText, 
            int _errorType,
            String _settingName,
            Object _errorInfo ) {

        errorType = _errorType;
        
        if ( _errorMessageText != null ) {
            
            errorMessageText = new String( _errorMessageText );
        }
        
        if ( _settingName != null ) {
            
            settingName = new String( _settingName );
        }
        
        if ( _errorInfo != null ) {
        
            if ( _errorInfo instanceof String ) {
                
                errorInfo = new String( (String) _errorInfo );
            }
            else {
            
                errorInfo = _errorInfo;
        	}
        }
    }
    
    public TdaError( TdaError _TdaError ) {

        this ( _TdaError.errorMessageText, 
                _TdaError.errorType,
                _TdaError.settingName, 
                (Object) _TdaError.errorInfo );
    }
    
    /**
     * @return Returns the errorMessageText.
     */
    public String getErrorMessageText() {
        return errorMessageText;
    }
    /**
     * @return Returns the errorType.
     */
    public int getErrorType() {
        return errorType;
    }
    /**
     * @return Returns the errorInfo.
     */
    public Object getErrorInfo() {
        return errorInfo;
    }
    /**
     * @return Returns the settingName.
     */
    public String getSettingName() {
        return settingName;
    }
}
