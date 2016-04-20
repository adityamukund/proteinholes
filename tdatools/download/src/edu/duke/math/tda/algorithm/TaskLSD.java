/*
 * Created Feb 26, 2013
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
package edu.duke.math.tda.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.duke.math.tda.structures.RipsToPersistence;
import edu.duke.math.tda.structures.PointRn;
import edu.duke.math.tda.structures.PointRnLocal;
import edu.duke.math.tda.structures.edgematrix.EdgeMatrixAsMultiDimArray;
import edu.duke.math.tda.structures.edgematrix.EdgeMatrixI;
import edu.duke.math.tda.structures.metric.MetricFactory;
import edu.duke.math.tda.structures.metric.MetricI;
import edu.duke.math.tda.structures.pointcloud.PointCloudInRn;
import edu.duke.math.tda.structures.results.ResultsContainerI;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaError;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Code for the Least-spherical-distance (LSD) task. 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Feb 26, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class TaskLSD extends Task {

	// Underlying objects
	PointCloudInRn pointCloud_;
	PointRn centerPoint_;
	RipsToPersistence edgeList_;      
	double distanceBound_;               
	double radius_;
		
	String strPointCloudFile_ = new String();
	String strInputDirectory_ = new String();
	String strOutputDirectory_ = new String();
	String strIntervalsFile_ = new String();
	String strIntervalsAndGensFile_ = new String();
	
	
	public TaskLSD( Settings _processData ) throws Exception {
		
		super( _processData );
		
		// Validate the required settings
		boolean isDataValid = validateRequiredData();

		
		// We check if there were any problems. If we found any, we cannot continue
		// setting up.
		if ( !isDataValid ) {
		    
		    throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER, 
		            processData_.compileErrorMessages().toString() );
		}
		
		// Set up the required (subordinate) objects
		setupTask();
	}
	
	// Compiles various pieces of info about the search, to be used by the recorder
	protected void setupTask() throws Exception {
		
		// Set the string buffer for collecting feedback
		strBufTaskInfo = new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
				
    	loadPointCloudAndCenterPoints();
	
        if ( processData_.wereThereProblems() ) {
            
            throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER,
                    "(Checkpoint) TDA performed a set of validation checks, " +
                    "and discovered the following issues " +
                    "which prevented further program execution:" + 
                    TDA.FEEDBACK_NEWLINE +
                    processData_.compileErrorMessages().toString() );
        }
	}
	
	public ResultsContainerI getResultsContainer() {
		
		return this.resultsContainer_;
	}
	
	public void executeTask() throws Exception {
		
    	String strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_COMPUTELOCALNHOOD );
    	double radius = Double.valueOf( processData_.getValidatedProcessParameter( 
		        TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD ) );
    	PointCloudInRn localNeighborhood;    	
		boolean suppressAllOutput = processData_.getValidatedProcessParameter( 
				TDA.SETTING_SUPPRESSALLOUTPUT ).
				equalsIgnoreCase( TDA.UI_SUPPRESSALLOUTPUT_YES );
    	
    	localNeighborhood =	computeLocalNeighborhoodsByDistance(  
	    			pointCloud_, 
	    			centerPoint_,
	    			metric_,
	    			2* radius );

	    	
    	// hjs 5/20/2014	Basic output for Matlab execution
    	// may want to wrap it in condition (and check against apiMode)

    	if ( !suppressAllOutput ) {
    		
	    	System.out.println( "------------------------------------------------- " );
	    	System.out.println( TDA.APPLICATION_NAME + ": Computing " + TDA.UI_TASK_LSD );
	    	System.out.println( "------------------------------------------------- " );
		
	    	System.out.println( "\nNumber of points = " 
	    			+ localNeighborhood.getNumberOfPoints() );
	    	System.out.println( "Radius = " 
	    			+ radius );
    	}

    	EdgeMatrixI distanceMatrix =
	    	computeLocalSphericalDistanceMatrix( localNeighborhood, centerPoint_, radius );
	    	

		// Add the doubles array to the results, as a double[][], so we can get to the result from
    	// environments where introspection and type casting is not an option
		EdgeMatrixAsMultiDimArray distMat = ( EdgeMatrixAsMultiDimArray ) distanceMatrix;
		
    	resultsContainer_.addRegisteredResult( 
    			TDA.DATA_REGISTEREDRESULT_LSD_0, 	    			 
    			distMat.getEdgeLengths(), 
    			"distanceMatrix (as double[][])" );
    	
    	// Add result in our "standard" generic form
    	resultsContainer_.addRegisteredResult(
    			TDA.DATA_REGISTEREDRESULT_LSD_1,
    			distanceMatrix, 
    			"distanceMatrix" );
    		
    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
		
    		System.out.println( "Distance matrix: \n" + distanceMatrix.toString() );
    	}
	}

	public EdgeMatrixI computeLocalSphericalDistanceMatrix( 
			final PointCloudInRn _pointCloud,
			final PointRn _centerPoint,
    		final double _radius ) throws Exception {
				
		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
			
    		System.out.println( "PC in computeLSD\n" + _pointCloud.asString() );
		}

		MetricFactory metricFactory = new MetricFactory( processData_ );
		MetricI _metric = metricFactory.getMetric();
		
    	// Load the coordinates of the anchor point
    	PointRn z = _centerPoint;

    	PointRn x;
    	PointRn y;
    	PointRn xOrig;
    	PointRn yOrig;
    	PointRn wx;
    	PointRn wy;
    	
    	double Lxy;
    	double Lx;
    	double Ly;
    	double Lwx;
    	double Lwy;
    	
    	double dx;
    	double dy;
    	
    	double X;
    	double Y;
    	double Z;
    	
    	// just to make stuff look nicer
    	final double R = _radius;
    	
    	// variables for solving the final equation 
    	double[] c = new double[ 8 ];
    	double b1;
    	double b2;
    	double a1;
    	double a2;
    	double A;
    	double B;
    	double C;
    	
    	double d1;
    	double d2;
    	double d_ij;

    	int numberOfPoints = _pointCloud.getNumberOfPoints();

    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
		
    		System.out.println( "Computing the local spherical Distance Matrix, for radius=" + R + "." );
    	}
    	
        EdgeMatrixAsMultiDimArray distanceMatrix =
        		new EdgeMatrixAsMultiDimArray( numberOfPoints );
		
    	// Now process the supplied points in a pair-wise loop:
    	for ( int i=0; i<numberOfPoints; i++ ) {

	    	x = _pointCloud.getPoint( i );
			xOrig = new PointRn( x );

			// compute all x-derived quantities, since we will use them for all points y
			// in the inner loop
	    	x = x.translate( z );
	    	Lx = _metric.compute( x );
	    	if ( Lx == 0 ) {
	    		
	    		// both are actually 0:
	    		wx = x;
	    		Lwx = Lx;
	    	}
	    	else {
	    		
		    	wx = x.scalarMultiply( R/Lx );
		    	Lwx = _metric.compute( wx );
	    	}
	    	
	    	dx = Math.abs( R - Lx );
    		distanceMatrix.setEdgeLength( i, i, dx );
	    		    	
    		for ( int j=i+1; j<numberOfPoints; j++ ) {

    			d_ij = -2;
		    	y = _pointCloud.getPoint( j );
    			yOrig = new PointRn( y );
		    	y = y.translate( z );

		    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
		    		
		    		System.out.println( "\nProcessing (pre-translated) points " + 
		    				"x=" + xOrig.toString() + ", y=" + yOrig.toString() );
		    	}

	    		
		    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
		    		
		    		System.out.println( "\nPoints [indexes] (i=" + i + ", j=" + j +
						")  <==>  x=" 
						+ x.toString() + ", y=" + y.toString() + "\n" );
		    	}
				
		    	Lxy = _metric.computeDistance( x, y );		

		    	// first check if points lie very far apart
		    	if ( Lxy > R ) {

		    		distanceMatrix.setEdgeLength( i, j, -1 );
		    		
		    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
		    			
		        		System.out.println( "M(i=" + i + ", j=" + j + ") = -1\n" );
		    		}
		    	}
		    	else {
		    	
			    	Ly = _metric.compute( y );
			    	dy = Math.abs( R - Ly );

			    	// if one of the points coincides with the center point z, then
			    	// set the distance to R
			    	if ( Ly == 0 || Lx == 0 ) {

			    		distanceMatrix.setEdgeLength( i, j, R );
			    		
			    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
			    			
			        		System.out.println( "M(i=" + i + ", j=" + j + "j) = R = " + R + "\n" );
			    		}
			    	}
				    else {
				    	
				    	if ( Ly == 0 ) {
				    		
				    		// both are actually 0:
				    		wy = y;
				    		Lwy = Ly;
				    	}
				    	else {
					    	
					    	wy = y.scalarMultiply( R/Ly );
					    	Lwy = _metric.compute( wy );
				    	}
				    	
				    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
				    		
				    		System.out.println( "Lx = " + Lx + ", Ly = " + Ly );
				    		System.out.println( "wx = " + wx.asStringBasic() + ", wy = " + wy.asStringBasic() );
				    	}
				    	
				    	if ( wx.hasSameCoordinates( wy, TDA.TDA_NUMERICEQUALITY ) ) {
				    		
				    		// update from John; note: dx = |R-Lx|, etc
				    		if ( dx > dy ) {
				    		
				    			distanceMatrix.setEdgeLength( i, j, dx );
				    			if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
				    				
				    	    		System.out.println( "M(i=" + i + ", j=" + j + ") = max{*dx*,dy} = " + dx + "\n" );
				    			}
				    		}
				    		else {
				    		
				    			distanceMatrix.setEdgeLength( i, j, dy );
				    			if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
				    				
				    	    		System.out.println( "M(i=" + i + ", j=" + j + ") = max{dx,*dy*} = " + dy + "\n" );
				    			}
				    		}
				    	}
				    	else if ( wx.hasSameCoordinates( wy.scalarMultiply( -1 ),
				    				TDA.TDA_NUMERICEQUALITY ) ) {
				    		
				    		distanceMatrix.setEdgeLength( i, j, Math.sqrt( R*R + Lx*Ly ) );
				    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
				    			
				        		System.out.println( "M(i=" + i + ", j=" + j + ") = Math.sqrt( R*R + Lx*Ly ) = " + 
				        					Math.sqrt( R*R + Lx*Ly ) + "\n" );
				    		}
				    	}
				    	else {
				    		// computing the dx and dy conditions
				    	
					    	if ( dy >= _metric.computeDistance( x, wy ) ) {
					    		
					    		distanceMatrix.setEdgeLength( i, j, dy );
					    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
					    			
					        		System.out.println( "M(i=" + i + ", j=" + j + ") = dy = " + dy + "\n" );
					    		}
					    	} // test on dy
					    	else {

						    	if ( dx >= _metric.computeDistance( y, wx ) ) {
						
						    		distanceMatrix.setEdgeLength( i, j, dx );
						    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
						    			
						        		System.out.println( "M(i=" + i + ", j=" + j + ") = dx = " + dx + "\n" );
						    		}
						    	} // test on dx
								else {
									// solving the final equation
	
							    	X = Lx*Lx;
							    	Y = Ly*Ly;
							    	Z = x.dotProduct( y );
							    	
							    	// now solve the system of equations:
							    	c[ 1 ] = 2*( Z-X );
							    	if ( Math.abs( c[ 1 ] ) < TDA.TDA_NUMERICEQUALITY ) c[ 1 ] = 0;
							    	c[ 2 ] = 2*( Y-Z );
							    	if ( Math.abs( c[ 2 ] ) < TDA.TDA_NUMERICEQUALITY ) c[ 2 ] = 0;
							    	c[ 3 ] =   ( X-Y );
							    	c[ 4 ] = X;
							    	c[ 5 ] = 2*Z;
							    	c[ 6 ] = Y;
							    	c[ 7 ] = -R*R;



							    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
							    		
							    		System.out.println( "  X = " + X + ", Y = " + Y + ", Z = " + Z );
								    	for ( int k=1; k<8; k++ ) {
	
									    	System.out.println( "  C[" + k + "] = " + c[k] );
								    	}
							    	}						

						    		// -------------------------------
						    		// First "sanity" check: this can't happen at this point of the code
						    		// -------------------------------
							    	
						    		if ( c[ 1 ] == 0 && c[ 2 ] == 0 ) {

					    				// throw exception: we should have handled this case before we get here               
						    			throw new TdaException( 
						    					TDA.ERROR_APP_DEV, 
						    					"[computeLocalSphericalDistanceMatrix] C[1] and C[2] both =0: can't continue."
						    					+ "\nPoints: z=" + z.toString() + 
						    					", x=" + x.toString() + 
						    					", y=" + y.toString(), 
						    					this );
						    		}
						    		
						    		// -------------------------------
						    		// From here on, either c1 or c2 is non-zero
						    		// -------------------------------
						    		
						    		if ( c[ 1 ] == 0 ) {
						    			
										// case: b = -C[3]/C[2];
										// then substitute and solve the quadratic (for 'a')
										// Note: the a, b, c in the next 3 lines are simply temp variables 
						    			// (different from John's flowchart nomenclature)
										A = c[4];
										B = - c[5]*c[3]/c[2];
										C = c[6]*c[3]*c[3]/(c[2]*c[2]) + c[7];
						    			
										// Recall: First equation was reduced to
										b1 = -c[3]/c[2];
								    	
								    	if ( A == 0 ) {
								    		
								    		// quadratic reduces to linear equation
								    		
								    		if ( B != 0 ) {
								    			
								    			// Second equation becomes
								    			a1 = -C/B;								    			
								    			
										    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
										    	
										    	if ( d1 >= 0 ) {
										    		
										    		d1 = Math.sqrt( d1 );
										    		
											    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
											    		
												    	System.out.println( "[a=0] b1=" + b1 + ", d1=" + d1 );
											    	}
										    	}
										    	else {
									    			
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] c[1]=0: a=0, b!=0, but d1<0: can't continue.", 
									    					this );
										    	}
								    		}
								    		else {
								    			
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] a=0 and b=0: can't continue:\n" +
								    					"This case should never be encountered here!", 
								    					this );
								    		}
								    	}
								    	else {
								    		// solve quadratic (#1)
								    		
								    		if ( B*B - 4*A*C < 0 ) {
								    			
								    			if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
								    				
								    	    		System.out.println( "***   b*b - 4*a*c < 0 " );
								    			}
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] b*b-4*a*c<0: can't continue.", 
								    					this );
	
								    		}
								    		
									    	a1 = ( -B + Math.sqrt( B*B - 4*A*C )) / ( 2*A );
									    	a2 = ( -B - Math.sqrt( B*B - 4*A*C )) / ( 2*A );
																	    	
									    	
									    	// Which solution pair do we pick?    	
									    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
									    	d2 = R*R + ( 1-2*a2 )*X - 2*b1*Z;
	
									    	if ( Double.isNaN( d1 ) || Double.isNaN( d2 ) ) {
									    	
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] d1 or d2 is NaN: "
								    					+ "can't continue (d1=' " + d1 + "', d2='" + d2 + "').", 
								    					this );
									    	}
									    	
									    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
									    		
									    		System.out.println( "[quadr. case] d1 = " + d1 + ",   d2 = " + d2 );
									    	}
									    	
									    	if ( d1 > 0 && d2 > 0 ) {
//									    		System.out.println( "d1 and d2 pos" );
								    		
									    		if ( d1 < d2 ) {

//										    		System.out.println( "d1 < d2, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else {

//										    		System.out.println( "d1 > d2, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
									    	}
									    	else {

//									    		System.out.println( "not both d1 and d2 are pos" );
									    		if ( d1 > 0 ) {

//										    		System.out.println( "only d1>0, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else if ( d2 > 0 ) {

//										    		System.out.println( "only d2>0, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
										    	else {
										    		
										    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
										    			
										        		System.out.println( "\n*** Can't find solution: " +
										        				"(d1=' " + d1 + "', d2='" + d2 + "').\n" );
										    		}
		
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] no solution to quadr. eq.: can't continue.", 
									    					this );
										    	}
									    	}
								    	}
						    		}
						    		else if ( c[ 2 ] == 0 ) {
						    			
										// case: a = -C[3]/C[1];
										// then substitute and solve quadratic (for 'a')
										// Note: the a, b, c in the next 3 lines are simply temp variables (diff. from
										// John's flowchart nomenclature)
										A = c[6];
										B = - c[5]*c[3]/c[1];
										C = c[4]*c[3]*c[3]/(c[1]*c[1]) + c[7];

										// Recall: First equation was reduced to
										a1 = -c[3]/c[1];
										
								    	if ( A == 0 ) {
								    		
								    		// quadratic reduces to linear equation
								    		
								    		if ( B != 0 ) {

								    			b1 = -C / B;
								    			
										    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
										    	
										    	if ( d1 >= 0 ) {
										    		
										    		d1 = Math.sqrt( d1 );
										    		
											    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
											    		
												    	System.out.println( "[b=0] a1=" + a1 + ", d1=" + d1 );
											    	}
										    	}
										    	else {
									    			
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] c[1]=0: a=0, b!=0, but d1<0: can't continue.", 
									    					this );
										    	}
								    		}
								    		else {
								    			
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] a=0 and b=0: can't continue:\n" +
								    					"This case should never be encountered here!", 
								    					this );
								    		}
								    	}
								    	else {
								    		// solve quadratic (#2)
								    		
								    		if ( B*B - 4*A*C < 0 ) {
								    			
								    			if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
								    				
								    	    		System.out.println( "****   b*b - 4*a*c < 0 " );
								    			}
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] b*b-4*a*c<0: can't continue.", 
								    					this );
	
								    		}
								    		
									    	b1 = ( -B + Math.sqrt( B*B - 4*A*C )) / ( 2*A );
									    	b2 = ( -B - Math.sqrt( B*B - 4*A*C )) / ( 2*A );
																		    	
									    	
									    	// Which solution pair do we pick?    	
									    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
									    	d2 = R*R + ( 1-2*a1 )*X - 2*b2*Z;
	
									    	if ( Double.isNaN( d1 ) || Double.isNaN( d2 ) ) {
									    	
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] d1 or d2 is NaN: "
								    					+ "can't continue (d1=' " + d1 + "', d2='" + d2 + "').", 
								    					this );
									    	}
									    	
									    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
									    		
									    		System.out.println( "[quadr. case] d1 = " + d1 + ",   d2 = " + d2 );
									    	}

									    	
									    	if ( d1 > 0 && d2 > 0 ) {
//									    		System.out.println( "d1 and d2 pos" );
								    		
									    		if ( d1 < d2 ) {

//										    		System.out.println( "d1 < d2, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else {

//										    		System.out.println( "d1 > d2, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
									    	}
									    	else {

//									    		System.out.println( "not both d1 and d2 are pos" );
									    		if ( d1 > 0 ) {

//										    		System.out.println( "only d1>0, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else if ( d2 > 0 ) {

//										    		System.out.println( "only d2>0, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
										    	else {
										    		
										    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
										    			
										        		System.out.println( "\n*** Can't find solution: " +
										        				"(d1=' " + d1 + "', d2='" + d2 + "').\n" );
										    		}
		
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] no solution to quadr. eq.: can't continue.", 
									    					this );
										    	}
									    	}
								    	}
						    		}
						    		else {
						    			
						    			// case of both c1 and c2 being non-zero
							    		
								    	A = c[4]*c[2]*c[2]/(c[1]*c[1]) - c[5]*c[2]/c[1] + c[6];
								    	B = 2 * c[4]*c[2]*c[3]/(c[1]*c[1]) - c[5]*c[3]/c[1];
								    	C = c[4]*c[3]*c[3]/(c[1]*c[1]) + c[7];
								    	
	
								    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
								    		
									    	System.out.println( "  A=" + A + ", B=" + B + ", C=" + C );
								    	}
	
								
								    	if ( A == 0 ) {
								    		// quadratic reduces to linear equation
								    		
								    		if ( B != 0 ) {
								    		
								    			b1 = -C / B;
								    			a1 = -( c[3] + c[2] * b1 ) / c[1];
								    			
										    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
										    	
										    	if ( d1 >= 0 ) {
										    		
										    		d_ij = Math.sqrt( d1 );
										    		
											    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
											    		
												    	System.out.println( "[c1,c2!=0, a=0] b1=" + b1 + ", d1=" + d1 );
											    	}
										    	}
										    	else {
									    			
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] c[1]!=0: a=0, b!=0, but d1<0: can't continue.", 
									    					this );
										    	}
								    		}
								    		else {
								    			
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] c[1]!=0 and c[2]!=0: a and b =0: can't continue:\n" +
								    					"This case should never be encountered here!", 
								    					this );
								    		}
								    	}
								    	else {
								    		// solve quadratic (#3)
								    		
								    		if ( B*B - 4*A*C < 0 ) {
								    			
								    			if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
								    				
								    	    		System.out.println( "*****   b*b - 4*a*c < 0 " );
								    			}
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] b*b-4*a*c<0: can't continue.", 
								    					this );
	
								    		}
								    		
									    	b1 = ( -B + Math.sqrt( B*B - 4*A*C )) / ( 2*A );
									    	b2 = ( -B - Math.sqrt( B*B - 4*A*C )) / ( 2*A );
									
									    	a1 = ( -c[ 2 ]*b1 - c[ 3 ] ) / c[ 1 ];
									    	a2 = ( -c[ 2 ]*b2 - c[ 3 ] ) / c[ 1 ];
									    	
									    	
									    	// Which solution pair do we pick?    	
									    	d1 = R*R + ( 1-2*a1 )*X - 2*b1*Z;
									    	d2 = R*R + ( 1-2*a2 )*X - 2*b2*Z;
	
									    	if ( Double.isNaN( d1 ) || Double.isNaN( d2 ) ) {
									    	
							    				// throw exception                
								    			throw new TdaException( 
								    					TDA.ERROR_APP_DEV, 
								    					"[computeLocalSphericalDistanceMatrix] d1 or d2 is NaN: "
								    					+ "can't continue (d1=' " + d1 + "', d2='" + d2 + "').", 
								    					this );
									    	}
									    	
									    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
									    		
									    		System.out.println( "[quadr. case] d1 = " + d1 + ",   d2 = " + d2 );
									    	}

									    	
									    	if ( d1 > 0 && d2 > 0 ) {
//									    		System.out.println( "d1 and d2 pos" );
								    		
									    		if ( d1 < d2 ) {

//										    		System.out.println( "d1 < d2, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else {

//										    		System.out.println( "d1 > d2, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
									    	}
									    	else {

//									    		System.out.println( "not both d1 and d2 are pos" );
									    		if ( d1 > 0 ) {

//										    		System.out.println( "only d1>0, d1 is used" );
										    		d_ij = Math.sqrt( d1 );
									    		}
									    		else if ( d2 > 0 ) {

//										    		System.out.println( "only d2>0, d2 is used" );
										    		d_ij = Math.sqrt( d2 );
									    		}
										    	else {
										    		
										    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
										    			
										        		System.out.println( "\n*** Can't find solution: " +
										        				"(d1=' " + d1 + "', d2='" + d2 + "').\n" );
										    		}
		
								    				// throw exception                
									    			throw new TdaException( 
									    					TDA.ERROR_APP_DEV, 
									    					"[computeLocalSphericalDistanceMatrix] no solution to quadr. eq.: can't continue.", 
									    					this );
										    	}
									    	}
								    	}
						    		}

						    		
						    		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
						    			
						        		System.out.println( "M(i=" + i + ", j=" + j + ") = " + d_ij + "\n" );
						    		}
							    	// set value to solution of equations
						    		distanceMatrix.setEdgeLength( i, j, d_ij );
						    		
								} // solving the equation
					    	} // test on dx
				    	} // computing the dx and dy
			    	} // Lx and Ly non-zero
	    		} // Lxy <= R
	    	} // inner loop j over points
    	} // outer loop i over points
    	
    	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
    		
    		System.out.println( "Done with computing LSD." );
    	}
		
		return distanceMatrix;
    }


	public PointCloudInRn computeLocalNeighborhoodsByDistance( 
			final PointCloudInRn _pointCloud, 
    		final PointRn _centerPoint,
    		final MetricI _metric, 
    		final double _distanceBound ) throws Exception {

    	PointRn p1;
    	PointRn p2;
    	double distanceBetweenPoints;
    	int numberOfPointsInPointCloud;

    	PointCloudInRn neighborhoodPoints = new PointCloudInRn();
    	
    	List<PointRn> allPoints = new ArrayList<PointRn>();
    	
    	StringBuffer strBufCollectFeedback =
			new StringBuffer( TDA.BUFFERLENGTH_SMALL );

		startTime_ = System.currentTimeMillis();
    	
    	allPoints = _pointCloud.getPoints();
    	numberOfPointsInPointCloud = allPoints.size();

		neighborhoodPoints = new PointCloudInRn();
    	
		// get the point that defines the i-th local neighborhood
		p1 = _centerPoint; //centerPoints.get( i );
			
			
// ***
// TODO: decide if we always want the center point included
// ***
			// and add it to the current local neighborhood (distance 0, so
			// it will never get removed)
//    		neighborhoodPoints.addPoint( new PointRnLocal( p1, 0 ) );
    		
    		
    		
    		// We need to start comparing at 0, because we are not comparing
    		// within the same set
    		for ( int j=0; j<numberOfPointsInPointCloud; j++ ) {
    			
    	    	p2 = allPoints.get( j );
    	    	distanceBetweenPoints = _metric.computeDistance( p1, p2 );
    	    	
    	    	// Omit the point itself if we encounter it, by enforcing the 
    	    	// distance to be >0
    	    	// Now that we don't include the center point by default, we change
    	    	// the condition to >=0, so that the center point can be included in the
    	    	// listing of points in the point cloud
    	    	if ( distanceBetweenPoints < _distanceBound &&
    	    			distanceBetweenPoints >= 0 ) {
    	    	
    	    		neighborhoodPoints.addPoint( new PointRnLocal( p2, distanceBetweenPoints ) );	
    	    	}
    	    	
//    	    	// Omit the point itself if we encounter it, by enforcing the 
//    	    	// distance to be >0
//    	    	if ( distanceBetweenPoints > 0 ) {
//
//	    	    	// fill up the initial set of "neighbors"
//	    	    	if ( neighborhoodPoints.size() < _pointsInEachLocalNeighborhood ) {
//	    	    		
//	    	    		neighborhoodPoints.add( new PointRnLocal( p2, distanceBetweenPoints ) );
//	    	    	}
//	    	    	else if ( distanceBetweenPoints < threshold ) {
//	
//	    	    		// add the new edge, because it is one of the N-shortest
//	    	    		neighborhoodPoints.add( new PointRnLocal( p2, distanceBetweenPoints ) );
//	    	    		
//	    	    		// remove the longest edge from the set
//	    	    		neighborhoodPoints.remove( neighborhoodPoints.last() );
//	    	    		
//	    	    		// finally, set threshold to highest value that remains
//	    	    		threshold = neighborhoodPoints.last().getDistanceToReferencePoint();
//	    	    	}
//	    		}
    		}

        	// ---------------------------------------------
        	// write the local points to file
        	// ---------------------------------------------
        	String strListOfPoints = new String("# center point for this local neighborhood:\n");
        	
    		// (hjs 3/27/2013) New convention: add center point to top of file.
        	strListOfPoints = strListOfPoints + p1.asStringBasicTab() + "\n\n" +
        			"# closest points in neighborhood (unsorted; distance < " +_distanceBound +
        			"):\n";
    		
        	PointRn pointToCheck;
        	for ( int j=0; j<neighborhoodPoints.getPointCloudSize(); j++ ) {
        		
        		pointToCheck = neighborhoodPoints.getPoint( j );
        		strListOfPoints = strListOfPoints + 
        				pointToCheck.asStringBasicTab().toString() + "\n";
        	}

        	if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
        	
        		System.out.println( strListOfPoints );
        	}

        	String strFileName = "nearestPoints_";
        	String strFileExtension = ".txt";
        	
        	return neighborhoodPoints;
	}
	
	
	protected void loadPointCloudAndCenterPoints() throws Exception {

    	// need to get access to some utility functions" for loading point cloud
		//TdaAlgTest compTopoTest = new TdaAlgTest();
		
    	StringBuffer strBufCollectFeedback =
    			new StringBuffer( TDA.BUFFERLENGTH_SMALL );
    	
    	String strSettingChoice;

    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD );
    	radius_ = Double.parseDouble(strSettingChoice);

    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD );
    	centerPoint_ = new PointRn( processData_.getValidatedProcessParameter( 
    			TDA.SETTING_CENTERPOINTFORLOCALNBHD ) );

    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_POINTCLOUDLOCALNBHDFILE );
    	strPointCloudFile_ = strSettingChoice;
    	
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_INPUTDIRECTORY );
    	strInputDirectory_ = strSettingChoice;
    	
    	
    	// TODO: may be needed for standalone
    	strSettingChoice = processData_.getValidatedProcessParameter(
    			TDA.SETTING_OUTPUTDIRECTORY );
    	strOutputDirectory_ = strSettingChoice;
    	
    	String pointCloudAsString = processData_.getValidatedProcessParameter( 
    			TDA.SETTING_DATALOADEDASARRAY );
    	if ( pointCloudAsString != null && pointCloudAsString.length() > 1 ) {

	    	pointCloud_ = new PointCloudInRn( pointCloudAsString );
    	}
    	else {
    		
	    	// Load the point cloud and the "local" points for which we want the
	    	// neighborhood of N points
	    	pointCloud_ = new PointCloudInRn( strInputDirectory_, strPointCloudFile_ );
    	}
	}

    public void updateProcessData( Settings _processData ) throws Exception {
        // nothing to do
    }
    
	private boolean validateRequiredData() throws Exception {
	    
	    boolean isDataValid = true;
	    
	    String settingNameCanonical;
	    String settingNameDescriptive;
	    String settingNameForDisplay;
        String settingDataType;
		SettingItem settingItem;
		Set validValues = new HashSet();
		int validationType;
        String strCondition;
        final int maxItemsUsed = 4;
        double[] dblValue = new double[maxItemsUsed];
        SettingItem[] arrSettingItem = new SettingItem[maxItemsUsed];

        // Validate the 'radius'
        settingNameCanonical = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD;
        settingNameDescriptive = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD_DESCR;
        settingNameForDisplay = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_DOUBLE;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        arrSettingItem[0] = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                null,
                Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
        
        if ( arrSettingItem[0].isValidSetting() ) {

            try {

                strCondition = new String( "greater than 0" );
                dblValue[0] = Double.parseDouble( 
                		processData_.getValidatedProcessParameter(
                                settingNameCanonical ));
                if ( dblValue[0] <= 0 ) {
                    
                	processData_.addToErrors( new TdaError( 
                            StringUtil.composeErrorMessage( 
                                    arrSettingItem[0], 
                                    strCondition ),
                            TDA.ERRORTYPE_INVALIDRANGE,
                            settingNameCanonical,
                            StringUtil.getClassName( this ) ) );
                    
                    isDataValid = false;
                    
                }
            }
            catch ( Exception e ) {

                throw new TdaException( 
                        TDA.ERROR_APP_DEV, arrSettingItem[0], this );
            }
        }
        else {
            
            isDataValid = false;
        }
        
        
        settingNameCanonical = TDA.SETTING_POINTCLOUDLOCALNBHDFILE;
        settingNameDescriptive = TDA.SETTING_POINTCLOUDLOCALNBHDFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_POINTCLOUDLOCALNBHDFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_POINTCLOUDLOCALNBHDFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the point cloud file." );
        }
        else {
            
        	processData_.setDynamicProcessParameter( TDA.DATA_POINTCLOUDLOCALNBHDFILE, 
            		processData_.getValidatedProcessParameter( 
            				TDA.SETTING_POINTCLOUDLOCALNBHDFILE ));
        }


        // Validate the 'SETTING_CENTERPOINTFORLOCALNBHD'
        settingNameCanonical = TDA.SETTING_CENTERPOINTFORLOCALNBHD;
        settingNameDescriptive = TDA.SETTING_CENTERPOINTFORLOCALNBHD_DESCR;
        settingNameForDisplay = TDA.SETTING_CENTERPOINTFORLOCALNBHD_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        arrSettingItem[0] = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT,
                null );
             
	    return isDataValid;
	}
}

