package com.file.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.file.util.vo.Box;
import com.file.util.vo.BoxChild;
import com.file.util.vo.KshChild;

/**
 * @author
 *
 */
public class FileParser {

	public static String SAMPLE_XLSX_FILE_PATH = "." + File.separatorChar;
	private static final String SH_DIRECTORY = "." + File.separatorChar + "shfiles";

	public static void main(String[] args) throws IOException, InvalidFormatException {
		if (args.length == 0) {
			throw new RuntimeException("Required input xslx file");
		}
		SAMPLE_XLSX_FILE_PATH = SAMPLE_XLSX_FILE_PATH + args[0];

		File shDirectory = new File(SH_DIRECTORY);
		if (!shDirectory.exists()) {
			shDirectory.mkdir();
		}
		Map<String, List<String>> vapAndBoxListMap = WorkBookHelper.getVapNameAndBoxList(SAMPLE_XLSX_FILE_PATH);

		Map<String, List<Box>> resultVapAndBoxList = processVaps(vapAndBoxListMap);

		WorkBookHelper.writeResultToExcel(resultVapAndBoxList);

	}

	/**
	 * Iterates the vap and list of boxs for the vap.
	 * 
	 * @param vapAndBoxListMap.
	 * @return
	 */
	private static Map<String, List<Box>> processVaps(Map<String, List<String>> vapAndBoxListMap) {
		Map<String, List<Box>> resultVapAndBoxList = new LinkedHashMap<>();

		for (Entry<String, List<String>> entry : vapAndBoxListMap.entrySet()) {
			List<Box> boxs = new ArrayList<>();
			resultVapAndBoxList.put(entry.getKey(), boxs);

			for (String txtFileTobeProcessed : entry.getValue()) {
				Box box = new Box();
				box.setName(txtFileTobeProcessed);

				List<BoxChild> boxChilds = getBoxChilds(txtFileTobeProcessed);

				box.setBoxChilds(boxChilds);
				boxs.add(box);
			}
		}
		return resultVapAndBoxList;
	}

	private static List<BoxChild> getBoxChilds(String txtFileTobeProcessed) {
		List<BoxChild> jobs = JobExtractor.getJobs(txtFileTobeProcessed);
		for (BoxChild boxChild : jobs) {
			List<String> files = boxChild.getFilesTobeScanned();
			List<KshChild> childs = new ArrayList<>();
			for (String fileTobeProcessed : files) {
				scanFile(fileTobeProcessed, childs);
			}
			boxChild.setKshChilds(childs);

		}
		return jobs;
	}

	/**
	 * @param fileTobeProcessed
	 * @param childs
	 */
	private static void scanFile(String fileTobeProcessed, List<KshChild> childs) {

		List<String> childScripts = GetScriptsInTheFile.getScripts(fileTobeProcessed);
		if (!childScripts.isEmpty()) {
			for (String filename : childScripts) {
				scanFile(filename, childs);
			}
		}

		KshChild boxChild = SqlExtractor.exctractSqlCommands(fileTobeProcessed);
		childs.add(boxChild);
		/*
		 * boolean sqlChilds =
		 * !CollectionUtils.isEmpty(boxChild.getSqlComands()); if (sqlChilds ||
		 * !CollectionUtils.isEmpty(boxChild.getParams())) { if (!sqlChilds) {
		 * childs.put(fileTobeProcessed + ".txt", boxChild); } else {
		 * childs.put(fileTobeProcessed, boxChild); } }
		 */

	}

	private static void copyFiles(String fileTobeProcessed) {
		try {
			Files.copy(new File(fileTobeProcessed).toPath(),
					new File(SH_DIRECTORY + File.separatorChar + fileTobeProcessed).toPath(),
					StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
