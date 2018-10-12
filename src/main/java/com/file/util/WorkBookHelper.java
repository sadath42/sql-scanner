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
		row.createCell(2).setCellValue("SCRIPT");
		row.createCell(3).setCellValue("COMAND");
		row.createCell(4).setCellValue("TYPE");
		row.createCell(5).setCellValue("PARAM");
	}

	/**
	 * 
	 * @param row
	 * @param txtFileTobeProcessed
	 * @param map
	 * @param sheet1
	 */
	public static void writeScriptAndSqlDataToExcel(XSSFRow row, String txtFileTobeProcessed, Map<String, BoxChild> map,
			XSSFSheet sheet1) {

		int rownNum = sheet1.getPhysicalNumberOfRows();
		int firstRow = rownNum;
		row.createCell(1).setCellValue(txtFileTobeProcessed);
		boolean firstChildForBox = true;
		for (Entry<String, BoxChild> entry1 : map.entrySet()) {
			BoxChild boxChild = entry1.getValue();
			List<SqlComand> sqlCommands = boxChild.getSqlComands();
			List<String> params = boxChild.getParams();
			Iterator<String> paramiterator = params.iterator();
			Iterator<SqlComand> sqlIterator = sqlCommands.iterator();
			int i = 0;
			int firstRowForShell;
			if (firstChildForBox) {
				firstRowForShell = rownNum;
			} else {
				firstRowForShell = rownNum + 1;
			}
			while (paramiterator.hasNext() || sqlIterator.hasNext()) {

				SqlComand sqlComand;

				if (i == 0) {
					/*
					 * Dont create row only for first child . use the existing
					 * row.
					 */
					if (!firstChildForBox) {
						row = sheet1.createRow(rownNum);
						rownNum++;
					}
					if (!entry1.getKey().endsWith(".txt")) {
						row.createCell(2).setCellValue(entry1.getKey());
					}
					if (sqlIterator.hasNext()) {
						sqlComand = sqlIterator.next();
						row.createCell(3).setCellValue(sqlComand.getCommandName());
						row.createCell(4).setCellValue(sqlComand.getCommandType());
					}
					if (paramiterator.hasNext()) {
						row.createCell(5).setCellValue(paramiterator.next());
					}
				} else {
					row = sheet1.createRow(rownNum);
					rownNum++;
					if (sqlIterator.hasNext()) {
						sqlComand = sqlIterator.next();

						row.createCell(3).setCellValue(sqlComand.getCommandName());
						row.createCell(4).setCellValue(sqlComand.getCommandType());
					}
					if (paramiterator.hasNext()) {
						row.createCell(5).setCellValue(paramiterator.next());
					}
				}
				firstChildForBox = false;
				i++;
			}
			if (firstRowForShell != rownNum) {
				mergeCellsAndAllignCenter(sheet1, firstRowForShell - 1, rownNum - 1, 2, 2);
			}
		}
		if (firstRow != rownNum) {
			// Merge First column
			mergeCellsAndAllignCenter(sheet1, firstRow - 1, rownNum - 1, 0, 0);
			// Merge Second column
			mergeCellsAndAllignCenter(sheet1, firstRow - 1, rownNum - 1, 1, 1);
		}
	}

	private static void mergeCellsAndAllignCenter(XSSFSheet sheet1, int firstRow, int lastRow, int firstCol,
			int lastCol) {
		/*
		 * XSSFCell boxCell = sheet1.getRow(firstRow).getCell(firstCol);
		 * sheet1.addMergedRegion(new CellRangeAddress(firstRow, lastRow,
		 * firstCol, lastCol)); CellUtil.setVerticalAlignment(boxCell,
		 * VerticalAlignment.CENTER); CellUtil.setAlignment(boxCell,
		 * HorizontalAlignment.CENTER);
		 */
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
				writeScriptAndSqlDataToExcel(row, box.getName(), box.getScriptAndSqlList(), sheet1);
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
