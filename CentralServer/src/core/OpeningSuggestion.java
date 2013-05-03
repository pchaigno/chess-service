package core;

/**
 * Represent the suggestion of opening move made by a databases.
 */
public class OpeningSuggestion extends MoveSuggestion {
	private int nbPlay;
	private double probaWin;
	private double probaDraw;
	private static final int WEIGHT_NB_PLAY = 1;
	private static final int WEIGHT_PROBA_WIN = 1;
	
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
		this.score = WEIGHT_NB_PLAY*this.nbPlay+WEIGHT_PROBA_WIN*this.probaWin;
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