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
	 * @param move The move suggested.
	 * @param depth The depth.
	 * @param engineScore The score attributed by the engine.
	 */
	public BotSuggestion(String move, int depth, double engineScore) {
		super(move);
		this.depth = depth;
		this.engineScore = engineScore;
		// TODO Compute score.
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