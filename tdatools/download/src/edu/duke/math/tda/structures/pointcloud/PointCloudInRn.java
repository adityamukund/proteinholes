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

package edu.duke.math.tda.structures.pointcloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.duke.math.tda.structures.PointRn;
import edu.duke.math.tda.structures.metric.MetricI;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * PointCloud class for representing a set of points in R^n
 * (i.e., currently our points are in Cartesian space R^n)
 * <p><strong>Details:</strong> <br>
 * 
 * Constructors from other point cloud, or from plain list of points.
 * Alternatively, a point cloud can be built up one-by-one by adding points 
 * individually.
 *  
 * <p><strong>Change History:</strong> <br>
 * Created October 2012
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class PointCloudInRn extends PointCloud {
	
	// ********
	// Note: to use the cross-checking, we need to use the 'processData' mechanism
	// CAREFUL: need to make sure that processData is a valid reference!!
//	protected Settings processData_; // Not yet used!!

	protected int numberOfPoints_; // TODO: replace by checking array size, unless used 
	// for cross-checking?
	protected ArrayList<PointRn> points_ = new ArrayList<PointRn>();
	
	protected int pointDimension_ = TDA.UNKNOWNPOINTDIMENSION;    
	    
	public PointCloudInRn() {
		
		this.numberOfPoints_ = 0;
	}
	    
	public PointCloudInRn( final PointCloudInRn _orig ) {
		
		this.points_ = new ArrayList<PointRn>( _orig.points_ );
		this.numberOfPoints_ = _orig.numberOfPoints_;
	}
	
	public PointCloudInRn( final ArrayList<PointRn> _points ) {
		
		this.points_ = new ArrayList<PointRn>( points_ );
		this.numberOfPoints_ = _points.size();
	}
	
	public PointCloudInRn( final String _directory, final String _fileName ) throws Exception {
		
		super( _directory, _fileName );
		
		setupPointCloudInRn();	
	}
	
	public PointCloudInRn( final String _strPointCloudFromString ) throws Exception {
		
		super( _strPointCloudFromString );

		setupPointCloudInRn();		
	}
	
	public PointCloudInRn( final String _directory, 
							final String _fileName, 
							final int _M ) throws Exception {
		
		super( _directory, _fileName );
		
		// Note: all set-up is done within the load method
		this.loadPointCloudFromTimeSeriesFile( _directory, _fileName, _M );
	}
	
	protected void setupPointCloudInRn() throws Exception {
		
		double[][] dblLoadedPoints;
		
		// Note: all set-up is done within the load method
		dblLoadedPoints = loadPointCloudFromFile();

		pointDimension_ = dblLoadedPoints[0].length;
		
		// "post" the dimension of the points
		// Can't do this until we have access to processData_
//		processData_.setDynamicProcessParameter( 
//				COMPTOPO.DATA_POINTDIMENSION,
//				Integer.toString( dblLoadedPoints[ 0 ].length ) );
		
		PointRn pointToAdd;
		this.points_ = new ArrayList<PointRn>();
		// Now create the array of points from the loaded data:
		for ( int j=0; j<numberOfPointsLoaded; j++ ) {
			
			pointToAdd = new PointRn( dblLoadedPoints[ j ] );
			this.points_.add( pointToAdd );
		}

		this.numberOfPoints_ = this.points_.size();	
	}
	    
	public int getNumberOfPoints() {
		
		return points_.size();
	}
	
	public long getPointDimension() {
		
		return pointDimension_;
	}
	
	public long getPointCloudSize() {
		
		return points_.size();
	}
	
	public ArrayList<PointRn> getPoints() {
		
		return points_;
	}
	
	public String PointAsString( int index_ ) {
		
		return "nyi";
	}
	    
	public void addPoint( PointRn _p ) {
		
		points_.add( _p );
	}

	public PointRn getPoint( int index_ ) {
		
		return points_.get( index_ );
	}
	
	public StringBuffer asSparseMatrix( final MetricI _metric,
			final double _distanceBound ) throws Exception {

		PointRn p1;
    	PointRn p2;
		StringBuffer pointCloudAsString = new StringBuffer( 
				"# sparse matrix generated from point cloud\n\n" );
		int numberOfPoints = points_.size();
		double distanceBetweenPoints;
		
		for ( int i=0; i<numberOfPoints; i++ ) {
    		
			p1 = points_.get( i );
			
    		for ( int j=i+1; j<numberOfPoints; j++ ) {
    			
    	    	p2 = points_.get( j );
    	    	distanceBetweenPoints = _metric.computeDistance( p1, p2 );
    	    	
    	    	if ( distanceBetweenPoints < _distanceBound ) {

    	    		// add to return string
    	    		pointCloudAsString.append( i + "  " + j + "  " + distanceBetweenPoints + "\n" );
    	    	}
    		}
		}
		
		return pointCloudAsString;
	}
	
	public StringBuffer asString() {
		
		StringBuffer pointCloudAsString = new StringBuffer( "" );
		
		for ( int i=0; i< points_.size(); i++ ) {
			
			pointCloudAsString.append( points_.get( i ).asString() );
			pointCloudAsString.append( "\n" );
		}
		
		return pointCloudAsString;
	}
	
	// for writing out to file, without any formatting
	public StringBuffer asStringBasic() {
		
		StringBuffer pointCloudAsString = new StringBuffer( "" );
		
		for ( int i=0; i< points_.size(); i++ ) {
			
			pointCloudAsString.append( points_.get( i ).asStringBasicTab() );
			pointCloudAsString.append( "\n" );
		}
		
		return pointCloudAsString;
	}
	
	public StringBuffer asStringWithIndex() {
		
		StringBuffer pointCloudAsString = new StringBuffer( "" );
		
		for ( int i=0; i< points_.size(); i++ ) {

			pointCloudAsString.append( "index =" + Integer.toString( i ) + ", " );
			pointCloudAsString.append( points_.get( i ).asString() );
			pointCloudAsString.append( "\n" );
		}
		
		return pointCloudAsString;
	}

	
	// load a series of time points f(0), f(1), f(2), ..., f(n) from a text file,
	// then create a point cloud X with points
	//        (f(0), f(1), ..., f(M))
	//        (f(1), f(2), ..., f(M+1))
	//        (f(2), f(3), ..., f(M+2))
	//	      ...
	//	      (f(n-M), f(n-M+1), ..., f(n))
    //		  where M is an integer << n
    //
    protected void loadPointCloudFromTimeSeriesFile( final String _directoryName,
            final String _fileName, final int _M ) throws Exception {

        // Find (and check) the number of data rows and columns in the specified file
        findDataRowAndColumnCounts();

        // Find the number of data rows in the specified file
        int lineCountInDataFile = observedRowCount; // this needs to be 1!!
        
        if ( lineCountInDataFile != 1 ) {
        	
        	// throw an exception
        	
        	
        }
        
        int observedNbrTimeSeriesPoints = observedColumnCount;
        this.pointDimension = _M;
               
        // Set the pattern for the data parsing
        // Note we allow comments at the end of a line (via Pattern.COMMENTS)
        Pattern pattern = Pattern.compile( 
                TDA.PATTERN_PARSING_WHITESPACE, Pattern.COMMENTS );
        
		final File dataFile = new File( _directoryName, _fileName);
		FileReader fileReader = null;

		try {
		
			if ( !dataFile.exists() ) {
			    
				throw new TdaException( 
				        TDA.ERROR_APP_USERINPUT, 
                        "(Loading point cloud) " +
				        "Cannot find the data file: '" 
				        + _fileName + "' in directory '" + _directoryName + "'." );
			}
			
			
			int i;
			String strCurrentObservedValue;
		    double dblCurrentObservedValue;
		    String textline;
            int nbrTimeSeriesPoints = observedNbrTimeSeriesPoints; //pointDimension;
            int observedTimeSeriesPointsOnLine;
            
            // compute the array dimension for the number of points to create:
            numberOfPointsLoaded = lineCountInDataFile;
            
            double[] timeSeriesPoints = new double[ nbrTimeSeriesPoints ];
            
			// Set up the file loading:
			bufferedReader_ = new BufferedReader(
			        new FileReader(_directoryName + File.separator + _fileName));
					
			// Now read the data (single line from file)
			i = 0;
			while ( ( textline = bufferedReader_.readLine()) != null ) {
                				
				// ---------------------------------------
				// Process the entries on the current line
				// ---------------------------------------
				
                textline = textline.trim();
                String[] lineComponents = pattern.split( textline );
                observedTimeSeriesPointsOnLine = lineComponents.length;
                
				// Ignore blank or commented out lines
				if ( observedTimeSeriesPointsOnLine > 0 && 
                        !( textline.startsWith( "#" ) ) && !( textline.equals( "" ) ) ) {

                    // We can never (even in debug mode) indicate more entries than
                    // there are in the file, so apply sanity check:
                    if ( observedTimeSeriesPointsOnLine < nbrTimeSeriesPoints ) {
                        
                        throw new TdaException( 
                                TDA.ERROR_APP_USERINPUT,
                                "(Loading time series) " + "The time series supplied " + 
                                " in data file '" + _fileName +
                                "' contains fewer (namely, " + observedTimeSeriesPointsOnLine + 
                                ") values than the expected number (namely, " + nbrTimeSeriesPoints + ")." );
                    }

				    // Next "Sanity" cross-check on the data: The data needs to have
				    // exactly as many data points as the separately user-supplied
				    // count - HOWEVER, we allow exceptions in debug mode, so we
                    // can run multiple tests from a single data set
					if ( observedTimeSeriesPointsOnLine > nbrTimeSeriesPoints && !TDA.DEBUG ) {
					    
    					// Note: this allows for the data file to contain more data
    					// points than there are specified variables (the extra values are ignored),
    					// but we throw an exception when there are less data points
					    throw new TdaException( 
					            TDA.ERROR_APP_USERINPUT,
                                "(Loading time series) " + "The time series supplied" + 
					            " in data file '" + _fileName +
					            "' contains only " + observedTimeSeriesPointsOnLine + 
					            " values, so we cannot create points of dimension " + 
					            pointDimension + "." );
					}
                    
					// Use pointDimension as loop conditions
					for ( int j=0; j<nbrTimeSeriesPoints; j++ ) {
					    
					    strCurrentObservedValue = lineComponents[j];
					    
					    if ( strCurrentObservedValue.length() == 0 || 
					            strCurrentObservedValue.equalsIgnoreCase( "" ) ) {
					        
					        // flag invalid (missing) observation value
					        throw new TdaException( 
					                TDA.ERROR_APP_USERINPUT,
					                "Line '" + (i+1) + "' of the data file '" +
                                    _fileName + "' is " +
					                "missing a value for coordinate '" + j + "'." );
					    }
					    else { //if ( strCurrentObservedValue.length() > 1  ) {
					        
					        // Check if non-numeric. If so, tell the user
					        try {
					            
					            dblCurrentObservedValue = 
					                Double.parseDouble( strCurrentObservedValue );
					            timeSeriesPoints[ j ] = dblCurrentObservedValue;
					        }
							catch (Exception e) {
							    
							    throw new TdaException( e,
							            TDA.ERROR_APP_USERINPUT,
						                "Time series value '" + (i+1) + "' of the data file '" +
                                        _fileName + "' contains an invalid value ('" +
						                strCurrentObservedValue +
						                "') for coordinate '" +
						                j + "'." );
							}
					    }
					}
                    
                    i++;
				}
			}
			
			
			PointRn pointToAdd;
			this.points_ = new ArrayList<PointRn>();
            double[] dblPointCoordinates = new double[ pointDimension ];
			
			// now that we have the time series loaded, we can compute the point cloud
			for ( int k=0; k<nbrTimeSeriesPoints-pointDimension+1; k++ ) {
				
				// set the point's coordinates
				for ( int l=0; l<pointDimension; l++ ) {
					
					dblPointCoordinates[ l ] = timeSeriesPoints[ k+l ];
				}
				
				// create the point, and add it to the set of points
				pointToAdd = new PointRn( dblPointCoordinates );
				this.points_.add( pointToAdd );
			}

			this.numberOfPoints_ = this.points_.size();
		}
		catch (IOException e) {
		   
		    throw new Exception( e );
		}		
		catch (TdaException e) {

			throw new TdaException( e );
		}		
		finally {
			try { 
								
				if (fileReader != null ) fileReader.close();
				if (bufferedReader_ != null ) bufferedReader_.close();
			} 
			catch( IOException e ) {
                // nothing else to do?
            };
		}
    }
}
