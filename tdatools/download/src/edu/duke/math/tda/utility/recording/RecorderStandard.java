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
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Provides a set of "standard" statistics describing the progress and results of 
 * the search algorithms.
 *
 * <p><strong>Details:</strong> <br>
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class RecorderStandard extends Recorder {
	
	public RecorderStandard( Settings _processData ) throws Exception {
		
		super( _processData );
	}
	
	// Keep the initial data recording separate from the constructor
	// for flexibility
	public void recordInitialData( Object _task ) throws Exception {
		
		super.recordInitialData( _task );
	}

	public synchronized void recordRecurringData( Object _task ) throws Exception {
			
	}

	public synchronized void recordFinalData( Object _task ) throws Exception {
		
		super.recordFinalData( _task );
		
		// Record data that is specific to this implementation:
		
		commitData( outputResultsOnly , statisticsBuffer );
		
		// Now clear the text buffer that was written to file
		statisticsBuffer = new StringBuffer(TDA.BUFFERLENGTH_STAT);
	}
	
	public synchronized void recordSpecifiedData( StringBuffer dataToRecord ) throws Exception{
		
		statisticsBuffer.append( dataToRecord );
		
		commitData( outputResultsOnly , statisticsBuffer );
		
		// Now clear the text buffer that was written to file
		statisticsBuffer = new StringBuffer(TDA.BUFFERLENGTH_STAT);
	}
}
