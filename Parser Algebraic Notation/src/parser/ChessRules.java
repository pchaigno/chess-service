package parser;

import java.util.HashMap;
import java.util.Map;

public class ChessRules {
	Map<Character, Integer> letter;
	char[] letters = {'0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	
	public ChessRules() {
		this.letter = new HashMap<Character, Integer>();
		letter.put('a', 1);
		letter.put('b', 2);
		letter.put('c', 3);
		letter.put('d', 4);
		letter.put('e', 5);
		letter.put('f', 6);
		letter.put('g', 7);
		letter.put('h', 8);
	}
	
	public  pawn(Board board, char fromX, int fromY, char toX, int toY, capture) {
		var legalPawns = [];
		var result = [];
		var toXnum = letter.get(toX);
		var pawn;
		int pawnX;
		int pawnY;

		// White pawns move "up", black move "down"
		int mod;
		if(board.currentMove.equals("white")) { 
			mod = 1;
		} else {
			mod = -1;
		}
		// Get possible pawns given the color and x coordinate
		var pawns = board.getPiece("pawn", board.currentMove, fromX);
		var legalPawns;
		for(int i=0 ; i<pawns.length ; i++) {
			pawn = board.pieces[pawns[i]];
			pawnX = letter.get(pawn.square.x);
			pawnY = pawn.square.y;
			// Check if pawn could move to the the given square
			if((!capture && (toY == pawnY + mod*2 || toY == pawnY + mod) && toXnum == pawnX) || (capture && toY == pawnY + mod && (toXnum == pawnX + 1 || toXnum == pawnX - 1))) {
				legalPawns.push(pawn);
			}
		}

		if(legalPawns.length > 1) {
			// The only case is if there's a pawn in starting position and pawn right above it
			result[0] = toX;
			// Legal move would be move for 1
			result[1] = toY - mod;
		} else if(legalPawns.length == 1) {
			result[0] = legalPawns[0].square.x;
			result[1] = legalPawns[0].square.y;
		}
		return result;
	}

	public  knight(Board board, char fromX, int fromY, char toX, int toY, capture) {
		var legalKnights = [];
		var knight;
		int knightX;
		int knightY;
		int toXnum = letter.get(toX);
		var knights = board.getPiece("knight", board.currentMove, fromX, fromY);

		for(int i=0 ; i<knights.length ; i++) {
			knight = board.pieces[knights[i]];
			knightX = letter.get(knight.square.x);
			knightY = knight.square.y;
			if((Math.abs(toY - knightY) == 1 && Math.abs(toXnum - knightX) == 2) || (Math.abs(toY - knightY) == 2 && Math.abs(toXnum - knightX) == 1)) {
				legalKnights.push(knight);
			}
		}
		// Knight and all other pieces are tricker, because you have to exclude pieces from legalPieces which you can't move because that would impsoe your king to check
		return this.executeCheck(board, legalKnights, toX, toY, capture);
	}

	public  bishop(Board board, char fromX, int fromY, char toX, int toY, capture) {
		var legalBishops = [];
		var bishopX;
		var bishopY;
		var xDiff;
		var yDiff;
		var modX;
		var modY;
		var blocked;
		var bishop;

		var toXnum = letter[toX];
		var bishops = board.getPiece("bishop", board.currentMove, fromX, fromY);
		for(int i=0 ; i<bishops.length ; i++) {
			bishop = board.pieces[bishops[i]];
			bishopX = letter.get(bishop.square.x);
			bishopY = bishop.square.y;
			xDiff = toXnum - bishopX;
			yDiff = toY - bishopY;
			// If we could make that move
			if(Math.abs(xDiff) == Math.abs(yDiff)) {
				blocked = false;
				// Now we check if there are no pieces between bishop and target
				// Which technically can only happen if we promote a pawn to the bishop of a color we already have, but nevertheless
				if (xDiff > 0)
					modX = 1;
				else
					modX = -1;
				if (xDiff == yDiff)
					modY = 1;
				else
					modY = -1;
				for(var j = 1; j < Math.abs(xDiff); j++) {
					if(board.squares[letters[toXnum - modX*j]][toY - modX*modY*j].piece != null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalBishops.push(bishop);
				}
			}
		}
		return this.executeCheck(board, legalBishops, toX, toY, capture);
	}

	public  rook(Board board, char fromX, int fromY, char toX, int toY, capture) {
		var legalRooks = [];
		var result = [];
		int rookX;
		int rookY;
		int diff;
		boolean modY;
		int modA;
		boolean blocked;
		var rook;

		var toXnum = letter[toX];
		var rooks = board.getPiece("rook", board.currentMove, fromX, fromY);
		for(int i=0 ; i<rooks.length ; i++) {
			rook = board.pieces[rooks[i]];
			rookX = letter[rook.square.x];
			rookY = rook.square.y;
			// If we could make that move
			if(toY == rookY || toXnum == rookX) {
				blocked = false;
				// Now we check if there are no pieces between rook and target
				if(toY == rookY) {
					modY = false;
					diff = toXnum - rookX;
				} else {
					modY = true;
					diff = toY - rookY;
				}
				if(diff > 0) {
					modA = 1;
				} else {
					modA = -1;
				}
				for(int j=1 ; j<Math.abs(diff) ; j++) {
					if (modY && board.squares[letters[rookX]][toY - modA*j].piece != null) {
						blocked = true;
					} else if (!modY && board.squares[letters[toXnum - modA*j]][toY].piece != null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalRooks.push(rook);
				}
			}
		}
		return this.executeCheck(board, legalRooks, toX, toY, capture);
	}

	public  queen(Board board, char fromX, int fromY, char toX, int toY, capture) {
		var legalQueens = [];
		int queenX;
		var queenY;
		int xDiff;
		int yDiff;
		int modX;
		int modY;
		int diff;
		boolean modR;
		int modA;
		boolean blocked;
		var queen;

		int toXnum = letter.get(toX);
		var queens = board.getPiece("queen", board.currentMove, fromX, fromY);
		for(int i=0 ; i<queens.length ; i++) {
			queen = board.pieces[queens[i]];
			queenX = letter.get(queen.square.x);
			queenY = queen.square.y;
			xDiff = toXnum - queenX;
			yDiff = toY - queenY;
			// If we could make that move
			// bishop style
			if(Math.abs(xDiff) == Math.abs(yDiff)) {
				blocked = false;
				// Now we check if there are no pieces between queen and target
				if (xDiff > 0) {
					modX = 1;
				} else {
					modX = -1;
				if (xDiff == yDiff) {
					modY = 1;
				} else {
					modY = -1;
				}
				for(int j=1 ; j<Math.abs(xDiff) ; j++) {
					if(board.squares[letters[toXnum - modX*j]][toY - modX*modY*j].piece != null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalQueens.push(queen);
				}
			// rook style
			} else if (toY == queenY || toXnum == queenX) {
				blocked = false;
				// Now we check if there are no pieces between queen and target
				if(toY == queenY) {
					modR = false;
					diff = toXnum - queenX;
				} else {
					modR = true;
					diff = toY - queenY;
				}
				if(diff > 0) {
					modA = 1;
				} else {
					odA = -1;
				}
				for(int j=1 ; j<Math.abs(diff) ; j++) {
					if(modR && board.squares[letters[queenX]][toY - modA*j].piece != null) {
						blocked = true;
					} else if(!modR && board.squares[letters[toXnum - modA*j]][toY].piece != null) {
						blocked = true;
					}
				}
				if(!blocked) {
					legalQueens.push(queen);
				}
			} 
			
		}
		return this.executeCheck(board, legalQueens, toX, toY, capture);
	}

	// Gets the king position
	public  king(board, fromX, fromY, toX, toY, capture) {
		var king;
		var result = [];
		king = board.pieces[board.getPiece("king", board.currentMove)];
		result[0] = king.square.x;
		result[1] = king.square.y;
		return result;
	}

	public  executeCheck(board, legalPieces, toX, toY, capture) {
		var result = [];
		if(legalPieces.length > 1) {
			for(int i=0 ; i<legalPieces.length ; i++) {
				var pieceX = legalPieces[i].square.x;
				var pieceY = legalPieces[i].square.y;
				var saveFEN = board.currentFEN();
				// If nothing, temporarily make that move to see if king would be under check if we do;
				board.makeMove(pieceX, pieceY, toX, toY, capture);
				board.switchMove();
				if(!this.check(board)) {
					result[0] = pieceX;
					result[1] = pieceY;
					board.loadFEN(saveFEN);
					break;
				}
				// Restore the board
				board.loadFEN(saveFEN);
			}
		} else if(legalPieces.length == 1) {
			result[0] = legalPieces[0].square.x;
			result[1] = legalPieces[0].square.y;
		} else {
			result = null;
		}
		return result;
	}

	// Sees if board is in check state for the current player
	public boolean check(Board board) {
		var attackArray = [];
		String kingColor;
		var king;
		var kingX;
		var kingY;
		char fromX;
		int fromY;
		
		if(board.currentMove.equals("white")) {
			kingColor = "black";
		} else {
			kingColor = "white";
		}
		king = board.pieces[board.getPiece("king", kingColor)];
		kingX = king.square.x;
		kingY = king.square.y;
		for(int i=0 ; i<board.pieces.length ; i++) {
			if(board.pieces[i].color == board.currentMove) {
				fromX = board.pieces[i].square.x;
				fromY = board.pieces[i].square.y;
				// We simply check if any of the pieces can "capture" enemy king, if so, its check
				attackArray = eval("this." + board.pieces[i].name + "(board, \"" + fromX + "\", \"" + fromY + "\", \"" + kingX + "\", \"" + kingY + "\", true)");
				if(attackArray != null) {
					return true;
					break;
				}
			}
		}
		return false;
	}
}