package core;

/**
 * Represent the suggestion of opening move made by a databases.
 * @author Clement Gautrais
 */
public class OpeningSuggestion extends MoveSuggestion {
	protected int nbPlay;
	protected double probaWin;
	protected double probaDraw;
	protected static final double WEIGHT_NB_PLAY = 0.8;
	protected static final double WEIGHT_PROBA_WIN = 0.2;
	
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
		if(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.NORMALIZATION_VARIANCE))!=0) {
			this.score = WEIGHT_NB_PLAY*computeScoreNbPlay()/(Math.sqrt(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.NORMALIZATION_VARIANCE))));
		} else {
			this.score = WEIGHT_NB_PLAY*computeScoreNbPlay();
		}
		if(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_PROBAW, Statistic.NORMALIZATION_VARIANCE))!=0) {
			this.score += WEIGHT_PROBA_WIN*computeScoreProbaWin()/(Math.sqrt(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_PROBAW, Statistic.NORMALIZATION_VARIANCE))));
		} else {
			this.score += WEIGHT_PROBA_WIN*computeScoreProbaWin();
		}
	}
	
	/**
	 * TODO
	 */
	public double computeScoreNbPlay() {
		return (nbPlay-Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.MEAN)));
	}
	
	/**
	 * TODO
	 * @return TODO
	 */
	public double computeScoreProbaWin() {
		return (probaWin - 0.5);
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
}