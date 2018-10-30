package com.file.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.constants.PATTERN;
import com.file.util.vo.KshChild;
import com.file.util.vo.SqlComand;

public class SqlExtractor {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	
	private static final String VSQL_TYPE = "VSQL";
	private static final String BTEQ_TYPE = "BTEQ-SQL";

	/**
	 * Method will extract the sql command.
	 * 
	 * @param fileTobeProcessed
	 * @param cmdParams 
	 * @param node
	 */
	public static KshChild exctractSqlCommands(String fileTobeProcessed, Map<String, String> cmdParams) {
		KshChild boxChild = new KshChild(fileTobeProcessed);
		List<SqlComand> comands = new ArrayList<>();
		try {

			if ("txt".equals(FilenameUtils.getExtension(fileTobeProcessed))) {
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed,cmdParams));
			} else {
			//	String filePath = "." + File.separatorChar + fileTobeProcessed;
				File file = new File(fileTobeProcessed);
				String fileString = FileUtils.readFileToString(file, "UTF-8");
				Matcher matcher = PATTERN.SQLEOCPATTERN.matcher(fileString); //match portions of file between beginning of line with <<EOC and ending with EOC
				int vsqlCount = 0; //updated and added count variables to increment VSQL vs BTEQ counts
				int bteqCount = 0;
				while(matcher.find()){
					String subScript = matcher.group(); 
					String sqlType = getSqlTypeFromMatch(subScript); //determine VSQL vs BTEQ-SQL from the subtext (note these are hard-coded)
					Matcher matcher2 = PATTERN.SQLPATTERN.matcher(subScript); //now match sql statements delimited by semicolon using existing code
					while (matcher2.find()) {
						String matchedSql = matcher2.group();
						SqlComand comand = new SqlComand();
						comand.setCommandSqlType(sqlType);
						
						//need to increment the count based on the sql type and add that to the comand object
						if(sqlType.equals(VSQL_TYPE)){
							vsqlCount++;
							comand.setCommandName(sqlType + vsqlCount);
						} else if(sqlType.equals(BTEQ_TYPE)){
							bteqCount++;
							comand.setCommandName(sqlType + bteqCount);
						} else {
							comand.setCommandName(sqlType + "_Unrecognized");
						}
						Matcher matcher3 = PATTERN.SQLCMDPATTERN.matcher(matchedSql);
						matcher3.find();
						String commandType = matcher3.group();
						comand.setCommandType(commandType.trim());
						comands.add(comand);
					}
				}
				boxChild.setSqlComands(comands);
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed,cmdParams));
			}

		} catch (IOException e) {
			LOGGER.error("Error while parsinfg scripts {}", e);
		}
		return boxChild;

	}

	private static String getSqlTypeFromMatch(String group) {
		String sqlType = null;
		if(group.toLowerCase().contains("vsql")){
			sqlType = VSQL_TYPE;
		} else if(group.toLowerCase().contains("bteq")){
			sqlType = BTEQ_TYPE;
		} else {
			sqlType = "UNRECOGNIZED";
		}
		return sqlType;
	}

}
