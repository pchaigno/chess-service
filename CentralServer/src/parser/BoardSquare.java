package parser;

/**
 * Represent a board square.
 */
public class BoardSquare {
	char x;
	int y;
	BoardPiece piece;
	
	/**
	 * Constructor
	 * @param x The abscissa.
	 * @param y The ordinate.
	 */
	public BoardSquare(char x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}
}