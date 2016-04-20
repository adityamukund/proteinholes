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

/**
 * Documents the interface for creating a statistics implementation for 
 * describing the progress and results of the search algorithms.
 *
 * <p><strong>Details:</strong> <br>
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 *
 */
public interface RecorderI {
	
	public abstract void recordInitialData( 
	        Object _task ) throws Exception;
	
	public abstract void recordRecurringData( 
			Object _task ) throws Exception;
	
	public abstract void recordFinalData( 
			Object _task ) throws Exception;
	
	public abstract void recordSpecifiedData(
			StringBuffer dataToRecord ) throws Exception;
}