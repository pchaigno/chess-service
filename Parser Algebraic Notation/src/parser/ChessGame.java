package parser;

import java.util.Stack;

public class ChessGame {
	// Game is stored in FENs for every consecutive move.
	Stack<String> FENs;
	// Raw notation with a bunch of tokens (comments, NAGs, recursive variations).
	String notation;
	// The position we are at (FEN).
	String currPosition;
	// At default.
	int notationMove;
	
	DisplayMove dispMove;
	// Used for display
	Stack<DisplayMove> displayNotation;
	
	public ChessGame() {
		this.FENs = new Stack<String>();
		this.notationMove = -1;
		this.currPosition = "";
		this.dispMove = new DisplayMove();
		this.dispMove.type = "start";
		this.dispMove.fenlink = 0;
		this.displayNotation = new Stack<DisplayMove>();
		this.displayNotation.push(dispMove);
	}
}