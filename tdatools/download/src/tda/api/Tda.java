/*
 * Created 2014
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */

package tda.api;


import edu.duke.math.tda.application.*;
import edu.duke.math.tda.structures.results.ResultGeneric;
import edu.duke.math.tda.structures.results.ResultsCollection;
import edu.duke.math.tda.structures.results.ResultsContainerI;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.UTIL;
import edu.duke.math.tda.utility.settings.Settings;

/**
 * Wrapper to main Tda functions in ApplicationTda class, for API purposes (Matlab, etc).
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2014
 * <p>
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 * 
 */

public class Tda {

	ApplicationTda appTda_;

	ResultsContainerI resultsContainer_;
	
	Settings processData_;

	public Tda() {
		
		try {
			
			// start fresh
			reset();	
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not reset Tda object." );
//			e.printStackTrace();
		}	
	}
	
	public Tda( final String _configArg ) {
		
		try {
			
			// start fresh
			reset();
			
			this.assignData( _configArg );
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not create Tda object with argument '" + _configArg + "'." );
//			e.printStackTrace();
		}
	}
	
	public Tda( final String[] _configArg ) {
		
		try {
			
			// start fresh
			reset();
			
			this.assignData( _configArg );
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not create Tda object with arguments '" + _configArg + "'." );
//			e.printStackTrace();
		}
	}
	
	public Tda( final double[] _configArg ) {
		
		try {
			
			// start fresh
			reset();
			
			this.assignData( _configArg );
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not create Tda object with arguments '" + _configArg + "'." );
//			e.printStackTrace();
		}
	}
	
	public Tda( final double[][] _dblArgs ) {
		
		try {
			
			// start fresh
			reset();
			
			this.assignData( _dblArgs );
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not create Tda object with arguments '" 
					+ _dblArgs + "'." );
//			e.printStackTrace();
		}
	}
	
	public Tda( final String[] _args, final double[][] _dblArgs ) {
		
		try {
			
			// start fresh
			reset();
			
			this.assignData( _args, _dblArgs );
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR] Could not create Tda object with arguments '" 
					+ _args	+ "' and the supplied array of type double[][]." );
//			e.printStackTrace();
		}
	}
	
	public void reset() {

		// reset to a new ApplicationTda object
		appTda_ = new ApplicationTda();
		
		// get the associated settings
		processData_ = appTda_.getSettings();
	}
	
	

	public ResultsContainerI RCA0() {
		
		String[] rca0Args = new String[ 2 ];
		rca0Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_RCA0;
		rca0Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( rca0Args );			
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA0] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}
	
	public ResultsContainerI RCA0( final String[] _args ) {
		
		String[] rca0Args = new String[ 2 ];
		rca0Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_RCA0;
		rca0Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {
			
			appTda_.assignData( _args );
			appTda_.assignData( rca0Args );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA0_args] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	public ResultsContainerI RCA0( final double[][] _dblArgs ) {
		
		String[] rca0Args = new String[ 2 ];
		rca0Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_RCA0;
		rca0Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( _dblArgs );
			appTda_.assignData( rca0Args );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
			
			if ( getLastExecutedTaskReturnValue() == 0 ) {
				
				resultsContainer_ = appTda_.getResults();
			}
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA0_dblArgs] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	public ResultsContainerI RCA0( final String[] _args, final double[][] _dblArgs ) {
		
		String[] rca1Args = new String[ 2 ];
		rca1Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_RCA0;
		rca1Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;

		try {

			appTda_.assignData( _args );
			appTda_.assignData(_dblArgs );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
			
			if ( getLastExecutedTaskReturnValue() == 0 ) {
				
				resultsContainer_ = appTda_.getResults();
			}
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA0_args_dblArgs] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	
	
	

	public ResultsContainerI RCA1() {
		
		String[] rca1Args = new String[ 2 ];
		rca1Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M12;
		rca1Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( rca1Args );			
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA1] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}
	
	//// deprec all other RCA1 calls? Keep for now for backwards compatibility
	public ResultsContainerI RCA1( final String[] _args ) {
		
		String[] rca1Args = new String[ 2 ];
		rca1Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M12;
		rca1Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {
			
			appTda_.assignData( _args );
			appTda_.assignData( rca1Args );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA1_args] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	public ResultsContainerI RCA1( final double[][] _dblArgs ) {
		
		String[] rca1Args = new String[ 2 ];
		rca1Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M12;
		rca1Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( _dblArgs );
			appTda_.assignData( rca1Args );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
			
			if ( getLastExecutedTaskReturnValue() == 0 ) {
				
				resultsContainer_ = appTda_.getResults();
			}
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA1_dblArgs] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	public ResultsContainerI RCA1( final String[] _args, final double[][] _dblArgs ) {
		
		String[] rca1Args = new String[ 2 ];
		rca1Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M12;
		rca1Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;

		try {

			appTda_.assignData( _args );
			appTda_.assignData(_dblArgs );
			appTda_.executeTda();

			resultsContainer_ = appTda_.getResults();
			
			if ( getLastExecutedTaskReturnValue() == 0 ) {
				
				resultsContainer_ = appTda_.getResults();
			}
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA1_args_dblArgs] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}

		return resultsContainer_;
	}
	
	
	
	
	public ResultsContainerI RCA2() {
		
		String[] rca2Args = new String[ 2 ];
		rca2Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M23;
		rca2Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( rca2Args );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA2] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}
	
	// Keep wrappers for backward compatibility
	public ResultsContainerI RCA2( final String[] _args ) {
		
		String[] rca2Args = new String[ 2 ];
		rca2Args[ 0 ] = "taskChoice=" + TDA.UI_TASK_M23;
		rca2Args[ 1 ] = "applicationMode=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( _args );
			appTda_.assignData( rca2Args );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA2_args] Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}
	
	public ResultsContainerI RCA2( final double[][] _dblArgs ) {

		String[] rca2Args = new String[ 2 ];
		rca2Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M23;
		rca2Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( rca2Args );
			appTda_.assignData( _dblArgs );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA2_dblArgs]  Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}

	public ResultsContainerI RCA2( final String[] _args, final double[][] _dblArgs ) {

		String[] rca2Args = new String[ 2 ];
		rca2Args[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_M23;
		rca2Args[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
		
		try {

			appTda_.assignData( _args );
			appTda_.assignData( rca2Args );
			appTda_.assignData( _dblArgs );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			System.out.println( "[ERROR RCA2_args_dblArgs]  Exception encountered: " + e.toString() );
			e.printStackTrace();
		}
		
		return resultsContainer_;
	}
	
	public ResultsContainerI LSD( final double[][] pointCloud_, 
			final double radius_, 
			final double[] point_ ) {
		
		// TODO: use generic container to put stuff in (avoid array with it's rigidity)
		String[] lsdArgs = new String[ 5 ];
		
			
		try {

			lsdArgs[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_LSD;
			lsdArgs[ 1 ] = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD + "=" + Double.toString( radius_ );
			lsdArgs[ 2 ] = TDA.SETTING_CENTERPOINTFORLOCALNBHD + "=" + 
								UTIL.convertToString( point_ );
			lsdArgs[ 3 ] = TDA.SETTING_DATALOADEDASARRAY + "=" + 
								UTIL.convertToString( pointCloud_ );
			lsdArgs[ 4 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
			
			appTda_.assignData( lsdArgs );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			resultsContainer_.addRegisteredResult( 
					TDA.DATA_REGISTEREDRESULT_LSD_ERROR1,
					e, 
					"[ERROR Tda.LSD-1]" );
			
			if ( TDA.DEBUG ) {

				System.out.println( "[ERROR LSD-1]  Exception encountered: " + e.toString() );
				e.printStackTrace();
			}
		}	

		return resultsContainer_;		
	}
	
	public ResultsContainerI LSD( final double[] pointCloud_, 
			final double radius_, 
			final double point_ ) {
				
		// TODO: use generic container to put stuff in (avoid array with it's rigidity)
		String[] lsdArgs = new String[ 5 ];
		
		
		try {

			lsdArgs[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_LSD;
			lsdArgs[ 1 ] = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD + "=" + Double.toString( radius_ );
			lsdArgs[ 2 ] = TDA.SETTING_CENTERPOINTFORLOCALNBHD + "=" + point_;
			lsdArgs[ 3 ] = TDA.SETTING_DATALOADEDASARRAY + "=" + 
								UTIL.convertToString( pointCloud_ );
			lsdArgs[ 4 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
			
			appTda_.assignData( lsdArgs );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			resultsContainer_.addRegisteredResult( 
					TDA.DATA_REGISTEREDRESULT_LSD_ERROR2,
					e, 
					"[ERROR Tda.LSD-2]" );
			
			if ( TDA.DEBUG ) {

				System.out.println( "[ERROR Tda.LSD-2]  Exception encountered: " + e.toString() );
				e.printStackTrace();
			}
		}

		return resultsContainer_;		
	}
	
	public ResultsContainerI LSD() {

		// Here all settings have been loaded already -- note that we should take a look at what
		// data the user has 'assigned', and make sure it makes sense
		//

		String[] lsdArgs = new String[ 2 ];
		
		try {

			
			lsdArgs[ 0 ] = TDA.SETTING_TASKCHOICE + "=" + TDA.UI_TASK_LSD;
			lsdArgs[ 1 ] = TDA.SETTING_APPLICATIONMODE + "=" + TDA.UI_APPLICATIONMODE_API;
			
			
			// TODO to be on the safe side, examine the data that is used for running
			
			
			appTda_.assignData( lsdArgs );
			appTda_.executeTda();
			resultsContainer_ = appTda_.getResults();
		}
		catch ( Exception e ) {
			
			resultsContainer_.addRegisteredResult( 
					TDA.DATA_REGISTEREDRESULT_LSD_ERROR3,
					e, 
					"[ERROR Tda.LSD()]" );
			
			if ( TDA.DEBUG ) {

				System.out.println( "[ERROR Tda.LSD-3]  Exception encountered: " + e.toString() );
				e.printStackTrace();
			}
		}

		return resultsContainer_;		
	}
				
	// TODO: deprec for a more general method??
	public Object getResults( int _intSelector ) throws Exception {
		
		Object resultToReturn = new Object();
		
		ResultsContainerI resultsContainer;
		resultsContainer = this.appTda_.getResults();
		ResultGeneric res;
		
		res = resultsContainer.getResult( _intSelector );
		
		// Check all allowable "specific" result types
		// (we need to cast here because Api "consumers" such as Matlab otherwise
		// can't make sense of the generic return type (and so wouldn't be
		// able to get to the results)
		if ( res.getResult() instanceof ResultsCollection ) {
			
			resultToReturn = (ResultsCollection) res.getResult();
		}
		else {
			
			// default (TODO)
			resultToReturn = res.getResult();
		}
		
		return resultToReturn;
	}
	
	public ResultsContainerI getResults() {

		return this.appTda_.getResults();
	}
	
	protected int getLastExecutedTaskReturnValue() {
		
		return this.appTda_.getLastExecutedTaskReturnValue();
	}
	
	// Trivial wrapper for better end user experience
	public String getInfo() {
		
		return this.appTda_.getInfo( 0 );
	}
	
	public String getInfo( final int _requestedInfoFlag ) {
		
		return this.appTda_.getInfo( _requestedInfoFlag );
	}
	
	// TODO: enough info?
	public String toString() {
		
		// Info to the user what this object is ("think" Matlab, etc)
		StringBuffer strObjDescription = new StringBuffer( TDA.APPLICATION_NAME + " " +
				TDA.APPLICATION_VERSIONNUMBER + ", " + 
				TDA.APPLICATION_VERSIONDATE );
		
		// TODO:  ? add more relevant info
		// ? last result summary?
		
		if ( appTda_.getResults() != null && appTda_.getResults().getResultsCount() > 0 ) {

			strObjDescription.append( "\n Results of the last program execution:\n" );

			for (int i=0; i< resultsContainer_.getResultsCount(); i++ ) {
				
				strObjDescription.append( "\n " + i + " ):\n" +  resultsContainer_.getResult( i ) );
			}
		}
		
		
		return strObjDescription.toString();
	}
	
	
	// TODO: need to handle the Exceptions earlier!!! 
	
	
	
	// TODO
	// Need to be careful: when an existing (i.e., already stored) parameter is being
	// specified in a new setParams call, we need to replace the old value with the new one.
	public void assignData( final String _arg ) throws Exception {
			
			this.appTda_.assignData( _arg );
		}
		
	public void assignData( final String[] _args ) throws Exception {
		
		this.appTda_.assignData( _args );
	}
	
	public void assignData( final double[] _dblArgs ) 
			throws Exception {

		this.appTda_.assignData( _dblArgs );
	}
	
	public void assignData( final double[][] _dblArgs ) 
			throws Exception {

		this.appTda_.assignData( _dblArgs );
	}
	
	public void assignData( final String[] _args, final double[][] _dblArgs ) 
			throws Exception {

		this.appTda_.assignData( _args, _dblArgs );
	}
	
	public String getParameters() {
		
		// return the parameters (without the doubles array)
		return this.appTda_.getParameters();
	}
	
	public String getData() {

		// return the doubles data array
		return this.appTda_.getAssignedData();
	}
	
	public String getDataInternal() {

		// return the doubles data array
		return this.appTda_.getPassedInParameters();
	}
	
	
	
	// TODO: allow access to single parameter???
	// i.e. "tell me what the value for parameter x is"
	
	
	
	// TODO
	protected String getFeedback() {
		
		String feedback = new String();
		
		return feedback;
	}
}
