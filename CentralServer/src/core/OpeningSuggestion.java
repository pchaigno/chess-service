package core;

/**
 * Represent the suggestion of opening move made by a databases.
 */
public class OpeningSuggestion extends MoveSuggestion {
	private int nbPlay;
	private double probaWin;
	private double probaDraw;
	private static final double WEIGHT_NB_PLAY = 0.8;
	private static final double WEIGHT_PROBA_WIN = 0.2;
	
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
		System.out.println(computeScoreNbPlay());
		System.out.println(computeScoreProbaWin());
		this.score = WEIGHT_NB_PLAY*computeScoreNbPlay()+WEIGHT_PROBA_WIN*computeScoreProbaWin();
	}
	
	public double computeScoreNbPlay(){
		if(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.VARIANCE))!=0)
			return (nbPlay-Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.MEAN)))
				/(Math.sqrt(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.VARIANCE))));
		else
			return (nbPlay-Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_NB_PLAY, Statistic.MEAN)));
	}
	
	public double computeScoreProbaWin(){
		if(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_PROBAW, Statistic.VARIANCE))!=0)
			return (probaWin - 0.5)/(Math.sqrt(Double.parseDouble(StatsManager.getProperty(StatsManager.STATS_PROBAW, Statistic.VARIANCE))));
		else
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