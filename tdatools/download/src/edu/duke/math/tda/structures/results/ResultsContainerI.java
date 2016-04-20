/*
 * Created 2014
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */

package edu.duke.math.tda.structures.results;

import java.util.ArrayList;

/**
 * ResultsContainerI interface.
 *   
 * <p><strong>Change History:</strong> <br>
 * Created 2014
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public interface ResultsContainerI {

	// Method for adding results with 'fixed/hardcoded' ID, so that scripts
	// depending on results retrieval will remain valid
	public abstract void addRegisteredResult( 
			final int _resultID,
			final Object _objectToAdd, 
			final String _resultDescription );

	// Method for adding new results (e.g., on experimental basis) by developer,
	// without having to define IDs
	public abstract void addResult( 
			final Object _objectToAdd, 
			final String _resultDescription );
	
	// Getting a count of the stored results
	public abstract int getResultsCount();
	
	// Generic way to get all results that are stored in the container, in 
	// the form of an array
	public abstract ArrayList<ResultGeneric> getResults();
	
	// Method for accessing the i-th result that is stored in the container
	public abstract ResultGeneric getResult( final int _resultID );
		
	// Access the info about the i-th result
	public abstract String getResultInfo( final int _resultID );
		
	// (deprec?) Get an array of all the descriptions of the stored results
	public abstract String[] getResultInfo();
}
