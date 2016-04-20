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

import edu.duke.math.tda.structures.EdgeI;
import edu.duke.math.tda.utility.TDA;

/**
 * Interval class
 * 
 * <p><strong>Details:</strong> <br>
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created February 2013
 * 
 * (hjs 3/25/2013)	Moved constants to TDA
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class Interval {
		
	protected int indexForAssociatedEdge_ = -1;
	protected double birth_ = -1;
	protected double death_ = TDA.INTERVAL_NODEATHVALUE; // default to our value for "infinity"
	protected int unionFindVertexIndex_ = -1;

	// hjs 10/13/2014 Newly tracked vertices
	protected int birthEdgeVertex1_ = -1;
	protected int birthEdgeVertex2_ = -1;
	protected int deathEdgeVertex1_ = -1;
	protected int deathEdgeVertex2_ = -1;
	
	// Setting up an "empty" Interval
	public Interval() {}
	
	public Interval( final int _indexForAssociatedEdge, 
			final double _birth, 
			final double _death,
			final int _unionFindVertexIndex ) {
		
		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.birth_ = _birth;
		this.death_ = _death;
		this.unionFindVertexIndex_ = _unionFindVertexIndex;
	}
	
	public Interval( final int _indexForAssociatedEdge, 
			final double _birth, 
			final double _death ) {
		
		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.birth_ = _birth;
		this.death_ = _death;
	}

	public Interval( final int _indexForAssociatedEdge, 
			final double _birth ) {

		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.birth_ = _birth;
	}

	// hjs 10/13/2014 Add constructor to handle newly tracked vertices
	public Interval( final int _indexForAssociatedEdge, 
			final EdgeI _birthEdge, 
			final EdgeI _deathEdge ) {
		
		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.birth_ = _birthEdge.getEdgeLength();
		this.death_ = _deathEdge.getEdgeLength();

		this.birthEdgeVertex1_ = _birthEdge.getVertexIndex1();
		this.birthEdgeVertex2_ = _birthEdge.getVertexIndex2();
		this.deathEdgeVertex1_ = _deathEdge.getVertexIndex1();
		this.deathEdgeVertex2_ = _deathEdge.getVertexIndex2();
	}

	// hjs 10/13/2014 Add constructor to handle newly tracked vertices
	public Interval( final int _indexForAssociatedEdge, 
			final EdgeI _birthEdge ) {

		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.birth_ = _birthEdge.getEdgeLength();
		
		this.birthEdgeVertex1_ = _birthEdge.getVertexIndex1();
		this.birthEdgeVertex2_ = _birthEdge.getVertexIndex2();
	}

	public Interval( final Interval _otherInterval ) {

		this.indexForAssociatedEdge_ = _otherInterval.indexForAssociatedEdge_;
		this.birth_ = _otherInterval.birth_;
		this.death_ = _otherInterval.death_;
		this.unionFindVertexIndex_ = _otherInterval.unionFindVertexIndex_;
		// hjs 10/14/2014
		this.birthEdgeVertex1_ = _otherInterval.birthEdgeVertex1_;
		this.birthEdgeVertex2_ = _otherInterval.birthEdgeVertex2_;
		this.deathEdgeVertex1_ = _otherInterval.deathEdgeVertex1_;
		this.deathEdgeVertex2_ = _otherInterval.deathEdgeVertex2_;
	}
	
	public int getUnionFindVertexIndex() {
		
		return this.unionFindVertexIndex_;
	}
	
	public double getBirth() {
		
		return this.birth_;
	}
	
	public double getDeath() {
		
		return this.death_;
	}
	
	public int getIndexForAssociatedEdge() {
		
		return this.indexForAssociatedEdge_;
	}
	
	public double getBirthEdgeVertex1() {
		
		return this.birthEdgeVertex1_;
	}
	
	public double getBirthEdgeVertex2() {
		
		return this.birthEdgeVertex2_;
	}
	
	public double getDeathEdgeVertex1() {
		
		return this.deathEdgeVertex1_;
	}
	
	public double getDeathEdgeVertex2() {
		
		return this.deathEdgeVertex2_;
	}
	
	public String toStringPlain() {
		
		StringBuffer strInterval = new StringBuffer();
		
		if ( death_ == TDA.INTERVAL_NODEATHVALUE ) {

			strInterval.append( birth_ + "\t" + TDA.INTERVAL_NODEATHVALUE );
		}
		else {
			
			strInterval.append( birth_ + "\t" + death_ );
		}
		
		return strInterval.toString();
	}
	
	public String toStringComma() {
		
		StringBuffer strInterval = new StringBuffer();
		
		if ( death_ == TDA.INTERVAL_NODEATHVALUE ) {

			strInterval.append( birth_ + ", " + TDA.INTERVAL_NODEATHVALUE );
		}
		else {
			
			strInterval.append( birth_ + ", " + death_ );
		}
		
		return strInterval.toString();
	}
	
	public String toStringFormatted() {
		
		StringBuffer strInterval = new StringBuffer();
		
		if ( death_ == TDA.INTERVAL_NODEATHVALUE ) {

			strInterval.append( "( " + birth_ + ", " + 
					TDA.INTERVAL_NODEATHVALUE + " )" );
		}
		else {
			
			strInterval.append( "( " + birth_ + ", " + death_ + " )" );
		}
		
		return strInterval.toString();
	}
	
	public String toString() {
		
		StringBuffer strInterval = new StringBuffer();
		
		// add the index of the associated edge
		strInterval.append( indexForAssociatedEdge_ + ":  " );

		// hjs 10/13/2014 Add newly tracked vertices to output
		if ( death_ == TDA.INTERVAL_NODEATHVALUE ) {

			strInterval.append( "( " + birth_ + ", " + 
					TDA.INTERVAL_NODEATHVALUE + " )" 
//					+ ", " + this.birthEdgeVertex1_ + ", " + this.birthEdgeVertex2_ 
					);
//			strInterval.append( "( " + birth_ + ", " + 
//					TDA.INTERVAL_NODEATHVALUE + " )" );
		}
		else {

			strInterval.append( "( " + birth_ + ", " + death_ + " )" 
//					+ ", " + this.birthEdgeVertex1_ + ", " + this.birthEdgeVertex2_ 
//					+ ", " + this.deathEdgeVertex1_ + ", " + this.deathEdgeVertex2_
					);
//			strInterval.append( "( " + birth_ + ", " + death_ + " )" );
		}
		
		return strInterval.toString();
	}
}

