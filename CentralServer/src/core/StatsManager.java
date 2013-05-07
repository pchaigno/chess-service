package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
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
	
	//TODO mieux gerer les parametres avec un "structure" commune (un nom et un rang par param) pour plus de généricite
	private static final int NB_PARAMS_OPENINGS = 2;
	private static final int RANGE_NB_PLAY = 0;
	private static final int RANGE_PROBAW = 1;
	
	private static final int NB_PARAMS_BOTS = 2;
	private static final int RANGE_BOT_DEPTH = 0;
	private static final int RANGE_BOT_SCORE = 1;
	
	
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
			init();
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
	public static boolean updateStatistics(Set<? extends MoveSuggestion> moves){
		Set<OpeningSuggestion> openings = new HashSet<OpeningSuggestion>();
		Set<BotSuggestion> bots = new HashSet<BotSuggestion>();
		
		for(MoveSuggestion m : moves){
			if(m instanceof OpeningSuggestion)
				openings.add((OpeningSuggestion) m);
			
			else if(m instanceof BotSuggestion)
				bots.add((BotSuggestion) m);
		}
		
		boolean updated = updateOpeningStatistics(openings);
		updated&=updateBotStatistics(bots);
		
		return updated;		
	}
	
	private static boolean updateOpeningStatistics(Set<OpeningSuggestion> moves){
		double[] movesStats = computeOpeningStats(moves);
		
		boolean updated = updateEntity(STATS_NB_PLAY, movesStats[RANGE_NB_PLAY], movesStats[RANGE_NB_PLAY+NB_PARAMS_OPENINGS], moves.size(), movesStats[RANGE_NB_PLAY+2*NB_PARAMS_OPENINGS], movesStats[RANGE_NB_PLAY+3*NB_PARAMS_OPENINGS]);
		updated &= updateEntity(STATS_PROBAW, movesStats[RANGE_PROBAW], movesStats[RANGE_PROBAW+NB_PARAMS_OPENINGS], moves.size(), movesStats[RANGE_PROBAW+2*NB_PARAMS_OPENINGS], movesStats[RANGE_PROBAW+3*NB_PARAMS_OPENINGS]);
		
		return updated;
	}
	
	private static boolean updateBotStatistics(Set<BotSuggestion> moves){
		double[] movesStats = computeBotStats(moves);
		
		boolean updated = updateEntity(STATS_BOT_DEPTH, movesStats[RANGE_BOT_DEPTH], movesStats[RANGE_BOT_DEPTH+NB_PARAMS_BOTS], moves.size(), movesStats[RANGE_BOT_DEPTH+2*NB_PARAMS_BOTS], movesStats[RANGE_BOT_DEPTH+3*NB_PARAMS_BOTS]);
		updated &= updateEntity(STATS_BOT_SCORE, movesStats[RANGE_BOT_SCORE], movesStats[RANGE_BOT_SCORE+NB_PARAMS_BOTS], moves.size(), movesStats[RANGE_BOT_SCORE+2*NB_PARAMS_BOTS], movesStats[RANGE_BOT_SCORE+3*NB_PARAMS_BOTS]);
		
		return updated;
	}
	
	/**
	 * Return a table containing statistics about the moves.
	 * @param moves The moves to compute the stats about.
	 * @return A table of size 4*NB_PARAMS. The NB_PARAMS firsts elements contain the mean, the NB_PARAMS last the variance, same pattern for normalization (2 last)
	 * and the 2 "subtables" are ordered by RANGE_...
	 */
	private static double[] computeOpeningStats(Set<OpeningSuggestion> moves){
		double[] stats = new double[4*NB_PARAMS_OPENINGS];
		Arrays.fill(stats, 0);
		
		for(OpeningSuggestion move: moves) {
			stats[RANGE_NB_PLAY] += move.getNbPlay();
			stats[RANGE_PROBAW] += move.getProbaWin();
			stats[RANGE_NB_PLAY+NB_PARAMS_OPENINGS] += Math.pow(move.getNbPlay(), 2);
			stats[RANGE_PROBAW+NB_PARAMS_OPENINGS] += Math.pow(move.getProbaWin(), 2);
			stats[RANGE_NB_PLAY+2*NB_PARAMS_OPENINGS] += move.computeScoreNbPlay();
			stats[RANGE_PROBAW+2*NB_PARAMS_OPENINGS] += move.computeScoreProbaWin();
			stats[RANGE_NB_PLAY+3*NB_PARAMS_OPENINGS] += Math.pow(move.computeScoreNbPlay(), 2);
			stats[RANGE_PROBAW+3*NB_PARAMS_OPENINGS] += Math.pow(move.computeScoreProbaWin(), 2);
			
		}
		if(moves.size()>0) {
			for(int i=0 ; i<stats.length ; i++) {
				stats[i] /= moves.size();
			}
		}
		for(int i=0 ; i<NB_PARAMS_OPENINGS ; i++){
			stats[NB_PARAMS_OPENINGS+i] -= Math.pow(stats[i], 2);
			stats[3*NB_PARAMS_OPENINGS+i] -= Math.pow(stats[2*NB_PARAMS_OPENINGS+i], 2);
		}
		
		return stats;
	}
	
	private static double[] computeBotStats(Set<BotSuggestion> moves){
		double[] stats = new double[4*NB_PARAMS_BOTS];
		Arrays.fill(stats, 0);
		
		for(BotSuggestion move: moves) {
			stats[RANGE_BOT_DEPTH] += move.getDepth();
			stats[RANGE_BOT_SCORE] += move.getEngineScore();
			stats[RANGE_BOT_DEPTH+NB_PARAMS_BOTS] += Math.pow(move.getDepth(), 2);
			stats[RANGE_BOT_SCORE+NB_PARAMS_BOTS] += Math.pow(move.getEngineScore(), 2);
			stats[RANGE_BOT_DEPTH+2*NB_PARAMS_BOTS] += move.computeScoreDepth();
			stats[RANGE_BOT_SCORE+2*NB_PARAMS_BOTS] += move.computeScoreEngineScore();
			stats[RANGE_BOT_DEPTH+3*NB_PARAMS_BOTS] += Math.pow(move.computeScoreDepth(), 2);
			stats[RANGE_BOT_SCORE+3*NB_PARAMS_BOTS] += Math.pow(move.computeScoreEngineScore(), 2);
			
		}
		if(moves.size()>0) {
			for(int i=0 ; i<stats.length ; i++) {
				stats[i] /= moves.size();
			}
		}
		for(int i=0 ; i<NB_PARAMS_BOTS ; i++){
			stats[NB_PARAMS_BOTS+i] -= Math.pow(stats[i], 2);
			stats[3*NB_PARAMS_BOTS+i] -= Math.pow(stats[2*NB_PARAMS_BOTS+i], 2);
		}
		
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
	private static boolean updateEntity(String propertyEntity, double mean, double variance, int weight, double normalizationMean, double normalizationVariance){
		double newMean = computeMean(propertyEntity, mean, weight, false);
		double newVariance = computeVariance(propertyEntity, mean, variance, weight, false);
		double newNormalizationMean = computeMean(propertyEntity, normalizationMean, weight, true);
		double newNormalizationVariance = computeVariance(propertyEntity, normalizationMean, normalizationVariance, weight, true);
		int newWeight = computeWeight(propertyEntity, weight);
		
		setProperty(propertyEntity, Statistic.MEAN, newMean+"");
		setProperty(propertyEntity, Statistic.VARIANCE, newVariance+"");
		setProperty(propertyEntity, Statistic.WEIGHT, newWeight+"");
		setProperty(propertyEntity, Statistic.NORMALIZATION_MEAN, newNormalizationMean+"");
		setProperty(propertyEntity, Statistic.NORMALIZATION_VARIANCE, newNormalizationVariance+"");
		
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
	private static double computeVariance(String propertyEntity, double mean, double variance, int weight, boolean normalization) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		double currentVariance = Double.parseDouble(getProperty(propertyEntity, Statistic.VARIANCE));
		
		if(normalization){
			currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.NORMALIZATION_MEAN));
			currentVariance = Double.parseDouble(getProperty(propertyEntity, Statistic.NORMALIZATION_VARIANCE));
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
	 * Compute the mean.
	 * @param propertyEntity The properties.
	 * @param mean The old mean.
	 * @param weight TODO
	 * @return The new mean.
	 */
	private static double computeMean(String propertyEntity, double mean, int weight, boolean normalization) {
		int currentWeight = Integer.parseInt(getProperty(propertyEntity, Statistic.WEIGHT));
		double currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.MEAN));
		if(normalization){
			currentMean = Double.parseDouble(getProperty(propertyEntity, Statistic.NORMALIZATION_MEAN));
		}
		if(weight+currentWeight > 0) {
			return (currentMean*currentWeight+mean*weight)/(currentWeight+weight);
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
		setProperty(STATS_NB_PLAY, Statistic.NORMALIZATION_MEAN, "0");
		setProperty(STATS_NB_PLAY, Statistic.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_PROBAW, Statistic.MEAN, "0");
		setProperty(STATS_PROBAW, Statistic.VARIANCE, "0");
		setProperty(STATS_PROBAW, Statistic.WEIGHT, "0");
		setProperty(STATS_PROBAW, Statistic.NORMALIZATION_MEAN, "0");
		setProperty(STATS_PROBAW, Statistic.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_BOT_DEPTH, Statistic.MEAN, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.VARIANCE, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.WEIGHT, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.NORMALIZATION_MEAN, "0");
		setProperty(STATS_BOT_DEPTH, Statistic.NORMALIZATION_VARIANCE, "0");
		
		setProperty(STATS_BOT_SCORE, Statistic.MEAN, "0");
		setProperty(STATS_BOT_SCORE, Statistic.VARIANCE, "0");
		setProperty(STATS_BOT_SCORE, Statistic.WEIGHT, "0");
		setProperty(STATS_BOT_SCORE, Statistic.NORMALIZATION_MEAN, "0");
		setProperty(STATS_BOT_SCORE, Statistic.NORMALIZATION_VARIANCE, "0");
		
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