/*
 * Created February 2013
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
 * ResultsCollection class
 * 
 * <p><strong>Details:</strong> <br>
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created February 2013
 * 
 * hjs (3/24/2013)	Change key for the collection to be a simple integer,
 * 					because the associatedEdge is not unique.
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;

public class ResultsCollection {

	// 
	protected int mapKey_ = -1;
	protected SortedMap<Integer, ResultRCA> resultsCollection_;
	
	// Set up a ResultsCollection with an empty container
	public ResultsCollection() {
		
		resultsCollection_ = new TreeMap<Integer, ResultRCA>();
	}
	
	// add a result entry that only has an interval
	public void addResult( final Interval _interval ) throws Exception {
		
		// Add the new result to the collection
		this.mapKey_++;
		resultsCollection_.put( 
				this.mapKey_,
				new ResultRCA( _interval ) );
	}
	
	// add a "complete" result entry (note: this needs to be a new entry, and be consistent
	// with already existing ones)
	public void addResult( final Interval _interval,
			final GeneratorList _generatorList ) throws Exception {

		// Add the new result to the collection 
		this.mapKey_++;
		resultsCollection_.put( 
				this.mapKey_, 
				new ResultRCA( _interval, _generatorList ) );
	}
	
	// TODO: when finishing the M01 work, make sure this works as intended!!
	// add a generatorList to a result entry
	public void updateResult( final int _resultIndex, 
			final GeneratorList _generatorList ) {

		// get the result that is associated with the specified edge,
		// and update its generator list
		ResultRCA tmpResult = resultsCollection_.get( _resultIndex );
		tmpResult.setGeneratorList( _generatorList );
		resultsCollection_.put( _resultIndex, tmpResult );
	}
	
	// Just in case the "consumer" of this class wants to do their own processing
	// of the raw results
	public SortedMap<Integer, ResultRCA> getResultsColletion() {
		
		return this.resultsCollection_;
	}

	// Provide the intervals only, in array form (necessary for Matlab interface)
    public double[][] getIntervals() {
    	
    	ResultRCA tmpResult;
    	int numberOfIntervals = resultsCollection_.size();
    	double[][] arrIntervalsToReturn = new double[ numberOfIntervals ][ 2 ];
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		arrIntervalsToReturn[ entry.getKey() ][ 0 ] = tmpResult.getInterval().getBirth();
    		arrIntervalsToReturn[ entry.getKey() ][ 1 ] = tmpResult.getInterval().getDeath();
    	}
    	
    	return arrIntervalsToReturn;
    }
    

    public double[][] getIntervalsAndBirthDeathGiversDim0() {
    	
    	ResultRCA tmpResult;
    	int numberOfIntervals = resultsCollection_.size();
    	double[][] arrIntervalsToReturn = new double[ numberOfIntervals ][ 4 ];
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		arrIntervalsToReturn[ entry.getKey() ][ 0 ] = tmpResult.getInterval().getBirth();
    		arrIntervalsToReturn[ entry.getKey() ][ 1 ] = tmpResult.getInterval().getDeath();
    		// dim. 0 specific info: index of death edge, index of birth vertex
    		arrIntervalsToReturn[ entry.getKey() ][ 2 ] = tmpResult.getInterval().getIndexForAssociatedEdge();
    		arrIntervalsToReturn[ entry.getKey() ][ 3 ] = tmpResult.getInterval().getUnionFindVertexIndex();
    	}
    	
    	return arrIntervalsToReturn;
    }
    

    // hjs 10/14/2014 Add output method for newly tracked vertices
    public double[][] getIntervalsAndBirthDeathGiversDim1() {
    	
    	ResultRCA tmpResult;
    	int numberOfIntervals = resultsCollection_.size();
    	double[][] arrIntervalsToReturn = new double[ numberOfIntervals ][ 6 ];
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		arrIntervalsToReturn[ entry.getKey() ][ 0 ] = tmpResult.getInterval().getBirth();
    		arrIntervalsToReturn[ entry.getKey() ][ 1 ] = tmpResult.getInterval().getDeath();
    		// dim. 1 specific info:  initial vertex of birth edge, final vertex of birth edge,   
    		// initial vertex of death edge, final vertex of death edge
      		arrIntervalsToReturn[ entry.getKey() ][ 2 ] = tmpResult.getInterval().getBirthEdgeVertex1();
    		arrIntervalsToReturn[ entry.getKey() ][ 3 ] = tmpResult.getInterval().getBirthEdgeVertex2();
    		arrIntervalsToReturn[ entry.getKey() ][ 4 ] = tmpResult.getInterval().getDeathEdgeVertex1();
    		arrIntervalsToReturn[ entry.getKey() ][ 5 ] = tmpResult.getInterval().getDeathEdgeVertex2();
    	}
    	
    	return arrIntervalsToReturn;
    }

	// Provide the intervals only, in array form
    public String getIntervalsExpandedAsString() {
    	
    	ResultRCA tmpResult;
    	int numberOfIntervals = resultsCollection_.size();
//    	double[][] arrIntervalsToReturn = new double[ numberOfIntervals ][ 4 ];
    	StringBuffer strBufIntervals = new StringBuffer();
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		
    		strBufIntervals.append( "( " );
    		strBufIntervals.append( tmpResult.getInterval().getBirth() );
    		strBufIntervals.append( ", " );
    		strBufIntervals.append( tmpResult.getInterval().getDeath() );
    		strBufIntervals.append( "), " );
    		strBufIntervals.append( tmpResult.getInterval().getIndexForAssociatedEdge() );
    		strBufIntervals.append( ", " );
    		strBufIntervals.append( tmpResult.getInterval().getUnionFindVertexIndex() );
    		strBufIntervals.append( "\n" );
    	}
    	
    	return strBufIntervals.toString();
    }

	// (Debug) Provide the intervals only
    public String toString() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	ResultRCA tmpResult;
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strIntervals.append( tmpResult.getInterval().toString() + "\n" );
    	}
    	
    	return strIntervals.toString();
    }

	// Provide the intervals only
    public String getIntervalsAsString() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	ResultRCA tmpResult;
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strIntervals.append( tmpResult.getInterval().toString() + "\n" );
    	}
    	
    	return strIntervals.toString();
    }
    
	// Provide the intervals only as simple string
    public String getIntervalsPlain() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	ResultRCA tmpResult;
    	
    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strIntervals.append( tmpResult.getInterval().toStringPlain() + "\n" );
    	}
    	
    	return strIntervals.toString();
    }

	// Provide the generators only (same as plain for now) -- for consistency, need to change
    // this to match doubles array for getIntervals     TODO
    public String getGenerators() {
    	
    	StringBuffer strGenerators = new StringBuffer();
    	ResultRCA tmpResult;

    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strGenerators.append( tmpResult.getGeneratorList().toString() + "\n" );
    	}
    	
    	return strGenerators.toString();
    }

	// Provide the generators only (same as plain for now) -- for consistency, need to change
    // this to match doubles array for getIntervals     TODO-TODO
    public String getGeneratorsExpanded() {
    	
    	StringBuffer strGenerators = new StringBuffer();
    	ResultRCA tmpResult;

    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strGenerators.append( tmpResult.getGeneratorList().toString() + "\n" );
    	}
    	
    	return strGenerators.toString();
    }

	// Provide the generators only
    public String getGeneratorsPlain() {
    	
    	StringBuffer strGenerators = new StringBuffer();
    	ResultRCA tmpResult;

    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strGenerators.append( tmpResult.getGeneratorList().toString() + "\n" );
    	}
    	
    	return strGenerators.toString();
    }
    
	// Provide the generators only
    public String getGeneratorsAsString() {
    	
    	StringBuffer strGenerators = new StringBuffer();
    	ResultRCA tmpResult;

    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strGenerators.append( tmpResult.getGeneratorList().toStringPlain() + "\n" );
    	}
    	
    	return strGenerators.toString();
    }

	// Provide the intervals and the generators
    public String getIntervalsAndGeneratorsAsString() {
    	
    	StringBuffer strGenerators = new StringBuffer();
    	ResultRCA tmpResult;

    	for ( Map.Entry<Integer, ResultRCA> entry : resultsCollection_.entrySet()) {
    		
    		tmpResult = entry.getValue();
    		strGenerators.append( tmpResult.getInterval().toString() + "  "
    				+ tmpResult.getGeneratorList().toStringPlain() + "\n" );
    	}
    	
    	return strGenerators.toString();
    }
}
