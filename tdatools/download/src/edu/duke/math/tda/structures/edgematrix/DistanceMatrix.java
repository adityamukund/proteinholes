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
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;

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
public class DistanceMatrix {

	protected double[][] distances_;
	protected int numberOfVertices_;
	protected BufferedReader bufferedReader_;
	protected String strDistanceMatrixAsString_ = new String();

	protected int observedRowCount_;
	protected int observedColumnCount_;

	public DistanceMatrix ( 
			final String _distanceMatrixAsString ) throws Exception {

		// load the distance matrix from the string
		
		strDistanceMatrixAsString_ = new String( _distanceMatrixAsString );
    	bufferedReader_ = new BufferedReader( new StringReader( _distanceMatrixAsString ) );
		
        distances_ = loadDistanceMatrix();
	}

	public DistanceMatrix ( 
			final String _directory, 
			final String _fileName ) throws Exception {
		
		// load the distance matrix from the file
		
        File dataFile = new File(_directory, _fileName);
    
        if ( !dataFile.exists() ) {
            
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(Loading distance matrix) " +
                    "Cannot find the file: '" 
                    + _fileName + "' in directory '" + _directory + "'." );
        }
        
        bufferedReader_ = new BufferedReader(
                new FileReader( _directory + File.separator + _fileName ) );
        
        distances_ = loadDistanceMatrix();
	}
	
	protected double[][] loadDistanceMatrix() throws Exception {
			
		double[][] edgeLength;
		
		findMatrixDimension();
		this.numberOfVertices_ = this.observedRowCount_;
		
		// Set the pattern for the data parsing
		// Note we allow comments at the end of a line (via Pattern.COMMENTS)
		Pattern pattern = Pattern.compile( 
	          TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );
	  	
		try {
			
			int i;
			String strCurrentObservedValue;
		    double dblCurrentObservedValue;
		    String textline;
		    int suppliedPointDimension = observedRowCount_;
		    int observedPointDimensionOnCurrentLine;
	      
	
		    edgeLength = new double[ observedRowCount_ ][ observedRowCount_ ];
	  	
			// Now read the data
		    bufferedReader_ = new BufferedReader( new StringReader( strDistanceMatrixAsString_ ) );
			i = 0;
			while ( ( textline = bufferedReader_.readLine()) != null ) {
	          				
				// ---------------------------------------
				// Process the entries on the current line
				// ---------------------------------------
				
	          textline = textline.trim();
	          String[] lineComponents = pattern.split( textline );
	          observedPointDimensionOnCurrentLine = lineComponents.length;
	          
				// Ignore blank or commented out lines
				if ( observedPointDimensionOnCurrentLine > 0 && 
	                  !( textline.startsWith( "#" ) ) && !( textline.equals( "" ) ) ) {
	
					// We can never (even in debug mode) indicate more entries than
	                // there are in the file, so apply sanity check:
	                if ( observedPointDimensionOnCurrentLine < suppliedPointDimension ) {
	                      
	                	throw new TdaException( 
	                              TDA.ERROR_APP_USERINPUT,
	                              "(Loading distance matrix) " + "Line #" + (i+1) + 
						            " in the data for the distance matrix" +
	                              " contains fewer (namely, " + observedPointDimensionOnCurrentLine + 
	                              ") coordinate values than the expected " + this.numberOfVertices_ + "." );
	                }
	
				    // Next "Sanity" cross-check on the data: The rows need to have
				    // exactly as many data points as the separately user-supplied
				    // count - HOWEVER, we allow exceptions in debug mode, so we
                  	// can run multiple tests from a single data set
					if ( observedPointDimensionOnCurrentLine > suppliedPointDimension && !TDA.DEBUG ) {
					    
  					// Note: this allows for the data file to contain more data
  					// points than there are specified entries (the extra values are ignored),
  					// but we throw an exception when there are less data points
					    throw new TdaException( 
					            TDA.ERROR_APP_USERINPUT,
                              "(Loading distance matrix) " + "Line #" + (i+1) + 
					            " in the data for the distance matrix" +
					            " contains " + observedPointDimensionOnCurrentLine + 
					            " coordinate values instead of the expected " + this.numberOfVertices_ + "." );
					}
	                  
					// Use pointDimension as loop conditions
					for ( int j=0; j<suppliedPointDimension; j++ ) {
					    
					    strCurrentObservedValue = lineComponents[j];
					    
					    if ( strCurrentObservedValue.length() == 0 || 
					            strCurrentObservedValue.equalsIgnoreCase( "" ) ) {
					        
					        // flag invalid (missing) value
					        throw new TdaException( 
					                TDA.ERROR_APP_USERINPUT,
					                "Line '" + (i+1) + "'" +
  					            " in the data for the distance matrix is" +
					                " missing a value for entry '" + j + "'." );
					    }
					    else {
					        
					        // Check if non-numeric. If so, tell the user
					        try {
					            
					            dblCurrentObservedValue = 
					                Double.parseDouble( strCurrentObservedValue );
	    					    edgeLength[i][j] = dblCurrentObservedValue;
					        }
							catch (Exception e) {
							    
							    throw new TdaException( e,
							            TDA.ERROR_APP_USERINPUT,
						                "Line '" + (i+1) + "'" +
      					            " in the data for the distance matrix" +
      					            " contains an invalid value ('" +
						                strCurrentObservedValue +
						                "') for entry '" +
						                j + "'." );
							}
					    }
					}
                  
					i++;
				}
			}
			
			return edgeLength;
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
	    
	protected void findMatrixDimension() throws Exception {
        

    	// TODO: Fix this
    	// Sad kludge: can't seem to re-open the file after first read..., so store the contents
    	StringBuffer strBufDistanceMatrixAsString = new StringBuffer();
    	
        FileReader fileReader = null;
        StringTokenizer tokenizer;
        
        try {

            int rowCountInDataFile = 0;
            int columnCountInDataFile = 0;
                               
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
                            columnCountForCurrentLine != columnCountInDataFile ) {
                        
                        // This violates the stipulation that the observations data needs to be in
                        // rectangular form
                        throw new TdaException( 
                                TDA.ERROR_APP_USERINPUT, 
                                "(Loading distance matrix) " +
                                "The data file for the distance matrix" +
                                " needs to have the same number of entries for all rows " +
                                "(First discrepancy found at line " + 
                                (rowCountInDataFile+1) + 
                                ")." );
                        
                    }
                    strBufDistanceMatrixAsString.append( line + "\n" );
                    columnCountInDataFile = lineComponents.length;
                    
                    rowCountInDataFile++;
                }
            }

            // Check that the matrix is square
            if ( rowCountInDataFile != columnCountInDataFile ) {
            	
            	throw new TdaException( 
                        TDA.ERROR_APP_USERINPUT, 
                        "(Loading distance matrix) " +
                        "The data file for the distance matrix" +
                        " needs to have the same number of rows as columns." );
            }

            strDistanceMatrixAsString_ = strBufDistanceMatrixAsString.toString();
            
            
            // finally assign results to data members
            observedRowCount_ = rowCountInDataFile;
            observedColumnCount_ = columnCountInDataFile;
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

	
	public double[][] getDistances() {
	                
	     return distances_;
	}
	
	// TODO: get rid of one method
	public int getNumberOfVertices() {
	
		return this.numberOfVertices_;
	}
	
	public int getNumberOfRows() {
	
		return this.numberOfVertices_;
	}
}
