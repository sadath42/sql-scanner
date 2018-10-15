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

import com.file.util.constants.PATTERN;
import com.file.util.vo.KshChild;
import com.file.util.vo.SqlComand;

public class SqlExtractor {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Method will extract the sql command.
	 * 
	 * @param fileTobeProcessed
	 * @param node
	 */
	public static KshChild exctractSqlCommands(String fileTobeProcessed) {
		KshChild boxChild = new KshChild(fileTobeProcessed);
		List<SqlComand> comands = new ArrayList<>();
		try {

			if (FilenameUtils.getExtension(fileTobeProcessed).isEmpty()) {
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed));
			} else {
				String filePath = "." + File.separatorChar + fileTobeProcessed;
				File file = new File(filePath);
				String fileString = FileUtils.readFileToString(file, "UTF-8");
				Matcher matcher = PATTERN.SQLPATTERN.matcher(fileString);
				int count = 0;
				while (matcher.find()) {
					count++;
					String matchedSql = matcher.group();
					SqlComand comand = new SqlComand();
					comand.setCommandName("SQL" + count);
					Matcher matcher2 = PATTERN.SQLCMDPATTERN.matcher(matchedSql);
					matcher2.find();
					String commandType = matcher2.group();
					comand.setCommandType(commandType.trim());
					comands.add(comand);
				}
				boxChild.setSqlComands(comands);
				boxChild.setParams(GetParmsInTheFile.getParams(fileTobeProcessed));
			}

		} catch (IOException e) {
			LOGGER.error("Error while parsinfg scripts {}", e);
		}
		return boxChild;

	}

}
