package centralserver;

/**
 * Represent the suggestion of move made by the databases.
 */
public class DatabaseSuggestion extends MoveSuggestion {
	private int number_play;
	private float proba_win;
	private float proba_draw;
	
	/**
	 * Constructor
	 * @param move The move suggested.
	 * @param number_play The number of times the move has been played.
	 * @param proba_win The probability of winning.
	 * @param proba_draw The probability of drawing.
	 */
	public DatabaseSuggestion(String move, int number_play, float proba_win, float proba_draw) {
		super(move);
		this.number_play = number_play;
		this.proba_draw = proba_draw;
		this.proba_win = proba_win;
	}

	/**
	 * @return The number of times the move has been played.
	 */
	public int getNumber_play() {
		return this.number_play;
	}

	/**
	 * @return The probability of winning the game.
	 */
	public float getProba_win() {
		return this.proba_win;
	}

	/**
	 * @return The probability of drawing.
	 */
	public float getProba_draw() {
		return this.proba_draw;
	}
}