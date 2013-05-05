package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handle the properties of the software such as the listening port or the timeouts.
 */
public class PropertiesManager {
	private static Properties conf = null;
	private static final String CONFIG_FILE = "conf.properties";
	// List of properties' name
	public static final String PROPERTY_PORT_LISTENER = "port";
	public static final String PROPERTY_CONNECT_TIMEOUT = "connect_timeout";
	public static final String PROPERTY_READ_TIMEOUT = "read_timeout";
	public static final String PROPERTY_DATABASE = "database";
	
	/**
	 * @return The object containing the configuration.
	 */
	private static Properties getConfiguration() {
		if(conf == null) {
			/** Load the configuration's properties. */
			conf = new Properties();
			try {
				conf.load(new FileInputStream(CONFIG_FILE));
			} catch(FileNotFoundException e) {
				// TODO Generate the file.
				System.err.println("Config file ("+CONFIG_FILE+") not found.");
			} catch(IOException e) {
				System.err.println("Unable to load the config file.");
				System.err.println(e.getMessage());
			}
		}
		return conf;
	}
	
	/**
	 * @param propertyName The name of the property.
	 * @return The property named propertyName.
	 */
	public static String getProperty(String propertyName) {
		return getConfiguration().getProperty(propertyName);
	}
	
	/**
	 * Set the property propertyName to the value propertyValue.
	 * @param propertyName The name of the property.
	 * @param propertyValue The value of the property.
	 */
	public static void setProperty(String propertyName, String propertyValue){
		getConfiguration().setProperty(propertyName, propertyValue);
	}
	
	/**
	 * Save the properties.
	 * @return True if the properties were saved successfully, false otherwise.
	 */
	public static boolean saveProperties() {
		try {
			getConfiguration().store(new FileOutputStream(CONFIG_FILE), "Properties for central server");
			return true;
		} catch(FileNotFoundException e) {
			// TODO Generate the file.
			System.err.println("Config file ("+CONFIG_FILE+") not found.");
		} catch(IOException e) {
			System.err.println("Unable to load the config file.");
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Initialize the properties to their default values and save them.
	 */
	private static void init() {
		conf = new Properties();
		setProperty(PROPERTY_CONNECT_TIMEOUT, "2000");
		setProperty(PROPERTY_READ_TIMEOUT, "3000");
		setProperty(PROPERTY_DATABASE, "databases/resources/resources_empty.db");
		setProperty(PROPERTY_PORT_LISTENER, "9998");
		saveProperties();
	}
	
	/**
	 * Main method. Just initialize the properties to their defaults values.
	 * @param args
	 */
	public static void main(String[] args) {
		init();
	}
}