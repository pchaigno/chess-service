package parser;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public class Parser {
	private static Map<Character, Integer> letter;
	private ChessRules rules;
	private ChessBoard board;
	private String fen;

	/**
	 * Constructor
	 * @param fen The FEN.
	 */
	public Parser(String fen) {
		this.fen = fen;
		
		letter = new HashMap<Character, Integer>();
		letter.put('a', 1);
		letter.put('b', 2);
		letter.put('c', 3);
		letter.put('d', 4);
		letter.put('e', 5);
		letter.put('f', 6);
		letter.put('g', 7);
		letter.put('h', 8);
	
		this.rules = new ChessRules();
		
		this.board = new ChessBoard();
		this.board.loadFEN(this.fen);
	}

	/**
	 * Main method.
	 * Convert a LAN to a SAN.
	 * @param lan The Long Algebraic Notation.
	 * @return The Short Algebraic Notation.
	 */
	public String convertLANToSAN(String lan) {
		return this.UCItoPGN(lan, this.board);
	}
	
	/**
	 * Convert UCI style move into PGN style move
	 * @param ucimove TODO
	 * @param board TODO
	 * @return TODO
	 */
	private String UCItoPGN(String ucimove, ChessBoard board) {
		char fromX = ucimove.charAt(0);
		int fromY = Integer.parseInt(""+ucimove.charAt(1));
		char toX = ucimove.charAt(2);
		int toY = Integer.parseInt(""+ucimove.charAt(3));
		String piece = board.squares.get(fromX)[fromY].piece.name;
		boolean capture = false;

		if(board.squares.get(toX)[toY].piece != null) {
			capture = true;
		}
		
		// Castling
		if(piece.equals("king") && Math.abs(letter.get(fromX)-letter.get(toX))==2) {
			if(toX == 'g') {
				return "O-O";
			} else {
				return "O-O-O";
			}
		}

		String pgnfromX = "";
		String pgnfromY = "";

		// Determine if we need fromX/fromY coordinates in PGN move
		if(this.evalRules(piece, board, (char)0, -1, toX, toY, capture).x!=0) {
			pgnfromX = "";
			pgnfromY = "";
		} else if(this.evalRules(piece, board, fromX, -1, toX, toY, capture).x!=0) {
			pgnfromX = ""+fromX;
			pgnfromY = "";
		} else if(this.evalRules(piece, board, (char)0, fromY, toX, toY, capture).x!=0) {
			pgnfromX = "";
			pgnfromY = ""+fromY;
		} else if(this.evalRules(piece, board, fromX, fromY, toX, toY, capture).x!=0) {
			pgnfromX = ""+fromX;
			pgnfromY = ""+fromY;
		}

		String pgnpiece = "";
		if(piece.equals("knight")) {
			pgnpiece = "N";
		} else if(piece.equals("pawn")) {
			pgnpiece = "";
		} else {
			pgnpiece = ""+Character.toUpperCase(piece.charAt(0));
		}

		// En passant capture
		if((""+toX+toY).equals(board.enPassant) && piece.equals("pawn")) {
			capture = true;
		}

		String pgncapture = "";
		if (capture) {
			pgncapture = "x";
		}
		if (capture && piece.equals("pawn")) {
			pgnfromX = ""+fromX;
		}

		System.out.println(pgnpiece);
		System.out.println(pgnfromX);
		System.out.println(pgnfromY);
		System.out.println(pgncapture);
		System.out.println(toX);
		System.out.println(toY);
		return pgnpiece + pgnfromX + pgnfromY + pgncapture + toX + toY;
	}
	
	/**
	 * TODO
	 * Note: Added to convert eval method from JavaScript.
	 * @param piece TODO
	 * @param board TODO
	 * @param fromX TODO
	 * @param fromY TODO
	 * @param toX TODO
	 * @param toY TODO
	 * @param capture TODO
	 * @return TODO
	 */
	private BoardSquare evalRules(String piece, ChessBoard board, char fromX, int fromY, char toX, int toY, boolean capture) {
		if(piece.equals("pawn")) {
			return this.rules.pawn(board, fromX, fromY, toX, toY, capture);
		}
		if(piece.equals("knight")) {
			return this.rules.knight(board, fromX, fromY, toX, toY, capture);
		}
		if(piece.equals("bishop")) {
			return this.rules.bishop(board, fromX, fromY, toX, toY, capture);
		}
		if(piece.equals("rook")) {
			return this.rules.rook(board, fromX, fromY, toX, toY, capture);
		}
		if(piece.equals("queen")) {
			return this.rules.queen(board, fromX, fromY, toX, toY, capture);
		}
		if(piece.equals("king")) {
			return this.rules.king(board, fromX, fromY, toX, toY, capture);
		}
		throw new IllegalArgumentException("evalRules: method not found!");
	}
	
	/**
	 * Main method. Just for tests.
	 */
	public static void main(String[] args) {
		String fen = "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
		// LAN from 127.0.0.1/rest/openings/rnbqkbnr$pppp1ppp$4p3$8$4P3$8$PPPP1PPP$RNBQKBNR w KQkq - 0 2
		String lan = "b1c3";
		String san;
		Parser parser = new Parser(fen);
		san = parser.convertLANToSAN(lan);
		System.out.println(san);
	}
}