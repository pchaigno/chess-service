package core;

/**
 * Represent the suggestion of move made by a bot.
 * Bots send a score and a computation depth with the move.
 * @author Paul Chaignon
 */
public class BotSuggestion extends MoveSuggestion {
	protected int depth;
	protected double engineScore;
	protected static final double WEIGHT_ENGINE_SCORE = Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_ENGINESCORE));
	protected static final double WEIGHT_DEPTH = Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_DEPTH));
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param depth The depth.
	 * @param engineScore The score attributed by the engine.
	 */
	public BotSuggestion(String move, int depth, double engineScore) {
		super(move);
		this.depth = depth;
		this.engineScore = engineScore;
		this.computeScore();
	}
	
	/**
	 * @return The depth.
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * @return The engine score.
	 */
	public double getEngineScore() {
		return engineScore;
	}
	
	/**
	 * Compute the score associated to the depth.
	 * Use saved properties from the StatsManager.
	 * @see StatsManager
	 * Formula:score=depth-E[depth]
	 * @return The score.
	 */
	public double computeScoreDepth() {
		return this.depth - Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_BOT_DEPTH, Statistic.Stat.MEAN));
	}
	
	/**
	 * Compute the score associated to the engine score.
	 * Use saved properties from the StatsManager.
	 * @see StatsManager
	 * Formula:score=engineScore-E[engineScore]
	 * @return The score.
	 */
	public double computeScoreEngineScore() {
		return this.engineScore - Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_BOT_SCORE, Statistic.Stat.MEAN));
	}

	@Override
	protected void computeScore() {
		double variance = Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_BOT_DEPTH, Statistic.Stat.NORMALIZATION_VARIANCE));
		if(variance!=0) {
			this.score = WEIGHT_DEPTH * this.computeScoreDepth() / Math.sqrt(variance);
		} else {
			this.score = WEIGHT_DEPTH * this.computeScoreDepth();
		}
		variance = Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_BOT_SCORE, Statistic.Stat.NORMALIZATION_VARIANCE));
		if(variance!=0) {
			this.score += WEIGHT_ENGINE_SCORE * this.computeScoreEngineScore() / Math.sqrt(variance);
		} else {
			this.score += WEIGHT_ENGINE_SCORE * this.computeScoreEngineScore();
		}
	}
}