package com.file.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

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
			Set<String> cfgFiles = new LinkedHashSet<>();

			for (String txtFileTobeProcessed : entry.getValue()) {
				Box box = new Box();
				box.setName(txtFileTobeProcessed);

				List<BoxChild> boxChilds = getBoxChilds(txtFileTobeProcessed, cfgFiles);

				box.setBoxChilds(boxChilds);
				boxs.add(box);
			}
		}
		return resultVapAndBoxList;
	}

	/**
	 * Extracts the child's of the box .Each job being the child of box.
	 * Recursively iterates the scripts to extract the sql and params invoked in
	 * the script.
	 * 
	 * @param txtFileTobeProcessed
	 * @param filenames
	 * @return
	 */
	private static List<BoxChild> getBoxChilds(String txtFileTobeProcessed, Set<String> filenames) {
		txtFileTobeProcessed = getFilePath(txtFileTobeProcessed+".txt");
		List<BoxChild> jobs = JobExtractor.getJobs(txtFileTobeProcessed);
		for (BoxChild boxChild : jobs) {
			List<String> files = boxChild.getFilesTobeScanned();
			List<KshChild> childs = new ArrayList<>();
			for (String fileTobeProcessed : files) {
				// To remove duplicate and recurive parsing
				if (filenames.add(fileTobeProcessed)) {
					scanFile(fileTobeProcessed, childs, boxChild.getCmdParams(), filenames);
				}
			}
			boxChild.setKshChilds(childs);

		}
		return jobs;
	}

	private static String getFilePath(String txtFileTobeProcessed) {
		String filePath = txtFileTobeProcessed;
		LOGGER.info("Finding path of file {}",txtFileTobeProcessed);
		try (Stream<Path> stream = Files.find(Paths.get("."), 2,
				(path, attr) -> path.getFileName().toString().equals(txtFileTobeProcessed))) {
			Optional<Path> findFirst = stream.findFirst();
			if(findFirst.isPresent()){
			filePath = findFirst.get().toString();
			}
		} catch (IOException e) {
			LOGGER.error("Error while finding files {}", e);
		}
		return filePath;
	}

	/**
	 * Recursively parses the files extracts tge sqls and params.
	 * 
	 * @param fileTobeProcessed
	 * @param childs
	 * @param cmdParams
	 * @param cfgFiles
	 */
	private static void scanFile(String fileTobeProcessed, List<KshChild> childs, Map<String, String> cmdParams,
			Set<String> fileNames) {
		fileTobeProcessed = getFilePath(fileTobeProcessed);
		List<String> childScripts = GetScriptsInTheFile.getScripts(fileTobeProcessed, fileNames);
		if (!childScripts.isEmpty()) {
			for (String filename : childScripts) {
				scanFile(filename, childs, cmdParams, fileNames);
			}
		}

		KshChild boxChild = SqlExtractor.exctractSqlCommands(fileTobeProcessed, cmdParams);
		childs.add(boxChild);

	}

}
