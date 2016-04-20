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

package edu.duke.math.tda.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.duke.math.tda.persistence.M01PersistenceMatrix;
import edu.duke.math.tda.persistence.M12PersistenceMatrix;
import edu.duke.math.tda.persistence.PersistenceMatrix;
import edu.duke.math.tda.structures.edgematrix.DistanceMatrix;
import edu.duke.math.tda.structures.edgematrix.EdgeMatrixAsMultiDimArray;
import edu.duke.math.tda.structures.edgematrix.EdgeMatrixAsSortedMap;
import edu.duke.math.tda.structures.edgematrix.EdgeMatrixI;
import edu.duke.math.tda.structures.edgematrix.FaceTrackerAsSortedMap;
import edu.duke.math.tda.structures.edgematrix.FaceTrackerI;
import edu.duke.math.tda.structures.edgematrix.SparseMatrix;
import edu.duke.math.tda.structures.metric.MetricI;
import edu.duke.math.tda.structures.pointcloud.PointCloudInRn;
import edu.duke.math.tda.structures.results.Interval;
import edu.duke.math.tda.structures.results.ResultsCollection;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaError;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.recording.RecorderI;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Class for special lists of Edges (was at some point simply called 'EdgeList').
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created October 2012
 * 
 * as of 4/2015, still contains a bunch of code related to open questions...
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class RipsToPersistence {

	// set of edges as supplied by the user
	protected List<EdgeI> edges_ = new ArrayList<EdgeI>();
	// set of edges as being build up by the assembly process
	protected List<EdgeI> builtUpEdges_ = new ArrayList<EdgeI>();
	// helper variables, so we don't have to 'look up' these values all the time
	protected int numberOfEdges_;
	protected int numberOfVertices_;
	
	// List of edges connected to each vertex
	protected ArrayList<ArrayList<Integer>> localListL_;
	// May want to use a sorted collection for this one instead (?):
	// List of vertices connected to each vertex
	protected ArrayList<ArrayList<Integer>> localListLv_;
	protected List<PointRn> vertices_ = new ArrayList<PointRn>();
	protected double distanceBound_;
	protected MetricI metric_;
	protected PointCloudInRn pointCloud_;
	
	protected StringBuffer strBufSparseMat_ = new StringBuffer();
	
	// data structure for quick look-up of edge indices
	protected EdgeMatrixI edgeMatrix_;
	
	protected FaceTrackerI faceTracker_;
	
	protected M12PersistenceMatrix matrixM12_;
	protected PersistenceMatrix reductionMatrix_;
	protected M01PersistenceMatrix matrixM01_;
	
	protected int[] p_;
	protected int[] U_;
	// array used for tracking non-zero diagonal elements (sparse matrix data)
	protected double[] diag_;

	// data structures for tracking results for different output targets
	protected ResultsCollection resCollZeroDimPers_ = new ResultsCollection();
	protected String zeroDimPers_ = new String( "" );
	protected String zeroDimPersPlain_ = new String( "" );
	protected String zeroDimPersForDiagram_ = new String( "" );
	protected String zeroDimPersForDiagramZeros_ = new String( "" );

	// internal tracking for later feedback
	protected StringBuffer strBufCollectFeedback_ = 
	    new StringBuffer( TDA.BUFFERLENGTH_STAT );
	protected volatile RecorderI edgeListStatistics_;

	// timing variables
	protected volatile long startTime_;
	protected volatile long elapsedTime_;

	// global access to settings container
	protected Settings processData_;
	// factory that knows what edges are to be created
	protected EdgeFactory edgeFactory_;

	protected FaceFactory faceFactory_;
	
//	// find a better way than to use this global variable
//	int[] localVertices_;

	// hjs 2/7/2014 Track changes to components as we go through union-find algorithm
	SortedMap<Integer, ArrayList<Integer>> componentList = new TreeMap<Integer, ArrayList<Integer>>();
	SortedMap<Integer, ArrayList<Integer>> intermediateComponentList = new TreeMap<Integer, ArrayList<Integer>>();


    protected class LocalEdgeFinder {

    	int[] localVertices_;
    	ArrayList<EdgeI> localEdges_;

		public LocalEdgeFinder( final EdgeI _referenceEdge ) throws Exception {
    		
    		findLocalEdges( _referenceEdge );
    	}

    	public ArrayList<EdgeI> getLocalEdges() {
    		
    		return localEdges_;
    	}
    	
    	public int[] getLocalVertices(){
    		
    		return localVertices_;
    	}

    	protected void findLocalEdges( final EdgeI _referenceEdge ) throws Exception {

        	List<EdgePair> edgePairList = new ArrayList<EdgePair>();
        	ArrayList<EdgeI> localEdges = new ArrayList<EdgeI>();
        	EdgePair edgePair;
        	EdgeI edgeToProcess;
        	int indexOfReferenceEdge;
        	int tmpEdgeListIndex;

    		final int vertex1 = _referenceEdge.getVertexIndex1();
    		final int vertex2 = _referenceEdge.getVertexIndex2();
    		
    		edgePairList = findEdgePairsToOppositeVerticesM23( _referenceEdge );
//    		System.out.println( "edgePairList: " + edgePairList );
    		localVertices_ = new int[ edgePairList.size() ];
    		
    		// find localVertex list first
    		for ( int i=0; i<edgePairList.size(); i++ ) {
    			
    			edgePair = edgePairList.get( i );
    			
    			// take care of first edge in this edgePair
    			edgeToProcess = edgePair.getEdge1();

    			// Record the vertex that is not part of the edge that we started with
    			// Note: we need to look only at one edge from each pair
    			if ( edgeToProcess.hasVertex( vertex1 ) ) {
    				
    				localVertices_[ i ] = edgeToProcess.getOtherVertex( vertex1 );
    			}
    			else {
    				
    				localVertices_[ i ] = edgeToProcess.getOtherVertex( vertex2 );
    			}
    		}
    		
    		// now find localEdges
    		indexOfReferenceEdge = _referenceEdge.getEdgeListIndex();
    		EdgeI tmpLocalEdge;
    		for ( int i=0; i<localVertices_.length; i++ ) {
    			for ( int j=1; j<localVertices_.length; j++ ) {
    			
    				tmpEdgeListIndex = edgeMatrix_.getEdgeIndex( 
    						localVertices_[ i ], localVertices_[ j ] );
    				if ( tmpEdgeListIndex > -1 && tmpEdgeListIndex < indexOfReferenceEdge && i < j ) {
    					
    					// Actually, instead of adding an existing edge, make one that has "local vertex indexes"
    					// in this case i and j:					
    					tmpLocalEdge = edgeFactory_.createEdge( i, j );
    					
    					// hjs 12/8/2014 try to add edgeLengths, but:  indexes are not correct yet 
//    					tmpLocalEdge.setDistance( 
//    							edgeMatrix_.getEdgeLength( localVertices_[ i ], localVertices_[ j ] ) );
    					
    					
    					
    					localEdges.add( tmpLocalEdge ); // NOTE: this local edge corresponds to the global edge with
    					// index 'tmpEdgeListIndex'!!
    					// TODO: MAY want to save this info
    					
    					
    					
//    					localEdges.add( edgeMatrix_.getEdge( 
//    							localVertices_[ i ], localVertices_[ j ] ) );
    				}
    			}
    		}
    		   		        	
    		localEdges_ = localEdges;
        }
    }

	// TODO -- this constructor is not complete!!
//	public RipsToPersistence( final RipsToPersistence _orig ) {
//    	
//    	this.distanceBound_ = _orig.getDistanceBound();
//    	this.numberOfEdges_ = _orig.getNumberOfEdges();
//    	this.pointCloud_ = _orig.getPointCloud();
//    	this.numberOfVertices_ = pointCloud_.getNumberOfPoints();
//
//    	this.processData_ = _orig.processData_;
//    	edgeFactory_ = new EdgeFactory( processData_ );
//    	
//		setup();
//		
//    	this.edges_ = _orig.getEdges();
//    	this.vertices_ = _orig.getVertices();
//    	this.metric_ = _orig.getMetric();
//    	
//    	// TODO:
////    	this.edgeMatrix_ = new EdgeMatrix( this.numberOfEdges_ );
////    	this.reductionMatrix_ = new ReductionMatrix();
//    }
	
	
	
	/// NOTE: this is a special constructor for M23 use internal only (make private after
	// unit tests are done)
	public RipsToPersistence( final ArrayList<EdgeI> _localEdgeList ) throws Exception {

//    	this.processData_ = _processData;
//    	edgeFactory_ = new EdgeFactory( processData_ );
//    	
//    	this.distanceBound_ = _distanceBound;
//    	this.metric_ = _metric;

//    	startTime_ = System.currentTimeMillis();
//    	
//    	this.edges_ = computeEdges( _distMatrix );
//    	this.numberOfEdges_ = edges_.size();
    	
//    	setup();

		// Assign the uPos and pPos properties, as well as the p_ array values
//		this.applyUnionFind();
//		this.assignEdgeProperties( true );
		
//    	this.matrixM12_ = new M12PersistenceMatrix( this );
//    	this.matrixM01_ = new M01PersistenceMatrix( this );
//
//    	elapsedTime_ = System.currentTimeMillis() - startTime_; 		
//    	System.out.println( "Time used for computing edges: " + 
//    			StringUtil.formatElapsedTime(
//    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
	}
	
	public RipsToPersistence( final DistanceMatrix _distMatrix, 
    		final MetricI _metric, 
			final double _distanceBound,
			Settings _processData ) throws Exception {

    	this.processData_ = _processData;
    	edgeFactory_ = new EdgeFactory( processData_ );
    	
    	this.distanceBound_ = _distanceBound;
    	this.metric_ = _metric;

    	startTime_ = System.currentTimeMillis();
    	
    	this.edges_ = computeEdges( _distMatrix );
    	this.numberOfEdges_ = edges_.size();
    	
    	setup();

		// Assign the uPos and pPos properties, as well as the p_ array values
		this.applyUnionFind();
		
    	this.matrixM12_ = new M12PersistenceMatrix( this );
    	this.matrixM01_ = new M01PersistenceMatrix( this );

    	elapsedTime_ = System.currentTimeMillis() - startTime_; 		
//    	System.out.println( "Time used for computing edges: " + 
//    			StringUtil.formatElapsedTime(
//    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
	}
	
	public RipsToPersistence( final SparseMatrix _sparseMatrix, 
    		final MetricI _metric, 
			final double _distanceBound,
			Settings _processData ) throws Exception {

    	this.processData_ = _processData;
    	edgeFactory_ = new EdgeFactory( processData_ );

    	this.distanceBound_ = _distanceBound;
    	this.metric_ = _metric;

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before calling computeEdges," )));
    	
    	startTime_ = System.currentTimeMillis();
    	
    	this.edges_ = computeEdges( _sparseMatrix );
    	this.numberOfEdges_ = edges_.size();
   	
//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before calling setup," )));
		
    	setup();

		// Assign the uPos and pPos properties, as well as the p_ array values
		this.applyUnionFind();

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before creating M12PersistenceMatrix," )));
		
    	this.matrixM12_ = new M12PersistenceMatrix( this );

    	elapsedTime_ = System.currentTimeMillis() - startTime_; 		
//    	System.out.println( "Time used for computing edges: " + 
//    			StringUtil.formatElapsedTime(
//    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor after creating M12PersistenceMatrix," )));
		
    	//this.matrixM01_ = new M01PersistenceMatrix( this );
	}
	
    public RipsToPersistence( final PointCloudInRn _pointCloud,
    		final MetricI _metric,
    		final double _distanceBound,
			Settings _processData ) throws Exception {

    	// Number of points in point cloud, at which we switch from using
    	// array to sparse  representation
    	int switchToSparseAt;
    	
    	//
    	this.processData_ = _processData;
    	edgeFactory_ = new EdgeFactory( processData_ );

    	this.distanceBound_ = _distanceBound;
    	this.metric_ = _metric;
    	this.numberOfVertices_ = _pointCloud.getNumberOfPoints();
        diag_ = new double[ numberOfVertices_ ]; // initializes to zeros
        // not sure if we actually need to keep the point cloud around
    	this.pointCloud_ = _pointCloud;
    	
    	// hjs 11/4/2013 Switch from the distance matrix to the sparse matrix representation
    	// for point cloud data
    	validateRequiredData();
    	
    	switchToSparseAt = Integer.parseInt( processData_.getValidatedProcessParameter( 
				TDA.SETTING_SWITCHTOSPARSEAT ) );
		

    	startTime_ = System.currentTimeMillis();
    	
		// "preprocessing": get a sparse matrix from the point cloud
    	if ( switchToSparseAt < _pointCloud.getNumberOfPoints() ) {    	
    	
    		// "preprocessing": get a sparse matrix from the point cloud
	    	String strPointCloudAsSparseMatrix = _pointCloud.asSparseMatrix( _metric, _distanceBound ).toString();
	    	SparseMatrix sparseMatrix = new SparseMatrix( strPointCloudAsSparseMatrix, _processData );
	    	
	    	this.edges_ = computeEdges( sparseMatrix );
    	}
    	else {

	    	this.edges_ = computeEdges( _pointCloud );
    	}
    	
    	

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before calling computeEdges," )));
    	
    	this.numberOfEdges_ = edges_.size();

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before calling setup," )));
		
    	setup();

		// Assign the uPos and pPos properties, as well as the p_ array values
		this.applyUnionFind();

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn EdgeListconstructor before creating M12PersistenceMatrix," )));
		
    	this.matrixM12_ = new M12PersistenceMatrix( this );
    	
    	


    	elapsedTime_ = System.currentTimeMillis() - startTime_; 
    	
//    	System.out.println( "Time used for computing edges: " + 
//    			StringUtil.formatElapsedTime(
//    					elapsedTime_, 1, TDA.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
    	
    	
    	
//    	this.processData_ = _processData;
//    	edgeFactory_ = new EdgeFactory( processData_ );
//
//    	this.distanceBound_ = _distanceBound;
//    	this.metric_ = _metric;
//    	this.pointCloud_ = _pointCloud;
//    	this.numberOfVertices_ = pointCloud_.getNumberOfPoints();
//        diag_ = new double[ numberOfVertices_ ]; // initializes to zeros
//
//    	setup();
//
////    	this.p_ = new int[ numberOfVertices_ ];
////
////    	this.localListL = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
////    	this.localListLv = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
////    	// set up an empty arrayList for collecting the edges per vertex
////    	// (trivially indexed by vertex index)
////    	for ( int i=0; i<numberOfVertices_; i++ ) {
////    		
//////    		this.L.add( new ArrayList<Edge>() );
////    		this.localListL.add( new ArrayList<Integer>() );
////    		this.localListLv.add( new ArrayList<Integer>() );
////    	}
//		
//    	
//    	// hjs 11/1/2013 modify pointCloud code to support sparseMatrix internal storage
//    	// Set up the edges from the vertices in the point cloud
//    	SparseMatrix sparseMatrix = new SparseMatrix( _pointCloud, distanceBound_, metric_, _processData );
//    	this.edges_ = computeEdges( sparseMatrix );
////    	this.edges_ = computeEdges( _pointCloud );
//    	this.numberOfEdges_ = edges_.size();
//
//		// Assign the uPos and pPas properties, as well as the p_ array values
//		this.assignEdgeProperties();
////		this.assignEdgeProperties( true );
//		
//    	// need to do this inside of 'computeEdges'
////    	this.edgeMatrix = new EdgeMatrix( this.numberOfEdges_ );
//    	this.matrixM12_ = new M12PersistenceMatrix( this );
//    	this.matrixM01_ = new M01PersistenceMatrix( this );
////    	this.reductionMatrix_ = new PersistenceMatrix( this );
    }


    public RipsToPersistence( final PointCloudInRn _pointCloud,
    		final MetricI _metric,
    		final double _distanceBound,
			Settings _processData, int _useSparse ) throws Exception {

    	this.processData_ = _processData;
    	edgeFactory_ = new EdgeFactory( processData_ );

    	this.distanceBound_ = _distanceBound;
    	this.metric_ = _metric;
    	this.pointCloud_ = _pointCloud;
    	this.numberOfVertices_ = pointCloud_.getNumberOfPoints();
        diag_ = new double[ numberOfVertices_ ]; // initializes to zeros

    	setup();
		
    	
    	// hjs 11/1/2013 modify pointCloud code to support sparseMatrix internal storage
    	// Set up the edges from the vertices in the point cloud
    	this.edges_ = computeEdges( _pointCloud );
    	this.numberOfEdges_ = edges_.size();

		// Assign the uPos and pPas properties, as well as the p_ array values
		this.applyUnionFind();
		
    	// need to do this inside of 'computeEdges'
    	this.matrixM12_ = new M12PersistenceMatrix( this );
    	this.matrixM01_ = new M01PersistenceMatrix( this );
    }

    
    // hjs 11/1/2013  deprec
    public RipsToPersistence( final PointCloudInRn _pointCloud, 
	    		final PointCloudInRn _localPoints,
	    		final int _pointsInEachLocalNeighborhood,
	    		final MetricI _metric, 
	    		final double _distanceBound ) throws Exception {

    	
//    	this.distanceBound = _distanceBound;
//    	this.metric_ = _metric;
//    	this.pointCloud_ = _pointCloud;
//    	this.numberOfVertices_ = pointCloud_.getNumberOfPoints();
//        diag_ = new double[ numberOfVertices_ ]; // initializes to zeros
//
//    	setup();
//    	
////    	this.p_ = new int[ numberOfVertices_ ];
////
////    	this.localListL = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
////    	this.localListLv = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
////    	// set up an empty arrayList for collecting the edges per vertex
////    	// (trivially indexed by vertex index)
////    	for ( int i=0; i<numberOfVertices_; i++ ) {
////    		
//////    		this.L.add( new ArrayList<Edge>() );
////    		this.localListL.add( new ArrayList<Integer>() );
////    		this.localListLv.add( new ArrayList<Integer>() );
////    	}
//		
//    	// Set up the edges from the vertices in the point cloud
//    	this.edges_ = computeEdges( _pointCloud, 
//    			_localPoints, 
//    			_pointsInEachLocalNeighborhood );
//    	this.numberOfEdges_ = edges_.size();
//
//		// Assign the uPos and pPas properties, as well as the p_ array values
//		this.assignEdgeProperties();
////		this.assignEdgeProperties( true );
//		
//    	// need to do this inside of 'computeEdges'
////    	this.edgeMatrix = new EdgeMatrix( this.numberOfEdges_ );
//    	this.matrixM12_ = new M12PersistenceMatrix( this );
//    	this.matrixM01_ = new M01PersistenceMatrix( this );
////    	this.reductionMatrix_ = new PersistenceMatrix( this );
    }
    
	protected boolean validateRequiredData() throws Exception {
	
		    
		    boolean isDataValid = true;
	
		    String settingNameCanonical;
		    String settingNameDescriptive;
		    String settingNameForDisplay;
	        String settingDataType;
			SettingItem settingItem;
			Set<String> validValues = new HashSet<String>();
			int validationType;
	        String strCondition;
	        int intValue;
	        
	        

	        // Validate the 'switch between using the distance matrix and
	        // the sparse matrix representation'
	        settingNameCanonical = TDA.SETTING_SWITCHTOSPARSEAT;
	        settingNameDescriptive = TDA.SETTING_SWITCHTOSPARSEAT_DESCR;
	        settingNameForDisplay = TDA.SETTING_SWITCHTOSPARSEAT_DISP;
	        settingDataType = TDA.VALIDATION_DATATYPE_INTEGER;
	        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	        settingItem = processData_.processSetting( settingNameCanonical, 
	                settingNameDescriptive,
	                settingNameForDisplay,
	                settingDataType,
	                validationType,
	                null,
	                Integer.toString( TDA.DEFAULT_SWITCHTOSPARSEAT ) );
	        
	        if ( settingItem.isValidSetting() ) {
	
	            try {
	
	                strCondition = new String( "greater or equal than 0" );
	                intValue = Integer.parseInt( 
	                		processData_.getValidatedProcessParameter(
	                                settingNameCanonical ));
	                if ( intValue < 0 ) {
	                    
	                	processData_.addToErrors( new TdaError( 
	                            StringUtil.composeErrorMessage( 
	                            		settingItem, 
	                                    strCondition ),
	                            TDA.ERRORTYPE_INVALIDRANGE,
	                            settingNameCanonical,
	                            StringUtil.getClassName( this ) ) );
	                    
	                    isDataValid = false;
	                    
	                }
	            }
	            catch ( Exception e ) {
	
	                throw new TdaException( 
	                        TDA.ERROR_APP_DEV, settingItem, this );
	            }
	        }
	        else {
	            
	            isDataValid = false;
	        }
	        
	        return isDataValid;
	}

	public void setup() {		

    	p_ = new int[ numberOfVertices_ ];

    	this.localListL_ = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
    	this.localListLv_ = new ArrayList<ArrayList<Integer>>( numberOfVertices_ );
    	// set up an empty arrayList for collecting the edges per vertex
    	// (trivially indexed by vertex index)
    	for ( int i=0; i<numberOfVertices_; i++ ) {

    		this.localListL_.add( new ArrayList<Integer>() );
    		this.localListLv_.add( new ArrayList<Integer>() );
    	}
    	
    	// set up the FaceFactory
    	faceFactory_ = new FaceFactory( this.processData_ );
    	
    	// set up the tracking of the faces
    	faceTracker_ = new FaceTrackerAsSortedMap( numberOfVertices_ );
	}
	
	public Settings getSettings() {
		
		return processData_;
	}
	
	// Compute the sorted list of edges (subject to the distance bound)
    protected List<EdgeI> computeEdges( 
    		final DistanceMatrix _distMatrix ) throws Exception {

    	double L2Distance;
    	int numberOfPoints;
    	List<EdgeI> computedEdges = new ArrayList<EdgeI>();
    	EdgeI tmpEdge;

        if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    	
        	strBufCollectFeedback_.append( "\n\nNow loading the edges" );
        }

		startTime_ = System.currentTimeMillis();
    	
        numberOfPoints = _distMatrix.getNumberOfVertices();
        this.numberOfVertices_ = _distMatrix.getNumberOfVertices();
        double[][] edgeLengths = _distMatrix.getDistances();
        diag_ = new double[ numberOfPoints ];
    	
    	// compare the points pair-wise
    	for ( int i=0; i<numberOfPoints; i++ ) {
    		
    		// for "special' distance matrices, we allow non-zero values on the diagonal
    		diag_[ i ] = edgeLengths[ i ][ i ];
    		
    		for ( int j=i+1; j<numberOfPoints; j++ ) {
    			
    			// Get the edge length from the supplied value    	
    			L2Distance = edgeLengths[ i ][ j ];
    	    	
    	    	// Note: by comparing the supplied value to 0, we can easily supply
    	    	// only those edges that we want to use:
    	    	if ( L2Distance > 0 && L2Distance < this.distanceBound_ ) {

    	    		computedEdges.add( edgeFactory_.createEdge( i, j, L2Distance ) );
    	    	}
    		}
    	}
    	
    	// ------------------------
    	// Before returning the edge list, SORT it by the distance (done via
    	// default comparator implementation in Edge class)
    	// ------------------------
    	Collections.sort( computedEdges );


    	this.edgeMatrix_ = new EdgeMatrixAsMultiDimArray( numberOfPoints );
    	
    	// now assign the index values to each edge
    	// and store 

    	for ( int i=0; i<computedEdges.size(); i++ ) {
    		
    		tmpEdge = computedEdges.get( i );
    		tmpEdge.setEdgeListIndex( i );
    		edgeMatrix_.setEdgeIndex( tmpEdge.getVertexIndex1(), 
				    				tmpEdge.getVertexIndex2(), 
				    				tmpEdge.getEdgeListIndex() );
    		strBufSparseMat_.append( tmpEdge.getVertexIndex1() + " \t" +
    				tmpEdge.getVertexIndex2() + " \t" +  tmpEdge.getEdgeLength() + "\n" );
    	}
    	// this would set the edge length
//    	for ( int i=0; i<edgesToCompute.size(); i++ ) {
//    		
//    		tmpEdge = edgesToCompute.get( i );
//    		tmpEdge.setEdgeListIndex( i );
//    		edgeMatrix_.setEdgeLength( tmpEdge.getVertexIndex1(), 
//				    				tmpEdge.getVertexIndex2(), 
//				    				tmpEdge.getDistance() );
//    	}

		elapsedTime_ = System.currentTimeMillis() - startTime_;

//		if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//	    	
//        	strBufCollectFeedback.append( "\nElapsed time (after computing edges):  ");
//        	strBufCollectFeedback.append( 
//		        StringUtil.formatElapsedTime( 
//		                elapsedTime, 1, COMPTOPO.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
//		}
		
		
//		edgeListStatistics.recordSpecifiedData( strBufCollectFeedback );

    	return computedEdges;
    }
	
	// Compute the sorted list of edges (subject to the distance bound)
    protected List<EdgeI> computeEdges( 
    		final SparseMatrix _sparseMatrix ) throws Exception {

    	double edgeLength;
    	int numberOfPoints;
        List<EdgeI> edges = _sparseMatrix.getEdges();
    	List<EdgeI> edgesToCompute = new ArrayList<EdgeI>(); 
    	EdgeI tmpEdge;

        if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    	
        	strBufCollectFeedback_.append( "\n\nNow loading the edges" );
        }

		startTime_ = System.currentTimeMillis();
    	
        numberOfPoints = _sparseMatrix.getNumberOfVertices();
        diag_ = new double[ numberOfPoints ]; // initializes to zeros
        this.numberOfVertices_ = _sparseMatrix.getNumberOfVertices();
        
        for ( int i=0; i<edges.size(); i++ ) {
        	
        	tmpEdge = edges.get( i );
        	edgeLength = tmpEdge.getEdgeLength();
        	
	    	if ( edgeLength > 0 && edgeLength < this.distanceBound_ ) {
	    		
	    		
	    		// if the edge has two equal vertices (allowed as special case),
	    		// then handle it separately by recording the edgeLength value
	    		if ( tmpEdge.getVertexIndex1() != tmpEdge.getVertexIndex2() ) {

		    		edgesToCompute.add( tmpEdge );
	    		}
	    		else {
	    			
	        		// for "special' distance matrices, we allow non-zero values on the diagonal
	        		diag_[ i ] = tmpEdge.getEdgeLength();
	    		}
			}
        }

//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn computeEdges [1]," )));
    	
    	// ------------------------
    	// Before returning the edge list, SORT it by the distance (done via
    	// default comparator implementation in Edge class)
    	// ------------------------
    	Collections.sort( edgesToCompute );
    	
//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn computeEdges [after sort]," )));

    	this.edgeMatrix_ = new EdgeMatrixAsSortedMap( _sparseMatrix );
    	
//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn computeEdges [after new EdgeMatrixAsSortedMap]," )));
    	
    	// now assign the index values to each edge
    	// and store 

    	for ( int i=0; i<edgesToCompute.size(); i++ ) {
    		
    		tmpEdge = edgesToCompute.get( i );
    		tmpEdge.setEdgeListIndex( i );
    		edgeMatrix_.setEdgeIndex( tmpEdge.getVertexIndex1(), 
				    				tmpEdge.getVertexIndex2(), 
				    				tmpEdge.getEdgeListIndex() );
    	}
    	
//		System.out.println( new StringBuffer( StringUtil.compileMemoryInfo( 
//				"\n\nIn computeEdges after setting up edgesToCompute list," )));
    	
    	
    	// this would set the edge length
//    	for ( int i=0; i<edgesToCompute.size(); i++ ) {
//    		
//    		tmpEdge = edgesToCompute.get( i );
//    		tmpEdge.setEdgeListIndex( i );
//    		edgeMatrix_.setEdgeLength( tmpEdge.getVertexIndex1(), 
//				    				tmpEdge.getVertexIndex2(), 
//				    				tmpEdge.getDistance() );
//    	}

		elapsedTime_ = System.currentTimeMillis() - startTime_;

//		if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//	    	
//        	strBufCollectFeedback.append( "\nElapsed time (after computing edges):  ");
//        	strBufCollectFeedback.append( 
//		        StringUtil.formatElapsedTime( 
//		                elapsedTime, 1, COMPTOPO.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
//		}
		
		
//		edgeListStatistics.recordSpecifiedData( strBufCollectFeedback );

    	return edgesToCompute;
    }

    // Compute the sorted list of edges (subject to the distance bound)
    protected List<EdgeI> computeEdges( final PointCloudInRn _pointCloud ) throws Exception {

    	PointRn p1;
    	PointRn p2;
    	double distanceBetweenPoints;
    	int numberOfPoints;
    	List<EdgeI> edgesToCompute = new ArrayList<EdgeI>();
    	List<PointRn> pointsInPointCloud = new ArrayList<PointRn>();
    	EdgeI tmpEdge;

        if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    	
        	strBufCollectFeedback_.append( "\n\nNow computing the edges\n" );
        }

		startTime_ = System.currentTimeMillis();
    	
    	pointsInPointCloud = _pointCloud.getPoints();
    	numberOfPoints = pointsInPointCloud.size();
    	
    	// compare the points pair-wise
    	for ( int i=0; i<numberOfPoints; i++ ) {
    		
			p1 = pointsInPointCloud.get( i );
			
    		for ( int j=i+1; j<numberOfPoints; j++ ) {
    			
    	    	p2 = pointsInPointCloud.get( j );
    	    	distanceBetweenPoints = metric_.computeDistance( p1, p2 );
    	    	
    	    	if ( distanceBetweenPoints < this.distanceBound_ ) {

    	    		edgesToCompute.add( edgeFactory_.createEdge( i, j, distanceBetweenPoints ) );
    	    		
//    	    		strBufSparseMat_.append( i + " \t" + j + " \t" +  distanceBetweenPoints + "\n" );
    	    	}
    		}
    	}
    	
    	// ------------------------
    	// Before returning the edge list, SORT it by the distance (done via
    	// default comparator implementation in Edge class)
    	// ------------------------
    	Collections.sort( edgesToCompute );
    	
    	// TODO: examine if the sparse matrix would be better (storage vs perf trade off)
    	this.edgeMatrix_ = new EdgeMatrixAsMultiDimArray( numberOfPoints );
    	
    	// now assign the index values to each edge
    	// and store 

    	for ( int i=0; i<edgesToCompute.size(); i++ ) {
    		
    		tmpEdge = edgesToCompute.get( i );
    		tmpEdge.setEdgeListIndex( i );
    		edgeMatrix_.setEdgeIndex( tmpEdge.getVertexIndex1(), 
				    				tmpEdge.getVertexIndex2(), 
				    				tmpEdge.getEdgeListIndex() );
    		strBufSparseMat_.append( tmpEdge.getVertexIndex1() + " \t" +
    				tmpEdge.getVertexIndex2() + " \t" +  tmpEdge.getEdgeLength() + "\n" );
    	}
    	
    	
    	
    	// this would set the edge length
//    	for ( int i=0; i<edgesToCompute.size(); i++ ) {
//    		
//    		tmpEdge = edgesToCompute.get( i );
//    		tmpEdge.setEdgeListIndex( i );
//    		edgeMatrix_.setEdgeLength( tmpEdge.getVertexIndex1(), 
//				    				tmpEdge.getVertexIndex2(), 
//				    				tmpEdge.getDistance() );
//    	}

		elapsedTime_ = System.currentTimeMillis() - startTime_;

//		if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//	    	
//        	strBufCollectFeedback.append( "\nElapsed time (after computing edges):  ");
//        	strBufCollectFeedback.append( 
//		        StringUtil.formatElapsedTime( 
//		                elapsedTime, 1, COMPTOPO.FEEDBACK_OPTION_TIMEFORMAT_MIXED ) );
//		}
		
		
//		edgeListStatistics.recordSpecifiedData( strBufCollectFeedback );

    	return edgesToCompute;
    }
    
    public double getDistanceBound() {
    	
    	return this.distanceBound_;
    }
    
    public PointCloudInRn getPointCloud() {
    	
    	return this.pointCloud_;
    }
    
    public int getNumberOfEdges() {
    	
    	return this.numberOfEdges_;
    }
    
    public int getNumberOfVertices() {
    	
    	return this.numberOfVertices_;
    }
    
    public int getP( final int _vertexIndex ) {
    	
    	return this.p_[ _vertexIndex ];
    }
    
    public int[] getP() {
    	
    	return this.p_;
    }
    
    public List<EdgeI> getEdges() {
    	    	
    	return this.edges_;
    }

    public List<PointRn> getVertices() {
    	    	
    	return this.vertices_;
    }
    
    public MetricI getMetric() {
    	    	
    	return this.metric_;
    }
    
    public PersistenceMatrix getMatrixM01() {
    	    	
    	return this.matrixM01_;
    }
    
    public PersistenceMatrix getMatrixM12() {
    	    	
    	return this.matrixM12_;
    }
    
    // assign the edgeType and dagType to the edges
//    public void assignEdgeType() {
//    	;
//    }
//    public void assignDagType() {
//    	;
//    }
	
    public StringBuffer getCollectedFeedback() {

    	// safe content before resetting
    	StringBuffer strBufferToReturn = new StringBuffer( this.strBufCollectFeedback_ );
    	
		this.strBufCollectFeedback_ = 
		    new StringBuffer( TDA.BUFFERLENGTH_STAT );
		
		return strBufferToReturn;
	}
    
    public StringBuffer printP() {
    	
    	StringBuffer PAsString = new StringBuffer( "" );

    	PAsString.append( "Edge List P-array:" );
    	for ( int j=0; j<this.p_.length; j++ ) {
    	
	    	
	    	PAsString.append( "\n			P(" + Integer.toString( j ) 
					+ ") = " + this.p_[ j ] );
    	}
    	PAsString.append( "\n" );
		
		return PAsString;
    }
    
    public StringBuffer printL() {
    	
    	StringBuffer LAsString = new StringBuffer( "" );

    	LAsString.append( "local list L:" );
    	for ( int j=0; j<localListL_.size(); j++ ) {
    	
	    	ArrayList<Integer> tempL = (ArrayList<Integer>) this.localListL_.get( j );
	    	Integer edgeIndex;
	    	
	    	LAsString.append( "\n			L(" + Integer.toString( j ) 
					+ ") = " );
			for ( int i=0; i< tempL.size(); i++ ) {
				
				edgeIndex = (Integer) tempL.get( i );
				LAsString.append( "  " + edgeIndex.toString() + "" );
			}
    	}
    	LAsString.append( "\n" );
		
		return LAsString;
    }
    
    public StringBuffer printL( final int vertexIndex_ ) {
    	
    	// for now, always print entire list
    	if (true) return printL();
    	
    	

		StringBuffer LAsString = new StringBuffer( "" );
    	ArrayList<Integer> tempL = (ArrayList<Integer>) this.localListL_.get( vertexIndex_);
    	Integer edgeIndex;
    	
    	LAsString.append( "(local list L(vertexIndex=" + Integer.toString( vertexIndex_ ) 
				+ ")   " );
		for ( int i=0; i< tempL.size(); i++ ) {
			
			edgeIndex = (Integer) tempL.get( i );
			LAsString.append( "  " + edgeIndex.toString() );
		}
		
		return LAsString;
    }

    public String toString() {
    	
    	return this.asStringComplete().toString();
    }

    public StringBuffer asString() {
    		
		StringBuffer edgeListAsString = new StringBuffer( "" );
		
		for ( int i=0; i< edges_.size(); i++ ) {
			
			// hjs TODO
			edgeListAsString.append( ( (EdgeZ2) edges_.get( i )).asString() );
			edgeListAsString.append( "\n" );
		}
		
		return edgeListAsString;
	}
    
    public StringBuffer asStringComplete() {
    		
		StringBuffer edgeListAsString = new StringBuffer( "" );
		
		for ( int i=0; i< edges_.size(); i++ ) {
			
			// hjs TODO
			edgeListAsString.append( ( (EdgeI) edges_.get( i )).asStringComplete() );
			edgeListAsString.append( "\n" );
		}
		
		return edgeListAsString;
	}
        
    /*
     * Find the list of all pairs of edges that connect the specified edge to 
     * all opposite vertices. 
     * 
     * NOW using the localListL
     * 
     */
//    protected List<EdgePair> findEdgePairsToOppositeVertices( final EdgeZ2 _edge ) throws Exception {
    
    protected List<EdgePair> findEdgePairsToOppositeVerticesM12( final EdgeI _edge ) 
    		throws Exception {
		
		// data derived from the _edge that we are working with (all set up as final) 
    	final int vertex1 = _edge.getVertexIndex1();
    	final int vertex2 = _edge.getVertexIndex2();
    	final int edgeIndex = _edge.getEdgeListIndex();
    	final ArrayList<Integer> indexesOfEdgesConnectedToVertex1 = 
    				( ArrayList<Integer> ) localListL_.get( vertex1 );    	
    	final ArrayList<Integer> indexesOfEdgesConnectedToVertex2 = 
    				( ArrayList<Integer> ) localListL_.get( vertex2 );

    	final ArrayList<Integer> verticesConnectedToVertex1 = 
    				( ArrayList<Integer> ) localListLv_.get( vertex1 );    	
    	final ArrayList<Integer> verticesConnectedToVertex2 = 
    				( ArrayList<Integer> ) localListLv_.get( vertex2 );
    	
    	final int numberOfEdgesConnectedToVertex1 = indexesOfEdgesConnectedToVertex1.size();
    	final int numberOfEdgesConnectedToVertex2 = indexesOfEdgesConnectedToVertex2.size();

    	EdgeI edge1;
    	EdgeI edge2;

    	int oppositeVertex;
    	ArrayList<Integer> oppositeVertexList = new ArrayList<Integer>();

    	boolean isCandidate;
    	EdgePair edgePairCandidate;
    	ArrayList<EdgePair> edgePairCandidateList;
    	ArrayList<EdgePair> edgePairListToReturn;
    
    	int tmpEdgeIndex;
    	int otherVertexIndex1;
    	int otherVertexIndex2;
    	int vertexCount;

    	int edgePairCount;
    	final int numberOfEdgePairCandidates; 
    	int[] U; 	

    	
//			    	System.out.println();
//			    	System.out.print( "	find intersect list for edge = " );
//			    	System.out.print( _edge.asStringComplete() );
//	
//					System.out.println( "\n		" +
//							"@@ vertex1=" + vertex1 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex1 ) );
//			
//					System.out.println( "\n		" +
//							"@@ vertex2=" + vertex2 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex2 ) );
					
//    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
//        	
//    				strBufCollectFeedback_.append( "\n\n	find intersect list for edge = " );
//    				strBufCollectFeedback_.append( _edge.asStringComplete() );
//	
//    				strBufCollectFeedback_.append( "\n		" +
//							"@@ vertex1=" + vertex1 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex1 ) );
//			
//    				strBufCollectFeedback_.append( "\n		" +
//							"@@ vertex2=" + vertex2 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex2 ) );
//    	}

    	edgePairCandidateList = new ArrayList<EdgePair>();

		int indexPointerForList1 = 0;
		int indexPointerForList2 = 0;
		int listSize1;
		int listSize2;    		
		int vertexIndex1;
		int vertexIndex2;
		int edgeIndex1;
		int edgeIndex2;
		ArrayList<Integer> vertexIntersectList = new ArrayList<Integer>();
		ArrayList<Integer> vertexList1;
		ArrayList<Integer> vertexList2;
		
		vertexList1 = this.localListLv_.get( vertex1 );
		vertexList2 = this.localListLv_.get( vertex2 );
		listSize1 = vertexList1.size();
		listSize2 = vertexList2.size();

		// Find the intersection between the two lists
		while ( indexPointerForList1<listSize1 && indexPointerForList2<listSize2 ) {
			
			vertexIndex1 = vertexList1.get( indexPointerForList1 );
			vertexIndex2 = vertexList2.get( indexPointerForList2 );
			
			if ( vertexIndex1 == vertexIndex2 ) {
				
				vertexIntersectList.add( new Integer( vertexIndex1 ) );
				

	    		edgeIndex1 = this.edgeMatrix_.getEdgeIndex( vertex1, vertexIndex1 );
	    		edgeIndex2 = this.edgeMatrix_.getEdgeIndex( vertex2, vertexIndex1 );
				
				edgePairCandidate = new EdgePair( 
						this.edges_.get( edgeIndex1 ), 
						this.edges_.get( edgeIndex2 ),
						vertexIndex1 );
				
				if ( vertex1 > vertex2 ) {
					
	    		    throw new TdaException( TDA.ERROR_APP_DEV, 
	    		            "[PersistenceMatrix - findEdgePairsToOppositeVertices] " +
	    		            "Unexpected ordering of vertices v1 and v2 " +
	    		            "for the edge to be processed: " +
	    		            "v1 = " + vertex1 + ", v2= " + vertex2 + "." ); 
				}
				// compute and set the orientation of each edge in this pair
				if ( vertex1 < vertexIndex1 && vertexIndex1 < vertex2 ) {
					
					// both "opposite" edges have positive orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, 1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, 1 );
				}
				else if ( vertex1 < vertex2 && vertex2 < vertexIndex1 ) {

					// the "opposite" edges have alternate orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, 1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, -1 );
				}
				else if ( vertexIndex1 < vertex1 && vertex1 < vertex2 ) {

					// the "opposite" edges have alternate orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, -1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, 1 );
				}
				else {
					
					// should never happen
	    		    throw new TdaException( TDA.ERROR_APP_DEV, 
	    		            "[PersistenceMatrix - findEdgePairsToOppositeVertices] " +
	    		            "Unexpected ordering of vertices for the edge to be processed, " +
	    		            "relative to the opposite vertex: " +
	    		            "v1 = " + vertex1 + ", v2= " + vertex2 + ", ov=" + vertexIndex1 +
	    		            "." ); 
				}
				
				
				edgePairCandidateList.add( edgePairCandidate );
				
				indexPointerForList1++;
				indexPointerForList2++;
			}
			else {
				
				if ( vertexIndex1 < vertexIndex2 ) {
					
					indexPointerForList1++;
				}
				else {
					
					indexPointerForList2++;
				}
			}
		}
		    	
//    	System.out.println( "\n		" +
//			"! edgePairCandidateList: " + edgePairCandidateList.toString() );
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
        	strBufCollectFeedback_.append( "\n		" +
        			"! edgePairCandidateList: " + edgePairCandidateList.toString() );
    	}
    	
    	// ---------
    	// Use Union-Find to get the number of connected components (i.e., non-redundant edgePairs; note
    	// that the algorithm actually finds the index of the "opposite vertex" that corresponds to each 
    	// such edgePair in the edgePairCandidateList)
    	// ---------
    	numberOfEdgePairCandidates = edgePairCandidateList.size();    	
    	U = new int[ numberOfEdgePairCandidates ];
    	        	    	
    	// Point each vertex (EdgePair) to itself
    	for ( int ii=0; ii<numberOfEdgePairCandidates; ii++ ) {
			
    		U[ ii ] = ii;
		}
		
		edgePairCount = numberOfEdgePairCandidates;
		
		int a, b;
		for ( int aa=0; aa<numberOfEdgePairCandidates; aa++ ) {
			for ( int bb=aa+1; bb<numberOfEdgePairCandidates; bb++ ) {
							
				tmpEdgeIndex = this.edgeMatrix_.getEdgeIndex( 
						edgePairCandidateList.get( aa ).getCommonVertexIndex(), 
						edgePairCandidateList.get( bb ).getCommonVertexIndex() );
				
				if ( tmpEdgeIndex > -1 && tmpEdgeIndex < edgeIndex ) {
					
					a = aa;
					b = bb;
					
					// keep working with this edge		
    	    		while ( U[ a ] != a ) { a = U[ a ]; }
    	    		while ( U[ b ] != b ) { b = U[ b ]; }
    	    		
    	    		if ( a != b ) {

    					// hjs 7/25/2013 [based on] Mod with help from Paul: 
//    					U[ b ] = a;
    					if ( a<b ) {

    						U[ b ] = a;
    					}
    					else {

    						U[ a ] = b;
    					}
    					
    	    			edgePairCount--;
    	    		}
				}
	    		
				if ( edgePairCount == 1 ) break;
			}

			if ( edgePairCount == 1 ) break;
		}
		

    	edgePairListToReturn = new ArrayList<EdgePair>();
    	
		// Union-Find is complete
		// Final step: get the actual "non-redundant" edgePairs, corresponding to those
		// "opposite" vertices that have U(a)==a        			
		for ( int ii=0; ii<numberOfEdgePairCandidates; ii++ ) {
			
			if ( U[ ii ] == ii ) {
				
				// add the ii-th edgePair
				edgePairListToReturn.add( edgePairCandidateList.get( ii ) );

//				System.out.println( "\n		" +
//						">> edgePairCandidate accepted: " + edgePairCandidateList.get( ii ).toString() );
				
				if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	
		        	strBufCollectFeedback_.append( "\n\n		" +
						">> edgePairCandidate accepted: " + edgePairCandidateList.get( ii ).toString() );
				}
			}
			else {
				
//				System.out.println( "\n		" +
//						">> edgePairCandidate rejected: " + edgePairCandidateList.get( ii ).toString() );
				
				if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	
		        	strBufCollectFeedback_.append( "\n\n		" +
						">> edgePairCandidate rejected: " + edgePairCandidateList.get( ii ).toString() );
				}
			}
		}


//					System.out.println( "\n		" +
//							"*** edgePairList to be returned: " + edgePairListToReturn.toString() );
					
		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    	
        	strBufCollectFeedback_.append( "\n\n		" +
							"*** edgePairList to be returned: " + edgePairListToReturn.toString() );
		}
    	
    	return edgePairListToReturn;
    }
    

    protected List<EdgePair> findEdgePairsToOppositeVerticesM23( final EdgeI _edge ) 
    		throws Exception {
		
		// data derived from the _edge that we are working with (all set up as final) 
    	final int vertex1 = _edge.getVertexIndex1();
    	final int vertex2 = _edge.getVertexIndex2();
//    	final int edgeIndex = _edge.getEdgeListIndex();
//    	final ArrayList<Integer> indexesOfEdgesConnectedToVertex1 = 
//    				( ArrayList<Integer> ) localListL_.get( vertex1 );    	
//    	final ArrayList<Integer> indexesOfEdgesConnectedToVertex2 = 
//    				( ArrayList<Integer> ) localListL_.get( vertex2 );

//    	final ArrayList<Integer> verticesConnectedToVertex1 = 
//    				( ArrayList<Integer> ) localListLv_.get( vertex1 );    	
//    	final ArrayList<Integer> verticesConnectedToVertex2 = 
//    				( ArrayList<Integer> ) localListLv_.get( vertex2 );
    	
//    	final int numberOfEdgesConnectedToVertex1 = indexesOfEdgesConnectedToVertex1.size();
//    	final int numberOfEdgesConnectedToVertex2 = indexesOfEdgesConnectedToVertex2.size();
//
//    	EdgeI edge1;
//    	EdgeI edge2;
//
//    	int oppositeVertex;
//    	ArrayList<Integer> oppositeVertexList = new ArrayList<Integer>();

//    	boolean isCandidate;
    	EdgePair edgePairCandidate;
    	ArrayList<EdgePair> edgePairCandidateList;
//    	ArrayList<EdgePair> edgePairListToReturn;
//    
//    	int tmpEdgeIndex;
//    	int otherVertexIndex1;
//    	int otherVertexIndex2;
//    	int vertexCount;
//
//    	int edgePairCount;
//    	final int numberOfEdgePairCandidates; 
//    	int[] U; 	

    	
//			    	System.out.println();
//			    	System.out.print( "	find intersect list for edge = " );
//			    	System.out.print( _edge.asStringComplete() );
//	
//					System.out.println( "\n		" +
//							"@@ vertex1=" + vertex1 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex1 ) );
//			
//					System.out.println( "\n		" +
//							"@@ vertex2=" + vertex2 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex2 ) );
					
//    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
//        	
//    				strBufCollectFeedback_.append( "\n\n	find intersect list for edge = " );
//    				strBufCollectFeedback_.append( _edge.asStringComplete() );
//	
//    				strBufCollectFeedback_.append( "\n		" +
//							"@@ vertex1=" + vertex1 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex1 ) );
//			
//    				strBufCollectFeedback_.append( "\n		" +
//							"@@ vertex2=" + vertex2 + 
//							", localListL: " + 
//							this.printArrayList( indexesOfEdgesConnectedToVertex2 ) );
//    	}

    	edgePairCandidateList = new ArrayList<EdgePair>();

		int indexPointerForList1 = 0;
		int indexPointerForList2 = 0;
		int listSize1;
		int listSize2;    		
		int vertexIndex1;
		int vertexIndex2;
		int edgeIndex1;
		int edgeIndex2;
		ArrayList<Integer> vertexIntersectList = new ArrayList<Integer>();
		ArrayList<Integer> vertexList1;
		ArrayList<Integer> vertexList2;
		
		vertexList1 = this.localListLv_.get( vertex1 );
		vertexList2 = this.localListLv_.get( vertex2 );
		listSize1 = vertexList1.size();
		listSize2 = vertexList2.size();

		// Find the intersection between the two lists
		while ( indexPointerForList1<listSize1 && indexPointerForList2<listSize2 ) {
			
			vertexIndex1 = vertexList1.get( indexPointerForList1 );
			vertexIndex2 = vertexList2.get( indexPointerForList2 );
			
			if ( vertexIndex1 == vertexIndex2 ) {
				
				vertexIntersectList.add( new Integer( vertexIndex1 ) );
				

	    		edgeIndex1 = this.edgeMatrix_.getEdgeIndex( vertex1, vertexIndex1 );
	    		edgeIndex2 = this.edgeMatrix_.getEdgeIndex( vertex2, vertexIndex1 );
				
				edgePairCandidate = new EdgePair( 
						this.edges_.get( edgeIndex1 ), 
						this.edges_.get( edgeIndex2 ),
						vertexIndex1 );
				
				if ( vertex1 > vertex2 ) {
					
	    		    throw new TdaException( TDA.ERROR_APP_DEV, 
	    		            "[PersistenceMatrix - findEdgePairsToOppositeVertices] " +
	    		            "Unexpected ordering of vertices v1 and v2 " +
	    		            "for the edge to be processed: " +
	    		            "v1 = " + vertex1 + ", v2= " + vertex2 + "." ); 
				}
				// compute and set the orientation of each edge in this pair
				if ( vertex1 < vertexIndex1 && vertexIndex1 < vertex2 ) {
					
					// both "opposite" edges have positive orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, 1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, 1 );
				}
				else if ( vertex1 < vertex2 && vertex2 < vertexIndex1 ) {

					// the "opposite" edges have alternate orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, 1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, -1 );
				}
				else if ( vertexIndex1 < vertex1 && vertex1 < vertex2 ) {

					// the "opposite" edges have alternate orientation
					edgePairCandidate.setEdgeOrientation( edgeIndex1, -1 );
					edgePairCandidate.setEdgeOrientation( edgeIndex2, 1 );
				}
				else {
					
					// should never happen
	    		    throw new TdaException( TDA.ERROR_APP_DEV, 
	    		            "[PersistenceMatrix - findEdgePairsToOppositeVertices] " +
	    		            "Unexpected ordering of vertices for the edge to be processed, " +
	    		            "relative to the opposite vertex: " +
	    		            "v1 = " + vertex1 + ", v2= " + vertex2 + ", ov=" + vertexIndex1 +
	    		            "." ); 
				}
				
				
				edgePairCandidateList.add( edgePairCandidate );
				
				indexPointerForList1++;
				indexPointerForList2++;
			}
			else {
				
				if ( vertexIndex1 < vertexIndex2 ) {
					
					indexPointerForList1++;
				}
				else {
					
					indexPointerForList2++;
				}
			}
		}
		    	
//    	System.out.println( "\n		" +
//			"! edgePairCandidateList: " + edgePairCandidateList.toString() );
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
        	strBufCollectFeedback_.append( "\n		" +
        			"! edgePairCandidateList: " + edgePairCandidateList.toString() );
    	}
    	    	
    	return edgePairCandidateList;
    }
    
    public M01PersistenceMatrix computeMatrixM01() throws Exception {

//    	final int numberOfVertices = numberOfVertices_;
    	final int numberOfEdges = this.edges_.size();
    	int vertex1;
    	int vertex2;
    	int boundaryVertex1;
    	int boundaryVertex2;
//    	int tmpVertex1;
//    	int tmpVertex2 = -1;
//    	int tmpVertex3 = -1;
    	int tmpEdgeIndex;
    	EdgeI edgeToProcess;
//    	EdgeZ2 edgeToProcess;
    	boolean isPPositiveValue;
		SortedMap<Integer, Integer> tmpColumnList;
//		ArrayList<Integer> tmpColumnList;
    	
		// for local output only
//		List<Integer> dagList;
//		List<Integer> tmpDagList;
//		Edge tmpEdge = new Edge( -1, -1 );
		
		// boolean flag for processing first edgePair
//		boolean isFirstEdgePair;
    	  	  	
		// "standard case", only use union-find to assign uPos property to edges
//		this.assignEdgeProperties( true );
		
		
		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    	
//        	strBufCollectFeedback.append( "\n" );
//	    	strBufCollectFeedback.append( "\n*** After assignEdgeTypes: EdgeList edgeList(complete) = " );
//	    	strBufCollectFeedback.append( "\n" + this.asStringComplete() );
		}
		
//    	if  ( true ) return this.matrixM01_;
		
    	// now go through edges based on the pPos value
    	for ( int currentEdgeIndex=0; currentEdgeIndex<numberOfEdges; currentEdgeIndex++ ) {
//    	for ( int currentEdgeIndex=0; currentEdgeIndex<0; currentEdgeIndex++ ) {
			
    		// get the edge to process from the loop index
    		edgeToProcess = edges_.get( currentEdgeIndex );
    		    		
    		// crucial: build up the entity //// NOT USED for M01
//    		builtUpEdges_.add( edgeToProcess );

    		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    	    	
            	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    		strBufCollectFeedback_.append( "\nProcessing edge i=" + currentEdgeIndex );
	    		strBufCollectFeedback_.append( "\n------------------------------- " );
    		}

			
			if ( currentEdgeIndex == 78 ) {
				
				int abc;
				abc=1;
			}
    		
    		vertex1 = edgeToProcess.getVertexIndex1();
    		vertex2 = edgeToProcess.getVertexIndex2();
    		isPPositiveValue = edgeToProcess.isPPositive();
//    		isUPositiveValue = edgeToProcess.isUPositive();
    		
    		// reset the column list
    		tmpColumnList = new TreeMap<Integer, Integer>();
//    		tmpColumnList = new ArrayList<Integer>();
    		
    		
    		// if pPos is true (positive):
    		if ( isPPositiveValue ) {
    		
    			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    		    	
    	        	strBufCollectFeedback_.append( 
    	        				"\n+ 'p-Positive': Edge (index=" + currentEdgeIndex + ")"
			    				+ edgeToProcess.asStringComplete() );
    			}
    			
//    			if ( currentEdgeIndex == 78 ) {
//    				
//    				int abc;
//    				abc=1;
//    			}
    			
    			// Find the boundary associated with vertex 1
//    			tmpVertex1 = vertex1;
//    			tmpVertex2 = vertex2;
    			boundaryVertex1 = vertex1;
    			tmpEdgeIndex = this.getP( vertex1 );
				
				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
			    	
		        	strBufCollectFeedback_.append( "\n	[M01 vertex1 proc - start] v="
		        			+ boundaryVertex1 + ", p[" + boundaryVertex1 + "]=" + tmpEdgeIndex );
				}
    			
//    			if ( tmpEdgeIndex == -1 ) {
//    				
//    				// vertex is not paired to an edge, so we are done, and record it:
////    				tmpColumnList.add( new Integer( vertex1 ) );
////        			tmpVertex3 = vertex1;
//    				boundaryVertex1 = vertex1;
//    			}
//    			else {

    				while ( tmpEdgeIndex > -1 ) {
        				
    					// walk "through" the P array until we hit -1
    					
//    					tmpVertex1 = tmpVertex2;
//    					tmpVertex2 = this.edges_.get( tmpEdgeIndex ).getOtherVertex( tmpVertex1 );
//    					// CHECK: this can't ever be equal to the previous edge, or we
//    					// end up in an infinite loop
//        				tmpEdgeIndex = this.getP( tmpVertex2 );	

//	    				boundaryVertex1 = vertex1;
    					boundaryVertex1 = this.edges_.get( tmpEdgeIndex ).getOtherVertex( boundaryVertex1 );
	    				tmpEdgeIndex = this.getP( boundaryVertex1 );
	    				
	    				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    			    	
	    		        	strBufCollectFeedback_.append( "\n	[M01 vertex1 proc] v="
	    		        			+ boundaryVertex1 + ", p[" + boundaryVertex1 + "]=" + tmpEdgeIndex );
	    				}
        			}
    				
//    				if ( tmpVertex1 == -1 ) {
//        				
//        				int abc;
//        				abc=2;
//        			}
//        			tmpColumnList.add( new Integer( tmpVertex1 ) );
//        			tmpVertex3 = tmpVertex1;
//    			}
    			
    			

    			
    			// Same for vertex 2
//    			tmpVertex1 = vertex2;
//    			tmpVertex2 = vertex2;
    			boundaryVertex2 = vertex2;
    			tmpEdgeIndex = this.getP( vertex2 );
				
				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
			    	
		        	strBufCollectFeedback_.append( "\n	[M01 vertex2 proc - start] v="
		        			+ boundaryVertex2 + ", p[" + boundaryVertex2 + "]=" + tmpEdgeIndex );
				}
    			
//    			if ( tmpEdgeIndex == -1 ) {
//
////    				tmpColumnList.add( new Integer( vertex2 ) );
//    				boundaryVertex2 = vertex2;
//    			}
//    			else {

    				while ( tmpEdgeIndex > -1 ) {
    					
						// walk "through" the P array until we hit -1
    					
//						tmpVertex1 = tmpVertex2;
//						tmpVertex2 = this.edges_.get( tmpEdgeIndex ).getOtherVertex( tmpVertex1 );
//						// CHECK: this can't ever be equal to the previous edge, or we
//						// end up in an infinite loop
//	    				tmpEdgeIndex = this.getP( tmpVertex2 );
	    				
//	    				boundaryVertex2 = vertex2;
    					boundaryVertex2 = this.edges_.get( tmpEdgeIndex ).getOtherVertex( boundaryVertex2 );
	    				tmpEdgeIndex = this.getP( boundaryVertex2 );
	    				
	    				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    			    	
	    		        	strBufCollectFeedback_.append( "\n	[M01 vertex2 proc] v="
	    		        			+ boundaryVertex2 + ", p[" + boundaryVertex2 + "]=" + tmpEdgeIndex );
	    				}
        			}
        			    				
//        			tmpColumnList.add( new Integer( tmpVertex1 ) );
//    			}


//    			if ( tmpVertex1 == tmpVertex3 ) {
    			if ( boundaryVertex1 == boundaryVertex2 ) {
    				
    				// Add "null-column" to M01 matrix
    				this.matrixM01_.addEmptyColumn( currentEdgeIndex );
    			}
    			else {
    				
    				// create the column
        			tmpColumnList.put( boundaryVertex1, boundaryVertex1 );
        			tmpColumnList.put( boundaryVertex2, boundaryVertex2 );
//        			tmpColumnList.add( new Integer( boundaryVertex1 ) );
//        			tmpColumnList.add( new Integer( boundaryVertex2 ) );
    				
    				// Add column to M01 matrix
    				this.matrixM01_.addColumn( currentEdgeIndex, tmpColumnList );
    			}
    			

				if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
			    	
		        	strBufCollectFeedback_.append( "\n	M01 column added: " + 
		        			tmpColumnList.toString() );
				}    			
    		}
    		else {
    			
    			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
		    	
    				strBufCollectFeedback_.append( 
	        				"\n+ 'p-Negative': Edge (index=" + currentEdgeIndex + ")"
		    				+ edgeToProcess.asStringComplete() );
    				
    				strBufCollectFeedback_.append( "\n	Nothing to be done. " );
    			}
			}
    	}
    					

    	// FOR TESTING ONLY
		// Add columns to M01 matrix
//    	tmpColumnList = new ArrayList<Integer>();    	
//		tmpColumnList.add( new Integer( 3 ) );
//		tmpColumnList.add( new Integer( 5 ) );
//		this.matrixM01_.addColumn( 3, tmpColumnList );
//
//    	tmpColumnList = new ArrayList<Integer>();  
//		tmpColumnList.add( new Integer( 1 ) );
//		tmpColumnList.add( new Integer( 5 ) );
//		this.matrixM01_.addColumn( 2, tmpColumnList );
//
//    	tmpColumnList = new ArrayList<Integer>();  
//		tmpColumnList.add( new Integer( 1 ) );
//		tmpColumnList.add( new Integer( 3 ) );
//		this.matrixM01_.addColumn( 6, tmpColumnList );
//
//    	tmpColumnList = new ArrayList<Integer>();  
//		tmpColumnList.add( new Integer( 2 ) );
//		tmpColumnList.add( new Integer( 4 ) );
//		this.matrixM01_.addColumn( 6, tmpColumnList );
		
		return this.matrixM01_;
    
    }
    
//    protected int[] findIntersectList( int _vertex1, int _vertex2 ) {
//    
//    	int[] intersectList;
//    	
//    	// TODO
//    	
//    	intersectList = new int[ 1 ];
//    	return intersectList;
//    }
//    
//    protected ArrayList<EdgeI> findLocalVertices( int _vertex1, int _vertex2 ) {
//
//    	ArrayList<EdgeI> localEdges = new ArrayList<EdgeI>();
//    	
//    	// TODO
//    	
//    	return localEdges;
//    }
    
//    
//    protected ArrayList<EdgeI> findLocalEdges( final EdgeI _referenceEdge ) throws Exception {
//
//    	List<EdgePair> edgePairList = new ArrayList<EdgePair>();
//    	ArrayList<EdgeI> localEdges = new ArrayList<EdgeI>();
//    	EdgePair edgePair;
//    	EdgeI edgeToProcess;
//    	EdgeI tmpEdge;
//    	int indexOfReferenceEdge;
//    	int tmpEdgeListIndex;
//
//		final int vertex1 = _referenceEdge.getVertexIndex1();
//		final int vertex2 = _referenceEdge.getVertexIndex2();
//
//
//		if ( _referenceEdge.getEdgeListIndex() > 12 ) {
//			
//			int x;
//			x = 4;
//		}
//		
//		edgePairList = findEdgePairsToOppositeVerticesM23( _referenceEdge );
////		System.out.println( "edgePairList: " + edgePairList );
//		localVertices_ = new int[ edgePairList.size() ];
//		
//		// find localVertex list first
//		for ( int i=0; i<edgePairList.size(); i++ ) {
//			
//			edgePair = edgePairList.get( i );
//			
//			// take care of first edge in this edgePair
//			edgeToProcess = edgePair.getEdge1();
//
//			// Record the vertex that is not part of the edge that we started with
//			// Note: we need to look only at one edge from each pair
//			if ( edgeToProcess.hasVertex( vertex1 ) ) {
//				
//				localVertices_[ i ] = edgeToProcess.getOtherVertex( vertex1 );
//			}
//			else {
//				
//				localVertices_[ i ] = edgeToProcess.getOtherVertex( vertex2 );
//			}
//		}
//		
//		// now find localEdges
//		indexOfReferenceEdge = _referenceEdge.getEdgeListIndex();
//		EdgeI tmpLocalEdge;
//		for ( int i=0; i<localVertices_.length; i++ ) {
//			for ( int j=1; j<localVertices_.length; j++ ) {
//			
//				tmpEdgeListIndex = edgeMatrix_.getEdgeIndex( 
//						localVertices_[ i ], localVertices_[ j ] );
//				if ( tmpEdgeListIndex > -1 && tmpEdgeListIndex < indexOfReferenceEdge && i < j ) {
//					
//					// Actually, instead of adding an existing edge, make one that has "local vertex indexes"
//					// in this case i and j:					
//					tmpLocalEdge = edgeFactory_.createEdge( i, j );
//					
//					// hjs 12/8/2014 try to add edgeLengths, but:  indexes are not correct yet 
////					tmpLocalEdge.setDistance( 
////							edgeMatrix_.getEdgeLength( localVertices_[ i ], localVertices_[ j ] ) );
//					
//					
//					
//					localEdges.add( tmpLocalEdge ); // NOTE: this local edge corresponds to the global edge with
//					// index 'tmpEdgeListIndex'!!
//					// TODO: MAY want to save this info
//					
//					
//					
////					localEdges.add( edgeMatrix_.getEdge( 
////							localVertices_[ i ], localVertices_[ j ] ) );
//				}
//			}
//		}
//		
//		// now that we have the local edges, do our thing to them:
//		
//
//		int numberOfLocalEdges = localEdges.size();
//		
//		
////		int numberOfLocalVertices = localVertices_.length;
////		int[] U = new int[ numberOfLocalVertices ];
////		int[] localDag0 = new int[ numberOfLocalVertices ];
////		
////
////		if( numberOfLocalEdges > 0 ) {
////			
////			int a, aa, b, bb;
////			int count = numberOfLocalVertices;
////			int tmpLocalEdgeListIndex;
////			
////			U = new  int[ numberOfLocalVertices ];
////			EdgeI tempEdge; 				// tempEdge should use edge1 format
////			// Declare and clear localDag0 and U;
////			
////			for ( int i=0; i<numberOfLocalVertices; ++i ) { 
////
////				// NO: this is not right:
//////				tmpLocalEdgeListIndex = localEdges.get( i ).getEdgeListIndex();
//////				localDag0[ tmpLocalEdgeListIndex ] = tmpLocalEdgeListIndex;
//////				U[ tmpLocalEdgeListIndex ] = tmpLocalEdgeListIndex;
////				// deprec:
////				
////				
////				localDag0[ i ] = i;
////				U[ i ] = i;
////			}
////			
////			for ( int j=0; j<numberOfLocalEdges; j++ ) {
////							
////				tempEdge = localEdges.get( j ); 
////				aa = tempEdge.getVertexIndex1(); 
////				bb = tempEdge.getVertexIndex2();	// Assumes that aa < bb -- This is critical for the numberOfLocalVertices look
////				a = aa; 
////				b = bb;
////				
////				while ( U[ a ] != a ) { a = U[ a ]; } 
////				while ( U[ b ] != b ) { b = U[ b ]; } 
////				
////				if ( a!=b ){					// Don't point if forms a loop
////				
////					U[ b ] = a;
////					count--;
////					if ( localDag0[ b ] == b ) {		// Try to point bb to aa
////						
////						localDag0[ b ] = a;
////						localEdges.get( j ).setXPositive( false );
////					}		
////					else if ( localDag0[ a ] == a ){		// If can't point bb to aa, try to point aa to bb
////						
////						localDag0[ a ] = b;
////						localEdges.get( j ).setXPositive( false );
////					}	
////				}
////				
////				if (count == 1) break;				// If count == 1, no more assignments can be
////									// made so we break out of the for loop.
////			}
////		}
//		
//		
//		
//		
//		// TODO: want to return NOT the actual local edges, but the corresponding global ones!!! (for
//		// later access to actual vertex indexes, etc)
//		
//		
//	
//		
////		System.out.println( "LocalEdges before returning: " );
////		for ( int i=0; i<localEdges.size(); i++ ) {
////			
////			System.out.println( "   " + localEdges.get( i ) );
////		}
//    	
//    	return localEdges;
//    }
    
    // for now, keep these functions as wrappers
    protected int F2( final int _vertexIndex1, 
    		final int _vertexIndex2 ) throws Exception {
    
    	return this.edgeMatrix_.getEdgeIndex( _vertexIndex1, _vertexIndex2 );
    }
    
    protected int F3( final int _vertexIndex1, 
    		final int _vertexIndex2, 
    		final int _vertexIndex3 ) throws Exception {
    
//    	System.out.println( "\nGetting F3( " + _vertexIndex1 + ", " +
//    			_vertexIndex2 + ", " +
//    			_vertexIndex3 + " )"
//    			);
    	
    	return faceTracker_.getFaceIndex( _vertexIndex1, _vertexIndex2, _vertexIndex3 );
    }
    
    // Note: any i for which i == localDag0[ i ] is a 0-cycle; we have to return the full
    // dag0 list, though, because of existing code, at least for now.
    public int[] make0Cycles ( final int _nbrOfVertices,
    		final ArrayList<EdgeI> _edgesToProcess ) throws Exception {
//    protected ArrayList<Integer> make0Cycles ( final int _nbrOfVertices,
//    		final ArrayList<EdgeI> _edgesToProcess ) throws Exception {

    	int[] localDag0;
    	int[] U;
		int numberOfEdges = _edgesToProcess.size();
//		ArrayList<Integer> zeroCycles;
    	
		int a, aa, b, bb;
						
		System.out.println( "\t[Func1] List of edges:" );
		for ( int k=0; k<_edgesToProcess.size(); k++ ) {
			
			System.out.println( "\t" + _edgesToProcess.get( k ));
		}

		localDag0 = new int[ _nbrOfVertices ];
		
		
//		System.out.println( "\t\t---[Func1] numberOfEdges = " + numberOfEdges );
		if( numberOfEdges > 0 ) {
			
			int count = _nbrOfVertices;
			int tmpEdgeListIndex;
			
			U = new  int[ _nbrOfVertices ];
			EdgeI tempEdge; 				// tempEdge should use edge1 format
			// Declare and clear localDag0 and U;
			
			for ( int i=0; i<_nbrOfVertices; ++i ) { 

				localDag0[ i ] = i;
				U[ i ] = i;
			}


			System.out.println( "\t\t---[Func1] localDag0 after setup: " );
			for ( int i=0; i<localDag0.length; i++ ) {
				
				System.out.println( "\t\t" + localDag0[ i ] );
			}
			
			for ( int j=0; j<numberOfEdges; j++ ) {
							
				tempEdge = _edgesToProcess.get( j ); 
				aa = tempEdge.getVertexIndex1(); 
				bb = tempEdge.getVertexIndex2();	// Assumes that aa < bb -- This is critical for the numberOfLocalVertices look
				a = aa; 
				b = bb;
				
				while ( U[ a ] != a ) { a = U[ a ]; } 
				while ( U[ b ] != b ) { b = U[ b ]; } 
				
				if ( a!=b ){					// Don't point if forms a loop
				
					U[ b ] = a;
					count--;
					if ( localDag0[ bb ] == bb ) {		// Try to point bb to aa
						
						localDag0[ bb ] = aa;
						_edgesToProcess.get( j ).setXPositive( false );
					}		
					else if ( localDag0[ aa ] == aa ){		// If we can't point bb to aa, try to point aa to bb
						
						localDag0[ aa ] = bb;
						_edgesToProcess.get( j ).setXPositive( false );
					}	
				}
				
				if (count == 1) break;				// If count == 1, no more assignments can be
									// made so we break out of the for loop.
			}
		}

//		zeroCycles = new ArrayList<Integer>();
		System.out.println( "\t\t---[Func1] localDag0 after processing: " );
		for ( int i=0; i<localDag0.length; i++ ) {
			
			System.out.println( "\t\t" + localDag0[ i ] );
//			if ( i == localDag0[ i ] ) {
//
//				zeroCycles.add( i );
//			}
		}
		
		System.out.println( "\t\t---[Func1] done. " );
    	
//    	return zeroCycles;
		return localDag0;
    }

    public ArrayList<ArrayList<EdgeI>> make1Cycles ( final int _nbrOfVertices,
    		final ArrayList<EdgeI> _edgesToProcess, int[] _localDag0 ) throws Exception {    
//    protected ArrayList<ArrayList<EdgeI>> make1Cycles ( final int _nbrOfVertices,
//    		final ArrayList<EdgeI> _edgesToProcess, ArrayList<Integer> _localDag0 ) throws Exception {

    	ArrayList <EdgeI> tempEdgeList; // = new ArrayList<EdgeI>();
    	ArrayList <ArrayList <EdgeI>> edgeListsToReturn = new ArrayList <ArrayList<EdgeI>>();

    	EdgeMatrixI localEdgeMatrix = new EdgeMatrixAsMultiDimArray( _nbrOfVertices );
    	
    	EdgeI edgeToProcess;
    	int a, b;
    	int tmpEdgeIndex;

		System.out.println( "\t[make1Cycle] List of edges:" );
		for ( int k=0; k<_edgesToProcess.size(); k++ ) {
			
			System.out.println( "\t" + _edgesToProcess.get( k ));
			localEdgeMatrix.setEdgeIndex( _edgesToProcess.get( k ).getVertexIndex1(), 
					_edgesToProcess.get( k ).getVertexIndex2(), k );
		}

		
		// now process each of the edges
		for ( int k=0; k<_edgesToProcess.size(); k++ ) {
			
			edgeToProcess = _edgesToProcess.get( k );
			
			// the edge is from a local list, so checking if it is positive means looking at xPositive:
			if ( edgeToProcess.isXPositive() ) {
				
				// reset the temporary list for this edge
				tempEdgeList = new ArrayList<EdgeI>();
				
				// add the edge itself to the new list:
				tempEdgeList.add( edgeToProcess );
				

				a = edgeToProcess.getVertexIndex1();
				while( a != _localDag0[ a ] ) {
					
					tmpEdgeIndex = localEdgeMatrix.getEdgeIndex( a, _localDag0[ a ] );
					tempEdgeList.add( _edgesToProcess.get( tmpEdgeIndex ));
					a = _localDag0[ a ];
				}
				
				
				
				b = edgeToProcess.getVertexIndex2();
				while( b != _localDag0[ b ] ) {

					tmpEdgeIndex = localEdgeMatrix.getEdgeIndex( b, _localDag0[ b ] );
					tempEdgeList.add( _edgesToProcess.get( tmpEdgeIndex ));
					b = _localDag0[ b ];
				}
				
			
				// add the temp list to our list of edge-lists to return
				edgeListsToReturn.add( tempEdgeList );
			}
		}

		System.out.println( "\t\t---[make1Cycles] done. " );
    	
    	return edgeListsToReturn;
    
    }
    
    protected ArrayList<ArrayList<EdgeI>> Func4 ( final int _nbrOfVertices,
    		final ArrayList<EdgeI> _edgesToProcess, 
    		int[] _localDag0, 
    		int _specifiedEdgeIndex ) throws Exception {

    	ArrayList <EdgeI> tempEdgeList; // = new ArrayList<EdgeI>();
    	ArrayList <ArrayList <EdgeI>> edgeListsToReturn = new ArrayList <ArrayList<EdgeI>>();

    	EdgeI edgeToProcess;
    	int a, b;		
		
		// here we only process the edge with index '_specifiedEdgeIndex':
		edgeToProcess = _edgesToProcess.get( _specifiedEdgeIndex );
		
		// the edge is from a local list, so checking if it is positive means looking at xPositive:
		if ( edgeToProcess.isXPositive() ) {
			
			// reset the temporary list for this edge
			tempEdgeList = new ArrayList<EdgeI>();
			
			// add the edge itself to the new list:
			tempEdgeList.add( edgeToProcess );
			

			a = edgeToProcess.getVertexIndex1();
			while( a != _localDag0[ a ] ) {
				
//						tempEdgeList.add( a, ...);
				a = _localDag0[ a ];
			}			
			
			b = edgeToProcess.getVertexIndex2();
			while( b != _localDag0[ b ] ) {
				
//						tempEdgeList.add( b, ...);
				b = _localDag0[ b ];
			}
			
			// add the temp list to our list of edge-lists to return
			edgeListsToReturn.add( tempEdgeList );
		}

		System.out.println( "\t\t---[Func4] done. " );
    	
    	return edgeListsToReturn;
    
    }
    
    protected ArrayList<ArrayList<EdgeI>> makeOneCycles ( final int _nbrOfVertices,
    		final ArrayList<EdgeI> _edgesToProcess ) throws Exception {

    	ArrayList <EdgeI> tempEdgeList; // = new ArrayList<EdgeI>();
    	ArrayList <ArrayList <EdgeI>> edgeListsToReturn = new ArrayList <ArrayList<EdgeI>>();

    	EdgeMatrixI localEdgeMatrix = new EdgeMatrixAsMultiDimArray( _nbrOfVertices );
    	
    	int vertex1;
    	int vertex2;
    	EdgeI edgeToProcess;
    	boolean isPositiveValue;
    	
//		int[] localVertices;
    	int[] localDag0;
    	int[] U;
		int numberOfEdges = _edgesToProcess.size();
    	

		int a, aa, b, bb;
			
			
			
		System.out.println( "\t[makeOneCycle] List of edges:" );
		for ( int k=0; k<_edgesToProcess.size(); k++ ) {
			
			System.out.println( "\t" + _edgesToProcess.get( k ));
			localEdgeMatrix.setEdgeIndex( _edgesToProcess.get( k ).getVertexIndex1(), 
					_edgesToProcess.get( k ).getVertexIndex2(), k );
		}

		localDag0 = new int[ _nbrOfVertices ];
		
		
//		System.out.println( "\t\t---[makeOneCycle] numberOfEdges = " + numberOfEdges );
		if( numberOfEdges > 0 ) {
			
//				int a, aa, b, bb;
			int count = _nbrOfVertices;
			int tmpEdgeListIndex;
			
			U = new  int[ _nbrOfVertices ];
			EdgeI tempEdge; 				// tempEdge should use edge1 format
			// Declare and clear localDag0 and U;
			
			for ( int i=0; i<_nbrOfVertices; ++i ) { 

				localDag0[ i ] = i;
				U[ i ] = i;
			}


			System.out.println( "\t\t---[makeOneCycle] localDag0 after setup: " );
			for ( int i=0; i<localDag0.length; i++ ) {
				
				System.out.println( "\t\t" + localDag0[ i ] );
			}
			
			for ( int j=0; j<numberOfEdges; j++ ) {
							
				tempEdge = _edgesToProcess.get( j ); 
				aa = tempEdge.getVertexIndex1(); 
				bb = tempEdge.getVertexIndex2();	// Assumes that aa < bb -- This is critical for the numberOfLocalVertices look
				a = aa; 
				b = bb;
				
				while ( U[ a ] != a ) { a = U[ a ]; } 
				while ( U[ b ] != b ) { b = U[ b ]; } 
				
				if ( a!=b ){					// Don't point if forms a loop
				
					U[ b ] = a;
					count--;
					if ( localDag0[ bb ] == bb ) {		// Try to point bb to aa
						
						localDag0[ bb ] = aa;
						_edgesToProcess.get( j ).setXPositive( false );
					}		
					else if ( localDag0[ aa ] == aa ){		// If we can't point bb to aa, try to point aa to bb
						
						localDag0[ aa ] = bb;
						_edgesToProcess.get( j ).setXPositive( false );
					}	
				}
				
				if (count == 1) break;				// If count == 1, no more assignments can be
									// made so we break out of the for loop.
			}
		}

		System.out.println( "\t\t---[makeOneCycle] localDag0 after processing: " );
		for ( int i=0; i<localDag0.length; i++ ) {
			
			System.out.println( "\t\t" + localDag0[ i ] );
		}
		
		int tmpEdgeIndex;
		// now process each of the edges
		for ( int k=0; k<_edgesToProcess.size(); k++ ) {
			
			edgeToProcess = _edgesToProcess.get( k );
			
			// the edge is from a local list, so checking if it is positive means looking at xPositive:
			if ( edgeToProcess.isXPositive() ) {
				
				// reset the temporary list for this edge
				tempEdgeList = new ArrayList<EdgeI>();
				
				// add the edge itself to the new list:
				tempEdgeList.add( edgeToProcess );
				

				a = edgeToProcess.getVertexIndex1();
				while( a != localDag0[ a ] ) {
					
					tmpEdgeIndex = localEdgeMatrix.getEdgeIndex( a, localDag0[ a ] );
					tempEdgeList.add( _edgesToProcess.get( tmpEdgeIndex ));
					a = localDag0[ a ];
				}
				
				
				
				b = edgeToProcess.getVertexIndex2();
				while( b != localDag0[ b ] ) {

					tmpEdgeIndex = localEdgeMatrix.getEdgeIndex( b, localDag0[ b ] );
					tempEdgeList.add( _edgesToProcess.get( tmpEdgeIndex ));
					b = localDag0[ b ];
				}
				
				
			
				// add the temp list to our list of edge-lists to return
				edgeListsToReturn.add( tempEdgeList );
			}
		}

		System.out.println( "\t\t---[makeOneCycle] done. " );
    	
    	return edgeListsToReturn;
    }
    

    public M12PersistenceMatrix computeMatrixM23() throws Exception {

    	final int numberOfEdges = edges_.size();
    	int vertex1;
    	int vertex2;
    	EdgeI edgeToProcess;
    	boolean isPositiveValue;

		SortedMap<Integer, Integer> columnList = new TreeMap<Integer, Integer>();

    	// M23
    	ArrayList<EdgeI> localEdges = new ArrayList<EdgeI>();
		int[] localVertices;
    	int[] localDag0;
    	int[] U;
		int numberOfLocalEdges;
		int numberOfLocalVertices;
		
    	FaceI currentFace;
    	ArrayList<FaceI> faceList = new ArrayList<FaceI>();
    	int currentFaceIndex;
    	LocalEdgeFinder localEdgeFinder;
    			
//		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
//	    	
//        	strBufCollectFeedback_.append( "\n" );
//	    	strBufCollectFeedback_.append( "\n*** After assignEdgeProperties: EdgeList edgeList(complete) = " );
//	    	strBufCollectFeedback_.append( "\n" + this.asStringComplete() );
//		}
    	
		
    	// now go through edges based on the edgeType
    	for ( int currentEdgeIndex=0; currentEdgeIndex<numberOfEdges; currentEdgeIndex++ ) {
			
    		// get the edge info
    		edgeToProcess = edges_.get( currentEdgeIndex );
    		
    		if ( edgeToProcess.getEdgeLength() > 11 ) { // last 2 edges for simple diamond
    			
    			int x = 1;
    		}

    		// crucial: build up the entity
    		builtUpEdges_.add( edgeToProcess );

    		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    	    	
            	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    		strBufCollectFeedback_.append( "\nProcessing edge i=" + currentEdgeIndex );
	    		strBufCollectFeedback_.append( "\n------------------------------- " );
    		}
    		
        	System.out.println( strBufCollectFeedback_.toString() );
        	strBufCollectFeedback_ = new StringBuffer( TDA.BUFFERLENGTH_STAT );

    		int v1, v2;
    		vertex1 = edgeToProcess.getVertexIndex1();
    		vertex2 = edgeToProcess.getVertexIndex2();
    		isPositiveValue = edgeToProcess.isUPositive();
    		
    		
    		
    		
    		// hjs 12/9/2014 -- try
//			// Add the current edge's index to the local lists of each of its vertices
//			((ArrayList<Integer>) localListL_.get( vertex1 )).add( new Integer(currentEdgeIndex) );
//			((ArrayList<Integer>) localListL_.get( vertex2 )).add( new Integer(currentEdgeIndex) );
//			
//			// Add the current edge's OTHER VERTEX to the local lists Lv of each of its vertices
//			((ArrayList<Integer>) localListLv_.get( vertex1 )).add( new Integer(
//					edgeToProcess.getOtherVertex( vertex1 ) ) );
//			((ArrayList<Integer>) localListLv_.get( vertex2 )).add( new Integer( 
//					edgeToProcess.getOtherVertex( vertex2 ) ) );
//			Collections.sort( localListLv_.get( vertex1 ) );
//			Collections.sort( localListLv_.get( vertex2 ) );

    		
    		
    		
    		
    		
    		// if edge is positive:
	    	if ( isPositiveValue ) {
	    		
	    		v1 = vertex1;
	    		v2 = vertex2;
	    		
	    		// find the list of 'local edges'
	    		localEdgeFinder = new LocalEdgeFinder( edgeToProcess );
	    		localEdges = localEdgeFinder.getLocalEdges(); //  findLocalEdges( edgeToProcess );
				numberOfLocalEdges = localEdges.size();
				// Note that the localEdgeFinder also computed the localVertices, so:
				localVertices = localEdgeFinder.getLocalVertices();  // localVertices_;
				numberOfLocalVertices = localVertices.length;
				
				System.out.println( "\tList of localEdges:" );
				for ( int k=0; k<localEdges.size(); k++ ) {
					
					System.out.println( "\t" + localEdges.get( k ));
				}

				numberOfLocalVertices = localVertices.length;
//				localDag0 = new int[ numberOfLocalVertices ];
				
				System.out.println( "\t\t--- localVertices: " );
				for ( int i=0; i<localVertices.length; i++ ) {
					
					System.out.println( "\t\t" + localVertices[ i ] );
				}
				
	    		
	    		// so localVertices converts local indices to global
				
//				System.out.println( "\t\t--- numberOfLocalEdges = " + numberOfLocalEdges );
//				if( numberOfLocalEdges > 0 ) {
//					
//					int a, aa, b, bb;
//					int count = numberOfLocalVertices;
//					int tmpEdgeListIndex;
//					
//					U = new  int[ numberOfLocalVertices ];
//					EdgeI tempEdge; 				// tempEdge should use edge1 format
//					// Declare and clear localDag0 and U;
//					
//					for ( int i=0; i<numberOfLocalVertices; ++i ) { 
//
//						// deprec:
//						localDag0[ i ] = i;
//						U[ i ] = i;
//					}
//
//
//					System.out.println( "\t\t--- localDag0 after setup: " );
//					for ( int i=0; i<localDag0.length; i++ ) {
//						
//						System.out.println( "\t\t" + localDag0[ i ] );
//					}
//					
//					for ( int j=0; j<numberOfLocalEdges; j++ ) {
//									
//						tempEdge = localEdges.get( j ); 
//						aa = tempEdge.getVertexIndex1(); 
//						bb = tempEdge.getVertexIndex2();	// Assumes that aa < bb -- This is critical for the numberOfLocalVertices look
//						a = aa; 
//						b = bb;
//						
//						while ( U[ a ] != a ) { a = U[ a ]; } 
//						while ( U[ b ] != b ) { b = U[ b ]; } 
//						
//						if ( a!=b ){					// Don't point if forms a loop
//						
//							U[ b ] = a;
//							count--;
//							if ( localDag0[ bb ] == bb ) {		// Try to point bb to aa
//								
//								localDag0[ bb ] = aa;
//								localEdges.get( j ).setXPositive( false );
//							}		
//							else if ( localDag0[ aa ] == aa ){		// If we can't point bb to aa, try to point aa to bb
//								
//								localDag0[ aa ] = bb;
//								localEdges.get( j ).setXPositive( false );
//							}	
//						}
//						
//						// hjs 12/9/2014: replaced by above changes (working with John)
////						if ( a!=b ){					// Don't point if forms a loop
////						
////							U[ b ] = a;
////							count--;
////							if ( localDag0[ b ] == b ) {		// Try to point bb to aa
////								
////								localDag0[ b ] = a;
////								localEdges.get( j ).setXPositive( false );
////							}		
////							else if ( localDag0[ a ] == a ){		// If we can't point bb to aa, try to point aa to bb
////								
////								localDag0[ a ] = b;
////								localEdges.get( j ).setXPositive( false );
////							}	
////						}
//						
//						if (count == 1) break;				// If count == 1, no more assignments can be
//											// made so we break out of the for loop.
//					}
//				}
				

//				System.out.println( "\t\t--- numberOfLocalEdges = " + numberOfLocalEdges );
				if( numberOfLocalEdges > 0 ) {
					
					localDag0 = make0Cycles( numberOfLocalVertices, localEdges );				

					System.out.println( "\t\t--- localDag0 (0-cycles): " );
					for ( int i=0; i<localDag0.length; i++ ) {
						
						System.out.println( "\t\t" + localDag0[ i ] );
					}
//					for ( int i=0; i<localDag0.size(); i++ ) {
//						
//						System.out.println( "\t\t" + localDag0.get( i ) );
//					}
				
				
				
					ArrayList<ArrayList<EdgeI>> listOfOneCycles = 
//							makeOneCycles( localEdges.size(), localEdges );
							make1Cycles ( numberOfLocalVertices, localEdges, localDag0 );
					
					
	
					System.out.println( "\t\t+++ (prelim. 1-Cycles): " );
					ArrayList<EdgeI> tmpList;
					for ( int i=0; i<listOfOneCycles.size(); i++ ) {
						
						tmpList = listOfOneCycles.get( i );
						System.out.println( "\t\t list i=" + i + "\n" + this.printArrayList( tmpList ) );
					}
					System.out.println( "\t\t++++++  " );
					
				
					//INTRODUCE GLOBAL INDICES FOR TRIANGLES
					
					// Start loop at j=1 because edge is paired with the j=0 face which 
					// therefore doesn't need to be stored.
					for ( int j=1; j<numberOfLocalVertices; j++ ) {
						
						
						
						// make new face and new entry for F3 at the triple (v1,v2,localVertex[j])
						System.out.println( "\nCalling faceFactory to create new Face, loop-j="
									+ j 
									+ ":  v1=" + v1
									+ ", v2=" + v2
									+ ", localVertices[j]=" + localVertices[ j ]
	//								+ ", nbrLocVert=" + numberOfLocalVertices
									);

						// hjs 1/9/2015 pass along the current edge (needed for dag2List)
						currentFace = faceFactory_.createFace( edgeToProcess, localVertices[ j ] );
//						currentFace = faceFactory_.createFace( v1, v2, localVertices[ j ] );
						
				    	System.out.println( "New face: " + currentFace );
				    	
				    	currentFaceIndex = currentFace.getFaceListIndex();
				    	System.out.println( "(running) index of the new face: " + currentFaceIndex );
				    	
				    	

				    	// hjs 1/8/2015
					    // For currentFace, add itself to its dagList
				    	currentFace.addToDagList( new Integer( currentFaceIndex ),
		    					new Integer( 1 ) );
		    			
				    	
				    	// may want to add after face is "finalized"?
				    	faceList.add( currentFace );
				    	System.out.println( "\n  *** Current face-list:\n" + faceList );
				    	
				    	System.out.println( "\n  *** Is localDag0[j] == j  ::  " 
				    				+ localDag0[j] + " =? " + j );
						
		//				** int index = makeNewTriangle( v1, v2, localVertex[j] );localVertices
						if ( localDag0[j] == j ){
						
	//						System.out.println( "Adding row to M23 matrix: "
	//						+ "faceIndex = " + currentFaceIndex + ", currentEdgeIndex= " + currentEdgeIndex );
							// ******
							// Adding row
							// ******
							strBufCollectFeedback_.append( "\nAdding row to M23 matrix: "
									+ "faceIndex = " + currentFaceIndex + ", currentEdgeIndex= " + currentEdgeIndex );
							// add a row to M[2,3] for this triangle
	//		    			matrixM12_.addRowEdgeListEntry( currentFaceIndex );
							strBufCollectFeedback_.append( "\nAdding M23 row entry: " + 
									currentEdgeIndex );
			    			matrixM12_.addRowEdgeListEntry( currentEdgeIndex );
						}
						// localDag0[j] != j  means that we only need a dag2List addition to triangle triangleList[index] 
						else{
							
	//						tmpFace.addToDagList( edgeIndex1, edgeIndex2 );
						
							if ( j < localDag0.length ) {
								
								FaceI tmpFace;
								int tmpFaceIndex;
								SortedMap<Integer, Integer> tmpDagList;
								
								System.out.println( "localDag0[ " + j + " ]= " + localDag0[j] );
								System.out.println( "   [before] Merging daglists for face=" + currentFace );
								System.out.println( "   and face=" );
								System.out.println( F3( v1, v2,
										localVertices[ localDag0[j] ] ) );
								
								tmpFaceIndex = F3( v1, v2,
										localVertices[ localDag0[j] ] );
								if ( tmpFaceIndex != -1 ) {
								
									tmpFace = faceList.get( tmpFaceIndex );
									tmpDagList = tmpFace.getDagList();
									if ( !tmpDagList.isEmpty() ) {
									
										currentFace.mergeIntoDagList( tmpDagList );
									}
	//								System.out.println( "   with face=" + tmpFace );
	//								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
	//								System.out.println( "   - tmpDagList=" + tmpDagList );
								}
	//							else {
	//								
	//								System.out.println( "   - Face doesn't exist: " 
	//											+ "v1=" + v1 
	//											+ ", v2=" + v2
	//											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
	//							}
								// these 2 correspond to edgePairs, except that the second
								// index is replaced by the "dagList"-index
	//							System.out.println( "   and face=" + F3( v1, localVertices[j],
	//									localVertices[ localDag0[j] ] ) );
								
	
	
								tmpFaceIndex = F3( v1, localVertices[j],
										localVertices[ localDag0[j] ] );
								if ( tmpFaceIndex != -1 ) {
								
									tmpFace = faceList.get( tmpFaceIndex );
									tmpDagList = tmpFace.getDagList();
									if ( !tmpDagList.isEmpty() ) {
									
										currentFace.mergeIntoDagList( tmpDagList );
									}
	//								System.out.println( "   with face=" + tmpFace );
	//								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
	//								System.out.println( "   - tmpDagList=" + tmpDagList );
								}
	//							else {
	//								
	//								System.out.println( "   - Face doesn't exist: " 
	//											+ "v1=" + v1 
	//											+ ", locV[j]=" + localVertices[j]
	//											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
	//							}
	
								tmpFaceIndex = F3( v2, localVertices[j],
										localVertices[ localDag0[j] ] );
								if ( tmpFaceIndex != -1 ) {
								
									tmpFace = faceList.get( tmpFaceIndex );
									tmpDagList = tmpFace.getDagList();
									if ( !tmpDagList.isEmpty() ) {
									
										currentFace.mergeIntoDagList( tmpDagList );
									}
	//								System.out.println( "   with face=" + tmpFace );
	//								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
	//								System.out.println( "   - tmpDagList=" + tmpDagList );
								}
	//							else {
	//								
	//								System.out.println( "   - Face doesn't exist: " 
	//											+ "v2=" + v2 
	//											+ ", locV[j]=" + localVertices[j]
	//											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
	//							}
							}
						}
					}
				
					int tmpFaceIndex;
					FaceI tmpFace;
					// Build columns of M23. We'll get one for every element of localEdges which is Pos (isPos ==T)
					// This requires a search through the dag. We could replace this by passing lists as we did with M[1,2]
					// but I'm afraid that the storage requirement will get too big. Try later, perhaps.
					for( int j=0; j<numberOfLocalEdges; j++ ){
						     	
						EdgeI tempEdge = localEdges.get( j );
						
						if( tempEdge.isXPositive() ) {
							
							columnList.clear();
							int c = tempEdge.getVertexIndex1(); 
							int d = tempEdge.getVertexIndex2();
							
							tmpFaceIndex = F3(v1,c,d);
							if ( faceList.contains( tmpFaceIndex )) {
								
								tmpFace = faceList.get( F3( v1,c,d ) );
								columnList.putAll( faceList.get( F3(v1,c,d) ).getDagList() );
	//							System.out.println( "   b: adding to columnList, dagList=" + faceList.get( F3(v1,c,d) ).getDagList() );
							}
							
							tmpFaceIndex = F3(v2,c,d);
							if ( faceList.contains( tmpFaceIndex )) {
								
								tmpFace = faceList.get( F3( v2,c,d ) );
								columnList.putAll( faceList.get( F3(v2,c,d) ).getDagList() );
	//							System.out.println( "   b: adding to columnList, dagList=" + faceList.get( F3(v2,c,d) ).getDagList() );
							}
							
							
							// TODO: this is not safe??? Or do the faces exist??? Actually: YES (v1 and v2 are
							// the vertices of the current edge, and c,d are the vertices of the edge between 
							// "opposite vertices", so by construction, this face will exist, and will have
							// some dagList -- TODO: check if that dagList <> null, and what to do if null
	//						columnList.putAll( faceList.get( F3(v1,c,d) ).getDagList() );
	//						columnList.putAll( faceList.get( F3(v2,c,d) ).getDagList() );
		
							
							// add columnList to M[2,3]
	
							if ( columnList.size() > 0 ) {
								
	//							System.out.println( "Adding column to M23 matrix: "
	//									+ "currentEdgeIndex = " + currentEdgeIndex +
	//									"\ncolumnList = " + columnList );
							// ???
								strBufCollectFeedback_.append( "\nAdding M23 column entry: " + 
										currentEdgeIndex );
							
								this.matrixM12_.addColumn( currentEdgeIndex, 
										columnList );
	//							System.out.println( "   ^^^: adding Col, columnList=" + columnList );
							}
						}
					}

				}
				else {

					System.out.println( "\t\t--- numberOfLocalEdges==0, so localDag0 is empty " );
				}
			// Add vertices to L
			} // end of 'isPositiveValue=true'
			
			
			//From here we return values, etc. as usual.
				

	    	
			
////			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
//	    	
//	        	strBufCollectFeedback_.append( "\n" +
//					"\n-- localListL(" + vertex1 + ") = " );
//	        	if ( localListL_.get( vertex1 ).size()>0 ) {
//		        	strBufCollectFeedback_.append( 
//							this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex1 ) ) );
//	        	}
//	        	strBufCollectFeedback_.append( "\n-- localListL(" + vertex2 + ") = " );
//	        	if ( localListL_.get( vertex2 ).size()>0 ) {
//		        	strBufCollectFeedback_.append( 
//							this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex2 ) ) );
//	        	} 
//	        	strBufCollectFeedback_.append( 
////	        			this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
//					"\n-- localListL = " + 
//						this.printArrayList( localListL_ ) );
//		    	
//	        	
//	        	strBufCollectFeedback_.append( "\n" +
//					"\n-- localListLv(" + vertex1 + ") = " );
//	        	if ( localListL_.get( vertex1 ).size()>0 ) {
//	        		strBufCollectFeedback_.append( 
//	        				this.printArrayList( ( ArrayList<Integer> ) localListLv_.get( vertex1 ) ) );
//	        	};
//	        	strBufCollectFeedback_.append( "\n-- localListLv(" + vertex2 + ") = " ); 
//	        	if ( localListL_.get( vertex2 ).size()>0 ) {
//	        		strBufCollectFeedback_.append( 
//	        				this.printArrayList( ( ArrayList<Integer> )localListLv_.get( vertex2 ) )  );
//	        	}
//	        	System.out.println( strBufCollectFeedback_.toString() );
//	        	strBufCollectFeedback_ = new StringBuffer( TDA.BUFFERLENGTH_STAT );
//			}	
				

	    	// TODO: check on the "timing" of these calls: (may need to be placed earlier?)

			// Add the current edge's index to the local lists of each of its vertices
			((ArrayList<Integer>) localListL_.get( vertex1 )).add( new Integer(currentEdgeIndex) );
			((ArrayList<Integer>) localListL_.get( vertex2 )).add( new Integer(currentEdgeIndex) );
			
			// Add the current edge's OTHER VERTEX to the local lists Lv of each of its vertices
			((ArrayList<Integer>) localListLv_.get( vertex1 )).add( new Integer(
					edgeToProcess.getOtherVertex( vertex1 ) ) );
			((ArrayList<Integer>) localListLv_.get( vertex2 )).add( new Integer( 
					edgeToProcess.getOtherVertex( vertex2 ) ) );
			Collections.sort( localListLv_.get( vertex1 ) );
			Collections.sort( localListLv_.get( vertex2 ) );


        	strBufCollectFeedback_.append( "\n" +
				"\n-- localListL(" + vertex1 + ") = " );
        	if ( localListL_.get( vertex1 ).size()>0 ) {
	        	strBufCollectFeedback_.append( 
						this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex1 ) ) );
        	}
        	strBufCollectFeedback_.append( "\n-- localListL(" + vertex2 + ") = " );
        	if ( localListL_.get( vertex2 ).size()>0 ) {
	        	strBufCollectFeedback_.append( 
						this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex2 ) ) );
        	} 
        	strBufCollectFeedback_.append( 
//        			this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
				"\n-- localListL = " + 
					this.printArrayList( localListL_ ) );
	    	
        	
        	strBufCollectFeedback_.append( "\n" +
				"\n-- localListLv(" + vertex1 + ") = " );
        	if ( localListL_.get( vertex1 ).size()>0 ) {
        		strBufCollectFeedback_.append( 
        				this.printArrayList( ( ArrayList<Integer> ) localListLv_.get( vertex1 ) ) );
        	};
        	strBufCollectFeedback_.append( "\n-- localListLv(" + vertex2 + ") = " ); 
        	if ( localListL_.get( vertex2 ).size()>0 ) {
        		strBufCollectFeedback_.append( 
        				this.printArrayList( ( ArrayList<Integer> )localListLv_.get( vertex2 ) )  );
        	}
        	strBufCollectFeedback_.append( 
//        			this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
				"\n-- localListLv = " + 
					this.printArrayList( localListLv_ ) );
        	System.out.println( strBufCollectFeedback_.toString() );
        	strBufCollectFeedback_ = new StringBuffer( TDA.BUFFERLENGTH_STAT );
    	}
    		
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
    		strBufCollectFeedback_.append( "\n\n------------------------------- " );
    		strBufCollectFeedback_.append( "\n\nedgeList:\n"  + this.asStringComplete() );
    		strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	}

    	// All the edges are processed, and we have our reduction matrix:
    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\nReduction Matrix M23" );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n" + this.matrixM12_.toString() );

    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {

        	System.out.println( strBufCollectFeedback_.toString() );
    	}
    	
//    	System.out.println( "Done computing M23." );
    	
		return this.matrixM12_;
    }
    
    public M12PersistenceMatrix computeMatrixM23_withFeedback() throws Exception {

    	final int numberOfEdges = edges_.size();
    	int vertex1;
    	int vertex2;
    	EdgeI edgeToProcess;
    	boolean isPositiveValue;
    	EdgeI edge1;
    	EdgeI edge2;
    	List<EdgePair> edgePairList = new ArrayList<EdgePair>();
		EdgePair edgePairToProcess;
    	int oppositeVertex;

		SortedMap<Integer, Integer> columnList = new TreeMap<Integer, Integer>();

    	// M23
//    	EdgeI[] localEdges;
    	ArrayList<EdgeI> localEdges = new ArrayList<EdgeI>();
		int[] localVertices;
    	int[] localDag0;
    	int[] U;
		int numberOfLocalEdges;
		int numberOfLocalVertices;
		
    	FaceI currentFace;
    	ArrayList<FaceI> faceList = new ArrayList<FaceI>();
    	int currentFaceIndex;

    	LocalEdgeFinder localEdgeFinder;
    	
		// for local output only
//    	SortedMap<Integer, Integer> dagList;
//		EdgeI tmpEdge = edgeFactory_.createEdge( -1, -1 );
		
		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    	
        	strBufCollectFeedback_.append( "\n" );
	    	strBufCollectFeedback_.append( "\n*** After assignEdgeProperties: EdgeList edgeList(complete) = " );
	    	strBufCollectFeedback_.append( "\n" + this.asStringComplete() );
		}
    	
		
    	// now go through edges based on the edgeType
    	for ( int currentEdgeIndex=0; currentEdgeIndex<numberOfEdges; currentEdgeIndex++ ) {
			
    		// get the edge info
    		edgeToProcess = edges_.get( currentEdgeIndex );

//	    	System.out.println( "\n*****\n" +
//	    			"Processing edgeIdx=" + currentEdgeIndex + ", edge=" + edgeToProcess +
//	    			"\n*****\n" );
    		    		
    		// crucial: build up the entity
    		builtUpEdges_.add( edgeToProcess );

    		if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    	    	
            	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    		strBufCollectFeedback_.append( "\nProcessing edge i=" + currentEdgeIndex );
	    		strBufCollectFeedback_.append( "\n------------------------------- " );
    		}

    		int v1, v2;
    		vertex1 = edgeToProcess.getVertexIndex1();
    		vertex2 = edgeToProcess.getVertexIndex2();
    		isPositiveValue = edgeToProcess.isUPositive();
    		
    		
    		// if edge is positive:
	    	if ( isPositiveValue ) {
	    		
	    		v1 = vertex1;
	    		v2 = vertex2;
	    		
	    		
	//    		localVertices = intersect( L[v1],L[v2] );		// localVertices[0], … , localVertices[r-1] = vertex indices in intersection
	//    		localVertices = findIntersectList( v1, v2 );
	//    		localEdges = findLocalVertices( v1, v2 );
	    		if ( edgeToProcess.getEdgeListIndex() > 12 ) {
	    			
	    			int x;
	    			x = 4;
	    		}


	    		localEdgeFinder = new LocalEdgeFinder( edgeToProcess );
	    		localEdges = localEdgeFinder.getLocalEdges(); //  findLocalEdges( edgeToProcess );
				numberOfLocalEdges = localEdges.size();
				// Note that the localEdgeFinder also computed the localVertices, so:
				localVertices = localEdgeFinder.getLocalVertices();  // localVertices_;
				numberOfLocalVertices = localVertices.length;
				
				
				
//	    		localEdges = findLocalEdges( edgeToProcess );
//				numberOfLocalEdges = localEdges.size();
				
				System.out.println( "\tList of localEdges:" );
				for ( int k=0; k<localEdges.size(); k++ ) {
					
					System.out.println( "\t" + localEdges.get( k ));
				}
				// !!!
				// wrong
	//			numberOfLocalVertices = numberOfLocalEdges;
	//			localVertices = new int[ numberOfLocalVertices ];
	//			localDag0 = new int[ numberOfLocalVertices ];
				
				
//				localVertices = localVertices_;
//				numberOfLocalVertices = localVertices.length;
				localDag0 = new int[ numberOfLocalVertices ];
				
//				for ( int i=0; i<numberOfLocalVertices; i++ ) {
//					
//					// we know that every local edge has vertex v1 or v2, so we
//					// easily find the "other" vertices
//					if ( localEdges.get( i ).hasVertex( v1 ) ) {
//	
//						localVertices[ i ] = localEdges.get( i ).getOtherVertex( v1 );
//					}
//					else {
//	
//						localVertices[ i ] = localEdges.get( i ).getOtherVertex( v2 );
//					}
//	
//					System.out.println( "localVertices[ " + i + " ] = " + localVertices[i] );
//					System.out.println( "localEdges[ " + i + " ] = " + localEdges.get( i ) );
//				}

				
				System.out.println( "\t\t--- localVertices: " );
				for ( int i=0; i<localVertices.length; i++ ) {
					
					System.out.println( "\t\t" + localVertices[ i ] );
				}
				
	    		
	    		// so localVertices converts local indices to global
	    		
	//			numberOfLocalVertices = localVertices.length;
	//			if (numberOfLocalVertices > 0 ){
							
					// Make list of edges in lower link of edgeList[i]
					//Initialize empty list localEdges that use the edge1 format
	    		//// redundant
	//				for ( int c=0; c<numberOfLocalVertices; c++ ) {
	//					for( int d=c+1; d<numberOfLocalVertices; d++ ) {
	//						
	//						int tempEdgeIndex = F2( localVertices[c], localVertices[d] );
	//						if ( tempEdgeIndex != -1 && tempEdgeIndex < currentEdgeIndex ) 
	//							localEdges.add();  //addEdge(tempEdgeIndex);    // isPos set to T      
	//					}
	//				}
				
							// THIS PART IS NEW
							// localDag0 works with local indices only
	//
	//				numberOfLocalEdges = localEdges.size();
	//				// !!!
	//				numberOfLocalVertices = numberOfLocalEdges;
			
//				xxxxx
				System.out.println( "\t\t--- numberOfLocalEdges = " + numberOfLocalEdges );
				if( numberOfLocalEdges > 0 ) {
					
					int a, aa, b, bb;
					int count = numberOfLocalVertices;
					int tmpEdgeListIndex;
					
					U = new  int[ numberOfLocalVertices ];
					EdgeI tempEdge; 				// tempEdge should use edge1 format
					// Declare and clear localDag0 and U;
					
					for ( int i=0; i<numberOfLocalVertices; ++i ) { 

						// NO: this is not right:
//						tmpEdgeListIndex = localEdges.get( i ).getEdgeListIndex();
//						localDag0[ tmpEdgeListIndex ] = tmpEdgeListIndex;
//						U[ tmpEdgeListIndex ] = tmpEdgeListIndex;
						// deprec:
						localDag0[ i ] = i;
						U[ i ] = i;
					}


					System.out.println( "\t\t--- localDag0 after setup: " );
					for ( int i=0; i<localDag0.length; i++ ) {
						
						System.out.println( "\t\t" + localDag0[ i ] );
					}
					
					for ( int j=0; j<numberOfLocalEdges; j++ ) {
									
						tempEdge = localEdges.get( j ); 
						aa = tempEdge.getVertexIndex1(); 
						bb = tempEdge.getVertexIndex2();	// Assumes that aa < bb -- This is critical for the numberOfLocalVertices look
						a = aa; 
						b = bb;
						
						while ( U[ a ] != a ) { a = U[ a ]; } 
						while ( U[ b ] != b ) { b = U[ b ]; } 
						
						if ( a!=b ){					// Don't point if forms a loop
						
							U[ b ] = a;
							count--;
							if ( localDag0[ b ] == b ) {		// Try to point bb to aa
								
								localDag0[ b ] = a;
								localEdges.get( j ).setXPositive( false );
							}		
							else if ( localDag0[ a ] == a ){		// If we can't point bb to aa, try to point aa to bb
								
								localDag0[ a ] = b;
								localEdges.get( j ).setXPositive( false );
							}	
						}
						
						if (count == 1) break;				// If count == 1, no more assignments can be
											// made so we break out of the for loop.
					}
				}

				System.out.println( "\t\t--- localDag0 after processing: " );
				for ( int i=0; i<localDag0.length; i++ ) {
					
					System.out.println( "\t\t" + localDag0[ i ] );
				}
			
				//INTRODUCE GLOBAL INDICES FOR TRIANGLES
				
				// Start loop at j=1 because edge is paired with the j=0 face which 
				// therefore doesn't need to be stored.
				for ( int j=1; j<numberOfLocalVertices; j++ ) {
					
					
					
					// make new face and new entry for F3 at the triple (v1,v2,localVertex[j])
	//				int index = makeNewTriangle( v1, v2, localVertices[j] );
					System.out.println( "\nCalling faceFactory to create new Face, loop-j="
								+ j 
								+ ":  v1=" + v1
								+ ", v2=" + v2
								+ ", localVertices[j]=" + localVertices[ j ]
//								+ ", nbrLocVert=" + numberOfLocalVertices
								);
					// hjs 1/9/2015 pass along the current edge (needed for dag2List)
					currentFace = faceFactory_.createFace( edgeToProcess, localVertices[ j ] );
//					currentFace = faceFactory_.createFace( v1, v2, localVertices[ j ] );
			    	System.out.println( "New face: " + currentFace );
			    	
			    	currentFaceIndex = currentFace.getFaceListIndex();
			    	System.out.println( "(running) index of the new face: " + currentFaceIndex );
			    	
			    	// may want to add after face is "finalized"?
			    	faceList.add( currentFace );
			    	System.out.println( "\n  *** Current face-list:\n" + faceList );
			    	
			    	System.out.println( "\n  *** Is localDag0[j] == j  ::  " 
			    				+ localDag0[j] + " =? " + j );
					
	//				** int index = makeNewTriangle( v1, v2, localVertex[j] );localVertices
					if ( localDag0[j] == j ){
					
						System.out.println( "Adding row to M23 matrix: "
								+ "faceIndex = " + currentFaceIndex + ", currentEdgeIndex= " + currentEdgeIndex );
						// add a row to M[2,3] for this triangle
//		    			matrixM12_.addRowEdgeListEntry( currentFaceIndex );
		    			matrixM12_.addRowEdgeListEntry( currentEdgeIndex );
					}
					// localDag0[j] != j  means that we only need a dag2List addition to triangle triangleList[index] 
					else{
						
//						tmpFace.addToDagList( edgeIndex1, edgeIndex2 );
					
						if ( j < localDag0.length ) {
							
							FaceI tmpFace;
							int tmpFaceIndex;
							SortedMap<Integer, Integer> tmpDagList;
							
//							System.out.println( "localDag0[ " + j + " ]= " + localDag0[j] );
							System.out.println( "   [before] Merging daglists for face=" + currentFace );
//							System.out.println( "   and face=" );
//							System.out.println( F3( v1, v2,
//									localVertices[ localDag0[j] ] ) );
							
							tmpFaceIndex = F3( v1, v2,
									localVertices[ localDag0[j] ] );
							if ( tmpFaceIndex != -1 ) {
							
								tmpFace = faceList.get( tmpFaceIndex );
								tmpDagList = tmpFace.getDagList();
								if ( !tmpDagList.isEmpty() ) {
								
									currentFace.mergeIntoDagList( tmpDagList );
								}
								System.out.println( "   with face=" + tmpFace );
								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
								System.out.println( "   - tmpDagList=" + tmpDagList );
							}
							else {
								
								System.out.println( "   - Face doesn't exist: " 
											+ "v1=" + v1 
											+ ", v2=" + v2
											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
							}
							// these 2 correspond to edgePairs, except that the second
							// index is replaced by the "dagList"-index
//							System.out.println( "   and face=" + F3( v1, localVertices[j],
//									localVertices[ localDag0[j] ] ) );
							


							tmpFaceIndex = F3( v1, localVertices[j],
									localVertices[ localDag0[j] ] );
							if ( tmpFaceIndex != -1 ) {
							
								tmpFace = faceList.get( tmpFaceIndex );
								tmpDagList = tmpFace.getDagList();
								if ( !tmpDagList.isEmpty() ) {
								
									currentFace.mergeIntoDagList( tmpDagList );
								}
								System.out.println( "   with face=" + tmpFace );
								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
								System.out.println( "   - tmpDagList=" + tmpDagList );
							}
							else {
								
								System.out.println( "   - Face doesn't exist: " 
											+ "v1=" + v1 
											+ ", locV[j]=" + localVertices[j]
											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
							}

							tmpFaceIndex = F3( v2, localVertices[j],
									localVertices[ localDag0[j] ] );
							if ( tmpFaceIndex != -1 ) {
							
								tmpFace = faceList.get( tmpFaceIndex );
								tmpDagList = tmpFace.getDagList();
								if ( !tmpDagList.isEmpty() ) {
								
									currentFace.mergeIntoDagList( tmpDagList );
								}
								System.out.println( "   with face=" + tmpFace );
								System.out.println( "   - currentFace.dagList=" + currentFace.getDagList() );
								System.out.println( "   - tmpDagList=" + tmpDagList );
							}
							else {
								
								System.out.println( "   - Face doesn't exist: " 
											+ "v2=" + v2 
											+ ", locV[j]=" + localVertices[j]
											+ ", locV[ dag0[j] ]=" + localVertices[ localDag0[j] ]); 
							}
						}
					}
				}
				
				int tmpFaceIndex;
				FaceI tmpFace;
				// Build columns of M23. We'll get one for every element of localEdges which is Pos (isPos ==T)
				// This requires a search through the dag. We could replace this by passing lists as we did with M[1,2]
				// but I'm afraid that the storage requirement will get too big. Try later, perhaps.
				for( int j=0; j<numberOfLocalEdges; j++ ){
					     	
					EdgeI tempEdge = localEdges.get( j );
					
					if( tempEdge.isXPositive() ) {
						
						columnList.clear();
						int c = tempEdge.getVertexIndex1(); 
						int d = tempEdge.getVertexIndex2();
						
						tmpFaceIndex = F3(v1,c,d);
						if ( faceList.contains( tmpFaceIndex )) {
							
							tmpFace = faceList.get( F3( v1,c,d ) );
							columnList.putAll( faceList.get( F3(v1,c,d) ).getDagList() );
							System.out.println( "   b: adding to columnList, dagList=" + faceList.get( F3(v1,c,d) ).getDagList() );
						}
						
						tmpFaceIndex = F3(v2,c,d);
						if ( faceList.contains( tmpFaceIndex )) {
							
							tmpFace = faceList.get( F3( v2,c,d ) );
							columnList.putAll( faceList.get( F3(v2,c,d) ).getDagList() );
							System.out.println( "   b: adding to columnList, dagList=" + faceList.get( F3(v2,c,d) ).getDagList() );
						}
						
						
						// TODO: this is not safe??? Or do the faces exist??? Actually: YES (v1 and v2 are
						// the vertices of the current edge, and c,d are the vertices of the edge between 
						// "opposite vertices", so by construction, this face will exist, and will have
						// some dagList -- TODO: check if that dagList <> null, and what to do if null
//						columnList.putAll( faceList.get( F3(v1,c,d) ).getDagList() );
//						columnList.putAll( faceList.get( F3(v2,c,d) ).getDagList() );
	
						
						// add columnList to M[2,3]

						if ( columnList.size() > 0 ) {
							
							System.out.println( "Adding column to M23 matrix: "
									+ "currentEdgeIndex = " + currentEdgeIndex +
									"\ncolumnList = " + columnList );
						// ???
						
						
							this.matrixM12_.addColumn( currentEdgeIndex, 
									columnList );
							System.out.println( "   ^^^: adding Col, columnList=" + columnList );
						}
					}
				}
			// Add vertices to L
			}
			
			
			//From here we return values, etc. as usual.
				
				
				

	    	// TODO: check on the "timing" of these calls: (may need to be placed earlier?)

			// Add the current edge's index to the local lists of each of its vertices
			((ArrayList<Integer>) localListL_.get( vertex1 )).add( new Integer(currentEdgeIndex) );
			((ArrayList<Integer>) localListL_.get( vertex2 )).add( new Integer(currentEdgeIndex) );
			
			// Add the current edge's OTHER VERTEX to the local lists Lv of each of its vertices
			((ArrayList<Integer>) localListLv_.get( vertex1 )).add( new Integer(
					edgeToProcess.getOtherVertex( vertex1 ) ) );
			((ArrayList<Integer>) localListLv_.get( vertex2 )).add( new Integer( 
					edgeToProcess.getOtherVertex( vertex2 ) ) );
			Collections.sort( localListLv_.get( vertex1 ) );
			Collections.sort( localListLv_.get( vertex2 ) );
			
			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	
	        	strBufCollectFeedback_.append( "\n" +
					"\n-- localListL(" + vertex1 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex1 ) ) +
					"\n-- localListL(" + vertex2 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
					"\n-- localListL = " + 
						this.printArrayList( localListL_ ) );
			}
    	}
    		
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
    		strBufCollectFeedback_.append( "\n\n------------------------------- " );
    		strBufCollectFeedback_.append( "\n\nedgeList:\n"  + this.asStringComplete() );
    		strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	}

    	// All the edges are processed, and we have our reduction matrix:
    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\nReduction Matrix M23" );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n------------------------------- " );
    	strBufCollectFeedback_.append( "\n" + this.matrixM12_.toString() );

    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {

        	System.out.println( strBufCollectFeedback_.toString() );
    	}
    	
//    	System.out.println( "Done computing M23." );
    	
		return this.matrixM12_;    
    }

    
    ////May want to return the reduction matrix from this method,
    // since that is the desired output from the algorithm
    //    public void processEdges() throws Exception {
    public M12PersistenceMatrix computeMatrixM12ref() throws Exception {

    	final int numberOfEdges = edges_.size();
    	int vertex1;
    	int vertex2;
    	EdgeI edgeToProcess;
    	boolean isPositiveValue;
    	EdgeI edge1;
    	EdgeI edge2;
    	List<EdgePair> edgePairList = new ArrayList<EdgePair>();
		EdgePair edgePairToProcess;
    	int oppositeVertex;
    	
		// for local output only
    	SortedMap<Integer, Integer> dagList;
		EdgeI tmpEdge = edgeFactory_.createEdge( -1, -1 );
		boolean suppressAllOutput;

		suppressAllOutput = processData_.getValidatedProcessParameter( 
				TDA.SETTING_SUPPRESSALLOUTPUT ).
				equalsIgnoreCase( TDA.UI_SUPPRESSALLOUTPUT_YES );
		
		if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    	
        	strBufCollectFeedback_.append( "\n" );
	    	strBufCollectFeedback_.append( "\n*** After assignEdgeProperties: EdgeList edgeList(complete) = " );
	    	strBufCollectFeedback_.append( "\n" + this.asStringComplete() );
		}
    	
		
    	// now go through edges based on the edgeType
    	for ( int currentEdgeIndex=0; currentEdgeIndex<numberOfEdges; currentEdgeIndex++ ) {
	
    		// get the edge info
    		edgeToProcess = edges_.get( currentEdgeIndex );
    		    		
    		// crucial: build up the entity
    		builtUpEdges_.add( edgeToProcess );

    		if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    	    	
            	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    		strBufCollectFeedback_.append( "\nProcessing edge i=" + currentEdgeIndex );
	    		strBufCollectFeedback_.append( "\n------------------------------- " );
    		}
    		
    		
    		vertex1 = edgeToProcess.getVertexIndex1();
    		vertex2 = edgeToProcess.getVertexIndex2();
    		isPositiveValue = edgeToProcess.isUPositive();
    		
    		// if edge is positive:
    		if ( isPositiveValue ) {
    		
    			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    		    	
    	        	strBufCollectFeedback_.append( "\n+ 'Positive': Edge (index=" + currentEdgeIndex + ")"
			    					+ edgeToProcess.asStringComplete() );
    			}

				// We have a positive edge, so compute the iEL
    			edgePairList = findEdgePairsToOppositeVerticesM12( edgeToProcess );

			    	        if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			
//			    	        	strBufCollectFeedback.append( "\n" + 
//			    	                    new StringBuffer( StringUtil.compileMemoryInfo( 
//			    	                            "    [EdgeList]: After finding edgePairs" )) );
			    	        
	    	        			strBufCollectFeedback_.append( "\n  Edge-Pair list for edge=" + 
	    	        					edgeToProcess.asString() + ": " );
			    	        }
		    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	    			
		    	    			for ( int j=0; j<edgePairList.size(); j++ ) {
				    	    		
				    	    		edgePairToProcess = edgePairList.get( j );
			    	            	strBufCollectFeedback_.append( "\n    " + 
			    	            			edgePairToProcess.asStringComplete() );
			    	    		}
			    	    	}
			    	    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	        	
			    	    			strBufCollectFeedback_.append( "\n" );
			    	    	}
	    		
	    		// if iEL empty:
	    		if ( edgePairList == null || edgePairList.size() == 0 ) {

	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( "\n	*** Intersect list is empty" );
				    	strBufCollectFeedback_.append( "\n			" +
				    					"Adding 'self' (=this edge " + edgeToProcess.asString() +
				    					" ) to its dagList" );
	    			}

				    // For edgeToProcess, add itself to its dagList
//	    			edgeToProcess.addToDagList( new Integer( currentEdgeIndex ),
//	    					new Integer( 1 ) );
	    			
	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( "\n" );
				    			strBufCollectFeedback_.append( "\n			" +
				    					"DagList of Edge " + edgeToProcess.asString() + ":  " );
	    			}
	    			
	    			dagList = edgeToProcess.getDagList();
	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( dagList );
	    			
	    				strBufCollectFeedback_.append( "\n\n	*** " +
	    								"Add ROW (1-cycle) to reduction matrix: " + currentEdgeIndex );
	    			}
	    			
	    			
	    			matrixM12_.addRowEdgeListEntry( currentEdgeIndex );
	    		}
	    		else { // iEL is not empty

	    			// -----------------------------------------------------------------
	    			// process the FIRST edgePair (different action from all other pairs)
	    			// -----------------------------------------------------------------
		    		edgePairToProcess = edgePairList.get( 0 );
    	    		edge1 = edgePairToProcess.getEdge1();
    	    		edge2 = edgePairToProcess.getEdge2();
    	    		oppositeVertex = edgePairToProcess.getCommonVertexIndex();
		    		
    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    	    	    	
    	            	strBufCollectFeedback_.append( "\n			" +
										"Processing first edgePair " + edgePairToProcess.asString() );
    	    		}
		
		    		// both edges negative
		    		if ( !edge1.isUPositive() && !edge2.isUPositive() ) {
		    			
		    			// 7/15/2013 hjs 	fix
		    	    	edgeToProcess.setUPositive( false );
									
		    	    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	        	
		    	        	strBufCollectFeedback_.append( "\n			" +
											"Changed edge from 'positive' to 'negative': " + 
											edgeToProcess.asStringComplete() );
		    	    	}
					}
		    		else {
		    			
	    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
	    	    		if ( edge1.isUPositive() ) { 	    			

	    	    			// hjs 9/25/2013	adjust the orientation by -1, per requirement change
	    	    			edgeToProcess.mergeIntoDagList( edge1.getDagList(), 
	    	    					-1 * edgePairToProcess.getEdgeOrientation( edge1.getEdgeListIndex() ) );
	    	    			
	    	    			if ( oppositeVertex > vertex1 && oppositeVertex > vertex2 ) {

//		    	    			edgeToProcess.mergeIntoDagList( edge1.getDagList() );
	    	    			}
	    	    			else if ( oppositeVertex < vertex1 && oppositeVertex < vertex2 ) {
	    	    				
	    	    			}
	    	    			else {
	    	    				
	    	    			}	    	    			
							
	    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    	    		    	
	    	    	        	strBufCollectFeedback_.append( "\n			" +
									"Merged dagList of (positive) edge " + edge1.asStringComplete() +
									"\n			 into edge " + edgeToProcess.asStringComplete() );
	    	    			}
	    	    		}

	    	    		if ( edge2.isUPositive() ) { 	    			

	    	    			// hjs 9/25/2013	adjust the orientation by -1, per requirement change
	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList(), 
	    	    					-1 * edgePairToProcess.getEdgeOrientation( edge2.getEdgeListIndex() ) );
//	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList(), 
//	    	    					edgePairToProcess.getEdgeOrientation( 2 ) );
//	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList() );
							
	    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    	    		    	
	    	    	        	strBufCollectFeedback_.append( "\n			" +
									"Merged dagList of (positive) edge " + edge2.asStringComplete() +
									"\n			 into edge " + edgeToProcess.asStringComplete() );
	    	    			}
	    	    		}
		    		}

	    			// now remove the current pair from the edgePair list, 
	    			// to set-up processing the remaining edgePairs:
		    		edgePairList.remove( edgePairToProcess );

		    		if ( edgePairList != null ) {

		    			// -----------------------------------------------------------------
		    			// process the remaining edgePairs
		    			// -----------------------------------------------------------------
		    			while ( edgePairList.size() > 0 ) {
		    				
		    				// get the first edgePair from the list
				    		edgePairToProcess = edgePairList.get( 0 );
		    	    		edge1 = edgePairToProcess.getEdge1();
		    	    		edge2 = edgePairToProcess.getEdge2();
		    	    		
		    	    		// reset the temporary dagList that will give us a column in the reduction matrix
//		    	    		tmpDagList = new ArrayList<Integer>();
		    	    		
		    	    		tmpEdge = edgeFactory_.createEdge( edgeToProcess );
				    		
		    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	    	    	
		    	            	strBufCollectFeedback_.append( "\n\n			" +
	    							"Processing (remaining edgePair(s)) " + edgePairToProcess.asString() +
	    							", starting with temp. dagList: " + tmpEdge.getDagList().toString() );
		    	    		}
		    	    		
		    	    		// only do something when either edge is positive
		    	    		// [changed]
		    	    		
		    	    		if ( !edge1.isUPositive() && !edge2.isUPositive() ) {

				    			// 7/15/2013 hjs 	fix  DON'T USE: tmpEdge already has proper dagList
		    	    			// passed on from edgeToProcess
		    	    			// In any case, would simply want to transfer the daglist, and NOT
		    	    			// use the merge method!!
//		    	    			tmpEdge.mergeIntoDagList( edgeToProcess.getDagList() );
		    	    		}
		    	    		else {
		    	    			
			    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
			    	    		if ( edge1.isUPositive() ) { 	    			

			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList(), 
			    	    					edgePairToProcess.getEdgeOrientation( edge1.getEdgeListIndex() ) );
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList(), 
//			    	    					edgePairToProcess.getEdgeOrientation( 1 ) );
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList() );	
									
			    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	    		    	
			    	    	        	strBufCollectFeedback_.append( "\n			" +
											"Merged dagList of (positive) edge " + edge1.asStringComplete() +
											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );
			    	    			}
			    	    		}
		
			    	    		if ( edge2.isUPositive() ) { 	    			

			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList(), 
			    	    					edgePairToProcess.getEdgeOrientation( edge2.getEdgeListIndex() ) );
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList(), 
//			    	    					edgePairToProcess.getEdgeOrientation( 2 ) );
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList() );	
									
			    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	    		    	
			    	    	        	strBufCollectFeedback_.append( "\n			" +
											"Merged dagList of (positive) edge " + edge2.asStringComplete() +
											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );	
			    	    			}
			    	    		}
		    	    		}

//		    	    		if ( edge1.isUPositive() || edge2.isUPositive() ) {
//		    	    			
//			    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
//			    	    		if ( edge1.isUPositive() ) { 	    			
//		
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList() );	
//									
//			    	    			if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			    	    		    	
//			    	    	        	strBufCollectFeedback.append( "\n			" +
//											"Merged dagList of (positive) edge " + edge1.asStringComplete() +
//											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );
//			    	    			}
//			    	    		}
//		
//			    	    		if ( edge2.isUPositive() ) { 	    			
//		
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList() );	
//									
//			    	    			if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			    	    		    	
//			    	    	        	strBufCollectFeedback.append( "\n			" +
//											"Merged dagList of (positive) edge " + edge2.asStringComplete() +
//											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );	
//			    	    			}
//			    	    		}
//		    	    		}
	    					
			    			// now remove the current pair from the edgePair list, to set-up processing the next edgePair:
					    	edgePairList.remove( edgePairToProcess );
			    				    	    		
					    	if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
					        	
					        	strBufCollectFeedback_.append( "\n\n	*** " +
											"Add COLUMN to reduction matrix (if non-empty dagList):" );
					    	}
				
							// only add the column if the dagList is non-empty
							if ( tmpEdge.getDagList().size() > 0 ) {
							
								// Add dagList to reductionMatrix
								this.matrixM12_.addColumn( currentEdgeIndex, 
										tmpEdge.getDagList() );
	
								if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
							    	
						        	strBufCollectFeedback_.append( "\n	Column --> temp. DagList " + 
						        			tmpEdge.getDagList().toString() );
								}
							}
		    			}
		    		}
	    		}
    		}
    		else { // edge is negative

    			if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    		    	
    	        	strBufCollectFeedback_.append( "\n- 'Negative': Edge (index=" + currentEdgeIndex + ")"
    					+ edgeToProcess.asStringComplete()
    					+ ": \n		Assigning edge (i=" + currentEdgeIndex + ")" + edgeToProcess.toString() 
    					+ " to L for vertex=" + vertex1 + " and " + vertex2 );
    			}
    		}

//    		strBufCollectFeedback.append( 
//                    new StringBuffer( StringUtil.compileMemoryInfo( 
//                            "\n\n    After processing edge (i=" + currentEdgeIndex + ")")) );
//    		System.out.println( 
//                    new StringBuffer( StringUtil.compileMemoryInfo( 
//                            "\n\n    After processing edge (i=" + currentEdgeIndex + ")")) );
    		
//    		strBufCollectFeedback.append( "\n" );

    		// Add the current edge's index to the local lists of each of its vertices
			((ArrayList<Integer>) localListL_.get( vertex1 )).add( new Integer(currentEdgeIndex) );
			((ArrayList<Integer>) localListL_.get( vertex2 )).add( new Integer(currentEdgeIndex) );
			
    		// Add the current edge's OTHER VERTEX to the local lists Lv of each of its vertices
			((ArrayList<Integer>) localListLv_.get( vertex1 )).add( new Integer(
					edgeToProcess.getOtherVertex( vertex1 ) ) );
			((ArrayList<Integer>) localListLv_.get( vertex2 )).add( new Integer( 
					edgeToProcess.getOtherVertex( vertex2 ) ) );
			Collections.sort( localListLv_.get( vertex1 ) );
			Collections.sort( localListLv_.get( vertex2 ) );
			
			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	
	        	strBufCollectFeedback_.append( "\n" +
					"\n-- localListL(" + vertex1 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex1 ) ) +
					"\n-- localListL(" + vertex2 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
					"\n-- localListL = " + 
						this.printArrayList( localListL_ ) );
			}
			
			// hjs 10/10/2013 quick dump of local lists
//			System.out.println( "\n" +
//					"\n-- localListL(" + vertex1 + ") = " + 
//						this.printArrayList( ( ArrayList<Integer> ) localListL.get( vertex1 ) ) +
//					"\n-- localListL(" + vertex2 + ") = " + 
//						this.printArrayList( ( ArrayList<Integer> )localListL.get( vertex2 ) ) +
//					"\n-- localListL = " + 
//						this.printArrayList( localListL ) );
			
			
			//			System.out.print( "Adding index of Edge (i.e., i=" + i + ") to local List L for its endpoints:" );
			//			System.out.print( printL() );
			
//			            if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			
//			                System.out.println(
//			                        new StringBuffer( StringUtil.compileMemoryInfo( 
//			                                "    [EdgeList: processEdges] after processing another edge" )) );
//			            }
			            
			            
    	}
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
        	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n\nedgeList:\n"  + this.asStringComplete() );
	    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	}

    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    		
	    	// All the edges are processed, and we have our reduction matrix:
	    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\nReduction Matrix M12" );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n" + this.matrixM12_.toString() );

        	System.out.println( strBufCollectFeedback_.toString() );
    	}
    	
		return this.matrixM12_;
    
    }
    
    ////May want to return the reduction matrix from this method,
    // since that is the desired output from the algorithm
    //    public void processEdges() throws Exception {
    public M12PersistenceMatrix computeMatrixM12() throws Exception {

    	final int numberOfEdges = edges_.size();
    	int vertex1;
    	int vertex2;
    	EdgeI edgeToProcess;
    	boolean isPositiveValue;
    	EdgeI edge1;
    	EdgeI edge2;
    	List<EdgePair> edgePairList = new ArrayList<EdgePair>();
		EdgePair edgePairToProcess;
    	int oppositeVertex;
    	
		// for local output only
    	SortedMap<Integer, Integer> dagList;
		EdgeI tmpEdge = edgeFactory_.createEdge( -1, -1 );
		boolean suppressAllOutput;

		suppressAllOutput = processData_.getValidatedProcessParameter( 
				TDA.SETTING_SUPPRESSALLOUTPUT ).
				equalsIgnoreCase( TDA.UI_SUPPRESSALLOUTPUT_YES );
		
		if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
	    	
        	strBufCollectFeedback_.append( "\n" );
	    	strBufCollectFeedback_.append( "\n*** After assignEdgeProperties: EdgeList edgeList(complete) = " );
	    	strBufCollectFeedback_.append( "\n" + this.asStringComplete() );
		}
    	
		
    	// now go through edges based on the edgeType
    	for ( int currentEdgeIndex=0; currentEdgeIndex<numberOfEdges; currentEdgeIndex++ ) {
			
    		// get the edge info
    		edgeToProcess = edges_.get( currentEdgeIndex );
    		    		
    		// crucial: build up the entity
    		builtUpEdges_.add( edgeToProcess );

    		if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    	    	
            	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    		strBufCollectFeedback_.append( "\nProcessing edge i=" + currentEdgeIndex );
	    		strBufCollectFeedback_.append( "\n------------------------------- " );
    		}
    		
    		
    		vertex1 = edgeToProcess.getVertexIndex1();
    		vertex2 = edgeToProcess.getVertexIndex2();
    		isPositiveValue = edgeToProcess.isUPositive();
    		
    		// if edge is positive:
    		if ( isPositiveValue ) {
    		
    			if ( TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    		    	
    	        	strBufCollectFeedback_.append( "\n+ 'Positive': Edge (index=" + currentEdgeIndex + ")"
			    					+ edgeToProcess.asStringComplete() );
    			}

				// We have a positive edge, so compute the iEL
    			edgePairList = findEdgePairsToOppositeVerticesM12( edgeToProcess );

			    	        if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			
//			    	        	strBufCollectFeedback.append( "\n" + 
//			    	                    new StringBuffer( StringUtil.compileMemoryInfo( 
//			    	                            "    [EdgeList]: After finding edgePairs" )) );
			    	        
	    	        			strBufCollectFeedback_.append( "\n  Edge-Pair list for edge=" + 
	    	        					edgeToProcess.asString() + ": " );
			    	        }
		    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	    			
		    	    			for ( int j=0; j<edgePairList.size(); j++ ) {
				    	    		
				    	    		edgePairToProcess = edgePairList.get( j );
			    	            	strBufCollectFeedback_.append( "\n    " + 
			    	            			edgePairToProcess.asStringComplete() );
			    	    		}
			    	    	}
			    	    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	        	
			    	    			strBufCollectFeedback_.append( "\n" );
			    	    	}
	    		
	    		// if iEL empty:
	    		if ( edgePairList == null || edgePairList.size() == 0 ) {

	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( "\n	*** Intersect list is empty" );
				    	strBufCollectFeedback_.append( "\n			" +
				    					"Adding 'self' (=this edge " + edgeToProcess.asString() +
				    					" ) to its dagList" );
	    			}

				    // For edgeToProcess, add itself to its dagList
	    			edgeToProcess.addToDagList( new Integer( currentEdgeIndex ),
	    					new Integer( 1 ) );
//	    			edgeToProcess.addToDagList( new Integer( currentEdgeIndex ),
//	    					new Integer( currentEdgeIndex ) );
	    			
	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( "\n" );
				    			strBufCollectFeedback_.append( "\n			" +
				    					"DagList of Edge " + edgeToProcess.asString() + ":  " );
	    			}
	    			
	    			dagList = edgeToProcess.getDagList();
	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    		    	
	    	        	strBufCollectFeedback_.append( dagList );
	    			
	    				strBufCollectFeedback_.append( "\n\n	*** " +
	    								"Add ROW (1-cycle) to reduction matrix: " + currentEdgeIndex );
	    			}
	    			
	    			
	    			matrixM12_.addRowEdgeListEntry( currentEdgeIndex );
	    		}
	    		else { // iEL is not empty

	    			// -----------------------------------------------------------------
	    			// process the FIRST edgePair (different action from all other pairs)
	    			// -----------------------------------------------------------------
		    		edgePairToProcess = edgePairList.get( 0 );
    	    		edge1 = edgePairToProcess.getEdge1();
    	    		edge2 = edgePairToProcess.getEdge2();
    	    		oppositeVertex = edgePairToProcess.getCommonVertexIndex();
		    		
    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    	    	    	
    	            	strBufCollectFeedback_.append( "\n			" +
										"Processing first edgePair " + edgePairToProcess.asString() );
    	    		}
		
		    		// both edges negative
		    		if ( !edge1.isUPositive() && !edge2.isUPositive() ) {
		    			
		    			// 7/15/2013 hjs 	fix
		    	    	edgeToProcess.setUPositive( false );
									
		    	    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	        	
		    	        	strBufCollectFeedback_.append( "\n			" +
											"Changed edge from 'positive' to 'negative': " + 
											edgeToProcess.asStringComplete() );
		    	    	}
					}
		    		else {
		    			
	    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
	    	    		if ( edge1.isUPositive() ) { 	    			

	    	    			// hjs 9/25/2013	adjust the orientation by -1, per requirement change
	    	    			edgeToProcess.mergeIntoDagList( edge1.getDagList(), 
	    	    					-1 * edgePairToProcess.getEdgeOrientation( edge1.getEdgeListIndex() ) );
	    	    			
	    	    			if ( oppositeVertex > vertex1 && oppositeVertex > vertex2 ) {

//		    	    			edgeToProcess.mergeIntoDagList( edge1.getDagList() );
	    	    			}
	    	    			else if ( oppositeVertex < vertex1 && oppositeVertex < vertex2 ) {
	    	    				
	    	    			}
	    	    			else {
	    	    				
	    	    			}	    	    			
							
	    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    	    		    	
	    	    	        	strBufCollectFeedback_.append( "\n			" +
									"Merged dagList of (positive) edge " + edge1.asStringComplete() +
									"\n			 into edge " + edgeToProcess.asStringComplete() );
	    	    			}
	    	    		}

	    	    		if ( edge2.isUPositive() ) { 	    			

	    	    			// hjs 9/25/2013	adjust the orientation by -1, per requirement change
	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList(), 
	    	    					-1 * edgePairToProcess.getEdgeOrientation( edge2.getEdgeListIndex() ) );
//	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList(), 
//	    	    					edgePairToProcess.getEdgeOrientation( 2 ) );
//	    	    			edgeToProcess.mergeIntoDagList( edge2.getDagList() );
							
	    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
	    	    		    	
	    	    	        	strBufCollectFeedback_.append( "\n			" +
									"Merged dagList of (positive) edge " + edge2.asStringComplete() +
									"\n			 into edge " + edgeToProcess.asStringComplete() );
	    	    			}
	    	    		}
		    		}

	    			// now remove the current pair from the edgePair list, 
	    			// to set-up processing the remaining edgePairs:
		    		edgePairList.remove( edgePairToProcess );

		    		if ( edgePairList != null ) {

		    			// -----------------------------------------------------------------
		    			// process the remaining edgePairs
		    			// -----------------------------------------------------------------
		    			while ( edgePairList.size() > 0 ) {
		    				
		    				// get the first edgePair from the list
				    		edgePairToProcess = edgePairList.get( 0 );
		    	    		edge1 = edgePairToProcess.getEdge1();
		    	    		edge2 = edgePairToProcess.getEdge2();
		    	    		
		    	    		// reset the temporary dagList that will give us a column in the reduction matrix
//		    	    		tmpDagList = new ArrayList<Integer>();
		    	    		
		    	    		tmpEdge = edgeFactory_.createEdge( edgeToProcess );
				    		
		    	    		if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	    	    	
		    	            	strBufCollectFeedback_.append( "\n\n			" +
	    							"Processing (remaining edgePair(s)) " + edgePairToProcess.asString() +
	    							", starting with temp. dagList: " + tmpEdge.getDagList().toString() );
		    	    		}
		    	    		
		    	    		// only do something when either edge is positive
		    	    		// [changed]
		    	    		
		    	    		if ( !edge1.isUPositive() && !edge2.isUPositive() ) {

				    			// 7/15/2013 hjs 	fix  DON'T USE: tmpEdge already has proper dagList
		    	    			// passed on from edgeToProcess
		    	    			// In any case, would simply want to transfer the daglist, and NOT
		    	    			// use the merge method!!
//		    	    			tmpEdge.mergeIntoDagList( edgeToProcess.getDagList() );
		    	    		}
		    	    		else {
		    	    			
			    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
			    	    		if ( edge1.isUPositive() ) { 	    			

			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList(), 
			    	    					edgePairToProcess.getEdgeOrientation( edge1.getEdgeListIndex() ) );
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList(), 
//			    	    					edgePairToProcess.getEdgeOrientation( 1 ) );
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList() );	
									
			    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	    		    	
			    	    	        	strBufCollectFeedback_.append( "\n			" +
											"Merged dagList of (positive) edge " + edge1.asStringComplete() +
											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );
			    	    			}
			    	    		}
		
			    	    		if ( edge2.isUPositive() ) { 	    			

			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList(), 
			    	    					edgePairToProcess.getEdgeOrientation( edge2.getEdgeListIndex() ) );
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList(), 
//			    	    					edgePairToProcess.getEdgeOrientation( 2 ) );
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList() );	
									
			    	    			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
			    	    		    	
			    	    	        	strBufCollectFeedback_.append( "\n			" +
											"Merged dagList of (positive) edge " + edge2.asStringComplete() +
											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );	
			    	    			}
			    	    		}
		    	    		}

//		    	    		if ( edge1.isUPositive() || edge2.isUPositive() ) {
//		    	    			
//			    	    		// merge any positive edge's dagList into the edgeToProcess' dagList
//			    	    		if ( edge1.isUPositive() ) { 	    			
//		
//			    	    			tmpEdge.mergeIntoDagList( edge1.getDagList() );	
//									
//			    	    			if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			    	    		    	
//			    	    	        	strBufCollectFeedback.append( "\n			" +
//											"Merged dagList of (positive) edge " + edge1.asStringComplete() +
//											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );
//			    	    			}
//			    	    		}
//		
//			    	    		if ( edge2.isUPositive() ) { 	    			
//		
//			    	    			tmpEdge.mergeIntoDagList( edge2.getDagList() );	
//									
//			    	    			if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			    	    		    	
//			    	    	        	strBufCollectFeedback.append( "\n			" +
//											"Merged dagList of (positive) edge " + edge2.asStringComplete() +
//											"\n			 into temp. dagList: " + tmpEdge.getDagList().toString() );	
//			    	    			}
//			    	    		}
//		    	    		}
	    					
			    			// now remove the current pair from the edgePair list, to set-up processing the next edgePair:
					    	edgePairList.remove( edgePairToProcess );
			    				    	    		
					    	if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
					        	
					        	strBufCollectFeedback_.append( "\n\n	*** " +
											"Add COLUMN to reduction matrix (if non-empty dagList):" );
					    	}
				
							// only add the column if the dagList is non-empty
							if ( tmpEdge.getDagList().size() > 0 ) {
							
								// Add dagList to reductionMatrix
								this.matrixM12_.addColumn( currentEdgeIndex, 
										tmpEdge.getDagList() );
	
								if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
							    	
						        	strBufCollectFeedback_.append( "\n	Column --> temp. DagList " + 
						        			tmpEdge.getDagList().toString() );
								}
							}
		    			}
		    		}
	    		}
    		}
    		else { // edge is negative

    			if ( TDA.DEBUG && TDA.TRACE_FEEDBACK && TDA.TRACE_EDGELIST ) {
    		    	
    	        	strBufCollectFeedback_.append( "\n- 'Negative': Edge (index=" + currentEdgeIndex + ")"
    					+ edgeToProcess.asStringComplete()
    					+ ": \n		Assigning edge (i=" + currentEdgeIndex + ")" + edgeToProcess.toString() 
    					+ " to L for vertex=" + vertex1 + " and " + vertex2 );
    			}
    		}

//    		strBufCollectFeedback.append( 
//                    new StringBuffer( StringUtil.compileMemoryInfo( 
//                            "\n\n    After processing edge (i=" + currentEdgeIndex + ")")) );
//    		System.out.println( 
//                    new StringBuffer( StringUtil.compileMemoryInfo( 
//                            "\n\n    After processing edge (i=" + currentEdgeIndex + ")")) );
    		
//    		strBufCollectFeedback.append( "\n" );

    		// Add the current edge's index to the local lists of each of its vertices
			((ArrayList<Integer>) localListL_.get( vertex1 )).add( new Integer(currentEdgeIndex) );
			((ArrayList<Integer>) localListL_.get( vertex2 )).add( new Integer(currentEdgeIndex) );
			
    		// Add the current edge's OTHER VERTEX to the local lists Lv of each of its vertices
			((ArrayList<Integer>) localListLv_.get( vertex1 )).add( new Integer(
					edgeToProcess.getOtherVertex( vertex1 ) ) );
			((ArrayList<Integer>) localListLv_.get( vertex2 )).add( new Integer( 
					edgeToProcess.getOtherVertex( vertex2 ) ) );
			Collections.sort( localListLv_.get( vertex1 ) );
			Collections.sort( localListLv_.get( vertex2 ) );
			
			if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
		    	
	        	strBufCollectFeedback_.append( "\n" +
					"\n-- localListL(" + vertex1 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> ) localListL_.get( vertex1 ) ) +
					"\n-- localListL(" + vertex2 + ") = " + 
						this.printArrayList( ( ArrayList<Integer> )localListL_.get( vertex2 ) ) +
					"\n-- localListL = " + 
						this.printArrayList( localListL_ ) );
			}
			
			// hjs 10/10/2013 quick dump of local lists
//			System.out.println( "\n" +
//					"\n-- localListL(" + vertex1 + ") = " + 
//						this.printArrayList( ( ArrayList<Integer> ) localListL.get( vertex1 ) ) +
//					"\n-- localListL(" + vertex2 + ") = " + 
//						this.printArrayList( ( ArrayList<Integer> )localListL.get( vertex2 ) ) +
//					"\n-- localListL = " + 
//						this.printArrayList( localListL ) );
			
			
			//			System.out.print( "Adding index of Edge (i.e., i=" + i + ") to local List L for its endpoints:" );
			//			System.out.print( printL() );
			
//			            if ( COMPTOPO.DEBUG && COMPTOPO.TRACE_EDGELIST ) {
//			
//			                System.out.println(
//			                        new StringBuffer( StringUtil.compileMemoryInfo( 
//			                                "    [EdgeList: processEdges] after processing another edge" )) );
//			            }
			            
			            
    	}
    	
    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
        	
        	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n\nedgeList:\n"  + this.asStringComplete() );
	    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
    	}

    	if ( TDA.DEBUG && TDA.TRACE_EDGELIST ) {
    		
	    	// All the edges are processed, and we have our reduction matrix:
	    	strBufCollectFeedback_.append( "\n\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\nReduction Matrix M12" );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n------------------------------- " );
	    	strBufCollectFeedback_.append( "\n" + this.matrixM12_.toString() );

        	System.out.println( strBufCollectFeedback_.toString() );
    	}
    	
		return this.matrixM12_;
    }
    
    // util only
    public String printArrayList( final ArrayList _arrayList ) {

		StringBuffer arrayListAsString = new StringBuffer( "" ) ;//"ArrayList = " );

		arrayListAsString.append( _arrayList.get( 0 ).toString() );
    	for ( int i=1; i<_arrayList.size(); i++ ) {

    		arrayListAsString.append( ", " );
    		arrayListAsString.append( _arrayList.get( i ).toString() );
    	}
    	
    	return arrayListAsString.toString();
    }
    
    
    
	public void setupDagLists() {
	    	
    	final int numberOfEdges = edges_.size();
    	EdgeI tempEdge;
    	
    	for ( int i=0; i<numberOfEdges; i++ ) {
    		tempEdge = edges_.get( i );
    		
    		if ( tempEdge.isUPositive() ) {

    			edges_.get( i ).addToDagList( i, 1 );
    		}
		}
	}
	
	protected void maintainComponentLists__1( final int a, final int b ) {

//		ArrayList<Integer> tmpComponentList;
//		ArrayList<Integer> tmpIntermediateComponentList;
//
//		
//		if ( componentList.containsKey( a ) ) {
//			
//			// add the extra "originating value" 'a' to the list
//			tmpComponentList = componentList.get( a );
//			tmpComponentList.add( b );
////			componentList.put( a, tmpComponentList );
//			if ( componentList.containsKey( b ) ) {
//				
//				// add all items that were already components of b
//				// to a's list, and track them separately
//				tmpComponentList.addAll( componentList.get( b ) );
//				
//				tmpIntermediateComponentList = componentList.get( b );
//				tmpIntermediateComponentList.add( b );
//				intermediateComponentList.put( b, tmpIntermediateComponentList );
//			}
//
//			componentList.put( a, tmpComponentList );
//		}
//		else {
//
//			// start a new list, and place 'a' on it
//			tmpComponentList = new ArrayList<Integer>();
//			tmpComponentList.add( b );
////			componentList.put( a, tmpComponentList );
//			if ( componentList.containsKey( b ) ) {
//				
//				// add all items that were already components of b
//				// to a's list, and track them separately
//				tmpComponentList.addAll( componentList.get( b ) );
//				
//				tmpIntermediateComponentList = componentList.get( b );
//				tmpIntermediateComponentList.add( b );
//				intermediateComponentList.put( b, tmpIntermediateComponentList );
//			}
//
//			componentList.put( a, tmpComponentList );
//		}
	}

	protected void maintainComponentLists_( final int _a, final int _b, final int _aa, final int _bb ) {		
	}
	
	
	protected void maintainComponentLists( final int _a, final int _b ) {
		// "b is now pointing to a" (b being the "UF vertex")

		ArrayList<Integer> tmpComponentList;
		ArrayList<Integer> tmpIntermediateComponentList;

		
		if ( componentList.containsKey( _a ) ) {
			
			// add the extra "originating value" 'a' to the list
			tmpComponentList = componentList.get( _a );
			tmpComponentList.add( _b );
//			componentList.put( a, tmpComponentList );
			if ( componentList.containsKey( _b ) ) {
				
				// add all items that were already components of b
				// to a's list, and track them separately
				tmpComponentList.addAll( componentList.get( _b ) );
				
				tmpIntermediateComponentList = componentList.get( _b );
				tmpIntermediateComponentList.add( _b );
				intermediateComponentList.put( _b, tmpIntermediateComponentList );
			}
			else {
				
				if ( intermediateComponentList.containsKey( _b ) ) {

					tmpIntermediateComponentList = intermediateComponentList.get( _b );
					tmpIntermediateComponentList.add( _a );
					intermediateComponentList.put( _b, tmpIntermediateComponentList );
				}
				else {
					
					tmpIntermediateComponentList = new ArrayList<Integer>();
					tmpIntermediateComponentList.add( _a );
					intermediateComponentList.put( _b, tmpIntermediateComponentList );
				}
			}

			componentList.put( _a, tmpComponentList );
		}
		else {

			// start a new list, and place 'a' on it
			tmpComponentList = new ArrayList<Integer>();
			tmpComponentList.add( _b );
//			componentList.put( a, tmpComponentList );
			if ( componentList.containsKey( _b ) ) {
				
				// add all items that were already components of b
				// to a's list, and track them separately
				tmpComponentList.addAll( componentList.get( _b ) );
				
				tmpIntermediateComponentList = componentList.get( _b );
				tmpIntermediateComponentList.add( _b );
				intermediateComponentList.put( _b, tmpIntermediateComponentList );
			}
			else {
				
				if ( intermediateComponentList.containsKey( _b ) ) {

					tmpIntermediateComponentList = intermediateComponentList.get( _b );
					tmpIntermediateComponentList.add( _a );
					intermediateComponentList.put( _b, tmpIntermediateComponentList );
				}
				else {
					
					tmpIntermediateComponentList = new ArrayList<Integer>();
					tmpIntermediateComponentList.add( _a );
					intermediateComponentList.put( _b, tmpIntermediateComponentList );
				}
			}

			componentList.put( _a, tmpComponentList );
		}
	}
    
    /*
     * Use Union-Find algorithm to assign 'positive' property to selected edges
     * 
     */
	// Note: incomplete: never received data needed to finish this code
    public void applyUnionFind_withComp() throws Exception {
//    public void assignEdgeProperties_Paul() throws Exception {

		final int numberOfVertices = this.getNumberOfVertices();

    	U_ = new int[ numberOfVertices ];
    	
		int a;
		int b;
		int aa;
		int bb;
		int count;
		final int numberOfEdges = edges_.size();
    	EdgeI tempEdge;
		
		boolean conditionFlag;
		
		
		count = numberOfVertices;
		for ( int i=0; i<numberOfVertices; i++ ) {
			
			p_[ i ] = -1;
		}
		for ( int i=0; i<numberOfVertices; i++ ) {
				
			U_[ i ] = i;
		}
		
		for ( int i=0; i<numberOfEdges; i++ ) {
		
			tempEdge = edges_.get( i );
			System.out.println( "\n\n* Adding edge= " + tempEdge );
			aa = tempEdge.getVertexIndex1();
			bb = tempEdge.getVertexIndex2();
			
			a = aa;
			b = bb;

			System.out.println( "U[]= " + StringUtil.arrayAsString( U_ ) );
			System.out.println( "componentList= " + componentList );
			
			// U portion
			while ( U_[ a ] != a ) { a = U_[ a ]; }
			while ( U_[ b ] != b ) { b = U_[ b ]; }
					
			if ( a != b ) {

				// hjs 10/21/2013 Mod to adjust for use of non-zero Mii values
				// Per John, this will allow us to keep the correct ordering
				if ( diag_[ a ] < diag_[ b ] ) {
					
					U_[ b ] = a;
					System.out.println( "setting uf-vertex: pointing b=" + b + " to a=" + a 
							+ ", aa=" + aa + ", bb=" + bb );
					tempEdge.setUnionFindVertexIndex( b );
					maintainComponentLists( a, b );
				}
				else if ( diag_[ a ] > diag_[ b ] ) {
					
					U_[ a ] = b;
					System.out.println( "setting uf-vertex: pointing a=" + a + " to b=" + b  
							+ ", aa=" + aa + ", bb=" + bb );
					tempEdge.setUnionFindVertexIndex( a );
					maintainComponentLists( b, a );
				}
				else {

					// hjs 6/21/2013, 6/26/2013 changes for Paul to provide the
					// associated vertex index in our 0-dim intervals
					// hjs 7/25/2013 Mod working with Paul:
					if ( a<b ) {

						U_[ b ] = a;
						System.out.println( "setting uf-vertex: pointing b=" + b + " to a=" + a  
								+ ", aa=" + aa + ", bb=" + bb );
						tempEdge.setUnionFindVertexIndex( b );
						maintainComponentLists( a, b );
					}
					else {

						U_[ a ] = b;
						System.out.println( "setting uf-vertex: pointing a=" + a + " to b=" + b  
								+ ", aa=" + aa + ", bb=" + bb );
						tempEdge.setUnionFindVertexIndex( a );
						maintainComponentLists( b, a );
					}
				}
				tempEdge.setUPositive( false );

				conditionFlag = false;
				
				// P portion
				if ( p_[ bb ] == -1 ) {
				 
					p_[ bb ] = i;
					conditionFlag = true;
				}
				else if ( p_[ aa ] == -1 ) {
				 
					p_[ aa ] = i;
					conditionFlag = true;
				}
				if( conditionFlag == true ){
				 
					tempEdge.setPPositive( false );
				}
				
				count--;
			}

			System.out.println( "U[]= " + StringUtil.arrayAsString( U_ ) );
			System.out.println( "componentList= " + componentList );
			System.out.println( "intermediateComponentList= " + intermediateComponentList );
			
		 	if ( count == 1 ) break;
		}
		
		
		for ( int i=0; i<numberOfVertices; i++ ) {

			if ( componentList.containsKey( i ) ) {

				System.out.println( "component i=" + i + 
						", list = " + componentList.get( i ) );
			}
		}
		
		for ( int i=0; i<numberOfVertices; i++ ) {
			
			if ( intermediateComponentList.containsKey( i ) ) {

				System.out.println( "intermediateComponent i=" + i + 
						", list = " + intermediateComponentList.get( i ) );
			}
		}
		
	    
	    // Store the edges with uPos<0 as 0-dim. persistence
    	EdgeI tmpEdge;
    	StringBuffer strBuf0dimPers = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersPlain = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersForDiagramDeaths = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersForDiagramBirths = new StringBuffer( "" );
    	ArrayList<Double> deathValues = new ArrayList<Double>();
    	ArrayList<Double> birthValues = new ArrayList<Double>();
    	
	    for ( int i=0; i< this.edges_.size(); i++ ) {
	    	
	    	tmpEdge = this.edges_.get( i );
	    	
	    	if ( !tmpEdge.isUPositive() ) {

	    		// hjs 10/25/2013 Eliminate all 0-dim. intervals of zero length 
	    		// from being reported
	    		if ( diag_[ tmpEdge.getUnionFindVertexIndex() ] != tmpEdge.getEdgeLength() ) {
	    			
		    		strBuf0dimPersPlain.append( " " + diag_[ tmpEdge.getUnionFindVertexIndex() ] + 
		    				", " + tmpEdge.getEdgeLength() + "\n"  );
		    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
		    				"( "  + diag_[ tmpEdge.getUnionFindVertexIndex() ] + 
		    				", " + tmpEdge.getEdgeLength() + " ), " +
		    				tmpEdge.getUnionFindVertexIndex() + 
		    				"\n"  );
		    		
		    		resCollZeroDimPers_.addResult( 
		    				new Interval( 
		    						tmpEdge.getEdgeListIndex(), 
		    						diag_[ tmpEdge.getUnionFindVertexIndex() ], 
		    						tmpEdge.getEdgeLength(),
		    						tmpEdge.getUnionFindVertexIndex() ) );
	    		
//	    		strBuf0dimPersPlain.append( " 0, " + tmpEdge.getEdgeLength() + "\n"  );
//	    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
//	    				"( 0, " + tmpEdge.getEdgeLength() + " ), " +
//	    				tmpEdge.getUnionFindVertexIndex() +
//	    				"\n"  );
//	    		
//	    		resCollZeroDimPers.addResult( 
//	    				new Interval( 
//	    						tmpEdge.getEdgeListIndex(), 
//	    						0, 
//	    						tmpEdge.getEdgeLength(),
//	    						tmpEdge.getUnionFindVertexIndex() ) );

		    		// record the birth and death values, for drawing routines
		    		
		    		birthValues.add( new Double( diag_[ tmpEdge.getUnionFindVertexIndex() ] ) );
		    		deathValues.add( new Double( tmpEdge.getEdgeLength() ) );
	    		}
	    	}
	    }
		System.out.println( "\nResults:\n" + strBuf0dimPers.toString() );
	    
	    // hjs 11/1/2013 Add "infinite" 0-dim intervals for indicating connected components
	    ArrayList<Integer> processedVertices = new ArrayList<Integer>();
	    for ( int i=0; i< this.edges_.size(); i++ ) {

    		tmpEdge = this.edges_.get( i );
    		
	    	// filter out all edges that "point to themselves" (i.e., represent a component)
	    	if ( !processedVertices.contains( tmpEdge.getVertexIndex1() ) &&
	    			U_[ tmpEdge.getVertexIndex1() ] == tmpEdge.getVertexIndex1() ) {
	    		
	    		
	    		strBuf0dimPersPlain.append( " " + diag_[ tmpEdge.getVertexIndex1() ] + 
	    				", INF\n"  );
	    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
	    				"( "  + diag_[ tmpEdge.getVertexIndex1() ] + 
	    				", INF ), " +
	    				tmpEdge.getVertexIndex1() + 
	    				"\n"  );
	    		
	    		resCollZeroDimPers_.addResult( 
	    				new Interval( 
	    						tmpEdge.getEdgeListIndex(), 
	    						diag_[ tmpEdge.getVertexIndex1() ], 
	    						-1,
	    						tmpEdge.getUnionFindVertexIndex() ) );
	    		

	    		birthValues.add( new Double( diag_[ tmpEdge.getVertexIndex1() ] ) );
	    		deathValues.add( new Double( -1 ) );
	    		
	    		processedVertices.add( tmpEdge.getVertexIndex1() );
	    	}
	    }
	    
	    zeroDimPers_ = strBuf0dimPers.toString();
	    zeroDimPersPlain_ = strBuf0dimPersPlain.toString();

    	// Compose the proper "v, v, v, ..., v" format to pass to drawing routine
	    // hjs 10/21/2013: add possible non-zero birth values to strings (instead of all zeros)
    	if ( deathValues.size() > 0 ) {
    	
    		strBuf0dimPersForDiagramDeaths.append( Double.toString( deathValues.get( 0 ) ) );
    		strBuf0dimPersForDiagramBirths.append( Double.toString( birthValues.get( 0 ) ) );
		    for ( int i=1; i< deathValues.size(); i++ ) {    	
	
		    	strBuf0dimPersForDiagramDeaths.append( 
	    				", " + Double.toString( deathValues.get( i ) ) );
	    		
		    	strBuf0dimPersForDiagramBirths.append( 
						", " + Double.toString( birthValues.get( i ) ) );
		    }
    	}
	    
	    zeroDimPersForDiagram_ = strBuf0dimPersForDiagramDeaths.toString();
	    zeroDimPersForDiagramZeros_ = strBuf0dimPersForDiagramBirths.toString();
    	
	    return;
	}
    
    /*
     * Use Union-Find algorithm to assign 'positive' property to selected edges
     * 
     */
    public void applyUnionFind() throws Exception {
//    public void assignEdgeProperties_orig() throws Exception {

		final int numberOfVertices = this.getNumberOfVertices();

    	U_ = new int[ numberOfVertices ];
    	
		int a;
		int b;
		int aa;
		int bb;
		int count;
		final int numberOfEdges = edges_.size();
    	EdgeI tempEdge;
		
		boolean conditionFlag;
		
		count = numberOfVertices;
		for ( int i=0; i<numberOfVertices; i++ ) {
			
			p_[ i ] = -1;
		}
		for ( int i=0; i<numberOfVertices; i++ ) {
				
			U_[ i ] = i;
		}
		
		for ( int i=0; i<numberOfEdges; i++ ) {
		
			tempEdge = edges_.get( i );
			aa = tempEdge.getVertexIndex1();
			bb = tempEdge.getVertexIndex2();
			
			a = aa;
			b = bb;
			
			// U portion
			while ( U_[ a ] != a ) { a = U_[ a ]; }
			while ( U_[ b ] != b ) { b = U_[ b ]; }
			
			if ( a != b ) {

				// hjs 10/21/2013 Mod to adjust for use of non-zero Mii values
				// Per John, this will allow us to keep the correct ordering
				if ( diag_[ a ] < diag_[ b ] ) {
					
					U_[ b ] = a;
					tempEdge.setUnionFindVertexIndex( b );
				}
				else if ( diag_[ a ] > diag_[ b ] ) {
					
					U_[ a ] = b;
					tempEdge.setUnionFindVertexIndex( a );
				}
				else {

					// hjs 6/21/2013, 6/26/2013 changes for Paul to provide the
					// associated vertex index in our 0-dim intervals
					// hjs 7/25/2013 Mod working with Paul:
					if ( a<b ) {

						U_[ b ] = a;
						tempEdge.setUnionFindVertexIndex( b );
					}
					else {

						U_[ a ] = b;
						tempEdge.setUnionFindVertexIndex( a );
					}
				}
				tempEdge.setUPositive( false );

				conditionFlag = false;
				
				// P portion
				if ( p_[ bb ] == -1 ) {
				 
					p_[ bb ] = i;
					conditionFlag = true;
				}
				else if ( p_[ aa ] == -1 ) {
				 
					p_[ aa ] = i;
					conditionFlag = true;
				}
				if( conditionFlag == true ){
				 
					tempEdge.setPPositive( false );
				}
				
				count--;
			}
		 
		 	if ( count == 1 ) break;
		}
	    
		
	    // Store the edges with uPos<0 as 0-dim. persistence
    	EdgeI tmpEdge;
    	StringBuffer strBuf0dimPers = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersPlain = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersForDiagramDeaths = new StringBuffer( "" );
    	StringBuffer strBuf0dimPersForDiagramBirths = new StringBuffer( "" );
    	ArrayList<Double> deathValues = new ArrayList<Double>();
    	ArrayList<Double> birthValues = new ArrayList<Double>();
    	
	    for ( int i=0; i< this.edges_.size(); i++ ) {
	    	
	    	tmpEdge = this.edges_.get( i );
	    	
	    	if ( !tmpEdge.isUPositive() ) {

	    		// hjs 10/25/2013 Eliminate all 0-dim. intervals of zero length 
	    		// from being reported
	    		if ( diag_[ tmpEdge.getUnionFindVertexIndex() ] != tmpEdge.getEdgeLength() ) {
	    			
		    		strBuf0dimPersPlain.append( " " + diag_[ tmpEdge.getUnionFindVertexIndex() ] + 
		    				", " + tmpEdge.getEdgeLength() + "\n"  );
		    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
		    				"( "  + diag_[ tmpEdge.getUnionFindVertexIndex() ] + 
		    				", " + tmpEdge.getEdgeLength() + " ), " +
		    				tmpEdge.getUnionFindVertexIndex() + 
		    				"\n"  );
		    		
		    		resCollZeroDimPers_.addResult( 
		    				new Interval( 
		    						tmpEdge.getEdgeListIndex(), 
		    						diag_[ tmpEdge.getUnionFindVertexIndex() ], 
		    						tmpEdge.getEdgeLength(),
		    						tmpEdge.getUnionFindVertexIndex() ) );
	    		
//	    		strBuf0dimPersPlain.append( " 0, " + tmpEdge.getEdgeLength() + "\n"  );
//	    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
//	    				"( 0, " + tmpEdge.getEdgeLength() + " ), " +
//	    				tmpEdge.getUnionFindVertexIndex() +
//	    				"\n"  );
//	    		
//	    		resCollZeroDimPers.addResult( 
//	    				new Interval( 
//	    						tmpEdge.getEdgeListIndex(), 
//	    						0, 
//	    						tmpEdge.getEdgeLength(),
//	    						tmpEdge.getUnionFindVertexIndex() ) );

		    		// record the birth and death values, for drawing routines
	    			
		    		birthValues.add( new Double( diag_[ tmpEdge.getUnionFindVertexIndex() ] ) );
		    		deathValues.add( new Double( tmpEdge.getEdgeLength() ) );
		    		
//		    		System.out.println( "Component: [" + diag_[ tmpEdge.getUnionFindVertexIndex() ] 
//		    				+ ", " + tmpEdge.getEdgeLength() + ")" );
	    		}
	    	}
	    }
	    
	    // hjs 11/1/2013 Add "infinite" 0-dim intervals for indicating connected components
	    ArrayList<Integer> processedVertices = new ArrayList<Integer>();
//	    for ( int i=0; i< this.edges_.size(); i++ ) {
//
//    		tmpEdge = this.edges_.get( i );
//    		
//	    	// filter out all edges that "point to themselves" (i.e., represent a component)
//	    	if ( !processedVertices.contains( tmpEdge.getVertexIndex1() ) &&
//	    			U_[ tmpEdge.getVertexIndex1() ] == tmpEdge.getVertexIndex1() ) {
//	    		
//	    		
//	    		strBuf0dimPersPlain.append( " " + diag_[ tmpEdge.getVertexIndex1() ] + 
//	    				", INF\n"  );
//	    		strBuf0dimPers.append( tmpEdge.getEdgeListIndex() + ": " +
//	    				"( "  + diag_[ tmpEdge.getVertexIndex1() ] + 
//	    				", INF ), " +
//	    				tmpEdge.getVertexIndex1() + 
//	    				"\n"  );
//	    		
//	    		resCollZeroDimPers_.addResult( 
//	    				new Interval( 
//	    						tmpEdge.getEdgeListIndex(), 
//	    						diag_[ tmpEdge.getVertexIndex1() ], 
//	    						-1,
//	    						tmpEdge.getUnionFindVertexIndex() ) );
//	    		
//
//	    		birthValues.add( new Double( diag_[ tmpEdge.getVertexIndex1() ] ) );
//	    		deathValues.add( new Double( -1 ) );
//	    		
//	    		processedVertices.add( tmpEdge.getVertexIndex1() );
//	    	}
//	    }
	    
	    // hjs 6/2/2014 -- important fix for not losing "isolated" components
	    // hjs 9/19/2014 -- replace -1 in 4th interval argument by unionFindVertexIndex
	    // (->Paul and Ellen's work; 0-dim. generators)
	    for ( int i=0; i<numberOfVertices; i++ ) {
	    	
	    	if ( U_[ i ] == i ) {
	    		
	    		birthValues.add( new Double( diag_[ i ] ) );
	    		deathValues.add( new Double( -1 ) );

	    		// TODO: check to make sure the extra "default" values don't get us in trouble down the road
	    		resCollZeroDimPers_.addResult( 
	    				new Interval( 
	    						-1, 
	    						diag_[ i ], 
	    						TDA.INTERVAL_VALUEINFINITY,
	    						i ) );
//								-1 ) );
//				-1 ) );
	    		
//	    		System.out.println( "Component: [" + diag_[ i ] + ", -1)" );
	    	}
	    }
	    
	    zeroDimPers_ = strBuf0dimPers.toString();
	    zeroDimPersPlain_ = strBuf0dimPersPlain.toString();

    	// Compose the proper "v, v, v, ..., v" format to pass to drawing routine
	    // hjs 10/21/2013: add possible non-zero birth values to strings (instead of all zeros)
    	if ( deathValues.size() > 0 ) {
    	
    		strBuf0dimPersForDiagramDeaths.append( Double.toString( deathValues.get( 0 ) ) );
    		strBuf0dimPersForDiagramBirths.append( Double.toString( birthValues.get( 0 ) ) );
		    for ( int i=1; i< deathValues.size(); i++ ) {    	
	
		    	strBuf0dimPersForDiagramDeaths.append( 
	    				", " + Double.toString( deathValues.get( i ) ) );
	    		
		    	strBuf0dimPersForDiagramBirths.append( 
						", " + Double.toString( birthValues.get( i ) ) );
		    }
    	}
	    
	    zeroDimPersForDiagram_ = strBuf0dimPersForDiagramDeaths.toString();
	    zeroDimPersForDiagramZeros_ = strBuf0dimPersForDiagramBirths.toString();
    	
	    return;
	}
    
    public ResultsCollection getZeroDimemsionalIntervals() {
    	
    	return resCollZeroDimPers_;
    }
    
    public String get0DimPers() {
    	
    	return zeroDimPers_;
    }
    
    public String get0DimPersPlain() {
    	
    	return zeroDimPersPlain_;
    }
    
    public String get0DimPersForDiagram() {
    	
    	return zeroDimPersForDiagram_;
    }
    
    public String get0DimPersForDiagramZeros() {
    	
    	return zeroDimPersForDiagramZeros_;
    }
    
    public String getPointCloudAsSparseMatrix() {
    	
    	return strBufSparseMat_.toString();
    }
}

   