/*
 * Created January 2013
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

package edu.duke.math.tda.structures.pointcloud;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.duke.math.tda.structures.PointRn;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * PointCloudI is an interface for the PointCloud hierarchy
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created January 2013
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */


public abstract class PointCloud implements PointCloudI {

	protected Settings processData_; // Not yet used!!

	// variables used (only) for loading a point cloud from a file, and for potential
	// cross-checking this info with the info that the user entered in the settings file
	protected int observedRowCount;	// the number of coordinates of each point
	protected int observedColumnCount; // the number of points in the file
	protected int pointDimension;
	
	// variables used in the load-from-file process
	protected double[][] dblLoadedPoints;
	protected int numberOfPointsLoaded;
	
	protected BufferedReader bufferedReader_;
	protected String strPointCloudAsString_ = new String();
	

	public PointCloud() {
		
	}
	
	public PointCloud( final String _strPointCloudFromString ) throws Exception {

        if ( _strPointCloudFromString == null ) {
            
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(PointCloud[String] Loading point cloud) " +
                    "The supplied data for the point cloud cannot be empty!" );
        }

		strPointCloudAsString_ = new String( _strPointCloudFromString );
    	bufferedReader_ = new BufferedReader( new StringReader( strPointCloudAsString_ ) );
	}
	
	public PointCloud( final String _directory, final String _fileName ) throws Exception {
		       
        File dataFile = new File(_directory, _fileName);
    
        if (!dataFile.exists()) {
            
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(PointCloud[dir+file] Loading point cloud) " +
                    "Cannot find the file: '" 
                    + _fileName + "' in directory '" + _directory + "'." );
        }
        
        bufferedReader_ = new BufferedReader(
                new FileReader( _directory + File.separator + _fileName ));
	}
	
	@Override
	public ArrayList<PointRn> getPoints() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	// load points from a text file:
	// each point is on a row, with coordinates comma (or whitespace) -delimited
	protected double[][] loadPointCloudFromFile() throws Exception {
        		
        // Find (and check) the number of data rows and columns in the specified file
        findDataRowAndColumnCounts();
        	// _directoryName, _fileName );

        // Find the number of data rows (OBSERVATIONS) in the specified file
        int lineCountInObsFile = observedRowCount;
        
        int observedPointDimension = observedColumnCount;
        this.pointDimension = observedColumnCount;
               
        // Set the pattern for the data parsing
        // Note we allow comments at the end of a line (via Pattern.COMMENTS)
        Pattern pattern = Pattern.compile( 
                TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );

		try {
		
//			if ( !dataFile.exists() ) {
//			    
//				throw new CompTopoException( 
//				        COMPTOPO.ERROR_COMPTOPO_USERINPUT, 
//                        "(Loading point cloud) " +
//				        "Cannot find the data file: '" 
//				        + _fileName + "' in directory '" + _directoryName + "'." );
//			}
			
			int i;
			String strCurrentObservedValue;
		    double dblCurrentObservedValue;
		    String textline;
            int suppliedPointDimension = observedPointDimension; //pointDimension;
            int observedPointDimensionOnCurrentLine;
            
            numberOfPointsLoaded = lineCountInObsFile;
            
            dblLoadedPoints = 
                new double[ numberOfPointsLoaded ][ pointDimension ];
            
			// Now read the data
			i = 0;
        	bufferedReader_ = new BufferedReader( new StringReader( strPointCloudAsString_ ) );
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
                                "(Loading point cloud) " + "Point #" + (i+1) + 
                                " in the supplied data contains fewer (namely, " + 
                                observedPointDimensionOnCurrentLine + 
                                ") coordinate values than the expected " + pointDimension + "." );
//                                "(Loading point cloud) " + "Point #" + (i+1) + 
//                                " in data file '" + _fileName +
//                                "' contains fewer (namely, " + observedPointDimensionOnCurrentLine + 
//                                ") coordinate values than the expected " + pointDimension + "." );
                    }

				    // Next "Sanity" cross-check on the data: The data needs to have
				    // exactly as many data points as the separately user-supplied
				    // count - HOWEVER, we allow exceptions in debug mode, so we
                    // can run multiple tests from a single data set
					if ( observedPointDimensionOnCurrentLine > suppliedPointDimension && !TDA.DEBUG ) {
					    
    					// Note: this allows for the data file to contain more data
    					// points than there are specified variables (the extra values are ignored),
    					// but we throw an exception when there are less data points
					    throw new TdaException( 
					            TDA.ERROR_APP_USERINPUT,
                                "(Loading point cloud) " + "Point #" + (i+1) + 
					            " in the supplied data contains " + 
					            observedPointDimensionOnCurrentLine + 
					            " coordinate values instead of the expected " + pointDimension + "." );
//                                "(Loading point cloud) " + "Point #" + (i+1) + 
//					            " in data file '" + _fileName +
//					            "' contains " + observedPointDimensionOnCurrentLine + 
//					            " coordinate values instead of the expected " + pointDimension + "." );
					}
                    
					// Use pointDimension as loop conditions
					for ( int j=0; j<suppliedPointDimension; j++ ) {
					    
					    strCurrentObservedValue = lineComponents[j];
					    
					    if ( strCurrentObservedValue.length() == 0 || 
					            strCurrentObservedValue.equalsIgnoreCase( "" ) ) {
					        
					        // flag invalid (missing) observation value
					        throw new TdaException( 
					                TDA.ERROR_APP_USERINPUT,
					                "Line '" + (i+1) + "' of the data " +
                                    " is missing a value for coordinate '" + j + "'." );
//					                "Line '" + (i+1) + "' of the data file '" +
//                                    _fileName + "' is " +
//					                "missing a value for coordinate '" + j + "'." );
					    }
					    else { //if ( strCurrentObservedValue.length() > 1  ) {
					        
					        // Check if non-numeric. If so, tell the user
					        try {
					            
					            dblCurrentObservedValue = 
					                Double.parseDouble( strCurrentObservedValue );
	    					    dblLoadedPoints[i][j] = dblCurrentObservedValue;
					        }
							catch (Exception e) {
							    
							    throw new TdaException( e,
							            TDA.ERROR_APP_USERINPUT,
						                "Line (point) '" + (i+1) + "' of the data " +
                                        "contains an invalid value ('" +
						                strCurrentObservedValue +
						                "') for coordinate '" +
						                j + "'." );
//					            COMPTOPO.ERROR_COMPTOPO_USERINPUT,
//				                "Line (point) '" + (i+1) + "' of the data file '" +
//                                _fileName + "' contains an invalid value ('" +
//				                strCurrentObservedValue +
//				                "') for coordinate '" +
//				                j + "'." );
							}
					    }
					}
                    
                    i++;
				}
			}

			return dblLoadedPoints;
		}
		catch (IOException e) {
		   
		    throw new Exception( e );
		}		
		catch (TdaException e) {

			throw new TdaException( e );
		}		
		finally {
			try { 
								
				// TODO: put this in a destructor
				if (bufferedReader_ != null ) { 
					
					bufferedReader_.close();
				}
			} 
			catch( IOException e ) {
                // nothing else to do?
            };
		}
    }
    
    protected void findDataRowAndColumnCounts() 
				throws Exception {
//            final String _directory, final String _fileName ) throws Exception {

    	// TODO: Fix this
    	// Sad kludge: can't seem to re-open the file after first read..., so store the contents
    	StringBuffer strBufPointCloudAsString = new StringBuffer();
        FileReader fileReader = null;
        
        try {

            int rowCountInDataFile = 0;
            int columnCountInDataFile = 0;

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
                // without upsetting the data loading
//                if ( !line.startsWith( "#" ) && columnCountForCurrentLine > 1 ) {
                // hjs 5/7/2014	Allow single column, for processing of 1-dim. points
                if ( !line.startsWith( "#" ) && columnCountForCurrentLine > 0 ) {
                    
                    if ( columnCountInDataFile > 0 && 
                            columnCountForCurrentLine != columnCountInDataFile ) {
                        
                        // This violates the stipulation that the observations data needs to be in
                        // rectangular form
                        throw new TdaException( 
                                TDA.ERROR_APP_USERINPUT, 
                                "(Loading point cloud) " +
                                "The data for the point cloud " +
                                " needs to have the same number of coordinates for all points " +
                                "(First discrepancy found at line " + 
                                (rowCountInDataFile+1) + 
                                ")." );
//                                COMPTOPO.ERROR_COMPTOPO_USERINPUT, 
//                                "(Loading point cloud) " +
//                                "The data file '" 
//                                + _fileName + "' in directory '" + _directory + "'" +
//                                " needs to have the same number of coordinates for all points " +
//                                "(First discrepancy found at line " + 
//                                (rowCountInDataFile+1) + 
//                                ")." );
                        
                    }
                    
                    strBufPointCloudAsString.append( line + "\n" );
                    columnCountInDataFile = lineComponents.length;
                    
                    rowCountInDataFile++;
                }
            }

            // finally assign results to data members
            observedRowCount = rowCountInDataFile;
            observedColumnCount = columnCountInDataFile;
            
            strPointCloudAsString_ = strBufPointCloudAsString.toString();
        }
        catch (IOException e) {
               
            throw new Exception( e );
        }       
        catch (TdaException e) {

            throw new TdaException( e );
        }       
        finally {
                                
            if (fileReader != null ) fileReader.close();
        }
    }
}
