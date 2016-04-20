/*
 * Created June 27, 2013
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
package edu.duke.math.tda.structures.edgematrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;

import edu.duke.math.tda.structures.EdgeFactory;
import edu.duke.math.tda.structures.EdgeI;
import edu.duke.math.tda.structures.PointRn;
import edu.duke.math.tda.structures.metric.MetricI;
import edu.duke.math.tda.structures.pointcloud.PointCloudInRn;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Documents 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 3, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class SparseMatrix {
	
	// sparse matrix implementation
	protected SortedMap<Integer, Double> sparseEdgeLengths_ = new TreeMap<Integer, Double>();
	protected int numberOfVertices_;
	// map the (sorted) edges from their row-col based index to their sequential number
	protected int[] edgeIndexes_;
	protected SortedMap<Integer, Integer> sparseEdgeIndexes_ = new TreeMap<Integer, Integer>();
	

	protected List<EdgeI> edges_ = new ArrayList<EdgeI>();
	
	protected BufferedReader bufferedReader_;
	protected String strSparseMatrixAsString_ = new String();

	protected int observedRowCount_;
	protected int observedColumnCount_;
	
	protected EdgeFactory edgeFactory_;
	protected Settings processData_;

	int minIndexAdjustment_;

	public SparseMatrix ( 
			final String _distanceMatrixAsString,
			Settings _processData ) throws Exception {

		// we need the process data, so we can pass the info on to the edgeFactory
		processData_ = _processData;

		// load the sparse matrix from the string
		strSparseMatrixAsString_ = new String( _distanceMatrixAsString );
    	bufferedReader_ = new BufferedReader( new StringReader( _distanceMatrixAsString ) );
		
        sparseEdgeLengths_ = loadSparseMatrix();
	}

	public SparseMatrix ( 
			final String _directory, 
			final String _fileName,
			Settings _processData ) throws Exception {
		
		// we need the process data, so we can pass the info on to the edgeFactory
		processData_ = _processData;
		
		// load the sparse matrix from the specified file
        File dataFile = new File(_directory, _fileName);
    
        if (!dataFile.exists()) {
            
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(Loading sparse matrix) " +
                    "Cannot find the file: '" 
                    + _fileName + "' in directory '" + _directory + "'." );
        }
        
        bufferedReader_ = new BufferedReader(
                new FileReader( _directory + File.separator + _fileName ) );
        
        sparseEdgeLengths_ = loadSparseMatrix();
	}
	
	protected SortedMap<Integer, Double> loadSparseMatrix() throws Exception {
		
		SortedMap<Integer, Double> sparseEdges = new TreeMap<Integer, Double>();
		
		findMatrixDimension(); // _directory, _fileName );
		edgeIndexes_ = new int [ this.observedRowCount_ ];
		
		edgeFactory_ = new EdgeFactory( processData_ );
		
		// Set the pattern for the data parsing
		// Note we allow comments at the end of a line (via Pattern.COMMENTS)
		Pattern pattern = Pattern.compile( 
	          TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );
	
		try {
			
			int i;
			int row = -1;
			int col = -1;
			double dblCurrentObservedValue = -1;
			String textline;
			int entriesOnCurrentLine;
			int tmpVertexIndex;
			int tmpEdgeIndex = 1;
	      
			// Now read the data
			bufferedReader_ = new BufferedReader( new StringReader( strSparseMatrixAsString_ ) );
			i = 0;
			while ( ( textline = bufferedReader_.readLine()) != null ) {
	          				
				// ---------------------------------------
				// Process the entries on the current line
				// ---------------------------------------
				
	        	textline = textline.trim();
	        	String[] lineComponents = pattern.split( textline );
	        	entriesOnCurrentLine = lineComponents.length;
	          
				// Ignore blank or commented out lines
				if ( entriesOnCurrentLine > 0 && 
	                  !( textline.startsWith( "#" ) ) && !( textline.equals( "" ) ) ) {
	
					// We can never (even in debug mode) indicate more entries than
					// there are in the file, so apply sanity check:
					if ( entriesOnCurrentLine != 3 ) {
					      
						throw new TdaException( 
					              TDA.ERROR_APP_USERINPUT,
					              "(Loading sparse matrix) " + "Line #" + (i+1) + 
					            " in the data for the sparse matrix" +
					          " does not contain the 3 expected values (instead: " + entriesOnCurrentLine + 
					          ")." );
					}
							        
			        // Check if non-numeric. If so, tell the user
                    // hjs 7/19/2013  strange fix necessary for the api-mode's call
                    // where the sparse matrix is loaded from a 2-dim double array (Matlab)
			        try {

			        	row = (int) Math.round( Double.parseDouble( lineComponents[ 0 ] ));
			        	col = (int) Math.round( Double.parseDouble( lineComponents[ 1 ] ));
//			        	row = Integer.parseInt( lineComponents[ 0 ] );
//			        	col = Integer.parseInt( lineComponents[ 1 ] );
			            dblCurrentObservedValue = 
			                Double.parseDouble( lineComponents[ 2 ] );
			            
			            if ( row > col ) {
			            	
			            	// swap the vertex indices
			            	tmpVertexIndex = row;
			            	row = col;
			            	col = tmpVertexIndex;
			            }
			            
			            // adjust for min index being greater than 0
			            if ( minIndexAdjustment_ > 0 ) {

			            	row = row - minIndexAdjustment_;
			            	col = col - minIndexAdjustment_;
			            }
			        }
					catch ( Exception e ) {
					    
					    throw new TdaException( e,
					            TDA.ERROR_APP_USERINPUT,
				                "Line '" + (i+1) + "'" +
  					            " in the data for the sparse matrix" +
  					            " contains an invalid value ('" +
				                row + "  " + col + "  " + 
				                dblCurrentObservedValue + "'.\n" );
					}
												
		            tmpEdgeIndex = row * this.observedRowCount_ + col;
		            sparseEdges.put( tmpEdgeIndex,
		            		dblCurrentObservedValue );

		            edges_.add( edgeFactory_.createEdge( row, col, dblCurrentObservedValue ));
		            
		            if ( sparseEdgeIndexes_.containsKey( tmpEdgeIndex )) {
		            	
		            	throw new TdaException(
					            TDA.ERROR_APP_USERINPUT,
				                "Line '" + (i+1) + "'" +
  					            " in the data for the sparse matrix" +
  					            " specifies another value for the same " +
  					            "row-column combination (namely '" +
				                row + ", " + col + "').\n" );
		            }
		            
		            sparseEdgeIndexes_.put( tmpEdgeIndex, i );
	                  
	                i++;
				}
			}
			
			return sparseEdges;
		}
		catch (IOException e) {
		   
		    throw new Exception( e );
		}		
		catch (TdaException e) {
	
			throw new TdaException( e );
		}		
		finally {
			try {
								
				if (bufferedReader_ != null ) bufferedReader_.close();
			} 
			catch( IOException e ) {
	          // nothing else to do?
	      };
		}
	}
	    
	// preprocess the raw input data
	protected void findMatrixDimension() throws Exception {        

    	// TODO: Fix this
    	// Sad kludge: can't seem to re-open the file after first read..., so store the contents
    	StringBuffer strBufSparseMatrixAsString = new StringBuffer();
    	
        FileReader fileReader = null;
        StringTokenizer tokenizer;
        
        int maxIndex = -1;
        int minIndex = 1000; // this is not bullet-proof; if our min at the end is still not
        // smaller than this, then throw an exception, and let user deal with their mess

        double component0;
        double component1;
        
        try {

            int rowCountInDataFile = 0;
            int columnCountInDataFile = 0;
            String itemDelimiter = TDA.DELIMITER_DEFAULT_MATRIXDATA; //(currently: \t)
                                                               
            boolean columnsCounted = false;
            String line;
            // Now start reading the data
            while ( ( line = bufferedReader_.readLine()) != null ) {
                
                
                // ---------------------------------------
                // Process the entries on the current line
                // ---------------------------------------

                // Set the pattern for the data parsing
                Pattern pattern = Pattern.compile( 
                        TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );
                
                line = line.trim();
                String[] lineComponents = pattern.split( line );
                int columnCountForCurrentLine = lineComponents.length;
                
                
                // this allows us to have a blank line or one that starts with #
                // without upsetting the observation loading
                if ( !line.startsWith( "#" ) && columnCountForCurrentLine > 1 ) {
                    
                    if ( columnCountInDataFile > 0 && 
                            columnCountForCurrentLine != 3 ) {
                        
                        // This violates the stipulation that the observations data needs to be in
                        // rectangular form
                        throw new TdaException( 
                                TDA.ERROR_APP_USERINPUT, 
                                "(Loading sparse matrix) " +
                                "The data file for the sparse matrix" +
                                " needs to have 3 entries for all rows " +
                                "(First discrepancy found at line " + 
                                (rowCountInDataFile+1) + 
                                ", '" + line +
                                "')." );
                        
                    }
                    strBufSparseMatrixAsString.append( line + "\n" );
                    columnCountInDataFile = lineComponents.length;

                    // hjs 7/19/2013  strange-looking fix necessary for the api-mode's call
                    // where the sparse matrix is loaded from a 2-dim doubles array (Matlab)
			        try {

			        	component0 = Math.round( Double.valueOf( lineComponents[ 0 ] ));
			        	component1 = Math.round( Double.valueOf( lineComponents[ 1 ] ));

//	                    if ( Integer.valueOf( lineComponents[ 0 ] ) > maxIndex ) {
	                    if ( Math.round( component0 ) > maxIndex ) {

//	                    	maxIndex = Integer.parseInt( lineComponents[ 0 ] );
	                    	maxIndex = (int) component0;
	                    }
//	                    else if ( Integer.valueOf( lineComponents[ 1 ] ) > maxIndex ) {
//	                    else 
	                    if ( Math.round( component1 ) > maxIndex ) {

//	                    	maxIndex = Integer.parseInt( lineComponents[ 1 ] );
	                    	maxIndex = (int) component1;
	                    }
	                    
	                    if ( Math.round( component0 ) < minIndex ) {

	                    	minIndex = (int) component0;
	                    }
	                    
	                    if ( Math.round( component1 ) < minIndex ) {

	                    	minIndex = (int) component1;
	                    }
			        }
					catch (Exception e) {
					    
					    throw new TdaException( e,
					            TDA.ERROR_APP_USERINPUT,
				                "Line '" + line + "'" +
					            " in the data for the sparse matrix" +
					            " contains an invalid index entry: '" +
					            lineComponents[ 0 ] + "' and '" + 
					            lineComponents[ 1 ] + "' need to be integers.\n" );
					}
                    
                    rowCountInDataFile++;
                }
            }

            if ( minIndex > 999 ) {
			    
			    throw new TdaException( TDA.ERROR_APP_USERINPUT,
		                "The sparse matrix input relies on the vertex indices to start at close values, "
		                + "which we 'correct' internally to start at 0. The values you provide are "
		                + "larger than 999, which we refuse to handle. Sorry -- You will need to "
		                + "fix this before using "
		                + TDA.APPLICATION_NAME
		                + ".\n" );
            }
            else {
            	
            	// hjs 10/25/2013 Add adjustment for min vertex indexes being greater than 0
            	// TODO:
            	minIndexAdjustment_ = minIndex;
            }
            
            this.strSparseMatrixAsString_ = strBufSparseMatrixAsString.toString();
            
            // TODO: clean-up
            // finally assign results to data members
            this.observedRowCount_ = maxIndex+1;
            this.observedColumnCount_ = maxIndex+1;
            
            // TODO? (this is not a vertex count)
    		this.numberOfVertices_ = maxIndex+1;
        }
        catch (IOException e) {
               
            throw new Exception( e );
        }       
        catch (TdaException e) {

            throw new TdaException( e );
        }       
        finally {
                                
//            if (fileReader != null ) fileReader.close();
//            if (bufferedReader != null ) bufferedReader.close();
        }
    }
	
	public SortedMap<Integer, Double> getSparseEdgeLengths() {
		
		return sparseEdgeLengths_; 
	}
	
	public int getMinIndexAdjustment() {
	
		return minIndexAdjustment_;
	}
	
	public void setMinIndexAdjustment( int _minIndexAdjustment ) {
	
		minIndexAdjustment_ = _minIndexAdjustment;
	}
	
	public int getNumberOfVertices() {
	
		return numberOfVertices_;
	}
	
	public int getDimension() {
	
		return sparseEdgeLengths_.size();
	}

	public List<EdgeI> getEdges() {
		
		return edges_;
	}
	
	public int getSparseEdgeIndex( int _row, int _col ) {
		
		return sparseEdgeIndexes_.get( _row * this.observedColumnCount_ + _col );
	}
	
	public int getSparseEdgeIndex( int _index ) {
		
		return sparseEdgeIndexes_.get( _index );
	}
}
