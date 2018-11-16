package com.file.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.file.util.vo.Box;
import com.file.util.vo.BoxChild;
import com.file.util.vo.KshChild;
import com.file.util.vo.SqlComand;

public class WorkBookHelper {
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Parses the input xlsx file and retrieves vap name and associated box's.
	 * Based on columns first column being the vap and second being its boxes.
	 * 
	 * @param filePath
	 * @return
	 * @throws EncryptedDocumentException
	 * @throws IOException
	 */
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
		// row.createCell(0).setCellValue("VAP");
		// row.createCell(1).setCellValue("BOX");
		row.createCell(0).setCellValue("JOB");
		row.createCell(1).setCellValue("JOB TYPE");
		row.createCell(2).setCellValue("COMMAND");
		row.createCell(3).setCellValue("SCRIPT");
		row.createCell(4).setCellValue("SQL COMAND");
		row.createCell(5).setCellValue("SQL TYPE"); // added header to support
													// SQL Type
		row.createCell(6).setCellValue("TYPE");
		row.createCell(7).setCellValue("PARAM");
	}

	/**
	 * 
	 * @param row
	 * @param txtFileTobeProcessed
	 * @param list
	 * @param sheet1
	 * @param cmdAndCount
	 */
	public static void writeScriptAndSqlDataToExcel(XSSFRow row, String txtFileTobeProcessed, List<BoxChild> list,
			XSSFSheet sheet1, Map<String, Integer> cmdAndCount, String vapName) {
		int rownNum = sheet1.getPhysicalNumberOfRows();

		row.createCell(1).setCellValue(txtFileTobeProcessed);
		int i = 0;

		for (BoxChild boxChild : list) {
			int firstRow = sheet1.getPhysicalNumberOfRows() - 1;

			boolean firstChildForBox = true;
			if (i != 0) {
				firstRow = rownNum;
				row = sheet1.createRow(rownNum);
				rownNum++;
			}
			printConstantColumns(row, txtFileTobeProcessed, boxChild, vapName);
			List<String> params = boxChild.getParams();

			if (!params.isEmpty()) {

				for (String param : params) {
					if (!firstChildForBox) {
						row = sheet1.createRow(rownNum);
						rownNum++;
					}
					printConstantColumns(row, txtFileTobeProcessed, boxChild, vapName);

					row.createCell(9).setCellValue(param); // updated index to
															// support sql type
															// addition
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
					// if (j == 0) {
					row.createCell(5).setCellValue(kshChild.getName());
					// }

					if (sqlIterator.hasNext()) {
						sqlComand = sqlIterator.next();
						row.createCell(6).setCellValue(sqlComand.getCommandName());

						String sqlType = sqlComand.getCommandSqlType(); // add
																		// sql
																		// type
																		// to
																		// the
																		// workbook
						if (sqlType != null) {
							row.createCell(7).setCellValue(sqlType);
						}

						String commandType = sqlComand.getCommandType().toUpperCase();
						row.createCell(8).setCellValue(commandType); // updated
																		// index
																		// to
																		// support
																		// addition
																		// of
																		// sql
																		// type

						String countType = sqlType + "_" + commandType;
						Integer cmDCount = cmdAndCount.get(countType);
						if (cmDCount == null) {
							cmdAndCount.put(countType, 1);
						} else {
							cmDCount++;
							cmdAndCount.put(countType, cmDCount);
						}
					}
					if (paramiterator.hasNext()) {
						row.createCell(9).setCellValue(paramiterator.next()); // updated
																				// index
																				// to
																				// support
																				// addition
																				// of
																				// sql
																				// type
					}
					printConstantColumns(row, txtFileTobeProcessed, boxChild, vapName);
					firstChildForBox = false;
					j++;
				}

			}
			i++;
			// int lastRow = sheet1.getPhysicalNumberOfRows() - 1;
			// if (firstRow != lastRow) {
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 2, 2);
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 3, 3);
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 4, 4);
			//
			// }

		}

	}
	
	/**
	 * 
	 * @param row
	 * @param txtFileTobeProcessed
	 * @param list
	 * @param sheet1
	 * @param cmdAndCount
	 */
	public static void writeScriptAndSqlDataToExcel(XSSFRow row,  List<BoxChild> list,
			XSSFSheet sheet1, Map<String, Integer> cmdAndCount) {
		int rownNum = sheet1.getPhysicalNumberOfRows();

		//row.createCell(1).setCellValue(txtFileTobeProcessed);
		int i = 0;

		for (BoxChild boxChild : list) {
			int firstRow = sheet1.getPhysicalNumberOfRows() - 1;

			boolean firstChildForBox = true;
			if (i != 0) {
				firstRow = rownNum;
				row = sheet1.createRow(rownNum);
				rownNum++;
			}
			printConstantColumns(row, null, boxChild, null);
			List<String> params = boxChild.getParams();

			if (!params.isEmpty()) {

				for (String param : params) {
					if (!firstChildForBox) {
						row = sheet1.createRow(rownNum);
						rownNum++;
					}
					printConstantColumns(row, null, boxChild, null);

					row.createCell(7).setCellValue(param); // updated index to
															// support sql type
															// addition
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
					// if (j == 0) {
					row.createCell(3).setCellValue(kshChild.getName());
					// }

					if (sqlIterator.hasNext()) {
						sqlComand = sqlIterator.next();
						row.createCell(4).setCellValue(sqlComand.getCommandName());

						String sqlType = sqlComand.getCommandSqlType(); // add
																		// sql
																		// type
																		// to
																		// the
																		// workbook
						if (sqlType != null) {
							row.createCell(5).setCellValue(sqlType);
						}

						String commandType = sqlComand.getCommandType().toUpperCase();
						row.createCell(6).setCellValue(commandType); // updated
																		// index
																		// to
																		// support
																		// addition
																		// of
																		// sql
																		// type

						String countType = sqlType + "_" + commandType;
						Integer cmDCount = cmdAndCount.get(countType);
						if (cmDCount == null) {
							cmdAndCount.put(countType, 1);
						} else {
							cmDCount++;
							cmdAndCount.put(countType, cmDCount);
						}
					}
					if (paramiterator.hasNext()) {
						row.createCell(7).setCellValue(paramiterator.next()); // updated
																				// index
																				// to
																				// support
																				// addition
																				// of
																				// sql
																				// type
					}
					printConstantColumns(row, null, boxChild, null);
					firstChildForBox = false;
					j++;
				}

			}
			i++;
			// int lastRow = sheet1.getPhysicalNumberOfRows() - 1;
			// if (firstRow != lastRow) {
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 2, 2);
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 3, 3);
			// mergeCellsAndAllignCenter(sheet1, firstRow, lastRow, 4, 4);
			//
			// }

		}

	}

	private static void printConstantColumns(XSSFRow row, String txtFileTobeProcessed, BoxChild boxChild,
			String vapName) {
	//	row.createCell(0).setCellValue(vapName);
	//	row.createCell(1).setCellValue(txtFileTobeProcessed);
		row.createCell(0).setCellValue(boxChild.getJob());
		row.createCell(1).setCellValue(boxChild.getJobType());
		row.createCell(2).setCellValue(boxChild.getCommand());
	}

	/**
	 * Method to write the result generated from parsing ksh files.
	 * 
	 * @param resultVapAndBoxList
	 */
	public static void writeResultToExcel(Map<String, List<Box>> resultVapAndBoxList) {
		XSSFWorkbook workbookout = new XSSFWorkbook();
		XSSFSheet resultSheet = workbookout.createSheet("results");
		WorkBookHelper.initializeExcel(workbookout, resultSheet);

		for (Entry<String, List<Box>> entry : resultVapAndBoxList.entrySet()) {
			int vapCount = 0;
			Map<String, Integer> cmdAndCount = new HashMap<>();
			// int firstRowForVap = resultSheet.getPhysicalNumberOfRows();

			for (Box box : entry.getValue()) {
				int firstRow = resultSheet.getPhysicalNumberOfRows();

				XSSFRow row = resultSheet.createRow(firstRow);
				if (vapCount == 0) {
					row.createCell(0).setCellValue(entry.getKey());
				}
				writeScriptAndSqlDataToExcel(row, box.getName(), box.getBoxChilds(), resultSheet, cmdAndCount,
						entry.getKey());
				vapCount++;
				// int lastRow = resultSheet.getPhysicalNumberOfRows() - 1;
				// if (firstRow != lastRow)
				// mergeCellsAndAllignCenter(resultSheet, firstRow, lastRow, 1,
				// 1);
			}
			writeSqlCounts(cmdAndCount, resultSheet);
			// int lastRowVap = resultSheet.getPhysicalNumberOfRows() - 1;
			// if (firstRowForVap != lastRowVap)
			// mergeCellsAndAllignCenter(resultSheet, firstRowForVap,
			// lastRowVap, 0, 0);

		}

		try (FileOutputStream outputStream = new FileOutputStream("result.xlsx")) {
			workbookout.write(outputStream);
			workbookout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the sql type counts for a particular vap.
	 * 
	 * @param cmdAndCount
	 * @param resultSheet
	 */
	private static void writeSqlCounts(Map<String, Integer> cmdAndCount, XSSFSheet resultSheet) {

		Set<Entry<String, Integer>> entrySet = cmdAndCount.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			int firstRow = resultSheet.getPhysicalNumberOfRows();
			XSSFRow row = resultSheet.createRow(firstRow);
			row.createCell(7).setCellValue(entry.getKey()); // updated indexes
															// to support
															// addition of sql
															// type
			row.createCell(8).setCellValue(entry.getValue());
			LOGGER.info("{} --------------------------> {}", entry.getKey(), entry.getValue());
		}
	}

	public static List<String> getActiveJobs(String filePath) throws EncryptedDocumentException, IOException {
		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(filePath));

		Sheet sheet = workbook.getSheetAt(0);
		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();

		int count = 0;
		List<String> jobList = new ArrayList<>();
		String vpName = null;

		for (Row row : sheet) {
			Cell cell = row.getCell(0);
			if (cell != null) {
				jobList.add(dataFormatter.formatCellValue(cell));
			}

		}
		return jobList;
	}

	public static void writeResultToExcel(List<BoxChild> boxChilds) {
		XSSFWorkbook workbookout = new XSSFWorkbook();
		XSSFSheet resultSheet = workbookout.createSheet("results");
		WorkBookHelper.initializeExcel(workbookout, resultSheet);

		Map<String, Integer> cmdAndCount = new HashMap<>();

		int firstRow = resultSheet.getPhysicalNumberOfRows();

		XSSFRow row = resultSheet.createRow(firstRow);

		writeScriptAndSqlDataToExcel(row, boxChilds, resultSheet, cmdAndCount);

		writeSqlCounts(cmdAndCount, resultSheet);

		try (FileOutputStream outputStream = new FileOutputStream("result.xlsx")) {
			workbookout.write(outputStream);
			workbookout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to merge the rows of the excel.
	 * 
	 * @param sheet1
	 * @param firstRow
	 * @param lastRow
	 * @param firstCol
	 * @param lastCol
	 */
	// private static void mergeCellsAndAllignCenter(XSSFSheet sheet1, int
	// firstRow, int lastRow, int firstCol,
	// int lastCol) {
	//
	// XSSFCell boxCell = sheet1.getRow(firstRow).getCell(firstCol);
	// sheet1.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol,
	// lastCol));
	// CellUtil.setVerticalAlignment(boxCell, VerticalAlignment.CENTER);
	// CellUtil.setAlignment(boxCell, HorizontalAlignment.CENTER);
	//
	// }

}
