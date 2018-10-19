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
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.constants.PATTERN;

public class GetScriptsInTheFile {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Method returns the list of shell scripts referenced.
	 * 
	 * @param fileTobeProcessed
	 * @param fileNames
	 * @return
	 */
	public static List<String> getScripts(String fileTobeProcessed, HashSet<String> fileNames) {
		List<String> fileList = new ArrayList<>();
		String filePath;

		filePath = "." + File.separatorChar + fileTobeProcessed;

		LOGGER.info("---------------------------->Started reading file {}", fileTobeProcessed);
		try (FileReader reader = new FileReader(filePath)) {

			BufferedReader bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();

			while (line != null) {
				line = bufferedReader.readLine();

				if (line != null && !line.isEmpty() && !"#".equals(line.charAt(0) + "")) {
					List<String> cfgFiles = getConfigWithoutExtension(line);
					Matcher matcher = PATTERN.FILETOBEPROCESSED.matcher(line);
					while (matcher.find()) {
						String matchedString = matcher.group();
						/*
						 * This regular expresion match to remove any special
						 * characters matched.
						 */
						LOGGER.info("First macth {}", matchedString);
						Matcher matcher2 = PATTERN.INVALIDFLE.matcher(matchedString);
						int endindex = 0;
						while (matcher2.find()) {
							endindex = matcher2.end();
						}
						String finalMatch = matchedString.substring(endindex);
						LOGGER.info("Final macth {} ", finalMatch);

						/*
						 * To check weather a valid final name matched . to
						 * avoid matches like .ksh
						 */
						Matcher matcherFinal = PATTERN.FILETOBEPROCESSED.matcher(finalMatch);
						if (matcherFinal.find() && fileNames.add(finalMatch)) {
							fileList.add(finalMatch);
						}

					}
					for (String cfgFile : cfgFiles) {
						if (fileNames.add(cfgFile)) {
							fileList.add(cfgFile);

						}
					}
				}

			}

		} catch (IOException e) {
			LOGGER.error("Error while parsinfg scripts {}", e);
		}
		return fileList;
	}

	/**
	 * Method to extract the file reference without the extension.
	 * 
	 * @param line
	 * @return
	 */
	private static List<String> getConfigWithoutExtension(String line) {
		List<String> cfFiles = new ArrayList<>();
		String regex = "\\s*?[\\.|(?i)sh]\\s+(\\$\\{CONFIGDIR\\}\\/)(\\w+)\\s*";
		Pattern PARAM = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = PARAM.matcher(line);
		while (matcher.find()) {
			System.out.println(matcher.group(2));
			cfFiles.add(matcher.group(2));

		}

		return cfFiles;
	}

	public static void main(String[] args) {
	}

}
