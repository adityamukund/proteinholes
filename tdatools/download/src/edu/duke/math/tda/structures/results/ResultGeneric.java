/*
 * Created Dec 4, 2013
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

package edu.duke.math.tda.structures.results;

/**
 * Documents the generic Result implementation
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Dec 4, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class ResultGeneric {
	
	protected int resultID_ = -1;
	protected Object result_;
	protected String resultDescription_;
	
	// Constructor is only way to create a 'generic result'
	public ResultGeneric( 
			final int _resultID,
			final Object _resultObject, 
			final String _resultDescription ) {
		
		resultID_ = _resultID;
		result_ = _resultObject;
		
		if ( _resultDescription != null ) {
		
			resultDescription_ = new String( _resultDescription );
		}
		else {
			
			resultDescription_ = new String( "No description is available for this result." );
		}
	}
	
	// Accessor methods to the data members
	public int getResultID() {
		
		return resultID_;
	}
	
	public Object getResult() {
		
		return result_;
	}
	
	public String getResultDescription() {
		
		return resultDescription_;
	}
	
	public String toString() {
		
		return resultDescription_ + ":\n" + result_.toString();
	}
	
	public String toStringWithID() {
		
		return "ID=" + resultID_ + ", " + 
				resultDescription_ + ":\n" + result_.toString();
	}
}