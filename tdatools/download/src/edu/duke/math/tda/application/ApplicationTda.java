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
package edu.duke.math.tda.application;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.duke.math.tda.algorithm.*;
import edu.duke.math.tda.structures.results.ResultsContainer;
import edu.duke.math.tda.structures.results.ResultsContainerI;
import edu.duke.math.tda.utility.*;
import edu.duke.math.tda.utility.errorhandling.TdaErrorHandler;
import edu.duke.math.tda.utility.errorhandling.TdaException;
import edu.duke.math.tda.utility.settings.SettingItem;
import edu.duke.math.tda.utility.settings.Settings;

/** 
 * Main class for accessing the Tda code
 * 
 * 
 * <p><strong>Details:</strong> <br>
 *  
 * 
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class ApplicationTda {

    protected static Settings processData_;
    protected String[] storedArgs_;
    protected static TaskI task_;
    protected ResultsContainerI resultsContainer_; // = new ResultsContainer();
    int lastExecutedTaskReturnValue_;
    
	protected SortedMap<String, String> rawSettingsList_;
	
    
    public ApplicationTda() {

		TdaErrorHandler errorHandler = new TdaErrorHandler();
		rawSettingsList_ = new TreeMap<String, String>();
		resultsContainer_ = new ResultsContainer();
		
    	try {
    		
			processData_ = new Settings();
    		errorHandler = new TdaErrorHandler( processData_ );
		}
		catch ( final TdaException e ) {
		    
			System.out.println( "[Tda-] Error getting new settings!" );
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {

			System.out.println( "[Gen.] Error getting new settings!" );
		    errorHandler.handleGeneralException( e );
		}
    }
    
    public ApplicationTda( Settings _processData ) {

		TdaErrorHandler errorHandler = new TdaErrorHandler();
		rawSettingsList_ = new TreeMap<String, String>();
		resultsContainer_ = new ResultsContainer();
		
		try {
		
			if ( _processData == null ) {
				
				processData_ = new Settings();
			}
			else {
		    	
				// TODO -- deep copy  VS assignment??
		    	processData_ = _processData;
			}
	    	
			errorHandler = new TdaErrorHandler( processData_ );	
		}
		catch ( final TdaException e ) {

			System.out.println( "[Tda- 2] Error getting new settings!" );
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {

			System.out.println( "[Gen. 2] Error getting new settings!" );
		    errorHandler.handleGeneralException( e );
		}
    }
    	
    // main entry point for stand-alone use
	public static void main( String[] _args ) {

		TdaErrorHandler errorHandler = new TdaErrorHandler();
		ApplicationTda tda = new ApplicationTda();
		
		try {

            // Load and validate the parameters for running the application
			if ( processData_ == null ) {
            
				processData_ = new Settings();
	    		errorHandler = new TdaErrorHandler( processData_ );
			}
			
			tda.assignData( _args );
			tda.executeTda();
		}
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
	}
	
	protected String convertArrayToString( final double[] _arrayToConvert )
			throws Exception {

		
		// Create the extra argument, for supplying the point cloud in string form
		StringBuffer dataLoadedAsArray = new StringBuffer();

		// translate the passed-in array into a string (using tabs as white-space 
		// delimiters, and inserting crlf's) 
		for ( int i=0; i<_arrayToConvert.length; i++ ) {

			dataLoadedAsArray.append( _arrayToConvert[ i ] + "\n" );
		}
		
		return dataLoadedAsArray.toString();
	}
	
	protected String convertArrayToString( final double[][] _arrayToConvert )
			throws Exception {

		int arrayDimension;

		arrayDimension = _arrayToConvert[ 0 ].length;
		
		// Create the extra argument, for supplying the point cloud in string form
		StringBuffer dataLoadedAsArray = new StringBuffer();

		// translate the passed-in array into a string (using tabs as white-space 
		// delimiters, and inserting crlf's) 
		for ( int i=0; i<_arrayToConvert.length; i++ ) {
			
			if ( _arrayToConvert[ i ].length == arrayDimension ) {
			
				for ( int j=0; j<_arrayToConvert[ i ].length; j++ ) {

					dataLoadedAsArray.append( _arrayToConvert[ i ][ j ] + "\t" );
				}

				dataLoadedAsArray.append( "\n" );
			}
			else {

                throw new TdaException( 
                        TDA.ERROR_APP_USERINPUT,
                        "(CompTopo.executeCompTopo) " +
                        "For line i=" + i + ", the supplied number of values " +
                        "is different from the expected " +
                        "number (" + arrayDimension +
                        ").\n" +
                        "Please make sure that the array you pass along is rectangular." );
			}
		}
		
		return dataLoadedAsArray.toString();
	}
	
	protected void updateSettingsList( final String _settingKey, 
			final String _settingValue ) {
		
		// convention: check if the setting is already recorded; if so,
		// then remove the corresponding value
		if ( rawSettingsList_.containsKey( _settingKey.toLowerCase() ) ) {
			
			rawSettingsList_.remove( _settingKey );
		}

		rawSettingsList_.put( _settingKey.toLowerCase(), _settingValue );
	}
		
	protected void parseStringSetting( final String _settingToParse ) throws Exception {

		String strParameterName;
		String strParameterValue;
	    StringTokenizer tokenizer;
		int tokenCount;
		
		tokenizer = 
		        new StringTokenizer( _settingToParse, "=" );
		tokenCount = tokenizer.countTokens();
		
		// 9/8/2005 hjs	1.0.3	Add a little extra flexibility to the command 
		//						line parameter parsing, by allowing the use 
		//						of empty arguments (e.g., to "cancel" an 
		//						already specified value)
		if ( tokenCount > 2 ) {
		    
		    throw new TdaException( TDA.ERROR_APP_USERINPUT,
		            "\n (Settings) Cannot recognize the input parameter '" +
		            _settingToParse +
		            "'. Please use the 'item=value' format." );
		}
		
		strParameterName = tokenizer.nextToken();

		if ( tokenCount == 2 ) {
		    
		    strParameterValue = tokenizer.nextToken();
		}
		else {
		
		    strParameterValue = "";
		}			      

		updateSettingsList( 
		        strParameterName.toLowerCase(), strParameterValue );
	}
	
	public String getParameters() {
		
		StringBuffer strAssignedData = new StringBuffer();
		String key;
		
		Set<String> keySet = rawSettingsList_.keySet();
		Iterator<String> keyIter = keySet.iterator();
		
		while ( keyIter.hasNext() ) {
		
			key = keyIter.next();

			// Only collect the parameters (not the [optional] doubles array)
			if ( !key.equalsIgnoreCase( TDA.SETTING_DATALOADEDASARRAY ) ) {
			
				strAssignedData.append( "\n" + key + "=" + rawSettingsList_.get( key ) );
			}
		}
		
		return strAssignedData.toString();
	}
	
	public String getAssignedData() {
		
		StringBuffer strAssignedData = new StringBuffer();
		String key;
		
		Set<String> keySet = rawSettingsList_.keySet();
		Iterator<String> keyIter = keySet.iterator();
		
		while ( keyIter.hasNext() ) {
		
			key = keyIter.next();
			
			// Filter out the doubles array, if it was specified:
			if ( key.equalsIgnoreCase( TDA.SETTING_DATALOADEDASARRAY ) ) {
			
				strAssignedData.append( "\n" + key + "=" + rawSettingsList_.get( key ) );
			}
		}
		
		return strAssignedData.toString();
	}
	
	public String getPassedInParameters() {
		
		return this.processData_.getPassedInParameters();
	}
	
	//
	public int assignData( final String[] _settingsToAssign, 
				final double[][] _dblArgs )
					throws Exception {
		
		String arrayAsString = convertArrayToString( _dblArgs );
		updateSettingsList( TDA.SETTING_DATALOADEDASARRAY, arrayAsString );
		
		for ( int i=0; i<_settingsToAssign.length; i++ ) {
				
			parseStringSetting( _settingsToAssign[ i ] );
		}
		
		return 1;
	}
	
	public int assignData( final double[][] _dblArgs )
					throws Exception {

		String arrayAsString = convertArrayToString( _dblArgs );
		updateSettingsList( TDA.SETTING_DATALOADEDASARRAY, arrayAsString );
		
		return 1;
	}
	
	public int assignData( final double[] _dblArgs )
					throws Exception {

		String arrayAsString = convertArrayToString( _dblArgs );
		updateSettingsList( TDA.SETTING_DATALOADEDASARRAY, arrayAsString );
		
		return 1;
	}

	public int assignData( final String _settingToAssign ) throws Exception {

		parseStringSetting( _settingToAssign );		

		return 1;
	}

	public int assignData( final String[] _settingsToAssign ) throws Exception {

		for ( int i=0; i<_settingsToAssign.length; i++ ) {
				
			parseStringSetting( _settingsToAssign[ i ] );
		}
		

		return 1;
	}

	
	protected String[] combineArgs( final String[] _args ) {
		
		String key;		
		Set<String> keySet = rawSettingsList_.keySet();
		Iterator<String> keyIter = keySet.iterator();
		StringBuffer combArgs = new StringBuffer();
		
		String[] combinedArgs = new String[ _args.length + rawSettingsList_.size() ];

		int i;
		
		for ( i=0; i<_args.length; i++ ) {
			
			combinedArgs[ i ] = _args[ i ];
			combArgs.append( "\n\t" + _args[i] );
		}

		while ( keyIter.hasNext() ) {
		
			key = keyIter.next();
			
			combinedArgs[ i ] = key + "=" + rawSettingsList_.get( key );
			combArgs.append( "\n\t" + key + "=" + rawSettingsList_.get( key ) );
			i++;
		}
		
		return combinedArgs;
	}
	

	// Wrapper for Matlab access: supply pointCloud as a 2-dimensional array
//	public static void executeCompTopo( String[] _args, double[][] _dblArgs ) 
//				throws Exception {
//	public double[][] executeTda( String[] _args, double[][] _dblArgs, String[] _apiArgs ) 
//				throws Exception {

	// Instead: standardize on int return value
	public int executeTda( String[] _args, double[][] _dblArgs, String[] _apiArgs ) 
				throws Exception {
		
		// number of columns in the passed-in array
		int arrayWidth;
		int nbrOfArgs;
		int retVal;
		
		if ( _dblArgs == null ) {

			nbrOfArgs = _args.length + _apiArgs.length;
		}
		else {

			nbrOfArgs = _args.length + _apiArgs.length + 1;
		}
		
		String[] modifiedArgs = new String[ nbrOfArgs ]; 
		
		for ( int i=0; i<_args.length; i++ ) {
			
			modifiedArgs[ i ] = _args[ i ];
		}
		for ( int i=0; i<_apiArgs.length; i++ ) {
			
			modifiedArgs[ i+_args.length ] = _apiArgs[ i ];
		}		

		if ( _dblArgs != null ) {
			
			// Record the dimension of the point cloud (i.e., the dimension of each point)
			// so we can use it later
			arrayWidth = _dblArgs[ 0 ].length;
			
			// Create the extra argument, for supplying the point cloud in string form
			StringBuffer dataLoadedAsArray = new StringBuffer( 
					TDA.SETTING_DATALOADEDASARRAY + "=" );
	
			// translate the passed-in array into a string (using tabs as white-space 
			// delimiters, and inserting crlf's) 
			for ( int i=0; i<_dblArgs.length; i++ ) {
				
				if ( _dblArgs[ i ].length == arrayWidth ) {
				
					for ( int j=0; j<_dblArgs[ i ].length; j++ ) {
	
						dataLoadedAsArray.append( _dblArgs[ i ][ j ] + "\t" );
					}
	
					dataLoadedAsArray.append( "\n" );
				}
				else {
	
					// this is the only condition that we can/should enforce on the data
	                throw new TdaException( 
	                        TDA.ERROR_APP_USERINPUT,
	                        "(CompTopo.executeCompTopo) " +
	                        "For line i=" + i + ", the supplied number of values " +
	                        "is different from the expected " +
	                        "number (" + arrayWidth +
	                        ").\n" +
	                        "Please make sure that the array you pass along is rectangular." );
				}
			}
	
			modifiedArgs[ nbrOfArgs-1 ]  = dataLoadedAsArray.toString();
		}
				
		assignData( modifiedArgs );
		retVal = executeTda();

		return retVal;
	}

	// Wrapper for Matlab access: supply pointCloud as a 2-dimensional array
//	public static void executeCompTopo( String[] _args, double[][] _dblArgs ) 
//				throws Exception {
	public int executeTda( String[] _args, double[][] _dblArgs ) 
				throws Exception {
		
		int retVal;
		
		// number of columns in the passed-in array
		int arrayWidth;
		
		String[] modifiedArgs = new String[ _args.length + 1 ]; 
		
		for ( int i=0; i<_args.length; i++ ) {
			
			modifiedArgs[ i ] = _args[ i ];
		}
		
		// Record the dimension of the point cloud (i.e., the dimension of each point)
		// so we can use it later
		arrayWidth = _dblArgs[ 0 ].length;
		
		// Create the extra argument, for supplying the point cloud in string form
		StringBuffer dataLoadedAsArray = new StringBuffer( 
				TDA.SETTING_DATALOADEDASARRAY + "=" );

		// translate the passed-in array into a string (using tabs as white-space 
		// delimiters, and inserting crlf's) 
		for ( int i=0; i<_dblArgs.length; i++ ) {
			
			if ( _dblArgs[ i ].length == arrayWidth ) {
			
				for ( int j=0; j<_dblArgs[ i ].length; j++ ) {

					dataLoadedAsArray.append( _dblArgs[ i ][ j ] + "\t" );
				}

				dataLoadedAsArray.append( "\n" );
			}
			else {

				// this is the only condition that we can/should enforce on the data
                throw new TdaException( 
                        TDA.ERROR_APP_USERINPUT,
                        "(CompTopo.executeCompTopo) " +
                        "For line i=" + i + ", the supplied number of values " +
                        "is different from the expected " +
                        "number (" + arrayWidth +
                        ").\n" +
                        "Please make sure that the array you pass along is rectangular." );
			}
		}

		modifiedArgs[ _args.length ]  = dataLoadedAsArray.toString();

		assignData( modifiedArgs );
		retVal = executeTda();

		return retVal;
	}
	
	
//	public static void executeTda( String[] _args ) throws Exception {
	public int executeTda() throws Exception {
		
		int retVal;
		TdaErrorHandler errorHandler = new TdaErrorHandler();
		

		resultsContainer_ = new ResultsContainer();
        
        try {
        	
        	
        	// Crucial: combine the passed-in args with the previously assigned
        	// (via assignData) args        	

			processData_ = new Settings();
			processData_.processCommandLine( this.rawSettingsList_ );
        	
    		errorHandler = new TdaErrorHandler( processData_ );
	    }
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
         
		try {
			
            // Setup the error handler so it knows about the (loaded) settings
            errorHandler = new TdaErrorHandler( processData_ );
			

            // Now validate the settings for our class
            validateRequiredData();
	    }
		catch ( final TdaException e ) {
		    
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
		    
		    errorHandler.handleGeneralException( e );
		}
         
		try {
			
			
			// TODO: all execute calls need to return 'int'
            	executeTaskUnits();
            	retVal = 0;
	    }
		catch ( final TdaException e ) {
		    
			retVal = -1;
		    errorHandler.handleApplicationException( e );
		}
		catch ( final Exception e ) {
			
			retVal = -2;
		    errorHandler.handleGeneralException( e );
		}

		return retVal;
 	}
	

//	public ResultsContainerI getResults() throws Exception {
	public ResultsContainerI getResults() {
		
		return resultsContainer_;
	}
	
//	protected static void executeTaskUnits() throws Exception {
	protected void executeTaskUnits() throws Exception {

        String taskChoice;
        
        taskChoice = processData_.getValidatedProcessParameter(
                TDA.SETTING_TASKCHOICE );

        // need to reset now, because any attempt to execute needs to start with
        // a fresh set of settings and possible results
    	resultsContainer_ = new ResultsContainer();
    	rawSettingsList_ = new TreeMap<String, String>(); // this is already absorbed
    	// into the processData container at this point
    			
        // ---------------------------------------
        // Set up any task object that would execute before an algorithm
        // ---------------------------------------
        if ( !taskChoice.equalsIgnoreCase( TDA.UI_TASK_SKIP ) ) {
        	
        	// Note that the task choice is validated against the list of valid
        	// choices, so we just need to make sure that every valid task is assigned
        	// one of the cases:
        	
        	if ( taskChoice.equalsIgnoreCase(
                TDA.UI_TASK_LSD ) ) {
    
//        		System.out.println( "*** task=LSD ");
        		task_ = new TaskLSD( processData_ );
        	}
        	else if ( taskChoice.equalsIgnoreCase(
                    TDA.UI_TASK_RCA0 )) {

//        		System.out.println( "*** task=RCA0 ");
        		task_ = new AlgorithmRCA0( processData_ );
            }
        	else if ( taskChoice.equalsIgnoreCase(
                    TDA.UI_TASK_M23 )) {

//        		System.out.println( "*** task=M23 ");
///// Disabled for now:
        		//task_ = new AlgorithmM23( processData_ );
            }
            else if ( taskChoice.equalsIgnoreCase( 
                    TDA.UI_TASK_M12 )) {

//        		System.out.println( "*** task=M12, processData_:\n" + 
//        				processData_.getPassedInParameters() );
            	task_ = new AlgorithmM12( processData_ );
            }
            else if ( taskChoice.equalsIgnoreCase( 
                    TDA.UI_TASK_M12ref )) {

//        		System.out.println( "*** task=M12, processData_:\n" + 
//        				processData_.getPassedInParameters() );
            	task_ = new AlgorithmM12ref( processData_ );
            }
            else if ( taskChoice.equalsIgnoreCase(
                    TDA.UI_TASK_M01 ) ) {

//        		System.out.println( "*** task=M01 ");
            	task_ = new AlgorithmM01( processData_ );
            }
        }
        else {

//    		System.out.println( "*** no task ");
        	// TODO: may want to let the user know...
        }
        
        if ( task_ == null ) {

//    		System.out.println( "[executeTaskUnits] task = null ???");
    		
            // If the task choice was valid, but the associated object
            // can't be created for some strange reason, we need to exit
            throw new TdaException( 
                    TDA.ERROR_APP_USERINPUT,
                    "(TDA.main) " +
                    "Task object turned out to be invalid (null object) after setup. " +
                    "Is the taskChoice setting specifying a valid (known) task?" );
        }
        else {
        	

//    		System.out.println( "[executeTaskUnits] task = null? -- NO ");
        }

        if ( processData_.wereThereProblems() ) {

//    		System.out.println( "[executeTaskUnits] wereThereProblems() ");
    		
	    	throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER,
	              "(Checkpoint) TDA performed a set of validation checks, " +
		          "and discovered the following issues " +
		          "which prevented further program execution:" + 
		              TDA.FEEDBACK_NEWLINE +
		              processData_.compileErrorMessages().toString() );
        }
        else {

//    		System.out.println( "[executeTaskUnits] wereThereProblems()? -- NO ");
        }
    	
		task_.executeTask();
    	resultsContainer_ = (ResultsContainerI) task_.getResultsContainer();
	}

    /**
     * Validates the settings values required for getting the main class started.
     * 
     * @return Returns the boolean flag that indicates whether a crucial setting
     * could not be validated.
     */
    private static boolean validateRequiredData() throws Exception {

        // utility variables for validating
		Set<String> validValues = new HashSet<String>();
        boolean isDataValid = true;        
        String settingNameCanonical;
        String settingNameDescriptive;
        String settingNameForDisplay;
        String settingDataType;
        SettingItem settingItem;
        int validationType;
        
        
        // Validate the task
        settingNameCanonical = TDA.SETTING_TASKCHOICE;
        settingNameDescriptive = TDA.SETTING_TASKCHOICE_DESCR;
        settingNameForDisplay = TDA.SETTING_TASKCHOICE_DISP;
        validValues.clear();
        validValues.add( TDA.UI_TASK_LSD );
        validValues.add( TDA.UI_TASK_M23 );
        validValues.add( TDA.UI_TASK_M12 );
        validValues.add( TDA.UI_TASK_M12ref );
        validValues.add( TDA.UI_TASK_RCA0 );        
//        validValues.add( TDA.UI_TASK_M01 );
        validValues.add( TDA.UI_TASK_SKIP );
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = processData_.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                validValues, 
                TDA.UI_TASK_SKIP );

        if ( !settingItem.isValidSetting() ) {
          
        	throw new TdaException( TDA.ERROR_CHECKPOINTTRIGGER,
                  "(Checkpoint) Cannot continue without a valid task choice.\n" + 
                  processData_.compileErrorMessages().toString() );
        } 
        
        return isDataValid;
    }
    
    /**
     * Generates feedback in case the task encountered a problem.
     */
    protected void handleFeedbackForProblems() throws Exception {

        StringBuffer finalCheckPointFeedback = new StringBuffer(
            TDA.BUFFERLENGTH_STAT );
        int lineLength = TDA.FEEDBACK_LINELENGTH;
        
        finalCheckPointFeedback.append( TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.
                substring( 0, lineLength ) );
        finalCheckPointFeedback.append( TDA.FEEDBACK_NEWLINE );
        finalCheckPointFeedback.append( TDA.FEEDBACKSTRING_FINALCHECK );
        finalCheckPointFeedback.append( TDA.FEEDBACK_NEWLINEPLUSDASHEDLINE.
                substring( 0, lineLength ) );

        finalCheckPointFeedback.append( TDA.FEEDBACK_NEWLINE );
        finalCheckPointFeedback.append( processData_.compileErrorMessages().toString() );
        
        finalCheckPointFeedback.append( TDA.FEEDBACK_NEWLINE );
        finalCheckPointFeedback.append( processData_.compileWarningMessages().toString() );

        Collection outputFileFlags = new HashSet();
        outputFileFlags.add( new Integer( TDA.FILE_RESULTS ) );
        processData_.writeToFile( outputFileFlags , finalCheckPointFeedback );
    }

    /**
     * @return Returns the settings.
     */
    public Settings getSettings() {
    	
    	// TODO  May want to provide a more end-user friendly format, because
    	// the raw processData contains a boat load of stuff
    	
        return processData_;
    }

	
	protected void setLastExecutedTaskReturnValue( final int retValToAssign ) {
		
		lastExecutedTaskReturnValue_ = retValToAssign;
	}
	
	public int getLastExecutedTaskReturnValue() {
		
		return lastExecutedTaskReturnValue_;
	}
	
	// Trivial wrapper for better end user experience
	public String getInfo() {
		
		return getInfo( 0 );
	}
	
	public String getInfo( final int _requestedInfoFlag ) {
		
		String retVal = "getInfo( i ) provides basic information about the " +
				"state of your " + TDA.APPLICATION_NAME +  " instance: \n" +
				"use i = \n" +
    			"        1 for summary of results, \n" +
    			"        2 for error messages (if any), \n" + 
    			"        3 for warning messages (if any).\n" + 
    			"        4 for version number and date.\n" + 
    			"        5 for complete listing of settings/configuration values.\n" +
    			"        6 for the setting values specified in the configuration file.\n" +
    			"        7 for a listing of available algorithms and tasks.\n" +
    			"\n";
		
		switch ( _requestedInfoFlag ) {

        case 0:
        	break;

        case 1:
        	retVal = "Results summary";
        	break;
        	
        case 2:
        	
        	if ( getLastExecutedTaskReturnValue() >= 0 ) {
        	
        		retVal = "No Error to report.";
        	}
        	else {
        	
        		// TODO -- can provide more?
//        		retVal = "Error info from last program execution: \n"
//        				+ "Error code = " + 
//        				getLastExecutedTaskReturnValue();
        		retVal = "Error:\n" + 
    			this.processData_.getCollectedErrors().toString();
        	}
        	break;
        	
        case 3:
        	retVal = "Warnings:\n" + 
        			this.processData_.getCollectedWarnings().toString();
        	break;
        	
        case 4:
        	retVal = TDA.APPLICATION_NAME + " " +
        				TDA.APPLICATION_VERSIONNUMBER + ", " + 
        				TDA.APPLICATION_VERSIONDATE;
        	break;
        	
        case 5:
        	// (Only) the parameters that were passed in (e.g., via the assignData methods)
        	// to the application
        	retVal = "Assigned Data:\n" + 
        			this.processData_.getPassedInParameters();
        	break;
        	
        case 51:
        	// TODO: add tda.config.txt
        	retVal = "All Settings and Configuration Parameters:\n" + 
        			this.processData_.toString();
        	break;
        	
        case 6:
        	// TODO: add tda.config.txt
        	retVal = "Configuration parameters in file '" + 
        				TDA.DEFAULT_SETTINGSFILENAME + "':";
        	break;
        	
        case 7:
        	retVal = "Available algorithms and tasks:\n" + 
    				"710 RCA1 (Rips Complex Analysis in dim. 1)\n" +
    				"711 RCA2 (Rips Complex Analysis in dim. 2)\n" +
	    				"720 LSD (Local Spherical Distance matrix)\n" +
	    				"721 LPH (LPH followed by RCA1)\n" +
        				"\n";
        	break;
        	
        case 710:
        	retVal = "RCA1 (Rips Complex Analysis in dim. 1):\n" + 
        				"  RCA1( listOfSettings, pointCloud ) -- " +
        				"String[], double[][], " + 
        				"returns success=0 or error code<0 -- int\n" + 
        				"\n";
        	break;

        	
        case 711:
        	retVal = "RCA2 (Rips Complex Analysis in dim. 2):\n" + 
        				"  RCA2( listOfSettings, pointCloud ) -- " +
        				"String[], double[][], " + 
        				"returns success=0 or error code<0 -- int\n" + 
        				"\n";
        	break;
        	
        case 720:
        	retVal = "LSD (Local Spherical Distance matrix):\n" + 
        				"  LSD( pointCloud, radius, centerPoint ) -- " +
        				"double[][], double, double[], " + 
        				"returns distanceMatrix -- double[N][N]; if error, returns array with N=1, value[1][1]<0 \n" + 
        				"\n";
        	break;
        	
        case 721:
        	retVal = "LPH (LPH followed by RCA1):\n" + 
        				"  LPH( pointCloud, radius, centerPoint ) -- " +
        				"double[][], double, double[], " + 
        				"returns success=0 or error<0 -- int\n" + 
        				"\n";
        	break;

        default:
//        	retVal = "Sorry, for the input you supplied (" + _requestedInfoFlag + 
//        				") there is no info available.";
        	break;
	    }
		
		return retVal;
	}
	
	public String toString() {
		
		// Info to the user what this object is ("think" Matlab, etc)
		String strObjDescription = new String( TDA.APPLICATION_NAME + " " +
				TDA.APPLICATION_VERSIONNUMBER + ", " + 
				TDA.APPLICATION_VERSIONDATE );
		
		// TODO:  ? add more relevant info
		
		return strObjDescription;
	}
}
