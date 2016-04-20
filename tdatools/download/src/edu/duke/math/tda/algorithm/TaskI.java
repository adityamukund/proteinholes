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

import edu.duke.math.tda.structures.results.ResultsContainerI;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Documents the interface for creating a task implementation. 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Feb 26, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public interface TaskI {

    /**
     * Performs a task.
     */
    public abstract void executeTask() throws Exception;
    
	/**
	 * Updates whatever setting within processData that is being
	 * changed in the particular implementation
	 * 
	 * @param processData The data to be exchanged.
	 */
	public abstract void updateProcessData( Settings processData ) throws Exception;
    
	/**
	 * @return Returns the statistics about the particular implementation.
	 */
	public abstract StringBuffer provideCollectedStatistics() throws Exception;

	public abstract ResultsContainerI getResultsContainer();
}