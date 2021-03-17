package Library;

import java.io.IOException;

import ExcelComparator.Utils.GeneralUtils;

public class reportUtility {

	public String getReportConfigPath() throws IOException{
		String reportConfigPath =System.getProperty("user.dir")+"\\ExcelComparator\\src\\main\\java\\Library\\extent-config.xml";
		if(reportConfigPath!= null) return reportConfigPath;
		else throw new RuntimeException("Report Config Path not specified in the Configuration.properties file for the Key:reportConfigPath");		
	}
}
