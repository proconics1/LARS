package bsi.lars.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * This class manages the configuration data. At the moment, only the database path is here.
 * The configuration is always read from file or written to file.
 * 
 *
 */
public class Config {
	/**
	 * Keywords
	 */
	private static final String DB_PATH = "DB-PATH";
	private static final String PERCENTAGE = "PERCENTAGE";//TODO: remove
	private static final String OPENREPORT = "OPENREPORT";
	
	/**
	 *Configuration File Name
	 */
	private static final String CONFIG_XML = "config.xml";
	public static final boolean DEBUG = true;
	private static File configFile = new File(CONFIG_XML);
	
	/**
	 * Function that retrieves the value for the passed keyword from the file.
	 * @param key the keyword for which the value is to be retrieved
	 * @return returns the value for the passed keyword
	 */
	private static String getProperty(String key) {
		Properties props = new Properties();
		if(configFile.exists()) {
			try {
				FileInputStream in = new FileInputStream(configFile);
				props.loadFromXML(in);
				in.close();
			} catch (Exception e) {
			}
		}
		return props.getProperty(key);
	}
	
	/**
	 * Function that writes the value for the passed keyword to the file.
	 * @see Properties#setProperty(String, String)
	 * @param key key the keyword for which the value should be written
	 * @param value the value for the passed keyword
	 * @return the previous value for the passed keyword, or null if no value was previously set.
	 */
	private static Object setProperty(String key, String value) {
		Properties props = new Properties();
		if(configFile.exists()) {
			try {
				FileInputStream in = new FileInputStream(configFile);
				props.loadFromXML(in);
				in.close();
			} catch (Exception e) {
			}
		}
		Object result = props.setProperty(key, value);
		try {
			FileOutputStream os = new FileOutputStream(configFile);
			props.storeToXML(os, "LARS ICS - configuration file");
			os.close();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * Visible functions that abstract the keywords
	 *read the Database path
	 * @return returns the database path stored in the configuration file, or null if nothing is specified
	 */
	public static String getDataBasePath() {
		return getProperty(DB_PATH);
	}
	
	/**
	 * Visible functions that abstract the keywords
	 * write the database path
	 * @param dbPath the path to the database to be stored in the configuration file
	 */
	public static void setDataBasePath(String dbPath) {
		setProperty(DB_PATH, dbPath);
	}

	/**
	 * Reads the percentage for the calculation
	 * @param default_value Standard-Value for the parameter
	 * @return Returns the currently specified percentage
	 */
	public static int getScorePercentage(int default_value) {
		String value = getProperty(PERCENTAGE);
		if(value != null) {
			try{
				return Integer.parseInt(value);
			}catch(Exception e) {
			}
		}
		setProperty(PERCENTAGE, default_value + "");
		return default_value;
	}

	/**
	 * Reads if the reports should be displayed directly after generation
	 * @return Returns true if the reports should be opened after generation.
	 */
	public static boolean isOpenReportAfterCreation() {
		String value = getProperty(OPENREPORT);
		if(value != null) {
			try{
				return Boolean.parseBoolean(value);
			}catch(Exception e) {
			}
		}
		return true;
	}
}
