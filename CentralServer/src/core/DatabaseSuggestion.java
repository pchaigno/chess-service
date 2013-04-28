package core;

/**
 * Represent the suggestion of move made by the databases.
 */
public class DatabaseSuggestion extends MoveSuggestion {
	private int nb;
	private float probatowin;
	private float probatonull;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param nb The number of times the move has been played.
	 * @param probatowin The probability of winning.
	 * @param probatonull The probability of drawing.
	 */
	public DatabaseSuggestion(String move, int nb, float probatowin, float probatonull) {
		super(move);
		this.nb = nb;
		this.probatonull = probatonull;
		this.probatowin = probatowin;
	}

	/**
	 * @return The number of times the move has been played.
	 */
	public int getnb() {
		return this.nb;
	}

	/**
	 * @return The probability of winning the game.
	 */
	public float getprobatowin() {
		return this.probatowin;
	}

	/**
	 * @return The probability of drawing.
	 */
	public float getprobatonull() {
		return this.probatonull;
	}

	@Override
	public String toString() {
		return "DatabaseSuggestion [nb=" + nb + ", probatowin=" + probatowin
				+ ", probatonull=" + probatonull + ", toString()="
				+ super.toString() + "]";
	}
}