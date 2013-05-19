package core;

/**
 * Represent the suggestion of move made by the bots.
 * @author Paul Chaignon
 */
public class BotSuggestion extends MoveSuggestion {
	protected int depth;
	protected double engineScore;
	protected static final double WEIGHT_ENGINE_SCORE = 0.7;
	protected static final double WEIGHT_DEPTH = 0.3;
	
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
	 * TODO
	 * @return TODO
	 */
	public double computeScoreDepth() {
		return this.depth - Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_BOT_DEPTH, Statistic.Stat.MEAN));
	}
	
	/**
	 * TODO
	 * @return TODO
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