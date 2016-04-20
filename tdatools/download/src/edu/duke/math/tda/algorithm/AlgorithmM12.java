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
package edu.duke.math.tda.algorithm;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.duke.math.tda.persistence.*;
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
 * Algorithm for computing 1-dimensional persistence (RCA1 == M12). 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * <p>
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class AlgorithmM12 extends Task {

	// Underlying objects
	protected PointCloudInRn pointCloud_;
	protected DistanceMatrix distanceMatrix_;
	protected SparseMatrix sparseMatrix_;
	protected RipsToPersistence edgeList_;
	protected double distanceBound_;     

	
	protected ResultsCollection resultsCollection_;
	protected ArrayList<Interval> intervals_;

	protected ResultsCollection zeroDimIntervals_ = new ResultsCollection();
	protected ResultsCollection oneDimIntervals_ = new ResultsCollection();
	
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
	
	public AlgorithmM12( Settings _processData ) throws Exception {
		
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
		
		resultsCollection_ = new ResultsCollection();

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
    	}
	}

    public void executeTask() throws Exception {
        
        // Record the initial data
    	// TODO
		algorithmStatistics_.recordInitialData( this );
		
		TdaErrorHandler errorHandler = new TdaErrorHandler( processData_ );
		
        try {

        	this.computeM12();
	    }
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
    }

    protected void computeM12() throws Exception  {

    	M12PersistenceMatrix matrixM12;

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
	    	strBufCollectedFeedback.append( "\n" + TDA.APPLICATION_NAME + ": Computing " + TDA.UI_TASK_M12 );
	    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
    	}

    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		strBufMemoryInfo.append( 
	    		new StringBuffer( StringUtil.compileMemoryInfo( 
	    				"\n\nAt start-up of M12 computation," )) );
    	}

    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_8,
    			StringUtil.compileMemoryInfo( "At start-up of M12 computation," ), 
    			"Memory use at startup of M12 computation" );

    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {
    		
    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
    	
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );

    	// hjs 10/21/2014 Comment out: (shows up in errors, in the wrong places)
//    	strBufCollectedFeedback.append( edgeList_.getCollectedFeedback() );
//
//    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
//    			|| runningInEclipse_ ) {
//
//    		strBufMemoryInfo.append( "\n" +  StringUtil.compileMemoryInfo(
//    	         "After creating edgeList," ));
//    	}


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
    	
    	
//    	processData_.setDynamicProcessParameter( 
//    			TDA.DATA_NUMBEROFEDGES, 
//    			Integer.toString( edgeList_.getNumberOfEdges() ) );
    	
    	
// TODO: decide if edgeList should be placed in resultsContainer    	

//    	strBufCollectedFeedback.append( "\n" );
//    	strBufCollectedFeedback.append( "\nEdgeList:" );
//    	strBufCollectedFeedback.append( "\n" + edgeList_.asStringComplete() );
    	
    	
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

//    	strBufCollectedFeedback.append( edgeList_.getCollectedFeedback() );

    	// Record the feedback to date, and reset the buffer
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
//    	strBufCollectedFeedback = new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	// ---------------------------------------------
    	// Process the edges
    	// ---------------------------------------------
    	
    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectedFeedback.append( "\n--- Processing edges" );
    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
  	
    	
    	// ---------------------------------------------
    	// Compute the persistence matrix
    	// ---------------------------------------------
    	matrixM12 = edgeList_.computeMatrixM12();
    	
    	    	

    	// ---------------------------------------------
    	// Collect the internal feedback collected by the edgeList
    	// ---------------------------------------------
//    	strBufCollectedFeedback.append( edgeList_.getCollectedFeedback() );


    	strBufCollectedFeedback.append( "\n\nDone" );

    	if (( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		strBufMemoryInfo.append( "\n" +
			    	new StringBuffer( StringUtil.compileMemoryInfo(
			    	     "After computing M12 matrix," )));
    	}

    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_9,
    			StringUtil.compileMemoryInfo( "After computing M12 matrix," ), 
    			"Memory use after computing matrix M12" );


    	// ---------------------------------------------
    	// Record the feedback again
    	// ---------------------------------------------
//    	algorithmStatistics_.recordSpecifiedData( strBufMemoryInfo );
    	
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	
//    	System.out.println( "\n****" + strBufMemoryInfo + "\n**** \n" );
//    	resultsContainer_.addResult(
//    			strBufMemoryInfo, 
//    			"memory used" );
//    	resultsContainer_.addRegisteredResult( 4,
//    			strBufMemoryInfo.toString(), 
//    			"memory used -- reg" );
//    	resultsContainer_.addRegisteredResult( 5,
//    			"@@@\n\nsome info\n@@@", 
//    			"tmp string -- reg" );
    	
    	
    	// TODO: decide if reduction matrix should be placed in resultsContainer (based on some flag?)
    	
    	// All the edges are processed, and we have our reduction matrix:
//    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n--- M12 after processing edges" );
//    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n\n" + matrixM12.asStringFormat1() );

    	
//    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
//    	strBufCollectFeedback =
//    			new StringBuffer( COMPTOPO.BUFFERLENGTH_SMALL );

    	
    	// ---------------------------------------------
    	// Reduce the persistence matrix to compute the intervals
    	// ---------------------------------------------

    	// hjs 11/17/2014 Add more granular timing info, and export via API
    	elapsedTime_ = System.currentTimeMillis() - startTime_;
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_4, 
    			StringUtil.formatElapsedTime(
    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ), 
    			"Computation time (formatted) of matrix M12" );
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_6, 
    			elapsedTime_, 
    			"Computation time (raw) of matrix M12" );

    	long reductionTime_ = System.currentTimeMillis();
    	
    	matrixM12.reduce();

    	elapsedTime_ = System.currentTimeMillis() - reductionTime_;
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_5, 
    			StringUtil.formatElapsedTime(
    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ), 
    			"Reduction time (formatted) of matrix M12" );
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_7,
    			elapsedTime_, 
    			"Reduction time (raw) of matrix M12" );

//    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n--- M12 after reduction step" );
//    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n\n" + matrixM12.getCollectedFeedback() );
//    	strBufCollectFeedback.append( "\n\n" + matrixM12.asStringFormat1() );
//
//    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n--- Intervals found (M12 internal compilation)" );
//    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
//    	strBufCollectFeedback.append( "\n\n" + matrixM12.getIntervalsAsString() );

//    	algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	strBufCollectedFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	intervals_ = matrixM12.getIntervals();
    	Interval tmpInterval;
    	
    	for ( int i=0; i<intervals_.size(); i++ ) {
    	
    		tmpInterval = intervals_.get( i );
    		resultsCollection_.addResult( tmpInterval );
    	}

    	if ( runningInEclipse_ ) {
    	
	    	strBufCollectedFeedback.append( "\n\n------------------------------------------------- " );
	    	strBufCollectedFeedback.append( "\n--- 0-dim. intervals found" );
	    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
//	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.getZeroDimemsionalIntervals().getIntervalsAsString() );
	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.getZeroDimemsionalIntervals().getIntervalsExpandedAsString() );
//	    	strBufCollectedFeedback.append( "\n\n" + edgeList_.get0DimPers() );
    	//}
//	    	System.out.println( strBufCollectedFeedback );
    	
	       	strBufCollectedFeedback.append( "\n\n" );
	       	strBufCollectedFeedback.append( "------------------------------------------------- " );
	    	strBufCollectedFeedback.append( "\n--- 1-dim. intervals found" );
	    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );
	//    	// "expanded format": includes originating edge
	//    	strBufCollectFeedback.append( "\n\n" + resultsCollection_.getIntervalsAsString() );
//	    	strBufCollectedFeedback.append( "\n\n" + matrixM12.getIntervalsAsStringComma() );
	    	strBufCollectedFeedback.append( "\n\n" + matrixM12.getIntervalsAsString() );
	    	

//	    	System.out.println( strBufCollectedFeedback );
	    	
	    	processData_.setDynamicProcessParameter(
	    			TDA.DATA_1DINTERVALS, matrixM12.getIntervalsAsStringCommaComma() );
	    	
    	}

    	if ( runningInEclipse_ || !apiMode_ ) {
    		
    		
	    	// ---------------------------------------------
	    	// Write the interval data to a separate file
	    	// ---------------------------------------------
	    	String strTmp = matrixM12.getIntervalsAsStringComma();
	    	this.processData_.writeStringToFile( 
	    			strOutputDirectory_ + File.separator + str1DIntervalsFile_,
	    			strTmp, false, false );
	    	//
	    	strTmp = edgeList_.get0DimPersPlain();
	    	this.processData_.writeStringToFile( 
	    			strOutputDirectory_ + File.separator + str0DIntervalsFile_,
	    			strTmp, false, false );
	    	
	    	// Set up the data required for drawing the diagrams
	    	processData_.setDynamicProcessParameter(
	    			TDA.DATA_0DXCOORDS, edgeList_.get0DimPersForDiagramZeros() );
	    	processData_.setDynamicProcessParameter(
	    			TDA.DATA_0DYCOORDS, edgeList_.get0DimPersForDiagram() );
	    	
	    	// Write out the sparse matrix

	    	strTmp = edgeList_.getPointCloudAsSparseMatrix();
//	    	this.processData_.writeStringToFile( 
//	    			strOutputDirectory_ + File.separator + "sparse.txt",
//	    			strTmp, false, false );
	    	if ( !processData_.getValidatedProcessParameter( 
	    			TDA.SETTING_SUPPLYDATAAS ).equalsIgnoreCase( 
	    					TDA.UI_SUPPLYDATAAS_SPARSEMATRIX ) &&
	    			
	    			processData_.getValidatedProcessParameter( 
	    			TDA.SETTING_CONVERTDATATOSPARSEMATRIXFORMAT ).equalsIgnoreCase( 
	    					TDA.UI_CONVERTDATATOSPARSEMATRIXFORMAT_YES ) ) {

		    	this.processData_.writeStringToFile( 
		    			strOutputDirectory_ + File.separator + strSparseMatrixDataFile_,
		    			strTmp, false, false );
	    	}
    	}
    	
    	

    	//
//    	String strTmp = edgeList_.get0DimPers();
//    	this.processData_.writeStringToFile( 
//    			strOutputDirectory_ + File.separator + str0DIntervalsFile_,
//    			strTmp, false, false );
    	
    	
    	

    	zeroDimIntervals_ = edgeList_.getZeroDimemsionalIntervals();
//    	double[][] intervals = zeroDimIntervals.getIntervals();
    	
    	oneDimIntervals_ = matrixM12.getResults();
//    	double[][] intervals = oneDimIntervals.getIntervals();
    	
//    	strBufCollectFeedback.append( "\n" + zeroDimIntervals.toString() + "\n" );

//    	System.out.println( "[AlgM12.computeM12] Adding 0 and 1-dim intervals to resultsContainer." );

    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_0, 
    			zeroDimIntervals_, 
    			"0-dim. intervals" );
    	
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_1, 
    			oneDimIntervals_, 
    			"1-dim. intervals" );
    	
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_2, 
    			edgeList_.getEdges().size(), 
    			"Number of edges" );
    	
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_RCA1_3, 
    			matrixM12.getInitialMatrixSize(), 
    			"Initial size (number of columns) of reduction matrix" );

//    	resultsContainer_.addResult( 0, zeroDimIntervals_, "0-dim. intervals" );
//    	resultsContainer_.addResult( 1, oneDimIntervals_, "1-dim. intervals" );
//    	resultsContainer_.addResult( 2, edgeList_.getEdges().size(), "number of edges" );
//    	resultsContainer_.addResult( 3, matrixM12.getInitialMatrixSize(), "initial size (number of columns) of reduction matrix" );
//    	
    	
    	
    	//// DEPREC:
//    	// Set up the data required for drawing the diagrams
//    	processData_.setDynamicProcessParameter(
//    			TDA.DATA_1DXCOORDS, matrixM12.getIntervalsBirths() );
//    	processData_.setDynamicProcessParameter(
//    			TDA.DATA_1DYCOORDS, matrixM12.getIntervalsDeaths() );

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

//    	strBufCollectedFeedback.append( "\n\n------------------------------------------------- " );
//    	strBufCollectedFeedback.append( "\nCompleted M12 computation" );
//    	strBufCollectedFeedback.append( "\n------------------------------------------------- " );


    	// -----------------------------------------------
    	// Final recording of the collected feedback data
    	// -----------------------------------------------
    	if ( !suppressAllOutput || ( TDA.GLOBALFEEDBACK && TDA.TRACE_MEMORYUSE ) 
    			|| runningInEclipse_ ) {

    		algorithmStatistics_.recordSpecifiedData( strBufCollectedFeedback );
    	}
	}
       
    public void updateProcessData( Settings _processData ) throws Exception {
        // nothing to do for now
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
	        
//	        if ( settingItem.isValidSetting() ) {
//	
//	            try {
//	
//	                strCondition = new String( "greater or equal than 2" );
//	                dblValue[0] = Double.parseDouble( 
//	                		processData_.getValidatedProcessParameter(
//	                                settingNameCanonical ));
//	                if ( dblValue[0] < 2 ) {
//	                    
//	                	processData_.addToErrors( new CompTopoError( 
//	                            StringUtil.composeErrorMessage( 
//	                            		settingItem, 
//	                                    strCondition ),
//	                            COMPTOPO.ERRORTYPE_INVALIDRANGE,
//	                            settingNameCanonical,
//	                            StringUtil.getClassName( this ) ) );
//	                    
//	                    isDataValid = false;
//	                }
//	            }
//	            catch ( Exception e ) {
//	
//	                throw new CompTopoException( 
//	                        COMPTOPO.ERROR_COMPTOPO_DEV, settingItem, this );
//	            }
//	        }
//	        else {
//	            
//	            isDataValid = false;
//	        }
	        
        
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
        

        settingNameCanonical = TDA.SETTING_1DINTERVALSFILE;
        settingNameDescriptive = TDA.SETTING_1DINTERVALSFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DINTERVALSFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DINTERVALSFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional intervals file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DINTERVALSFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DINTERVALSFILE ));
        }

        settingNameCanonical = TDA.SETTING_1DINTERVALSGENSFILE;
        settingNameDescriptive = TDA.SETTING_1DINTERVALSREPSFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DINTERVALSREPSFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DINTERVALSREPSFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional " +
                    "intervals and generators file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DINTERVALSREPSFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DINTERVALSGENSFILE ));
        }


        settingNameCanonical = TDA.SETTING_0DDIAGRAMFILE;
        settingNameDescriptive = TDA.SETTING_0DDIAGRAMFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_0DDIAGRAMFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_0DDIAGRAMFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 0-dimensional diagram file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_0DDIAGRAMFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_0DDIAGRAMFILE ));
        }



        settingNameCanonical = TDA.SETTING_1DDIAGRAMFILE;
        settingNameDescriptive = TDA.SETTING_1DDIAGRAMFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DDIAGRAMFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DDIAGRAMFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional diagram file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DDIAGRAMFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DDIAGRAMFILE ));
        }

        // These settings are used for auxilliary output, and likely should be available
        // for all executions of the algorithm
    	
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
        

        settingNameCanonical = TDA.SETTING_1DINTERVALSFILE;
        settingNameDescriptive = TDA.SETTING_1DINTERVALSFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DINTERVALSFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DINTERVALSFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional intervals file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DINTERVALSFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DINTERVALSFILE ));
        }

        settingNameCanonical = TDA.SETTING_1DINTERVALSGENSFILE;
        settingNameDescriptive = TDA.SETTING_1DINTERVALSREPSFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DINTERVALSREPSFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DINTERVALSREPSFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional " +
                    "intervals and generators file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DINTERVALSREPSFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DINTERVALSGENSFILE ));
        }


        settingNameCanonical = TDA.SETTING_0DDIAGRAMFILE;
        settingNameDescriptive = TDA.SETTING_0DDIAGRAMFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_0DDIAGRAMFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_0DDIAGRAMFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 0-dimensional diagram file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_0DDIAGRAMFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_0DDIAGRAMFILE ));
        }



        settingNameCanonical = TDA.SETTING_1DDIAGRAMFILE;
        settingNameDescriptive = TDA.SETTING_1DDIAGRAMFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_1DDIAGRAMFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_1DDIAGRAMFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the 1-dimensional diagram file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_1DDIAGRAMFILE, 
            		processData_.getValidatedProcessParameter( TDA.SETTING_1DDIAGRAMFILE ));
        }

	    return isDataValid;
	}
}
