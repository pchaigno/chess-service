package parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import parser.BoardPiece.*;

/**
 * Represent the chess board.
 * @author Paul Chaignon
 */
public class ChessBoard {
	// Board square notation:
	final int[] numbers = {0, 8, 7, 6, 5, 4, 3, 2, 1};
	final char[] letters = {'0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	@SuppressWarnings("serial")
	Map<Character, Integer> letter = new HashMap<Character, Integer>() {{
		this.put('a', 1);
		this.put('b', 2);
		this.put('c', 3);
		this.put('d', 4);
		this.put('e', 5);
		this.put('f', 6);
		this.put('g', 7);
		this.put('h', 8);
	}};
	
	// Variables used to load/save FEN:
	// The piece to move now.
	PieceColor currentMove;
	// State of castling
	String castling;
	// If there's enpassant pawn
	String enPassant;
	// Number of halfmoves
	int halfMoves;
	// Full number of moves
	int fullMoves;

	// Holds references to pieces 
	// Piece object contains name, color and reference to board square its in
	List<BoardPiece> pieces;

	// Board squares
	// These that hold a piece contain reference to piece object (so board squares and piece are circle referenced)
	Map<Character, BoardSquare[]> squares;
	
	/**
	 * Constructor
	 */
	ChessBoard() {
		this.pieces = new LinkedList<BoardPiece>();
		
		this.squares = new HashMap<Character, BoardSquare[]>();
		this.squares.put('a', new BoardSquare[9]);
		this.squares.put('b', new BoardSquare[9]);
		this.squares.put('c', new BoardSquare[9]);
		this.squares.put('d', new BoardSquare[9]);
		this.squares.put('e', new BoardSquare[9]);
		this.squares.put('f', new BoardSquare[9]);
		this.squares.put('g', new BoardSquare[9]);
		this.squares.put('h', new BoardSquare[9]);
		for(char keyVar: this.squares.keySet()) {
			for(int j=1 ; j<=8 ; j++) {
				this.squares.get(keyVar)[j] = new BoardSquare(keyVar, j);
			}
		}
	}
	
	/**
	 * Create piece objects and place a reference to them for square they're in.
	 * @param name The type of the piece.
	 * @param color The color of the piece.
	 * @param x The abscissa of the piece.
	 * @param y The ordinate of the piece.
	 */
	private void addPiece(PieceType name, PieceColor color, char x, int y) {
		BoardPiece newPiece = new BoardPiece(name, color);
		newPiece.square = this.squares.get(x)[y];
		this.pieces.add(newPiece);
		this.squares.get(x)[y].piece = newPiece;
	}

	/**
	 * Search for pieces by name, color and either (or both) of coordinates.
	 * @param type The type of the piece.
	 * @param color The color of the piece.
	 * @param x The abscissa of the piece.
	 * @param y The ordinate of the piece.
	 * @return An array of matches - corresponding indexes of pieces array.
	 */
	List<Integer> getPiece(PieceType type, PieceColor color, char x, int y) {
		List<Integer> result = new LinkedList<Integer>();
		for(int i=0 ; i<this.pieces.size() ; i++) {
			if(this.pieces.get(i).type==type && this.pieces.get(i).color==color 
					&& this.pieces.get(i).square!=null 
					&& ((x!=0 && this.pieces.get(i).square.x==x) || x==0) 
					&& ((y!=-1 && this.pieces.get(i).square.y==y) || y==-1)) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * Switches the current move
	 */
	void switchMove() {
		if(this.currentMove==PieceColor.WHITE) {
			this.currentMove = PieceColor.BLACK;
		} else {
			this.currentMove = PieceColor.WHITE;
		}
	}
	
	/**
	 * Simple move function with from & to variables
	 * @param fromX 
	 * @param fromY 
	 * @param toX 
	 * @param toY 
	 * @param capture 
	 */
	void makeMove(char fromX, int fromY, char toX, int toY, boolean capture) {
		BoardPiece previousPiece = this.squares.get(fromX)[fromY].piece;
		previousPiece.square = this.squares.get(toX)[toY];
		if(capture && this.squares.get(toX)[toY].piece != null) {
			this.squares.get(toX)[toY].piece.square = null;
		}
		this.squares.get(toX)[toY].piece = previousPiece;
		this.squares.get(fromX)[fromY].piece = null;
	}
	
	/**
	 * @param reduced True if the FEN need to be reduced.
	 * @return The current FEN.
	 */
	String currentFEN(boolean reduced) {
		String fen = "";
		for(int num=8 ; num>=1 ; num--) {
			int emptyCounter = 0;
			char[] keys = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
			for(char keyVar: keys) {
				if(this.squares.get(keyVar)[num].piece != null) {
					if(emptyCounter != 0) {
						fen += emptyCounter;
						emptyCounter = 0;
					}
					PieceType pieceType = this.squares.get(keyVar)[num].piece.type;
					PieceColor pieceColor = this.squares.get(keyVar)[num].piece.color;
					String name = PieceType.getLetter(pieceType, false);
					if(pieceColor==PieceColor.WHITE) {
						fen += name;
					} else if(name!=null) {
						fen += name.toLowerCase();
					}
				} else {
					emptyCounter++;
				}
			}
			if(emptyCounter != 0) {
				fen += emptyCounter;
			}
			if(num != 1) {
				fen += "/";
			}
		}
		fen += " "+PieceColor.getLetter(this.currentMove);
		fen += " "+this.castling;
		fen += " "+this.enPassant;
		if(!reduced) {
			if(this.halfMoves!=-1) {
				fen += " "+this.halfMoves;
			}
			if(this.fullMoves!=-1) {
				fen += " "+this.fullMoves;
			}
		}
		return fen;
	}

	/**
	 * Prototype function used to load FEN into board.
	 * @param fen The FEN.
	 */
	void loadFEN(String fen) {
		for(char keyVar: this.squares.keySet()) {
			for(int j=1 ; j<=8 ; j++) {
				this.squares.get(keyVar)[j].piece = null;
			}
		}
		this.pieces = new LinkedList<BoardPiece>();
	
		String[] fenArray = fen.split(" ");
		String[] boardArray = fenArray[0].split("/");
		for(int lines=1 ; lines<=8 ; lines++) {
			String line = boardArray[lines-1];
			int colsY = 1;
			for(int cols=0 ; cols<line.length() ; cols++) {
				char letter = line.charAt(cols);
				PieceColor color;
				if(String.valueOf(letter).matches("[rbqkpn]")) {
					color = PieceColor.BLACK;
				} else if(String.valueOf(letter).matches("[RBQKPN]")) {
					color = PieceColor.WHITE;
				} else {
					colsY = colsY + Integer.parseInt(String.valueOf(letter));
					continue;
				}
				PieceType name = PieceType.getType(letter);
				char x = this.letters[colsY];
				int y = this.numbers[lines];
				this.addPiece(name, color, x, y);
				colsY++;
			}
		}
		
		if(fenArray[1].equals("b")) {
			this.currentMove = PieceColor.BLACK;
		} else {
			this.currentMove = PieceColor.WHITE;
		}
		
		this.castling = fenArray[2];
		this.enPassant = fenArray[3];
		if(fenArray.length==6) {
			this.halfMoves = Integer.parseInt(fenArray[4]);
			this.fullMoves = Integer.parseInt(fenArray[5]);
		} else {
			this.halfMoves = -1;
			this.fullMoves = -1;
		}
	}
}