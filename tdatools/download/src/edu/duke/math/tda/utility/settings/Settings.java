/*
 * Created 2012
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */

package edu.duke.math.tda.utility.settings;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.regex.Pattern;

import edu.duke.math.tda.structures.results.ResultRCA;
import edu.duke.math.tda.utility.TDA;
import edu.duke.math.tda.utility.TdaRandomNumber;
import edu.duke.math.tda.utility.FileUtil;
import edu.duke.math.tda.utility.JZOOWildCardFilter;
import edu.duke.math.tda.utility.StringUtil;
import edu.duke.math.tda.utility.errorhandling.TdaError;
import edu.duke.math.tda.utility.errorhandling.TdaException;

/**
 * Manages the initial, validated, and dynamically updated parameters used by the program.
 *   
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class Settings {

    // Used to make sure that multiple threads do not share the same seed
    protected static final long baseRandomSeed_ = System.currentTimeMillis();
    
    // Initially supplied command line parameters
    protected String[] commandLineParameters_;
	
    // Parameters that cannot be changed
    protected final Properties initialProcessParametersAsLoaded_;
    protected final Properties initialProcessParametersLowercase_;

	protected Properties passedInParameters_;
	protected Properties loadedSettings_;
	
	// Dafault parameters for the application
	protected Properties defaultProcessParameters_;
	
	// Parameters that change during the search
	protected Properties dynamicProcessParameters_;
	
	// General purpose storage container to be used by any object that has
	// access to processData
	protected Set<Object> generalProcessDataStorage_;

    protected Set<String> registeredSettings_ = new HashSet<String>();
    protected Set<SettingItem> processedSettings_ = new HashSet<SettingItem>();
	protected Set<TdaError> collectedErrors_ = new HashSet<TdaError>();
	protected Set<TdaError> collectedWarnings_ = new HashSet<TdaError>();
	protected Properties validatedSettings_ = new Properties();
    
    // Use the settings as a wrapper around our I/O class
    protected FileUtil fileUtil_;
    
    // Access to random sequence, application-wide
    protected TdaRandomNumber randomNumber_ = new TdaRandomNumber();

    /**
     * Basic constructor that simply sets up the internal data for the settings object.
     * 
     */ 
    public Settings() throws Exception { 
                
        // Create the objects for storing the initial, validated and dynamic parameters.
        // Note: the initial parameter set is simply the loaded "raw" set of data, and 
        // should only be used with caution. The validated data set contains the same
        // info, but has basic checks applied (a numeric property should not be an
        // alphanumneric string after all).
        initialProcessParametersAsLoaded_ = new Properties();
        initialProcessParametersLowercase_ = new Properties();
        
        defaultProcessParameters_ = new Properties();
        passedInParameters_ = new Properties();
        loadedSettings_ = new Properties();
        dynamicProcessParameters_ = new Properties();

        generalProcessDataStorage_ = new HashSet<Object>();
        registeredSettings_ = new HashSet<String>();
        processedSettings_ = new HashSet<SettingItem>();
        collectedErrors_ = new HashSet<TdaError>();
        collectedWarnings_ = new HashSet<TdaError>();

        validatedSettings_ = new Properties();
    }

    /**
     * Constructor based on an existing settings. Note that this constructor does a "deep" copy.
     *
     * @param _settings The settings that we want to use as basis for the new object.
     * 
     */ 
    public Settings( Settings _settings ) throws Exception { 
        
        synchronized ( this ) {
            
            // (Need to ) Handle the "final" data (first)
            initialProcessParametersAsLoaded_ = 
                assignProperties( _settings.initialProcessParametersAsLoaded_ );
            initialProcessParametersLowercase_ = 
                assignProperties( _settings.initialProcessParametersLowercase_ );
            
            loadSettings( _settings );
            
            validateRequiredData();
            
            // Set up the file I/O
            prepareFileOutput();
        }
    }

    /**
     * Constructor based on an existing settings. Note that this constructor does a "deep" copy.
     *
     * @param _settings The settings that we want to use as basis for the new object.
     * 
     */ 
    public Settings( Settings _settings, int _threadIndex ) throws Exception { 
        
        synchronized ( this ) {
            
            // (Need to ) Handle the "final" data (first)
            initialProcessParametersAsLoaded_ = 
                assignProperties( _settings.initialProcessParametersAsLoaded_ );
            initialProcessParametersLowercase_ = 
                assignProperties( _settings.initialProcessParametersLowercase_ );
            
            loadSettings( _settings );
            
            this.setDynamicProcessParameter( 
                    TDA.DATA_THREADINDEX, Integer.toString( _threadIndex ) );
            
            validateRequiredData();
            
            // Set up the file I/O
            prepareFileOutput();
        }
    }

    /**
     * Method to superseed the constructor with command line arguments.
     * 
     * Note: Due to the expanded role of the settings class ("processData"),
     * it is really important that we have a valid settings object immediately
     * from the very start. This is not guaranteed when we use the original
     * constructor that included the processing of the command line arguments.
     * 
     * @param _applicationParameters The list of parameters provided.
     */
  public void processCommandLine( final String[] _applicationParameters ) 
          throws Exception { 
      
      // store in case we need to report on them later
      commandLineParameters_ = _applicationParameters;
      
      // applicationParameters are the runtime parameters supplied by the user
      // to the "application class" that is the wrapper for executing the core code (e.g.,
      // the commandline application, or a gui)
            
      loadSettings( _applicationParameters );

      // Validate any parameter that is part of this class
      validateRequiredData();

      // When loading settings based on application parameters we need to 
      // validate our report file name (we only want to do this once, so there
      // is no need to do it again when we base other settings instances on this
      // 'original' one
      validateReportFiles();
      
      // Set up the file I/O
      prepareFileOutput();

      // Set up access to this class for the static StringUtil class
      StringUtil.setProcessData( this );
  }
  
  // temp. wrapper
  public void processCommandLine( final SortedMap<String, String> _settingsList ) 
		  throws Exception { 
	  //convert the map to a string array
	  String[] applicationParameters = new String[ _settingsList.size() ];
	  
	  int i = 0;
	  for ( Map.Entry<String, String> entry : _settingsList.entrySet()) {
		  
		  applicationParameters[ i ] = entry.getKey() + "=" + entry.getValue();
		  i++;
	  }
  
	  processCommandLine( applicationParameters );		  
  }
  

    /** Loads the settings based on the parameters from an existing settings object.
     * 
     * @param _settings    The existing settings object to be used as basis for loading.
     */
    public synchronized void loadSettings( final Settings _settings ) 
            throws Exception {
      
      defaultProcessParameters_ = assignProperties( _settings.defaultProcessParameters_ );
      passedInParameters_ = assignProperties( _settings.passedInParameters_ );
      loadedSettings_ = assignProperties( _settings.loadedSettings_ );
      dynamicProcessParameters_ = assignProperties( _settings.dynamicProcessParameters_ );

      collectedWarnings_ = assignSet(_settings.collectedWarnings_ );
      generalProcessDataStorage_ = assignSet( _settings.generalProcessDataStorage_ );
      registeredSettings_ = assignSet( _settings.registeredSettings_ );
      processedSettings_ = assignSet( _settings.processedSettings_ );
      collectedErrors_ = assignSet( _settings.collectedErrors_ );

      validatedSettings_ = assignProperties( _settings.validatedSettings_ );
    }

    /** Loads the settings based on the (typically: commandline) parameters provided by the user.
     * 
     * @param _applicationParameters The list of parameters provided by the user to the
     * application
     */
    protected synchronized void loadSettings( final String[] _applicationParameters ) 
			throws Exception {
		
	  	// If any optional parameters were specified, load them here
	    if ( _applicationParameters != null ) {
		
	        passedInParameters_ = loadPassedInParameters( _applicationParameters );
	    }
	    
	    try {
	    	
		    // Load the parameters from the settings file 
			loadedSettings_ = loadFileBasedParameters();
	    }
	    catch ( final TdaException e ) {
		    
//			System.out.println( "[Settings.loadSettings, TdaExcep] " + e.getMessage() );
			
			
		}
		catch ( final Exception e ) {

//			System.out.println( "[Settings.loadSettings, GenExcep] " + e.getMessage() );

			
		}
		
		// Load the default parameters, allowing us to keep machine dependent
		// or user-designated "shared" parameters separate from other core parameters
		// (not tested: no support planed at this time)
//		defaultProcessParameters = loadDefaultValuesForParameters();

		// Merge the different sets of loaded parameters
		combineParameters();
	}

    /** Helper function to make a copy of a Properties object.
     * 
     * @param _propertiesToAssign The properties to use as the basis of the assignment.
     */
    protected Properties assignProperties( Properties _propertiesToAssign ) {
        
        Properties clonedProperties = new Properties();
        Set parameterSet;
        Iterator parameterIterator;
        String strNextProperty;
        String strPropertyValue;
        String strParameterName;
        String strParameterValue;

        parameterSet = _propertiesToAssign.keySet();
        parameterIterator = parameterSet.iterator();
        while ( parameterIterator.hasNext() ) {
            
            strParameterName = (String) parameterIterator.next();

            strParameterValue = 
                _propertiesToAssign.getProperty( strParameterName );
            
            clonedProperties.setProperty( 
                    strParameterName, strParameterValue );
        }
        
        return clonedProperties;
    }

    /** Helper function to make a copy of a set of objects (of types SettingItem, String,
     * or TdaError)
     * 
     * @param _setToAssign The set to assign to.
     */
    protected Set assignSet( Set _setToAssign ) {
        
        Set<Object> clonedSet = new HashSet<Object>();
        Object objToClone;
        Object clonedObj = null;

        Iterator itemIterator = _setToAssign.iterator();
        
        while ( itemIterator.hasNext() ) {
            
            objToClone = itemIterator.next();
            
            // Check what type the object is, and clone accordingly
            if ( objToClone instanceof MandatorySettingItem ) {
                
                clonedObj = 
                    new MandatorySettingItem( (MandatorySettingItem) objToClone );
            }
            else if ( objToClone instanceof OptionalSettingItem ) {
                
                clonedObj = 
                    new OptionalSettingItem( (OptionalSettingItem) objToClone );
            }
            else if ( objToClone instanceof String ) {
                
                clonedObj = 
                    new String( (String) objToClone );
            }
            else if ( objToClone instanceof TdaError ) {
                
                clonedObj = 
                    new TdaError( (TdaError) objToClone );
            }
            else {
                
                // Add a warning if we end up here
                addToWarnings( new TdaError( 
                        "(Settings.assignSet) " +
                        "Unknown object type encountered; cannot clone the object of class '" +
                        clonedObj.getClass().getName() + "'",
                        TDA.ERRORTYPE_ALERT_DEV,
                        "",
                        null ) );
                
                // default, when we don't know or care what type the object is
                clonedObj = null;
            }
            
            // Add any cloned object to our set
            if ( clonedObj != null ) clonedSet.add( clonedObj );
        }
        
        return clonedSet;
    }
	
	protected void combineParameters() throws Exception {
	    
	    String settingsFileName = new String();
		String settingsFileDirectory = new String();
	    Set parameterSet;
		Iterator parameterIterator;
		String strNextProperty;
		String strPropertyValue;
		String strParameterName;
		String strParameterValue;

	    // This "overrides" the "regular" loaded settings with the passedInParameters
	    parameterSet = passedInParameters_.keySet();
		parameterIterator = parameterSet.iterator();
		while ( parameterIterator.hasNext() ) {
		    
		    strParameterName = (String) parameterIterator.next();
		    strParameterName = strParameterName.trim();

		    strParameterValue = 
		        passedInParameters_.getProperty( strParameterName ).trim();
		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
			
		    loadedSettings_.setProperty( 
		            strParameterName, strParameterValue );
		}
	    
		dynamicProcessParameters_ = new Properties();
		generalProcessDataStorage_ = new HashSet<Object>();
			    
	    // Store name and path of settings file
		dynamicProcessParameters_.setProperty(
		        TDA.DATA_SPECIFIEDSETTINGSFILE,
		        settingsFileName );
		dynamicProcessParameters_.setProperty(
		        TDA.DATA_SPECIFIEDSETTINGSFILEDIRECTORY,
		        settingsFileDirectory );
		
        registeredSettings_ = compileRegisteredSettings();
        
		// Load the "raw" initialSettings: avoid a simple reference to loadedSettings
		// to make initialProcessParametersAsLoaded truly final
	    parameterSet = loadedSettings_.keySet();
		parameterIterator = parameterSet.iterator();
		while (parameterIterator.hasNext()) {
		    
			strNextProperty = (String) parameterIterator.next();	
			strPropertyValue = loadedSettings_.getProperty( strNextProperty ).trim();
			
			// Special cleanup: remove any substring from the end of the property value
			// after a comment symbol is encountered. Then remove white space around
			// the property value.
			strPropertyValue = StringUtil.removeTrailingComment( strPropertyValue )
					.trim();
            
            // This will add a warning message to the output if the user has deprecated
            // settings as part of the input
            checkForDeprecatedSettings( strNextProperty );
            
            // This will add a warning message if the user supplies a setting that isn't
            // "registered" to the application (we provide this as a convenience to users in case
            // of misspelling, etc)
            checkForUnregisteredSettings( strNextProperty );
            

			initialProcessParametersAsLoaded_.setProperty( 
			        strNextProperty, strPropertyValue );
			
			initialProcessParametersLowercase_.setProperty( 
			        strNextProperty.toLowerCase(), strPropertyValue );
		}

		// Now handle embedded tokens - note that we can't do this in
		// the above loop, because of interdependencies
	    parameterSet = initialProcessParametersAsLoaded_.keySet();
		parameterIterator = parameterSet.iterator();
		String updatedPropertyValue;
		
		while ( parameterIterator.hasNext() ) {
		    
			strNextProperty = (String) parameterIterator.next();	
			strPropertyValue = initialProcessParametersAsLoaded_.getProperty( 
			        strNextProperty ).trim();

			if ( strPropertyValue.indexOf( TDA.DEFAULT_TOKENINDICATOR ) >= 0 ) {

			    updatedPropertyValue = new String( parseForTokens( strPropertyValue ) );
			        
				initialProcessParametersAsLoaded_.setProperty( 
				        strNextProperty, updatedPropertyValue );
				
			    initialProcessParametersLowercase_.setProperty( 
			        strNextProperty.toLowerCase(), updatedPropertyValue );
			}
		}
	}


    /** Utility function that compiles a list of the settings that are known to be valid
     * within the current version of the application.
     */
    // Check if the supplied setting is not in use anymore, and let the user know by
    // displaying an alert
    public Set<String> compileRegisteredSettings() {

        Set<String> knownSettings = new HashSet<String>();
        
        knownSettings.add( TDA.SETTING_SUPPRESSALLOUTPUT );
        knownSettings.add( TDA.SETTING_CENTERPOINTFORLOCALNBHD );
        knownSettings.add( TDA.SETTING_SWITCHTOSPARSEAT );
        knownSettings.add( TDA.SETTING_CENTERPOINTFORLPH );
//        knownSettings.add( TDA.SETTING_USEPYTHONINWINDOWS );
        knownSettings.add( TDA.SETTING_CONVERTDATATODISTANCEMATRIXFORMAT );
        knownSettings.add( TDA.SETTING_DISTANCEMATRIXFILEOUTPUT );
        knownSettings.add( TDA.SETTING_CONVERTDATATOSPARSEMATRIXFORMAT );
        knownSettings.add( TDA.SETTING_SPARSEMATRIXFILEOUTPUT );
        knownSettings.add( TDA.SETTING_APPLICATIONMODE );
        knownSettings.add( TDA.SETTING_DATALOADEDASARRAY );
        knownSettings.add( TDA.SETTING_DISTANCEMATRIXASSTRING );
        knownSettings.add( TDA.SETTING_POINTCLOUDASSTRING );
        knownSettings.add( TDA.SETTING_POINTCLOUDSPHDISTMATRIXFILE );
        knownSettings.add( TDA.SETTING_POINTCLOUDLOCALNBHDFILE );
        knownSettings.add( TDA.SETTING_RUNNINGINECLIPSE );
        knownSettings.add( TDA.SETTING_RADIUSFORSPHERICALDISTANCE );
        knownSettings.add( TDA.SETTING_P_VALUE_FOR_LP );
        knownSettings.add( TDA.SETTING_POINTDIMENSION );
//        knownSettings.add( TDA.SETTING_COMPUTELOCALNHOOD );
//        knownSettings.add( TDA.SETTING_TIMESERIESFILE );
        knownSettings.add( TDA.SETTING_1DDIAGRAMFILE );
        knownSettings.add( TDA.SETTING_0DDIAGRAMFILE );
//        knownSettings.add( TDA.SETTING_POSTPROCESSINGCHOICE );
        knownSettings.add( TDA.SETTING_METRICCHOICE );
//        knownSettings.add( TDA.SETTING_WORKFLOWCHOICE );
        knownSettings.add( TDA.SETTING_TASKCHOICE );
//        knownSettings.add( TDA.SETTING_ALGORITHMCHOICE );
//        knownSettings.add( TDA.SETTING_POINTSPERNEIGHBORHOOD );
//        knownSettings.add( TDA.SETTING_CENTERPOINTSFORLOCALNBHDFILE );
//        knownSettings.add( TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD );
        knownSettings.add( TDA.SETTING_SUPPLYDATAAS );
        knownSettings.add( TDA.SETTING_ZP_VALUE );
        knownSettings.add( TDA.SETTING_POINTCLOUDFILE );
        knownSettings.add( TDA.SETTING_0DINTERVALSFILE );
        knownSettings.add( TDA.SETTING_1DINTERVALSFILE );
        knownSettings.add( TDA.SETTING_1DINTERVALSGENSFILE );
        knownSettings.add( TDA.SETTING_DISTANCEBOUND );
        knownSettings.add( TDA.SETTING_NUMBEROFPOINTS );
        knownSettings.add( TDA.SETTING_NUMBEROFENTITIES );
        knownSettings.add( TDA.SETTING_ENTITYTYPE );
        knownSettings.add( TDA.SETTING_HORIZONTALOFFSET );
        
//        knownSettings.add( TDA.SETTING_TIMESERIES );
//        knownSettings.add( TDA.SETTING_TIMESERIESPOINTCOUNT );
        knownSettings.add( TDA.SETTING_POINTCLOUDDIMENSION );
        
//        knownSettings.add( TDA.SETTING_ASKTOVERIFYSETTINGS );
        knownSettings.add( TDA.SETTING_CMDARG_SETTINGSDIRECTORY );
        knownSettings.add( TDA.SETTING_CMDARG_SETTINGSFILENAME );
        knownSettings.add( TDA.SETTING_DATASET );
        knownSettings.add( TDA.SETTING_DISPLAYDEBUGINFO );
        knownSettings.add( TDA.SETTING_DISPLAYMEMORYINFO );
        knownSettings.add( TDA.SETTING_DISPLAYSTATISTICS );
        knownSettings.add( TDA.SETTING_DOTFILEEXTENSION );
        knownSettings.add( TDA.SETTING_DOTGRAPHICSFORMAT );
        knownSettings.add( TDA.SETTING_ERRORDIRECTORY );
        knownSettings.add( TDA.SETTING_ERRORFILE );
        knownSettings.add( TDA.SETTING_FILEREPORTINGINTERVAL );
        knownSettings.add( TDA.SETTING_HTMLFILEEXTENSION );
        knownSettings.add( TDA.SETTING_INPUTDIRECTORY );
        knownSettings.add( TDA.SETTING_NOTES );
        knownSettings.add( TDA.SETTING_OUTPUTDIRECTORY );
        knownSettings.add( TDA.SETTING_PROJECT );
        knownSettings.add( TDA.SETTING_REPORTFILE );
        knownSettings.add( TDA.SETTING_SCREENREPORTINGINTERVAL );
        knownSettings.add( TDA.SETTING_STATISTICSCHOICE );
        knownSettings.add( TDA.SETTING_SUMMARYFILE );
        knownSettings.add( TDA.SETTING_TIMESTAMPSTRINGFORFILES );
        knownSettings.add( TDA.SETTING_TRACKINGFILE );
        knownSettings.add( TDA.SETTING_USER );
        knownSettings.add( TDA.SETTING_THREADS );
        knownSettings.add( TDA.SETTING_FILENAMEPREFIXFORTHREADS );
        knownSettings.add( TDA.SETTING_XMLINPUTFILES );
        knownSettings.add( TDA.SETTING_XMLINPUTDIRECTORY );
        knownSettings.add( TDA.SETTING_XMLOUTPUTDIRECTORY );
        knownSettings.add( TDA.SETTING_XMLREPORTFILE );
        knownSettings.add( TDA.SETTING_XMLSETTINGSTOEXPORT );
        knownSettings.add( TDA.SETTING_APPLICATIONSEED );
        
        return knownSettings;
    }
    
    // Check if the supplied setting is not in use anymore, and let the user know by
    // displaying an alert
    protected void checkForUnregisteredSettings( final String _settingName ) {

        String itemToFind;
        Iterator<String> settingItemIterator = registeredSettings_.iterator();
        while ( settingItemIterator.hasNext() ) {
            
            itemToFind = settingItemIterator.next();
            if ( itemToFind.equalsIgnoreCase( _settingName ) ) {
                                
                return;
            }
        }
        
        addToWarnings( new TdaError( 
            "The setting '" + _settingName +
                "' is not known as a valid setting in " +
                TDA.APPLICATION_NAME + " " + TDA.APPLICATION_VERSIONNUMBER + ".",
            TDA.ERRORTYPE_ALERT_UNKNOWNSETTING,
            _settingName,
            null ) );
    }
    
    // Check if the supplied setting is not in use anymore, and let the user know by
    // displaying an alert
    protected void checkForDeprecatedSettings( final String _settingName ) {
        
        // taken care of
    }
	
	// Parses the supplied string for embedded tokens (e.g., a time stamp token. Note:
	// Currently we only have the need for parsing time stamps)
	protected String parseForTokens( final String _stringToParse ) {
	    
	    String processedString = new String( _stringToParse );
	    String tokenToProcess;
        String defaultToken;
        String valueToInsert;

	    // Parse for embedded time stamps
	    // ------------------------------
	    	    
	    Set<String> tokenSet = new HashSet<String>();
		
	    // Allow multiple, equivalent tokens for the time stamp
	    tokenSet.add( TDA.DATA_TIMESTAMP_TOKEN );
	    tokenSet.add( TDA.DATA_TIMESTAMP_TOKEN_ALT0 );
	    tokenSet.add( TDA.DATA_TIMESTAMP_TOKEN_ALT1 );
        tokenSet.add( TDA.DATA_TIMESTAMP_TOKEN_ALT2 );
        tokenSet.add( TDA.DATA_TIMESTAMP_TOKEN_ALT3 );
	    
	    Iterator<String> tokenIterator = tokenSet.iterator();
	    while ( tokenIterator.hasNext() ) {
	        
	        tokenToProcess = tokenIterator.next();
	        
		    if ( _stringToParse.indexOf( tokenToProcess ) >= 0 ) {
				
                // The timeStampFormat won't be validated at this point, so get it
                // directly from the input
			    String tsf = this.getInitialProcessParameterLowercase( 
			            TDA.SETTING_TIMESTAMPSTRINGFORFILES.toLowerCase() );
                
                if ( tsf == null || tsf == "" ) tsf = TDA.DEFAULT_TIMESTAMPSTRINGFORFILES;
			    
		        processedString = processedString.replaceAll( tokenToProcess,
		            StringUtil.timeStamp( this, 
	                        TDA.SETTING_TIMESTAMPSTRINGFORFILES, tsf, 
	                        TDA.DEFAULT_TIMESTAMPSTRINGFORFILES ) );
		    }
	    }
        
        // generic processing of other tokens
        tokenSet = new HashSet<String>();
        defaultToken = TDA.DATA_THREADID_TOKEN;
        tokenSet.add( defaultToken );
        tokenSet.add( TDA.DATA_THREADID_TOKEN_ALT0 );
        
        tokenIterator = tokenSet.iterator();
        while ( tokenIterator.hasNext() ) {
            
            tokenToProcess = tokenIterator.next();
            
            int indexOfSubstring = _stringToParse.toLowerCase().indexOf( tokenToProcess.toLowerCase() ); 
            if ( indexOfSubstring >= 0 ) {
                                
                // we can't use the standard replaceAll function, because it can't ignore the case,
                // and we don't want to modify the letters' case in the source string 
                String part1 = processedString.substring( 0 , indexOfSubstring );
                String part2 = processedString.substring( indexOfSubstring + tokenToProcess.length() );
                // Note that for this token we don't insert the value, because we have a special
                // function that applies a rule
                processedString = part1 + defaultToken + part2;
            }
        }
        
	    return processedString;
	}

    // Parses the supplied string for embedded tokens (e.g., a time stamp token. Note:
    // Currently we mainly have the need for parsing time stamps)
    public String parseForTokensAfterValidation( final String _stringToParse ) {
        
        String processedString = new String( _stringToParse );
        String tokenToProcess;
        String defaultToken;
        String valueToInsert;
                
        Set<String> tokenSet;
        Iterator<String> tokenIterator;
        
        // generic processing of various "general" tokens
        tokenSet = new HashSet<String>();
        tokenSet.add( TDA.DATA_DISTANCE_TOKEN );
        tokenSet.add( TDA.DATA_DISTANCE_TOKEN_ALT0 );
        valueToInsert = this.getValidatedProcessParameter( TDA.SETTING_DISTANCEBOUND );
        
        tokenIterator = tokenSet.iterator();
        while ( tokenIterator.hasNext() ) {
            
            tokenToProcess = tokenIterator.next();
            
            int indexOfSubstring = processedString.toLowerCase().indexOf( tokenToProcess.toLowerCase() ); 
            if ( indexOfSubstring >= 0 ) {
                                
                // we can't use the standard replaceAll function, because it can't ignore the case,
                // and we don't want to modify the letters' case in the source string 
                String part1 = processedString.substring( 0 , indexOfSubstring );
                String part2 = processedString.substring( indexOfSubstring + tokenToProcess.length() );
                processedString = part1 + valueToInsert + part2;
            }
        }

        tokenSet = new HashSet<String>();
        tokenSet.add( TDA.DATA_POINTCLOUDFILE_TOKEN );
        tokenSet.add( TDA.DATA_POINTCLOUDFILE_TOKEN_ALT0 );
        valueToInsert = this.getValidatedProcessParameter( TDA.SETTING_POINTCLOUDFILE );
        
        // Need to make sure the user didn't include "\" or "/" in the name of the file
        if ( valueToInsert.contains( "/" ) ) {
            
        	valueToInsert = valueToInsert.replaceAll( "/", "_" );
    	}
        if ( valueToInsert.contains( "\\" ) ) {
            
        	valueToInsert = valueToInsert.replaceAll( "\\", "_" );
    	}
        
        tokenIterator = tokenSet.iterator();
        while ( tokenIterator.hasNext() ) {
            
            tokenToProcess = tokenIterator.next();
            
            int indexOfSubstring = processedString.toLowerCase().indexOf( tokenToProcess.toLowerCase() ); 
            if ( indexOfSubstring >= 0 ) {
                                
                // we can't use the standard replaceAll function, because it can't ignore the case,
                // and we don't want to modify the letters' case in the source string 
                String part1 = processedString.substring( 0 , indexOfSubstring );
                String part2 = processedString.substring( indexOfSubstring + tokenToProcess.length() );
                processedString = part1 + valueToInsert + part2;
            }
        }

        tokenSet = new HashSet<String>();
        tokenSet.add( TDA.DATA_RADIUSBLN_TOKEN );
        tokenSet.add( TDA.DATA_RADIUSBLN_TOKEN_ALT0 );
        valueToInsert = this.getValidatedProcessParameter( TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD );
        
        tokenIterator = tokenSet.iterator();
        while ( tokenIterator.hasNext() ) {
            
            tokenToProcess = tokenIterator.next();
            
            int indexOfSubstring = processedString.toLowerCase().indexOf( tokenToProcess.toLowerCase() ); 
            if ( indexOfSubstring >= 0 ) {
                                
                // we can't use the standard replaceAll function, because it can't ignore the case,
                // and we don't want to modify the letters' case in the source string 
                String part1 = processedString.substring( 0 , indexOfSubstring );
                String part2 = processedString.substring( indexOfSubstring + tokenToProcess.length() );
                processedString = part1 + valueToInsert + part2;
            }
        }

        tokenSet = new HashSet<String>();
        tokenSet.add( TDA.DATA_DISTANCE_TOKEN );
        tokenSet.add( TDA.DATA_DISTANCE_TOKEN_ALT0 );
        valueToInsert = this.getValidatedProcessParameter( TDA.SETTING_DISTANCEBOUND );
        
        tokenIterator = tokenSet.iterator();
        while ( tokenIterator.hasNext() ) {
            
            tokenToProcess = tokenIterator.next();
            
            int indexOfSubstring = processedString.toLowerCase().indexOf( tokenToProcess.toLowerCase() ); 
            if ( indexOfSubstring >= 0 ) {
                                
                // we can't use the standard replaceAll function, because it can't ignore the case,
                // and we don't want to modify the letters' case in the source string 
                String part1 = processedString.substring( 0 , indexOfSubstring );
                String part2 = processedString.substring( indexOfSubstring + tokenToProcess.length() );
                processedString = part1 + valueToInsert + part2;
            }
        }
        
        return processedString;
    }
		
	protected Properties loadFileBasedParameters() throws Exception {

	    Properties loadedParameters = new Properties();
	    
	    String settingsFileName = new String();
		String settingsFileDirectory = new String();
	
		settingsFileName = passedInParameters_.getProperty( 
		        TDA.SETTING_CMDARG_SETTINGSFILENAME.toLowerCase() );	
		
	    if ( settingsFileName == null || settingsFileName.length() < 1 ) {
	        
	        settingsFileName = TDA.DEFAULT_SETTINGSFILENAME;
            
            // Create a meaningful error message to the user
            StringBuffer errorMessage = new StringBuffer(
                    "No settings file ('settingsFile' parameter) " +
                    "supplied via command line argument?!" );

            errorMessage.append( TDA.FEEDBACK_NEWLINE );
            errorMessage.append( TDA.FEEDBACK_NEWLINE );
            errorMessage.append( "Supplied command line arguments:" );
            for ( int i=0; i < commandLineParameters_.length; i++ ) {

                errorMessage.append( TDA.FEEDBACK_NEWLINE );
                errorMessage.append( TDA.FEEDBACK_QUOTES );
                errorMessage.append( commandLineParameters_[i].toString() );
                errorMessage.append( TDA.FEEDBACK_QUOTES );
            }
            
            this.addToWarnings( new TdaError( 
                    errorMessage.toString(),
                    TDA.ERRORTYPE_ALERT_UNKNOWNSETTING,
                    TDA.SETTING_CMDARG_SETTINGSFILENAME,
                    null ) );
            
            this.addToWarnings( new TdaError( 
                    "Default value ('" + TDA.DEFAULT_SETTINGSFILENAME +
                    "') applied to 'settingsFile' parameter.",
                    TDA.ERRORTYPE_ALERT_DEFAULTAPPLIED,
                    TDA.SETTING_CMDARG_SETTINGSFILENAME,
                    null ) );
	    }
	    
	    settingsFileDirectory = passedInParameters_.getProperty( 
		        TDA.SETTING_CMDARG_SETTINGSDIRECTORY.toLowerCase() );
	    
		if ( settingsFileDirectory == null || settingsFileDirectory.length() < 1 ) {
		    
		    settingsFileDirectory = "";
	    }
        
	    // Now load the settings. The file needs to adhere to Java's convention of
	    // listing properties and their values.
        if ( fileUtil_ == null ) fileUtil_ = new FileUtil( this );
        loadedParameters = fileUtil_.loadSettings( 
                settingsFileDirectory, settingsFileName );
	    
	    return loadedParameters;
	}
	
	protected Properties loadPassedInParameters ( final String[] _applicationParameters ) 
		throws Exception {
	    
	    Properties loadedParameters = new Properties();
		String applicationParameter;
		String strParameterName;
		String strParameterValue;
	    StringTokenizer tokenizer;
		int tokenCount;
		
		for ( int i=_applicationParameters.length-1; i >= 0; i-- ) {

		    applicationParameter = _applicationParameters[i].trim();
		    
		    if ( applicationParameter.indexOf( TDA.DEFAULT_ITEMVALUESEPARATOR ) < 0 ) {
		        
		        // It seems to make no sense to let a parameter be specified without
		        // a value (even an empty one), so tell the user:
		        throw new TdaException( TDA.ERROR_APP_USERINPUT,
			            "\n  (Settings) Cannot recognize the input parameter '" + 
			            applicationParameter + 
			            "'. Please use the 'item=value' format." );
		    }
	    
			tokenizer = 
		        new StringTokenizer( applicationParameter, "=" );
			tokenCount = tokenizer.countTokens();
			
			// 9/8/2005 hjs	1.0.3	Add a little extra flexibility to the command 
			//						line parameter parsing, by allowing the use 
			//						of empty arguments (e.g., to "cancel" an 
			//						already specified value)
			if ( tokenCount > 2 ) {
			    
			    throw new TdaException( TDA.ERROR_APP_USERINPUT,
			            "\n (Settings) Cannot recognize the input parameter '" +
			            applicationParameter +
			            "'. Please use the 'item=value' format." );
			}
			
			strParameterName = tokenizer.nextToken();

			if ( tokenCount == 2 ) {
			    
			    strParameterValue = tokenizer.nextToken();
			}
			else {
			
			    strParameterValue = "";
			}			      

			loadedParameters.setProperty( 
			        strParameterName.toLowerCase(), strParameterValue );
		}
		
		return loadedParameters;
	}
    
    protected boolean validateReportFiles()throws Exception {

        boolean isDataValid = true;

        String settingNameCanonical;
        String settingNameDescriptive;
        String settingNameForDisplay;
        String settingDataType;
        SettingItem settingItem;
        Set<String> validValues = new HashSet<String>();
        int validationType;

        
        settingNameCanonical = TDA.SETTING_REPORTFILE;
        settingNameDescriptive = TDA.SETTING_REPORTFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_REPORTFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_MANDATORY;
        settingItem = processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.DEFAULT_REPORTFILE );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the report file." );
        }
        else {
            
            this.setDynamicProcessParameter( TDA.DATA_REPORTFILE, 
                    this.getValidatedProcessParameter( TDA.SETTING_REPORTFILE ));
        }
        
        settingNameCanonical = TDA.SETTING_XMLREPORTFILE;
        settingNameDescriptive = TDA.SETTING_XMLREPORTFILE_DESCR;
        settingNameForDisplay = TDA.SETTING_XMLREPORTFILE_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                null );
        
        if ( !settingItem.isValidSetting() ) {
            
            throw new TdaException( TDA.ERROR_APP_USERINPUT, 
                    "(Settings.validateRequiredData) " +
                    "Need to supply a valid name for the XML report file." );
        }
        else {
            
            this.setDynamicProcessParameter( TDA.DATA_XMLREPORTFILE, 
                    this.getValidatedProcessParameter( TDA.SETTING_XMLREPORTFILE ));
        }

        return isDataValid;
    }
		
	public boolean validateRequiredData() throws Exception {

	    boolean isDataValid = true;

	    String settingNameCanonical;
	    String settingNameDescriptive;
	    String settingNameForDisplay;
        String settingDataType;
		SettingItem settingItem;
		Set<String> validValues = new HashSet<String>();
		int validationType;
        String strCondition;
        final int maxItemsUsed = 4;
        double[] dblValue = new double[maxItemsUsed];
        SettingItem[] arrSettingItem = new SettingItem[maxItemsUsed];

        // Note: we use the following convention for seeding the individual threads:
        // - first we get our "base" seed, which can be either user supplied or based on
        // a rule (currently we use a call system time)
        // - then thread 1 (i.e., internal index 0) is assigned this seed, and all other
        // threads are assigned seeds by adding their thread index to the base
        // - the controller (which is not a search thread per se) gets assigned the (base
        // seed -1) [although currently we don't make use of this value].
        synchronized( getClass() ) {
            
            String strThreadID = getDynamicProcessParameter( TDA.DATA_THREADINDEX );
            int threadID = -1;
            long randomSeed;
            
            // Note: the controller doesn't have a threadID, so make sure we get through here
            if ( strThreadID != null ) {
                
                try {

                    // Get the thread's ID
                    threadID = Integer.parseInt( strThreadID );
                }
                catch ( Exception e ) {
                    
                    // use -1 by convention for the "thread controller" (i.e., main app
                    // with its own settings)
                    threadID = -1;
                }
            }
            
            
            
            // Validate the data that we want to use in the token replacement
            
            
            

            // Validate the 'radius'
            settingNameCanonical = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD;
            settingNameDescriptive = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD_DESCR;
            settingNameForDisplay = TDA.SETTING_RADIUSFORLOCALNEIGHBORHOOD_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_DOUBLE;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            arrSettingItem[0] = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    null,
                    Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
            


            settingNameCanonical = TDA.SETTING_DATALOADEDASARRAY;
            settingNameDescriptive = TDA.SETTING_DATALOADEDASARRAY_DESCR;
            settingNameForDisplay = TDA.SETTING_DATALOADEDASARRAY_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    null );

            settingNameCanonical = TDA.SETTING_DISTANCEMATRIXASSTRING;
            settingNameDescriptive = TDA.SETTING_DISTANCEMATRIXASSTRING_DESCR;
            settingNameForDisplay = TDA.SETTING_DISTANCEMATRIXASSTRING_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    null );
            
            settingNameCanonical = TDA.SETTING_POINTCLOUDASSTRING;
            settingNameDescriptive = TDA.SETTING_POINTCLOUDASSTRING_DESCR;
            settingNameForDisplay = TDA.SETTING_POINTCLOUDASSTRING_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    null );

            settingNameCanonical = TDA.SETTING_POINTCLOUDFILE;
            settingNameDescriptive = TDA.SETTING_POINTCLOUDFILE_DESCR;
            settingNameForDisplay = TDA.SETTING_POINTCLOUDFILE_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    TDA.DEFAULT_POINTCLOUDFILE ); 

            settingNameCanonical = TDA.SETTING_CONVERTDATATOSPARSEMATRIXFORMAT;
            settingNameDescriptive = TDA.SETTING_CONVERTDATATOSPARSEMATRIXFORMAT_DESCR;
            settingNameForDisplay = TDA.SETTING_CONVERTDATATOSPARSEMATRIXFORMAT_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    TDA.UI_CONVERTDATATOSPARSEMATRIXFORMAT_DEFAULT ); 

            settingNameCanonical = TDA.SETTING_SPARSEMATRIXFILEOUTPUT;
            settingNameDescriptive = TDA.SETTING_SPARSEMATRIXFILEOUTPUT_DESCR;
            settingNameForDisplay = TDA.SETTING_SPARSEMATRIXFILEOUTPUT_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_STRING;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    TDA.APP_FREEFORMINPUT, 
                    TDA.DEFAULT_SPARSEMATRIXFILEOUTPUT );   

            
            // Validate the 'distance bound'
            settingNameCanonical = TDA.SETTING_DISTANCEBOUND;
            settingNameDescriptive = TDA.SETTING_DISTANCEBOUND_DESCR;
            settingNameForDisplay = TDA.SETTING_DISTANCEBOUND_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_DOUBLE;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            arrSettingItem[0] = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    null,
                    Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
            
            
            
            
            
            
                
            // Validate the 'seed'
            settingNameCanonical = TDA.SETTING_APPLICATIONSEED;
            settingNameDescriptive = TDA.SETTING_APPLICATIONSEED_DESCR;
            settingNameForDisplay = TDA.SETTING_APPLICATIONSEED_DISP;
            settingDataType = TDA.VALIDATION_DATATYPE_LONG;
            validationType = TDA.VALIDATIONTYPE_OPTIONAL;
            settingItem = this.processSetting( settingNameCanonical, 
                    settingNameDescriptive,
                    settingNameForDisplay,
                    settingDataType,
                    validationType,
                    null,
                    Long.toString( baseRandomSeed_ ) );
    
            String strSeed;
            if ( settingItem.isValidSetting ) {
            
                strSeed = getValidatedProcessParameter( TDA.SETTING_APPLICATIONSEED );
                randomSeed = Long.parseLong( strSeed );
            }
            else {
                
                randomSeed = baseRandomSeed_;
            }

            // Compute the seed for the current thread from the base seed and the threadID
            randomSeed += threadID;
            this.setRandomSeed( randomSeed );
            this.setDynamicProcessParameter( TDA.DATA_APPLICATIONSEED, Long.toString( randomSeed ) );
        }
        
	    // TODO: Need to validate the next 2 settings as valid directories.
        // Right now the error messages will refer only to files that cannot
        // be found in those directories
	    
	    // Validate the 'Input Directory'
	    settingNameCanonical = TDA.SETTING_INPUTDIRECTORY;
	    settingNameDescriptive = TDA.SETTING_INPUTDIRECTORY_DESCR;
	    settingNameForDisplay = TDA.SETTING_INPUTDIRECTORY_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    settingItem = this.processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            TDA.APP_FREEFORMINPUT, null );
        
        // Validate the 'Output Directory'
        settingNameCanonical = TDA.SETTING_OUTPUTDIRECTORY;
        settingNameDescriptive = TDA.SETTING_OUTPUTDIRECTORY_DESCR;
        settingNameForDisplay = TDA.SETTING_OUTPUTDIRECTORY_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, null );
        
        // Validate the 'XML input files'
        settingNameCanonical = TDA.SETTING_XMLINPUTFILES;
        settingNameDescriptive = TDA.SETTING_XMLINPUTFILES_DESCR;
        settingNameForDisplay = TDA.SETTING_XMLINPUTFILES_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, null );
        
        // Validate the 'XML Input Directory'
        settingNameCanonical = TDA.SETTING_XMLINPUTDIRECTORY;
        settingNameDescriptive = TDA.SETTING_XMLINPUTDIRECTORY_DESCR;
        settingNameForDisplay = TDA.SETTING_XMLINPUTDIRECTORY_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, null );
        
        // Validate the 'XML Output Directory' (default to the regular output
        // directory when not specified)
        settingNameCanonical = TDA.SETTING_XMLOUTPUTDIRECTORY;
        settingNameDescriptive = TDA.SETTING_XMLOUTPUTDIRECTORY_DESCR;
        settingNameForDisplay = TDA.SETTING_XMLOUTPUTDIRECTORY_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                getValidatedProcessParameter( TDA.SETTING_OUTPUTDIRECTORY ) );
        
        // Validate the 'XML settings to export'
        settingNameCanonical = TDA.SETTING_XMLSETTINGSTOEXPORT;
        settingNameDescriptive = TDA.SETTING_XMLSETTINGSTOEXPORT_DESCR;
        settingNameForDisplay = TDA.SETTING_XMLSETTINGSTOEXPORT_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, null );
        
        
        // Validate the 'prefix for file names when threads are used'
        settingNameCanonical = TDA.SETTING_FILENAMEPREFIXFORTHREADS;
        settingNameDescriptive = TDA.SETTING_FILENAMEPREFIXFORTHREADS_DESCR;
        settingNameForDisplay = TDA.SETTING_FILENAMEPREFIXFORTHREADS_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_STRING;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                TDA.APP_FREEFORMINPUT, 
                TDA.SETTING_FILENAMEPREFIXFORTHREADS_DEFAULT );


        
	    // Validate 'suppress all output'
	    settingNameCanonical = TDA.SETTING_SUPPRESSALLOUTPUT;
	    settingNameDescriptive = TDA.SETTING_SUPPRESSALLOUTPUT_DESCR;
	    settingNameForDisplay = TDA.SETTING_SUPPRESSALLOUTPUT_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    validValues.add( TDA.UI_SUPPRESSALLOUTPUT_YES );
	    validValues.add( TDA.UI_SUPPRESSALLOUTPUT_NO );
	    settingItem = processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validValues, 
	            TDA.UI_SUPPRESSALLOUTPUT_DEFAULT );
	    
	    // Validate the internal 'running in Eclipse'
	    settingNameCanonical = TDA.SETTING_RUNNINGINECLIPSE;
	    settingNameDescriptive = TDA.SETTING_RUNNINGINECLIPSE_DESCR;
	    settingNameForDisplay = TDA.SETTING_RUNNINGINECLIPSE_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    validValues.add( TDA.UI_RUNNINGINECLIPSE_YES );
	    validValues.add( TDA.UI_RUNNINGINECLIPSE_NO );
	    settingItem = processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validValues, 
	            TDA.UI_RUNNINGINECLIPSE_DEFAULT );

	    // Validate the applicationMode
	    settingNameCanonical = TDA.SETTING_APPLICATIONMODE;
	    settingNameDescriptive = TDA.SETTING_APPLICATIONMODE_DESCR;
	    settingNameForDisplay = TDA.SETTING_APPLICATIONMODE_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    validValues.add( TDA.UI_APPLICATIONMODE_API );
	    validValues.add( TDA.UI_APPLICATIONMODE_STANDALONE );
	    settingItem = processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validValues, 
	            TDA.UI_APPLICATIONMODE_DEFAULT );
	    
	    
	    
	    // Validate the displayStatistics flag
	    // (This is not yet used: may want to use in Recorder classes?)
	    settingNameCanonical = TDA.SETTING_DISPLAYSTATISTICS;
	    settingNameDescriptive = TDA.SETTING_DISPLAYSTATISTICS_DESCR;
	    settingNameForDisplay = TDA.SETTING_DISPLAYSTATISTICS_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    validValues.add( TDA.UI_DISPLAYSTATISTICS_YES );
	    validValues.add( TDA.UI_DISPLAYSTATISTICS_NO );
	    settingItem = processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validValues, 
	            TDA.DEFAULT_DISPLAYSTATISTICS );

	    // Validate the 'display memory info' flag
	    settingNameCanonical = TDA.SETTING_DISPLAYMEMORYINFO;
	    settingNameDescriptive = TDA.SETTING_DISPLAYMEMORYINFO_DESCR;
	    settingNameForDisplay = TDA.SETTING_DISPLAYMEMORYINFO_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    validValues.add( TDA.UI_DISPLAYMEMORYINFO_YES );
	    validValues.add( TDA.UI_DISPLAYMEMORYINFO_NO );
	    settingItem = processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            validValues, 
	            TDA.DEFAULT_DISPLAYMEMORYINFO );
        

        // Validate the 'Threads Count'
        settingNameCanonical = TDA.SETTING_THREADS;
        settingNameDescriptive = TDA.SETTING_THREADS_DESCR;
        settingNameForDisplay = TDA.SETTING_THREADS_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_INTEGER;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        settingItem = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                null,
                Integer.toString( TDA.SETTING_DEFAULT_THREADS ) );
        
        setDynamicProcessParameter( TDA.DATA_MAXTHREADS , 
                getValidatedProcessParameter( settingNameCanonical ) );
        
        if ( settingItem.isValidSetting() ) {

            try {

                strCondition = new String( "greater than 0" );
                dblValue[0] = Double.parseDouble( 
                        this.getValidatedProcessParameter(
                                settingNameCanonical ));
                if ( dblValue[0] <= 0 ) {
                    
                    this.addToErrors( new TdaError( 
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
        
        
        /////////////////

	    // Validate the string for the time series
	    settingNameCanonical = TDA.SETTING_TIMESERIES;
	    settingNameDescriptive = TDA.SETTING_TIMESERIES_DESCR;
	    settingNameForDisplay = TDA.SETTING_TIMESERIES_DISP;
	    settingDataType = TDA.VALIDATION_DATATYPE_STRING;
	    validationType = TDA.VALIDATIONTYPE_OPTIONAL;
	    settingItem = this.processSetting( settingNameCanonical, 
	            settingNameDescriptive,
	            settingNameForDisplay,
	            settingDataType,
	            validationType,
	            TDA.APP_FREEFORMINPUT, null );
	    
	    
        // Validate the 'time series point count'
        settingNameCanonical = TDA.SETTING_TIMESERIESPOINTCOUNT;
        settingNameDescriptive = TDA.SETTING_TIMESERIESPOINTCOUNT_DESCR;
        settingNameForDisplay = TDA.SETTING_TIMESERIESPOINTCOUNT_DISP;
        settingDataType = TDA.VALIDATION_DATATYPE_INTEGER;
        validationType = TDA.VALIDATIONTYPE_OPTIONAL;
        arrSettingItem[0] = this.processSetting( settingNameCanonical, 
                settingNameDescriptive,
                settingNameForDisplay,
                settingDataType,
                validationType,
                null,
                Integer.toString( TDA.APP_NOVALUESUPPLIED_NUMBER ) );
                
	    return isDataValid;
	}
    
	public StringBuffer compileErrorMessages() {

	    StringBuffer dataTypeErrors =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    StringBuffer missingValueErrors =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    StringBuffer rulesViolationErrors =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    StringBuffer invalidRangeErrors =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    StringBuffer otherErrors =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    StringBuffer errorMessages =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    TdaError errorItem;
	    
		Iterator errorsIterator = collectedErrors_.iterator();
		while ( errorsIterator.hasNext() ) {
		    errorItem = ( TdaError ) errorsIterator.next();
		    
		    switch ( errorItem.getErrorType() )
		    {
		    case TDA.ERRORTYPE_MISSINGVALUE:
	    	    
		        missingValueErrors.append( "(" + 
		                TDA.ERRORDESCRIPTION_MISSINGVALUE + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	    	    break;

		    case TDA.ERRORTYPE_MISMATCHEDDATATYPE:
	    	    
		        dataTypeErrors.append( "(" +
		        		TDA.ERRORDESCRIPTION_MISMATCHEDDATATYPE + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	    	    break;

		    case TDA.ERRORTYPE_INVALIDRANGE:
	    	    
		        invalidRangeErrors.append( "(" +
		        		TDA.ERRORDESCRIPTION_INVALIDRANGE + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	    	    break;

            case TDA.ERRORTYPE_INVALIDCHOICE:
                
                invalidRangeErrors.append( "(" +
                        TDA.ERRORDESCRIPTION_INVALIDCHOICE + ") " +
                        errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
                break;

            case TDA.ERRORTYPE_INVALIDPATH:
                
                invalidRangeErrors.append( "(" +
                        TDA.ERRORDESCRIPTION_INVALIDPATH + ") " +
                        errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
                break;

		    case TDA.ERRORTYPE_RULEVIOLATION:
	    	    
		        rulesViolationErrors.append( "(" +
		        		TDA.ERRORDESCRIPTION_RULEVIOLATION + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	    	    break;

		    case TDA.ERRORTYPE_DOTINTERRUPTION:
	    	    
		        rulesViolationErrors.append( "(" +
		        		TDA.ERRORDESCRIPTION_DOTINTERRUPTION + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	    	    break;

            case TDA.ERRORTYPE_DOTEXECUTION:
                
                rulesViolationErrors.append( "(" +
                        TDA.ERRORDESCRIPTION_DOTEXECUTION + ") " +
                        errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
                break;

            case TDA.ERRORTYPE_POSTPROCESSING:
                
                rulesViolationErrors.append( "(" +
                        TDA.ERRORDESCRIPTION_POSTPROCESSING + ") " +
                        errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
                break;
	    	    
		    default: 
	    	    
		        otherErrors.append( "(" +
		        		TDA.ERRORDESCRIPTION_OTHER + ") " +
		                errorItem.getErrorMessageText() + TDA.FEEDBACK_NEWLINE );
	
		       	break;
		    }
		}
		
		errorMessages.append( dataTypeErrors );
		errorMessages.append( missingValueErrors );
		errorMessages.append( invalidRangeErrors );
		errorMessages.append( rulesViolationErrors );
		errorMessages.append( otherErrors );
				    
		return errorMessages;
	}
	
	public StringBuffer compileWarningMessages() {

	    StringBuffer warnings =  
	        new StringBuffer( TDA.BUFFERLENGTH_STAT );
	    
	    TdaError warningItem;
	    
		Iterator errorsIterator = collectedWarnings_.iterator();
		while ( errorsIterator.hasNext() ) {
		    warningItem = ( TdaError ) errorsIterator.next();
		    
		    switch ( warningItem.getErrorType() )
		    {
            case TDA.ERRORTYPE_ALERT_CORRECTEDCHOICE:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_ALERT_CORRECTEDCHOICE + ") " +
                        warningItem.getErrorMessageText() );
                break;

		    case TDA.ERRORTYPE_ALERT_OTHER:
	    	    
		        warnings.append( "(" +
		        		TDA.ERRORDESCRIPTION_ALERT_OTHER + ") " +
		        		warningItem.getErrorMessageText() );
	    	    break;
                
            case TDA.ERRORTYPE_WARNING_INVALIDCHOICE:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_WARNING_INVALIDCHOICE + ") " +
                        warningItem.getErrorMessageText() );
                break;
                
            case TDA.ERRORTYPE_ALERT_DEPRECATEDSETTING:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_ALERT_DEPRECATEDSETTING + ") " +
                        warningItem.getErrorMessageText() );
                break;
                
            case TDA.ERRORTYPE_ALERT_UNKNOWNSETTING:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_ALERT_UNKNOWNSETTING + ") " +
                        warningItem.getErrorMessageText() );
                break;
                
            case TDA.ERRORTYPE_ALERT_DEFAULTAPPLIED:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_ALERT_DEFAULTAPPLIED + ") " +
                        warningItem.getErrorMessageText() );
                break;
                
            case TDA.ERRORTYPE_ALERT_DEV:
                
                warnings.append( "(" +
                        TDA.ERRORDESCRIPTION_ALERT_DEV + ") " +
                        warningItem.getErrorMessageText() );
                break;
	    	    
		    default: 
	    	    
		        warnings.append( "(" +
		        		TDA.ERRORDESCRIPTION_WARNING_OTHER + ") " +
		        		warningItem.getErrorMessageText() );
	
		       	break;
		    }
		    
		    if ( errorsIterator.hasNext() ) {
		        
		        warnings.append( TDA.FEEDBACK_NEWLINE );
		    }
		}
				    
		return warnings;
	}
		
	// Wrapper around the internal collection for the processed items
	protected void addToProcessedSettings( final SettingItem _settingItem ) {
        
	    processedSettings_.add( _settingItem );
	}

	public Set getProcessedSettings() {
	    
	    return processedSettings_;
	}

    public String getSettingItemValueAsValidated( final String _settingName ) {
        
        SettingItem itemToFind;
        
        // If we can't find a matching setting, we return this value:
        String itemValue =  new String( TDA.DATA_SETTINGNOTFOUND );
        
        Iterator settingItemIterator = processedSettings_.iterator();
        while ( settingItemIterator.hasNext() ) {
            
            itemToFind = ( SettingItem ) settingItemIterator.next();
            if ( itemToFind.getItemNameForComparison().equalsIgnoreCase( 
                    _settingName ) ) {
                
                itemValue = itemToFind.getItemValueValidated();
                
                // if the setting has an invalid value, we return it
                if ( !itemToFind.isValidSetting() )
                    itemValue = TDA.DATA_SETTINGINVALIDVALUE;
                
                break;
            }
        }
        
        return itemValue;
    }

    public String getSettingItemValueAsLoaded( final String _settingName ) {
        
        SettingItem itemToFind;
        
        // If we can't find a matching setting, we return this value:
        String itemValue =  new String( TDA.DATA_SETTINGNOTFOUND );
        
        Iterator settingItemIterator = processedSettings_.iterator();
        while ( settingItemIterator.hasNext() ) {
            
            itemToFind = ( SettingItem ) settingItemIterator.next();
            String tmpName = itemToFind.getItemNameForComparison();

            if ( itemToFind.getItemNameForComparison().equalsIgnoreCase( 
                    _settingName ) ) {
                
                itemValue = itemToFind.getItemValueAsLoaded();
                
                // if the setting has an invalid value, we return it
                if ( !itemToFind.isValidSetting() )
                    itemValue = TDA.DATA_SETTINGINVALIDVALUE;
                
                break;
            }
        }
        
        return itemValue;
    }
	
	public boolean isSettingValueValid( final String _settingName ) {
	    
	    boolean isSettingValueValid = true;
	    
	    if ( getSettingItemValueAsValidated( _settingName ).equals( 
	            	TDA.DATA_SETTINGNOTFOUND ) || 
             getSettingItemValueAsValidated( _settingName ).equals( 
    	            TDA.DATA_SETTINGINVALIDVALUE ) ) {
	        
	        isSettingValueValid = false;
	    }
	    
	    return isSettingValueValid;	    
	}
    
    /**
     * Adds a set of errors to the collectedErrors set.
     */
	// Add a set of errors to the collectedErrors set
	public void addToErrors( final Set _errorItems ) {
	    
	    collectedErrors_.addAll( _errorItems );
	}
    
    /**
     * Add a single error to the collectedErrors set.
     */
	// Add a single error to the collectedErrors set
	public void addToErrors( final TdaError _errorItem ) {
	    
	    collectedErrors_.add( _errorItem );
	}
    
    /**
     * Add a single error to the collectedErrors set.
     */
	// In select exception instances (e.g., interrupted execution of dot, etc)
	// we want to pass along the exception, so we can provide the trace
	public void addToErrors( final TdaError _errorItem, final Exception e ) {
	    
	    collectedErrors_.add( _errorItem );

	    // Add the optional stack trace if the user has this option set
		if ( e != null && 
		        this.getValidatedProcessParameter( TDA.SETTING_DISPLAYDEBUGINFO )
	   	        	.equals( TDA.UI_DEBUGINFO_STACKTRACE ) ) {
	    
	   	    e.printStackTrace();
	   	}
	}
    
    /**
     * Get the set of collected errors.
     */
	public Set getCollectedErrors() {
	    
	    return collectedErrors_;
	}
    
    /**
     * Adds a set of warnings to the collectedWarnings set.
     */
	// Add a set of warnings to the collectedWarnings set
	public void addToWarnings( final Set _warningItems ) {
	    
	    collectedWarnings_.addAll( _warningItems );
	}
    
    /**
     * Add a single warning to the collectedWarnings set.
     */
	// Add a single warning to the collectedWarnings set
	public void addToWarnings( final TdaError _warningItem ) {
	    
	    collectedWarnings_.add( _warningItem );
	}
    
    /**
     * Get the set of collected warnings.
     */
	// Add a set of warnings to the collectedWarnings set
	public Set getCollectedWarnings() {
	    
	    return collectedWarnings_;
	}

    /**
     * Checks if the process encountered any (fatal) problems.
     */
	// Check if the error set is (not) empty
    public boolean wereThereProblems() throws Exception {
        
        boolean errorsEncountered = false;
        
        if ( getCollectedErrors().size() > 0 ) {
            			
			errorsEncountered = true;
		}
        
        return errorsEncountered;
    }

    
    /**
     * Processes the supplied setting.
     */
	// This is a simple wrapper around the validation of a setting 
	// that is provided by the settingItem class
	// Note that it encapsulates access to the internal storage of the
	// setting items and their values (currently using a simple property
	// called initialProcessParametersLowercase)
	// Features: makes lookup independent of caps in spelling
    public SettingItem processSetting( 
            final String _settingNameCanonical,
            final String _settingNameDescriptive,
            final String _settingNameForDisplay,
            final String _settingDataType,
            final int _validationType,
            final Object _additionalInfo, 
            final Object _defaultValue ) throws Exception {
                
        // _additionalInfo is used for acceptable values, patterns, etc.,
        // based on the type of data to be validated
        
	    String settingValue = new String();
		SettingItem settingItem = null;

	    settingValue = this.initialProcessParametersLowercase_.getProperty(
	            _settingNameCanonical.toLowerCase() );
        
		if ( _validationType == TDA.VALIDATIONTYPE_OPTIONAL ) {
		    
		    // Set up validation of an optional item 
		    settingItem = new OptionalSettingItem(
			    	_settingNameCanonical,_settingNameDescriptive, _settingNameForDisplay );
		}
		else if ( _validationType == TDA.VALIDATIONTYPE_MANDATORY ) {

		    // Set up validation of a mandatory item 
		    settingItem = new MandatorySettingItem(
		    	_settingNameCanonical,_settingNameDescriptive, _settingNameForDisplay );
		}
		else {
		    
		    // To use a new type of validation object besides OptionalSettingItem and
		    // MandatorySettingItem, that object needs to be properly defined, hence:   
		    throw new TdaException( TDA.ERROR_APP_DEV, 
		            "(Settings.processSetting) " +
		            "Development issue: " +
		            "Need to define a new class for the attempted validation! " +
		            "(Associated 'validation type' value = '" + _validationType +
		            "')." );
		}
		    
	    // Finally validate the setting item
	    settingItem.validate( 
	            settingValue,
	            _settingDataType,
	            _validationType,
	            _additionalInfo,
	            _defaultValue );
                
	    // Add the setting item to our "processed items" collection
	    addToProcessedSettings( settingItem );
	    
	    // If the validation encountered any errors, then collect those
	    if ( !settingItem.isValidSetting() ) {
	        
	        addToErrors( settingItem.getCollectedErrors() );
	    }
		
	    return settingItem;
    }

    /**
     * Applies formatting to the supplied string.
     */
    protected String formatForDisplay( final String _textToFormat ) {
        
        String prefix = TDA.ERRORMESSAGEDISPLAYPREFIX;

        return prefix + _textToFormat;
    }
	
	// -----------------------------------------
	// Access functions to individual parameters
	// -----------------------------------------

    /**
     * @return Returns the initialProcessParametersLowercase.
     */
    public String getInitialProcessParameterLowercase( final String _settingName ) {
        
        return initialProcessParametersLowercase_.getProperty( 
                _settingName );
    }

    /**
     * @return Returns the value of the specified validated setting.
     */
	// Validated data access
    // Wrapper for retrieving a value from a processed (validated) setting item
	public String getValidatedProcessParameter( final String _settingName ) {
        
	    SettingItem itemToFind; 
	    String itemValue =  new String( TDA.DATA_SETTINGNOTFOUND );
	    
		Iterator settingItemIterator = processedSettings_.iterator();
		while ( settingItemIterator.hasNext() ) {
		    
		    itemToFind = ( SettingItem ) settingItemIterator.next();
//            String tmp = itemToFind.toString();
//            System.out.println( tmp );
		    if ( itemToFind.getItemNameForComparison().equalsIgnoreCase( 
		            _settingName ) ) {
		        
		        itemValue = itemToFind.getItemValueValidated();
		        break;
		    }
		}

        if ( TDA.DEBUG && TDA.TRACE_VALIDATEDPARAMS ) {
        
            System.out.print( "\nGETTING validated param " + _settingName + 
                "= '" + itemValue +
                "'");
        }
        
	    return itemValue;
	}

    
    /**
     * Sets the validated process parameter (setting) to the supplied value.
     */
	public void setValidatedProcessParameter( final String _settingName, 
	        final String _settingValue ) throws Exception {

        if ( TDA.DEBUG && TDA.TRACE_VALIDATEDPARAMS ) {
        
            System.out.print( "\nSetting validated param " + _settingName + 
                "= '" + _settingValue +
                "'");
        }
        
	    SettingItem itemToFind =  null; 
	    
		Iterator settingItemIterator = processedSettings_.iterator();
		while ( settingItemIterator.hasNext() ) {
		    
		    itemToFind = ( SettingItem ) settingItemIterator.next();
            
            if ( itemToFind.getItemNameForComparison().contains("xml") ) {
                int x=0;
            }
            
            
		    if ( itemToFind.getItemNameForComparison().equalsIgnoreCase( 
		            _settingName ) ) {
		        
		        itemToFind.setItemValueValidated( _settingValue );
		        break;
		    }
		}
		
		if ( itemToFind == null ) {
		    
		    // Currently we don't let outside code set the value of setting items
		    // that don't exist in our processedSettings collection
		    // (Note: it's conceivable that one could want different behaviour
		    // here, but it would be at the cost of increased complexity elsewhere
		    // as well as [likely] less predictability)
            throw new TdaException( TDA.ERROR_APP_DEV,
                    "(Settings.setValidatedProcessParameter) " +
            		"(Developer issue) " +
            		"There is no setting item '" + _settingName + "' " +
    				"for assigning the value ('" + _settingValue + "')."  );
		}
	}
    
    /**
     * Gets the value of the specified dynamic process parameter.
     */
	// Dynamic data: generally data used by core classes in the implementation
	// of a given search strategy
	public String getDynamicProcessParameter( final String _parameterName ) {

        String itemValue =  dynamicProcessParameters_.getProperty( 
                _parameterName );
                
        if ( TDA.DEBUG && TDA.TRACE_DYNAMICPARAMS ) {
        
            System.out.print( "\nGETTING dynamic param " + _parameterName + 
                "= '" + itemValue +
                "'");
        }
        
		return itemValue;
	}
    
    /**
     * Sets the dynamic process parameter to the supplied value.
     */
	public void setDynamicProcessParameter(
            final String _parameterName, 
	        final String _parameterValue ) {

        if ( TDA.DEBUG && TDA.TRACE_DYNAMICPARAMS ) {
        
            System.out.print( "\nSetting dynamic param " + _parameterName + 
                "= '" + _parameterValue +
                "'");
        }
		
		dynamicProcessParameters_.setProperty(
		        _parameterName, _parameterValue);
	}
	
	/**
	 * @return Returns the dynamicProcessParameters.
	 */
	public Properties getDynamicProcessParameters() {
		return dynamicProcessParameters_;
	}
    
    /**
     * @param objectToAdd The object to add to the generalProcessDataStorage set.
     */
    public void addToGeneralProcessDataStorage( Object objectToAdd ) {
        generalProcessDataStorage_.add( objectToAdd );
    }
    /**
     * @return Returns the generalProcessDataStorage.
     */
    public Set getGeneralProcessDataStorage() {
        return generalProcessDataStorage_;
    }
    /**
     * @return Returns the size of the generalProcessDataStorage.
     */
    public int getStorageSize() {
        return generalProcessDataStorage_.size();
    }
	
	/**
	 * @return Convenient display of the current sets of parameters 
	 * (validated and passed-in). 
	 * Lists items within sets in alphabetic order.
	 */
	public synchronized String toString() {
	    
	    StringBuffer collectedSettings = 
	        new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
	    	    
	    SortedSet parameterSet;
		Iterator parameterIterator;
		String strParameterName;
		String strParameterValue;
        Properties tmpValidatedSettings = new Properties();
		
		collectedSettings.append( "\n\nPassed-in Parameters:\n" );

        if ( passedInParameters_ != null ) {
            
    		parameterSet = new TreeSet();
    	    parameterSet.addAll( passedInParameters_.keySet() );
    	    
    		parameterIterator = parameterSet.iterator();
    		while (parameterIterator.hasNext()) {
    		    
    		    strParameterName = (String) parameterIterator.next();
    		    strParameterName = strParameterName.trim();
    
    		    strParameterValue = 
    		        passedInParameters_.getProperty( strParameterName ).trim();
    		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
    			
    		    collectedSettings.append( "   " );
    		    collectedSettings.append( strParameterName );
    		    collectedSettings.append( " = " );
    		    collectedSettings.append( strParameterValue );
    		    collectedSettings.append( "\n" );
    		}
        }
        
		collectedSettings.append( "Validated Parameters (so far):\n" );

        if ( processedSettings_ != null ) {
            
    		// Create a simple properties set of the validated parameters
    	    SettingItem nextItem; 
    	    
    		Iterator settingItemIterator = processedSettings_.iterator();
    		while ( settingItemIterator.hasNext() ) {
    		    
    		    nextItem = ( SettingItem ) settingItemIterator.next();
    		    strParameterName = nextItem.getItemNameCanonical();
    		    strParameterName = strParameterName.trim();
    		    
    		    if ( nextItem.isValidSetting() ) {
    		        
    			    strParameterValue = 
    			        nextItem.getItemValueValidated().trim();
    		    }
    		    else {
    		        
    		        strParameterValue = "(no valid value)";
    		    }
    		    
    		    tmpValidatedSettings.setProperty( strParameterName, strParameterValue );
    		}
        }

        if ( tmpValidatedSettings != null ) {
            
    		parameterSet = new TreeSet();
    	    parameterSet.addAll( tmpValidatedSettings.keySet() );
    	    
    	    parameterIterator = parameterSet.iterator();
    		while ( parameterIterator.hasNext() ) {
    
    		    strParameterName = (String) parameterIterator.next();
    		    strParameterName = strParameterName.trim();
    
    		    strParameterValue = 
    		        tmpValidatedSettings.getProperty( strParameterName ).trim();
    		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
    
    		    collectedSettings.append( "   " );
    		    collectedSettings.append( strParameterName );
    		    collectedSettings.append( " = " );
    		    collectedSettings.append( strParameterValue );
    		    collectedSettings.append( "\n" );
    		}
        }
		            
		collectedSettings.append( "\n\n(Current set of) 'Dynamic' Parameters:\n" );

   
        if ( dynamicProcessParameters_ != null ) {
            
    		parameterSet = new TreeSet();
    	    parameterSet.addAll( dynamicProcessParameters_.keySet() );
    	    
    		parameterIterator = parameterSet.iterator();
    		while (parameterIterator.hasNext()) {
    		    
    		    strParameterName = (String) parameterIterator.next();
    		    strParameterName = strParameterName.trim();
    
    		    strParameterValue = 
    		        dynamicProcessParameters_.getProperty( strParameterName ).trim();
    		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
    			
    		    collectedSettings.append( "   " );
    		    collectedSettings.append( strParameterName );
    		    collectedSettings.append( " = " );
    		    collectedSettings.append( strParameterValue );
    		    collectedSettings.append( "\n" );
    		}
        }

		
		collectedSettings.append( "\n\nInitial Parameters (as loaded):\n" );

        if ( initialProcessParametersAsLoaded_ != null ) {
            
    		parameterSet = new TreeSet();
    		parameterSet.addAll( initialProcessParametersAsLoaded_.keySet() );
    		
    		parameterIterator = parameterSet.iterator();
    		while (parameterIterator.hasNext()) {
    		    
    		    strParameterName = (String) parameterIterator.next();
    		    strParameterName = strParameterName.trim();
    		
    		    strParameterValue = 
    		        initialProcessParametersAsLoaded_.getProperty( strParameterName ).trim();
    		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
    		
    		    collectedSettings.append( "   " );
    		    collectedSettings.append( strParameterName );
    		    collectedSettings.append( " = " );
    		    collectedSettings.append( strParameterValue );
    		    collectedSettings.append( "\n" );
    		}
        }
		
		return collectedSettings.toString();
	}

	/**
	 * @return Convenient display of the current sets of (validated and passed-in)
	 * parameters.
	 * 4/23/2014	hjs		Created for Api-mode access.
	 * 
	 */
	public synchronized String getPassedInParameters() {
	    
	    StringBuffer collectedSettings = 
	        new StringBuffer( TDA.BUFFERLENGTH_STAT_INTERNAL );
	    	    
	    SortedSet parameterSet;
		Iterator parameterIterator;
		String strParameterName;
		String strParameterValue;
        Properties tmpValidatedSettings = new Properties();
		
        if ( passedInParameters_ != null ) {
            
    		parameterSet = new TreeSet();
    	    parameterSet.addAll( passedInParameters_.keySet() );
    	    
    		parameterIterator = parameterSet.iterator();
    		while (parameterIterator.hasNext()) {
    		    
    		    strParameterName = (String) parameterIterator.next();
    		    strParameterName = strParameterName.trim();
    
    		    strParameterValue = 
    		        passedInParameters_.getProperty( strParameterName ).trim();
    		    strParameterValue = StringUtil.removeTrailingComment( strParameterValue );
    			
    		    collectedSettings.append( "   " );
    		    collectedSettings.append( strParameterName );
    		    collectedSettings.append( " = " );
    		    collectedSettings.append( strParameterValue );
    		    collectedSettings.append( "\n" );
    		}
        }
		
		return collectedSettings.toString();
	}
    
    public StringBuffer getOptionalThreadInfo() throws Exception {
        
        // Default the threadInfo string to a blank line that is approximately
        // the same length as the composed string below (currently: "[Thread i]"),
        // to keep some derived feedback strings properly composed (e.g., in errors).
        StringBuffer threadInfo = new StringBuffer( "" );
        int maxThreads = 1;
        int threadID = -1;
        
        try {
            maxThreads = Integer.parseInt( 
                    getDynamicProcessParameter( TDA.DATA_MAXTHREADS ));
        }
        catch ( Exception e ) {
            // swallow any exception
        }
        
        try {
            threadID = Integer.parseInt( 
                    getDynamicProcessParameter( TDA.DATA_THREADINDEX ));
        }
        catch ( Exception e ) {
            // swallow any exception
        }
        
        // Only supply thread info when there are >1 threads
        if ( maxThreads > 1 ) {
            
            // and: Only when we are within a thread (not the controller)
            if ( threadID > -1 ) {
                
                // Note that for the user's sake we start the thread numbering at 1 
                // (instead of 0)
                threadInfo = new StringBuffer( "[Thread " + ( threadID + 1 ) +
                        "]" );
            }
            else {
                
                threadInfo = new StringBuffer( "[Controller]" );
            }
        }
     
        return threadInfo;
    }
    
    // -------------------------------------------
    // Wrapper functions around the fileUtil class
    // ------------------------------------------- 
        
    public void writeToFile( final Collection _outputFileFlags, 
            final StringBuffer _stringBufferToWrite ) throws Exception {
                
        fileUtil_.writeToFile( _outputFileFlags, _stringBufferToWrite );
    }
    
    public void writeStringToFile( 
            final String _fileName, 
            final String _dataToWrite, 
            final boolean _traceToConsole ) throws Exception {
        
        fileUtil_.writeStringToFile( _fileName, _dataToWrite, _traceToConsole );
    }
    
    public void writeStringToFile( 
            final String _fileName, 
            final String _dataToWrite, 
            final boolean _traceToConsole,
            final boolean _append ) throws Exception {
        
        fileUtil_.writeStringToFile( _fileName, _dataToWrite, _traceToConsole, _append );
    }
    
    public void writeTraceToFile( 
            final String _dataToWrite, 
            final boolean _traceToConsole,
            final int _traceFileID ) throws Exception {
    
        fileUtil_.writeTraceToFile( _dataToWrite, _traceToConsole, _traceFileID );
    }
    
    public void recordError( final String _strErrorMessage ) throws Exception {
    
        if ( fileUtil_ == null ) fileUtil_ = new FileUtil( this );
        fileUtil_.recordError( _strErrorMessage );
    }

    // Note: this is called at the start of the search process, so we can write out 
    // data as it accumulates
    public void prepareFileOutput() throws Exception {
        
        fileUtil_ = new FileUtil( this );
        fileUtil_.prepareResultsFile();
    }

    // Note: this is called at the very end of the search process, to write out
    // the final data in xml format
    public void prepareXMLOutput() throws Exception {
        
        fileUtil_.prepareXMLResultsFile();
    }
    
    // Main wrapper function for accessing the random sequence
    // THIS method should be used so that the tesMode setting can work its magic.
    // Note: by having a 1-to-1 correspondence between the settings and the random sequence,
    // we can now run repeatable tests even for multi-threaded scenarios.
    public Random getRandomSequence() {
        
        return randomNumber_.getRandomSequence();
    }
    
    // Wrapper functions for accessing the seed for the random sequence
    public long getRandomSeed() {
        
        return randomNumber_.getRandomSeed();
    }
    public void setRandomSeed( long _randomSeed ) {
        
        randomNumber_.setRandomSeed( _randomSeed );
    }
}
