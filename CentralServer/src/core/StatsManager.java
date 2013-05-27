package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Handle the statistics about every parameters used for score computation (for openings mainly).
 * @author Clement Gautrais
 */
public class StatsManager {
	private static Properties conf = null;
	private static final String CONFIG_FILE = "stats.properties";
	// List of properties' name
	public static final String STATS_NB_PLAY = "nb_play";
	public static final String STATS_PROBAW = "proba_win";
	public static final String STATS_BOT_DEPTH = "bot_depth";
	public static final String STATS_BOT_SCORE = "bot_score";
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
				// If no stat file exist, we create one
				System.err.println("Config file ("+CONFIG_FILE+") not found. Creating an new one");
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
	 * Get a statictic from the property name.
	 * @param propertyName The name of the property we want to access.
	 * @param stat The statistic asked.
	 * @return The value of this statistic.
	 */
	public static String getProperty(String propertyName, Statistic.Stat stat) {
		return getConfiguration().getProperty(propertyName+"."+stat);
	}
	
	/**
	 * Change the entity's stat to the value propertyValue.
	 * @param propertyName The property to change the stat about.
	 * @param stat The stat asked
	 * @param propertyValue The new value.
	 */
	public static void setProperty(String propertyName, Statistic.Stat stat, String propertyValue) {
		getConfiguration().setProperty(propertyName+"."+stat, propertyValue);
	}
	
	/**
	 * Save the properties.
	 * @return True if the properties were saved successfully, false otherwise.
	 */
	public static boolean saveProperties() {
		try {
			getConfiguration().store(new FileOutputStream(CONFIG_FILE), "Game statistics");
			return true;
		} catch(FileNotFoundException e) {
			init();
			System.err.println("Config file ("+CONFIG_FILE+") not found.");
		} catch(IOException e) {
			System.err.println("Unable to load the config file.");
			System.err.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Update the statistics using the moves.
	 * @param moves The moves on which we will compute the statistics.
	 * @return True if stats are updated, false otherwise.
	 */
	public static boolean updateStatistics(Set<MoveSuggestion> moves) {
		boolean updated = updateEntity(STATS_BOT_DEPTH, moves);
		updated &= updateEntity(STATS_BOT_SCORE, moves);
		updated &= updateEntity(STATS_NB_PLAY, moves);
		updated &= updateEntity(STATS_PROBAW, moves);
		
		return updated;		
	}
	
	/**
	 * @param propertyName The name of the property that will be updated.
	 * @param moves The moves that will be used tu update the entity.
	 * @return True if the entity has been updated, false otherwise.
	 */
	private static boolean updateEntity(String propertyName, Set<MoveSuggestion> moves) {
		Set<Double> values = new HashSet<Double>();
		Set<Double> scoreValues = new HashSet<Double>();
		
		//We loop through the moves and add the right value in sets, depending on the property chosen
		for(MoveSuggestion move : moves) {
			if(move instanceof BotSuggestion) {
				if(STATS_BOT_DEPTH.equals(propertyName)) {
					values.add((double)((BotSuggestion)move).getDepth());
					scoreValues.add(((BotSuggestion)move).computeScoreDepth());
				} else if(STATS_BOT_SCORE.equals(propertyName)) {
					values.add(((BotSuggestion)move).getEngineScore());
					scoreValues.add(((BotSuggestion)move).computeScoreEngineScore());
				}
			} else if (move instanceof OpeningSuggestion) {
				if(STATS_NB_PLAY.equals(propertyName)) {
					values.add((double)((OpeningSuggestion)move).getNbPlay());
					scoreValues.add(((OpeningSuggestion)move).getScoreNbPlay());
				} else if(STATS_PROBAW.equals(propertyName)) {
					values.add(((OpeningSuggestion)move).getProbaWin());
					scoreValues.add(((OpeningSuggestion)move).getScoreProbaWin());
				}
			}
		}
		
		//We compute the stats (mean, variance, normalization_mean, normalization_variance) on the sets
		Statistic stats = computeStats(values, scoreValues);
		
		//We compute the new stats, using the one compute above and the one store in the stats properties file
		double newMean = computeMean(propertyName, stats.mean, values.size(), false);
		double newVariance = computeVariance(propertyName, stats.mean, stats.variance, values.size(), false);
		double newNormalizationMean = computeMean(propertyName, stats.normalizationMean, scoreValues.size(), true);
		double newNormalizationVariance = computeVariance(propertyName, stats.normalizationMean, stats.normalizationVariance, scoreValues.size(), true);
		int newWeight = computeWeight(propertyName, values.size());
		
		//We save the new properties in the stats properties file
		setProperty(propertyName, Statistic.Stat.MEAN, newMean+"");
		setProperty(propertyName, Statistic.Stat.VARIANCE, newVariance+"");
		setProperty(propertyName, Statistic.Stat.WEIGHT, newWeight+"");
		setProperty(propertyName, Statistic.Stat.NORMALIZATION_MEAN, newNormalizationMean+"");
		setProperty(propertyName, Statistic.Stat.NORMALIZATION_VARIANCE, newNormalizationVariance+"");
		
		return saveProperties();
	}
	
	/**
	 * Compute the stats (mean, variance, normalization_mena, normalization_variance) using sets.
	 * @param values The values used to compute mean and variance.
	 * @param scoreValues The values used to compute normalization_mean and normalization_variance.
	 * @return The computed statistics.
	 */
	private static Statistic computeStats(Set<Double> values, Set<Double> scoreValues) {
		double mean = 0, variance = 0, normalization_mean = 0, normalization_variance = 0;

		if(values.size()>0 && scoreValues.size() >0){
			//We compute mean and variance(V(X)=E[X²]-(E[X])²) for the values
			for(double value: values) {
				mean += value;
				variance += Math.pow(value, 2);
			}
			mean /= values.size();
			variance /= values.size();
			variance -= Math.pow(mean, 2);

			//We compute mean and variance for the values' score (used for normalization)
			for(double score: scoreValues) {
				normalization_mean += score;
				normalization_variance += Math.pow(score, 2);
			}
			normalization_mean /= scoreValues.size();
			normalization_variance /= scoreValues.size();
			normalization_variance -= Math.pow(normalization_mean, 2);
		}
		
		return new Statistic(mean, variance, normalization_mean, normalization_variance);
	}

	/**
	 * Compute the new weight for the property.
	 * Add the new one to the old one.
	 * @param propertyName The property.
	 * @param weight The weight used to compute the new one.
	 * @return The new weight.
	 */
	private static int computeWeight(String propertyName, int weight) {
		int currentWeight = Integer.parseInt(getProperty(propertyName, Statistic.Stat.WEIGHT));
		return currentWeight+weight;		
	}

	/**
	 * Compute the new variance for the property.
	 * X1 represent the n1 old values (stats on X1 stored in the stats properties file) and X2 represent the n2 new values (stats in parameters)
	 * Formula:V((X1,X2))=1/(n1+n2)²*(n1²*V(X1)+n2²*V(X2)+n1n2(V(X1)+V(X2)+E[X1]²+E[X2]²)-2n1n2*E[X1]E[X2])
	 * @param propertyName The property.
	 * @param mean The mean used to compute the new variance.
	 * @param variance The variance used to compute the new one.
	 * @param weight The weight used to compute the new variance.
	 * @return The new variance.
	 */
	private static double computeVariance(String propertyName, double mean, double variance, int weight, boolean normalization) {
		int currentWeight = Integer.parseInt(getProperty(propertyName, Statistic.Stat.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyName, Statistic.Stat.MEAN));
		double currentVariance = Double.parseDouble(getProperty(propertyName, Statistic.Stat.VARIANCE));
		
		if(normalization) {
			currentMean = Double.parseDouble(getProperty(propertyName, Statistic.Stat.NORMALIZATION_MEAN));
			currentVariance = Double.parseDouble(getProperty(propertyName, Statistic.Stat.NORMALIZATION_VARIANCE));
		}
		
		if((weight+currentWeight)>0) {
			return (Math.pow(currentWeight,2)*currentVariance
					+currentWeight*weight*(currentVariance+variance+Math.pow(currentMean, 2)+Math.pow(mean, 2))
					+Math.pow(weight, 2)*variance
					-2*currentWeight*weight*currentMean*mean)/(Math.pow(currentWeight+weight, 2));
		}
		
		return 0;
	}

	/**
	 * Compute the new mean.
	 * X1 represent the n1 old values (stats on X1 stored in the stats properties file) and X2 represent the n2 new values (stats in parameters)
	 * Formula:E[X1,X2]=(n1*E[X1]+n2*E[X2])/(n1+n2)
	 * @param propertyName The properties.
	 * @param mean The mean used to compute the new one.
	 * @param weight The weight used to compute the new mean.
	 * @return The new mean.
	 */
	private static double computeMean(String propertyName, double mean, int weight, boolean normalization) {
		int currentWeight = Integer.parseInt(getProperty(propertyName, Statistic.Stat.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyName, Statistic.Stat.MEAN));
		if(normalization) {
			currentMean = Double.parseDouble(getProperty(propertyName, Statistic.Stat.NORMALIZATION_MEAN));
		}
		if(weight+currentWeight > 0) {
			return (currentMean*currentWeight+mean*weight) / (currentWeight+weight);
		}
		return 0;
	}
	
	/**
	 * Initialize the properties with default values.
	 */
	private static void init() {
		conf = new Properties();
		setProperty(STATS_NB_PLAY, Statistic.Stat.MEAN, "0");
		setProperty(STATS_NB_PLAY, Statistic.Stat.VARIANCE, "0");
		setProperty(STATS_NB_PLAY, Statistic.Stat.WEIGHT, "0");
		setProperty(STATS_NB_PLAY, Statistic.Stat.NORMALIZATION_MEAN, "0");
		setProperty(STATS_NB_PLAY, Statistic.Stat.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_PROBAW, Statistic.Stat.MEAN, "0");
		setProperty(STATS_PROBAW, Statistic.Stat.VARIANCE, "0");
		setProperty(STATS_PROBAW, Statistic.Stat.WEIGHT, "0");
		setProperty(STATS_PROBAW, Statistic.Stat.NORMALIZATION_MEAN, "0");
		setProperty(STATS_PROBAW, Statistic.Stat.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_BOT_DEPTH, Statistic.Stat.MEAN, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.Stat.VARIANCE, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.Stat.WEIGHT, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.Stat.NORMALIZATION_MEAN, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.Stat.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_BOT_SCORE, Statistic.Stat.MEAN, "0");
		setProperty(STATS_BOT_SCORE, Statistic.Stat.VARIANCE, "0");
		setProperty(STATS_BOT_SCORE, Statistic.Stat.WEIGHT, "0");
		setProperty(STATS_BOT_SCORE, Statistic.Stat.NORMALIZATION_MEAN, "0");
		setProperty(STATS_BOT_SCORE, Statistic.Stat.NORMALIZATION_VARIANCE, "0");
		
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
