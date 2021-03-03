package ExcelComparator.Tests;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ExcelComparator.Utils.GeneralUtils;

public class missingValueExtractor extends csvUtils {
	private static Logger log = LogManager.getLogger(missingValueExtractor.class.getName());
//This method is used for extracting the Missing values from the header in between Prod and UAT data.
	
	public missingValueExtractor() throws IOException {
	
		// TODO Auto-generated constructor stub
		
	}
	 static String user_dir = System.getProperty("user.dir");
		static String configPropertyFilePath = user_dir + "\\src\\test\\java\\SelniumPractice\\WebAutomation\\config.properties";
		static String envPropertyFilePath = user_dir+ "\\src\\test\\java\\SelniumPractice\\WebAutomation\\env.properties";
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
	
//added
	//added

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
			if(tableNo == 1)
				tempColHeader.removeAll(prodColHeaderT1);
			else if(tableNo == 2)
				tempColHeader.removeAll(prodColHeaderT2);
			else if(tableNo == 3)
				tempColHeader.removeAll(prodColHeaderT3);
			else if(tableNo == 4)
				tempColHeader.removeAll(prodColHeaderT4);
			else if(tableNo == 5)
				tempColHeader.removeAll(prodColHeaderT5);
		}
		return tempColHeader;
	}
	
	//This method is used to extract the header with respect to Column count , First and last header of the table.
	public static ArrayList getColHeader( String fileName,int colCount , String firstHeader , String lastHeader) throws IOException{
		
		ArrayList ClmHdrsPrd = new ArrayList();
		//String path= "C:\\Users\\ankit\\Desktop\\Excel\\";
		String path=GeneralUtils.getEnvironment("folderPathforInputExcel");
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
