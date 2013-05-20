package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handle the properties of the software such as the listening port or the timeouts.
 * @author Clement Gautrais
 */
public class PropertiesManager {
	private static Properties conf = null;
	private static final String CONFIG_FILE = "conf.properties";
	// List of properties' name
	public static final String PROPERTY_PORT_LISTENER = "port";
	public static final String PROPERTY_CONNECT_TIMEOUT = "connect_timeout";
	public static final String PROPERTY_READ_TIMEOUT = "read_timeout";
	public static final String PROPERTY_DATABASE = "database";
	public static final String PROPERTY_WEIGHT_NBPLAY="weight_nbPlay";
	public static final String PROPERTY_WEIGHT_PROBAW="weight_probaW";
	public static final String PROPERTY_WEIGHT_ENGINESCORE="weight_engineScore";
	public static final String PROPERTY_WEIGHT_DEPTH="weight_depth";
	
	/**
	 * Load the configuration properties.
	 * @return The object containing the configuration.
	 */
	private static Properties getConfiguration() {
		if(conf == null) {
			/** Load the configuration properties. */
			conf = new Properties();
			try {
				conf.load(new FileInputStream(CONFIG_FILE));
			} catch(FileNotFoundException e) {
				// If no config file exist, we create one
				System.err.println("Config file ("+CONFIG_FILE+") not found. Creating a new one");
				File fileConfig = new File(CONFIG_FILE);
				try {
					fileConfig.createNewFile();
					init();
				} catch (IOException e1) {
					System.err.println("Unable to create the config file ("+CONFIG_FILE+").");
				}
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
		setProperty(PROPERTY_READ_TIMEOUT, "5000");
		setProperty(PROPERTY_DATABASE, "resources.db");
		setProperty(PROPERTY_PORT_LISTENER, "9998");
		setProperty(PROPERTY_WEIGHT_NBPLAY, "0.8");
		setProperty(PROPERTY_WEIGHT_PROBAW, "0.2");
		setProperty(PROPERTY_WEIGHT_DEPTH, "0.3");
		setProperty(PROPERTY_WEIGHT_ENGINESCORE, "0.7");
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