package ExcelComparator.Runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ExcelComparator.Utils.GeneralUtils;
import ExcelComparator.Tests.csvUtils;

public class ExcelcsvComp extends csvUtils{
	private static Logger log = LogManager.getLogger(ExcelcsvComp.class.getName());
	
	//This class is used for Comparing PROD and UAT Excel and produce the results.
	//New Build
	//test
	
	public ExcelcsvComp() throws IOException {		
		super();		 
	}
	static long startTime = System.currentTimeMillis();
		
	public static void main(String[] args) throws Exception  {
		log.info("Comparison between " + GeneralUtils.getEnvironment("inputExcelFileName")+".xlsx"+" & "+ GeneralUtils.getEnvironment("inputExcelTemplate")+".xlsx has been iniated.");
		System.out.println("Comparison between "+ GeneralUtils.getEnvironment("inputExcelFileName")+".xlsx"+" & "+ GeneralUtils.getEnvironment("inputExcelTemplate")+".xlsx has been iniated.");
		structureCheck(GeneralUtils.getEnvironment("inputExcelFileName"),1);
		log.info("Validated the Structure of the table for "+GeneralUtils.getEnvironment("inputExcelFileName") );
		finalData = new StringBuffer();
		structureCheck(GeneralUtils.getEnvironment("inputExcelTemplate"),2);		
		log.info("Validated the Structure of table for "+GeneralUtils.getEnvironment("inputExcelTemplate") );
		//Compare Actual and Baseline CSV and Generates a 3rd CSV 
		System.out.println("===================="+missingColm);
		csvComparison(missingValuesMap);
		log.info("Compared the ACtual and Baseline CSV and Generates 3rd CSV");
		//Converts Back the CSV to Excel
		long endTime   = System.currentTimeMillis();
		//Values Updated in ENV Property file for Summary Report
		//valueSetterPropertyFile("summaryRepoValH5",totalTime(startTime,endTime));
		GeneralUtils.setEnvironment("summaryRepoValH5",totalTime(startTime,endTime));
		log.info("Values updated in the ENV file");
		//Final Excel Creation.
		csvtoExcelCOnverion();
		log.info("Converted CSV into Excel");
		System.out.println("Comparison has been completed.\n The File is placed on the below path \n"+GeneralUtils.getEnvironment("finalExcelFolderPath"));		
	log.info("Comparison has been completed");	
	}
	
	//totalTime method returns the total time took to run the code in seconds.
	public static String totalTime(long startTime,long endTime) {
		long totalTime=endTime-startTime;
		long totalRuntime=totalTime/1000;
		String codeRunTime=String.valueOf(totalRuntime)+" SEC";		
		return codeRunTime;
	}

}
