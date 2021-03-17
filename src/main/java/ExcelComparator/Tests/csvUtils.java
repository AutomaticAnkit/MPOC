package ExcelComparator.Tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base;

import com.opencsv.CSVReaderBuilder;

import ExcelComparator.Utils.GeneralUtils;

public class csvUtils {
	private static Logger log = LogManager.getLogger(csvUtils.class.getName());
	boolean result = true;
	static String path;
	static int noOfTable;
	public static StringBuffer finalData = new StringBuffer();
	protected static ArrayList missingColm = new ArrayList();
	static ArrayList localmissingColm = new ArrayList();
	protected static HashMap<Integer, ArrayList> missingValuesMap = new HashMap<Integer, ArrayList>();
	protected static HashMap<Integer, ArrayList> completeValuesMap = new HashMap<Integer, ArrayList>();
	static String end = getDate() + "_" + getTime();
	static XSSFCellStyle failstyle;
	static XSSFCellStyle passWithVstyle;
	static XSSFCellStyle passstyle;
	static XSSFCellStyle headerstyle;
	static XSSFCellStyle style;
	static XSSFFont fontstyle;
	static missingValueExtractor missingValue;

	public csvUtils() throws IOException {

	}

	public static String getValFromConfigPropFile(String Key) throws IOException {

		return GeneralUtils.getProperty(Key);
	}

	public static String getValFromEnvPropFile(String Key) throws IOException {

		return GeneralUtils.getEnvironment(Key);
	}

	public static void getConfigValue() throws IOException {
		path = GeneralUtils.getEnvironment("folderPathforInputExcel");
		noOfTable = Integer.parseInt(GeneralUtils.getProperty("totalTables"));

	}

	private static String splitValue(String proName, int fileNo) {
		String result = null;
		if (fileNo == 1) {
			result = (proName.split(","))[0];
		} else if (fileNo == 2) {
			result = (proName.split(","))[1];
		}
		return result;
	}

	public static void matchStructure(String fileName, int fileNo) throws Exception, IOException {
		int totnumoftables = Integer.parseInt(GeneralUtils.getProperty("totalTables"));
		for (int i = 1; i <= totnumoftables; i++) {
			// fColumn array is getting Prod and template value and getting split.
			String[] fColumn = (GeneralUtils.getProperty("firstColumnHeaderTable" + i)).split(",");
			String[] lColumn = (GeneralUtils.getProperty("LastColumnHeaderTable" + i)).split(",");
//			String[] rowCount = (GeneralUtils.getProperty("noOfRowsInTable" + i)).split(",");
			String[] colCount = (GeneralUtils.getProperty("noOfColumnsInTable" + i)).split(",");
			// File no is 1 for prod and 2 for template
			for (int tbCount = 1; tbCount <= totnumoftables; tbCount++) {
				if (fileNo == 1) {
					// Below Method generates on the basis of first column header and Last column
					// heade
					missingValue.headerCompare(fileName, Integer.parseInt(colCount[0]), fColumn[0], lColumn[0],
							tbCount);

				} else {
					missingValue.headerCompare(fileName, Integer.parseInt(colCount[1]), fColumn[1], lColumn[1],
							tbCount);
				}
				System.out.println(
						"missingValuesMap.put(tableNo, tempData.indexOf(tempColHeader));: " + missingValuesMap);
			}
		}
	}

	// This method is used for checking the structure of the table.
	public static void structureCheck(String fileName, int fileNo) throws IOException {
		// Config Property file loaded

		int totnumoftables = Integer.parseInt(GeneralUtils.getProperty("totalTables"));
		for (int i = 1; i <= totnumoftables; i++) {
			// fColumn array is getting Prod and template value and getting split.
			String[] fColumn = (GeneralUtils.getProperty("firstColumnHeaderTable" + i)).split(",");
			String[] lColumn = (GeneralUtils.getProperty("LastColumnHeaderTable" + i)).split(",");
			String[] rowCount = (GeneralUtils.getProperty("noOfRowsInTable" + i)).split(",");
			String[] colCount = (GeneralUtils.getProperty("noOfColumnsInTable" + i)).split(",");
			// File no is 1 for prod and 2 for template
			if (fileNo == 1) {
				// Below Method generates on the basis of first column header and Last column
				// header.
				masterCSVGenrator(fileName, fColumn[0], lColumn[0], Integer.parseInt(colCount[0]),
						Integer.parseInt(rowCount[0]), i);
			} else {
				masterCSVGenrator(fileName, fColumn[1], lColumn[1], Integer.parseInt(colCount[1]),
						Integer.parseInt(rowCount[1]), i);
			}
		}
	}

	// This method generates the CSV for the Excel on the basis of first header last
	// header , No of column and no of rows.
	public static void masterCSVGenrator(String fileName, String fh, String lh, int nc, int nr, int tableNo)
			throws IOException {
		missingValue = new missingValueExtractor();
		String inputExcelFileName = fileName;
		missingColm = missingValue.headerCompare(fileName, nc, fh, lh, tableNo);
		localmissingColm = missingValue.headerCompare(fileName, nc, fh, lh, tableNo);
		System.out.println("missingColm : " + missingColm);
		// String buffer table 1 gets the values of table appended by ","
		StringBuffer table1 = tabletoStringGenrator(fileName, fh, lh, nc, nr, localmissingColm);
		// missingValuesMap.put(tableNo, missingColm);
		System.out.println("missingValuesMap" + missingValuesMap);
		System.out.println("PFB, the table data from table.");
		System.out.println(table1 + "\n");
		System.out.println("Final Data Value : " + finalData);
		finalData = finalData.append(table1);
		System.out.println(finalData);
		System.out.println("CSV File generated on the Below Location : - ");
		System.out.println(GeneralUtils.getEnvironment("folderPathforInputExcel") + inputExcelFileName + "\n");
		// Closing the CSV file Created
		FileOutputStream fileOut = new FileOutputStream(
				GeneralUtils.getEnvironment("folderPathforInputExcel") + inputExcelFileName + "CSV" + end + ".csv");
		fileOut.write(finalData.toString().getBytes());
		fileOut.close();
	}

	// This method genrates the table data to a string array on the basis of First ,
	// last and no of rows& columns.
	public static StringBuffer tabletoStringGenrator(String fileName, String firstHeader, String lastHeader,
			int numberOfCOlumns, int numberOfRows, ArrayList listOfIgnoreCols) throws IOException {
		// listOfIgnoreCols contains the values are that needs to be ignored while CSV
		// generation.
		// Values which are not present in prod and present in template will be ignored.
		String path = GeneralUtils.getEnvironment("folderPathforInputExcel");
		FileInputStream fileInStream = new FileInputStream(
				GeneralUtils.getEnvironment("folderPathforInputExcel") + fileName + ".xlsx");
		int rowcount = 1;
		ArrayList ignoreColNo = new ArrayList();
		XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);// Open the xlsx and get the requested sheet from the //
																// workbook
		XSSFSheet s1 = workBook.getSheetAt(0);// Get Sheet from WorkBook
		StringBuffer csvLine = new StringBuffer();// String buffer to be written in CSV file
		int rc = s1.getLastRowNum();// Get last row number
		System.out.println("Last row Number " + rc);
		// iterating on the rows
		for (int i = s1.getFirstRowNum(); i < rc; i++) {
			// checking if the row is null
			if (s1.getRow(i) != null) {
				// getting the last cell number of the row
				int cc = s1.getRow(i).getLastCellNum();
				// iterating over the cells
				for (int j = 0; j < cc; j++) {
					// checking if the cell is null
					if (s1.getRow(i).getCell(j) != null) {
						// temp variable to catch the value of I on which the filled Cell is found
						int temp = j;
						// checking if the first header index and last header index is null A1 A5
						if ((s1.getRow(i).getCell(j)) != null
								&& (s1.getRow(i).getCell(j + (numberOfCOlumns - 1))) != null) {
							// Checking first header and last header is numeric
							if (s1.getRow(i).getCell(j)
									.getCellType() == s1.getRow(i).getCell(j).getCellType().NUMERIC) {
							} else {
								// checking the cell type is numeric
								if ((s1.getRow(i).getCell(j)
										.getCellType() == s1.getRow(i).getCell(j).getCellType().NUMERIC)
										|| (s1.getRow(i).getCell(j + (numberOfCOlumns - 1)).getCellType() == s1
												.getRow(i).getCell(j + (numberOfCOlumns - 1)).getCellType().NUMERIC)) {
								}
								// checking if the A1 and A5 are found
								else if (((s1.getRow(i).getCell(j).getStringCellValue()).equals(firstHeader))
										&& ((s1.getRow(i).getCell(j + (numberOfCOlumns - 1)).getStringCellValue())
												.equals(lastHeader))) {
									// if A1 and A5 are found then headers will be will be appended
									for (int k = 0; k < numberOfCOlumns;) {
										Cell c1 = s1.getRow(i).getCell(j);
										// checking if the cell is null and contains k
										if (c1 != null && !(ignoreColNo.contains(k))) {
											// Checking the Cell Type
											switch (c1.getCellType()) {
											case STRING:
												// Checking if the headers are not present in the ignored list are
												// present in the cell
												if (!(listOfIgnoreCols
														.contains((s1.getRow(i).getCell(j).getStringCellValue())))) {
													// checking if the headers to be ignored is more than 0 and the
													// header present in the ignored list and the header in the cell is
													// equal
													if (listOfIgnoreCols.size() > 0 && (listOfIgnoreCols.contains(
															(s1.getRow(i).getCell(j).getStringCellValue())))) {

														if (k == 0)
															ignoreColNo.add(200);
														else if (k == 1)
															ignoreColNo.add(300);
														else if (k == 2)
															ignoreColNo.add(400);
														else if (k == 3)
															ignoreColNo.add(500);
														else if (k == 4)
															ignoreColNo.add(600);
													}
													// Appending of string value if the value is not available in the
													// ignired list
													csvLine.append(c1.getStringCellValue() + ",");
												} else {
													if (listOfIgnoreCols.size() > 0) {
														listOfIgnoreCols
																.remove((s1.getRow(i).getCell(j).getStringCellValue()));
														if (k == 0)
															ignoreColNo.add(200);
														else if (k == 1)
															ignoreColNo.add(300);
														else if (k == 2)
															ignoreColNo.add(400);
														else if (k == 3)
															ignoreColNo.add(500);
														else if (k == 4)
															ignoreColNo.add(600);
													}
												}
												break;
											case NUMERIC:
												// Control coming to Numeric if the cell type is numeric
												if (k == 0 && !ignoreColNo.contains(200)) {
													csvLine.append(c1.getNumericCellValue() + ",");
												} else if (k == 1 && !ignoreColNo.contains(300)) {
													csvLine.append(c1.getNumericCellValue() + ",");
												} else if (k == 2 && !ignoreColNo.contains(400)) {
													csvLine.append(c1.getNumericCellValue() + ",");
												} else if (k == 3 && !ignoreColNo.contains(500)) {
													csvLine.append(c1.getNumericCellValue() + ",");
												} else if (k == 4 && !ignoreColNo.contains(600)) {
													csvLine.append(c1.getNumericCellValue() + ",");
												}
												break;
											case BOOLEAN:
												csvLine.append(c1.getBooleanCellValue() + ",");
												break;
											case _NONE:
												break;

											case BLANK:
												break;

											default:
												break;
											}
										} else {

										}
										k++;
										j++;
										if (k % numberOfCOlumns == 0 && rowcount != numberOfRows) {
											rowcount++;
											k = 0;
											j = temp;
											i++;
										}
									}
									break;
								}

							}
						}
					}
				}
			}
		}
		// returns the csvline as string array
		return csvLine;
	}

	// This method compares the PROD and UAT CSV generated in structure check method
	public static void csvComparison(HashMap<Integer, ArrayList> missingValuesMap) throws IOException {
		String path = GeneralUtils.getEnvironment("folderPathforInputExcel");
		String file1 = GeneralUtils.getEnvironment("inputExcelFileName") + "CSV" + end + ".csv";
		String file2 = GeneralUtils.getEnvironment("inputExcelTemplate") + "CSV" + end + ".csv";
		String file3 = GeneralUtils.getEnvironment("inputExcelFileName")
				+ GeneralUtils.getEnvironment("inputExcelTemplate") + ".csv";
		ArrayList al1 = new ArrayList();
		ArrayList al2 = new ArrayList();
		// Counter to get all the Pass Values
		int counterPass = 0;
		// Counter to get all the Fail Values
		int counterFail = 0;
		// Counter to get Pass with Variance Values
		int counterPWV = 0;
		// Numbe of Tables
		int noOfTables = Integer.parseInt(GeneralUtils.getProperty("totalTables"));
		// File Writer to write 3rd CSV file after Comparison
		FileWriter writer = new FileWriter(path + file3);
		// Buffer reader to take all the data of CSV file
		BufferedReader CSVFile1 = new BufferedReader(new FileReader(path + file1));
		// data row 1 to capture the Line in CSV
		String dataRow1 = CSVFile1.readLine();
		// All the data from PROD CSV is inserted into al1 list for Comparison
		while (dataRow1 != null) {
			String[] dataArray1 = dataRow1.split(",");
			for (String item1 : dataArray1) {
				al1.add(item1);
			}
			dataRow1 = CSVFile1.readLine(); // Read next line of data.
		}
		CSVFile1.close();
		// All the data from Template CSV is inserted into al2 list for Comparison
		BufferedReader CSVFile2 = new BufferedReader(new FileReader(path + file2));
		// Capturring the linein String from CSV 2
		String dataRow2 = CSVFile2.readLine();
		// Using While loop to chek the next values in null or not and then putting them
		// into list
		while (dataRow2 != null) {
			String[] dataArray2 = dataRow2.split(",");
			for (String item2 : dataArray2) {
				al2.add(item2);
			}
			dataRow2 = CSVFile2.readLine(); // Read next line of data.
		}
		CSVFile2.close();
		System.out.println("al1" + al1);
		System.out.println("al2" + al2);
		boolean tableFlag = false;
		double var = 0;
		// Looping on the first list
		System.out.println("al1 list " + al1);
		int testing = 0;
		int x = 1;

		for (int i = 0; i < al1.size();) {
			// Looping on the number of tables
			System.out.println("noOfTables: " + noOfTables);
			while (x <= noOfTables) {
				System.out.println("noOfTablesxxxxxx: " + x);
				// tempcount get the column count on the basis of table
				int tempColCount = tablecolumnCount(x, 1);
				// Y is the temp variable to add into column count on line no 374
				int y = i;
				// Below if and else is responsible for writing Header and Compare the table
				// values.
				if ((al1.get(i).equals(splitValue(GeneralUtils.getProperty("firstColumnHeaderTable" + x), 1))
						&& al1.get(i + tempColCount - 1)
								.equals(splitValue(GeneralUtils.getProperty("LastColumnHeaderTable" + x), 1)))) {
					// This for loop is to print header of the table columns
					for (int k = i; k < tempColCount + y;) {
						writer.append("" + al1.get(k));
						writer.append(",");
						k++;
						i++;
					}
					testing = tempColCount;
				}
				// This is to compare the elements of table apart from Table Headers
				else if (al1.get(i).equals(al2.get(i))) {
					// Pass will be written in the comparison file for the same values for table
					writer.append("" + "Pass");
					writer.append(",");
					i++;
					counterPass++;
				} else {
					// Variance Calculater is used to return the variance value as per the column of
					// the table

					// int ab = i%tempColCount;
					var = varianceCalculator(tempColCount, testing, x, missingValuesMap);
					testing++;
					// Comparison is done with the tolerance value on the Yes flag
					if (GeneralUtils.getEnvironment("runWithTol").equalsIgnoreCase("Yes")) {
						double itemList1 = Double.parseDouble((String) al1.get(i));
						double itemList2 = Double.parseDouble((String) al2.get(i));
						String result = calculateWRTVariance(itemList1, itemList2, var);
						// writer writes the result and result contains the pass with variance
						// --variance value also
						writer.append("" + result);
						writer.append(",");
						i++;
						counterPWV++;
					} else {
						// Write writes the fail for the values which do not match
						writer.append("" + "Fail");
						writer.append(",");
						i++;
						counterFail++;
					}
				}
				if (i < al1.size() && x < noOfTables) {
					if (al1.get(i)
							.equals(splitValue(GeneralUtils.getProperty("firstColumnHeaderTable" + (x + 1)), 1))) {
						System.out.println("Am I coming here or not");
						x++;
						System.out.println("Am I coming here or not x : " + x);
						writer.append("\n");

					}
				} else {
					break;
				}
			}
		}

		writer.flush();
		writer.close();
		// Value of total Pass Values in the CSV
		//
		String passValue = String.valueOf(counterPass);
		// Value of Total Fail Values in CSV
		String failValue = String.valueOf(counterFail);
		// Value of total Values pass with variance
		String pWVValue = String.valueOf(counterPWV);
		valueSetterPropertyFile("summaryRepoValH6", passValue);
		valueSetterPropertyFile("summaryRepoValH7", failValue);
		valueSetterPropertyFile("summaryRepoValH8", pWVValue);
		System.out.println("File Created Successfully.");
		System.out.println("PLease Check the File on Below Location");
		System.out.println(path + "\\" + file3);
		// This is just to return the values which are differnt
		for (Object bs : al2) {
			al1.remove(bs);
		}
		int size = al1.size();
		System.out.println("Number of Values found diff are  " + size);
		System.out.println(" ");
		/*
		 * }catch (IOException e) { System.out.println(e); }
		 */
	}

	// This method is used for csv to Excel Converion and genrate the Final Excel
	// with results
	public static void csvtoExcelCOnverion() throws IOException {

		System.out.println(GeneralUtils.getEnvironment("folderPathforInputExcel")
				+ GeneralUtils.getEnvironment("inputExcelFileName") + ".xlsx");
		FileInputStream inputStream = new FileInputStream(
				new File(GeneralUtils.getEnvironment("folderPathforInputExcel")
						+ GeneralUtils.getEnvironment("inputExcelFileName") + ".xlsx"));
		// Created the WorkBook object and passed the file
		Workbook wb = new XSSFWorkbook(inputStream);
		String finalExcelFolderPath = GeneralUtils.getEnvironment("finalExcelFolderPath");
		String finalOutputExcelFile = GeneralUtils.getEnvironment("finalOutputExcelFile");
		// Variable to add current date and time in the end of the comparison report
		// String array to take each line from the csv
		String[] line;
		int r = 0;// Row increment
		// No of tables to work
		int noOfTables = Integer.parseInt(GeneralUtils.getProperty("totalTables"));
		// rowCount Variable to control the wrier on row level
		int rowCount = 1;
		CreationHelper helper = wb.getCreationHelper();
		// Creating a new sheet to present the results in the excel sheet on the prod
		// sheet provided as input.
		Sheet sheet = wb.createSheet("ProdVsUATDataComparison" + end);
		// Below Method is used for Highlighting the Border of the cell
		style = (XSSFCellStyle) wb.createCellStyle();
		failstyle = (XSSFCellStyle) wb.createCellStyle();
		passWithVstyle = (XSSFCellStyle) wb.createCellStyle();
		passstyle = (XSSFCellStyle) wb.createCellStyle();
		headerstyle = (XSSFCellStyle) wb.createCellStyle();
		fontstyle = (XSSFFont) wb.createFont();
		fontstyle.setFontHeightInPoints((short) 12);
		fontstyle.setBold(true);
		// Border styling for Cell
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setShrinkToFit(true);
		style.setWrapText(true);
		failstyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		failstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		passWithVstyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		passWithVstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		passstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		passstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerstyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerstyle.setFont(fontstyle);
		// CSV reader to read the CSV
		CSVReaderBuilder reader = new CSVReaderBuilder(
				new FileReader(GeneralUtils.getEnvironment("folderPathforInputExcel")
						+ GeneralUtils.getEnvironment("inputExcelFileName")
						+ GeneralUtils.getEnvironment("inputExcelTemplate") + ".csv"));
		// Putting all the lines in the List of String Arrayy
		List<String[]> csvRowAsStrng = reader.build().readAll();
		// Iterating over the List of Array
		for (int x = 1; x <= csvRowAsStrng.size(); x++) {
			// Cause list starts from zero
			line = csvRowAsStrng.get(x - 1);
			// Creating new row
			Row row = sheet.createRow((short) r++);
			// iterating on the first string array of the list
			for (int i = 0; i < (line.length - 1);) {
				// creating row
				row = sheet.createRow((short) r++);
				// Comparing i with size of line
				if (i < (line.length - 1)) {
					// rowCount to cotrol the cell wrting on the row level
					rowCount = 1;
					// temp count contains the table column count as per the table number provided
					// in the x
					int tmpColCount = tablecolumnCount(x, 1);
					//// temp rowCOunt contains the table column count as per the table number
					//// provided in the x
					int tmpRowCount = tableRowCount(x, 1);
					// Checking the first and last header of the table
					if (line[i].equals(splitValue(GeneralUtils.getProperty("firstColumnHeaderTable" + x), 1))
							&& line[i + (tmpColCount - 1)]
									.equals(splitValue(GeneralUtils.getProperty("LastColumnHeaderTable" + x), 1))) {
						// creating row
						row = sheet.createRow((short) r++);
						// iterating on the column count
						for (int k = 0; k < tmpColCount;) {
							if (x == csvRowAsStrng.size()) {
								// creating cell
								Cell cell = row.createCell(k);
								cell.setCellStyle(style);
								if (line[i].equalsIgnoreCase("Fail")) {
									cell.setCellStyle(style);
									cell.setCellStyle(failstyle);
								} else if (line[i].contains("Pass with Variance:")) {
									cell.setCellStyle(style);
									cell.setCellStyle(passWithVstyle);
								} else if (line[i].equalsIgnoreCase("Pass")) {
									cell.setCellStyle(style);
									cell.setCellStyle(passstyle);
								} else {
									cell.setCellStyle(style);
									cell.setCellStyle(headerstyle);
								}
//										cell.setCellStyle(style);
								// setting the cell value fetched from line string array
								cell.setCellValue(helper.createRichTextString(line[i]));
								k++;
								i++;
								// Controlling the writer to write upto no of rows present in the table
								if (i % tmpColCount == 0 && rowCount != tmpRowCount) {
									rowCount++;
									k = 0;
									row = sheet.createRow((short) r++);
								}
							} else if (!line[i].equals(
									splitValue(GeneralUtils.getProperty("firstColumnHeaderTable" + (x + 1)), 1))) {
								Cell cell = row.createCell(k);
								cell.setCellStyle(style);
								if (line[i].equalsIgnoreCase("Fail")) {
									cell.setCellStyle(style);
									cell.setCellStyle(failstyle);
								} else if (line[i].contains("Pass with Variance:")) {
									cell.setCellStyle(style);
									cell.setCellStyle(passWithVstyle);
								} else if (line[i].equalsIgnoreCase("Pass")) {
									cell.setCellStyle(style);
									cell.setCellStyle(passstyle);
								} else {
									cell.setCellStyle(style);
									cell.setCellStyle(headerstyle);
								}
//										cell.setCellStyle(style);
								cell.setCellValue(helper.createRichTextString(line[i]));
								k++;
								i++;
								if (i % tmpColCount == 0 && rowCount != tmpRowCount) {
									rowCount++;
									k = 0;
									row = sheet.createRow((short) r++);
								}
							}
						}
					} else {
						break;
					}
				} else {
					break;
				}
			}
		}
		// creating sheet 3rd tab
		Sheet s2 = wb.createSheet("SummaryReport" + end);
		// putting all the values of summary report in the third tab
		summaryReport(s2);
		// closing the excel.
		FileOutputStream fileOut = new FileOutputStream(finalExcelFolderPath + finalOutputExcelFile + end + ".xlsx");
		wb.write(fileOut);
		fileOut.close();
		System.out.println("File Created sucessfully.");
		/*
		 * } catch (IOException e) { System.out.println(e); }
		 */
	}
	// Reading the final comparison report

	public static void readAndCompareCompReportWithTemplate() throws FileNotFoundException, IOException {
		String finalExcelFolderPath = GeneralUtils.getEnvironment("finalExcelFolderPath");
		String finalOutputExcelFile = GeneralUtils.getEnvironment("finalOutputExcelFile");
//		String path = GeneralUtils.getEnvironment("folderPathforInputExcel");
		FileInputStream fileInStream = new FileInputStream(
				"C:\\Users\\703224653\\git\\MacquirePOC\\Reports\\ComparisonReport_11-03-2021_02-25-13.xlsx");
		XSSFWorkbook workBook = new XSSFWorkbook(fileInStream);// Open the xlsx and get the requested sheet from the //
																// workbook
		XSSFSheet outputSheet = workBook.getSheetAt(1);
		HashMap<Integer,ArrayList> hs=new HashMap<Integer,ArrayList>();
		
		ArrayList arr = new ArrayList();
		arr.add("A1");
		arr.add("A2");
		arr.add("A3");
		arr.add("A4");
		arr.add("A5");
		hs.put(1, arr);

//		String tempExcelFolderPath = GeneralUtils.getEnvironment("folderPathforInputExcel");
//		String tempInputExcelFile = GeneralUtils.getEnvironment("inputExcelTemplate");
////		String temppath = GeneralUtils.getEnvironment("folderPathforInputExcel");
//		FileInputStream tempfileInStream = new FileInputStream(
//				GeneralUtils.getEnvironment(tempExcelFolderPath + tempInputExcelFile + ".xlsx"));
//		XSSFWorkbook workBook1 = new XSSFWorkbook(tempfileInStream);// Open the xlsx and get the requested sheet from the																// workbook
//		XSSFSheet inputSheet = workBook.getSheetAt(0);
		int rc = outputSheet.getLastRowNum();// Get last row number
		System.out.println("Last row Number " + rc);
		// iterating on the rows
		for (int i = outputSheet.getFirstRowNum(); i < rc; i++) {
			System.out.println("I am on row ");
			// checking if the row is null
			if (outputSheet.getRow(i) != null) {
				// getting the last cell number of the row
				int cc = outputSheet.getRow(i).getLastCellNum();
				// iterating over the cells
				for (int j = 0; j < cc; j++) {
					System.out.println("I am on Cells ");
					// checking if the cell is null
					if (outputSheet.getRow(i).getCell(j) != null) {
						// temp variable to catch the value of I on which the filled Cell is found
						int temp = j;
						// checking if the first header index and last header index is null A1 A5
						if ((outputSheet.getRow(i).getCell(j)) != null
								&& (outputSheet.getRow(i).getCell(j + (4 - 1))) != null) {
							// Checking first header and last header is numeric
							if (outputSheet.getRow(i).getCell(j)
									.getCellType() == outputSheet.getRow(i).getCell(j).getCellType().NUMERIC) {
							} else {
								// checking the cell type is numeric
								if ((outputSheet.getRow(i).getCell(j)
										.getCellType() == outputSheet.getRow(i).getCell(j).getCellType().NUMERIC)
										|| (outputSheet.getRow(i).getCell(j + (4 - 1)).getCellType() == outputSheet
												.getRow(i).getCell(j + (4 - 1)).getCellType().NUMERIC)) {
								}
								// checking if the A1 and A5 are found
								else if (((outputSheet.getRow(i).getCell(j).getStringCellValue()).equals("A1"))
										&& ((outputSheet.getRow(i).getCell(j + (4 - 1)).getStringCellValue())
												.equals("A5"))) {
									// if A1 and A5 are found then headers will be will be appended
									for (int k = 0; k < 4;) {
										Cell c1 = outputSheet.getRow(i).getCell(j);
										// checking if the cell is null and contains k
										if (c1 != null) {
											// Checking the Cell Type
											switch (c1.getCellType()) {
											case STRING:
												// Checking if the headers are not present in the ignored list are
												// present in the cell
												
												for(int p=0;p<hs.get(1).size();) {
													if(outputSheet.getRow(i).getCell(j).getStringCellValue().equals(hs.get(1).get(p))) {
														
													}else {
														p++;
													}
												}
												if(hs.get(1).contains(outputSheet.getRow(i).getCell(j).getStringCellValue())) {
													System.out.println("Contains value"
															+ outputSheet.getRow(i).getCell(j).getStringCellValue());
												}
//												if (arr.equals(outputSheet.getRow(i).getCell(j).getStringCellValue())) {
//													System.out.println("Contains value"
//															+ outputSheet.getRow(i).getCell(j).getStringCellValue());
//												}
									

												break;
											case NUMERIC:
												// Control coming to Numeric if the cell type is numeric
												break;
											case BOOLEAN:

												break;
											case _NONE:
												break;

											case BLANK:
												break;

											default:
												break;
											}
										} else {

										}
										k++;
										j++;
//										if (k % numberOfCOlumns == 0 && rowcount != numberOfRows) {
//											rowcount++;
//											k = 0;
//											j = temp;
//											i++;
//										}
									}
									break;
								}

							}
						}
					}
				}
			}
		}

	}

	// Setting the property in the env file to genrate the summar report
	public static void valueSetterPropertyFile(String key, String Value) throws IOException {
		GeneralUtils.setEnvironment("summaryRepoValH3", getDate());
		GeneralUtils.setEnvironment("summaryRepoValH4", getTime());
	}

	// Writing the summary report values in the 3rd tab of comparison report
	public static void summaryReport(Sheet s2) throws IOException {
		int r = 0;
		for (int w = 1; w < 10;) {
			// creating the row
			Row row = s2.createRow(w);
			// creating the cell
			Cell cell = row.createCell(2);
			cell.setCellStyle(style);
			cell.setCellStyle(headerstyle);
			// setting the cell value from the env file
			cell.setCellValue(GeneralUtils.getEnvironment("summaryReportH" + w));
			Cell cell2 = row.createCell(3);
			cell2.setCellStyle(style);
			cell2.setCellStyle(passstyle);
			cell2.setCellValue(GeneralUtils.getEnvironment("summaryRepoValH" + w));
			w++;
		}
	}

	// getting current data
	private final static String getDate() {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		df.setTimeZone(TimeZone.getTimeZone("IST"));
		return (df.format(new Date()));
	}

	// getting current time
	private final static String getTime() {
		DateFormat df = new SimpleDateFormat("hh-mm-ss");
		// df.setTimeZone ( TimeZone.getTimeZone ( "PST" ) ) ;
		df.setTimeZone(TimeZone.getTimeZone("IST"));

		return (df.format(new Date()));
	}

	// returuing the no of rows of the table
	private static int tableRowCount(int xLoop, int fileNo) throws IOException {
		// row count variable to return the number of rows for a table
		int tRowCount = 0;
		int table1RowCount = Integer.parseInt((GeneralUtils.getProperty("noOfRowsInTable1")).split(",")[fileNo]);
		int table2RowCount = Integer.parseInt((GeneralUtils.getProperty("noOfRowsInTable2")).split(",")[fileNo]);
		int table3RowCount = Integer.parseInt((GeneralUtils.getProperty("noOfRowsInTable3")).split(",")[fileNo]);
		int table4RowCount = Integer.parseInt((GeneralUtils.getProperty("noOfRowsInTable4")).split(",")[fileNo]);
		int table5RowCount = Integer.parseInt((GeneralUtils.getProperty("noOfRowsInTable5")).split(",")[fileNo]);
		if (xLoop == 1) {
			tRowCount = table1RowCount;
		}
		if (xLoop == 2) {
			tRowCount = table2RowCount;
		}
		if (xLoop == 3) {
			tRowCount = table3RowCount;
		}
		if (xLoop == 4) {
			tRowCount = table4RowCount;
		}
		if (xLoop == 5) {
			tRowCount = table5RowCount;
		}
		return tRowCount;
	}

	// returuing the no of columns of the table
	private static int tablecolumnCount(int loopInt, int fileNo) throws IOException {
		int tColCount = 0;
		int table1ColCount = Integer.parseInt(splitValue((GeneralUtils.getProperty("noOfColumnsInTable1")), fileNo));
		int table2ColCount = Integer.parseInt(splitValue((GeneralUtils.getProperty("noOfColumnsInTable2")), fileNo));
		int table3ColCount = Integer.parseInt(splitValue((GeneralUtils.getProperty("noOfColumnsInTable3")), fileNo));
		int table4ColCount = Integer.parseInt(splitValue((GeneralUtils.getProperty("noOfColumnsInTable4")), fileNo));
		int table5ColCount = Integer.parseInt(splitValue((GeneralUtils.getProperty("noOfColumnsInTable5")), fileNo));
		if (loopInt == 1) {
			tColCount = table1ColCount;
		}
		if (loopInt == 2) {
			tColCount = table2ColCount;
		}
		if (loopInt == 3) {
			tColCount = table3ColCount;
		}
		if (loopInt == 4) {
			tColCount = table4ColCount;
		}
		if (loopInt == 5) {
			tColCount = table5ColCount;
		}
		return tColCount;
	}

	// calculate the variance of for the column
	private static String calculateWRTVariance(double itemList1, double itemList2, double var) {
		String result = "";
		System.out.println("itemList1" + itemList1);
		System.out.println("itemList2" + itemList2);
		System.out.println("var" + var);
		if (itemList1 < itemList2) {
			if (itemList2 - itemList1 <= var) {
				result = "Pass with Variance: " + var;
			} else {
				result = "Fail";
			}
		} else {
			if (itemList1 - var <= itemList2) {
				result = "Pass with Variance: " + var;
			} else {
				result = "Fail";
			}
		}
		return result;
	}

	// returing the variance as per the column level
	private static double varianceCalculator(int tableColumnCount, int iloop, int tableNumber,
			HashMap<Integer, ArrayList> missingValuesMap) throws IOException {
		// Variance variable to return the variance as per the column
		System.out.println("i value in variance method" + iloop);
		double VarianceCol1 = 0;
		double VarianceCol2 = 0;
		double VarianceCol3 = 0;
		double VarianceCol4 = 0;
		double VarianceCol5 = 0;
		System.out.println("tableNumber: " + tableNumber);
		// switch based on the no of table column count
		switch (tableColumnCount) {
		case 1:
			VarianceCol1 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(0)).toString()));
			break;
		case 2:
			VarianceCol1 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(0)).toString()));
			VarianceCol2 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(1)).toString()));
			break;
		case 3:
			VarianceCol1 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(0)).toString()));
			VarianceCol2 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(1)).toString()));
			VarianceCol3 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(2)).toString()));
			break;
		case 4:
			VarianceCol1 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(0)).toString()));
			VarianceCol2 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(1)).toString()));
			VarianceCol3 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(2)).toString()));
			VarianceCol4 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(3)).toString()));
			break;
		case 5:
			VarianceCol1 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(0)).toString()));
			VarianceCol2 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(1)).toString()));
			VarianceCol3 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(2)).toString()));
			VarianceCol4 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(3)).toString()));
			VarianceCol5 = Integer.parseInt(GeneralUtils.getProperty("tolranceValueTable" + tableNumber + "Col"
					+ ((missingValuesMap.get(tableNumber)).get(4)).toString()));
			break;
		}

		double variance = 0;

		switch (tableColumnCount) {
		case 5:
			if (iloop % tableColumnCount == 0) {
				variance = VarianceCol1;
			} else if (iloop % tableColumnCount == 1) {
				variance = VarianceCol2;
			} else if (iloop % tableColumnCount == 2) {
				variance = VarianceCol3;
			} else if (iloop % tableColumnCount == 3) {
				variance = VarianceCol4;
			} else if (iloop % tableColumnCount == 4) {
				variance = VarianceCol5;
			}
			break;

		case 4:
			if (iloop % tableColumnCount == 3) {
				variance = VarianceCol4;

			} else if (iloop % tableColumnCount == 0) {
				variance = VarianceCol1;

			} else if (iloop % tableColumnCount == 1) {
				variance = VarianceCol2;

			} else if (iloop % tableColumnCount == 2) {
				variance = VarianceCol3;

			}
			break;

		case 3:
			if (iloop % tableColumnCount == 2) {
				variance = VarianceCol3;
			} else if (iloop % tableColumnCount == 0) {
				variance = VarianceCol1;
			} else if (iloop % tableColumnCount == 1) {
				variance = VarianceCol2;
			}
			break;

		case 2:
			System.out.println("VarianceCol1: " + VarianceCol1);
			System.out.println("VarianceCol2: " + VarianceCol2);
			if (iloop % tableColumnCount == 0) {
				variance = VarianceCol1;
			} else if (iloop % tableColumnCount == 1) {
				variance = VarianceCol2;
			}
			break;

		case 1:
			if (iloop % tableColumnCount == 0) {
				variance = VarianceCol1;
			}
			break;
		}
		return variance;
	}

}
