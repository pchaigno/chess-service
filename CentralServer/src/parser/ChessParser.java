package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.EndingSuggestion;
import parser.BoardPiece.PieceColor;
import parser.BoardPiece.PieceType;

/**
 * Regroups all the methods to parse notations.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
public class ChessParser {
	@SuppressWarnings("serial")
	private static final Map<Character, Integer> letter = new HashMap<Character, Integer>() {{
		this.put('a', 1);
		this.put('b', 2);
		this.put('c', 3);
		this.put('d', 4);
		this.put('e', 5);
		this.put('f', 6);
		this.put('g', 7);
		this.put('h', 8);
	}};
	private ChessRules rules;
	private ChessBoard board;
	private String fen;

	/**
	 * Constructor
	 * @param fen The FEN.
	 * @throws IncorrectFENException 
	 */
	public ChessParser(String fen) throws IncorrectFENException {
		this.fen = fen;
	
		this.rules = new ChessRules();
		
		this.board = new ChessBoard();
		this.board.loadFEN(this.fen);
	}

	/**
	 * Convert a LAN to a SAN.
	 * @param lan The Long Algebraic Notation.
	 * @return The Short Algebraic Notation.
	 * @throws IncorrectFENException An IncorrectFENException
	 */
	public String convertLANToSAN(String lan) throws IncorrectFENException {
		return this.UCItoPGN(lan, this.board);
	}
	
	/**
	 * Convert a SAN to a LAN.
	 * @param san The Short Algebraic Notation.
	 * @return The Long Algebraic Notation.
	 * @throws IncorrectFENException An IncorrectFENException
	 */
	public String convertSANToLAN(String san) throws IncorrectFENException {
		// Regular move
		if(san.matches("[RBQKPN]?[a-h]?[1-8]?[x]?[a-h][1-8][=]?[QNRB]?[+#]?")) {
			return this.parseMove(this.board, san);
		// Castling
		}
		if(san.matches("(O-O-O|O-O)\\+?")) {
			return san;
		}
		throw new IllegalArgumentException("Illegal SAN: "+san);
	}
	
	/**
	 * Convert UCI style move into PGN style move
	 * @param ucimove The Long Algebraic Notation.
	 * @param board The chess board.
	 * @return The Short Algebraic Notation.
	 * @throws IncorrectFENException An IncorrectFENException
	 */
	private String UCItoPGN(String ucimove, ChessBoard board) throws IncorrectFENException {
		char fromX = ucimove.charAt(0);
		int fromY = Integer.parseInt(String.valueOf(ucimove.charAt(1)));
		char toX = ucimove.charAt(2);
		int toY = Integer.parseInt(String.valueOf(ucimove.charAt(3)));
		PieceType piece = board.squares.get(fromX)[fromY].piece.type;
		boolean capture = false;

		if(board.squares.get(toX)[toY].piece != null) {
			capture = true;
		}
		
		// Castling
		if(piece==PieceType.KING && Math.abs(letter.get(fromX)-letter.get(toX))==2) {
			if(toX == 'g') {
				return "O-O";
			} else {
				return "O-O-O";
			}
		}

		String pgnfromX = "";
		String pgnfromY = "";

		// Determine if we need fromX/fromY coordinates in PGN move
		if(this.rules.eval(piece, board, (char)0, -1, toX, toY, capture).x!=0) {
			pgnfromX = "";
			pgnfromY = "";
		} else if(this.rules.eval(piece, board, fromX, -1, toX, toY, capture).x!=0) {
			pgnfromX = String.valueOf(fromX);
			pgnfromY = "";
		} else if(this.rules.eval(piece, board, (char)0, fromY, toX, toY, capture).x!=0) {
			pgnfromX = "";
			pgnfromY = String.valueOf(fromY);
		} else if(this.rules.eval(piece, board, fromX, fromY, toX, toY, capture).x!=0) {
			pgnfromX = String.valueOf(fromX);
			pgnfromY = String.valueOf(fromY);
		}

		String pgnpiece = PieceType.getLetter(piece, true);

		// En passant capture
		if(board.enPassant.equals(String.valueOf(toX)+toY) && piece==PieceType.PAWN) {
			capture = true;
		}

		String pgncapture = "";
		if(capture) {
			pgncapture = "x";
		}
		if(capture && piece==PieceType.PAWN) {
			pgnfromX = String.valueOf(fromX);
		}

		return pgnpiece + pgnfromX + pgnfromY + pgncapture + toX + toY;
	}
	
	/**
	 * Parse a short algebraic notation to a long using the chess board.
	 * @param board The chess board.
	 * @param token The Short Algebraic Notation.
	 * @return The Long Algebraic Notation.
	 * @throws IncorrectFENException An IncorrectFENException
	 */
	private String parseMove(ChessBoard board, String token) throws IncorrectFENException {
		Matcher matcher = Pattern.compile("([RBQKPN])?([a-h])?([1-8])?([x])?([a-h])([1-8])([=]?)([QNRB]?)([+#]?)").matcher(token);
		char[] moveArray = new char[6];
		if(matcher.find()) {
			for(int i=0 ; i<6 ; i++) {
				String match = matcher.group(i+1);
				if(match==null) {
					moveArray[i] = 0;
				} else if(match.length()==1) {
					moveArray[i] =  match.charAt(0);
				} else {
					moveArray[i] = 0;
				}
			}
		}
		
		PieceType piece = PieceType.getType(moveArray[0]);

		char fromX = 0;
		if(moveArray[1]!=0) {
			fromX = moveArray[1];
		}
		int fromY = -1;
		if(moveArray[2]!=0) {
			fromY = Integer.parseInt(String.valueOf(moveArray[2]));
		}

		boolean capture;
		if(moveArray[3]!=0) {
			capture = true;
		} else {
			capture = false;
		}

		char toX = moveArray[4];
		int toY = Integer.parseInt(String.valueOf(moveArray[5]));

		// Determine the location of the piece to move using chess rules and incomplete information about it
		BoardSquare pieceXY = this.rules.eval(piece, board, fromX, fromY, toX, toY, capture);
		
		return String.valueOf(pieceXY.x)+pieceXY.y+toX+toY;
	}
	
	/**
	 * Set enPassant parameter at - in fen if no pawn can play enPassant.
	 */
	public void checkEnPassant() {
		boolean needEnPassant = false;
		if(!this.board.enPassant.equals("-")) {
			int mod;
			if(board.currentMove==PieceColor.WHITE) {
				mod = 1;
			} else {
				mod = -1;
			}
			char enPassantX = this.board.enPassant.charAt(0);
			int enPassantY = Integer.parseInt(""+this.board.enPassant.charAt(1));
			char enPassantGauche = enPassantX;
			enPassantGauche -= 1;
			char enPassantDroite = enPassantX;
			enPassantDroite += 1;

			if(enPassantX=='a') {
				if(this.board.squares.get(enPassantDroite)[enPassantY-mod]!=null) {
					if(this.board.squares.get(enPassantDroite)[enPassantY-mod].piece!=null) {
						if(this.board.squares.get(enPassantDroite)[enPassantY-mod].piece.type==PieceType.PAWN && this.board.squares.get(enPassantDroite)[enPassantY-mod].piece.color==board.currentMove) {
							needEnPassant = true;
						}
					}
				}
			} else if(enPassantX=='h') {
				if(this.board.squares.get(enPassantGauche)[enPassantY-mod]!=null) {
					if(this.board.squares.get(enPassantGauche)[enPassantY-mod].piece!=null) {
						if(this.board.squares.get(enPassantGauche)[enPassantY-mod].piece.type==PieceType.PAWN && this.board.squares.get(enPassantGauche)[enPassantY-mod].piece.color==board.currentMove) {
							needEnPassant = true;
						}
					}
				}
			} else {
				if(this.board.squares.get(enPassantGauche)[enPassantY-mod]!=null) {
					if(this.board.squares.get(enPassantGauche)[enPassantY-mod].piece!=null) {
						if(this.board.squares.get(enPassantGauche)[enPassantY-mod].piece.type==PieceType.PAWN && this.board.squares.get(enPassantGauche)[enPassantY-mod].piece.color==board.currentMove) {
							needEnPassant=true;
						}
					}
				}
				if(!needEnPassant && this.board.squares.get(enPassantDroite)[enPassantY-mod]!=null) {
					if(this.board.squares.get(enPassantDroite)[enPassantY-mod].piece!=null) {
						if(this.board.squares.get(enPassantDroite)[enPassantY-mod].piece.type==PieceType.PAWN && this.board.squares.get(enPassantDroite)[enPassantY-mod].piece.color==board.currentMove) {
							needEnPassant=true;
						}
					}
				}
			}
		}
		if(!needEnPassant) {
			this.board.enPassant = "-";
		}
	}
	
	/**
	 * @param reduced True if the FEN need to be reduced.
	 * @return The FEN.
	 */
	public String getFEN(boolean reduced) {
		return board.currentFEN(reduced);
	}
	
	/**
	 * @param fen
	 * @return The color.
	 */
	public static char getColor(String fen) throws IncorrectFENException {
		String[] splited_fen = fen.split(" ");
		char color;
		if(splited_fen.length > 3) {
			color = splited_fen[1].charAt(0);
		}else{
			throw new IncorrectFENException("Number of argument incorrect.");
		}
		return color;
	}
	
	/**
	 * @param fen The FEN.
	 * @param gameColor Computer's color.
	 * @return 0 for a draw, 1 for a win or -1 for a lose.
	 * @throws NumberFormatException
	 * @throws IncorectFENException
	 */
	public static int result(String fen, char gameColor) throws NumberFormatException, IncorrectFENException {
		int reward = 0;
		String[] splited_fen = fen.split(" ");
		if(splited_fen.length == 6) {
			int nb =  Integer.parseInt(splited_fen[4]);
			if(nb<50) {
				char color = 0;
				try {
					color = ChessParser.getColor(fen);
				}catch(IncorrectFENException e) {
					System.err.println("result :"+e.getMessage());
				}
				if(color == gameColor && color != 0) {
					reward = EndingSuggestion.WIN_RESULT;
				}else{
					if(gameColor == 'b' || gameColor == 'w') {
						reward = EndingSuggestion.LOOSE_RESULT;
					}else{
						reward = EndingSuggestion.DRAW_RESULT;
					}
				}
			}else{
				reward = EndingSuggestion.DRAW_RESULT;
			}
		}else{
			throw new IncorrectFENException("Number of argument incorrect.");
		}

		return reward;
	}
	
	/**
	 * Check is the FEN is correct.
	 * @param fen
	 * @return true if the FEN is correct.
	 */
	public static boolean isCorrect(String fen){
		String[] fenArray = fen.split(" ");
		String[] boardArray = fenArray[0].split("/");
		if(fenArray.length > 3 && boardArray.length == 8) {
			return true;
		}else{
			return false;
		}
	}
}