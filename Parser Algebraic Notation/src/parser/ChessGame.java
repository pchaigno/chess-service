package parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChessGame {
	// Game is stored in FENs for every consecutive move.
	List<String> FENs;
	// Raw notation with a bunch of tokens (comments, NAGs, recursive variations).
	String notation;
	// The position we are at (FEN).
	String currPosition;
	// At default.
	String notationMove;
	
	Map<String, String> dispMove;
	// Used for display
	Map<String, Map<String, String>> displayNotation;
	
	public ChessGame() {
		this.FENs = new LinkedList<String>();
		this.notationMove = "start";
		this.currPosition = "";
		this.dispMove = new HashMap<String, String>();
		this.dispMove.put("type", "start");
		this.dispMove.put("fenlink", 0);
		this.displayNotation = new HashMap<String, Map<String, String>>();
		this.displayNotation.put("start", dispMove);
	}
}