package core;

/**
 * Represent the suggestion of ending move made by a databases.
 */
public class EndingSuggestion extends MoveSuggestion {
	private int nbMoves;
	private GameResult result;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param result The result of the game.
	 * @param nbMoves The number of moves to the end of the game.
	 */
	public EndingSuggestion(String move, GameResult result, int nbMoves) {
		super(move);
		this.nbMoves = nbMoves;
		this.result = result;
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
	public GameResult getResult() {
		return this.result;
	}

	@Override
	public String toString() {
		return "EndingSuggestion [nbMoves="+nbMoves+", result="+result+ "]";
	}
}