package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Handle the statistics about every parameters used for score computation (for openings mainly).
 */
public class StatsManager {
	private static Properties conf = null;
	private static final String CONFIG_FILE = "stats.properties";
	// List of properties' name
	public static final String STATS_NB_PLAY = "nb_play";
	public static final String STATS_PROBAW = "proba_win";
	public static final String STATS_PROBAD = "proba_draw";
	
	//TODO mieux gerer les parametres avec un "structure" commune (un nom et un rang par param) pour plus de généricite
	private static final int NB_PARAMS = 3;
	private static final int RANGE_NB_PLAY = 0;
	private static final int RANGE_PROBAD = 1;
	private static final int RANGE_PROBAW = 2;
	
	
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
	 * Return the stat about the property entity.
	 * @param propertyEntity The entity to acces the stat about.
	 * @param stat The stat asked.
	 * @return TODO
	 */
	public static String getProperty(String propertyEntity, Statistic stat) {
		return getConfiguration().getProperty(propertyEntity+"."+stat);
	}
	
	/**
	 * Change the entity's stat to the value propertyValue.
	 * @param propertyEntity The entity to change the stat about.
	 * @param stat The stat asked @see Statistic.
	 * @param propertyValue The new value.
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
	 * Update the statistics using the moves
	 * @param moves The moves that will be played.
	 * @return True if stats are updated, false otherwise.
	 */
	public static boolean updateStatistics(Set<OpeningSuggestion> moves){
		double[] movesStats = computeStats(moves);
		
		boolean updated = updateEntity(STATS_NB_PLAY, movesStats[RANGE_NB_PLAY], movesStats[RANGE_NB_PLAY+NB_PARAMS], moves.size());
		updated &= updateEntity(STATS_PROBAD, movesStats[RANGE_PROBAD], movesStats[RANGE_PROBAD+NB_PARAMS], moves.size());
		updated &= updateEntity(STATS_PROBAW, movesStats[RANGE_PROBAW], movesStats[RANGE_PROBAW+NB_PARAMS], moves.size());
		
		return updated;
	}
	
	/**
	 * Return a table containing statistics about the moves.
	 * @param moves The moves to compute the stats about.
	 * @return A table of size 2*NB_PARAMS. The NB_PARAMS firsts elements contain the mean, the NB_PARAMS last the variance 
	 * and the 2 "subtables" are ordered by RANGE_...
	 */
	private static double[] computeStats(Set<OpeningSuggestion> moves){
		double[] stats = new double[2*NB_PARAMS];
		Arrays.fill(stats, 0);
		
		for(OpeningSuggestion move: moves) {
			//TODO voir si ici, il fait appeler les fonctions des parametres (le f dans a*f(x1)/A)
			stats[RANGE_NB_PLAY] += move.computeScoreNbPlay();
			stats[RANGE_PROBAD] += move.getProbaDraw();
			stats[RANGE_PROBAW] += move.computeScoreProbaWin();
			stats[RANGE_NB_PLAY+NB_PARAMS] += Math.pow(move.computeScoreNbPlay(), 2);
			stats[RANGE_PROBAD+NB_PARAMS] += Math.pow(move.getProbaDraw(), 2);
			stats[RANGE_PROBAW+NB_PARAMS] += Math.pow(move.computeScoreProbaWin(), 2);
		}
		if(moves.size()>0) {
			for(int i=0 ; i<NB_PARAMS ; i++) {
				stats[i] /= moves.size();
			}
		}
		for(int i=0 ; i<3 ; i++)
			stats[NB_PARAMS+i] -= Math.pow(stats[i], 2);
		
		return stats;
	}
	/**
	 * Compute and save the new statistics about all values : the old one stored and the new in parameters
	 * @param propertyEntity The entity to compute the stats about.
	 * @param mean The mean of the new data.
	 * @param variance The variance of the new data.
	 * @param weight The weight (size) of the new data.
	 * @return True if entity is updated, false otherwise.
	 */
	private static boolean updateEntity(String propertyEntity, double mean, double variance, int weight){
		double newMean = computeMean(propertyEntity, mean, weight);
		double newVariance = computeVariance(propertyEntity, mean, variance, weight);
		int newWeight = computeWeight(propertyEntity, weight);
		
		setProperty(propertyEntity, Statistic.MEAN, newMean+"");
		setProperty(propertyEntity, Statistic.VARIANCE, newVariance+"");
		setProperty(propertyEntity, Statistic.WEIGHT, newWeight+"");
		
		return saveProperties();
	}

	/**
	 * Compute the weight.
	 * Add the new one to the old one.
	 * @param propertyEntity The properties.
	 * @param weight The old weight.
	 * @return The new weight.
	 */
	private static int computeWeight(String propertyEntity, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		return currentWeight+weight;		
	}

	/**
	 * Compute the variance.
	 * @param propertyEntity The properties.
	 * @param mean TODO
	 * @param variance The old variance.
	 * @param weight TODO
	 * @return The new variance.
	 */
	private static double computeVariance(String propertyEntity, double mean, double variance, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		double currentVariance = Double.parseDouble(getProperty(propertyEntity, Statistic.VARIANCE));
		
		if((weight+currentWeight)>0) {
			return (Math.pow(currentWeight,2)*currentVariance
					+currentWeight*weight*(currentVariance+variance+Math.pow(currentMean, 2)+Math.pow(mean, 2))
					+Math.pow(weight, 2)*variance
					-2*currentWeight*weight*currentMean*mean)/(Math.pow(currentWeight+weight, 2));
		}
		
		return 0;
	}

	/**
	 * Compute the mean.
	 * @param propertyEntity The properties.
	 * @param mean The old mean.
	 * @param weight TODO
	 * @return The new mean.
	 */
	private static double computeMean(String propertyEntity, double mean, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		if(weight+currentWeight > 0) {
			return (currentMean*currentWeight+mean*weight)/(currentMean+weight);
		} else {
			return 0;
		}
	}
	
	/**
	 * Initialize the properties with default values.
	 */
	private static void init() {
		conf = new Properties();
		setProperty(STATS_NB_PLAY, Statistic.MEAN, "0");
		setProperty(STATS_NB_PLAY, Statistic.VARIANCE, "0");
		setProperty(STATS_NB_PLAY, Statistic.WEIGHT, "0");
		
		setProperty(STATS_PROBAD, Statistic.MEAN, "0");
		setProperty(STATS_PROBAD, Statistic.VARIANCE, "0");
		setProperty(STATS_PROBAD, Statistic.WEIGHT, "0");
		
		setProperty(STATS_PROBAW, Statistic.MEAN, "0");
		setProperty(STATS_PROBAW, Statistic.VARIANCE, "0");
		setProperty(STATS_PROBAW, Statistic.WEIGHT, "0");
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