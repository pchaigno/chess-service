package parser;

import java.util.LinkedList;
import java.util.List;

import parser.BoardPiece.*;

/**
 * Regroups the chess rules for each pieces and special moves.
 * These methods are used to search for a piece on the boarde knowing some of its parameters and using 
 * the movement rules for the piece in question.
 * @author Paul Chaignon
 */
public class ChessRules {

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for pawns.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 */
	private static BoardSquare pawn(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) {
		List<BoardPiece> legalPawns = new LinkedList<BoardPiece>();
		BoardSquare result = null;
		int toXnum = ChessBoard.letter.get(toX), pawnX, pawnY;
		BoardPiece pawn;

		// White pawns move "up", black move "down":
		int mod;
		if(color==PieceColor.WHITE) {
			mod = 1;
		} else {
			mod = -1;
		}
		
		// Get possible pawns given the color and x coordinate:
		List<Integer> pawns = board.getPiece(PieceType.PAWN, color, fromX, -1);
		for(int i=0; i<pawns.size(); i++) {
			pawn = board.pieces.get(pawns.get(i));
			pawnX = ChessBoard.letter.get(pawn.square.x);
			pawnY = pawn.square.y;
			// Check if pawn could move to the the given square:
			if((!capture && (toY==pawnY+mod*2 || toY==pawnY+mod) && toXnum==pawnX)
					|| (capture && toY==pawnY+mod && (toXnum==pawnX+1 || toXnum==pawnX-1))) {
				legalPawns.add(pawn);
			}
		}

		if(legalPawns.size()>1) {
			// The only case is if there's a pawn in starting position and pawn right above it.
			// Legal move would be move for 1.
			result = new BoardSquare(toX, toY-mod);
		} else if(legalPawns.size()==1) {
			result = new BoardSquare(legalPawns.get(0).square.x, legalPawns.get(0).square.y);
		}
		
		return result;
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for knights.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static BoardSquare knight(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) throws IncorrectFENException {
		List<BoardPiece> legalKnights = new LinkedList<BoardPiece>();
		BoardPiece knight;
		int knightX, knightY, toXnum = ChessBoard.letter.get(toX);
		List<Integer> knights = board.getPiece(PieceType.KNIGHT, color, fromX, fromY);

		for(int i=0; i<knights.size(); i++) {
			knight = board.pieces.get(knights.get(i));
			knightX = ChessBoard.letter.get(knight.square.x);
			knightY = knight.square.y;
			if((Math.abs(toY-knightY)==1 && Math.abs(toXnum-knightX)==2) || (Math.abs(toY-knightY)==2 && Math.abs(toXnum-knightX)==1)) {
				legalKnights.add(knight);
			}
		}

		// Knight and all other pieces are tricker, because you have to exclude pieces from legalPieces which you can't move 
		// because that would expose your king to check.
		return executeCheck(board, legalKnights, toX, toY, capture);
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for bishops.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static BoardSquare bishop(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) throws IncorrectFENException {
		List<BoardPiece> legalBishops = new LinkedList<BoardPiece>();
		int bishopX, bishopY, xDiff, yDiff, modX, modY;
		boolean blocked;
		BoardPiece bishop;

		int toXnum = ChessBoard.letter.get(toX);
		List<Integer> bishops = board.getPiece(PieceType.BISHOP, color, fromX, fromY);
		for(int i=0; i<bishops.size(); i++) {
			bishop = board.pieces.get(bishops.get(i));
			bishopX = ChessBoard.letter.get(bishop.square.x);
			bishopY = bishop.square.y;
			xDiff = toXnum - bishopX;
			yDiff = toY - bishopY;
			// If we could make that move
			if(Math.abs(xDiff)==Math.abs(yDiff)) {
				blocked = false;
				// Now we check if there are no pieces between bishop and target
				// Which technically can only happen if we promote a pawn to the
				// bishop of a color we already have, but nevertheless
				if(xDiff>0) {
					modX = 1;
				} else {
					modX = -1;
				}
				if(xDiff==yDiff) {
					modY = 1;
				} else {
					modY = -1;
				}
				for(int j=1; j<Math.abs(xDiff); j++) {
					if(board.squares.get(ChessBoard.letters[toXnum-modX*j])[toY-modX*modY*j].piece!=null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalBishops.add(bishop);
				}
			}
		}
		
		return executeCheck(board, legalBishops, toX, toY, capture);
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for rooks.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static BoardSquare rook(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) throws IncorrectFENException {
		List<BoardPiece> legalRooks = new LinkedList<BoardPiece>();
		int rookX, rookY, diff, modA;
		boolean modY, blocked;
		BoardPiece rook;

		int toXnum = ChessBoard.letter.get(toX);
		List<Integer> rooks = board.getPiece(PieceType.ROOK, color, fromX, fromY);
		for(int i=0; i<rooks.size(); i++) {
			rook = board.pieces.get(rooks.get(i));
			rookX = ChessBoard.letter.get(rook.square.x);
			rookY = rook.square.y;
			// If we could make that move
			if(toY==rookY || toXnum==rookX) {
				blocked = false;
				// Now we check if there are no pieces between rook and target
				if(toY==rookY) {
					modY = false;
					diff = toXnum - rookX;
				} else {
					modY = true;
					diff = toY - rookY;
				}
				if(diff>0) {
					modA = 1;
				} else {
					modA = -1;
				}
				for(int j=1; j<Math.abs(diff); j++) {
					if(modY && board.squares.get(ChessBoard.letters[rookX])[toY-modA*j].piece!=null) {
						blocked = true;
					} else if(!modY && board.squares.get(ChessBoard.letters[toXnum-modA*j])[toY].piece!=null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalRooks.add(rook);
				}
			}
		}
		
		return executeCheck(board, legalRooks, toX, toY, capture);
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for queens.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static BoardSquare queen(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) throws IncorrectFENException {
		List<BoardPiece> legalQueens = new LinkedList<BoardPiece>();
		int queenX, queenY, xDiff, yDiff, modX, modY, diff, modA;
		boolean modR, blocked;
		BoardPiece queen;

		int toXnum = ChessBoard.letter.get(toX);
		List<Integer> queens = board.getPiece(PieceType.QUEEN, color, fromX, fromY);
		for(int i=0; i<queens.size(); i++) {
			queen = board.pieces.get(queens.get(i));
			queenX = ChessBoard.letter.get(queen.square.x);
			queenY = queen.square.y;
			xDiff = toXnum - queenX;
			yDiff = toY - queenY;
			// If we could make that move
			// bishop style
			if(Math.abs(xDiff)==Math.abs(yDiff)) {
				blocked = false;
				// Now we check if there are no pieces between queen and target
				if(xDiff>0) {
					modX = 1;
				} else {
					modX = -1;
				}
				if(xDiff==yDiff) {
					modY = 1;
				} else {
					modY = -1;
				}
				for(int j=1; j<Math.abs(xDiff); j++) {
					if(board.squares.get(ChessBoard.letters[toXnum-modX*j])[toY-modX*modY*j].piece!=null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalQueens.add(queen);
				}
				// rook style
			} else if(toY==queenY || toXnum==queenX) {
				blocked = false;
				// Now we check if there are no pieces between queen and target
				if(toY==queenY) {
					modR = false;
					diff = toXnum - queenX;
				} else {
					modR = true;
					diff = toY - queenY;
				}
				if(diff>0) {
					modA = 1;
				} else {
					modA = -1;
				}
				for(int j=1; j<Math.abs(diff); j++) {
					if(modR && board.squares.get(ChessBoard.letters[queenX])[toY-modA*j].piece!=null) {
						blocked = true;
					} else if(!modR && board.squares.get(ChessBoard.letters[toXnum-modA*j])[toY].piece!=null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalQueens.add(queen);
				}
			}

		}
		return executeCheck(board, legalQueens, toX, toY, capture);
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules for kings.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 */
	private static BoardSquare king(ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) {
		BoardPiece king;
		BoardSquare result = null;
		List<Integer> pieces = board.getPiece(PieceType.KING, color, (char) 0, -1);
		if(pieces.size()==1) {
			king = board.pieces.get(pieces.get(0));
			result = new BoardSquare(king.square.x, king.square.y);
			return result;
		}
		throw new IllegalArgumentException("Can't get the piece king.");
	}
	
	/**
	 * Remove from the list of legal pieces the pieces that would results in a check for the king.
	 * @param board The board.
	 * @param legalPieces The pieces that can correspond to the destination coordinates.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The position of the piece on the board.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static BoardSquare executeCheck(ChessBoard board, List<BoardPiece> legalPieces, char toX, int toY, boolean capture) throws IncorrectFENException {
		BoardSquare result = null;
		if(legalPieces.size() > 1) {
			for(int i=0; i<legalPieces.size(); i++) {
				char pieceX = legalPieces.get(i).square.x;
				int pieceY = legalPieces.get(i).square.y;
				String saveFEN = board.currentFEN(false);
				// If nothing, temporarily make that move to see if king would
				// be under check if we do;
				board.makeMove(pieceX, pieceY, toX, toY, capture);
				if(!check(board, board.currentMove)) {
					result = new BoardSquare(pieceX, pieceY);
					board.loadFEN(saveFEN);
					break;
				}
				// Restore the board
				board.loadFEN(saveFEN);
			}
		} else if(legalPieces.size() == 1) {
			result = new BoardSquare(legalPieces.get(0).square.x, legalPieces.get(0).square.y);
		}
		return result;
	}

	/**
	 * Sees if board is in check state for a player.
	 * @param board The chess board.
	 * @param kingColor The color of the player to check for check.
	 * @return True if the board is in check state.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	static boolean check(ChessBoard board, PieceColor kingColor) throws IncorrectFENException {
		BoardSquare attackArray;
		BoardPiece king;
		char kingX;
		int kingY;
		char fromX;
		int fromY;
		
		List<Integer> pieces = board.getPiece(PieceType.KING, kingColor, (char)0, -1);
		if(pieces.size() == 1) {
			king = board.pieces.get(pieces.get(0));
			kingX = king.square.x;
			kingY = king.square.y;
			for(int i=0; i<board.pieces.size(); i++) {
				BoardPiece piece = board.pieces.get(i);
				if(piece.color!=kingColor && piece.type!=PieceType.KING) {
					fromX = piece.square.x;
					fromY = piece.square.y;
					// We simply check if any of the pieces can "capture" enemy king, if so, its check
					attackArray = eval(piece.type, board, piece.color, fromX, fromY, kingX, kingY, true);
					if(attackArray != null) {
						return true;
					}
				}
			}
			return false;
		}
		throw new IncorrectFENException("The "+kingColor+" king is missing.");
	}

	/**
	 * Found the position of a piece on the board.
	 * If the origin coordinates are missing, it will find them by using the movement rules.
	 * Call the right method depending on the piece type.
	 * Note: Added to convert eval method from JavaScript.
	 * @param piece The piece type.
	 * @param board The chess board.
	 * @param color The piece color.
	 * @param fromX The original abscissa.
	 * @param fromY The original ordinate.
	 * @param toX The destination abscissa.
	 * @param toY The destination ordinate.
	 * @param capture True if there is a capture.
	 * @return The board square.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	static BoardSquare eval(PieceType piece, ChessBoard board, PieceColor color, char fromX, int fromY, char toX, int toY, boolean capture) throws IncorrectFENException {
		switch(piece) {
			case PAWN:
				return pawn(board, color, fromX, fromY, toX, toY, capture);
			case KNIGHT:
				return knight(board, color, fromX, fromY, toX, toY, capture);
			case BISHOP:
				return bishop(board, color, fromX, fromY, toX, toY, capture);
			case ROOK:
				return rook(board, color, fromX, fromY, toX, toY, capture);
			case QUEEN:
				return queen(board, color, fromX, fromY, toX, toY, capture);
			case KING:
				return king(board, color, fromX, fromY, toX, toY, capture);
		}
		throw new IllegalArgumentException("Piece type not found: "+piece);
	}
}