/*
 * Created Jul 8, 2013
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

import edu.duke.math.tda.structures.EdgeI;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * Documents 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * <p><strong>Change History:</strong> <br>
 * Created on Jul 8, 2013
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class EdgeMatrixAsSortedMap extends EdgeMatrix {

	// class specific information

	protected int numberOfEntries_;

	protected int observedRowCount_;
	protected int observedColumnCount_;
	

	// sparse matrix for storing the edges
	protected SparseMatrix sparseMatrix_;
	
	protected int[] edgeIndexSparse_;
	
	public EdgeMatrixAsSortedMap( final SparseMatrix _sparseMatrix ) throws Exception {
				
		// call the super-class constructor
		super( _sparseMatrix );
		
		sparseMatrix_ = _sparseMatrix;		
		
		numberOfEntries_ = _sparseMatrix.getDimension();
		observedRowCount_ = _sparseMatrix.getNumberOfVertices();
		

//		setup( observedRowCount );
	}

	public EdgeMatrixAsSortedMap( final String _strSparseMatrixAsString ) throws Exception {
		
		super( _strSparseMatrixAsString );
	}
	
	public EdgeMatrixAsSortedMap( final int _numberOfVertices ) throws Exception {
		
		// call the super-class constructor
		super( _numberOfVertices );
		
		this.numberOfVertices_ = _numberOfVertices;
	}
	
	protected void setup( final int _numberOfVertices ) throws Exception {
				
		this.numberOfVertices_ = _numberOfVertices;
	}
	
	public void setEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2,
							final int _edgeListIndex ) throws Exception {
		
		try {

			EdgeI tmpEdge;
			int rowIndex;
			int colIndex;
			int tmpIndex;

			if ( _vertexIndex1 < _vertexIndex2 ) {

				rowIndex = _vertexIndex1;
				colIndex = _vertexIndex2;
			}
			else {

				rowIndex = _vertexIndex2;
				colIndex = _vertexIndex1;
			}

			// 7/12/2013 hjs fix for sparse data
			tmpIndex = this.sparseMatrix_.getSparseEdgeIndex( rowIndex, colIndex );
			tmpEdge = this.sparseMatrix_.getEdges().get( tmpIndex );

			tmpEdge.setEdgeListIndex( _edgeListIndex );
		}
		catch ( Exception e ) {
		
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrixAsSortedMap] Could not set the edge index for v1=" +
                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
		}
	}

	public int getEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception {

		EdgeI tmpEdge;
		int rowIndex;
		int colIndex;
		int tmpIndex;
		
		try {
		
			// 7/12/2013 hjs fix for sparse data
			if ( _vertexIndex1 < _vertexIndex2 ) {

				rowIndex = _vertexIndex1;
				colIndex = _vertexIndex2;
			}
			else {

				rowIndex = _vertexIndex2;
				colIndex = _vertexIndex1;
			}
			
			tmpIndex = this.sparseMatrix_.getSparseEdgeIndex( rowIndex, colIndex );
			tmpEdge = this.sparseMatrix_.getEdges().get( tmpIndex );
			return tmpEdge.getEdgeListIndex();
		}
		catch ( Exception e ) {
		
			// when there is no edge corresponding to the row/col index values, we
			// return the "flag" for 'no edge'
			return -1;
			
//			throw new CompTopoException( 
//                    COMPTOPO.ERROR_COMPTOPO_DEV, 
//                    "[EdgeMatrixAsSortedMap] Could not retrieve edge index for v1=" +
//                    _vertexIndex1 + ", v2=" + _vertexIndex2, this );
		}
	}
	
	public void setEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2,
							final double _distance ) throws Exception {
		
		try {

			// TODO: throw this as reminder that we don't have this implemented
			// (should actually be never needed -- for sparse matrix, the edge length is  
			// computed and then passed into the constructor)
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrix] Could not set the edge length for rowIndex=" +
                    _vertexIndex1 + ", colIndex=" + _vertexIndex2, this );
			
//	    	if ( Double.isNaN( _distance )) {
//	    		
//	    		System.out.println( "setEdgeLength is setting NaN, for i=" + _vertexIndex1 + ", j" + _vertexIndex2 );
//	    	}
//		
//			Edge tmpEdge;
//
//			tmpEdge = this.sparseMatrix_.getEdges().get( 
//					_rowIndex * getNumberOfVertices() + _colIndex );
//			tmpEdge.setEdgeListIndex( _distance );
//	    	
//			if ( _vertexIndex1 < _vertexIndex2 ) {
//
////				this.edgeLength_[ _vertexIndex1 ][ _vertexIndex2 ] = _distance;
//				connectionsPerVertex[ _vertexIndex1 ]++;
//			}
//			else {
//
////				this.edgeLength_[ _vertexIndex2 ][ _vertexIndex1 ] = _distance;
//				connectionsPerVertex[ _vertexIndex2 ]++;
//			}
		}
		catch ( Exception e ) {
		
			throw new TdaException( 
                    TDA.ERROR_APP_DEV, 
                    "[EdgeMatrixAsSortedMap] Could not set the edge length for rowIndex=" +
                    _vertexIndex1 + ", colIndex=" + _vertexIndex2, this );
		}
	}

	
	public double getEdgeLength( final int _vertexIndex1, 
			final int _vertexIndex2 ) throws Exception {

		EdgeI tmpEdge;
		int rowIndex;
		int colIndex;

		if ( _vertexIndex1 < _vertexIndex2 ) {

			rowIndex = _vertexIndex1;
			colIndex = _vertexIndex2;
		}
		else {

			rowIndex = _vertexIndex2;
			colIndex = _vertexIndex1;
		}
		
		tmpEdge = this.sparseMatrix_.getEdges().get( 
				rowIndex * getNumberOfVertices() + colIndex );
		
		return tmpEdge.getEdgeLength();
	}
	
	public int getNumberOfConnections( final int _vertexIndex ) {
		
		return connectionsPerVertex[ _vertexIndex ];
	}

	public int getDimension() {
		
		return this.observedRowCount_;
	}
		    
    public String toString() {

    	StringBuffer strBufMatrix = new StringBuffer( " to do " );
    	
//    	int matrixDimension = this.edgeLength_.length;
    	
//		for ( int i=0; i<matrixDimension; i++ ) {
//			
//			for ( int j=0; j<matrixDimension; j++ ) {
//			
//				strBufMatrix.append( this.edgeLength_[ i ][ j ] + "\t" );
//			}
//			
//			strBufMatrix.append( "\n" );
//		}
		
    	return strBufMatrix.toString();
    }

	@Override
	public EdgeI getEdge( final int _rowIndex, final int _colIndex ) throws Exception {
		
		int tmpIndex = sparseMatrix_.getSparseEdgeIndex( _rowIndex, _colIndex );
		return sparseMatrix_.getEdges().get( tmpIndex );
	}
}
