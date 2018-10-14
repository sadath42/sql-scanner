package com.file.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.file.util.vo.Box;
import com.file.util.vo.BoxChild;
import com.file.util.vo.KshChild;
import com.file.util.vo.SqlComand;

public class WorkBookHelper {

	public static Map<String, List<String>> getVapNameAndBoxList(String filePath)
			throws EncryptedDocumentException, IOException {
		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(filePath));

		Sheet sheet = workbook.getSheetAt(0);
		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();

		int count = 0;
		List<String> boxList;
		String vpName = null;
		Map<String, List<String>> vapAndBoxListMap = new LinkedHashMap<>();

		for (Row row : sheet) {
			count++;
			/*
			 * Since the first row is address.
			 */
			if (count == 1)
				continue;

			Cell cell0 = row.getCell(0);
			Cell cell1 = row.getCell(1);
			if (cell0 == null || cell1 == null) {
				return vapAndBoxListMap;
			}

			if (cell0.getCellType() == CellType.BLANK) {
				CellRangeAddress range = getMergedRegion(cell0);
				if (range != null) {
					Cell mergeValue = sheet.getRow(range.getFirstRow()).getCell(range.getFirstColumn());
					vpName = dataFormatter.formatCellValue(mergeValue);
				}
			} else if (cell0.getCellType() == CellType.STRING) {
				vpName = dataFormatter.formatCellValue(cell0);
			}
			boxList = vapAndBoxListMap.get(vpName);
			if (boxList == null) {
				boxList = new ArrayList<>();
				vapAndBoxListMap.put(vpName, boxList);
			}
			String boxName = dataFormatter.formatCellValue(cell1);
			boxName = boxName != null ? boxName.trim() : boxName;
			boxList.add(boxName);

		}
		return vapAndBoxListMap;
	}

	/**
	 * To get the range of the merged cells.
	 *
	 * @param cell
	 * @return
	 */
	public static CellRangeAddress getMergedRegion(Cell cell) {
		Sheet sheet = cell.getSheet();
		for (CellRangeAddress range : sheet.getMergedRegions()) {
			if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return range;
			}
		}
		return null;
	}

	/**
	 * Creates the output format in xlsx sheet.
	 * 
	 * @param workbookout
	 * @param sheet1
	 */
	public static void initializeExcel(XSSFWorkbook workbookout, XSSFSheet sheet1) {
		XSSFCellStyle style = workbookout.createCellStyle();
		XSSFFont font = workbookout.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);
		Row row = sheet1.createRow(0);
		row.setRowStyle(style);
		row.createCell(0).setCellValue("VAP");
		row.createCell(1).setCellValue("BOX");
		row.createCell(2).setCellValue("JOB");
		row.createCell(3).setCellValue("JOB TYPE");
		row.createCell(4).setCellValue("COMMAND");
		row.createCell(5).setCellValue("SCRIPT");
		row.createCell(6).setCellValue("SQL COMAND");
		row.createCell(7).setCellValue("TYPE");
		row.createCell(8).setCellValue("PARAM");
	}

	/**
	 * 
	 * @param row
	 * @param txtFileTobeProcessed
	 * @param list
	 * @param sheet1
	 */
	public static void writeScriptAndSqlDataToExcel(XSSFRow row, String txtFileTobeProcessed, List<BoxChild> list,
			XSSFSheet sheet1) {
		int rownNum = sheet1.getPhysicalNumberOfRows();

		row.createCell(1).setCellValue(txtFileTobeProcessed);
		int i = 0;

		for (BoxChild boxChild : list) {
			boolean firstChildForBox = true;
			if (i != 0) {
				row = sheet1.createRow(rownNum);
				rownNum++;
			}
			row.createCell(2).setCellValue(boxChild.getJob());
			row.createCell(3).setCellValue(boxChild.getJobType());
			 row.createCell(4).setCellValue(boxChild.getCommand());
			List<String> params = boxChild.getParams();

			if (!params.isEmpty()) {

				for (String param : params) {
					if (!firstChildForBox) {
						row = sheet1.createRow(rownNum);
						rownNum++;
					}

					row.createCell(8).setCellValue(param);
					firstChildForBox = false;

				}
			}

			List<KshChild> sqlCommands = boxChild.getKshChilds();
			for (KshChild kshChild : sqlCommands) {
				Iterator<String> paramiterator = kshChild.getParams().iterator();
				Iterator<SqlComand> sqlIterator = kshChild.getSqlComands().iterator();
				int j = 0;
				while (paramiterator.hasNext() || sqlIterator.hasNext()) {

					SqlComand sqlComand;

					/*
					 * Dont create row only for first child . use the existing
					 * row.
					 */
					if (!firstChildForBox) {
						row = sheet1.createRow(rownNum);
						rownNum++;
					}
					if (j == 0) {
						row.createCell(5).setCellValue(kshChild.getName());
					}

					if (sqlIterator.hasNext()) {
						sqlComand = sqlIterator.next();
						row.createCell(6).setCellValue(sqlComand.getCommandName());
						row.createCell(7).setCellValue(sqlComand.getCommandType());
					}
					if (paramiterator.hasNext()) {
						row.createCell(8).setCellValue(paramiterator.next());
					}
					/*
					 * } else { row = sheet1.createRow(rownNum); rownNum++; if
					 * (sqlIterator.hasNext()) { sqlComand = sqlIterator.next();
					 * 
					 * row.createCell(3).setCellValue(sqlComand.getCommandName()
					 * );
					 * row.createCell(4).setCellValue(sqlComand.getCommandType()
					 * ); } if (paramiterator.hasNext()) {
					 * row.createCell(5).setCellValue(paramiterator.next()); } }
					 */
					firstChildForBox = false;
					j++;
				}

			}
			i++;

		}

	}

	/**
	 * Method to write the result generated from parsing ksh files.
	 * 
	 * @param resultVapAndBoxList
	 */
	public static void writeResultToExcel(Map<String, List<Box>> resultVapAndBoxList) {
		XSSFWorkbook workbookout = new XSSFWorkbook();
		XSSFSheet sheet1 = workbookout.createSheet("results");
		WorkBookHelper.initializeExcel(workbookout, sheet1);

		for (Entry<String, List<Box>> entry : resultVapAndBoxList.entrySet()) {
			int vapCount = 0;

			for (Box box : entry.getValue()) {

				XSSFRow row = sheet1.createRow(sheet1.getPhysicalNumberOfRows());
				if (vapCount == 0) {
					row.createCell(0).setCellValue(entry.getKey());
				}
				writeScriptAndSqlDataToExcel(row, box.getName(), box.getBoxChilds(), sheet1);
				vapCount++;
			}
		}

		try (FileOutputStream outputStream = new FileOutputStream("result.xlsx")) {
			workbookout.write(outputStream);
			workbookout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
