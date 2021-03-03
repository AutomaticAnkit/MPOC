/**
 * The GeneralUtils class is used to write the commonly used methods
 *
 * @author Genpact
 * @version 
 * 
 */
package ExcelComparator.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneralUtils {
		private static Logger log = LogManager.getLogger(GeneralUtils.class.getName());
	public static String getProperty(String Key) throws IOException
	{
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\ExcelComparator\\Resources\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		return prop.getProperty(Key) ;
	}
	//getting values from prop file
	public static String getEnvironment(String Key) throws IOException
	{
	   	FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\ExcelComparator\\Resources\\env.properties");
		Properties prop = new Properties();
		prop.load(fis);
	   	return prop.getProperty(Key) ;
	}
	public static void setEnvironment(String Key,String value) throws IOException
	{
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\ExcelComparator\\Resources\\env.properties");
		Properties prop_in = new Properties();
		prop_in.load(fis);
		fis.close();
		  try (OutputStream output = new FileOutputStream(System.getProperty("user.dir")+"\\src\\test\\java\\ExcelComparator\\Resources\\env.properties")) {
	            prop_in.setProperty(Key, value);
	            // save properties to project root folder
	            prop_in.store(output, null);
	            } catch (IOException io) {
	            io.printStackTrace();
	        }
	    } 
	}


