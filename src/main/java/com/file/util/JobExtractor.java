package com.file.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.constants.PATTERN;
import com.file.util.vo.BoxChild;

public class JobExtractor {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private static final Map<Character, String> paramKeys = new HashMap<>();

	static {
		paramKeys.put('i', "CRH_JOB_ID");
		paramKeys.put('j', "CRH_JOB");
		paramKeys.put('p', "CRH_PARAM_FILE");
		paramKeys.put('f', "CRH_FILE_NM");

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
				Matcher matcher = PATTERN.JOBPATTERN.matcher(Job);
				matcher.find();
				boxChild.setJob(matcher.group());
				System.out.println(matcher.group());
				matcher = PATTERN.JOBTYPE.matcher(Job);
				matcher.find();
				System.out.println(matcher.group(1));
				boxChild.setJobType(matcher.group(1));
				matcher = PATTERN.CMDPATTERN.matcher(Job);
				if (matcher.find()) {
					String command = matcher.group(1);
					System.out.println(command);
					boxChild.setCommand(command);
					setCmdParams(command, boxChild.getCmdParams());
				}
				matcher = PATTERN.PARAMPATTERN.matcher(Job);
				while (matcher.find()) {
					params.add(getVAlidName(matcher.group(), PATTERN.PARAMPATTERN));
				}
				System.out.println("params \t" + params);
				matcher = PATTERN.FILETOBEPROCESSED.matcher(Job);
				while (matcher.find()) {
					filesTobeScanned.add(getVAlidName(matcher.group(), PATTERN.FILETOBEPROCESSED));
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

	private static void setCmdParams(String command, Map<String, String> cmdParams) {
		Set<Entry<Character, String>> entrySet = paramKeys.entrySet();

		for (Entry<Character, String> paramEntry : entrySet) {
			String regex = ".*?\\s+-" + paramEntry.getKey() + "\\s+([A-Za-z0-9-_]+)\\b.*";
			Pattern PARAM = Pattern.compile(regex, Pattern.MULTILINE);
			Matcher matcher = PARAM.matcher(command);
			if (matcher.find()) {
				System.out.println(paramEntry.getValue() + "------------>" + matcher.group(1));
				cmdParams.put(paramEntry.getValue(), matcher.group(1));
			}
		}

	}

	

	private static String getVAlidName(String matchedString, Pattern parampattern2) {
		Matcher matcher2 = PATTERN.INVALIDFLE.matcher(matchedString);
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
