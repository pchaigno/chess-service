package parser;

/**
 * Represent a piece on the board.
 * It contains a reference to the board square where the piece is.
 * @author Paul Chaignon
 */
public class BoardPiece {
	PieceType type;
	PieceColor color;
	BoardSquare square;
	
	/**
	 * Enumeration of piece types.
	 */
	enum PieceType {
		BISHOP, PAWN, KING, KNIGHT, QUEEN, ROOK;
		
		/**
		 * @param letter The letter.
		 * @return The piece type associated to this letter.
		 */
		static PieceType getType(char letter) {
			PieceType type = PieceType.PAWN;
			switch(Character.toLowerCase(letter)) {
				case 'r':
					type = PieceType.ROOK;
					break;
				case 'b':
					type = PieceType.BISHOP;
					break;
				case 'q':
					type = PieceType.QUEEN;
					break;
				case 'k':
					type = PieceType.KING;
					break;
				case 'n':
					type = PieceType.KNIGHT;
					break;
			}
			return type;
		}
		
		/**
		 * @param type The piece type.
		 * @param pawn If set to true, the pawns will return an empty string.
		 * @return The letter associated to this piece type.
		 */
		static String getLetter(PieceType type, boolean pawn) {
			if(type==PieceType.KNIGHT) {
				return "N";
			}
			if(type==PieceType.KING) {
				return "K";
			}
			if(type==PieceType.QUEEN) {
				return "Q";
			}
			if(type==PieceType.ROOK) {
				return "R";
			}
			if(type==PieceType.BISHOP) {
				return "B";
			}
			if(type==PieceType.PAWN) {
				if(pawn) {
					return "";
				}
				return "P";
			}
			return null;
		}
	}
	
	/**
	 * Enumeration of piece colors: black or white.
	 */
	enum PieceColor {
		WHITE, BLACK;
		
		/**
		 * @param color The color.
		 * @return The character corresponding to the color.
		 */
		static String getLetter(PieceColor color) {
			if(color==PieceColor.BLACK) {
				return "b";
			}
			return "w";
		}
		
		/**
		 * Invert the color.
		 * @param color The color.
		 * @return The other color.
		 */
		static PieceColor invert(PieceColor color) {
			if(color==BLACK) {
				return WHITE;
			}
			return BLACK;
		}
	}
	
	/**
	 * Constructor
	 * @param name The type of the piece.
	 * @param color The color of the piece.
	 */
	BoardPiece(PieceType type, PieceColor color) {
		this.type = type;
		this.color = color;
	}

	@Override
	public String toString() {
		return this.color+" "+this.type+" "+this.square;
	}
}