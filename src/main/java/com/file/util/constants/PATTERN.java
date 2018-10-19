package com.file.util.constants;

import java.util.regex.Pattern;

public interface PATTERN {

	String JOB = "^([A-Za-z0-9-_]+)";
	String JOB_TYPE = "job_type:(\\s*\\w+)";
	String CMD = "command:(\\s*.*?$)";
	String PARAM = "([^\\s]+(\\.(?i)(param|parm|awk|trg|cfg|sql|pl))\\b)";
	String NESTED_FILE_SEARCH = "([^\\s]+(\\.(?i)(ksh))\\b)";
	String INVALIDFILENAME = "([^A-Za-z0-9-_\\$\\{\\}\\.])";
	String SQL_COMMAND = "^(\\s*(?i)(SELECT|UPDATE|INSERT|CREATE|TURNCATE|GRANT|DROP))";
	String SQL = SQL_COMMAND + "\\s+[\\s\\S]+?\\;\\s*?$";
	

	String PARAM_ARG = "\\$\\{(.*?)\\}";
	Pattern PARAM_ARG_PATTERN = Pattern.compile(PARAM_ARG, Pattern.MULTILINE);

	Pattern JOBPATTERN = Pattern.compile(PATTERN.JOB, Pattern.MULTILINE);
	Pattern JOBTYPE = Pattern.compile(PATTERN.JOB_TYPE, Pattern.MULTILINE);
	Pattern CMDPATTERN = Pattern.compile(PATTERN.CMD, Pattern.MULTILINE);
	Pattern PARAMPATTERN = Pattern.compile(PATTERN.PARAM, Pattern.MULTILINE);
	Pattern FILETOBEPROCESSED = Pattern.compile(PATTERN.NESTED_FILE_SEARCH, Pattern.MULTILINE);
	Pattern INVALIDFLE = Pattern.compile(PATTERN.INVALIDFILENAME);
	Pattern SQLPATTERN = Pattern.compile(PATTERN.SQL, Pattern.MULTILINE);
	Pattern SQLCMDPATTERN = Pattern.compile(PATTERN.SQL_COMMAND, Pattern.MULTILINE);

}
