/*
 * Created July 2014
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
package edu.duke.math.tda.algorithm;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.duke.math.tda.structures.*;
import edu.duke.math.tda.structures.edgematrix.DistanceMatrix;
import edu.duke.math.tda.structures.edgematrix.SparseMatrix;
import edu.duke.math.tda.structures.pointcloud.PointCloudInRn;
import edu.duke.math.tda.structures.results.Interval;
import edu.duke.math.tda.structures.results.ResultsCollection;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaError;
import edu.duke.math.tda.utility.errorhandling.TdaErrorHandler;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Algorithm for computing 0-dimensional persistence (RCA-0). 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created July 2014 (based on M12: "M12 without 1-dim.")
 * <p>
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class AlgorithmRCA0 extends Task {

	// Underlying objects
	protected PointCloudInRn pointCloud_;
	protected DistanceMatrix distanceMatrix_;
	protected SparseMatrix sparseMatrix_;
	protected RipsToPersistence edgeList_;
	protected double distanceBound_;               
	protected double radius_;
	protected ArrayList<Interval> intervals_;
	protected ResultsCollection zeroDimIntervals_ = new ResultsCollection();
	protected String strPointCloudFile_ = new String();
	protected String strPointCloudFromString_ = new String();
	protected String strDistanceMatrixFromString_ = new String();
	protected String strSparseMatrixFromString_ = new String();
	protected String strDataLoadedAsArray_ = new String();
	protected String strLocalPointsFile_ = new String();
	protected String strInputDirectory_ = new String();
	protected String strOutputDirectory_ = new String();
	protected String str0DIntervalsFile_ = new String();
	protected String str1DIntervalsFile_ = new String();
	protected String str1DIntervalsAndRepsFile_ = new String();
	protected String strSparseMatrixDataFile_ = new String();
	protected String strDistanceMatrixDataFile_ = new String();

	protected boolean runningInEclipse_;
	protected boolean apiMode_;
	
	public AlgorithmRCA0( Settings _processData ) throws Exception {
		
		super( _processData );
		
		// Validate the required settings
		boolean isDataValid = validateRequiredData();
		
		runningInEclipse_ = processData_.getValidatedProcessParameter( 
				TDA.SETTING_RUNNINGINECLIPSE ).
				equalsIgnoreCase( TDA.UI_RUNNINGINECLIPSE_YES );
		
		apiMode_ = processData_.getValidatedProcessParameter( 
					TDA.SETTING_APPLICATIONMODE ).
					equalsIgnoreCase( TDA.UI_APPLICATIONMODE_API );
		
		// We check if there were any problems. If we found any, we cannot continue
		// setting up.
		if ( !isDataValid ) {
		    
		    throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER, 
		            processData_.compileErrorMessages().toString() );
		}
		
		// Set up the required (subordinate) objects
		setupAlgorithm();
	}
	
	// Compiles various pieces of info about the search, to be used by the recorder
	protected void setupAlgorithm() throws Exception {
			    
		// Set the string buffer for collecting feedback
		algorithmStats_ = new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
		
		// load the data
		loadData();

        if ( processData_.wereThereProblems() ) {
            
            throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER,
                    "(Checkpoint) TDA performed a set of validation checks, " +
                    "and discovered the following issues " +
                    "which prevented further program execution:" + 
                    TDA.FEEDBACK_NEWLINE +
                    processData_.compileErrorMessages().toString() );
        }
	}
	
	protected void loadData() throws Exception {
		   	
    	String strSettingChoice;
    	String strSupplyDataAs;
    	
    	// kludge for latest workflow LPH adaption (original data format supplied via
    	// settings file, but M12 will require the use use of distance matrix input)
    	if ( !processData_.getValidatedProcessParameter( 
    					TDA.SETTING_SUPPLYDATAAS ).equalsIgnoreCase( 
    							TDA.DATA_SETTINGNOTFOUND )) {
	    	
    		processData_.setDynamicProcessParameter( 
	    			TDA.DATA_SUPPLYDATAAS, processData_.getValidatedProcessParameter( 
	    					TDA.SETTING_SUPPLYDATAAS ) );
    	}
        
        	strSettingChoice = processData_.getValidatedProcessParameter(
         			TDA.SETTING_POINTCLOUDFILE );
         	strPointCloudFile_ = strSettingChoice;
	        	
         	strSettingChoice = processData_.getValidatedProcessParameter(
				TDA.SETTING_INPUTDIRECTORY );
         	strInputDirectory_ = strSettingChoice;
         	
        	strSettingChoice = processData_.getValidatedProcessParameter(
        			TDA.SETTING_OUTPUTDIRECTORY );
        	strOutputDirectory_ = strSettingChoice;
         	
        	strSettingChoice = processData_.getValidatedProcessParameter(
        			TDA.SETTING_SPARSEMATRIXFILEOUTPUT );
        	strSparseMatrixDataFile_ = strSettingChoice;
         	
        	strSettingChoice = processData_.getValidatedProcessParameter(
        			TDA.SETTING_DISTANCEMATRIXFILEOUTPUT );
        	strDistanceMatrixDataFile_ = strSettingChoice;
    	
    	// Shared settings
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_DISTANCEBOUND );
    	distanceBound_ = Double.parseDouble(strSettingChoice);
    	
    	// check: used??
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_CENTERPOINTSFORLOCALNBHDFILE );
    	strLocalPointsFile_ = strSettingChoice;
    	
    	
    	
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_0DINTERVALSFILE );
    	str0DIntervalsFile_ = strSettingChoice;
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_1DINTERVALSFILE );
    	str1DIntervalsFile_ = strSettingChoice;
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_1DINTERVALSGENSFILE );
    	str1DIntervalsAndRepsFile_ = strSettingChoice;

    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_POINTCLOUDASSTRING );
    	strPointCloudFromString_ = strSettingChoice;
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_DISTANCEMATRIXASSTRING );
    	strDistanceMatrixFromString_ = strSettingChoice;
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_DATALOADEDASARRAY );
    	strDataLoadedAsArray_ = strSettingChoice;
    	
    	
    	strSupplyDataAs = processData_.getDynamicProcessParameter( 
    			TDA.DATA_SUPPLYDATAAS );

    	if ( strSupplyDataAs.equalsIgnoreCase( TDA.UI_SUPPLYDATAAS_POINTCLOUD )) {

    		// "Adjust" when data was loaded from array [in Matlab]
    		if ( strDataLoadedAsArray_.length() > 0 ) {
    			
    			strPointCloudFromString_ = strDataLoadedAsArray_;
    		}
    		
        	// code for point cloud
        	if ( strPointCloudFromString_.length() > 0 ) {
        		
        		pointCloud_ = new PointCloudInRn( strPointCloudFromString_ );
        	}
        	else {
        	
        		pointCloud_ = new PointCloudInRn( strInputDirectory_, strPointCloudFile_ );
        	}

			// standard edgeList Constructor for single point cloud
			edgeList_ = new RipsToPersistence( pointCloud_, metric_, distanceBound_, processData_ );
    	}
    	else if ( strSupplyDataAs.equalsIgnoreCase( TDA.UI_SUPPLYDATAAS_DISTANCEMATRIX )) {

    		// "Adjust" when data was loaded from array [in Matlab]
    		if ( strDataLoadedAsArray_.length() > 0 ) {
    			
    			strDistanceMatrixFromString_ = strDataLoadedAsArray_;
    		}
    		    		
        	if ( strDistanceMatrixFromString_.length() > 0 ) {
        		

        		distanceMatrix_ = new DistanceMatrix( strDistanceMatrixFromString_ );        		
        	}
        	else {

        		distanceMatrix_ = new DistanceMatrix( strInputDirectory_, strPointCloudFile_ );
        	}
        	
			edgeList_ = new RipsToPersistence( distanceMatrix_, metric_, distanceBound_, processData_ );
    	}
    	else if ( strSupplyDataAs.equalsIgnoreCase( TDA.UI_SUPPLYDATAAS_SPARSEMATRIX ) || 
    			strSupplyDataAs.equalsIgnoreCase( TDA.UI_SUPPLYDATAAS_SPARSEMATRIX2 ) ) {
    		
    		// "Adjust" when data was loaded from array [in Matlab]
    		if ( strDataLoadedAsArray_.length() > 0 ) {
    			
    			strSparseMatrixFromString_ = strDataLoadedAsArray_;
    		}
    		
        	if ( strSparseMatrixFromString_.length() > 0 ) {
        		

        		sparseMatrix_ = new SparseMatrix( strSparseMatrixFromString_, processData_ );        		
        	}
        	else {

        		sparseMatrix_ = new SparseMatrix( strInputDirectory_, strPointCloudFile_, processData_ );
        	}
        	
			edgeList_ = new RipsToPersistence( sparseMatrix_, metric_, distanceBound_, processData_ );
			
			if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
	    			|| runningInEclipse_ ) {

	    		algorithmStatistics_.recordSpecifiedData( 
	    				new StringBuffer( StringUtil.compileMemoryInfo( 
	    						"\n\nIn RCA0 after creating edgeList," )) );
	    	}
//    		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//			"\n\nIn RCA0 after creating edgeList," )));
    	}
	}
	
    public void executeTask() throws Exception {
        
        // Record the initial data
    	// TODO
		algorithmStatistics_.recordInitialData( this );
		
		TdaErrorHandler errorHandler = new TdaErrorHandler( processData_ );
		
        try {

        	this.computeRCA0();
	    }
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
    }

    protected void computeRCA0() throws Exception  {

    	StringBuffer strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	StringBuffer strBufMemoryInfo =
			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
		boolean suppressAllOutput;

		suppressAllOutput = processData_.getValidatedProcessParameter( 
				TDA.SETTING_SUPPRESSALLOUTPUT ).
				equalsIgnoreCase( TDA.UI_SUPPRESSALLOUTPUT_YES );
    	
    	startTime_ = System.currentTimeMillis();

    	if ( !suppressAllOutput ) {

	    	strBufCollectedFeedback.append( "------------------------------------------------- " );
	    	strBufCollectedFeedback.append( "\n" + TDA.APPLICATION_NAME + ": Computing " + TDA.UI_TASK_RCA0 );
	    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
    	}

    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		strBufMemoryInfo.append( 
	    		new StringBuffer( StringUtil.compileMemoryInfo( 
	    				"\n\nAt start-up of " + 
	    				TDA.UI_TASK_RCA0 +
	    				" computation," )) );
    	}

    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {
    		
    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
    	
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	strBufCollectedFeedback.append( edgeList_.getCollectedFeedback() );

    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		strBufMemoryInfo.append( "\n" +  StringUtil.compileMemoryInfo(
    	         "After creating edgeList," ));
    	}

//    	strBufCollectFeedback.append( "\n" );
//    	strBufCollectFeedback.append( "EdgeList created." );

    	if ( !suppressAllOutput ) {
    		
	    	strBufCollectedFeedback.append( "\nNumber of vertices = " 
	    			+ edgeList_.getNumberOfVertices() );
	    	strBufCollectedFeedback.append( "\nNumber of edges = " 
	    			+ edgeList_.getNumberOfEdges() );
	    	strBufCollectedFeedback.append( "\nDistance bound on edges = " 
	    			+ edgeList_.getDistanceBound() );
	    	
	    	if ( processData_.getValidatedProcessParameter( TDA.SETTING_SUPPLYDATAAS )
	    			.equalsIgnoreCase( TDA.UI_SUPPLYDATAAS_POINTCLOUD ) 
	    			&& pointCloud_.getPointDimension() != TDA.UNKNOWNPOINTDIMENSION ) {
	    	
	    		strBufCollectedFeedback.append( "\nDimension of points  = " 
	    				+ pointCloud_.getPointDimension() );
	    	}
	    	else {
	    		
	    		strBufCollectedFeedback.append( "\nDimension of points: " 
	    				+ TDA.DATA_UNKNOWNPOINTDIMENSION );
	    	}
    	}
    	
    	
    	
    	// TODO: may want to store the collected content
    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
    	
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
//    	if (( COMPTOPO.GLOBALFEEDBACK && COMPTOPO.TRACE_MEMORYUSE ) 
//    			|| runningInEclipse_ ) {
//
//	    	//System.out.println();
//    		strBufMemoryInfo.append(
//	    			new StringBuffer( StringUtil.compileMemoryInfo(
//	    	        	"After assigning 'Positive' property to all edges," )) );
//    	}

    	strBufCollectedFeedback.append( edgeList_.getCollectedFeedback() );

    	// Record the feedback to date, and reset the buffer
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	strBufCollectedFeedback = new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	// ---------------------------------------------
    	// Process the edges
    	// ---------------------------------------------
    	
    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectedFeedback.append( "\n--- Processing edges" );
    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
  	
    	////Not needed for RCA0
    	// ---------------------------------------------
    	// Compute the persistence matrix
    	// ---------------------------------------------
//    	matricRCA0 = edgeList_.computeMatrixM23();
    	
    	    	
    	// ---------------------------------------------
    	// Record the feedback again
    	// ---------------------------------------------
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	


    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {
    		
    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	if ( runningInEclipse_ ) {
    	
	    	strBufCollectedFeedback.append( "\n\n------------------------------------------------- " );
	    	strBufCollectedFeedback.append( "\n--- 0-dim. intervals found" );
	    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
//	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.getZeroDimemsionalIntervals().getIntervalsAsString() );
	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.getZeroDimemsionalIntervals().getIntervalsExpandedAsString() );
//	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.get0DimPers() );
    	//}
 
    	}

    	if ( runningInEclipse_ || !apiMode_ ) {
    		
    		
	    	// ---------------------------------------------
	    	// Write the interval data to a separate file
	    	// ---------------------------------------------
	    	String strTmp;
	    	strTmp = edgeList_.get0DimPersPlain(); //edgeList_.get0DimPers();
	    	
	    	this.processData_.writeStringToFile( 
	    			strOutputDirectory_ + File.separator + str0DIntervalsFile_,
	    			strTmp, false, false );
    	}
    	
    	
    	zeroDimIntervals_ = edgeList_.getZeroDimemsionalIntervals();
    	
//    	strBufCollectFeedback.append( "\n" + zeroDimIntervals.toString() + "\n" );
    	

    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA0_0, 
    			zeroDimIntervals_, 
    			"0-dim. intervals" );
    	
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA0_2, 
    			edgeList_.getEdges().size(), 
    			"Number of edges" );
    	
    	// ---------------------------------------------
    	elapsedTime_ = System.currentTimeMillis() - startTime_;
    	processData_.setDynamicProcessParameter (
    			TDA.DATA_TOTALTIMEFORPREP, Long.toString( elapsedTime_ ) );

    	if ( !suppressAllOutput ) {
    		
	    	strBufCollectedFeedback.append( "\nElapsed time:  ");
	    	strBufCollectedFeedback.append(
	    			StringUtil.formatElapsedTime(
	    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
    	}

    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		strBufMemoryInfo.append( "\n" +
	    			new StringBuffer( StringUtil.compileMemoryInfo(
	    					"At completion," )) + "\n" );
        	algorithmStatistics_.recordSpecifiedData( strBufMemoryInfo );
    	}
    	
    	// -----------------------------------------------
    	// Final recording of the collected feedback data
    	// -----------------------------------------------
    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
	}
        
    public void updateProcessData( Settings _processData ) throws Exception {
        // nothing to do
    }
    
    // deprec
    protected ResultsCollection getZeroDimIntervals() {
    	
    	return zeroDimIntervals_;
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


        // Validate the 'distance bound'
        settingNameCanonical = TDA.SETTING_DISTANCEBOUND;
        settingNameDescriptive = TDA.SETTING_DISTANCEBOUND_DESCR;
        settingNameForDisplay = TDA.SETTING_DISTANCEBOUND_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_DOUBLE;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                null,
                Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
        
        if ( settingItem.isValidSetting() ) {

            try {

                strCondition = new String( "greater or equal than 0" );
                dblValue[0] = Double.parseDouble( 
                		processData_.getValidatedProcessParameter(
                                settingNameCanonical ));
                if ( dblValue[0] < 0 ) {
                    
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

                throw new TdaException( 
                        TDA.ERROR_APP_DEV, settingItem, this );
            }
        }
        else {
            
            isDataValid = false;
        }

        

        // Validate the 'supply data as' (the type in which the point cloud data
        // is supplied)
        settingNameCanonical = TDA.SETTING_SUPPLYDATAAS;
        settingNameDescriptive = TDA.SETTING_SUPPLYDATAAS_DESCR;
        settingNameForDisplay = TDA.SETTING_SUPPLYDATAAS_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validValues.clear();
        validValues.add( TDA.UI_SUPPLYDATAAS_POINTCLOUD );
        validValues.add( TDA.UI_SUPPLYDATAAS_DISTANCEMATRIX );
        validValues.add( TDA.UI_SUPPLYDATAAS_SPARSEMATRIX );
        validValues.add( TDA.UI_SUPPLYDATAAS_SPARSEMATRIX2 );
        validValues.add( TDA.UI_SUPPLYDATAAS_OBJECT );
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                validValues,
                TDA.UI_SUPPLYDATAAS_POINTCLOUD );
        
        if ( settingItem.isValidSetting() ) {

            try {


        		processData_.setDynamicProcessParameter( 
    	    			TDA.DATA_SUPPLYDATAAS, processData_.getValidatedProcessParameter( 
    	    					TDA.SETTING_SUPPLYDATAAS ) );
               
            }
            catch ( Exception e ) {

                throw new TdaException( 
                        TDA.ERROR_APP_DEV, settingItem, this );
            }
        }
        else {
            
            isDataValid = false;
        }
        
        settingNameCanonical = TDA.SETTING_POINTCLOUDFILE;
        settingNameDescriptive = TDA.SETTING_POINTCLOUDFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_POINTCLOUDFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_POINTCLOUDFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the point cloud file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_POINTCLOUDFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_POINTCLOUDFILE ));
        }
        

        // Validate the 'Zp-value'
        settingNameCanonical = TDA.SETTING_ZP_VALUE;
        settingNameDescriptive = TDA.SETTING_ZP_VALUE_DESCR;
        settingNameForDisplay = TDA.SETTING_ZP_VALUE_DISP;
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

               
            }
            catch ( Exception e ) {

                throw new TdaException( 
                        TDA.ERROR_APP_DEV, settingItem, this );
            }
        }
        else {
            
            isDataValid = false;
        }
        	        

        
        settingNameCanonical = TDA.SETTING_0DINTERVALSFILE;
        settingNameDescriptive = TDA.SETTING_0DINTERVALSFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_0DINTERVALSFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_0DINTERVALSFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 0-dimensional intervals file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DINTERVALSFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DINTERVALSFILE ));
        }

	    return isDataValid;
	}
}
