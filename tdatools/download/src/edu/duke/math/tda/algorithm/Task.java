/*
 * Created Feb 26, 2013
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

import java.text.DateFormat;
import java.util.HashSet;
import java.util.Set;

import edu.duke.math.tda.structures.metric.MetricFactory;
import edu.duke.math.tda.structures.metric.MetricI;
import edu.duke.math.tda.structures.results.ResultsContainer;
import edu.duke.math.tda.structures.results.ResultsContainerI;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.recording.RecorderI;
import edu.duke.math.tda.utility.recording.RecorderStandard;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Documents the abstract Task class for implementing the TaskI interface.
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Feb 26, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public abstract class Task implements TaskI {

	// --------------------------------------------------------------------
	// Common functionality that every derived Task subclass will share
	// --------------------------------------------------------------------
	
	
	// Objects that won't change once they've been created, and that are
	// used by every subclass:
	// Containers for the search process related data
	protected Settings processData_;
	
	ResultsContainerI resultsContainer_ = new ResultsContainer();
	//
	protected final MetricI metric_;
	
	// "Global" statistics collector for use in algorithm subclasses
    protected volatile RecorderI taskStatistics;
	
	// String container for collecting info about the process
	protected volatile StringBuffer strBufTaskInfo;
	
	// "Global" statistics collector for use in various classes
    protected volatile RecorderI algorithmStatistics_;
	
	// String container for collecting info about the process
	protected volatile StringBuffer algorithmStats_;
    
    // String for collecting memory info
	protected String displayMemoryInfo_;
	
	// Utility variables for tracking the elapsed time
	protected volatile long startTime_;
	protected volatile long elapsedTime_;
	protected volatile long intermediateTime_;
	protected DateFormat timeFormat_;

	// somewhat private (utility) variables
	protected long estimatedTime_;
	protected int numberOfDecimals_ = TDA.FEEDBACK_NUMBEROFDECIMALSINTIMEDISPLAY;
	protected int percentPaddingLength_ = TDA.FEEDBACK_PERCENTPADDINGLENGTH;
	protected int timePaddingLength_ = TDA.FEEDBACK_TIMEPADDINGLENGTH;

	protected String newLinePlusPrefix = TDA.FEEDBACK_NEWLINE + 
			TDA.FEEDBACK_DASH + TDA.FEEDBACK_SPACE;
	protected String linePostfix = TDA.FEEDBACK_SPACE + TDA.FEEDBACK_DASH;
	protected String prefix = TDA.FEEDBACK_DASH;
	protected int lineLength = TDA.FEEDBACK_LINELENGTH;
	/**
	 * Constructor for instantiating the objects shared between algorithm
	 * implementations.
	 * 
	 * @param _processData The special container for the initial, validated, and
	 * dynamically updated settings.
	 */
	public Task( Settings _processData ) throws Exception {

		MetricFactory metricFactory = new MetricFactory( _processData );
		metric_ = metricFactory.getMetric();
		
		strBufTaskInfo = new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
		algorithmStats_ = new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );

		// We want to link our internal processData container to the one that is
	    // being passed in to have convenient access to the dynamic process data
	    // from within various methods (mainly for the Algorithm subclasses).
	    this.processData_ = _processData;
                
		// Start the timer for the prep time
		startTime_ = System.currentTimeMillis();
		
		boolean isDataValid = validateRequiredData();
		
		// We check if there were any problems. If we found any, we cannot continue
		// setting up the Algorithm.
		if ( !isDataValid ) {
		    
		    throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER, 
		            processData_.compileErrorMessages().toString() );
		}

		// 
		elapsedTime_ = System.currentTimeMillis() - startTime_;
		processData_.setDynamicProcessParameter ( 
		        TDA.DATA_TOTALTIMEFORPREP, Long.toString( elapsedTime_ ) );
		


		//timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		timeFormat_ = DateFormat.getDateTimeInstance( DateFormat.SHORT, 
		        DateFormat.MEDIUM );
		

		
		// Set the user-selected statistics collector
		String strStatisticsChoice;
		strStatisticsChoice = _processData.getValidatedProcessParameter( 
		        TDA.SETTING_STATISTICSCHOICE );	
		String strSettingChoice = "";
		if ( strStatisticsChoice == TDA.UI_RECORDER_STANDARD ) { 

			this.algorithmStatistics_ = new RecorderStandard( _processData );
		}
		else {

			// Default case:
			this.algorithmStatistics_ = new RecorderStandard( _processData );
			strSettingChoice = TDA.UI_DEFAULTEDTO_DISP;
		}
		strSettingChoice = strSettingChoice + 
				StringUtil.getClassName( algorithmStatistics_ );
		
		processData_.setDynamicProcessParameter( 
				TDA.DATA_INFO_STATISTICS, 
				StringUtil.formatRightLeftJustified(
				        newLinePlusPrefix, TDA.SETTING_STATISTICSCHOICE_DISP,
				        strSettingChoice, null, lineLength ).toString() );

        
        // Cache the value
	    displayMemoryInfo_ = new String( processData_.getValidatedProcessParameter(
	            TDA.SETTING_DISPLAYMEMORYINFO ));
		
	}
	
	/* (non-Javadoc)
 * edu.duke.math.tda.algorithm.TaskI#executeTask()
	 */
	@Override
	public abstract void executeTask() throws Exception;
	

	/* (non-Javadoc)
 * edu.duke.math.tda.algorithm.TaskI#updateProcessData(edu.duke.math.comptopo.data.settings.Settings)
	 */
	@Override
	public void updateProcessData(Settings processData) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
 * edu.duke.math.tda.algorithm.TaskI#provideCollectedStatistics()
	 */
	@Override
	public StringBuffer provideCollectedStatistics() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Validates the settings values required for the task base class.
	 * 
	 * @return Returns the boolean flag that indicates whether a crucial setting
	 * could not be validated.
	 */
	private boolean validateRequiredData() throws Exception {

	    boolean isDataValid = true;

	    
	    
	    
	    return isDataValid;
	}

	/**
	 * @return Returns the elapsedTime that the task has been running.
	 */
	protected synchronized long getElapsedTime() {

		long localElapsedTime = System.currentTimeMillis() - startTime_;
		return localElapsedTime;
	}
	
	// TODO: switch to this:
//	public abstract Object getResultsContainer();

	public ResultsContainerI getResultsContainer() {
		
		return resultsContainer_;
	}
}
