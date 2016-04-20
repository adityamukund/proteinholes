/*
 * Created October 2012
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
import java.util.SortedMap;
import java.util.TreeMap;

import edu.duke.math.tda.structures.EdgeI;
import edu.duke.math.tda.structures.RipsToPersistence;
import edu.duke.math.tda.structures.results.Interval;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Reduction Matrix class
 * 
 * <p><strong>Details:</strong> <br>
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created October 2012
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

// TODO: this contains some dangerous type-casting, which needs to get fixed before production

public abstract class PersistenceMatrix {

	protected ArrayList<Column> columns_ = 
				new ArrayList<Column>();
	protected ArrayList< SortedMap<Integer, Integer> > coreductionColumns_ = 
		new ArrayList< SortedMap<Integer, Integer> >();
	
	protected ArrayList<Integer> associatedEdgesForColumnList_ = new ArrayList<Integer>();
	
	protected ArrayList<Integer> rowEdgeList_ = new ArrayList<Integer>();
	protected RipsToPersistence edgeList_;
	protected Settings processData_;

	protected ArrayList<Interval> intervals_ = new ArrayList<Interval>();

	StringBuffer strBufCollectFeedback = 
	    new StringBuffer( TDA.BUFFERLENGTH_STAT );
	
	ReductionImplementorI reductionImplementor_; 
   	
	// hjs 6/3/2014 Add to be able to report on initial size of reduction matrix
	int initialMatrixSize_;
    

	// lookup array for multiplicative inverses
	int[] inverse_;
	
	// Inner class for representing a Column
	protected class Column {

		protected Integer associatedEdge_ = -1;
		protected SortedMap<Integer, Integer> columnEntries_ = new TreeMap<Integer, Integer>();
		
		// The only way we add a new column is by knowing its associated edge and at least
		// its first entry
		public Column( final Integer _associatedEdge, 
				final SortedMap<Integer, Integer> _columnInfo ) {
			
			associatedEdge_ = _associatedEdge;
			
			columnEntries_ = new TreeMap<Integer, Integer>( _columnInfo );
		}
		
		// new column with no column entries
		public Column( final Integer _associatedEdge ) {
			
			associatedEdge_ = _associatedEdge;
			
			columnEntries_ = new TreeMap<Integer, Integer>();
		}
		
		public Column() {
			// TODO Auto-generated constructor stub
		}

		public Integer getAssociatedEdge() {
			
			return associatedEdge_;
		}
		
		public SortedMap<Integer, Integer> getColumnEntries() {
			
			return columnEntries_;
		}
		
		// get number of entries in the (sorted) list
		public int numberOfColumnEntries() {
			
			return columnEntries_.size();
		}
		
		public void addColumnEntry( final Integer _key, final Integer _value ) {
			
			columnEntries_.put( _key, _value );
		}
		
		public void removeColumnEntry( final Integer _key ) {
			
			columnEntries_.remove( _key );
		}
		
		public Column makeColumnCopy() {
			
			Column thisColumn = new Column();
			thisColumn.associatedEdge_ = this.associatedEdge_;
			
			for ( SortedMap.Entry<Integer, Integer> entry : columnEntries_.entrySet())
			{
				thisColumn.columnEntries_.put( entry.getKey(), entry.getValue() );
			}
			
			return thisColumn;
		}
		
		// TODO: add toString
		public String toString() {
			
			StringBuffer columnsAsString = new StringBuffer();

			columnsAsString.append( "e=" + associatedEdge_ );
			
			for ( SortedMap.Entry<Integer, Integer> entry : columnEntries_.entrySet())
			{
				columnsAsString.append( ", ( " + entry.getKey() + ", " + entry.getValue() + " )" );
			}
			
			return columnsAsString.toString();
		}
	}
	
	// Inner class hierarchy for the reduction implementors:
	//		interface ReductionImplementorI
	//		abstract base class ReductionImplementor
	//		implementation class for Z2 ReductionImplementorZ2
	//		implementation class for Zp ReductionImplementorZp
	protected interface ReductionImplementorI {
		
		public void reduce();
	}
	
	protected abstract class ReductionImplementor implements ReductionImplementorI {
		
		public abstract void reduce();
	}

	
	protected class ReductionImplementorZp extends ReductionImplementor {
		
		protected int p_;
		
		protected ReductionImplementorZp( final int _p ) {
			
			this.p_ = _p;
		}                      

		public void reduce() {
	    		    	
	    	// To do? -- Sanity check: do we have a matrix that is ready for this step?
	    	
	    	
	    	
	    	// Apply the reduction steps to the collected "matrix"
	    	

			ArrayList<Column> finalColumns = 
						new ArrayList<Column>();
			
	    	Column tempColumn1;
	    	Column tempColumn2;
			int lastKeyInColumn1;
			int lastKeyInColumn2;
			// set the "pointer" to the end of the list
			int currentKeyInColumn1;
			int currentKeyInColumn2;
			int numberOfEntriesInColumn1;
			int numberOfEntriesInColumn2;
			boolean wasMerged;
	    	int rowItemToCheck;
	    	int j;
	    	int sum;
			int lastColumnValue1;
			int lastColumnValue2;
			int columnFactor = 1;
	    	
	    	// used as a check whether a column has been completely eliminated
	    	boolean[] emptyColumnFlag = new boolean[ columns_.size() ];
	    	for ( int i=0; i<emptyColumnFlag.length; i++ ) {
	    		
	    		emptyColumnFlag[ i ] = false;
	    	}

			Column mergedColumn;
			
			EdgeI originatingEdge;
			EdgeI tmpEdge;
			final List<EdgeI> tmpEdgelist = edgeList_.getEdges();
			
			// Apply the reduction
			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_REDUCTION ) {
		    	
	        	strBufCollectFeedback.append( "Before reduction: \n" + asStringFormat1() );
			}
			
			if ( columns_.size() > 0 ) {
				
				// Take the last entry of the FIRST column
				// and add it to final list
				tempColumn1 = columns_.get( 0 ).makeColumnCopy();
				numberOfEntriesInColumn2 = tempColumn1.numberOfColumnEntries();
				int lastKey = tempColumn1.getColumnEntries().lastKey();

				finalColumns.add( tempColumn1 );

				// set up the co-reduction:
				setupCoreductionMatrix();			
				
				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
			    	
		        	strBufCollectFeedback.append( "\nfinalColumns.add[x1], i=" + 0 +
						", j=0" + " first elem.=" + finalColumns.get( 0 ) );
				}


				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					
					for ( int i=0; i<columns_.size(); i++ ) {
						System.out.println ( "column k = " + i + ":  " + columns_.get( i ).toString() );
					}
					System.out.println ( "\n final columns:" );
					for ( int k=0; k<finalColumns.size(); k++ ) {
						System.out.println ( "       finCol k=" + k + ", " + finalColumns.get( k ).toString() );
					}
					System.out.println ();
				}
				
				// Now go through the remaining columns
				for ( int i=1; i<columns_.size(); i++ ) {
		

					// reset the merged column
					wasMerged = false;
					tempColumn2 = columns_.get( i ).makeColumnCopy();
					// start with a new, "blank" column, with the same associated edge as column 3
					mergedColumn = new Column( tempColumn2.getAssociatedEdge() );
					numberOfEntriesInColumn2 = tempColumn2.numberOfColumnEntries();
					
					lastKeyInColumn2 = tempColumn2.getColumnEntries().lastKey();

					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
						
						System.out.println( "\ni=" + i + ",   tmpC2:   " + tempColumn2 );
						System.out.println();
					}
										
					// do the "merge-compare" with all previous columns (and a "restart" after each merge)				
					j = 0;
					while ( j<i && !emptyColumnFlag[ i ] ) {
		
						// only need to (re)visit any column that is not empty
						if ( !emptyColumnFlag[ j ] ) {
							
							// Get the "current" column #2 (only if it was changed due to previous merge)
							if ( wasMerged ) {
							
								// TODO: check if we actually have to reload the tempCol!!?
								tempColumn2 = columns_.get( i ).makeColumnCopy();
								mergedColumn = new Column( tempColumn2.getAssociatedEdge() );
								numberOfEntriesInColumn2 = tempColumn2.numberOfColumnEntries();
								wasMerged = false;
							}
							
							// Get the next column to compare to
							tempColumn1 = columns_.get( j ).makeColumnCopy();
							numberOfEntriesInColumn1 = tempColumn1.numberOfColumnEntries();

							if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
							
								System.out.println ( "  j=" + j + ": comparing C1: " + tempColumn1 + 
										",            C2: " + tempColumn2 );
							}
							
							// Note: col 2 will have at least one element, so no need to check?
							if ( numberOfEntriesInColumn1 > 0 && numberOfEntriesInColumn2 > 0 ) {

								lastKeyInColumn2 = tempColumn2.getColumnEntries().lastKey();
								lastKeyInColumn1 = tempColumn1.getColumnEntries().lastKey();
								// set the "pointer" to the end of the list
								currentKeyInColumn1 = lastKeyInColumn1;
								currentKeyInColumn2 = lastKeyInColumn2;

								// only merge when last elements match
								if ( lastKeyInColumn1 == lastKeyInColumn2 ) {

									// set flag that we merge(d)
									wasMerged = true;
									
									// Get the column values for their last columns, and compute the factor
									// for multiplying all entries in column 1, before they are added to column 2
									// Note that these values may not be "reduced" mod p, so we need to do it now
									// (otherwise out inverse lookup in the next statement may fail)
									lastColumnValue1 = tempColumn1.getColumnEntries().get( lastKeyInColumn1 ) % p_;
									lastColumnValue2 = tempColumn2.getColumnEntries().get( lastKeyInColumn2 ) % p_;
									
									while ( lastColumnValue2 < 0 ) lastColumnValue2 += p_;
									while ( lastColumnValue1 < 0 ) lastColumnValue1 += p_;
									columnFactor = - inverse_[ lastColumnValue1 ] * lastColumnValue2;
									while ( columnFactor < 0 ) columnFactor += p_;

									// first remove the last item from column 2, and column 1
									tempColumn2.getColumnEntries().remove( lastKeyInColumn2 );
									tempColumn1.getColumnEntries().remove( lastKeyInColumn1 );
									
									// if column 1 is empty, there's nothing left to do
									if ( tempColumn1.getColumnEntries().size() > 0 ) {

										int tmpValueColumn1;
										int tmpValueColumn2;
										// simply add all entries (multiplied by the factor) from col 1 to col2
										do {

											lastKeyInColumn1 = tempColumn1.getColumnEntries().lastKey();
//											lastColumnValue1 = tempColumn1.getColumnEntries().get( lastKeyInColumn1 );
								
											if ( tempColumn2.getColumnEntries().containsKey( lastKeyInColumn1 ) ) {
												
												tmpValueColumn1 = tempColumn1.getColumnEntries().get( lastKeyInColumn1 );
												tmpValueColumn2 = tempColumn2.getColumnEntries().get( lastKeyInColumn1 );
												
												// compute the new value for column 2
												tmpValueColumn2 = tmpValueColumn2 + tmpValueColumn1 * columnFactor;
												while ( tmpValueColumn2 < 0 ) tmpValueColumn2 += p_;
												tmpValueColumn2 = tmpValueColumn2 % p_;
												
												// and place this new value back into column 2
												if ( tmpValueColumn2 > 0 ) {
												
													tempColumn2.getColumnEntries().put( lastKeyInColumn1, tmpValueColumn2 );
												}
												else {
													
													tempColumn2.getColumnEntries().remove( lastKeyInColumn1 );
												}
											}
											else {
												
												// column 2 doesn't contain the key, so simply add the value from the (adjusted)
												// first column
												tmpValueColumn2 = tempColumn1.getColumnEntries().get( lastKeyInColumn1 ) * columnFactor;
												while ( tmpValueColumn2 < 0 ) tmpValueColumn2 += p_;
												tmpValueColumn2 = tmpValueColumn2 % p_;
												
												if ( tmpValueColumn2 > 0 ) {
												
													tempColumn2.getColumnEntries().put( lastKeyInColumn1, tmpValueColumn2 );
												}
												
											}
												
											// remove the last item from column 1
											tempColumn1.getColumnEntries().remove( lastKeyInColumn1 );
										} 
										while ( tempColumn1.getColumnEntries().size() > 0 );
									}
								}

								if ( TDA.TRACE_FEEDBACK && TDA.TRACE_COREDUCTION ) {
							    	
						        	strBufCollectFeedback.append( 
						        			"\nCoreduction merge for i=" + i + ", j=" + j +
						        			"\n" );
								}
								
								// TODO
								// apply the coreduction
//									applyCoreduction( j, i );

								if ( TDA.TRACE_FEEDBACK && TDA.TRACE_COREDUCTION ) {

						        	strBufCollectFeedback.append( "\n\nCoreduction columns: \n" 
						        			+ coreductionColumns_.toString() + "\n\n" );
								}
						
								if ( wasMerged ) {
		
									// update the processed column with the merged data
									columns_.set( i, tempColumn2 );
									j = 0;
								}
								else {
									
									// nothing was merged, so go to next column
									j++;
								}
							} // emptyColumn check
							else {
								
								// one column was empty; if it was the second one, then set its flag
								if ( numberOfEntriesInColumn2 == 0 ) {
									
									emptyColumnFlag[ i ] = true;
								}
								
								// in either case (i.e. col 1 or 2 being empty), there is nothing to compare, 
								// so skip ahead
								j++;
							}
						}
						else {
							
							// the next column in line for comparison is empty, so skip ahead
							j++;
						}
					} // while-loop for j
		
					if ( columns_.get( i ).numberOfColumnEntries() > 0 ) {

						// when all the merging is done, and the "column 2" is still non-empty,
						// then add it to our results
						finalColumns.add( columns_.get( i ).makeColumnCopy() );
						
						if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					    	
				        	strBufCollectFeedback.append( "\nfinalColumns.add[x2], i=" + i +
								", j-loop complete, lastElementInColumn2=" + lastKeyInColumn2 );
						}
					}
					else {
						
						// store the fact that we completely "merged away" a column, and won't need to
						// deal with it anymore
						emptyColumnFlag[ i ] = true;
					}

					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					
						System.out.println ( "\n final columns:" );
						for ( int k=0; k<finalColumns.size(); k++ ) {
							System.out.println ( "       finCol k=" + k + ", " + finalColumns.get( k ).toString() );
						}
					}
				} // end of i-loop
			} // condition that there are in fact any columns to process
			
			
			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
		    	
	        	strBufCollectFeedback.append( "\n\nAfter reduction: \n" + this.toString() );
	        	strBufCollectFeedback.append( "\n\nfinalColumns.size: " + finalColumns.size() );
			}
//			System.out.println( "\nAfter reduction: \n" + this.toString() );
//			System.out.println( "\nfinalColumns.size: " + finalColumns.size() );
						
			// Final step: compute the actual intervals
			
			Column tmpColumn;
			int tmpEdgeIndex;
			
			// column contributions:
	    	for ( int i=0; i< finalColumns.size(); i++ ) {
				
	    		tmpColumn = finalColumns.get( i );
	    		tmpEdgeIndex = tmpColumn.getColumnEntries().lastKey();
				originatingEdge = tmpEdgelist.get( tmpColumn.getAssociatedEdge() );

				if ( tmpEdgeIndex >=0 ) {
					
					tmpEdge = tmpEdgelist.get( tmpEdgeIndex );
					// hjs 10/13/2014 Use new constructor, set up for tracking the vertices of the edges
					intervals_.add( 
							new Interval( tmpColumn.getAssociatedEdge(),
									tmpEdge, 
									originatingEdge ));

					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					
						System.out.println( "col: adding interval " + new Interval( originatingEdge.getEdgeListIndex(),
								tmpEdge.getEdgeLength(), 
								originatingEdge.getEdgeLength() ) );
					}
					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {

			        	strBufCollectFeedback.append( "\nAdding interval from column, edge=" + finalColumns.get( i ) 
							+ ", orig. edge=" + originatingEdge.getEdgeListIndex() );
					}
//					System.out.println( "Adding interval from column, edge=" + finalColumns.get( i ) 
//							+ ", orig. edge=" + originatingEdge.edgeListIndex_ );
				}
//				else {
//					
//					// indicate ERROR
//					if ( COMPTOPO.TRACE_FEEDBACK && COMPTOPO.TRACE_PERSISTENCEMATRIX ) {
//				    	
//			        	strBufCollectFeedback.append( "\nERROR: finalColumns.get( " + i + " ) = -1" );
//					}
////					System.out.println( "ERROR: finalColumns.get( " + i + " ) = -1" );
//				}
	    	}
	    	
	    	
	    	ArrayList<Integer> usedEdges = new ArrayList<Integer>();
	    	for ( int k=0; k<finalColumns.size(); k++ ) {
				
	    		usedEdges.add( finalColumns.get( k ).columnEntries_.lastKey() );
			}
	    	
	    	// row contributions (death=infinite):
			for ( j=0; j<rowEdgeList_.size(); j++ ) {
				
				rowItemToCheck = rowEdgeList_.get( j );

	    		if ( !usedEdges.contains( rowItemToCheck ) ) {
	    			
					tmpEdge = tmpEdgelist.get( rowItemToCheck );
					// hjs 10/13/2014 Use new constructor, set up for tracking the vertices of the edge
					intervals_.add( 
							new Interval( rowItemToCheck, tmpEdge ));
//					intervals_.add( 
//							new Interval( rowItemToCheck, tmpEdge.getEdgeLength() ) );

					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					
						System.out.println( "row: adding interval " + 
								new Interval( rowItemToCheck, tmpEdge.getEdgeLength() ) );
					}
//					System.out.println( "\nAdding interval from row, tmpEdge.getDistance=" 
//		        			+ tmpEdge.getEdgeLength() );
					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
				    	
			        	strBufCollectFeedback.append( "\nAdding interval from row, tmpEdge.getDistance=" 
			        			+ tmpEdge.getEdgeLength() );
					}
//					System.out.println( "Adding interval from row, tmpEdge.getDistance=" + tmpEdge.getDistance() );
				}
			}

//			if ( COMPTOPO.TRACE_FEEDBACK && COMPTOPO.TRACE_PERSISTENCEMATRIX ) {
	//
//	        	strBufCollectFeedback.append( "\n\nFinal output (intervals): \n" 
//	        			+ this.getIntervalsAsString() );
	        	
	        	strBufCollectFeedback.append( "\n\nCoreduction columns: \n" 
	        			+ coreductionColumns_.toString() + "\n\n" );
//			}
	        	
//			System.out.println( "\nFinal output (intervals): \n" + this.getIntervalsAsString() );
	    
		}
	}
		
	protected class ReductionImplementorZ2 extends ReductionImplementor {
		
		protected ReductionImplementorZ2() {}
		
		public void reduce() {
	    		    	
	    	// To do -- Sanity check: do we have a matrix that is ready for this step?
	    	
	    	
	    	
	    	// Apply the reduction steps to the collected "matrix"
	    	

			ArrayList<Column> finalColumns = 
						new ArrayList<Column>();
			
	    	Column tempColumn1;
	    	Column tempColumn2;
			int lastKeyInColumn1;
			int lastKeyInColumn2;
			int numberOfEntriesInColumn1;
			int numberOfEntriesInColumn2;
			boolean wasMerged;
	    	int rowItemToCheck;
	    	int j;
	    	
	    	// used as a check whether a column has been completely eliminated
	    	boolean[] emptyColumnFlag = new boolean[ columns_.size() ];
	    	for ( int i=0; i<emptyColumnFlag.length; i++ ) {
	    		
	    		emptyColumnFlag[ i ] = false;
	    	}

			Column mergedColumn;
			
			EdgeI originatingEdge;
			EdgeI tmpEdge;
			final List<EdgeI> tmpEdgelist = edgeList_.getEdges();

			
			// Apply the reduction
			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_REDUCTION ) {
		    	
	        	strBufCollectFeedback.append( "Before reduction: \n" + asStringFormat1() );
			}
			
			if ( columns_.size() > 0 ) {
				
				// Take the last entry of the FIRST column
				// and add it to final list
				tempColumn1 = columns_.get( 0 ).makeColumnCopy();
				numberOfEntriesInColumn2 = tempColumn1.numberOfColumnEntries();
				int lastKey = tempColumn1.getColumnEntries().lastKey();

//				System.out.println ( "(first column) tempColumn1.lastKey: " + lastKey );

				finalColumns.add( tempColumn1 );

				// set up the co-reduction:
				setupCoreductionMatrix();

//				System.out.println( "Still before starting reduction -- " +
//						"co-reduction Matrix has form: \n" + coreductionColumns_.toString()
//						+ "\n" );
			
				
				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
			    	
		        	strBufCollectFeedback.append( "\nfinalColumns.add[x1], i=" + 0 +
						", j=0" + " first elem.=" + finalColumns.get( 0 ) );
				}

//				System.out.println ( "columns_.size():" + columns_.size() );


				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					
					System.out.println ( "Columns before applying reduction " );
					System.out.println ( "---------------------------------\n" );
					for ( int i=0; i<columns_.size(); i++ ) {
						System.out.println ( "column " + i + ":  " + columns_.get( i ).toString() );
					}
	
					System.out.println ( "\nReduction process" );
					System.out.println ( "-----------------" );
					
					System.out.println ( "\nFinal columns:" );
					for ( int k=0; k<finalColumns.size(); k++ ) {
						System.out.println ( "       column k=" + k + ":   " + finalColumns.get( k ).toString() );
					}
					System.out.println ( "---------------------------------------------------------" );
					System.out.println ();
				}
				
				// Now go through the remaining columns
				for ( int i=1; i<columns_.size(); i++ ) {
		

					// reset the merged column
					mergedColumn = new Column();
					wasMerged = false;
					tempColumn2 = columns_.get( i ).makeColumnCopy();
					
					numberOfEntriesInColumn2 = tempColumn2.numberOfColumnEntries();
					
					lastKeyInColumn2 = tempColumn2.getColumnEntries().lastKey();


					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
						
						System.out.println( "\nIteration i=" + i + ",   column to process:   " + tempColumn2 );
						System.out.println();
					}
										
					// do the "merge-compare" with all previous columns (and a "restart" after each merge)				
					j = 0;
					while ( j<i && !emptyColumnFlag[ i ] ) {
		
						// only need to (re)visit any column that is not empty
						if ( !emptyColumnFlag[ j ] ) {
							
							// Get the "current" column #2 (only if it was changed due to previous merge)
							if ( wasMerged ) {
							
								// TODO: check if we actually have to reload the tempCol!!?
								tempColumn2 = columns_.get( i ).makeColumnCopy();
								numberOfEntriesInColumn2 = tempColumn2.numberOfColumnEntries();
								wasMerged = false;
							}
							
							// Get the next column to compare to
							tempColumn1 = columns_.get( j ).makeColumnCopy();
							numberOfEntriesInColumn1 = tempColumn1.numberOfColumnEntries();


							if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
								
								System.out.println ( "    j=" + j + ": comparing C1: " + tempColumn1 + ", C2: " + tempColumn2 );
							}
							

							// Note: col 2 will have at least one element, so no need to check?
							if ( numberOfEntriesInColumn1 > 0 && numberOfEntriesInColumn2 > 0 ) {

								lastKeyInColumn2 = tempColumn2.getColumnEntries().lastKey();
								lastKeyInColumn1 = tempColumn1.getColumnEntries().lastKey();

								// only merge when last elements match
								if ( lastKeyInColumn1 == lastKeyInColumn2 ) {

									// set flag that we merge(d)
									wasMerged = true;
									
									// first remove the last item from column 2, and column 1
									tempColumn2.getColumnEntries().remove( lastKeyInColumn2 );
									tempColumn1.getColumnEntries().remove( lastKeyInColumn1 );
									
									// if column 1 is empty, there's nothing left to do
									if ( tempColumn1.getColumnEntries().size() > 0 ) {

										int tmpValueColumn1;
										int tmpValueColumn2;
										// simply add all entries (multiplied by the factor) from col 1 to col2
										do {

											// get the new "last key" from column1
											lastKeyInColumn1 = tempColumn1.getColumnEntries().lastKey();
								
											if ( tempColumn2.getColumnEntries().containsKey( lastKeyInColumn1 ) ) {
																									
												tempColumn2.getColumnEntries().remove( lastKeyInColumn1 );
											}
											else {
												
												tempColumn2.getColumnEntries().put( lastKeyInColumn1, 1 );
											}
												
											// remove the last item from column 1
											tempColumn1.getColumnEntries().remove( lastKeyInColumn1 );
										} 
										while ( tempColumn1.getColumnEntries().size() > 0 );
									}
								}

								if ( TDA.TRACE_FEEDBACK && TDA.TRACE_COREDUCTION ) {
							    	
						        	strBufCollectFeedback.append( 
						        			"\nCoreduction merge for i=" + i + ", j=" + j +
						        			"\n" );
								}
								
								// TODO
								// apply the coreduction
//									applyCoreduction( j, i );

								if ( TDA.TRACE_FEEDBACK && TDA.TRACE_COREDUCTION ) {

						        	strBufCollectFeedback.append( "\n\nCoreduction columns: \n" 
						        			+ coreductionColumns_.toString() + "\n\n" );
								}
						
								if ( wasMerged ) {
				
									// update the processed column with the merged data
									columns_.set( i, tempColumn2 );
									j = 0;
								}
								else {
									
									// nothing was merged, so go to next column
									j++;
								}
							} // emptyColumn check
							else {
								
								// one column was empty; if it was the second one, then set its flag
								if ( numberOfEntriesInColumn2 == 0 ) {
									
									emptyColumnFlag[ i ] = true;
								}
								
								// in either case (i.e. col 1 or 2 being empty), there is nothing to compare, 
								// so skip ahead
								j++;
							}
						}
						else {
							
							// the next column in line for comparison is empty, so skip ahead
							j++;
						}
					} // while-loop for j
		
					if ( columns_.get( i ).numberOfColumnEntries() > 0 ) {

						// when all the merging is done, and the "column 2" is still non-empty,
						// then add it to our results
						finalColumns.add( columns_.get( i ).makeColumnCopy() );
						
						if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
					    	
				        	strBufCollectFeedback.append( "\nfinalColumns.add[x2], i=" + i +
								", j-loop complete, lastElementInColumn2=" + lastKeyInColumn2 );
						}
					}
					else {
						
						// store the fact that we completely "merged away" a column, and won't need to
						// deal with it anymore
						emptyColumnFlag[ i ] = true;
					}
					
//					System.out.println ( "   end of loop i=" + i );
//					for ( int k=0; k<columns_.size(); k++ ) {
//						System.out.println ( "       col k=" + k + ", " + columns_.get( k ).toString() );
//					}


					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
						
						System.out.println ( "\nFinal columns after step i=" + i +
								":" );
						for ( int k=0; k<finalColumns.size(); k++ ) {
							System.out.println ( "       column k=" + k + ":   " + finalColumns.get( k ).toString() );
						}
						System.out.println ( "---------------------------------------------------------" );
						System.out.println ();
					}
				} // end of i-loop
			} // condition that there are in fact any columns to process
			
			
			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
		    	
	        	strBufCollectFeedback.append( "\n\nAfter reduction: \n" + this.toString() );
	        	strBufCollectFeedback.append( "\n\nfinalColumns.size: " + finalColumns.size() );
			}
			
			// Final step: compute the actual intervals
			
			Column tmpColumn;
			int tmpEdgeIndex;
			
			// column contributions:
	    	for ( int i=0; i< finalColumns.size(); i++ ) {
				
	    		tmpColumn = finalColumns.get( i );
	    		tmpEdgeIndex = tmpColumn.getColumnEntries().lastKey();
				originatingEdge = tmpEdgelist.get( tmpColumn.getAssociatedEdge() );

				if ( tmpEdgeIndex >=0 ) {
					
					tmpEdge = tmpEdgelist.get( tmpEdgeIndex );
					// hjs 10/13/2014 Use new constructor, set up for tracking the vertices of the edges
					intervals_.add( 
							new Interval( tmpColumn.getAssociatedEdge(),
									tmpEdge, 
									originatingEdge ));

					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
						
						System.out.println( "col: adding interval " + new Interval( originatingEdge.getEdgeListIndex(),
								tmpEdge.getEdgeLength(), 
								originatingEdge.getEdgeLength() ) );
					}
					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {

			        	strBufCollectFeedback.append( "\nAdding interval from column, edge=" + finalColumns.get( i ) 
							+ ", orig. edge=" + originatingEdge.getEdgeListIndex() );
					}
//					System.out.println( "Adding interval from column, edge=" + finalColumns.get( i ) 
//							+ ", orig. edge=" + originatingEdge.edgeListIndex_ );
				}
	    	}
	    	
	    	
	    	ArrayList<Integer> usedEdges = new ArrayList<Integer>();
	    	for ( int k=0; k<finalColumns.size(); k++ ) {
				
	    		usedEdges.add( finalColumns.get( k ).columnEntries_.lastKey() );
			}

	    	// row contributions (death=infinite):
			for ( j=0; j<rowEdgeList_.size(); j++ ) {
				
				rowItemToCheck = rowEdgeList_.get( j );

	    		if ( !usedEdges.contains( rowItemToCheck ) ) {
	    			
//	    			System.out.println( "\n   rowItemToCheck=" + rowItemToCheck + 
//	    					"\n   tmpEdgelist.size=" + tmpEdgelist.size() );
	    			
					tmpEdge = tmpEdgelist.get( rowItemToCheck );
					// hjs 10/13/2014 Use new constructor, set up for tracking the vertices of the edge
					intervals_.add( 
							new Interval( rowItemToCheck, tmpEdge ) );


					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
						
						System.out.println( "row: adding interval " + 
								new Interval( rowItemToCheck, tmpEdge.getEdgeLength() ) );
					}
//					System.out.println( "\nAdding interval from row, tmpEdge.getDistance=" 
//		        			+ tmpEdge.getEdgeLength() );
					if ( TDA.TRACE_FEEDBACK && TDA.TRACE_PERSISTENCEMATRIX ) {
				    	
			        	strBufCollectFeedback.append( "\nAdding interval from row, tmpEdge.getDistance=" 
			        			+ tmpEdge.getEdgeLength() );
					}
//					System.out.println( "Adding interval from row, tmpEdge.getDistance=" + tmpEdge.getDistance() );
				}
			}

//			if ( COMPTOPO.TRACE_FEEDBACK && COMPTOPO.TRACE_PERSISTENCEMATRIX ) {
	//
//	        	strBufCollectFeedback.append( "\n\nFinal output (intervals): \n" 
//	        			+ this.getIntervalsAsString() );
	        	
	        	strBufCollectFeedback.append( "\n\nCoreduction columns: \n" 
	        			+ coreductionColumns_.toString() + "\n\n" );
//			}
	        	
//			System.out.println( "\nFinal output (intervals): \n" + this.getIntervalsAsString() );
	    
		}

		
	    public void applyCoreduction( final int _colIndex1, final int _colIndex2 ) {

//	    	// Apply the merge operation of column _colIndex1 to column _colIndex2
//
//			ArrayList<Integer> tempColumn1;
//			ArrayList<Integer> tempColumn2;
//			ArrayList<Integer> mergedColumn;
//			int numberOfElementsInColumn1;
//			int numberOfElementsInColumn2;
//			int indexPointerForColumn1;
//			int indexPointerForColumn2;
//			int elementInColumn1;
//			int elementInColumn2;
//			
//			tempColumn2 = coreductionColumns_.get( _colIndex2 );
//			numberOfElementsInColumn2 = tempColumn2.size();
//			
//			tempColumn1 = coreductionColumns_.get( _colIndex1 );
//			numberOfElementsInColumn1 = tempColumn1.size();
//			
//
//			// Process column 2 when it has more than a single element
//			indexPointerForColumn1 = 0;
//			indexPointerForColumn2 = 0;
//			mergedColumn = new ArrayList<Integer>();
//			
//			while ( indexPointerForColumn1 != numberOfElementsInColumn1
//					&& indexPointerForColumn2 != numberOfElementsInColumn2 ) {
//
//				// first get the elements for the respective pointers in each column
//				elementInColumn1 = tempColumn1.get( indexPointerForColumn1 );
//				elementInColumn2 = tempColumn2.get( indexPointerForColumn2 );
//				
//				// if both indexes point to the same value, then modify column 2,
//				// and increment both pointers
//				if ( elementInColumn1 == elementInColumn2 ) {
//															
//					// we add nothing to the merged list,
//					// and increment the pointers for both lists
////					if ( indexPointerForColumn1 < numberOfElementsInColumn1-1 ) 
//						indexPointerForColumn1++;
////					if ( indexPointerForColumn2 < numberOfElementsInColumn2-1 ) 
//						indexPointerForColumn2++;
//				}
//				else if ( elementInColumn1 < elementInColumn2 ) {
//
//					// add this element from column 1 to the mergedColumn (which will become
//					// the new column 2)
//					mergedColumn.add( new Integer( elementInColumn1 ));
//					
//					// move pointer down in first column
////					indexPointerForColumn1++;
////					if ( indexPointerForColumn1 < numberOfElementsInColumn1-1 ) 
//						indexPointerForColumn1++;
//				}
//				else {
//
//					// keep this element from column 2, by adding it to the mergedColumn
//					mergedColumn.add( new Integer( elementInColumn2 ));
//					
//					// move pointer down in second column
////					indexPointerForColumn2++;
////					if ( indexPointerForColumn2 < numberOfElementsInColumn2-1 ) 
//						indexPointerForColumn2++;
//				}
//			}
//			
//			// add (possibly) remaining elements:
//			if ( indexPointerForColumn2 <= numberOfElementsInColumn2-1 ) {
//				
//				while ( indexPointerForColumn2 <= numberOfElementsInColumn2-1 ) {
//					
//					elementInColumn2 = tempColumn2.get( indexPointerForColumn2 );
//					mergedColumn.add( new Integer( elementInColumn2 ));
//					indexPointerForColumn2++;
//				}
//			}
//			if ( indexPointerForColumn1 <= numberOfElementsInColumn1-1 ) {
//
//				while ( indexPointerForColumn1 <= numberOfElementsInColumn1-1 ) {
//					
//					elementInColumn1 = tempColumn1.get( indexPointerForColumn1 );
//					mergedColumn.add( new Integer( elementInColumn1 ));
//					indexPointerForColumn1++;
//				}
//			}
//			    	
//			coreductionColumns_.set( _colIndex2, mergedColumn );
	    }
	}

	public PersistenceMatrix( final RipsToPersistence _edgeList )  {
		
		int Zp_value = 0;

		this.edgeList_ = _edgeList;
		this.processData_ = edgeList_.getSettings();
		
		String strZpValue = processData_.getValidatedProcessParameter(
	            TDA.SETTING_ZP_VALUE );

		if ( !strZpValue.equalsIgnoreCase( TDA.DATA_SETTINGNOTFOUND ) ) {
		
			try {
			
				Zp_value = Integer.parseInt( strZpValue );
			}
			catch ( Exception e ) {
				
				// really nothing to do, since default Zp-value is already set to 0
			}
		}
		
		// logic for choosing the type of reduction (Z2 or Zp, p>2) to be used		
		if ( Zp_value > 0 ) {
			
			reductionImplementor_ = new ReductionImplementorZp( Zp_value );

			inverse_= new int[ Zp_value ];
			int factor;
			// Compute the multiplicative inverses
			inverse_[ 1 ] = 1;
			for ( int i=2; i<Zp_value; i++ ) {
				
				factor = 2;
				while ( ((i*factor) % Zp_value) != 1 ) factor++;
				inverse_[ i ] = factor;
			}
		}
		else {

			reductionImplementor_ = new ReductionImplementorZ2();
	//		reductionImplementor = new ReductionImplementorZ2Zp();
		}
	}
	
	public int getNumberOfColumns() {
		
		return this.columns_.size();
	}
	
	public int getInitialMatrixSize() {
		
		return this.initialMatrixSize_;
	}
	
	public ArrayList<Interval> getIntervals() {
		
		return this.intervals_;
	}
	
	public Column getColumn( final int _columnIndex ) {
		
		return this.columns_.get( _columnIndex );
	}
	
    public String toString() {
    	
    	return this.asStringFormat1().toString();
    }
	
	public String asStringFormat1() {
		
		StringBuffer reductionMatrixAsString = new StringBuffer( "" );
		Column tempColumn;
		
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

		reductionMatrixAsString.append( "'row list': " + " " );
		for ( int j=0; j<this.rowEdgeList_.size(); j++ ) {
			
			reductionMatrixAsString.append( this.rowEdgeList_.get( j ) + "  " );
		}
		reductionMatrixAsString.append( "\n" );
		
		return reductionMatrixAsString.toString();
	}
	
	public void addColumn( int _edgeIndex, SortedMap<Integer, Integer> _columnToAdd ) {
		
		int key;
		int value;
		
		this.associatedEdgesForColumnList_.add( new Integer( _edgeIndex ));		
		
		// Instead: use columnItem:
		this.columns_.add( new Column( _edgeIndex, _columnToAdd  ) );
	}
		
	public void addRowEdgeListEntry( final int _edgeIndex ) {
		
		this.rowEdgeList_.add( new Integer( _edgeIndex ));
	}
	
	// TODO: extract code from reduction
	protected void mergeColumns( final int _indexColumn1, final int indexColumn2 ) {
		
		// The merging eliminates entries that are common to both lists
		
		
	}
    

    public StringBuffer getCollectedFeedback() {

    	// save content before resetting
    	StringBuffer strBufferToReturn = new StringBuffer( this.strBufCollectFeedback );
    	
		this.strBufCollectFeedback = 
		    new StringBuffer( TDA.BUFFERLENGTH_STAT );
		
		return strBufferToReturn;
	}
	
//    public void setupCoreductionMatrix( final int _dim ) {
    public void setupCoreductionMatrix() {
    	
    	// TODO
//    	ArrayList<Integer> tempColumn;
//
////    	for ( int i=0; i<_dim; i++ ) {
//        for ( int i=0; i<associatedEdgesForColumnList_.size(); i++ ) {
//    		
//    		tempColumn = new ArrayList<Integer>();
////    		tempColumn.add( new Integer( i ) );
//    		tempColumn.add( new Integer( associatedEdgesForColumnList_.get( i ) ) );
//    		coreductionColumns_.add( tempColumn );
//    	}
//    	
//		if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_PERSISTENCEMATRIX ) {
//        	
//        	strBufCollectFeedback.append( "\n\nCoreduction columns setup: \n" 
//        			+ this.coreductionColumns_.toString() + "\n\n" );
//		}
    }
	
    public void reduce() {
    	
    	// hjs 6/3/2014 capture the size of the reduction matrix, to be reported later
    	initialMatrixSize_ = this.columns_.size();
    	
    	// forward the reduction to the appropriate handler
    	reductionImplementor_.reduce();
    	
    }
    
    public String getIntervalsAsString() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	
    	for ( int j=0; j<this.intervals_.size(); j++ ) {
    		
    		strIntervals.append( this.intervals_.get( j ).toString() + "\n" );
    	}
    	
    	return strIntervals.toString();
    }
    
    public String getIntervalsAsStringPlain() {
    	
    	StringBuffer strIntervals = new StringBuffer();
    	
    	for ( int j=0; j<this.intervals_.size(); j++ ) {
    		
    		strIntervals.append( this.intervals_.get( j ).toStringPlain() + "\n" );
    	}
    	
    	return strIntervals.toString();
    }
    
    public String getIntervalsAsStringComma() {
    	
    	StringBuffer strBufIntervals = new StringBuffer();
    	
    	for ( int j=0; j<this.intervals_.size(); j++ ) {
    		
    		strBufIntervals.append( this.intervals_.get( j ).toStringComma() + "\n" );
    	}
    	
    	return strBufIntervals.toString();
    }
    
    public String getIntervalsAsStringCommaComma() {
    	
    	StringBuffer strBufIntervals = new StringBuffer();
    	
    	for ( int j=0; j<this.intervals_.size(); j++ ) {
    		
    		strBufIntervals.append( this.intervals_.get( j ).toStringComma() + ", " );
    	}
    	
    	return strBufIntervals.toString();
    }
    
    // 
    // Methods to extract data for drawing persistence diagrams
    // 
    public String getIntervalsBirths() {
    	
    	StringBuffer strBufBirthValues = new StringBuffer();

    	if ( this.intervals_.size() > 0 ) {
	    	
    		strBufBirthValues.append( this.intervals_.get( 0 ).getBirth() );
	    	for ( int j=1; j<this.intervals_.size(); j++ ) {
	    		
	    		strBufBirthValues.append( ", " + this.intervals_.get( j ).getBirth() );
	    	}
    	}
    	
    	return strBufBirthValues.toString();
    }
    
    public String getIntervalsDeaths() {
    	
    	StringBuffer strBufDeathValues = new StringBuffer();

    	if ( this.intervals_.size() > 0 ) {
			
    		strBufDeathValues.append( this.intervals_.get( 0 ).getDeath() );
	    	for ( int j=1; j<this.intervals_.size(); j++ ) {
	    	
	    		if ( this.intervals_.get( j ).getDeath() == -1 ) {

	    			strBufDeathValues.append( ", INF" );
	    		}
	    		else {
	    		
	    			strBufDeathValues.append( ", " + this.intervals_.get( j ).getDeath() );
	    		}
	    	}
    	}
    	
    	return strBufDeathValues.toString();
    }
}
