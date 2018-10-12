package com.file.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.vo.BoxChild;
import com.file.util.vo.SqlComand;

public class SqlExtractor {

	private static final String SQL_COMMAND_PATTERN = "^(\\s*(?i)(SELECT|UPDATE|INSERT|CREATE|TURNCATE|GRANT|DROP))";
	private static final String SQL_PATTERN = SQL_COMMAND_PATTERN + "\\s+[\\s\\S]+?\\;\\s*?$";

	private static final Pattern PATTERN;
	private static final Pattern CMDPATTERN;
	private static Logger LOGGER = 
			  LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	static {
		PATTERN = Pattern.compile(SQL_PATTERN, Pattern.MULTILINE);
		CMDPATTERN = Pattern.compile(SQL_COMMAND_PATTERN, Pattern.MULTILINE);

	}

	/**
	 * Method will extract the sql command.
	 * 
	 * @param fileTobeProcessed
	 * @param node
	 */
	public static BoxChild exctractSqlCommands(String fileTobeProcessed) {
		BoxChild boxChild = new BoxChild();
		List<SqlComand> comands = new ArrayList<>();
		try {

			if (FilenameUtils.getExtension(fileTobeProcessed).isEmpty()) {
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed));
			} else {
				String filePath = "." + File.separatorChar + fileTobeProcessed;
				File file = new File(filePath);
				String fileString = FileUtils.readFileToString(file, "UTF-8");
				Matcher matcher = PATTERN.matcher(fileString);
				int count = 0;
				while (matcher.find()) {
					count++;
					String matchedSql = matcher.group();
					SqlComand comand = new SqlComand();
					comand.setCommandName("SQL" + count);
					Matcher matcher2 = CMDPATTERN.matcher(matchedSql);
					matcher2.find();
					String commandType = matcher2.group();
					comand.setCommandType(commandType.trim());
					comands.add(comand);
				}
				boxChild.setSqlComands(comands);
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed));
			}

		} catch (IOException e) {
			LOGGER.error("Error while parsinfg scripts {}",e);
		}
		return boxChild;

	}

}
