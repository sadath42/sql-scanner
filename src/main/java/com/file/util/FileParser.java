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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.file.util.vo.Box;
import com.file.util.vo.BoxChild;

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
				Map<String, BoxChild> scriptAndSqlList = new LinkedHashMap<>();
				scanFile(txtFileTobeProcessed, scriptAndSqlList);
				box.setScriptAndSqlList(scriptAndSqlList);
				boxs.add(box);
			}
		}
		return resultVapAndBoxList;
	}

	/**
	 * @param fileTobeProcessed
	 * @param scriptAndSqlList
	 */
	private static void scanFile(String fileTobeProcessed, Map<String, BoxChild> scriptAndSqlList) {

		List<String> childScripts = GetScriptsInTheFile.getScripts(fileTobeProcessed);
		if (!childScripts.isEmpty()) {
			for (String filename : childScripts) {
				scanFile(filename, scriptAndSqlList);
			}
		}

		BoxChild exctractSqlCommands = SqlExtractor.exctractSqlCommands(fileTobeProcessed);
		boolean sqlChilds = !CollectionUtils.isEmpty(exctractSqlCommands.getSqlComands());
		if (sqlChilds || !CollectionUtils.isEmpty(exctractSqlCommands.getParams())) {
			if (!sqlChilds) {
				scriptAndSqlList.put(fileTobeProcessed + ".txt", exctractSqlCommands);
			} else {
				scriptAndSqlList.put(fileTobeProcessed, exctractSqlCommands);
			}
		}

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
