package parser;

/**
 * Represent a piece on the board.
 */
public class BoardPiece {
	PieceType type;
	PieceColor color;
	BoardSquare square;
	
	enum PieceType {
		BISHOP, PAWN, KING, KNIGHT, QUEEN, ROOK;
		
		/**
		 * @param letter The letter.
		 * @return The piece type associated to this letter.
		 */
		public static PieceType getType(char letter) {
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
		 * @return The letter associated to this piece type.
		 */
		public static String getLetter(PieceType type) {
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
			return "";
		}
	}
	
	enum PieceColor {
		WHITE, BLACK;
		
		public static String getLetter(PieceColor color) {
			if(color==PieceColor.BLACK) {
				return "b";
			} else {
				return "w";
			}
		}
	}
	
	/**
	 * Constructor
	 * @param name The type of the piece.
	 * @param color The color of the piece.
	 */
	public BoardPiece(PieceType type, PieceColor color) {
		this.type = type;
		this.color = color;
	}

	@Override
	public String toString() {
		return this.color+" "+this.type+" "+this.square;
	}
}