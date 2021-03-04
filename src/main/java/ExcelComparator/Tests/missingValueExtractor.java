package ExcelComparator.Tests;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class missingValueExtractor extends csvUtils {
	private static Logger log = LogManager.getLogger(missingValueExtractor.class.getName());
//This method is used for extracting the Missing values from the header in between Prod and UAT data.
	
	public missingValueExtractor() throws IOException {
	
		// TODO Auto-generated constructor stub
		
	}

	static String configPropertyFilePath = "C:\\Users\\ankit\\git\\repository\\ExcelComparator\\src\\test\\java\\SelniumPractice\\WebAutomation\\config.properties";
	static String envPropertyFilePath = "C:\\Users\\ankit\\git\\repository\\ExcelComparator\\src\\test\\java\\SelniumPractice\\WebAutomation\\env.properties";
	static ArrayList prodColHeaderT1 = new ArrayList();
	static ArrayList prodColHeaderT2 = new ArrayList();
	static ArrayList prodColHeaderT3 = new ArrayList();
	static ArrayList prodColHeaderT4 = new ArrayList();
	static ArrayList prodColHeaderT5 = new ArrayList();	
	static ArrayList prodRowHeaderT1 = new ArrayList();
	static ArrayList prodRowHeaderT2  = new ArrayList();
	static ArrayList prodRowHeaderT3 = new ArrayList();
	static ArrayList prodRowHeaderT4 = new ArrayList();
	static ArrayList prodRowHeaderT5  = new ArrayList();
	
	public static void main(String[] args) throws IOException {
	
	}
	
	public static ArrayList rowCompare(String fileName, int nr, String fr, String lr, int tableNo)throws IOException {
		
		ArrayList tempRowHeader = new ArrayList();
		if(fileName.equalsIgnoreCase(getValFromEnvPropFile("inputExcelFileName")))
		{
			if(tableNo == 1)
				prodColHeaderT1 = getColHeader(fileName, nr, fr, lr);
			else if(tableNo == 2)
				prodColHeaderT2 = getColHeader(fileName,nr, fr, lr);
			else if(tableNo == 3)
				prodColHeaderT3 = getColHeader(fileName, nr, fr, lr);
			else if(tableNo == 4)
				prodColHeaderT4 = getColHeader(fileName, nr, fr, lr);
			else if(tableNo == 5)
				prodColHeaderT5 = getColHeader(fileName,nr, fr, lr);
		}
		else
		{
			/*
			 * HashMap<Integer, String> test1 = new HashMap<>(); test1.put(1, "GG");
			 * test1.put(2, "GG1"); HashMap<Integer, String> test2 = new HashMap<>();
			 * test2.put(1, "GG"); System.out.println(test1.re);
			 */
			
			tempRowHeader = getColHeader(fileName, nr, fr, lr);			
			if(tableNo == 1)
				tempRowHeader.removeAll(prodColHeaderT1);
			else if(tableNo == 2)
				tempRowHeader.removeAll(prodColHeaderT2);
			else if(tableNo == 3)
				tempRowHeader.removeAll(prodColHeaderT3);
			else if(tableNo == 4)
				tempRowHeader.removeAll(prodColHeaderT4);
			else if(tableNo == 5)
				tempRowHeader.removeAll(prodColHeaderT5);
		}
		return tempRowHeader;
	}
	


	//This method is used for returning the list of the missing header in between Prod and UAT.
	public static ArrayList headerCompare(String fileName, int nc, String fh, String lh, int tableNo)throws IOException {		
		ArrayList tempColHeader = new ArrayList();
		if(fileName.equalsIgnoreCase("ProdData"))
		{
			if(tableNo == 1)
				prodColHeaderT1 = getColHeader(fileName, nc, fh, lh);
			else if(tableNo == 2)
				prodColHeaderT2 = getColHeader(fileName, nc, fh, lh);
			else if(tableNo == 3)
				prodColHeaderT3 = getColHeader(fileName, nc, fh, lh);
			else if(tableNo == 4)
				prodColHeaderT4 = getColHeader(fileName, nc, fh, lh);
			else if(tableNo == 5)
				prodColHeaderT5 = getColHeader(fileName, nc, fh, lh);
		}
		else
		{
			tempColHeader = getColHeader(fileName, nc, fh, lh);
			ArrayList tempData = new ArrayList();
			ArrayList tempDataList = new ArrayList();
			tempData =  getColHeader(fileName, nc, fh, lh);
			
			if(tableNo == 1)
			{
				for(int p=0;p<prodColHeaderT1.size();p++)
				{
					if(tempColHeader.contains(prodColHeaderT1.get(p)))
					{
						tempDataList.add((tempColHeader.indexOf(prodColHeaderT1.get(p)))+1);
					}					
				}
				tempColHeader.removeAll(prodColHeaderT1);
				//System.out.println("tempColHeader : "+  tempColHeader);				
			}
			else if(tableNo == 2) {
				for(int p=0;p<prodColHeaderT2.size();p++)
				{
					if(tempColHeader.contains(prodColHeaderT2.get(p)))
					{
						tempDataList.add((tempColHeader.indexOf(prodColHeaderT2.get(p))) + 1);
					}					
				}
				tempColHeader.removeAll(prodColHeaderT2);
				//missingValuesMap.put(tableNo, tempData.indexOf(tempColHeader));
			}else if(tableNo == 3) {
				for(int p=0;p<prodColHeaderT3.size();p++)
				{
					if(tempColHeader.contains(prodColHeaderT3.get(p)))
					{
						tempDataList.add((tempColHeader.indexOf(prodColHeaderT3.get(p))) + 1);
					}					
				}
				tempColHeader.removeAll(prodColHeaderT3);
			}else if(tableNo == 4) {
				for(int p=0;p<prodColHeaderT4.size();p++)
				{
					if(tempColHeader.contains(prodColHeaderT4.get(p)))
					{
						tempDataList.add((tempColHeader.indexOf(prodColHeaderT4.get(p))) + 1);
					}					
				}
				tempColHeader.removeAll(prodColHeaderT4);
			}else if(tableNo == 5) {
				for(int p=0;p<prodColHeaderT5.size();p++)
				{
					if(tempColHeader.contains(prodColHeaderT5.get(p)))
					{
						tempDataList.add((tempColHeader.indexOf(prodColHeaderT5.get(p))) + 1);
					}					
				}
				tempColHeader.removeAll(prodColHeaderT5);
			}			
			missingValuesMap.put(tableNo, tempDataList);
			System.out.println("missingValuesMap : " + missingValuesMap);
		}
		return tempColHeader;
	}
	
	//This method is used to extract the header with respect to Column count , First and last header of the table.
	public static ArrayList getColHeader( String fileName,int colCount , String firstHeader , String lastHeader) throws IOException{
		
		ArrayList ClmHdrsPrd = new ArrayList();
		String path= "C:\\Users\\703224653\\git\\MacquirePOC\\Test Data\\";
		// TODO Auto-generated method stub
		try {
		FileInputStream fis=new FileInputStream(path+fileName+".xlsx");
		XSSFWorkbook wb=new XSSFWorkbook(fis);
		ArrayList<String> al1= new ArrayList<>();
		XSSFSheet s=wb.getSheetAt(0);		
		int noOfRows=s.getLastRowNum();
		for(int i=s.getFirstRowNum();i<noOfRows;i++) {
			if(s.getRow(i) != null) {
			int noOfCells=s.getRow(i).getLastCellNum();
			for(int j=0;j<noOfCells;j++) {
				Cell cell=s.getRow(i).getCell(j);
				if(cell != null) {
					if(cell.getCellType()==cell.getCellType().NUMERIC) 
					{
//						System.out.println(cell.getNumericCellValue());
					}
					else if(cell.getStringCellValue().equals(firstHeader)) 
					{
						if(s.getRow(i).getCell(j+(colCount-1)).getStringCellValue().equalsIgnoreCase(lastHeader))
						{
							for(int k=j;k<colCount+j;k++) {
									ClmHdrsPrd.add(s.getRow(i).getCell(k).getStringCellValue());
							}
							
						}
					}
				}
			}
		}
	}
		}catch (IOException e) {
	      System.out.println(e);
	    }
		return ClmHdrsPrd;

	}

}
