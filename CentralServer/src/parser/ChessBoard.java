package parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represent the chess board.
 */
public class ChessBoard {
	// Board square notation:
	final int[] numbers = {0, 8, 7, 6, 5, 4, 3, 2, 1};
	final char[] letters = {'0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	Map<Character, Integer> letter;
	
	// Variables used to load/save FEN:
	// The piece to move now.
	String currentMove;
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
	public ChessBoard() {
		this.pieces = new LinkedList<BoardPiece>();
		
		this.letter = new HashMap<Character, Integer>();
		this.letter.put('a', 1);
		this.letter.put('b', 2);
		this.letter.put('c', 3);
		this.letter.put('d', 4);
		this.letter.put('e', 5);
		this.letter.put('f', 6);
		this.letter.put('g', 7);
		this.letter.put('h', 8);
		
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
	 * @param name TODO
	 * @param color TODO
	 * @param x TODO
	 * @param y TODO
	 */
	public void addPiece(String name, String color, char x, int y) {
		BoardPiece newPiece = new BoardPiece(name, color);
		newPiece.square = this.squares.get(x)[y];
		this.pieces.add(newPiece);
		this.squares.get(x)[y].piece = newPiece;
	}

	/**
	 * Search for pieces by name, color and either (or both) of coordinates.
	 * @param name TODO
	 * @param color TODO
	 * @param x TODO
	 * @param y TODO
	 * @return An array of matches - corresponding indexes of pieces array.
	 */
	public List<Integer> getPiece(String name, String color, char x, int y) {
		List<Integer> result = new LinkedList<Integer>();
		for(int i=0 ; i<this.pieces.size() ; i++) {
			if(this.pieces.get(i).name == name && this.pieces.get(i).color == color 
					&& this.pieces.get(i).square != null 
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
	public void switchMove() {
		if(this.currentMove.equals("white")) {
			this.currentMove = "black";
		} else {
			this.currentMove = "white";
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
	public void makeMove(char fromX, int fromY, char toX, int toY, boolean capture) {
		BoardPiece previousPiece = this.squares.get(fromX)[fromY].piece;
		previousPiece.square = this.squares.get(toX)[toY];
		if(capture && this.squares.get(toX)[toY].piece != null) {
			this.squares.get(toX)[toY].piece.square = null;
		}
		this.squares.get(toX)[toY].piece = previousPiece;
		this.squares.get(fromX)[fromY].piece = null;
	}
	
	/**
	 * @param reduced TODO
	 * @return The current FEN.
	 */
	public String currentFEN(boolean reduced) {
		String FEN = "";
		for(int num=8 ; num>=1 ; num--) {
			int emptyCounter = 0;
			for(char keyVar: this.squares.keySet()) {
				if(this.squares.get(keyVar)[num].piece != null) {
					if(emptyCounter != 0) {
						FEN += emptyCounter;
						emptyCounter = 0;
					}
					String pieceName = this.squares.get(keyVar)[num].piece.name;
					String pieceColor = this.squares.get(keyVar)[num].piece.color;
					char name = 0;
					if(pieceName.equals("rook")) {
						name = 'r';
					} else if(pieceName.equals("bishop")) {
						name = 'b';
					} else if(pieceName.equals("queen")) {
						name = 'q';
					} else if(pieceName.equals("king")) {
						name = 'k';
					} else if(pieceName.equals("pawn")) {
						name = 'p';
					} else if(pieceName.equals("knight")) {
						name = 'n';
					}
					if(pieceColor.equals("white")) {
						name = Character.toUpperCase(name);
						FEN += name;
					} else if(name!=0) { 
						FEN += name;
					}
				} else {
					emptyCounter++;
				}
			}
			if(emptyCounter != 0) {
				FEN += emptyCounter;
			}
			if(num != 1) {
				FEN += "/";
			}
		}
		FEN += " "+this.currentMove.substring(0, 1);
		FEN += " "+this.castling;
		FEN += " "+this.enPassant;
		if(!reduced) {
			FEN += " "+this.halfMoves;
			FEN += " "+this.fullMoves;
		}
		return FEN;
	}

	/**
	 * Prototype function used to load FEN into board.
	 * @param fen TODO
	 */
	public void loadFEN(String fen) {
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
			for(int cols=1 ; cols<=line.length() ; cols++) {
				char letter = line.charAt(cols-1);
				String color;
				if ((""+letter).matches("[rbqkpn]")) {
					color = "black";
				} else if ((""+letter).matches("[RBQKPN]")) {
					color = "white";
				} else {
					colsY = colsY + Integer.parseInt(""+letter);
					continue;
				}
				String name;
				switch(Character.toLowerCase(letter)) {
					case 'r':
						name = "rook";
						break;
					case 'b':
						name = "bishop";
						break;
					case 'q':
						name = "queen";
						break;
					case 'k':
						name = "king";
						break;
					case 'p':
						name = "pawn";
						break;
					case 'n':
						name = "knight";
						break;
					default:
						throw new IllegalArgumentException("loadFEN: No piece corresponding.");
				}
				char x = this.letters[colsY];
				int y = this.numbers[lines];
				this.addPiece(name, color, x, y);
				colsY++;
			}
		}
		if(fenArray[1].equals("b")) {
			this.currentMove = "black";
		} else {
			this.currentMove = "white";
		}
		this.castling = fenArray[2];
		this.enPassant = fenArray[3];
		this.halfMoves = Integer.parseInt(fenArray[4]);
		this.fullMoves = Integer.parseInt(fenArray[5]);
	}
}