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
import java.io.StringReader;
import java.util.StringTokenizer;
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

public abstract class EdgeMatrix implements EdgeMatrixI {

	// primary information
	protected int numberOfVertices_;
	
	// secondary information
	protected int[] connectionsPerVertex;

	protected BufferedReader bufferedReader_;
	protected String strMatrixAsString_ = new String();

	public EdgeMatrix( final String _strMatrixAsString ) {

		strMatrixAsString_ = new String( _strMatrixAsString );
    	bufferedReader_ = new BufferedReader( new StringReader( _strMatrixAsString ) );
	}
	
	public EdgeMatrix( final String _directory, final String _fileName ) throws Exception {
		       
        File dataFile = new File(_directory, _fileName);
    
        if (!dataFile.exists()) {
            
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(Loading distance matrix) " +
                    "Cannot find the file: '" 
                    + _fileName + "' in directory '" + _directory + "'." );
        }
        
        bufferedReader_ = new BufferedReader(
                new FileReader( _directory + File.separator + _fileName ) );
	}
	
	public EdgeMatrix( final DistanceMatrix _distMatrix ) throws Exception {
		       
		connectionsPerVertex = new int[ _distMatrix.getNumberOfVertices() ];
	}
	
	public EdgeMatrix( final SparseMatrix _sparseMatrix ) throws Exception {
		       
		connectionsPerVertex = new int[ _sparseMatrix.getNumberOfVertices() ];
	}
	
	public EdgeMatrix( final int _numberOfVertices ) throws Exception {
		
		connectionsPerVertex = new int[ _numberOfVertices ];
	};

	public int getNumberOfVertices() {
	
		return this.numberOfVertices_;
	}

	public abstract int getDimension();
	
	@Override
	public abstract EdgeI getEdge( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;
	
	@Override
	public abstract double getEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;

	@Override
	public abstract int getEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2 ) throws Exception;


	@Override
	public abstract void setEdgeIndex( final int _vertexIndex1, 
							final int _vertexIndex2,
							final int _edgeIndex ) throws Exception;

	@Override
	public abstract void setEdgeLength( final int _vertexIndex1, 
							final int _vertexIndex2,
							final double _edgeLength ) throws Exception;

	public abstract int getNumberOfConnections( final int _vertexIndex );
}
