/*
 * Created November 2012
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
import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.duke.math.tda.structures.EdgeI;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * EdgePair class for representing pairs of edges that share a common vertex.
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 *  
 * <p><strong>Change History:</strong> <br>
 * Created November 2012
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class EdgeMatrixAsMultiDimArray extends EdgeMatrix {

	// class specific information


	protected int observedRowCount_;
	protected int observedColumnCount_;
	
	// array for storing the edge length values
	protected double[][] edgeLength_;
	
	// array for storing the index of an edge (between 2 vertices, 
	// specified by their indices)
	protected int[][] edgeIndex_;
			
	public EdgeMatrixAsMultiDimArray( final DistanceMatrix _distMatrix ) throws Exception {
				
		// call the super-class constructor
		super( _distMatrix );
		
		// for the edgeLength values we can use implicit initializing to 0
//		edgeLength_ = new double[ numberOfVertices_ ][ numberOfVertices_ ];
		
		// Load the distance matrix
//		edgeLength_ = loadDistanceMatrixFromFile();
		edgeLength_ = _distMatrix.getDistances();
		observedRowCount_ = _distMatrix.getNumberOfRows();
		

//		setup( observedRowCount );
		
		// Set up edge indices
		edgeIndex_ = new int[ observedRowCount_ ][ observedRowCount_ ];
		
		// initialize all edge indices to -1 (we need to do this, because our edge index
		// values start at 0, i.e., we can't use implicit initializing to 0.
		for ( int i=0; i<observedRowCount_; i++ ) {
			for ( int j=0; j<observedRowCount_; j++ ) {
			
				edgeIndex_[ i ][ j ] = -1;
			}
		}
	}
	
	// should never be used?
//	public EdgeMatrixAsMultiDimArray( final SparseMatrix _sparseMatrix ) throws Exception {
//				
//		// call the super-class constructor
//		super( _sparseMatrix );
//		
//		// for the edgeLength values we can use implicit initializing to 0
////		edgeLength_ = new double[ numberOfVertices_ ][ numberOfVertices_ ];
//		
//		// Load the distance matrix
////		edgeLength_ = loadDistanceMatrixFromFile();
//		edgeLength_ = _sparseMatrix.getDistances();
//		observedRowCount_ = _sparseMatrix.getNumberOfRows();
//		
//
////		setup( observedRowCount );
//		
//		// Set up edge indices
//		edgeIndex_ = new int[ observedRowCount_ ][ observedRowCount_ ];
//		
//		// initialize all edge indices to -1 (we need to do this, because our edge index
//		// values start at 0, i.e., we can't use implicit initializing to 0.
//		for ( int i=0; i<observedRowCount_; i++ ) {
//			for ( int j=0; j<observedRowCount_; j++ ) {
//			
//				edgeIndex_[ i ][ j ] = -1;
//			}
//		}
//	}

	public EdgeMatrixAsMultiDimArray( final String _strDistanceMatrixAsString ) throws Exception {
		
		super( _strDistanceMatrixAsString );

		// Load the distance matrix
////		edgeLength_ = loadDistanceMatrixFromFile();
		
		// Set up edge indices
		edgeIndex_ = new int[ observedRowCount_ ][ observedRowCount_ ];
		
		// initialize all edge indices to -1 (we need to do this, because our edge index
		// values start at 0, i.e., we can't use implicit initializing to 0.
		for ( int i=0; i<observedRowCount_; i++ ) {
			for ( int j=0; j<observedRowCount_; j++ ) {
			
				edgeIndex_[ i ][ j ] = -1;
			}
		}
	}
	
	public EdgeMatrixAsMultiDimArray( final int _numberOfVertices ) throws Exception {
		
		// call the super-class constructor
		super( _numberOfVertices );
		
		this.numberOfVertices_ = _numberOfVertices;
		
		// for the edgeLength values we can use implicit initializing to 0
		edgeLength_ = new double[ _numberOfVertices ][ _numberOfVertices ];
		
		edgeIndex_ = new int[ _numberOfVertices ][ _numberOfVertices ];
		
		// initialize all edge indices to -1 (we need to do this, because our edge index
		// values start at 0, i.e., we can't use implicit initializing to 0.
		for ( int i=0; i<_numberOfVertices; i++ ) {
			for ( int j=0; j<_numberOfVertices; j++ ) {
			
				edgeIndex_[ i ][ j ] = -1;
			}
		}
	}
	
	protected void setup( final int _numberOfVertices ) throws Exception {
				
		this.numberOfVertices_ = _numberOfVertices;
		
		// for the edgeLength values we can use implicit initializing to 0
		edgeLength_ = new double[ _numberOfVertices ][ _numberOfVertices ];
		
		edgeIndex_ = new int[ _numberOfVertices ][ _numberOfVertices ];
		
		// initialize all edge indices to -1 (we need to do this, because our edge index
		// values start at 0, i.e., we can't use implicit initializing to 0.
		for ( int i=0; i<_numberOfVertices; i++ ) {
			for ( int j=0; j<_numberOfVertices; j++ ) {
			
				edgeIndex_[ i ][ j ] = -1;
			}
		}
	}
	
	public void setEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2,
							final int _edgeIndex ) throws Exception {
		
		try {
		
			if ( _vertexIndex1 < _vertexIndex2 ) {

				this.edgeIndex_[ _vertexIndex1 ][ _vertexIndex2 ] = _edgeIndex;
				connectionsPerVertex[ _vertexIndex1 ]++;
			}
			else {

				this.edgeIndex_[ _vertexIndex2 ][ _vertexIndex1 ] = _edgeIndex;
				connectionsPerVertex[ _vertexIndex2 ]++;
			}
		}
		catch ( Exception e ) {
		
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrixAsMultiDimArray] Could not set the edge index for v1=" +
                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
		}
	}

	public int getEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception {
		
		try {
		
			if ( _vertexIndex1 < _vertexIndex2 ) {
			
				return edgeIndex_[ _vertexIndex1 ][ _vertexIndex2 ];
			}
			else {
			
				return edgeIndex_[ _vertexIndex2 ][ _vertexIndex1 ];
			}
		}
		catch ( Exception e ) {
		
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrixAsMultiDimArray] Could not retrieve edge index for v1=" +
                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
		}
	}
	
	public void setEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2,
							final double _distance ) throws Exception {
		

		
		if ( TDA.DEBUG  && TDA.TRACE_COMPUTELSD ) {
			
    		System.out.println( "\n*** [EdgeMatrixAsMultiDimArray.setEdgeLength] Setting M (i=" + _vertexIndex1 + ", j=" + 
    					_vertexIndex2 + ") to " + _distance + "\n" );
		}
		
		try {

	    	if ( Double.isNaN( _distance )) {
	    		
	    		System.out.println( "setEdgeLength is setting NaN, for i=" + _vertexIndex1 + ", j=" + _vertexIndex2 );
	    	}
	    	
			if ( _vertexIndex1 < _vertexIndex2 ) {

				this.edgeLength_[ _vertexIndex1 ][ _vertexIndex2 ] = _distance;
				connectionsPerVertex[ _vertexIndex1 ]++;
			}
			else {

				this.edgeLength_[ _vertexIndex2 ][ _vertexIndex1 ] = _distance;
				connectionsPerVertex[ _vertexIndex2 ]++;
			}
		}
		catch ( Exception e ) {
		
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrixAsMultiDimArray] Could not set the edge length for v1=" +
                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
		}
	}

	
	public double getEdgeLength( final int _vertexIndex1, 
			final int _vertexIndex2 ) throws Exception {
		
		return this.edgeLength_[ _vertexIndex1 ][ _vertexIndex2 ];
	}

	
	public double[][] getEdgeLengths() throws Exception {
		
		return this.edgeLength_;
	}

//	public double getEdgeLength( final int _vertexIndex1, 
//							final int _vertexIndex2 ) throws Exception {
//		
//		try {
//		
//			if ( _vertexIndex1 < _vertexIndex2 ) {
//			
//				return edgeLength_[ _vertexIndex1 ][ _vertexIndex2 ];
//			}
//			else {
//			
//				return edgeLength_[ _vertexIndex2 ][ _vertexIndex1 ];
//			}
//		}
//		catch ( Exception e ) {
//		
//			throw new CompTopoException( 
//                    COMPTOPO.ERROR_COMPTOPO_DEV, 
//                    "[EdgeMatrix] Could not retrieve the edge length for v1=" +
//                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
//		}
//	}
	
	public int getNumberOfConnections( final int _vertexIndex ) {
		
		return connectionsPerVertex[ _vertexIndex ];
	}

	public int getDimension() {
		
		return this.observedRowCount_;
	}
	    
    public String toString() {

    	StringBuffer strBufMatrix = new StringBuffer( "" );
    	
    	int matrixDimension = this.edgeLength_.length;
    	
		for ( int i=0; i<matrixDimension; i++ ) {
			
			for ( int j=0; j<matrixDimension; j++ ) {
			
				strBufMatrix.append( this.edgeLength_[ i ][ j ] + "\t" );
			}
			
			strBufMatrix.append( "\n" );
		}
		
    	return strBufMatrix.toString();
    }

	@Override
	public EdgeI getEdge( int _vertexIndex1, int _vertexIndex2 ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
