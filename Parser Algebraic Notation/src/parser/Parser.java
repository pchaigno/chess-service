package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	private Map<Character, Integer> letter;
	private ChessRules rules;
	
	private Tree tree; // Added
	
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
	public String UCItoPGN(Map<String, String> moveArray, ChessBoard board) {
		// GE is "empty" move, indicates the line end (as game populating this line ended there)
		if(moveArray.get("Move").equals("GE")) {
			return "GE";
		}

		String ucimove = moveArray.get("Move");
		char fromX = ucimove.charAt(0);
		int fromY = ucimove.charAt(1);
		char toX = ucimove.charAt(2);
		int toY = ucimove.charAt(3);
		String piece = board.squares.get(fromX)[fromY].piece.name;
		boolean capture = false;
		//boolean promotion = false; // TODO Useless?

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

		char pgnpiece = 0;
		if(piece == "knight") {
			pgnpiece = 'N';
		} else if(piece == "pawn") {
			pgnpiece = 0;
		} else {
			pgnpiece = Character.toUpperCase(piece.charAt(0));
		}

		// En passant capture
		if((""+toX+toY).equals(board.enPassant) && piece.equals("pawn")) {
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
		//Object[] tagStrings = new Object[0]; // TODO Useless?
		PGN = PGN.replaceAll("\"([^ \" \\ \\r \\n]*(?:\\.[^\" \\ \\r \\n]*)*)\"", "");

		// Replace semicolon this.commentaries and store them in array
		PGN = PGN.replaceFirst("(;[\\W\\w]*?)\n", "");
		
		// Split the Games
		ChessGame game = tree.game;

		String notation = PGN.replaceAll("(\\[[\\W\\w]*?\\])", "");
		
		// Strip numbers notation 
		notation = notation.replaceAll("\\b[\\d]+[\\s]*[\\.]+", "");

		// Strip variations
		notation = notation.replaceAll("\\([^\\(\\)]*?\\)", "");

		// Strip the result (ending)
		notation = notation.replaceFirst("[\\s]+(?:0-1|1-0|1\\/2-1\\/2|\\*)[\\s]+", "");

		// Assign notation to game object for later parsing
		game.notation = notation;

		game.displayNotation.clear();
	}

	// Parses the notation of given game. Uses board object
	public void parseNotation(ChessBoard board, ChessGame game) {
		// Determine starting position
		game.FENs.clear();
		// TODO Adapt the FEN.
		game.FENs.add("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		
		DisplayMove dispMove = new DisplayMove();
		dispMove.type = "start";
		dispMove.fenlink = 0;
		game.displayNotation.add(dispMove);

		// Load starting position into board
		board.loadFEN(game.FENs.get(0));

		// Parse the notation tokens
		this.parseNotationTokens(board, game, game.notation);
	}

	// Recursive move parsing
	// Also prepare notation for display
	public void parseNotationTokens(ChessBoard board, ChessGame game, String notation) {
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
	
	public void parseMove(ChessBoard board, ChessGame game, String token) {
		Matcher matcher = Pattern.compile("([RBQKPN])?([a-h])?([1-8])?([x])?([a-h])([1-8])([=]?)([QNRB]?)([+#]?)").matcher(token);
		char[] moveArray = new char[9];
		if(matcher.find()) {
			for(int i=0 ; i<9 ; i++) {
				String match = matcher.group(0);
				if(match.length()==1) {
					moveArray[i] =  match.charAt(0);
				} else if(match.length()==0) {
					moveArray[i] = 0;
				} else {
					throw new IllegalArgumentException("parseMove: Too many matches.");
				}
			}
		}
		
		String piece = "pawn";
		if(moveArray[1]!=0) {
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
		}

		char fromX = moveArray[2];
		int fromY = Integer.parseInt(""+moveArray[3]);

		boolean capture;
		if(moveArray[4]!=0) {
			capture = true;
		} else {
			capture = false;
		}

		char toX = moveArray[5];
		int toY = Integer.parseInt(""+moveArray[6]);

		boolean promotion = false;
		String promoteTo = "";
		if(moveArray[8]!=0) {
			promotion = true;
			switch(Character.toLowerCase(moveArray[8])) {
				case 'r':
					promoteTo = "rook";
					break;
				case 'b':
					promoteTo = "bishop";
					break;
				case 'q':
					promoteTo = "queen";
					break;
				case 'n':
					promoteTo = "knight";
					break;
				default:
					break;
			}
		}

		// Determine the location of the piece to move using chess rules and incomplete information about it
		BoardSquare pieceXY = evalRules(piece, board, fromX, fromY, toX, toY, capture);

		DisplayMove dispMove = new DisplayMove();
		dispMove.type = "regular";
		dispMove.token = token;
		dispMove.color = board.currentMove;
		dispMove.fromX = pieceXY.x;
		dispMove.fromY = pieceXY.y;
		dispMove.toX = toX;
		dispMove.toY = toY;

		// Make piece move
		board.moveHandler(piece, pieceXY.x, pieceXY.y, toX, toY, capture, promotion, promoteTo);

		// Add FEN to game.FENs
		game.FENs.add(board.currentFEN(false));

		dispMove.num = board.fullMoves;
		dispMove.fenlink = game.FENs.size()-1;
		game.displayNotation.add(dispMove);
	}
	
	public void castle(ChessBoard board, ChessGame game, String token) {
		int line;
		if(board.currentMove == "white") {
			line = 1;
		} else {
			line = 8;
		}

		DisplayMove dispMove = new DisplayMove();
		dispMove.type = "regular";
		dispMove.token = token;
		dispMove.color = board.currentMove;

		// Add move to game.moves[]
		if(token.matches("^O-O\\+?$")) {
			dispMove.fromX = 'e';
			dispMove.fromY = line; 
			dispMove.toX = 'g';
			dispMove.toY = line;
		} else {
			dispMove.fromX = 'e';
			dispMove.fromY = line;
			dispMove.toX = 'c';
			dispMove.toY = line;
		}

		// Castle on board
		board.castle(token);

		// Add FEN to game.FENs
		game.FENs.add(board.currentFEN(false));

		dispMove.num = board.fullMoves;
		dispMove.fenlink = game.FENs.size()-1;
		game.displayNotation.add(dispMove);
	}
	
	/**
	 * Note: Added to convert eval method from JavaScript.
	 * @param piece
	 * @param board
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @param capture
	 * @return
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
}