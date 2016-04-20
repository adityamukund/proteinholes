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

package edu.duke.math.tda.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.duke.math.tda.structures.results.Interval;
import edu.duke.math.tda.structures.RipsToPersistence;
import edu.duke.math.tda.structures.results.ResultRCA;
import edu.duke.math.tda.structures.results.ResultsCollection;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * M12 Matrix class
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

// TODO: this contains some dangerous type-casting, which needs to get fixed before production

public class M12PersistenceMatrix extends PersistenceMatrix {

	public M12PersistenceMatrix( final RipsToPersistence _edgeList ) {

		super( _edgeList );
	}

    public String toString() {
    	
    	return this.asStringFormat1();
    }
	
	public String asStringFormat1() {
		
		StringBuffer reductionMatrixAsString = new StringBuffer( "" );
		Column tempColumn;
				
		if ( columns_ != null ) {
			for ( int i=0; i< this.columns_.size(); i++ ) {
				
				tempColumn = this.columns_.get( i );
	
				reductionMatrixAsString.append( "Column(" + i + "), from edge " +
						this.associatedEdgesForColumnList_.get( i ) + ": " );
				for ( int j=0; j<tempColumn.numberOfColumnEntries(); j++ ) {
	
					reductionMatrixAsString.append( tempColumn + "  " );
				}
				reductionMatrixAsString.append( "\n" );
			}
		}

		reductionMatrixAsString.append( "'Row list': " + " " );
		for ( int j=0; j<this.rowEdgeList_.size(); j++ ) {
			
			reductionMatrixAsString.append( this.rowEdgeList_.get( j ) + "  " );
		}
		reductionMatrixAsString.append( "\n" );
		
		return reductionMatrixAsString.toString();
	}	
	
	public ResultsCollection getResults() throws Exception {
		
		ResultsCollection m12Results = new ResultsCollection();

    	for ( Interval interval : intervals_ ) {
    		
    		m12Results.addResult( interval );
    	}
    	
    	return m12Results;
	}
}
