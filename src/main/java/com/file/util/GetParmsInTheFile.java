package com.file.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.constants.PATTERN;

public class GetParmsInTheFile {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Method returns the list of shell scripts referenced.
	 * 
	 * @param fileTobeProcessed
	 * @param cmdParams
	 * @return
	 */
	public static List<String> getParams(String fileTobeProcessed, Map<String, String> cmdParams) {
		HashSet<String> fileList = new LinkedHashSet<>();
		String filePath;
		/*if (FilenameUtils.getExtension(fileTobeProcessed).isEmpty()) {
			filePath = "." + File.separatorChar + fileTobeProcessed + ".txt";

		} else {
			filePath = "." + File.separatorChar + fileTobeProcessed;
		}*/
		filePath = "." + File.separatorChar + fileTobeProcessed;

		LOGGER.info("---------------------------->Started reading file {}", fileTobeProcessed);
		try (FileReader reader = new FileReader(filePath)) {

			BufferedReader bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();

			while (line != null) {
				line = bufferedReader.readLine();

				if (line != null && !line.isEmpty() && !"#".equals(line.charAt(0) + "")) {
					String macthedName = getParam(line);
					if (macthedName != null) {
						macthedName = resolveParamIfAny(macthedName, cmdParams);
						fileList.add(macthedName);
					}
				}

			}

		} catch (IOException e) {
			LOGGER.error("Error while get the parms {}", e);
		}
		return new ArrayList<>(fileList);
	}

	private static String resolveParamIfAny(String macthedName, Map<String, String> cmdParams) {
		Matcher matcher = PATTERN.PARAM_ARG_PATTERN.matcher(macthedName);
		if (matcher.find() && cmdParams != null) {
			String paramArg = matcher.group(1);
			String resolvedParam = cmdParams.get(paramArg.toUpperCase());
			if (resolvedParam != null)
				macthedName = macthedName.replace(matcher.group(), resolvedParam);
		}
		return macthedName;
	}

	private static String getParam(String line) {
		String finalMatch = null;

		Matcher matcher = PATTERN.PARAMPATTERN.matcher(line);
		while (matcher.find()) {
			String matchedString = matcher.group();
			/*
			 * This regular expresion match to remove any special characters
			 * matched.
			 */
			LOGGER.info("First macth " + matchedString);
			Matcher matcher2 = PATTERN.INVALIDFLE.matcher(matchedString);
			int endindex = 0;
			while (matcher2.find()) {
				endindex = matcher2.end();
			}
			finalMatch = matchedString.substring(endindex);
			LOGGER.info("Final macth {}", finalMatch);

			/*
			 * To check weather a valid final name matched . to avoid matches
			 * like .ksh
			 */
			Matcher matcherFinal = PATTERN.PARAMPATTERN.matcher(finalMatch);
			if (!matcherFinal.find()) {
				finalMatch = null;
			}
		}
		return finalMatch;
	}

}
