package core;

/**
 * Represent the suggestion of ending move made by a databases.
 * @author Paul Chaignon
 */
public class EndingSuggestion extends MoveSuggestion {
	protected int nbMoves;
	protected int result;
	public static final int WIN_RESULT = 1;
	public static final int LOOSE_RESULT = -1;
	public static final int DRAW_RESULT = 0;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param result The result of the game.
	 * @param nbMoves The number of moves to the end of the game.
	 * @throws IllegalArgumentException If the result is not -1, 0 or 1.
	 */
	public EndingSuggestion(String move, int result, int nbMoves) {
		super(move);
		this.nbMoves = nbMoves;
		if(result!=WIN_RESULT && result!=LOOSE_RESULT && result!=DRAW_RESULT) {
			throw new IllegalArgumentException("The result must be one of the result's constants.");
		} else {
			this.result = result;
		}
		this.computeScore();
	}
	
	/**
	 * @return The number of moves to the end of the game.
	 */
	public int getNbMoves() {
		return this.nbMoves;
	}
	
	/**
	 * @return The result of the game.
	 */
	public int getResult() {
		return this.result;
	}

	@Override
	public String toString() {
		return "EndingSuggestion [nbMoves="+nbMoves+", result="+result+ "]";
	}

	@Override
	protected void computeScore() {
		this.score = this.result*this.nbMoves;
	}
}