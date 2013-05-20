package core;

/**
 * Represent the suggestion of opening move made by a databases.
 * @author Clement Gautrais
 */
public class OpeningSuggestion extends MoveSuggestion {
	protected int nbPlay;
	protected double probaWin;
	protected double probaDraw;
	protected static final double WEIGHT_NB_PLAY = Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_NBPLAY));
	protected static final double WEIGHT_PROBA_WIN = Double.parseDouble(PropertiesManager.getProperty(PropertiesManager.PROPERTY_WEIGHT_PROBAW));
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param nbPlay The number of times the move has been played.
	 * @param probaWin The probability of winning.
	 * @param probaDraw The probability of drawing.
	 */
	public OpeningSuggestion(String move, int nbPlay, double probaWin, double probaDraw) {
		super(move);
		this.nbPlay = nbPlay;
		this.probaWin = probaWin;
		this.probaDraw = probaDraw;
		this.computeScore();
	}
	
	/**
	 * Compute the score associated to nbPlay.
	 * Formula:score=nbPlay-E[nbPlay]
	 * @return The score.
	 */
	public double getScoreNbPlay() {
		return this.nbPlay - Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.Stat.MEAN));
	}
	
	/**
	 * Compute the score associated to probaWin.
	 * Formula:score=probaWin-0.5
	 * @return The score.
	 */
	public double getScoreProbaWin() {
		return this.probaWin - 0.5;
	}

	/**
	 * @return The number of times the move has been played.
	 */
	public int getNbPlay() {
		return this.nbPlay;
	}

	/**
	 * @return The probability of winning the game.
	 */
	public double getProbaWin() {
		return this.probaWin;
	}

	/**
	 * @return The probability of drawing.
	 */
	public double getProbaDraw() {
		return this.probaDraw;
	}

	@Override
	public String toString() {
		return "DatabaseSuggestion [move="+this.move+", nbPlay="+this.nbPlay+", probaWin="+this.probaWin
				+", probaDraw="+this.probaDraw+"]";
	}

	@Override
	protected void computeScore() {
		double variance = Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.Stat.NORMALIZATION_VARIANCE));
		if(variance!=0) {
			this.score = WEIGHT_NB_PLAY * this.getScoreNbPlay() / Math.sqrt(variance);
		} else {
			this.score = WEIGHT_NB_PLAY * this.getScoreNbPlay();
		}
		variance = Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_PROBAW, Statistic.Stat.NORMALIZATION_VARIANCE));
		if(variance!=0) {
			this.score += WEIGHT_PROBA_WIN * this.getScoreProbaWin() / Math.sqrt(variance);
		} else {
			this.score += WEIGHT_PROBA_WIN * this.getScoreProbaWin();
		}
	}
}