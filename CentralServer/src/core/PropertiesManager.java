package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
	
	private static Properties conf = null;
	private static final String CONFIG_FILE = "conf.properties";
	//List of properties' name
	public static final String PROPERTY_PORT_LISTENER = "port";
	public static final String PROPERTY_CONNECT_TIMEOUT = "connect_timeout";
	public static final String PROPERTY_READ_TIMEOUT = "read_timeout";
	public static final String PROPERTY_DATABASE = "database";
	
	/**
	 * Return the object containing the configuration
	 * @return Return the object containing the configuration
	 */
	private static Properties getConfiguration() {
		if (conf == null) {
			/** Chargement des propriétés de configuration. */
			conf = new Properties();
			try {
				conf.load(new FileInputStream(CONFIG_FILE));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conf;
	}
	
	public static String getProperty(String propertyName){
		return getConfiguration().getProperty(propertyName);
	}
	
	/**
	 * Set the property propertyName to the value propertyValue
	 * @param propertyName the name of the property
	 * @param propertyValue the value of the property
	 */
	public static void setProperty(String propertyName, String propertyValue){
		getConfiguration().setProperty(propertyName, propertyValue);
	}
	

	public static boolean saveProperties(){
		try {
			getConfiguration().store(new FileOutputStream(CONFIG_FILE), "Properties for central server");
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Initialize the parameters to their default values and save them
	 */
	private static void init(){
		conf = new Properties();
		setProperty(PROPERTY_CONNECT_TIMEOUT, "2000");
		setProperty(PROPERTY_READ_TIMEOUT, "3000");
		setProperty(PROPERTY_DATABASE, "resources_empty.db");
		setProperty(PROPERTY_PORT_LISTENER, "9998");
		saveProperties();
	}
	
	public static void main(String[] args){
		init();
	}
}
