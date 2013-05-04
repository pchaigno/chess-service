package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handle the statistics about every parameters used for score computation (for openings mainly)
 * @author clemgaut
 *
 */
public class StatsManager {
	private static Properties conf = null;
	private static final String CONFIG_FILE = "stats.properties";
	// List of properties' name
	public static final String STATS_NB_PLAY = "nb_play";
	public static final String STATS_PROBAW = "proba_win";
	public static final String STATS_PROBAD = "proba_draw";
	
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
	 * Return the stat about the property entity
	 * @param propertyEntity the entity to acces the stat about
	 * @param stat The stat asked
	 * @return
	 */
	public static String getProperty(String propertyEntity, Statistic stat) {
		return getConfiguration().getProperty(propertyEntity+"."+stat);
	}
	
	/**
	 * Change the entity's stat to the value propertyValue
	 * @param propertyEntity propertyEntity the entity to change the stat about
	 * @param stat The stat asked @see Statistic
	 * @param propertyValue the new value
	 */
	public static void setProperty(String propertyEntity, Statistic stat, String propertyValue){
		getConfiguration().setProperty(propertyEntity+"."+stat, propertyValue);
	}
	
	/**
	 * Save the properties.
	 * @return True if the properties were saved successfully, false otherwise.
	 */
	public static boolean saveProperties() {
		try {
			getConfiguration().store(new FileOutputStream(CONFIG_FILE), "Game statistics");
			return true;
		} catch (FileNotFoundException e) {
			// TODO Generate the file.
			System.err.println("Config file ("+CONFIG_FILE+") not found.");
		} catch (IOException e) {
			System.err.println("Unable to load the config file.");
			System.err.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Compute and save the new statistics about all values : the old one stored and the new in parameters
	 * @param propertyEntity the entity to compute the stats about
	 * @param mean the mean of the new data
	 * @param variance the variance of the new data
	 * @param weight the weight (size) of the new data
	 * @return true if entity is updated, false otherwise
	 */
	public static boolean updateEntity(String propertyEntity, double mean, double variance, int weight){
		double newMean = computeMean(propertyEntity, mean, weight);
		double newVariance = computeVariance(propertyEntity, mean, variance, weight);
		int newWeight = computeWeight(propertyEntity, weight);
		
		setProperty(propertyEntity, Statistic.MEAN, newMean+"");
		setProperty(propertyEntity, Statistic.VARIANCE, newVariance+"");
		setProperty(propertyEntity, Statistic.WEIGHT, newWeight+"");
		
		return saveProperties();
	}

	private static int computeWeight(String propertyEntity, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		return currentWeight+weight;		
	}

	private static double computeVariance(String propertyEntity, double mean, double variance, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		double currentVariance = Double.parseDouble(getProperty(propertyEntity, Statistic.VARIANCE));
		
		double newVariance = (Math.pow(currentWeight,2)*currentVariance
				+currentWeight*weight*(currentVariance+variance+Math.pow(currentMean, 2)+Math.pow(mean, 2))
				+Math.pow(weight, 2)*variance
				-2*currentWeight*weight*currentMean*mean)/(Math.pow(currentWeight+weight, 2));
		
		return newVariance;
	}

	private static double computeMean(String propertyEntity, double mean, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		
		return (currentMean*currentWeight+mean*weight)/(currentMean+weight);
	}
	
	private static void init() {
		conf = new Properties();
		setProperty(STATS_NB_PLAY,Statistic.MEAN, "1000");
		setProperty(STATS_NB_PLAY,Statistic.VARIANCE, "50");
		setProperty(STATS_NB_PLAY,Statistic.WEIGHT, "1");
		
		setProperty(STATS_PROBAD,Statistic.MEAN, "0.2");
		setProperty(STATS_PROBAD,Statistic.VARIANCE, "0.1");
		setProperty(STATS_PROBAD,Statistic.WEIGHT, "3");
		
		setProperty(STATS_PROBAW,Statistic.MEAN, "0.5");
		setProperty(STATS_PROBAW,Statistic.VARIANCE, "0.05");
		setProperty(STATS_PROBAW,Statistic.WEIGHT, "2");
		saveProperties();
	}
	
	/**
	 * Main method. Just initialize properties file.
	 * @param args
	 */
	public static void main(String[] args) {
		init();
	}
	
}
