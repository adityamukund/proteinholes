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

package edu.duke.math.tda.utility.recording;

import edu.duke.math.tda.algorithm.*;
import edu.duke.math.tda.utility.*;
import edu.duke.math.tda.utility.errorhandling.*;
import edu.duke.math.tda.utility.settings.Settings;

import java.text.DateFormat;
import java.util.*;

/**
 * Combines the common code shared by the different statistics implementations.
 *
 * <p><strong>Details:</strong> <br>
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public abstract class Recorder implements RecorderI {

	// Internal data (statistics) collection buffer
	protected volatile StringBuffer statisticsBuffer;
	
	// Individual tracking strings
	protected volatile StringBuffer resultBuffer;
	protected volatile StringBuffer summaryBuffer;
	protected volatile StringBuffer traceBuffer;
	
	// Indicators (using collections) for printing to file(s)
	protected Collection outputFileFlags = new HashSet(
	        TDA.MAXOUTPUTFILES );
	protected Collection outputResultsOnly = new HashSet(
	        TDA.MAXOUTPUTFILES );
	protected Collection outputTraceOnly = new HashSet(
	        TDA.MAXOUTPUTFILES );
	protected Collection outputSummaryOnly = new HashSet(
	        TDA.MAXOUTPUTFILES );
	protected Collection outputToAllFiles = new HashSet(
	        TDA.MAXOUTPUTFILES );
	
	protected final Settings processData;
	protected DateFormat timeFormat;
    
	private static final String strHeader = new String(" -- ");	

	public Recorder( Settings _processData ) throws Exception {

		// Keep a link to the settings around
		this.processData = _processData;

		timeFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, 
		        DateFormat.MEDIUM );
		
		statisticsBuffer = new StringBuffer( TDA.BUFFERLENGTH_STAT );
		resultBuffer = new StringBuffer( TDA.BUFFERLENGTH_STAT );
		summaryBuffer = new StringBuffer( TDA.BUFFERLENGTH_STAT );
		traceBuffer = new StringBuffer( TDA.BUFFERLENGTH_STAT );

		// Set up the collections used for printing the results	
		outputResultsOnly.add( new Integer(TDA.FILE_RESULTS) );
		outputSummaryOnly.add( new Integer(TDA.FILE_SUMMARY) );
		outputTraceOnly.add( new Integer(TDA.FILE_TRACE) );
		//
		outputToAllFiles.add( new Integer(TDA.FILE_RESULTS) );
		outputToAllFiles.add( new Integer(TDA.FILE_SUMMARY) );
		outputToAllFiles.add( new Integer(TDA.FILE_TRACE) );		
	}
	
	// may want to keep the timing at which we write to disk internal to this class
	protected synchronized void commitData( final Collection _outputFileFlags, 
	        final StringBuffer _textToCommit ) throws Exception {
		
	    try {
	        	        
            processData.writeToFile( _outputFileFlags , _textToCommit );
	    }
		catch (TdaException e) {
		    
		    // Only for now:
		    System.out.println( "Recorder.commitData  -- TDAException: " 
					+ e.toString());

			throw new TdaException( e );
		}
		catch (Exception e) {
		    
		    // Only for now:
		    System.out.println( "Recorder.commitData  -- Exception: " 
					+ e.toString());

			throw new Exception( e );
		}
	}	

	public synchronized void recordInitialData( Object _task ) throws Exception {

        StringBuffer optionalThreadLabel =  new StringBuffer();
        
        synchronized( this ) {
            
            StringBuffer tmpStatisticsBuffer = new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
            
    	    try {

                optionalThreadLabel = processData.getOptionalThreadInfo();
                if ( optionalThreadLabel.length() > 0 ) {

                    tmpStatisticsBuffer.append( optionalThreadLabel );
                    tmpStatisticsBuffer.append( " " );
//                    tmpStatisticsBuffer.append( COMPTOPO.DATA_SEARCHDATA );
                    tmpStatisticsBuffer.append( TDA.FEEDBACK_NEWLINE );
                }
                
    	        int lineLength = TDA.FEEDBACK_LINELENGTH;
                String dashedLine = TDA.FEEDBACK_DASHEDLINE.substring( 0, lineLength-1 );
        		
    	        // Add the common header
                tmpStatisticsBuffer.append( dashedLine );
        		
                tmpStatisticsBuffer.append( StringUtil.getTdaSignature() );
    
                tmpStatisticsBuffer.append( TDA.FEEDBACK_NEWLINE );
                tmpStatisticsBuffer.append( dashedLine );
        		
                tmpStatisticsBuffer.append( StringUtil.getJobSignature( processData ) );
    
                tmpStatisticsBuffer.append( TDA.FEEDBACK_NEWLINE );
                tmpStatisticsBuffer.append( dashedLine );
        		    	        
    			tmpStatisticsBuffer.append( TDA.FEEDBACK_NEWLINE );
                statisticsBuffer.append( tmpStatisticsBuffer );
    
                // hjs 5/23/2013: control the level of verboseness of the feedback
//                commitData( outputResultsOnly , statisticsBuffer );
                //          commitData( outputToAllFiles , statisticsBuffer );
    
                // Now clear the text buffer that was written to file
                statisticsBuffer =  new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
    	    }
    		catch ( TdaException e ) {
    		    
    			throw new TdaException(e);
    		}
    		catch ( Exception e ) {
                
    			throw new TdaException( e, TDA.ERROR_APP_DEV,
    			        "(Recorder.recordInitialData) " + 
    			        "Error in composing the string for the initial data reporting." );
    		}
        }
	}
	
	protected synchronized StringBuffer collectFeedback() throws Exception {
	    
	    // Collect the common data for a simple report on the start of the search
	    
	    String strSettingChoice;
        StringBuffer strBufFeedback = new StringBuffer();	
        int lineLength = TDA.FEEDBACK_LINELENGTH;
        String dashedLine = TDA.FEEDBACK_DASHEDLINE.substring( 0, lineLength-1 ); 
        int filesUsed = 1;

	    // Adding a little extra formatting
		String newLinePlusPrefix = TDA.FEEDBACK_NEWLINE + TDA.FEEDBACK_DASH + 
				TDA.FEEDBACK_SPACE;

		// ------------------------------------
		// Add feedback about user choices
		// ------------------------------------
        
		strBufFeedback.append( StringUtil.formatRightLeftJustified( 
		        newLinePlusPrefix, TDA.SETTING_CMDARG_SETTINGSFILENAME_DISP, 
		        processData.getDynamicProcessParameter( 
					TDA.SETTING_CMDARG_SETTINGSFILENAME ), null, lineLength ) );


        strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
        strBufFeedback.append( dashedLine );
        
		// 
		strSettingChoice = processData.getValidatedProcessParameter( 
		        TDA.SETTING_INPUTDIRECTORY );
		strBufFeedback.append( StringUtil.formatRightLeftJustified( 
		        newLinePlusPrefix, TDA.SETTING_INPUTDIRECTORY_DISP, 
		        strSettingChoice, null, lineLength ) );        
		
		// Record any warnings
		if ( processData.getCollectedWarnings().size() > 0 ) {

            strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
            strBufFeedback.append( dashedLine );
			
			strBufFeedback.append( StringUtil.formatRightLeftJustified( 
			        newLinePlusPrefix, "Warnings:", 
			        "", null, lineLength ) );

            strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
            strBufFeedback.append( dashedLine );
			strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
			
			strBufFeedback.append( processData.compileWarningMessages() );
		}

        strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
        strBufFeedback.append( dashedLine );

        String displayMemoryInfo = processData.getValidatedProcessParameter(
                TDA.SETTING_DISPLAYMEMORYINFO );

        if ( displayMemoryInfo.equals( TDA.UI_DISPLAYMEMORYINFO_YES ) ) {

            strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
            strBufFeedback.append( TDA.FEEDBACK_NEWLINE );
            strBufFeedback.append( 
                    StringUtil.compileMemoryInfo( 
                            TDA.DATA_MEMORYINFOBEFORESTART ) );
        }
		
	    return strBufFeedback;
	}

	public synchronized void recordFinalData( Object _task ) throws Exception {
        // nothing to do
	};
	
	public abstract void recordRecurringData( Object _task ) throws Exception;

	/* (non-Javadoc)
	 * @see edu.duke.cs.tda.data.RecorderI
	 * 		#recordSpecifiedData(java.lang.String)
	 */
	public abstract void recordSpecifiedData(StringBuffer dataToRecord) throws Exception;
}
