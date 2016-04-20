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
package edu.duke.math.tda.utility;

/**
 * Defines all constants (numeric, string, etc) used by TDA.
 *
 * <p><strong>Details:</strong> <br>
 *  
 * 
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public final class TDA {
	
	// Remember to recompile all classes that reference this class when 
	// changing a static final value (especially the DEBUG/TRACE flags when 
	// switching to production mode)
	
    //-------------------------
    // TDA version identification:
    //-------------------------
	public static final String APPLICATION_NAME = "TDA";
	public static final String APPLICATION_NAME_LONG =
        "Topological Data Analysis";
	public static final String APPLICATION_VERSIONNUMBER = "1.0.0";
	public static final String APPLICATION_VERSIONDATE = "14 Apr 2015";
	public static final String APPLICATION_VERSIONDESCRIPTION = "Internal Code Target 1.0";
	//
    public static final String RELEASE = "Release";
    public static final String TDAVERSION = "TDA Version";
	public static final String RELEASE_LICENCED = "Licensed from Duke University";
	public static final String RELEASE_COPYRIGHT =
	    "Copyright (c) 2012-2014 by John Harer";
	public static final String RELEASE_ALLRIGHTS = "All rights reserved";
    // Used XML version
    public static final String DATA_TDAXMLTAG_XMLVERSION = 
        "<?xml version=" + "\"1.0\"" + " encoding= " + "\"UTF-8\"" + "?>";
    public static final String DATA_XMLVERSION = 
        "1.0";
    public static final String DATA_XMLVERSION_DISP = 
        "XML version:";
    // XML output data versioning:
    public static final String DATA_TDAXMLFORMATVERSION_1 = "1.0";
    public static final String DATA_TDAXMLFORMATVERSION = DATA_TDAXMLFORMATVERSION_1;
    public static final String DATA_TDAXMLFORMATVERSION_DISP = "TDA XML format version:";
	
    
       
    //-------------------------
    // DEVELOPMENT constants:
    //-------------------------
	
	//-------------------------
	// Part I: 		(Internal Development) Settings
	//-------------------------

    // Display the progress through a search (all dev. options are
    // tied to the global DEBUG flag unless otherwise indicated)
    public static final boolean CONFIG_DISPLAYPROGRESS = false;
    public static final int CONFIG_TRACEFILE_1 = 1;
    public static final int CONFIG_TRACEFILE_2 = 2;
    public static final int CONFIG_TRACEFILE_3 = 3;

	//-------------------------
	// Part II: 	Control application behaviour before folding into 
	// 				general release
	//-------------------------
	// For now, several special output sections are included in each search report.
	// To change the default behaviour, change the values of the constants:
	//
	public static final boolean CONFIG_DISPLAYSTATISTICS = true;
	public static final boolean CONFIG_DISPLAYSTATISTICS_EQUIVCHECK = true;
	//
    // To get the entire output that is written to file to also print
    // on the command line, set this to true:
    //
	// If you absolutely don't care about any command line output,
	// then set this flag to false:
    public static final boolean CONFIG_ECHOOUTPUTTOCMDLINE = true;
    // This should probably always be true (for the user's sake)
    public static final boolean CONFIG_ECHOERROROUTPUTTOCMDLINE = true; 

    // (Optional) final display of elapsed time
    public static final boolean CONFIG_FINALTIMEDISPLAY = false;
    
    // Path separators
    public static final String DATA_SEPARATORFORWARDSLASH = "/";
    public static final String DATA_SEPARATORBACKWARDSLASH = "\\";
    public static final String DATA_SEPARATORCOLON = ":";
    
	//-------------------------
	// Part III: 	Debug and Development Feedback flags
	//-------------------------
	
	// Main "Global" debug flag: turn(ed) off for release
	// default in user mode (vs developer mode): false
    public static final boolean DEBUG = false; //false;
	public static final boolean TRACE_FEEDBACK = true;

	// Individual (secondary) debug flags (always used as a dual
	// condition with DEBUG, so that we only need to turn off DEBUG
	// when we are ready to deploy)
	public static final boolean TRACE_EDGELIST = true;//true;
	public static final boolean TRACE_PERSISTENCEMATRIX = false;//true;
	public static final boolean TRACE_REDUCTION = false;//true;
	public static final boolean TRACE_COREDUCTION = false;//true;
	public static final boolean TRACE_COMPUTELSD = false;//true;

    public static final boolean TRACE_TIMINGINFO = false;
    public static final boolean TRACE_MEMORYUSE = false;//true;

    // Flag for turning off/on any feedback that is set up to be conditional
    // (e.g., memory info, time-taken info, etc)
    public static final boolean GLOBALFEEDBACK = true;
    // Individual feedback flags
    public static final boolean FEEDBACK_MEMORYUSE = true;
    public static final boolean FEEDBACK_TIME = true;
    
    
	public static final boolean TRACE_FILEUTIL = false;
	public static final boolean TRACE_STATISTICS = false;
    public static final boolean TRACE_VALIDATEDPARAMS = false;
    public static final boolean TRACE_DYNAMICPARAMS = false;
    
    public static final boolean TRACE_XMLOUTPUT = false;
    
	//--------------------------
	// Internal constants
	//--------------------------
	
    // String value used when we (internally) ask for a value for a property or setting,
    // but no such value exists (yet)
//    public static final String DATA_STRINGDOESNOTEXIST = "does not exist";
    
	//--------------------------
	// Part I:	Tuning constants
	//--------------------------

	
//	//--------------------------
//	// Part II: Values set as conventions
//	//--------------------------
	
	
    // Not used
	// Values for setting the logging preferences
	public static final int LOGGING_REGULAR = 1;
	public static final int LOGGING_MINIMAL = 2;
	public static final int LOGGING_VERBOSE = 3;
	
	// Values to indicate the logging files
	public static final int FILE_RESULTS = 0;
	public static final int FILE_SUMMARY = 1;
    public static final int FILE_TRACE = 2;
    public static final int FILE_RESULTSXML = 3;
    public static final int FILE_INTERVALS = 4;
    public static final int FILE_INTERVALSANDGENS = 5;

    public static final int TDA_SYSTEM_ENTER = 13;
    public static final int TDA_SYSTEM_NL = 10;
    
    public static final String APP_FREEFORMINPUT = "freeFormInput";
    //
    public static final String APP_NOVALUESUPPLIED_STRING = 
        "noValueSuppliedByUser";
    // Use this as general purpose indicator that user didn't specify
    // a value for a numeric input (of course, only applicable for
    // data that is supposed to be >=0, which most of ours is; for
    // data that can be negative, may need to use the string, and check
    // before converting setting back to number)
    public static final int APP_NOVALUESUPPLIED_NUMBER = -1;
    // This is the default value used when the user doesn't input a value
    // (could change to "none" or "not specified", etc.)
    public static final String APP_NOVALUE_INDISPLAY = "";
	
	// ------------------
	// Feedback-related formatting constants
	// ------------------
	
	public static final int FEEDBACK_LINELENGTH = 78;
	public static final String FEEDBACK_SEPARATOR_ITEMS = ":";
	public static final String FEEDBACK_SEPARATOR_VERTICAL = "|";
	public static final String FEEDBACK_NEWLINE = "\n";
	public static final String FEEDBACK_DASH = "-";
	public static final String FEEDBACK_SPACE = " ";
    public static final String FEEDBACK_COLON = ":";
    public static final String FEEDBACK_QUOTES = "\"";
    public static final String FEEDBACK_DASHEDLINE = 
        "----------------------------------------------------" + 
        "----------------------------------------------------";
    public static final String FEEDBACK_NEWLINEPLUSDASHEDLINE = 
        "\n----------------------------------------------------" + 
        "----------------------------------------------------";
    public static final String FEEDBACK_SPACESFORTRIMMING = 
        "                                                                            " + 
        "                                                                            ";
    
    public static final String FEEDBACKSTRING_STATUS = "Status:  ";
    public static final String FEEDBACKSTRING_TIME = "Time";
	public static final int FEEDBACK_PERCENTPADDINGLENGTH = 5;
	public static final int FEEDBACK_TIMEPADDINGLENGTH = 8;

    public static final String FEEDBACKSTRING_FINALCHECK = 
        "(Final Checkpoint, after task execution) " +
        "A final check revealed the following issues " +
        "that were encountered during TDA's execution:";
    
    public static final String FEEDBACKSTRING_POSTPROC = 
        "(Final Checkpoint, after post-processing) " +
        "A final check revealed the following issues " +
        "that were encountered during TDA's execution:";
		
	public static final int FEEDBACK_OPTION_TIMEFORMAT_DEFAULT = 0;
    public static final int FEEDBACK_OPTION_TIMEFORMAT_IN_D = 1;
    public static final int FEEDBACK_OPTION_TIMEFORMAT_IN_H = 2;
	public static final int FEEDBACK_OPTION_TIMEFORMAT_IN_M = 3;
	public static final int FEEDBACK_OPTION_TIMEFORMAT_IN_S = 4;
	public static final int FEEDBACK_OPTION_TIMEFORMAT_IN_MS = 5;
	public static final int FEEDBACK_OPTION_TIMEFORMAT_MIXED = 6;
	
	// Determines how many decimals are displayed for the elapsed time
	public static final int FEEDBACK_NUMBEROFDECIMALSINTIMEDISPLAY = 2;
	
	// String constants used for specifying time values
	public static final String DATA_TIMEQUALIFIER_DAYS = "d";
	public static final String DATA_TIMEQUALIFIER_HOURS = "h";
	public static final String DATA_TIMEQUALIFIER_MINUTES = "m";
	public static final String DATA_TIMEQUALIFIER_SECONDS = "s";
	public static final String DATA_TIMEQUALIFIER_MILLISECS = "ms";

	// String constants used for time display
    public static final String FEEDBACK_DEFAULT_TIME_DAYS = "d";
    public static final String FEEDBACK_DEFAULT_TIME_HOURS = "h";
	public static final String FEEDBACK_DEFAULT_TIME_MINUTES = "m";
	public static final String FEEDBACK_DEFAULT_TIME_SECONDS = "s";
	public static final String FEEDBACK_DEFAULT_TIME_MILLISECS = "ms";

	// Limits where we change the display from milliseconds to seconds
	public static final double FEEDBACK_DEFAULT_CUTOFF_TO_SECONDS = 0.99;
	// .. from seconds to minutes
	public static final double FEEDBACK_DEFAULT_CUTOFF_TO_MINUTES = 0.99;
	// .. minutes to hours
    public static final double FEEDBACK_DEFAULT_CUTOFF_TO_HOURS = 0.99;
    // .. hours to days
    public static final double FEEDBACK_DEFAULT_CUTOFF_TO_DAYS = 0.99;
	
	
	// 10/13/2005 (v2.0) hjs	Use the following "DecimalFormat"-compliant
	// strings to specify the formatting of decimal numbers
    // 9/27/2007 hjs Note: the strings are locale-dependent, so when we use them
    // we need to specify the US locale (see, e.g., StringUtil.formatDecimalDisplay)
//    public static final String FEEDBACK_DISPLAYFORMATFORNETWORKSCORE = "####.00000##################";
    public static final String FEEDBACK_DISPLAYFORMATFORNUMBER = "####.0000";

	// Output files used: summary, results, trace, xml
	public static final int MAXOUTPUTFILES = 4;
	
	// Initial initialSettings for internal string buffers
	public static final int BUFFERLENGTH_STAT = 10000;
	public static final int BUFFERLENGTH_STAT_INTERNAL = 2000;
	public static final int BUFFERLENGTH_STRUCTURE = 1000;
	public static final int BUFFERLENGTH_STRUCTURE_LARGE = 30000;
	public static final int BUFFERLENGTH_SMALL = 100;

		
	// Set up a large number as initial distance for finding the closest neighbors
    public static final double TDA_THRESHOLD = 10000000;

	// Value and string to represent 'infinity' ("death" value for intervals)
	public static final double INTERVAL_NODEATHVALUE = -1.0;
	public static final String INTERVAL_STRINGINFINITY = "infinity";
	public static final double INTERVAL_VALUEINFINITY = -1.0;

	//
    public static final double TDA_NUMERICEQUALITY = 0.0000000001;
	
	// -------------------
	// Test-related
	// -------------------
	
	// Testing-related data
    public static final double TEST_SCORETOLERANCE = 0.0000005;
	

    
	// -----------------------
	// Data that is being updated during search (setup or execution),
	// or during postprocessing
	//-----------------------
	
	// References to data that changes during the program execution
	public static final String DATA_ELAPSEDTIME = "elapsedTime";
	// Data that is being collected during the program execution
	public static final String DATA_TOTALTIMEFORPREP = "totalTimeForPrep";
	public static final String DATA_TOTALTIMEFOREXECUTION = "totalTimeForExecution";
	public static final String DATA_LOCALNEIGHBORHOODSREPORT = 
		"localNeighborhoodsReport";
    
	// Data that is updated in the algorithm via the updateProcessData method
	public static final String DATA_NUMBEROFPOINTSINPOINTCLOUD = 
	    "dataNumberOfPoints";
	public static final String DATA_NUMBEROFPOINTSINPOINTCLOUD_DESCR = 
	    "Number of points";
	public static final String DATA_NUMBEROFPOINTSINPOINTCLOUD_DISP = 
	    "Number of points:";
	
	public static final String DATA_NUMBEROFEDGES = 
	    "dataNumberOfEdges";
	public static final String DATA_NUMBEROFEDGES_DESCR = 
	    "Number of edges";
	public static final String DATA_NUMBEROFEDGES_DISP = 
	    "Number of edges:";
	    
    // -------------
    // Customization
    // -------------
    
    // Default time stamp for general use
    public static final String DEFAULT_TIMESTAMP = "yyyy.MM.dd.HH.mm.ss";

    // String appended as time stamp for various output files
    public static final String SETTING_TIMESTAMPSTRINGFORFILES = "timeStampFormat";
    public static final String SETTING_TIMESTAMPSTRINGFORFILES_DESCR = "Time stamp format";
    public static final String SETTING_TIMESTAMPSTRINGFORFILES_DISP = "Time stamp format:";
    public static final String DEFAULT_TIMESTAMPSTRINGFORFILES = "yyyy.MM.dd.HH.mm.ss";
    // alternative time stamps (with examples):
    // ----------------------------------------
    // appends nothing:
//  public static final String DEFAULT_TIMESTAMPSTRINGFORFILES = "";
    //
    // appends, e.g., "___23Jan2006":
//  public static final String DEFAULT_TIMESTAMPSTRINGFORFILES = "___ddMMMyyyy";

    // tokens accepted for time stamp
    public static final String DATA_TIMESTAMP_TOKEN = "@timestamp@";
    public static final String DATA_TIMESTAMP_TOKEN_ALT0 = "@time stamp@";
    public static final String DATA_TIMESTAMP_TOKEN_ALT1 = "@Time_Stamp@";
    public static final String DATA_TIMESTAMP_TOKEN_ALT2 = "@ts@";
    public static final String DATA_TIMESTAMP_TOKEN_ALT3 = "@TS@";
    
    // (v2.1) other tokens
    // Note that the generic pre-processing fails if the tokens will be case-insensitive,
    // and will replace all "related" tokens by the default token
    public static final String DATA_THREADID_TOKEN = "@threadID@";
    public static final String DATA_THREADID_TOKEN_ALT0 = "@thID@";
    
    public static final String DATA_DISTANCE_TOKEN = "@distanceBoundOnEdges@";
    public static final String DATA_DISTANCE_TOKEN_ALT0 = "@distBOE@";
    
    public static final String DATA_POINTCLOUDFILE_TOKEN = "@pointcloudFile@";
    public static final String DATA_POINTCLOUDFILE_TOKEN_ALT0 = "@pcfile@";
    
    public static final String DATA_RADIUSBLN_TOKEN = "@radiusBoundOfLocalNeighborhood@";
    public static final String DATA_RADIUSBLN_TOKEN_ALT0 = "@radBLN@";
    
	
    // ------------------------------
    // IDs for the results for the 'known' algorithms, with values 0 through 10 reserved
    // for results, and 11 to 13 (actually available to 99) for (specific) errors
    // Codes 20 and 21 are reserved for "general" errors and warnings, to be set by the 
    // standard error handlers.
    // ------------------------------
	public static final int DATA_REGISTEREDRESULT_GENERAL_ERRORS = 20;
	public static final int DATA_REGISTEREDRESULT_GENERAL_WARNINGS = 21;
	
	public static final int DATA_REGISTEREDRESULT_RCA0_0 = 0;
	public static final int DATA_REGISTEREDRESULT_RCA0_1 = 1;
	public static final int DATA_REGISTEREDRESULT_RCA0_2 = 2;
	public static final int DATA_REGISTEREDRESULT_RCA0_3 = 3;
	public static final int DATA_REGISTEREDRESULT_RCA0_4 = 4;
	public static final int DATA_REGISTEREDRESULT_RCA0_5 = 5;
	public static final int DATA_REGISTEREDRESULT_RCA0_6 = 6;
	public static final int DATA_REGISTEREDRESULT_RCA0_ERROR1 = 11;
	public static final int DATA_REGISTEREDRESULT_RCA0_ERROR2 = 12;
	public static final int DATA_REGISTEREDRESULT_RCA0_ERROR3 = 13;

	public static final int DATA_REGISTEREDRESULT_RCA1_0 = 0; // 0-dim. intervals
	public static final int DATA_REGISTEREDRESULT_RCA1_1 = 1; // 1-dim. intervals
	public static final int DATA_REGISTEREDRESULT_RCA1_2 = 2; // number of edges
	public static final int DATA_REGISTEREDRESULT_RCA1_3 = 3; // number of columns of reduction matrix
	public static final int DATA_REGISTEREDRESULT_RCA1_4 = 4; // timing info (formatted): M12 computation
	public static final int DATA_REGISTEREDRESULT_RCA1_5 = 5; // timing info (formatted): matrix reduction
	public static final int DATA_REGISTEREDRESULT_RCA1_6 = 6; // timing info (raw): M12 computation
	public static final int DATA_REGISTEREDRESULT_RCA1_7 = 7; // timing info (raw): matrix reduction
	public static final int DATA_REGISTEREDRESULT_RCA1_8 = 8; // memory usage: at start of M12 computation
	public static final int DATA_REGISTEREDRESULT_RCA1_9 = 9; // memory usage: at completion of M12 computation
	public static final int DATA_REGISTEREDRESULT_RCA1_ERROR1 = 11;
	public static final int DATA_REGISTEREDRESULT_RCA1_ERROR2 = 12;
	public static final int DATA_REGISTEREDRESULT_RCA1_ERROR3 = 13;
	
	public static final int DATA_REGISTEREDRESULT_RCA2_0 = 0;
	public static final int DATA_REGISTEREDRESULT_RCA2_1 = 1;
	public static final int DATA_REGISTEREDRESULT_RCA2_2 = 2;
	public static final int DATA_REGISTEREDRESULT_RCA2_3 = 3;
	public static final int DATA_REGISTEREDRESULT_RCA2_4 = 4;
	public static final int DATA_REGISTEREDRESULT_RCA2_5 = 5;
	public static final int DATA_REGISTEREDRESULT_RCA2_6 = 6;
	public static final int DATA_REGISTEREDRESULT_RCA2_ERROR1 = 11;
	public static final int DATA_REGISTEREDRESULT_RCA2_ERROR2 = 12;
	public static final int DATA_REGISTEREDRESULT_RCA3_ERROR3 = 13;
	
	public static final int DATA_REGISTEREDRESULT_LSD_0 = 0;
	public static final int DATA_REGISTEREDRESULT_LSD_1 = 1;
	public static final int DATA_REGISTEREDRESULT_LSD_2 = 2;
	public static final int DATA_REGISTEREDRESULT_LSD_3 = 3;
	public static final int DATA_REGISTEREDRESULT_LSD_4 = 4;
	public static final int DATA_REGISTEREDRESULT_LSD_5 = 5;
	public static final int DATA_REGISTEREDRESULT_LSD_6 = 6;
	public static final int DATA_REGISTEREDRESULT_LSD_ERROR1 = 11;
	public static final int DATA_REGISTEREDRESULT_LSD_ERROR2 = 12;
	public static final int DATA_REGISTEREDRESULT_LSD_ERROR3 = 13;
	
	public static final int DATA_UNREGISTEREDRESULT_STARTINDEX = 100;
    
    
    
	 // -----------------------
	 // Settings (user input)
	 // -----------------------

    //
    public static final String SETTING_APPLICATIONMODE =
        "applicationMode";
    public static final String SETTING_APPLICATIONMODE_DESCR =
        "Application mode";
    public static final String SETTING_APPLICATIONMODE_DISP =
        "Application mode:";
	public static final String UI_APPLICATIONMODE_API = "api";
	public static final String UI_APPLICATIONMODE_STANDALONE = "standalone";
	public static final String UI_APPLICATIONMODE_DEFAULT = 
		UI_APPLICATIONMODE_STANDALONE;

	//
    public static final String SETTING_RUNNINGINECLIPSE =
        "runningInEclipse";
    public static final String SETTING_RUNNINGINECLIPSE_DESCR =
        "Running in Eclipse";
    public static final String SETTING_RUNNINGINECLIPSE_DISP =
        "Running in Eclipse:";
	public static final String UI_RUNNINGINECLIPSE_YES = "yes";
	public static final String UI_RUNNINGINECLIPSE_NO = "no";
	public static final String UI_RUNNINGINECLIPSE_DEFAULT = UI_RUNNINGINECLIPSE_NO;
    
    //
    public static final String SETTING_SCREENREPORTINGINTERVAL =
        "screenReportingInterval";
    public static final String SETTING_SCREENREPORTINGINTERVAL_DESCR =
        "Screen reporting interval";
    public static final String SETTING_SCREENREPORTINGINTERVAL_DISP =
        "Screen reporting interval:";
    // Enter a number in seconds (we chose a small number so new users would
    // not have to wait too long for feedback)
    public static final int DEFAULT_SCREENREPORTINGINTERVAL = 10;
    //
    public static final String SETTING_FILEREPORTINGINTERVAL =
        "fileReportingInterval";
    public static final String SETTING_FILEREPORTINGINTERVAL_DESCR =
        "File reporting interval";
    public static final String SETTING_FILEREPORTINGINTERVAL_DISP =
        "File reporting interval:";
    // Enter a number in seconds (we chose a small number so new users would
    // not have to wait too long for feedback)
    public static final int DEFAULT_FILEREPORTINGINTERVAL = 60;
	
	// ------------------------------------------------------
	// Settings that can only be entered on the command line
	// ------------------------------------------------------
	
	// The main settings file, and possibly a directory where it is located (of
	// course, one can also include the path in the file specification) can only
	// be entered as a commandline argument (how else could it be found?). If it 
    // isn't specified, then the default value (DEFAULT_SETTINGSFILENAME) will be used. 
	// The settings file contains the various initialSettings for running a search,
	// such as selecting between different methods, input and output file names, etc.
	public static final String SETTING_CMDARG_SETTINGSFILENAME = "settingsFile";
	public static final String SETTING_CMDARG_SETTINGSFILENAME_DESCR = "Settings file";
	public static final String SETTING_CMDARG_SETTINGSFILENAME_DISP = "Settings file:";
	public static final String DEFAULT_SETTINGSFILENAME = "tda.config.txt";
//	public static final String DEFAULT_SETTINGSFILENAME = "tda.settings.txt";
	//
	public static final String SETTING_CMDARG_SETTINGSDIRECTORY = "settingsDirectory";
	public static final String SETTING_CMDARG_SETTINGSDIRECTORY_DESCR = "Settings directory";
	
	// -- This can be specified in the settings file
	// Location of default settings (not yet implemented)
//	public static final String DATA_SETTINGSDIRECTORYFORDEFAULTVALUES = "data";
	
	
	
	// Default for interactive feedback
//	public static final String SETTING_ASKTOVERIFYSETTINGS = 
//	    "askToVerifySettings";
//	public static final String SETTING_ASKTOVERIFYSETTINGS_DESCR = 
//	    "Ask to verify settings";
//	public static final String SETTING_ASKTOVERIFYSETTINGS_DISP = 
//	    "Ask to verify settings:";
//	public static final String UI_ASKTOVERIFYSETTINGS_YES = "yes";
//	public static final String UI_ASKTOVERIFYSETTINGS_NO = "no";
//	public static final String DEFAULT_ASKTOVERIFYSETTINGS = UI_ASKTOVERIFYSETTINGS_NO;
	
    // 
    public static final String DATA_SPECIFIEDSETTINGSFILE = 
        "specifiedSettingsFile";
    public static final String DATA_SPECIFIEDSETTINGSFILEDIRECTORY = 
        "specifiedSettingsFileDirectory";
    
    
	//--------------------------------
	// Strings - Names for user input parameters
	//
	// NOTE: these strings define the application's interface to the
	// outside world via the settings file. So any change in the values of
	// the strings will invalidate previous versions of the settings file, and
	// should thus be approached with great caution!!
	//--------------------------------

	public static final String UI_DEFAULT = "default"; // depends on implementation
	   

    // Tasks
	public static final String SETTING_TASKCHOICE = "taskChoice";
	public static final String SETTING_TASKCHOICE_DESCR = "Task choice";
	public static final String SETTING_TASKCHOICE_DISP = "Task:";
	public static final String UI_TASK_M23 = "M23";
	public static final String UI_TASK_M12 = "M12";
	public static final String UI_TASK_M12ref = "M12ref";
	public static final String UI_TASK_M01 = "M01";
	public static final String UI_TASK_RCA0 = "RCA0";
	public static final String UI_TASK_SKIP = "skip";
	public static final String UI_TASK_LSD = "LSD";
	// These are deprecated
	public static final String UI_TASK_LOCALNEIGHBORHOOD = "localNeighborhood";
	public static final String UI_TASK_COMPUTEPPOINTCLOUDFROMTIMESERIES = 
			"computePointcloudFromTimeSeries";
	public static final String UI_TASK_LOCALSPHERICALDISTANCE = "localSphericalDistance";
	public static final String UI_TASK_LOCALSPHERICALDISTANCEMATRIX = 
			"localSphericalDistanceMatrix";
	
	// -----
	
    // Metric
	public static final String SETTING_METRICCHOICE = "metricChoice";
	public static final String SETTING_METRICCHOICE_DESCR = "Metric choice";
	public static final String SETTING_METRICCHOICE_DISP = "Metric:";
	public static final String UI_METRIC_L1 = "L1";
	public static final String UI_METRIC_L2 = "L2";
	public static final String UI_METRIC_LP = "LP";
	public static final String UI_METRIC_LINF = "Linf";
	
	    
	// Statistics recording
	public static final String SETTING_STATISTICSCHOICE = "statisticsChoice";
	public static final String SETTING_STATISTICSCHOICE_DESCR = "Statistics choice";
	public static final String SETTING_STATISTICSCHOICE_DISP = "Statistics:";
	public static final String UI_RECORDER_STANDARD = "Standard";
	// Planned (but not yet implemented) choices
	public static final String UI_RECORDER_COMPACT = "Compact";
	public static final String UI_RECORDER_VERBOSE = "Verbose";
	public static final String SETTING_STATISTICDEFAULT = UI_RECORDER_STANDARD;

	// Displayed string when a core object's default value is used
	public static final String UI_DEFAULTEDTO_DISP = " defaulted to ";
	

	public static final String SETTING_INPUTDIRECTORY = "inputDirectory";
	public static final String SETTING_INPUTDIRECTORY_DESCR = "Input directory";
	public static final String SETTING_INPUTDIRECTORY_DISP = "Input directory:";
	// 5/19/2014	Based on larger Matlab distribution, change to bin/config directory
	public static final String DEFAULT_INPUTDIRECTORY = "bin/config";
//	public static final String DEFAULT_INPUTDIRECTORY = "tda/data";
//	public static final String DEFAULT_INPUTDIRECTORY = "data";
//	public static final String DEFAULT_INPUTDIRECTORY = "input";	

	public static final String SETTING_ERRORDIRECTORY = "errorDirectory";
	public static final String SETTING_ERRORDIRECTORY_DESCR = "Error directory";
	public static final String SETTING_ERRORFILE = "errorFile";
	public static final String DEFAULT_ERRORFILE = "error.txt";

    public static final String SETTING_OUTPUTDIRECTORY = "outputDirectory";
    public static final String SETTING_OUTPUTDIRECTORY_DESCR = "Output directory";
    public static final String SETTING_OUTPUTDIRECTORY_DISP = "Output directory:";
    public static final String DEFAULT_OUTPUTDIRECTORY = "output";

    public static final String SETTING_REPORTFILE = "reportFile";
    public static final String SETTING_REPORTFILE_DESCR = "Report file";
    public static final String SETTING_REPORTFILE_DISP = "Report file:";
    public static final String DEFAULT_REPORTFILE = "results.txt";
    public static final String DATA_REPORTFILE = "reportFile";

	public static final String SETTING_TRACKINGFILE = "trackingFile";
	public static final String SETTING_TRACKINGFILE_DESCR = "Tracking file";
	public static final String DEFAULT_TRACKINGFILE = "trace.txt";

	public static final String SETTING_SUMMARYFILE = "summaryFile";
	public static final String SETTING_SUMMARYFILE_DESCR = "Summary file";
	public static final String DEFAULT_SUMMARYFILE = "summary.txt";

    public static final String SETTING_XMLINPUTFILES = "xmlInputFiles";
    public static final String SETTING_XMLINPUTFILES_DESCR = "XML input files";
    public static final String SETTING_XMLINPUTFILES_DISP = "XML input files:";
    public static final String DATA_XMLINPUTFILES = "dataXmlInputFiles";

    public static final String SETTING_XMLINPUTDIRECTORY = "xmlInputDirectory";
    public static final String SETTING_XMLINPUTDIRECTORY_DESCR = "XML input directory";
    public static final String SETTING_XMLINPUTDIRECTORY_DISP = "XML input directory:";

    public static final String SETTING_XMLOUTPUTDIRECTORY = "xmlOutputDirectory";
    public static final String SETTING_XMLOUTPUTDIRECTORY_DESCR = "XML output directory";
    public static final String SETTING_XMLOUTPUTDIRECTORY_DISP = "XML output directory:";
    public static final String DEFAULT_XMLOUTPUTDIRECTORY = DEFAULT_OUTPUTDIRECTORY;

    public static final String SETTING_XMLREPORTFILE = "xmlReportFile";
    public static final String SETTING_XMLREPORTFILE_DESCR = "XML Report file";
    public static final String SETTING_XMLREPORTFILE_DISP = "XML Report file:";
    public static final String DEFAULT_XMLREPORTFILE = "results.xml";
    public static final String DATA_XMLREPORTFILE = "xmlReportFile";

    public static final String SETTING_XMLSETTINGSTOEXPORT = "xmlSettingsToExport";
    public static final String SETTING_XMLSETTINGSTOEXPORT_DESCR = "XML settings to export";
    public static final String SETTING_XMLSETTINGSTOEXPORT_DISP = "XML settings to export:";
    public static final String DATA_XMLSETTINGSTOEXPORT = "xmlSettingsToExport";
    public static final String UI_XMLSETTINGSTOEXPORT_ALL = "all";
    public static final String DATA_XMLPREFIXSPACER = "\t";
    
    public static final String SETTING_SUPPRESSALLOUTPUT = "suppressAllOutput";
    public static final String SETTING_SUPPRESSALLOUTPUT_DESCR = "Suppress all output";
    public static final String SETTING_SUPPRESSALLOUTPUT_DISP = "Suppress all output:";
    public static final String UI_SUPPRESSALLOUTPUT_YES = "yes";
    public static final String UI_SUPPRESSALLOUTPUT_NO = "no";
    public static final String UI_SUPPRESSALLOUTPUT_DEFAULT = UI_SUPPRESSALLOUTPUT_NO;
    
    
    public static final String SETTING_DISPLAYMEMORYINFO = "displayMemoryInfo";
    public static final String SETTING_DISPLAYMEMORYINFO_DESCR = "Display memory info";
    public static final String SETTING_DISPLAYMEMORYINFO_DISP = "Display memory info:";
    public static final String UI_DISPLAYMEMORYINFO_YES = "yes";
    public static final String UI_DISPLAYMEMORYINFO_NO = "no";
    public static final String DEFAULT_DISPLAYMEMORYINFO = UI_DISPLAYMEMORYINFO_NO;
    public static final String DATA_MEMORYINFO = "Memory info:";
    public static final String DATA_MEMORYINFOBEFORESTART = 
    	"Memory info before starting the program execution:";
    public static final String DATA_MEMORYINFOATFINISH = 
    	"Memory info after completing the program execution:";
    public static final String DATA_FINALREPORT = "Final report";
	
	public static final String SETTING_DISPLAYDEBUGINFO = 
	    "displayDebugInfo";
	public static final String SETTING_DISPLAYDEBUGINFO_DESCR = 
	    "Display debug info";
	public static final String SETTING_DISPLAYDEBUGINFO_DISP = 
	    "Display debug info:";
	public static final String UI_DEBUGINFO_STACKTRACE = 
	    "stackTrace";
	public static final String UI_DEBUGINFO_NONE = 
	    "none";
	public static final String SETTING_DEFAULT_DEBUGINFO = 
	    UI_DEBUGINFO_NONE;
	
	
	// Standard algorithms do not lend themselves to the use of threads,
	// but work flows and special task do. [not yet implemented]
    public static final String SETTING_THREADS = "threads";
    public static final String SETTING_THREADS_DESCR = "Number of threads";
    public static final String SETTING_THREADS_DISP = "Number of threads:";
    public static final String DATA_THREADNAME = "TDA thread #";
    //
    public static final String DATA_MAXTHREADS = "maxThreads";
    public static final String DATA_THREADINDEX = "threadIndex";
    public static final String DATA_THREADINDEX_DISP = "Index of current thread:";
    public static final String DATA_THREAD_FILEIDENTIFIER = "thread=";
    public static final int SETTING_DEFAULT_THREADS = 1;
    //
    public static final String SETTING_FILENAMEPREFIXFORTHREADS = "fileNamePrefixForThreads";
    public static final String SETTING_FILENAMEPREFIXFORTHREADS_DESCR = 
        "file name prefix for identifying threads";
    public static final String SETTING_FILENAMEPREFIXFORTHREADS_DISP = 
        "File name prefix for identifying threads:";
    public static final String SETTING_FILENAMEPREFIXFORTHREADS_DEFAULT = 
        "thread=@threadid@_";
    
	//----------------------------------------
	// Validation  and related error constants
	//----------------------------------------

	public static final String DATA_SETTINGNOTFOUND = "setting not found";
	public static final String DATA_SETTINGNOVALUEPROVIDED = "no value provided";
	public static final String DATA_SETTINGINVALIDVALUE = "invalid setting value";

	// This string is used in the error message when no task is specified by the user
	public static final String DATA_SETTINGNODEFAULTVALUE = "no value found";
	public static final String DATA_SETTINGDEFAULTVALUE_EMPTY = "";
	
	// Valid data types for settings validation
	public static final String VALIDATION_DATATYPE_STRING = "String";
	public static final String VALIDATION_DATATYPE_INTEGER = "Integer";
	public static final String VALIDATION_DATATYPE_INTEGERLIST = "IntegerList";
	public static final String VALIDATION_DATATYPE_LONG = "Long";
	public static final String VALIDATION_DATATYPE_DOUBLE = "Double";
	public static final String VALIDATION_DATATYPE_DOUBLELIST = "DoubleList";
	public static final String VALIDATION_DATATYPE_TIME = "Time";
	public static final String VALIDATION_DATATYPE_TIMESTAMP = "Timestamp";
    // -- Not yet supported data types: (use string instead for now)
    public static final String VALIDATION_DATATYPE_FILE = "File";
    public static final String VALIDATION_DATATYPE_DIRECTORY = "Directory";
    public static final String VALIDATION_DATATYPE_FILEPATH = "Path";
    // --
	
	// Basic types of validation
	public static final int VALIDATIONTYPE_MANDATORY = 1;
	public static final int VALIDATIONTYPE_OPTIONAL = 2;
	public static final int VALIDATIONTYPE_BASICRANGE = 3;
	public static final int VALIDATIONTYPE_RULE = 4;
	
	// Error types for validation, and their descriptive strings
	public static final int ERRORTYPE_MISSINGVALUE = 101;
	public static final String ERRORDESCRIPTION_MISSINGVALUE =
	    "Missing value of required setting";

	public static final int ERRORTYPE_MISSINGMANDATORYVALUE = 102;
	public static final String ERRORDESCRIPTION_MISSINGMANDATORYVALUE = 
	    "Missing mandatory value";

	public static final int ERRORTYPE_MISMATCHEDDATATYPE = 103;
	public static final String ERRORDESCRIPTION_MISMATCHEDDATATYPE = 
	    "Data is of unexpected type";
	
	public static final int ERRORTYPE_INVALIDRANGE = 104;
	public static final String ERRORDESCRIPTION_INVALIDRANGE = 
	    "Value out of accepted range";
	
	public static final int ERRORTYPE_RULEVIOLATION = 105;
	public static final String ERRORDESCRIPTION_RULEVIOLATION = 
	    "Rule violation";
    
    public static final int ERRORTYPE_INVALIDCHOICE = 106;
    public static final String ERRORDESCRIPTION_INVALIDCHOICE = 
        "Invalid setting choice";
    
    public static final int ERRORTYPE_INVALIDPATH = 107;
    public static final String ERRORDESCRIPTION_INVALIDPATH = 
        "Invalid file or directory path";
	
	public static final int ERRORTYPE_OTHER = 110;
	public static final String ERRORDESCRIPTION_OTHER = 
	    "Other error";
	
	public static final int ERRORTYPE_DOTINTERRUPTION = 120;
	public static final String ERRORDESCRIPTION_DOTINTERRUPTION = 
	    "Interruption of 'dot'";
    
    public static final int ERRORTYPE_DOTEXECUTION = 121;
    public static final String ERRORDESCRIPTION_DOTEXECUTION = 
        "'dot' execution";
    
    public static final int ERRORTYPE_POSTPROCESSING = 130;
    public static final String ERRORDESCRIPTION_POSTPROCESSING = 
        "Post-processing";
    
    public static final int ERRORTYPE_ALERT_OTHER = 200;
    public static final String ERRORDESCRIPTION_ALERT_OTHER =
        "Alert: (general)";
    
    public static final int ERRORTYPE_ALERT_CORRECTEDCHOICE = 201;
    public static final String ERRORDESCRIPTION_ALERT_CORRECTEDCHOICE =
        "Alert: invalid setting choice was corrected.";
        
    public static final int ERRORTYPE_ALERT_DEPRECATEDSETTING = 203;
    public static final String ERRORDESCRIPTION_ALERT_DEPRECATEDSETTING =
        "Alert: deprecated setting.";
    
    public static final int ERRORTYPE_ALERT_UNKNOWNSETTING = 204;
    public static final String ERRORDESCRIPTION_ALERT_UNKNOWNSETTING =
        "Alert: unknown setting.";
    
    public static final int ERRORTYPE_ALERT_DEFAULTAPPLIED = 205;
    public static final String ERRORDESCRIPTION_ALERT_DEFAULTAPPLIED =
        "Alert: default applied.";
	
	public static final int ERRORTYPE_WARNING_INVALIDCHOICE = 306;
	public static final String ERRORDESCRIPTION_WARNING_INVALIDCHOICE =
	    "Warning: Invalid setting choice";
	
	public static final int ERRORTYPE_WARNING_OTHER = 307;
	public static final String ERRORDESCRIPTION_WARNING_OTHER = 
	    "Warning (dev)";
    
    public static final int ERRORTYPE_ALERT_DEV = 308;
    public static final String ERRORDESCRIPTION_ALERT_DEV =
        "Alert: (dev)";
	
	// ---------------
	// General input
	// ---------------

	// 
	public static final String SETTING_COMPUTELOCALNHOOD = "computeLocalNeighborhoodBy";
	public static final String SETTING_COMPUTELOCALNHOOD_DESCR = "Compute local neighborhood by";
	public static final String SETTING_COMPUTELOCALNHOOD_DISP = "Compute local neighborhood by:"; 
	// valid values that can be used for computing the local neighborhood:
	public static final String UI_COMPUTELOCALNHOOD_DISTANCE = "distance";
	public static final String UI_COMPUTELOCALNHOOD_NUMBEROFPOINTS = "numberOfPoints"; 
	// 
	public static final String SETTING_SUPPLYDATAAS = "supplyDataAs";
	public static final String SETTING_SUPPLYDATAAS_DESCR = "Supply data as";
	public static final String SETTING_SUPPLYDATAAS_DISP = "Supply data as:";
	// for use in some workflows
	public static final String DATA_SUPPLYDATAAS = "supplyDataAs"; 
	// valid values that can be used for the supplied data:
	public static final String UI_SUPPLYDATAAS_POINTCLOUD = "pointCloud";
	public static final String UI_SUPPLYDATAAS_DISTANCEMATRIX = "distanceMatrix";
//	public static final String UI_SUPPLYDATAAS_SPARSEMATRIX = "sparseMatrix";
	// hjs 6/2/2014 request by Paul and Rann to use instead:
	// (because technically, our 'sparseMatrix' is not a sparse matrix)
	public static final String UI_SUPPLYDATAAS_SPARSEMATRIX = "monotonicFunctionOnSimplicialComplexMatrix";
	// and because this long term is, well, a little silly, add this equivalent one:
	public static final String UI_SUPPLYDATAAS_SPARSEMATRIX2 = "compact";
	public static final String UI_SUPPLYDATAAS_OBJECT = "object";
	// 
	public static final String SETTING_P_VALUE_FOR_LP = "p_valueForLpMetric";
	public static final String SETTING_P_VALUE_FOR_LP_DESCR = "p-value for Lp metric";
	public static final String SETTING_P_VALUE_FOR_LP_DISP = "p-value for Lp metric:";
	// 
	public static final String SETTING_ZP_VALUE = "zp_value";
	public static final String SETTING_ZP_VALUE_DESCR = "Zp-value";
	public static final String SETTING_ZP_VALUE_DISP = "Zp-value:";
	// 
	public static final String SETTING_POINTSPERNEIGHBORHOOD = "pointsPerLocalNeighborhood";
	public static final String SETTING_POINTSPERNEIGHBORHOOD_DESCR = "Points per local neighborhood";
	public static final String SETTING_POINTSPERNEIGHBORHOOD_DISP = "Points per local neighborhood:";
	// 
	public static final String SETTING_DISTANCEBOUND = "distanceBoundOnEdges";
	public static final String SETTING_DISTANCEBOUND_DESCR = "Distance bound on edges";
	public static final String SETTING_DISTANCEBOUND_DISP = "Distance bound on edges:";
	// 
	public static final String SETTING_RADIUSFORLOCALNEIGHBORHOOD = 
		"radiusBoundForLocalNeighborhood";
	public static final String SETTING_RADIUSFORLOCALNEIGHBORHOOD_DESCR = 
		"Radius bound for local neighborhood";
	public static final String SETTING_RADIUSFORLOCALNEIGHBORHOOD_DISP = 
		"Radius bound for local neighborhood:";
	// 
	public static final String SETTING_RADIUSFORSPHERICALDISTANCE = 
		"radiusBoundForSphericalDistance";
	public static final String SETTING_RADIUSFORSPHERICALDISTANCE_DESCR = 
		"Radius bound for spherical distance";
	public static final String SETTING_RADIUSFORSPHERICALDISTANCE_DISP = 
		"Radius bound for spherical distance:";
	// 
	public static final String SETTING_NUMBEROFPOINTS = "numberOfPoints";
	public static final String SETTING_NUMBEROFPOINTS_DESCR = "Number of points";
	public static final String SETTING_NUMBEROFPOINTS_DISP = "Number of points:";
	
	public static final String SETTING_POINTDIMENSION = "pointDimension";
	public static final String SETTING_POINTDIMENSION_DESCR = "Point dimension";
	public static final String SETTING_POINTDIMENSION_DISP = "Point dimension:";
//	public static final String DATA_POINTDIMENSION = "pointDimension";
	public static final int UNKNOWNPOINTDIMENSION = -1;
	public static final String DATA_UNKNOWNPOINTDIMENSION = "Unknown";
	
	// 
	public static final String SETTING_ENTITYTYPE = "entityType";
	public static final String SETTING_ENTITYTYPE_DESCR = "Entity type";
	public static final String SETTING_ENTITYTYPE_DISP = "Entity type:";
	// 
	public static final String SETTING_NUMBEROFENTITIES = "numberOfEntities";
	public static final String SETTING_NUMBEROFENTITIES_DESCR = "Number of entities";
	public static final String SETTING_NUMBEROFENTITIES_DISP = "Number of entities:";
	// 
	public static final String SETTING_HORIZONTALOFFSET = "horizontalOffset";
	public static final String SETTING_HORIZONTALOFFSET_DESCR = "Horizontal offset";
	public static final String SETTING_HORIZONTALOFFSET_DISP = "Horizontal offset:";

	// for loading a time series from a file 
	public static final String SETTING_TIMESERIESFILE = "timeSeriesFile";
	public static final String SETTING_TIMESERIESFILE_DESCR = "Time series file";
	public static final String SETTING_TIMESERIESFILE_DISP = "Time series file:";
    public static final String DEFAULT_TIMESERIESFILE = "timeSeries.txt";
    public static final String DATA_TIMESERIESFILE = "timeSeriesFile";
	// 
    

	// 
	public static final String SETTING_CENTERPOINTFORLOCALNBHD = 
		"centerPointForLocalNeighborhood";
	public static final String SETTING_CENTERPOINTFORLOCALNBHD_DESCR = 
		"Center point for local neighborhood";
	public static final String SETTING_CENTERPOINTFORLOCALNBHD_DISP = 
		"Center point for local neighborhood:";
    
    
	public static final String SETTING_CENTERPOINTSFORLOCALNBHDFILE = 
		"centerPointsForLocalNeighborhoodsFile";
	public static final String SETTING_CENTERPOINTSFORLOCALNBHDFILE_DESCR = 
		"Center points for local neighborhoods file";
	public static final String SETTING_CENTERPOINTSFORLOCALNBHDFILE_DISP = 
		"Center points for local neighborhoods file:";
    public static final String DEFAULT_CENTERPOINTSFORLOCALNBHDFILE = 
    	"centerPointsForLocalNbhd.txt";
    public static final String DATA_CENTERPOINTSFORLOCALNBHDFILE = 
        	"centerPointsForLocalNeighborhoodsFile";
    
    
    public static final String SETTING_CENTERPOINTFORLPH = 
        	"centerPointForLPH";
    
	// For now: this is for loading the point cloud from a file:
	public static final String SETTING_POINTCLOUDLOCALNBHDFILE = 
		"pointCloudForLocalNeighborhoodsFile";
	public static final String SETTING_POINTCLOUDLOCALNBHDFILE_DESCR = 
		"Point cloud for local neighborhoods file";
	public static final String SETTING_POINTCLOUDLOCALNBHDFILE_DISP = 
		"Point cloud for local neighborhoods file:";
    public static final String DEFAULT_POINTCLOUDLOCALNBHDFILE = 
    	"pointcloudForLocalNeighborhoods.txt";
    public static final String DATA_POINTCLOUDLOCALNBHDFILE = 
    	"pointCloudForLocalNeighborhoodsFile";
    public static final String DATA_POINTCLOUDLOCALNBHD_I = 
    	"indexOfNbhd";
    public static final String DATA_NUMBEROFLOCALNBHDS = 
    	"numberOfLocalNbhds";
    
	// For now: this is for loading the point cloud from a file:
	public static final String SETTING_POINTCLOUDSPHDISTMATRIXFILE = 
		"pointcloudForSphDistMatrixFile";
	public static final String SETTING_POINTCLOUDSPHDISTMATRIXFILE_DESCR = 
		"Point cloud for spherical distance matrix file";
	public static final String SETTING_POINTCLOUDSPHDISTMATRIXFILE_DISP = 
		"Point cloud for spherical distance matrix file:";
    public static final String DEFAULT_POINTCLOUDSPHDISTMATRIXFILE = 
    	"pointcloudForSphDistMatrix.txt";
    public static final String DATA_POINTCLOUDSPHDISTMATRIXFILE = 
    	"pointcloudForSphDistMatrixFile";

	public static final String SETTING_CONVERTDATATOSPARSEMATRIXFORMAT = "convertDataToSparseMatrixFormat";
	public static final String SETTING_CONVERTDATATOSPARSEMATRIXFORMAT_DESCR = "Convert data to sparse matrix format";
	public static final String SETTING_CONVERTDATATOSPARSEMATRIXFORMAT_DISP = "Convert data to sparse matrix format";
	public static final String UI_CONVERTDATATOSPARSEMATRIXFORMAT_YES = "yes";
	public static final String UI_CONVERTDATATOSPARSEMATRIXFORMAT_NO = "no";
	public static final String UI_CONVERTDATATOSPARSEMATRIXFORMAT_DEFAULT = 
		UI_CONVERTDATATOSPARSEMATRIXFORMAT_NO;

	public static final String SETTING_SPARSEMATRIXFILEOUTPUT = "sparseMatrixOutputFile";
	public static final String SETTING_SPARSEMATRIXFILEOUTPUT_DESCR = "Sparse Matrix Output File";
	public static final String SETTING_SPARSEMATRIXFILEOUTPUT_DISP = "Sparse Matrix Output File:";
    public static final String DEFAULT_SPARSEMATRIXFILEOUTPUT = "sparseMatrixData.txt";


	public static final String SETTING_CONVERTDATATODISTANCEMATRIXFORMAT = "convertDataToDistanceMatrixFormat";
	public static final String SETTING_CONVERTDATATODISTANCEMATRIXFORMAT_DESCR = "Convert data to distance matrix format";
	public static final String SETTING_CONVERTDATATODISTANCEMATRIXFORMAT_DISP = "Convert data to distance matrix format";
	public static final String UI_CONVERTDATATODISTANCEMATRIXFORMAT_YES = "yes";

	public static final String SETTING_DISTANCEMATRIXFILEOUTPUT = "distanceMatrixOutputFile";
	public static final String SETTING_DISTANCEMATRIXFILEOUTPUT_DESCR = "Distance Matrix Output File";
	public static final String SETTING_DISTANCEMATRIXFILEOUTPUT_DISP = "Distance Matrix Output File:";
    public static final String DEFAULT_DISTANCEMATRIXFILEOUTPUT = "distanceMatrixData.txt";
    
    
	// For now: this is for loading the point cloud from a file:
	public static final String SETTING_POINTCLOUDFILE = "pointCloudFile";
	public static final String SETTING_POINTCLOUDFILE_DESCR = "Point cloud file";
	public static final String SETTING_POINTCLOUDFILE_DISP = "Point cloud file:";
    public static final String DEFAULT_POINTCLOUDFILE = "pointcloud.txt";
    public static final String DATA_POINTCLOUDFILE = "pointCloudFile";
    public static final String DATA_POINTCLOUDDIRECTORY = "pointCloudDirectory";
    // special settings for the time series work flow
    public static final String DATA_POINTCLOUDFILEFROMTIMESERIES = 
    	"pointCloudFileFromTimeSeries";
    public static final String DATA_POINTCLOUDDIRECTORYFROMTIMESERIES = 
    	"pointCloudDirectoryFromTimeSeries";
    // special settings for the local neighborhood-spherical distance matrix work flow
    public static final String DATA_POINTCLOUDFILEFROMLOCALNBHD = 
    	"pointCloudFileFromLocalNbhd";
    public static final String DATA_POINTCLOUDDIRECTORYLOCALNBHD = 
    	"pointCloudDirectoryFromLocalNbhd";
    public static final String DATA_POINTCLOUDFILEFROMSPHDISTMAT = 
    	"pointCloudFileFromSphDistMat";
    public static final String DATA_POINTCLOUDDIRECTORYSPHDISTMAT = 
    	"pointCloudDirectoryFromSphDistMat";

	public static final String SETTING_POINTCLOUDASSTRING = "pointCloudAsString";
	public static final String SETTING_POINTCLOUDASSTRING_DESCR = "Point cloud as string";
	public static final String SETTING_POINTCLOUDASSTRING_DISP = "Point cloud as string:";

	public static final String SETTING_DISTANCEMATRIXASSTRING = "distanceMatrixAsString";
	public static final String SETTING_DISTANCEMATRIXASSTRING_DESCR = "Distance matrix as string";
	public static final String SETTING_DISTANCEMATRIXASSTRING_DISP = "Distance matrix as string:";

	public static final String SETTING_DATALOADEDASARRAY = "dataLoadedAsArray";
	public static final String SETTING_DATALOADEDASARRAY_DESCR = "Data loaded as array";
	public static final String SETTING_DATALOADEDASARRAY_DISP = "Data loaded as array:";
    //
    // Save the persistence intervals-only data
    // the 0-dimensional
	public static final String SETTING_0DINTERVALSFILE = "0DintervalsFile";
	public static final String SETTING_0DINTERVALSFILE_DESCR = "0D-intervals file";
	public static final String SETTING_0DINTERVALSFILE_DISP = "0D-intervals file:";
    public static final String DEFAULT_0DINTERVALSFILE = "0Dintervals.txt";
    public static final String DATA_0DINTERVALSFILE = "0DintervalsFile";
    // the 1-dimensional
	public static final String SETTING_1DINTERVALSFILE = "1DintervalsFile";
	public static final String SETTING_1DINTERVALSFILE_DESCR = "1D-intervals file";
	public static final String SETTING_1DINTERVALSFILE_DISP = "1D-intervals file:";
    public static final String DEFAULT_1DINTERVALSFILE = "1Dintervals.txt";
    public static final String DATA_1DINTERVALSFILE = "1DintervalsFile";
    // Save the persistence intervals and generators data
	public static final String SETTING_1DINTERVALSGENSFILE = "1DIntervalsAndRepsFile";
	public static final String SETTING_1DINTERVALSREPSFILE_DESCR = "1D-intervals-and-reps file";
	public static final String SETTING_1DINTERVALSREPSFILE_DISP = "1D-intervals-and-reps file:";
    public static final String DEFAULT_1DINTERVALSREPSFILE = "1DintervalsAndReps.txt";
    public static final String DATA_1DINTERVALSREPSFILE = "1DintervalsAndRepsFile";

    // the 0-dimensional diagram file
	public static final String SETTING_0DDIAGRAMFILE = "0DdiagramFile";
	public static final String SETTING_0DDIAGRAMFILE_DESCR = "0D-diagram file";
	public static final String SETTING_0DDIAGRAMFILE_DISP = "0D-diagram file:";
    public static final String DEFAULT_0DDIAGRAMFILE = "0Ddiagram.txt";
    public static final String DATA_0DDIAGRAMFILE = "0DdiagramFile";
    public static final String DATA_0DXCOORDS = "0DxCoords";
    public static final String DATA_0DYCOORDS = "0DyCoords";
    public static final String DATA_1DINTERVALS = "1Dintervals";
    // the 1-dimensional diagram file
	public static final String SETTING_1DDIAGRAMFILE = "1DdiagramFile";
	public static final String SETTING_1DDIAGRAMFILE_DESCR = "1D-diagram file";
	public static final String SETTING_1DDIAGRAMFILE_DISP = "1D-diagram file:";
    public static final String DEFAULT_1DDIAGRAMFILE = "1Ddiagram.txt";
    public static final String DATA_1DDIAGRAMFILE = "1DdiagramFile";
    public static final String DATA_1DXCOORDS = "1DxCoords";
    public static final String DATA_1DYCOORDS = "1DyCoords";
	
	// 
	public static final String SETTING_TIMESERIES = "timeSeries";
	public static final String SETTING_TIMESERIES_DESCR = "Time series values";
	public static final String SETTING_TIMESERIES_DISP = "Time series values:";
	// 
	public static final String SETTING_TIMESERIESPOINTCOUNT = "timeseriesPointCount";
	public static final String SETTING_TIMESERIESPOINTCOUNT_DESCR = "Time series point count";
	public static final String SETTING_TIMESERIESPOINTCOUNT_DISP = "Number of points in time series:";
	// 
	public static final String SETTING_POINTCLOUDDIMENSION = "pointCloudDimension";
	public static final String SETTING_POINTCLOUDDIMENSION_DESCR = "Point cloud dimension";
	public static final String SETTING_POINTCLOUDDIMENSION_DISP = "Dimension of the point cloud:";
	
	

	public static final String SETTING_SWITCHTOSPARSEAT = "switchToSparseAt";
	public static final String SETTING_SWITCHTOSPARSEAT_DESCR = "switch to sparse at";
	public static final String SETTING_SWITCHTOSPARSEAT_DISP = "Switch to sparse at:";
	// This is the value at which we switch over from using the distance matrix (better speed, but
	// much larger storage requirement) to a sparse matrix representation -- if the user didn't
	// specify a value for SETTING_SWITCHTOSPARSEAT:
	public static final int DEFAULT_SWITCHTOSPARSEAT = 1000;
	
	// Template for output of persistence diagrams via Python's MatPlotLib 
	// (disabled, and very likely superseeded by drawing using Matlab)
    public static final String DATA_PY_DRAWPERSDIAGRAMS = 
			"#!/usr/bin/python\n\n" +
			"# " + APPLICATION_NAME + "   --   " + APPLICATION_NAME_LONG +  "\n" +
			"# Visualization code based on Python's MatPlotLib \n" +
			"# Version " + APPLICATION_VERSIONNUMBER + "\n" +
	//		"# " + RELEASE_LICENCED + "\n" +
			"# " + RELEASE_COPYRIGHT + "\n" +
			"# " + RELEASE_ALLRIGHTS + "\n\n" +
		    "import matplotlib.pyplot as plt  \n" +
		    "import matplotlib.lines \n" +
		    "import numpy as np\n" +
		    "\n" +
		    "# Results from the TDA computations: \n" +
		    "xCoords = @xCoords@\n" +
		    "yCoords = @yCoords@\n\n" +
		    "xCoords0D = @xCoords0D@\n" +
		    "yCoords0D = @yCoords0D@\n" +
		    "\n" +
		    "# Get the largest x value, but no smaller than 1\n" +
		    "#maxCoords = max( max( xCoords ), 1 )\n" +
		    "# or (more general, in case most values are <<1):\n" +
		    "maxCoords = max( max( xCoords ), max( yCoords ) ) * 1.1" +
		    "\n" +
		    "rangeXY = maxCoords\n" +
		    "# Set the INF value to a little bigger than the max x coordinate\n" +
		    "epsInf = min( 0.15, maxCoords*0.2 )\n" +
		    "INF = maxCoords + epsInf\n" +
		    "\n" +
		    "# Set the range for drawing the center line\n" +
		    "rangeXY = maxCoords\n" +
		    "\n" +
		    "# Set a small buffer for displaying the points 'that don't die'\n" +
		    "eps = epsInf + min( 0.15, maxCoords*0.2 )\n" +
		    "\n" +
		    "# Plot 1-dim, with 'ro' = red circles\n" +
		    "plt.plot([xCoords],[yCoords], 'ro')\n" +
		    "\n" +
		    "# plot 0-dim, with blue circles\n" +
		    "plt.plot([xCoords0D],[yCoords0D], 'bo')\n" +
		    "\n" +
		    "# Set the label and end points for the center line\n" +
		    "plt.ylabel('TDA, 0 and 1-dim persistence')\n" +
		    "X = np.array([0, rangeXY])\n" +
		    "Y = np.array([0, rangeXY])\n" +
		    "\n" +
		    "# Draw the center line\n" +
		    "plt.plot(X,Y)\n" +
		    "\n" +
		    "# Make size slightly bigger than largest coordinate values\n" +
		    "plt.axis([-0.05, rangeXY + eps, -0.05, rangeXY + eps])\n" +
		    "# Save image in png format\n" +
		    "plt.savefig( '@1DFileName@' )\n" +
		    "\n" +
		    "# (Optionally) Display the image in a viewer \n" +
		    "#plt.show()\n";
	
	    
    
	public static final String SETTING_MAXTHREADS = "threadCount";

	
    public static final String UI_ENTITY_CIRCLE = 
        "circle";
    public static final String UI_ENTITY_DIAMOND = 
        "diamond";
    public static final String DEFAULT_ENTITY = 
    	UI_ENTITY_CIRCLE;
    
    
            
    // ---------------------
    // Project related input
    // ---------------------
    
    // All input is treated as string and not validated
    public static final String SETTING_PROJECT = "project";
    public static final String SETTING_PROJECT_DESCR = "Project";
    public static final String SETTING_PROJECT_DISP = "Project:";

    public static final String SETTING_USER = "user";
    public static final String SETTING_USER_DESCR = "User";
    public static final String SETTING_USER_DISP = "User:";
    
    public static final String SETTING_DATASET = "dataset";
    public static final String SETTING_DATASET_DESCR = "Dataset";
    public static final String SETTING_DATASET_DISP = "Dataset:";
    
    public static final String SETTING_NOTES = "notes";
    public static final String SETTING_NOTES_DESCR = "Notes";
    public static final String SETTING_NOTES_DISP = "Notes:";

    // -----------------------
    // Output-related settings
    //------------------------
    

	// -----------------------
	// Post-processing options
	// -----------------------
    // displayStatistics is not implemented yet
	public static final String SETTING_DISPLAYSTATISTICS = "displayStatistics";
	public static final String SETTING_DISPLAYSTATISTICS_DESCR = "Display statistics";
	public static final String SETTING_DISPLAYSTATISTICS_DISP = "Display statistics:";
	public static final String UI_DISPLAYSTATISTICS_YES = "yes";
	public static final String UI_DISPLAYSTATISTICS_NO = "no";
	public static final String DEFAULT_DISPLAYSTATISTICS = UI_DISPLAYSTATISTICS_NO;
	//
	//
	public static final String SETTING_DOTGRAPHICSFORMAT = "dotGraphicsFormat";
	public static final String SETTING_DOTGRAPHICSFORMAT_DESCR = "'dot' graphics format";
	public static final String SETTING_DOTGRAPHICSFORMAT_DISP = "'dot' graphics format:";
	// Supported graphics formats for dot
    public static final String UI_DOTFORMAT_CANON = "canon";
    public static final String UI_DOTFORMAT_DOT = "dot";
    public static final String UI_DOTFORMAT_FIG = "fig";
    public static final String UI_DOTFORMAT_GD = "gd";
    public static final String UI_DOTFORMAT_GIF = "gif";
    public static final String UI_DOTFORMAT_HPGL = "hpgl";
    public static final String UI_DOTFORMAT_IMAP = "IMAP";
    public static final String UI_DOTFORMAT_CMAP = "CMAP";
    public static final String UI_DOTFORMAT_JPG = "jpg";
    public static final String UI_DOTFORMAT_MIF = "mif";
    public static final String UI_DOTFORMAT_MP = "mp";
    public static final String UI_DOTFORMAT_PCL = "pcl";
    public static final String UI_DOTFORMAT_PIC = "pic";
    public static final String UI_DOTFORMAT_PLAIN = "plain";
    public static final String UI_DOTFORMAT_PNG = "png";
    public static final String UI_DOTFORMAT_PS = "ps";
    public static final String UI_DOTFORMAT_PS2 = "ps2";
    public static final String UI_DOTFORMAT_SVG = "svg";
    public static final String UI_DOTFORMAT_VRML = "vrml";
    public static final String UI_DOTFORMAT_VTX = "vtx";
    public static final String UI_DOTFORMAT_VBMP = "vbmp";
	public static final String DEFAULT_DOTGRAPHICSFORMAT = UI_DOTFORMAT_PNG;
	//
	public static final String SETTING_DOTFILEEXTENSION = "dotFileExtension";
	public static final String SETTING_DOTFILEEXTENSION_DESCR = "'dot' file extension";
	public static final String SETTING_DOTFILEEXTENSION_DISP = "'Dot' file extension:";
	public static final String DEFAULT_DOTFILEEXTENSION = "txt";
	//
	public static final String SETTING_HTMLFILEEXTENSION = "htmlFileExtension";
	public static final String SETTING_HTMLFILEEXTENSION_DESCR = "HTML file extension";
	public static final String SETTING_HTMLFILEEXTENSION_DISP = "HTML file extension:";
	public static final String DEFAULT_HTMLFILEEXTENSION = "html";

	// Containers that enable us to provide a certain order of the items in our feedback display
	public static final String DATA_INFO_SPECIFICALGORITHM = "InfoForSpecificAlgorithm";
	public static final String DATA_INFO_COREOBJECTS = "InfoCoreObjects";
	public static final String DATA_INFO_STATISTICS = "InfoStatistics";
    public static final String DATA_INFO_FEEDBACK_1 = "InfoFeedback";
    public static final String DATA_INFO_FEEDBACK_2 = "InfoFeedback2";
    
    //
	public static final String DATA_ALGORITHMINFO_SPECIFICALGORITHM = "InfoForSpecificAlgorithm";
	public static final String DATA_ALGORITHMINFO_COREOBJECTS = "AlgorithmInfoCoreObjects";
	public static final String DATA_ALGORITHMINFO_STATISTICS = "AlgorithmInfoStatistics";
    public static final String DATA_ALGORITHMINFO_FEEDBACK_1 = "AlgorithmInfoFeedback";
    public static final String DATA_ALGORITHMINFO_FEEDBACK_2 = "AlgorithmInfoFeedback2";
	
	
	//----------------------------------------------
	// Codes and strings used for EXCEPTION handling
	//----------------------------------------------

    // Internal errors, likely due to development issues
    public static final int ERROR_APP_DEV = 1;

    // Input-related errors
    public static final int ERROR_APP_USERINPUT = 100;
    
    // XML-processing-related errors
    public static final int ERROR_APP_XML = 110;
    
    // 1/18/2006 (v2.0) hjs
    // This is a special "error": it is used as part of our special error tracking
    // mechanism to trigger the processing of any encountered error to this point.
    public static final int ERROR_CHECKPOINTTRIGGER = 500;
    
    // This is another special error, for handling the out-of-memory scenario:
    public static final int ERROR_APP_OUTOFMEMORY = 1000;
    //
    public static final String ERRORMSG_APP_OUTOFMEMORY_1 = 
        "[Unrecoverable runtime error: out of memory]";
    public static final String ERRORMSG_APP_OUTOFMEMORY_2 = 
        "TDA's memory requirements during the search execution exceeded " +
        "its maximum alloted memory of ";
    public static final String ERRORMSG_APP_OUTOFMEMORY_3a = 
        "Although the search cannot be continued, TDA will try to " +
        "display as much information as possible about the obtained results.";
    public static final String ERRORMSG_APP_OUTOFMEMORY_3b = 
        "In addition, TDA will attempt to complete as many post-processing " +
        "options as possible.";
    public static final String ERRORMSG_APP_OUTOFMEMORY_3 = 
        ERRORMSG_APP_OUTOFMEMORY_3a + 
        FEEDBACK_NEWLINE +
        ERRORMSG_APP_OUTOFMEMORY_3b;
    
	public static final String ERRORMESSAGEDISPLAYPREFIX = "\n- ";
	
	// Errors-related strings
	public static final String ERRORMSG_MISSING_SETTINGSFILE = 
		"The main settings file could not be loaded.";
	public static final String ERRORMSG_INVALIDTASK = 
		"No valid task was specified in the settings file.";
	public static final String ERRORMSG_NULL_TASK = 
		"The application was unable to create a task object.";
	public static final String ERRORMSG_MISSINGOBSERVATIONS = 
		"ObservationsAsMatrix file cannot be found.";
	public static final String ERRORMSG_MISSINGSTRUCTUREFILE = 
		"A user-specified structure file cannot be found.";
	public static final String ERRORMSG_MISSINGFILE = 
		"File cannot be found: ";
	public static final String ERRORMSG_COULDNOTFINDVALIDCHANGE = 
		"Tried unsuccessfully to find a valid bayesNetChange.";
	
	
	// -------------------------------------------
	// Regular expressions for pattern (e.g., whitespace) matching
	// -------------------------------------------

    // Common characters in data file
    public static final String DEFAULT_COMMENTINDICATOR = "#";
    public static final String DEFAULT_WILDCARDINDICATOR = "*";
    public static final String DEFAULT_TOKENINDICATOR = "@";
    public static final String DEFAULT_ITEMVALUESEPARATOR = "=";
    // Multiple white space characters between data entries:
    public static final String WHITESPACE_MULTIPLECHARACTERS = "\\s+";
    // Exactly one white space character between data entries:
    public static final String WHITESPACE_SINGLECHARACTER = "\\s";
    
    // Various delimiters
    public static final String DELIMITER_DEFAULT_LIST = ",";
    public static final String DELIMITER_DEFAULT_LIST_XML = ",";
    public static final String DELIMITER_DEFAULT_ITEM = ":";
    public static final String DELIMITER_DEFAULT_TIME = ":";
    public static final String DELIMITER_SPACE = " ";
    // Use this to store (e.g., in variable names processing) a comma-delimited list
    public static final String DELIMITER_SPECIAL = "@,@";
    // for observations
    public static final String DELIMITER_DEFAULT_MATRIXDATA = "\t";

    // -----------------------------------------------------------------------
    // Currently there is no user access to the patterns via the settings file
    // Create your own pattern here, if necessary:
    // -----------------------------------------------------------------------	
    
    // Exactly one white space character between data entries:
    public static final String DELIMITERSWITHOUTWHITESPACE = "[,:;]";
    
    // Multiple white space characters between data entries separated by ":":
    // (used for checking a list of variable names)
    public static final String DELIMITERCOLONWITHWHITESPACE = "\\s?[:]\\s?";
    
    // Multiple white space characters between data entries separated by ",":
    // (used for checking a list of variable names)
    public static final String DELIMITERCOMMAWITHWHITESPACE = "\\s?[,]\\s?";
	
	// Multiple white space characters plus a delimiter between data entries:
    public static final String DELIMITERSPLUSWHITESPACE = "\\s?[,:;]\\s?";
    public static final String DELIMITERSPLUSWHITESPACE2 = "\\s+[,:;]\\s+";

	// These are the constants used for parsing
	public static final String PATTERN_PARSING_WHITESPACE = WHITESPACE_MULTIPLECHARACTERS;
	
	// pattern for mandatory lag for parsing comma-delimited lists
    public static final String PATTERN_INTEGERLIST = "\\s?[,]\\s?";
    public static final String PATTERN_DOUBLELIST = "\\s?[,]\\s?";
    // pattern for parsing list of settings
    public static final String PATTERN_STRINGLIST = "\\s?[,]\\s?";
	
       
    // XML handling, added in version 2.2
    public static final String DATA_XMLPARSER_DISP = "XML parser:";
    public static final String DATA_XMLPARSERCHOICE = "org.apache.xerces.parsers.SAXParser";
    public static final int DATA_XMLTYPE_ELEMENT = 1;    

    // Controlling the seed for the random sequence
    public static final String SETTING_APPLICATIONSEED = "randomSeed";
    public static final String SETTING_APPLICATIONSEED_DESCR = "random seed";
    public static final String SETTING_APPLICATIONSEED_DISP = "Random seed:";
    public static final String DATA_APPLICATIONSEED = "randomSeed";
}