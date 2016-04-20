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
 * Result class (intervals and generators)
 * 
 * (hjs 3/24/2013)	Conceptual change: we can have multiple intervals to be
 * 					associated with the same edge -- they will all have the 
 *					same birth value, but different death values.
 * 
 * <p><strong>Details:</strong> <br>
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created February 2013
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class ResultRCA {

	// Note: since we use default values for these properties, a result may be a
	// "well-defined" object (for Java), but may not contain yet specific result
	// information. In addition, a result may be "completed" in steps, so it may 
	// contain interval info, but not yet generator info. To make sure that any
	// information that is being attempted to be used is valid, is the task
	// of the consumer.
	protected int associatedEdge_ = -1;
	protected Interval interval_ = new Interval();
	protected GeneratorList generatorList_ = new GeneratorList();
 
	
	public ResultRCA( final Interval _interval ) {
		
		this.associatedEdge_ = _interval.getIndexForAssociatedEdge();
		this.interval_ = new Interval( _interval );
	}
	
	public ResultRCA( Interval _interval, 
			final GeneratorList _generatorList ) throws Exception {

		this.associatedEdge_ = _interval.getIndexForAssociatedEdge();		
		this.interval_ = new Interval( _interval );
		this.generatorList_ = new GeneratorList( _generatorList );
	}
	
	public void setInterval( final Interval _interval ) throws Exception {
		
		interval_ = new Interval( _interval );
	}
	
	public void setInterval( final int _indexForAssociatedEdge, 
			final double _birth, final double _death ) {
		
		interval_ = new Interval( _indexForAssociatedEdge, _birth, _death );
	}
	
	public void setInterval( final int _indexForAssociatedEdge, 
			final double _birth ) {
		
		interval_ = new Interval( _indexForAssociatedEdge, _birth );
	}
	
	public void setGeneratorList( GeneratorList _generatorList ) {
		
		this.generatorList_ = new GeneratorList( _generatorList );
	}
	
	public Interval getInterval() {
		
		return this.interval_;
	}
	
	public GeneratorList getGeneratorList() {
		
		return this.generatorList_;
	}
	
	public String toString() {
		
		return this.interval_.toString();
	}
}
