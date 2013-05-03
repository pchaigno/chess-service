package core;

/**
 * Represent the suggestion of opening move made by a databases.
 */
public class OpeningSuggestion extends MoveSuggestion {
	private int nbPlay;
	private float probaWin;
	private float probaDraw;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param nbPlay The number of times the move has been played.
	 * @param probaWin The probability of winning.
	 * @param probaDraw The probability of drawing.
	 */
	public OpeningSuggestion(String move, int nbPlay, float probaWin, float probaDraw) {
		super(move);
		this.nbPlay = nbPlay;
		this.probaWin = probaWin;
		this.probaDraw = probaDraw;
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
	public float getProbaWin() {
		return this.probaWin;
	}

	/**
	 * @return The probability of drawing.
	 */
	public float getProbaDraw() {
		return this.probaDraw;
	}

	@Override
	public String toString() {
		return "DatabaseSuggestion [nbPlay=" + nbPlay + ", probaWin=" + probaWin
				+ ", probaDraw=" + probaDraw + "]";
	}
}