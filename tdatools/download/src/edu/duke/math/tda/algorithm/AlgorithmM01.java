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


import java.util.ArrayList;

import edu.duke.math.tda.persistence.*;
import edu.duke.math.tda.structures.*;
import edu.duke.math.tda.structures.results.GeneratorList;
import edu.duke.math.tda.structures.results.ResultsCollection;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaErrorHandler;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Algorithm for computing 1-dimensional persistence, with generators (RCA1+ == M01). 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * <p>
 * 
 * as of 4/2015: incomplete
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class AlgorithmM01 extends AlgorithmM12 {

	ArrayList<GeneratorList> generatorLists_;

	int numberOfPoints;// = 4;
	int numberOfEntities;// = 3;
	String entityType = new String();
	double horizontalOffset;// = 10;
	
	public AlgorithmM01( Settings _processData ) throws Exception {
		
		super( _processData );
		
		// Validate the required settings
		boolean isDataValid = validateRequiredData();

		
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
				
		
		// ---- Pull this within the edge setup, because the edges may be given directly
		// (i.e. no need to load a point cloud)
		// TODO: if point cloud is loaded as points, or generated via recipe, then:
		// load the point cloud
		loadData();
//
//		// standard edgeList Constructor for single point cloud
    	edgeList_ = new RipsToPersistence( pointCloud_, metric_, distanceBound_, processData_ );
    	
        if ( processData_.wereThereProblems() ) {
            
            throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER,
                    "(Checkpoint) TDA performed a set of validation checks, " +
                    "and discovered the following issues " +
                    "which prevented further program execution:" + 
                    TDA.FEEDBACK_NEWLINE +
                    processData_.compileErrorMessages().toString() );
        }
	}
	
    public void executeAlgorithm() throws Exception {
        
        // Record the initial data
		algorithmStatistics_.recordInitialData( this );
		TdaErrorHandler errorHandler = new TdaErrorHandler( processData_ );
		
        try {
        	
        	// TODO: set computeGenerators based on user input 

        	// first compute the intervals
        	this.computeM12();
        	
        	// if specified, compute the generators
//        	if ( computeGenerators ) {
//        		
//            	this.computeM01();
//        	}
        	
        	// output the results in the specified format/file(s)
        	
        	
	    }
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
    }
    
    public void computeM01() throws Exception {

    	M01PersistenceMatrix matrixM01;

    	StringBuffer strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );

    	startTime_ = System.currentTimeMillis();

    	if ( TDA.GLOBALFEEDBACK && TDA.FEEDBACK_MEMORYUSE ) {

	    	strBufCollectFeedback.append( 
	    		new StringBuffer( StringUtil.compileMemoryInfo( 
	    				"At start-up of M01 computation:" )) );
	    	
	    	strBufCollectFeedback.append( "\n" );
    	}

    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );

    	if ( TDA.DEBUG && TDA.TRACE_MEMORYUSE ) {

    		strBufCollectFeedback.append( "\n" +  StringUtil.compileMemoryInfo(
    	         "    After creating edgeList" ));
    	}

    	strBufCollectFeedback.append( "\n" );
    	strBufCollectFeedback.append( "\nEdgeList created: number of edges = " 
    			+ edgeList_.getNumberOfEdges() );
    	
    	processData_.setDynamicProcessParameter( 
    			TDA.DATA_NUMBEROFEDGES, 
    			Integer.toString( edgeList_.getNumberOfEdges() ) );
    	
    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );

    	if ( TDA.DEBUG && TDA.TRACE_MEMORYUSE ) {

	    	System.out.println(
	    	 new StringBuffer( StringUtil.compileMemoryInfo(
	    	         "    After assigning 'Positive' property to all edges" )) );
    	}

    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );

    	// Record the feedback to date, and reset the buffer
    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback = new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	// ---------------------------------------------
    	// Process the edges
    	// ---------------------------------------------
    	
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- Processing edges" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );


    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );
    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );


    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );
    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	
    	// ---------------------------------------------
    	// Compute the persistence matrix
    	// ---------------------------------------------
    	matrixM01 = edgeList_.computeMatrixM01();
    	
    	strBufCollectFeedback.append( "\n" + edgeList_.printP() );

    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );
    	
    	strBufCollectFeedback.append( "\n\nDone" );
    	
    	if ( TDA.DEBUG && TDA.TRACE_MEMORYUSE ) {

		    	strBufCollectFeedback.append( "\n    " +
		    			new StringBuffer( StringUtil.compileMemoryInfo(
		    				"After computing persistence matrix" )) + "\n" );
    	}

    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	elapsedTime_ = System.currentTimeMillis() - startTime_;
    	processData_.setDynamicProcessParameter (
    	 TDA.DATA_TOTALTIMEFOREXECUTION, Long.toString( elapsedTime_ ) );
    	
    	strBufCollectFeedback.append( edgeList_.getCollectedFeedback() );
    	

    	// All the edges are processed, and we have our reduction matrix:
    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- M01 after processing edges" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n\n" + matrixM01.asStringFormat1() );

    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );
    	strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );

    	
    	// ---------------------------------------------
    	// Reduce the persistence matrix to compute the intervals
    	// ---------------------------------------------
    	
    	matrixM01.reduce();

    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- M01 after reduction step" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	// this grabs the feedback collected internally in the matrix reduction
    	strBufCollectFeedback.append( "\n\n" + matrixM01.getCollectedFeedback() );
    	strBufCollectFeedback.append( "\n\n" + matrixM01.asStringFormat1() );

    	
    	

    	matrixM01.computeGenerators();
    	
    	
    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- Generators found (M01 internal compilation)" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n\n" + matrixM01.getCollectedFeedback() );
    	strBufCollectFeedback.append( "\n\n" + matrixM01.getIntervalsAndGeneratorsAsString() );

    	

    	generatorLists_ = matrixM01.getGeneratorLists();
    	GeneratorList tmpGeneratorList;
    	
    	for ( int i=0; i<generatorLists_.size(); i++ ) {
    	
    		tmpGeneratorList = generatorLists_.get( i );
    		
    		// Note (3/24/2013) this call to updateResults will not work anymore, because
    		// we may have multiple intervals for each (associated) edge index
//    		resultsCollection_.updateResult( tmpGeneratorList.getIndexForAssociatedEdge(),
//    				tmpGeneratorList );
    		// likely, will need to modify this? Remember: there is no intrinsic
    		// meaning associated with the key (here: i) anymore... so we need to 
    		// be extra careful that we don't get the intervals in the 
    		// resultsCollection out of sync with the generator list.
    		resultsCollection_.updateResult( i, tmpGeneratorList );
    	}

    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- Generators found" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n\n" + resultsCollection_.getGeneratorsAsString() );


    	strBufCollectFeedback.append( "\n\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n--- Intervals + Generators found" );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n\n" + 
    			resultsCollection_.getIntervalsAndGeneratorsAsString() );

    	

    	elapsedTime_ = System.currentTimeMillis() - startTime_;
    	processData_.setDynamicProcessParameter (
    	 TDA.DATA_TOTALTIMEFORPREP, Long.toString( elapsedTime_ ) );
    	        strBufCollectFeedback.append( "\n  Elapsed time:  ");
    	strBufCollectFeedback.append(
    	 StringUtil.formatElapsedTime(
    	         elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );

    	if ( TDA.DEBUG && TDA.TRACE_MEMORYUSE ) {

    	strBufCollectFeedback.append( "\n    " +
    	new StringBuffer( StringUtil.compileMemoryInfo(
    	     "At completion" )) + "\n" );
    	}

    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	strBufCollectFeedback.append( "\n------------------------------------------------- " );
    	
    	algorithmStatistics_.recordSpecifiedData( strBufCollectFeedback );	
    }
    
        
    public void updateProcessData( Settings _processData ) throws Exception {
        // nothing to do
    }
}
