package core;

/**
 * The three different game results possible.
 */
public enum GameResult {
	DRAW(0), WIN(1), LOOSE(-1);
	private int code;
	
	/**
	 * Constructor
	 * @param code Code of the result.
	 */
	private GameResult(int code) {
		this.code = code;
	}
	
	/**
	 * @return The code.
	 */
	public int getCode() {
		return this.code;
	}
}