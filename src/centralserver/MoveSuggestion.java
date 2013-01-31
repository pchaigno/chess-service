package centralserver;

/**
 * The model for all suggestion of move (from databases or bots).
 */
public abstract class MoveSuggestion {
	private String move;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 */
	public MoveSuggestion(String move) {
		this.move = move;
	}
	
	/**
	 * @return The move suggested.
	 */
	public String getMove() {
		return this.move;
	}
}