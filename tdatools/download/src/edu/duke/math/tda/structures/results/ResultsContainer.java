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

import edu.duke.math.tda.utility.TDA;


/**
 * Implementation for ResultsContainerI interface.
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2014
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class ResultsContainer implements ResultsContainerI {

	protected ArrayList<ResultGeneric> listOfResults_ = new ArrayList<ResultGeneric>();

	protected String resultInfo_ = new String();
	
	// Use static counter to ensure that any result added via the 'quick' addResult method 
	// to this container has a unique ID (starting at [100] should be very generous with respect
	// to future 'fixed' ID requirements)
	protected static int currentUnregisteredResultID_ = TDA.DATA_UNREGISTEREDRESULT_STARTINDEX;
	
	
	// Method for adding a result that is associated with a "fixed" ID, as defined in TDA.jar.
	// Fixed results are always accessible via their fixed IDs, to ensure stability of the 
	// front end for the end user.
	public void addRegisteredResult( 
			final int _resultID,
			final Object _objectToAdd, 
			final String _resultDescription ) {
		
		if ( _objectToAdd != null ) {
	
			listOfResults_.add( new ResultGeneric( _resultID, 
					_objectToAdd, 
					_resultDescription ) );
		}
		else {
			
			// 'Save' explanation to user and developer
			listOfResults_.add( new ResultGeneric( currentUnregisteredResultID_, 
					null, 
					"[Dev Error] Trying to add a null object (with description '" +
					_resultDescription +
					"') to the results (id=" + currentUnregisteredResultID_  + ")!" ) );
			
			currentUnregisteredResultID_++;
		}
	}
	
	// Results that may be added in an ad-hoc or temporary matter. The user will get a listing
	// of each result with each ID from the front end, but can not rely on accessing the same such
	// result with the same ID each time the application is executed.
	public void addResult( 
		final Object _objectToAdd, 
		final String _resultDescription ) {
	
		if ( _objectToAdd != null ) {
		
			listOfResults_.add( new ResultGeneric( currentUnregisteredResultID_, 
									_objectToAdd, 
									_resultDescription ) );
			
			currentUnregisteredResultID_++;
		}
		else {
			
			// 'save' explanation to user/developer
			listOfResults_.add( new ResultGeneric( currentUnregisteredResultID_, 
					null, 
					"[Dev Error] Trying to add a null object (with description '" +
					_resultDescription +
					"') to the results (id=" + currentUnregisteredResultID_  + ")!" ) );
			
			currentUnregisteredResultID_++;
		}
	}
	
		
	public ArrayList<ResultGeneric> getResults() {

		ArrayList<ResultGeneric> sortedlistOfResultsToReturn_ = new ArrayList<ResultGeneric>();
		ResultGeneric[] listOfResultsSorted = new ResultGeneric[ listOfResults_.size() ];
		ResultGeneric[] listOfOnTheFlyResults = new ResultGeneric[ listOfResults_.size() ];
		int tmpResultID;
		int nbrOfOnTheFlyResults = 0;
		int nbrOfAssignedResults = 0;
		
		for ( int i=0; i<listOfResults_.size(); i++ ) {
		
			// first split into "assigned" resultIDs and "on-the-fly" ones 
			// because they can occur in any order, as well as interspersed
			tmpResultID = listOfResults_.get( i ).getResultID();
			if ( tmpResultID < 100 ) {
			
				// the "assigned" results start at index 0, and are in-order
				listOfResultsSorted[ tmpResultID ] = listOfResults_.get( i );
				nbrOfAssignedResults++;
			}
			else {
				
				// place the "on-the'fly" results in a temp. list
				listOfOnTheFlyResults[ nbrOfOnTheFlyResults ] = listOfResults_.get( i );
				nbrOfOnTheFlyResults++;
			}
		}

		// transform the list back into the original ArrayList format
		for ( int i=0; i<listOfResultsSorted.length; i++ ) {
			
			sortedlistOfResultsToReturn_.add( i, listOfResultsSorted[ i ] );
		}

		// now fold any "on-the-fly" results into the sorted list (we don't care about their resultIDs 
		// being in sorted order)
		if ( nbrOfOnTheFlyResults > 0 ) {
			
			for ( int i=0; i<nbrOfOnTheFlyResults; i++ ) {
				
				sortedlistOfResultsToReturn_.add( TDA.DATA_UNREGISTEREDRESULT_STARTINDEX + i, listOfOnTheFlyResults[ i ] );	
			}
		}
		
		return sortedlistOfResultsToReturn_;
	}
	

//	public ResultGeneric getResult( final int _resultIndex ) throws Exception {
	public ResultGeneric getResult( final int _resultID ) {
		
		ResultGeneric resultToReturn;
		
		// need to walk through results, because "id" values are not (necessarily) sequential
		for ( int i=0; i<listOfResults_.size(); i++ ) {

			resultToReturn = listOfResults_.get( i );
			
			if ( resultToReturn.getResultID() == _resultID ) {
				
				return resultToReturn;
			}			
		}
		
		return new ResultGeneric( -2, null, 
				"No result available for i=" + _resultID + "." );
	}
	

	public String[] getResultInfo() {
		
		String[] infoToReturn = new String[ listOfResults_.size() ];
		
		for ( int i=0; i<listOfResults_.size(); i++ ) {
			
			infoToReturn[ i ] = listOfResults_.get( i ).getResultID() +
					"\t" + listOfResults_.get( i ).getResultDescription();
		}
		
		
		return infoToReturn;
	}

	public String getResultInfo( final int _resultID ) {
		
		String infoToReturn;
		
		if ( _resultID < listOfResults_.size() ) {
			
			infoToReturn = listOfResults_.get( _resultID ).getResultDescription();
		}
		else {
			
			infoToReturn = new String( "Result item #" + _resultID + 
					" does not exist." );
		}
		
		return infoToReturn;
	}
	

	public int getResultsCount() {
		
		if ( listOfResults_ != null ) {
		
			return listOfResults_.size();
		}
		else {
			
			return -1;
		}
	}

	public String toString() {

		StringBuffer infoToReturn = new StringBuffer();
		
		// TODO: May want to list results in order of resultID (and not by order of
		// being added to the result set)
		if ( listOfResults_ != null && listOfResults_.size() > 0 ) {
			
			infoToReturn.append( "Available Results:" );
			
			for ( int i=0; i<listOfResults_.size(); i++ ) {
				
				infoToReturn.append( "\n" + listOfResults_.get( i ).getResultID() + "\t" + 
						listOfResults_.get( i ).getResultDescription() );
			}
		}
		else {

			infoToReturn.append( "No results available!" );
		}
		
		return infoToReturn.toString();
	}
}
