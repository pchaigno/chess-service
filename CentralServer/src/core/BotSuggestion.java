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
	
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public double getEngineScore() {
		return engineScore;
	}
	public void setEngineScore(double engineScore) {
		this.engineScore = engineScore;
	}
	
	public double computeScoreDepth(){
		//TODO
		return -1;
	}
	
	public double computeScoreEngineScore(){
		//TODO
		return -1;
	}
	
}