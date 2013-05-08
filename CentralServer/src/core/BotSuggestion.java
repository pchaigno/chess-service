package core;

/**
 * Represent the suggestion of move made by the bots.
 * @author Paul Chaignon
 */
public class BotSuggestion extends MoveSuggestion {
	protected int depth;
	protected double engineScore;
	
	/**
	 * Constructor
	 * @param move The move suggested
	 */
	public BotSuggestion(String move) {
		super(move);
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
		// TODO
		return -1;
	}
	
	/**
	 * TODO
	 * @return TODO
	 */
	public double computeScoreEngineScore() {
		// TODO
		return -1;
	}
}