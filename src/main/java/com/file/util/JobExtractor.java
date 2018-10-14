package com.file.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.vo.BoxChild;

public class JobExtractor {

	private static final String JOB_PATTERN = "^([A-Za-z0-9-_]+)";

	private static final String JOB_TYPE = "job_type:(\\s*\\w+)";

	private static final String CMD = "command:(\\s*.*?$)";

	private static final String PARAM_PATTERN = "([^\\s]+(\\.(?i)(param|parm|awk|trg|cfg|sql|pl))\\b)";

	private static final String RECURSIVE_FILE_PATTERN = "([^\\s]+(\\.(?i)(ksh))\\b)";
	private static final String INVALIDFILENAME = "([^A-Za-z0-9-_\\$\\{\\}\\.])";

	private static final Pattern jobPattern;
	private static final Pattern jobType;
	private static final Pattern cmdPattern;
	private static final Pattern paramPattern;
	private static final Pattern fileToBeProcessed;
	private static final Pattern invalidFle;

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	static {
		jobPattern = Pattern.compile(JOB_PATTERN, Pattern.MULTILINE);
		jobType = Pattern.compile(JOB_TYPE, Pattern.MULTILINE);
		cmdPattern = Pattern.compile(CMD, Pattern.MULTILINE);
		paramPattern = Pattern.compile(PARAM_PATTERN, Pattern.MULTILINE);
		fileToBeProcessed = Pattern.compile(RECURSIVE_FILE_PATTERN, Pattern.MULTILINE);
		invalidFle = Pattern.compile(INVALIDFILENAME);

	}

	public static List<BoxChild> getJobs(String txtFileTobeProcessed) {
		String filePath = "." + File.separatorChar + txtFileTobeProcessed + ".txt";
		File file = new File(filePath);
		String fileString;
		List<BoxChild> boxChilds = new ArrayList<>();

		try {
			fileString = FileUtils.readFileToString(file, "UTF-8");
			String[] split = fileString.split("insert_job[\\s\\S]*?:");
			for (int i = 1; i < split.length; i++) {
				BoxChild boxChild = new BoxChild();
				List<String> params = new ArrayList<>();
				List<String> filesTobeScanned = new ArrayList<>();
				String Job = split[i].trim();
				// System.out.println(Job);
				Matcher matcher = jobPattern.matcher(Job);
				matcher.find();
				boxChild.setJob(matcher.group());
				System.out.println(matcher.group());
				matcher = jobType.matcher(Job);
				matcher.find();
				System.out.println(matcher.group(1));
				boxChild.setJobType(matcher.group(1));
				matcher = cmdPattern.matcher(Job);
				if (matcher.find()) {
					System.out.println(matcher.group(1));
					boxChild.setCommand(matcher.group(1));
				}
				matcher = paramPattern.matcher(Job);
				while (matcher.find()) {
					params.add(getVAlidName(matcher.group(), paramPattern));
				}
				System.out.println("params \t" + params);
				matcher = fileToBeProcessed.matcher(Job);
				while (matcher.find()) {
					filesTobeScanned.add(getVAlidName(matcher.group(), fileToBeProcessed));
				}
				boxChild.setParams(params);
				boxChild.setFilesTobeScanned(filesTobeScanned);
				System.out.println("filesTobeScanned \t" + filesTobeScanned);
				boxChilds.add(boxChild);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return boxChilds;
	}

	private static String getVAlidName(String matchedString, Pattern parampattern2) {
		Matcher matcher2 = invalidFle.matcher(matchedString);
		int endindex = 0;
		while (matcher2.find()) {
			endindex = matcher2.end();
		}
		String finalMatch = matchedString.substring(endindex);
		LOGGER.info("Final macth {}", finalMatch);

		/*
		 * To check weather a valid final name matched . to avoid matches like
		 * .ksh
		 */
		Matcher matcherFinal = parampattern2.matcher(finalMatch);
		if (!matcherFinal.find()) {
			finalMatch = null;
		}
		return finalMatch;
	}

}
