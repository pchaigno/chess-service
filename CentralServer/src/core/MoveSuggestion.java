package core;

/**
 * The model for all suggestions of move (from databases or chess engines).
 * @author Paul Chaignon
 */
public abstract class MoveSuggestion {
	protected String move;
	protected double score;
	private static final double DEFAULT_SCORE = 1;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 */
	public MoveSuggestion(String move) {
		this.move = move;
		this.score = DEFAULT_SCORE;
	}
	
	/**
	 * @return The move suggested.
	 */
	public String getMove() {
		return this.move;
	}
	
	/**
	 * @return The score compute.
	 */
	public double getScore() {
		return this.score;
	}

	@Override
	public String toString() {
		return "MoveSuggestion [move=" + move + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((move==null)? 0 : move.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			return false;
		}
		MoveSuggestion other = (MoveSuggestion)obj;
		if(move==null) {
			if(other.move!=null) {
				return false;
			}
		} else if(!move.equals(other.move)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Compute the score.
	 * Use saved properties from the StatsManager.
	 * @see StatsManager
	 */
	protected abstract void computeScore();
}