package parser;

/**
 * Represent a board square.
 * @author Paul Chaignon
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
	BoardSquare(char x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}
}