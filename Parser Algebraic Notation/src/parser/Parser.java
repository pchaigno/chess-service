package parser;

import java.util.HashMap;
import java.util.Map;

public class Parser {
	private Map<Character, Integer> letter;
	private ChessRules rules;
	
	public Parser() {
		this.letter = new HashMap<Character, Integer>();
		this.letter.put('a', 1);
		this.letter.put('b', 2);
		this.letter.put('c', 3);
		this.letter.put('d', 4);
		this.letter.put('e', 5);
		this.letter.put('f', 6);
		this.letter.put('g', 7);
		this.letter.put('h', 8);

		this.rules = new ChessRules();
	}

	// Convert UCI style move into PGN style move
	public String UCItoPGN(Map<String, String> moveArray, Board board) {
		// GE is "empty" move, indicates the line end (as game populating this line ended there)
		if (moveArray.get("Move").equals("GE")) {
			return "GE";
		}

		String ucimove = moveArray.get("Move");
		char fromX = ucimove.charAt(0);
		int fromY = ucimove.charAt(1);
		char toX = ucimove.charAt(2);
		int toY = ucimove.charAt(3);
		String piece = board.squares.get(fromX)[fromY].piece.name;
		boolean capture = false;
		boolean promotion = false;

		if (board.squares.get(toX)[toY].piece != null) {
			capture = true;
		}
		

		// Castling
		if (piece.equals("king") && Math.abs(letter.get(fromX) - letter.get(toX)) == 2) {
			if (toX == 'g') {
				return "O-O";
			} else {
				return "O-O-O";
			}
		}

		String pgnfromX = "";
		String pgnfromY = "";

		// Determine if we need fromX/fromY coordinates in PGN move
		if (eval("this.rules." + piece + "(board, '', '', toX, toY, capture);")[0]) {
			pgnfromX = "";
			pgnfromY = "";
		} else if (eval("this.rules." + piece + "(board, fromX, '', toX, toY, capture);")[0]) {
			pgnfromX = ""+fromX;
			pgnfromY = "";
		} else if (eval("this.rules." + piece + "(board, '', fromY, toX, toY, capture);")[0]) {
			pgnfromX = "";
			pgnfromY = ""+fromY;
		} else if (eval("this.rules." + piece + "(board, fromX, fromY, toX, toY, capture);")[0]) {
			pgnfromX = ""+fromX;
			pgnfromY = ""+fromY;
		}

		char pgnpiece = 0;
		if (piece == "knight") {
			pgnpiece = 'N';
		} else if (piece == "pawn") {
			pgnpiece = 0;
		} else {
			pgnpiece = Character.toUpperCase(piece.charAt(0));
		}

		// En passant capture
		if ((""+toX+toY).equals(board.enPassant) && piece.equals("pawn")) {
			capture = true;
		}

		char pgncapture = 0;
		if (capture) {
			pgncapture = 'x';
		}
		if (capture && piece.equals("pawn")) {
			pgnfromX = ""+fromX;
		}

		return pgnpiece + pgnfromX + pgnfromY + pgncapture + toX + toY;
	}


	// Parses the PGN file, splits it for every game and creates game objects
	public void parsePGN(String PGN) {
		// IE uses \r\n, Gecko browsers use \n
		PGN = PGN.replaceAll("\\r\\n", "\n");
		// Replace bracket {} commentaries because the can mess up how we determine the game edges, store them in array
		PGN = PGN.replaceAll("\\{([\\W\\w]*?)\\}", "");

		// Replace strings (because they can contain the semicolon comments,
		// which could once again cripple our ability to parse the games correctly) and store them in array
		Object[] tagStrings = new Object[0]; // TODO Useless?
		PGN = PGN.replaceAll("\"([^ \" \\ \\r \\n]*(?:\\.[^\" \\ \\r \\n]*)*)\"", "");

		// Replace semicolon this.commentaries and store them in array
		PGN = PGN.replace("(;[\\W\\w]*?)\n", "");
		
		// Split the Games
		var game = tree.game;

		var notation = PGN.replaceAll("(\\[[\\W\\w]*?\\])", "");
		
		// Strip numbers notation 
		notation = notation.replaceAll("\\b[\\d]+[\\s]*[\\.]+", "");

		// Strip variations
		notation = notation.replaceAll("\\([^\\(\\)]*?\\)", "");

		// Strip the result (ending)
		notation = notation.replace("[\\s]+(?:0-1|1-0|1\\/2-1\\/2|\\*)[\\s]+", "");

		// Assign notation to game object for later parsing
		game.notation = notation;

		game.displayNotation = [];
	}

	// Parses the notation of given game. Uses board object
	public void parseNotation(Board board, ChessGame game) {
		// Determine starting position
		game.FENs = [];
		game.FENs.push("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		
		var dispMove = [];
		dispMove["type"] = "start";
		dispMove["fenlink"] = 0;
		game.displayNotation["start"] = dispMove;

		//game.currPosition = game.FENs[0];

		// Load starting position into board
		board.loadFEN(game.FENs[0]);

		// Parse the notation tokens
		this.parseNotationTokens(board, game, game.notation);
	}

	// Recursive move parsing
	// Also prepare notation for display
	public void parseNotationTokens(Board board, ChessGame game, String notation) {
		String[] notationTokens = notation.split("[\\s]+");
		String token;
		// Loop through notation tokens
		for(int i=0 ; i<notationTokens.length ; i++) {
			token = notationTokens[i];
			// Regular move
			if(token.matches("[RBQKPN]?[a-h]?[1-8]?[x]?[a-h][1-8][=]?[QNRB]?[+#]?")) {
				this.parseMove(board, game, token);
			// Castling
			} else if(token.matches("(O-O-O|O-O)\\+?")) {
				this.castle(board, game, token);
			}
		}
	}
	
	public void parseMove(Board board, ChessGame game, String token) {
		char[] moveArray = token.match(/([RBQKPN])?([a-h])?([1-8])?([x])?([a-h])([1-8])([=]?)([QNRB]?)([+#]?)/);
		String piece;
		if(moveArray[1]) {
			switch (Character.toLowerCase(moveArray[1])) {
				case 'r':
					piece = "rook";
					break;
				case 'b':
					piece = "bishop";
					break;
				case 'q':
					piece = "queen";
					break;
				case 'n':
					piece = "knight";
					break;
				case 'k':
					piece = "king";
					break;
				default:
					break;
			}
		} else {
			piece = "pawn";
		}

		var fromX = moveArray[2];
		var fromY = moveArray[3];

		var capture;
		if(moveArray[4]) {
			capture = true;
		} else {
			capture = false;
		}

		var toX = moveArray[5];
		var toY = moveArray[6];

		var promotion;
		var promoteTo;
		if(moveArray[8]) {
			promotion = true;
			switch (moveArray[8].toLowerCase()) {
			case "r":
				promoteTo = "rook";
				break;
			case "b":
				promoteTo = "bishop";
				break;
			case "q":
				promoteTo = "queen";
				break;
			case "n":
				promoteTo = "knight";
				break;
			default:
				break;
			}
		} else {
			promotion = false;
			promoteTo = "";
		}

		// Determine the location of the piece to move using chess rules and incomplete information about it
		var pieceXY = eval("this.rules." + piece + "(board, fromX, fromY, toX, toY, capture);");

		Map<Character, Character> dispMove = new HashMap<Character, Character>();
		dispMove.put("type", "regular");
		dispMove.put("token", token);
		dispMove.put("color", board.currentMove);
		dispMove.put("fromto", {fromX: pieceXY[0], fromY: pieceXY[1], toX: toX, toY: toY});

		// Make piece move
		board.moveHandler(piece, pieceXY[0], pieceXY[1], toX, toY, capture, promotion, promoteTo);

		// Add FEN to game.FENs
		game.FENs.push(board.currentFEN());

		dispMove["num"] = board.fullMoves;
		dispMove["fenlink"] = game.FENs.length - 1;
		game.displayNotation.push(dispMove);
	}
	
	public void castle(Board board, ChessGame game, String token) {
		int line;
		if(board.currentMove == "white") {
			line = 1;
		} else {
			line = 8;
		}

		Map<Character, Character> dispMove = new HashMap<Character, Character>();
		dispMove.get("type") = "regular";
		dispMove.get("token") = token;
		dispMove.get("color") = board.currentMove;

		// Add move to game.moves[]
		if(token.matches("^O-O\\+?$")) {
			dispMove["fromto"] = {fromX: "e", fromY: line, toX: "g", toY: line};
		} else {
			dispMove["fromto"] = {fromX: "e", fromY: line, toX: "c", toY: line};
		}

		// Castle on board
		board.castle(token);

		// Add FEN to game.FENs
		game.FENs.push(board.currentFEN());

		dispMove["num"] = board.fullMoves;
		dispMove["fenlink"] = game.FENs.length-1;
		game.displayNotation.push(dispMove);
	}
}