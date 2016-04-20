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
package edu.duke.math.tda.structures.metric;

import java.util.HashSet;
import java.util.Set;

import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaError;
import edu.duke.math.tda.utility.errorhandling.TdaErrorHandler;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Documents 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 30, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
// Note: we don't really need a 'factory' for producing a number of metric objects --
// however, the factory lets us encapsulate the metric-related code that the 'outside
// world' doesn't need to know about.
public class MetricFactory {
		
	Settings processData_;
	MetricI metric_;

	public MetricFactory( Settings _processData ) {

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
    		
    	    // Note: any algorithm that uses the Zp parrameter will have validated
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
    		
		}
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
	}
	

	public MetricI getMetric() throws Exception {
				
		String strSettingChoice;
		
		if ( metric_ == null ) {
			
			// determine the metric


			// select the metric
			strSettingChoice = 
				processData_.getValidatedProcessParameter( TDA.SETTING_METRICCHOICE );
			
			if ( strSettingChoice.equalsIgnoreCase( TDA.UI_METRIC_L1 ) ) {
				
				metric_ = new L1Metric();
			}
			else if ( strSettingChoice.equalsIgnoreCase( TDA.UI_METRIC_L2 ) ) {
				
				metric_ = new L2Metric();
			}
			else if ( strSettingChoice.equalsIgnoreCase( TDA.UI_METRIC_LP ) ) {
				
				// get the base p for the Lp metric
				String settingValue = 
					processData_.getValidatedProcessParameter( TDA.SETTING_P_VALUE_FOR_LP );
				
				if ( settingValue.equals( Integer.toString( 
						TDA.APP_NOVALUESUPPLIED_NUMBER ) ) ) {
					
					// throw an exception	            
//		            throw new CompTopoException( COMPTOPO.ERROR_COMPTOPO_USERINPUT,
//		                    "[MetricBasedComputeUnit] Cannot set up the Lp-metric " +
//		                    "without the p-Value >1." );
		            processData_.addToErrors( new TdaError(
		                    "Cannot set up the Lp-metric " +
			                "without the p-Value >=2." ,
			                TDA.ERRORTYPE_INVALIDRANGE,
			                TDA.SETTING_P_VALUE_FOR_LP,
			                StringUtil.getClassName( this ) ) );
		            
		            metric_ = new LpMetric( 2 );
				}
				else {
				
					double pValue = Double.parseDouble( 
						processData_.getValidatedProcessParameter( TDA.SETTING_P_VALUE_FOR_LP ) );
					
					// Check that pValue is valid? Already done in the validation section
					// for this class
					metric_ = new LpMetric( pValue );
				}
			}
			else if ( strSettingChoice.equalsIgnoreCase( TDA.UI_METRIC_LINF ) ) {
				
				metric_ = new LinfMetric();
			}
			else {
				
				// could throw exception, but this will have been handled by
				// the general validation code for the strSettingChoice...
				
				// or default to one of our implemented metrics
				metric_ = new L2Metric();
			}
		}
				
		return metric_;
	}

	protected boolean validateRequiredData() throws Exception {

	    boolean isDataValid = true;

	    String settingNameCanonical;
	    String settingNameDescriptive;
	    String settingNameForDisplay;
        String settingDataType;
		SettingItem settingItem;
		Set<String> validValues = new HashSet<String>();
		int validationType;
        String strCondition;
        final int maxItemsUsed = 4;
        double[] dblValue = new double[maxItemsUsed];
        


        // Validate the choice of metric
        settingNameCanonical = TDA.SETTING_METRICCHOICE;
        settingNameDescriptive = TDA.SETTING_METRICCHOICE_DESCR;
        settingNameForDisplay = TDA.SETTING_METRICCHOICE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validValues.clear();
        validValues.add( TDA.UI_METRIC_L1 );
        validValues.add( TDA.UI_METRIC_L2 );
        validValues.add( TDA.UI_METRIC_LP );
        validValues.add( TDA.UI_METRIC_LINF );
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                validValues,
                TDA.UI_METRIC_L2 );
        
        if ( settingItem.isValidSetting() ) {

            try {

               
            }
            catch ( Exception e ) {

                throw new TdaException( 
                        TDA.ERROR_APP_DEV, settingItem, this );
            }
        }
        else {
            
            isDataValid = false;
        }
        
        
        // Validate the 'p' value of the Lp-metric
        if ( settingItem.getItemValueValidated().
        		equalsIgnoreCase( TDA.UI_METRIC_LP ) ) {
        
        
	        settingNameCanonical = TDA.SETTING_P_VALUE_FOR_LP;
	        settingNameDescriptive = TDA.SETTING_P_VALUE_FOR_LP_DESCR;
	        settingNameForDisplay = TDA.SETTING_P_VALUE_FOR_LP_DISP;
	        settingDataType = TDA.VALIDATION_DATATYPE_INTEGER;
	        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	        settingItem = processData_.processSetting( settingNameCanonical, 
	                settingNameDescriptive,
	                settingNameForDisplay,
	                settingDataType,
	                validationType,
	                null,
	                Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
	        
	        if ( settingItem.isValidSetting() ) {
	
	            try {
	
	                strCondition = new String( "greater or equal than 2" );
	                dblValue[0] = Double.parseDouble( 
	                		processData_.getValidatedProcessParameter(
	                                settingNameCanonical ));
	                if ( dblValue[0] < 2 ) {
	                    
	                	processData_.addToErrors( new TdaError( 
	                            StringUtil.composeErrorMessage( 
	                            		settingItem, 
	                                    strCondition ),
	                            TDA.ERRORTYPE_INVALIDRANGE,
	                            settingNameCanonical,
	                            StringUtil.getClassName( this ) ) );
	                    
	                    isDataValid = false;
	                }
	            }
	            catch ( Exception e ) {
	
//	                throw new TdaException( 
//	                        TDA.ERROR_APP_DEV, settingItem, this );
	            }
	        }
	        else {

            	isDataValid = false;
//	        	throw new TdaException( 
//	                    TDA.ERROR_APP_DEV, settingItem, this );
	        }
        }
	    return isDataValid;
	}
}
