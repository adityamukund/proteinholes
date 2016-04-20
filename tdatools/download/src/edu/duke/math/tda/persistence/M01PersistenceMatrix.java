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
import java.util.Collections;
import java.util.List;

import edu.duke.math.tda.structures.EdgeZ2;
import edu.duke.math.tda.structures.Edge;
import edu.duke.math.tda.structures.RipsToPersistence;
import edu.duke.math.tda.structures.results.GeneratorList;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * M01 Matrix class
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

public class M01PersistenceMatrix extends PersistenceMatrix {
	
	protected ArrayList<Integer> emptyColumnsEdgeList_ = new ArrayList<Integer>();
	protected ArrayList<GeneratorList> generatorLists_ = new ArrayList<GeneratorList>();
		
	
	public M01PersistenceMatrix( final RipsToPersistence _edgeList ) {
		
		super( _edgeList );
	}

    public void computeGenerators() {
//    	
//    	ArrayList<Integer> tmpColumn = new ArrayList<Integer>();
//    	int vertex1 = -1;
//    	int vertex2 = -1;
//    	int tmpVertex1 = -1;
//    	int tmpVertex2 = -1;
//    	int tmpVertex3 = -1;
//    	int indexOfEdgeToProcess;
//    	Edge edgeToProcess;
//    	int tmpEdgeIndex;
//    	List<Edge> edges = this.edgeList_.getEdges();
//    	int[] p = this.edgeList_.getP();
//    	ArrayList<Integer> generatorsForCurrentColumn;
//    	GeneratorList generatorListForCurrentColumn;
//
////    	System.out.println( "Empty columns:\n" 
////    			+ this.emptyColumnsEdgeList_.toString() + "\n\n" );
//    	
//    	// Find the empty columns in the original matrix (after reduction):
//    	for ( int i=0; i< columns_.size(); i++ ) {
//    		
//    		if ( columns_.get( i ).size() == 0 ) {
//    			
//    	    	System.out.println( "Column " + i + " (with associated edge " + 
//    	    			associatedEdgesForColumnList_.get( i ) + ") is empty" + "\n\n" );
////    		}
////    	}
////    	
////    	System.out.println( "Coreduction columns (for computing generators):\n" 
////    			+ this.coreductionColumns_.toString() + "\n\n" );
////    	
////    	// get last entry from each column of the co-reduction matrix
////    	for ( int i=0; i< this.coreductionColumns_.size(); i++ ) {
//
//	    		// setup to a new blank generator list for this column
//	        	generatorsForCurrentColumn = new ArrayList<Integer>();
//	        	
//	    		tmpColumn = this.coreductionColumns_.get( i );
//	    		
//	    		for ( int j=0; j<tmpColumn.size(); j++ ) {
//
//	    			indexOfEdgeToProcess = tmpColumn.get( j );
////	    			indexOfEdgeToProcess = tmpColumn.get( tmpColumn.size()-1 );
//	
//					System.out.println( "\ni=" + i 
//							+ ", Processing  column: " + tmpColumn.toString() 
////							+ ", last entry: " + indexOfEdgeToProcess 
//							+ ", entry: " + indexOfEdgeToProcess 
//							+ "\n-------------------------------------------------" );
//		    		
//		    		
//		    		// add the current edge to the generator list
//					if ( !generatorsForCurrentColumn.contains( 
//							new Integer( indexOfEdgeToProcess ) ))  {
//						
//						System.out.println( "i=" + i 
//								+ ", Add starting edge: " + indexOfEdgeToProcess );
//			    		
//						generatorsForCurrentColumn.add( new Integer( indexOfEdgeToProcess ) );
//					}
//		    		
//		    		
//		    		edgeToProcess = edges.get( indexOfEdgeToProcess );
//		    		vertex1 = edgeToProcess.getVertexIndex1();
//		    		vertex2 = edgeToProcess.getVertexIndex2();
//		
//		//    		System.out.print( indexOfEdgeToProcess );
//		//    		System.out.println( ", edge = " + tmpEdge.toString() );
//		
//		    		
//					// Find the edges "linked" to this edge via vertex 1
//					tmpVertex1 = vertex1;
//					tmpVertex2 = vertex2;
//					tmpEdgeIndex = p[ vertex1 ];
//					System.out.println( "i=" + i 
//							+ ", Stepping through edges connected to vertex " + vertex1 );
//					
//					if ( tmpEdgeIndex == -1 ) {
//						
//						// DONE
//						System.out.println( "i=" + i 
//								+ ", Nothing else to do: " 
//								+ "p[ " + vertex1 + " ] = " + tmpEdgeIndex );
//					}
//					else {
//		
//						while ( tmpEdgeIndex > -1 ) {
//		
//		
//							if ( !generatorsForCurrentColumn.contains( new Integer( tmpEdgeIndex ) ))  {
//		
//								System.out.println( "i=" + i 
//										+ ", Adding edge " + tmpEdgeIndex  + " for " 
//										+ "p[ " + tmpVertex1 + " ] = " + tmpEdgeIndex );
//								
//								generatorsForCurrentColumn.add( new Integer( tmpEdgeIndex ) );
//							}
//							else {
//		
//								System.out.println( "i=" + i 
//										+ ", Would add edge (but already added): " 
//										+ indexOfEdgeToProcess );
//							}
//				    		
//							tmpVertex2 = tmpVertex1;
//							tmpVertex1 = edges.get( tmpEdgeIndex ).getOtherVertex( tmpVertex1 );
//							// CHECK: this can't ever be equal to the previous edge, or we
//							// end up in an infinite loop
//		    				tmpEdgeIndex = p[ tmpVertex1 ];				
//		    			}
//					}
//					
//		
//					// Find the edges "linked" to this edge via vertex 2
//					tmpVertex1 = vertex1;
//					tmpVertex2 = vertex2;
//					tmpEdgeIndex = p[ vertex2 ];
//					System.out.println( "i=" + i 
//							+ ", Stepping through edges connected to vertex " + vertex2 );
//					
//					if ( tmpEdgeIndex == -1 ) {
//						
//						// DONE
//						System.out.println( "i=" + i 
//								+ ", Nothing else to do: " 
//								+ "p[ " + vertex2 + " ] = " + tmpEdgeIndex );
//					}
//					else {
//		
//						while ( tmpEdgeIndex > -1 ) {
//		
//							
//							
//							if ( !generatorsForCurrentColumn.contains( new Integer( tmpEdgeIndex ) ))  {
//		
//								System.out.println( "i=" + i 
//										+ ", Adding edge " + tmpEdgeIndex  
//										+ " for p[ " + tmpVertex2 + " ] = " + tmpEdgeIndex );
//								
//								generatorsForCurrentColumn.add( new Integer( tmpEdgeIndex ) );
//							}
//							else {
//		
//								System.out.println( "i=" + i 
//										+ ", Would add edge (but already added): " 
//										+ tmpEdgeIndex );
//							}
//				    		
//							// walk "through" the P array until we hit -1
//							tmpVertex1 = tmpVertex2;
//							tmpVertex2 = edges.get( tmpEdgeIndex ).getOtherVertex( tmpVertex1 );
//							// CHECK: this can't ever be equal to the previous edge, or we
//							// end up in an infinite loop
//		    				tmpEdgeIndex = p[ tmpVertex2 ];				
//		    			}
//					}
//	    		}
//			
//				Collections.sort( generatorsForCurrentColumn );
//
//	        	generatorListForCurrentColumn = 
//	        			new GeneratorList( associatedEdgesForColumnList_.get( i ), 
//	        					generatorsForCurrentColumn );
//				
//	    		// Add the generator column for this edge to the generator list
//				this.generatorLists_.add( generatorListForCurrentColumn );
//    		}
//    	}
//    	
//
//		System.out.println( "\n\nGenerator lists: \n" + generatorLists_.toString() );
//		
//		strBufCollectFeedback.append( 
//				"\n\nGenerator lists: \n" + generatorLists_.toString() );
    }


    public String toString() {
    	
    	return this.asStringFormat1();
    }
	
	public ArrayList<GeneratorList> getGeneratorLists() {
		
		return this.generatorLists_;
	}
	
	public String asStringFormat1() {	// TODO: grab first part from super
		
		StringBuffer reductionMatrixAsString = new StringBuffer( "" );
		Column tempColumn;
//		ArrayList<Integer> tempColumn;
		
		for ( int i=0; i< this.columns_.size(); i++ ) {
			
			tempColumn = this.columns_.get( i );

			reductionMatrixAsString.append( "Column(" + i + "), from edge " +
					this.associatedEdgesForColumnList_.get( i ) + ": " );
			for ( int j=0; j<tempColumn.numberOfColumnEntries(); j++ ) {

				reductionMatrixAsString.append( tempColumn + "  " );
//				reductionMatrixAsString.append( tempColumn.get( j ) + "  " );
			}
			reductionMatrixAsString.append( "\n" );
		}

		reductionMatrixAsString.append( "'list of edges from empty columns': " + " " );
		for ( int j=0; j<this.emptyColumnsEdgeList_.size(); j++ ) {
			
			reductionMatrixAsString.append( this.emptyColumnsEdgeList_.get( j ) + "  " );
		}
		reductionMatrixAsString.append( "\n" );		
		
		return reductionMatrixAsString.toString();
	}
	
	public void addEmptyColumn( int _edgeIndex ) {
				
		this.emptyColumnsEdgeList_.add( new Integer( _edgeIndex ));
	}
	    
    public String getIntervalsAndGeneratorsAsString() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	
    	for ( int j=0; j<this.intervals_.size(); j++ ) {

        	// Intervals
    		strIntervals.append( this.intervals_.get( j ).toString() + "\t" );
    		
        	// Generators
    		strIntervals.append( " { generators go here }\n" );
//    		strIntervals.append( this.generators.get( j ).toString() + "\n" );
    	}
    	    	
    	return strIntervals.toString();
    }   
}
