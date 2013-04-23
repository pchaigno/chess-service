function chessBoard() {

	// Board square notation
	this.numbers = [0, 8, 7, 6, 5, 4, 3, 2, 1];
	this.letters = ["0", "a", "b", "c", "d", "e", "f", "g", "h"];
	this.letter = [];
	this.letter["a"] = 1;
	this.letter["b"] = 2;
	this.letter["c"] = 3;
	this.letter["d"] = 4;
	this.letter["e"] = 5;
	this.letter["f"] = 6;
	this.letter["g"] = 7;
	this.letter["h"] = 8;

	// Variables used to load/save FEN
	// The piece to move now
	this.currentMove = "";
	// State of castling
	this.castling;
	// If there's enpassant pawn
	this.enPassant;
	// Number of halfmoves
	this.halfMoves;
	// Full number of moves
	this.fullMoves;

	// Holds references to pieces 
	// piece object contains name, color and reference to board square its in
	this.pieces = [];

	// Board squares
	// These that hold a piece contain reference to piece object (so board squares and piece are circle referenced)
	this.squares = [];
	this.squares["a"] = [];
	this.squares["b"] = [];
	this.squares["c"] = [];
	this.squares["d"] = [];
	this.squares["e"] = [];
	this.squares["f"] = [];
	this.squares["g"] = [];
	this.squares["h"] = [];
	for(var keyVar in this.squares) {
		for(var j = 1; j <= 8; j++) {
			this.squares[keyVar][j] = new boardSquare(keyVar, j);
		}
	}
}

// Prototype function used to load FEN into board
chessBoard.prototype.loadFEN = 
	function(FEN) {
		for(var keyVar in this.squares) {
			for(var j = 1; j <= 8; j++) {
				this.squares[keyVar][j].piece = null;
			}
		}
		delete this.pieces;
		this.pieces = [];

		var FENArray = FEN.split(" ");
		var boardArray = FENArray[0].split("/");
		for(var lines = 1; lines <= 8; lines++) {
			var line = boardArray[lines - 1].split("");
			var colsY = 1;
			for(var cols = 1; cols <= line.length; cols++) {
				var letter = line[cols - 1];
				var color;
				if (/[rbqkpn]/.test(letter)) {
					color = "black";
				} else if (/[RBQKPN]/.test(letter)) {
					color = "white";
				} else {
					colsY = parseInt(colsY) + parseInt(letter);
					continue;
				}
				switch(letter.toLowerCase()) {
					case "r":
						name = "rook";
						break;
					case "b":
						name = "bishop";
						break;
					case "q":
						name = "queen";
						break;
					case "k":
						name = "king";
						break;
					case "p":
						name = "pawn";
						break;
					case "n":
						name = "knight";
						break;
					default:
						break;
				}
				var x = this.letters[colsY];
				var y = this.numbers[lines];
				this.addPiece(name, color, x, y);
				colsY++;
			}
		}
		if (FENArray[1] == "b")
			this.currentMove = "black";
		else
			this.currentMove = "white";
		this.castling = FENArray[2];
		this.enPassant = FENArray[3];
		this.halfMoves = FENArray[4];
		this.fullMoves = FENArray[5];
	}
// Create piece objects and place a reference to them for square they're in
chessBoard.prototype.addPiece = 
	function(name, color, x, y) {
		var newPiece = new boardPiece(name, color);
		newPiece.square = this.squares[x][y];
		this.pieces.push(newPiece);
		this.squares[x][y].piece = newPiece;
	}
// MoveHandler
chessBoard.prototype.moveHandler =
	function(piece, fromX, fromY, toX, toY, capture, promotion, promoteTo, varNum) {
		// Make piece move
		this.makeMove(fromX, fromY, toX, toY, capture);
		if (piece == "pawn") {
			// White pawns move "up", black move "down"
			var mod;
			if (this.currentMove == "white") 
				mod = 1;
			else
				mod = -1;
			// if enPassant capture, manually remove piece, as makeMove is simple and doesn't handle this
			if (capture && toX + toY == this.enPassant) {
				this.squares[toX][toY - mod].piece.square = null;
				this.squares[toX][toY - mod].piece = null;
			}
			// Set enPassant if needed
			if (Math.abs(toY - fromY) == 2 && (
													(	toX != "a" 
														&& this.squares[this.letters[this.letter[toX] - 1]][toY].piece != undefined 
														&& this.squares[this.letters[this.letter[toX] - 1]][toY].piece.color != this.currentMove 
														&& this.squares[this.letters[this.letter[toX] - 1]][toY].piece.name == "pawn" 
													) || (toX != "h"
														&& this.squares[this.letters[this.letter[toX] + 1]][toY].piece != undefined 
														&& this.squares[this.letters[this.letter[toX] + 1]][toY].piece.color != this.currentMove 
														&& this.squares[this.letters[this.letter[toX] + 1]][toY].piece.name == "pawn"
													)
												)
				) {
				this.enPassant = toX + (parseInt(toY) - mod);
			} else {
				this.enPassant = "-";
			}


			// Set the promotion piece if so
			if(promotion) {
				this.squares[toX][toY].piece.name = promoteTo;
			}
		} else {
			this.enPassant = "-";
			// Handle castling if rook moves
			if (piece == "rook" && this.castling != "-") {
				if (fromX == "a" && fromY == 8) {
					this.castling = this.castling.replace(/q/, "");
				} else if (fromX == "h" && fromY == 8) {
					this.castling = this.castling.replace(/k/, "");
				} else if (fromX == "a" && fromY == 1) {
					this.castling = this.castling.replace(/Q/, "");
				} else if (fromX == "h" && fromY == 1) {
					this.castling = this.castling.replace(/K/, "");
				}
			}
			if (piece == "king" && this.castling != "-") {
				if (this.currentMove == "white") {
					this.castling = this.castling.replace(/K/, "");
					this.castling = this.castling.replace(/Q/, "");
				} else {
					this.castling = this.castling.replace(/k/, "");
					this.castling = this.castling.replace(/q/, "");
				}
			}
			// If castling is empty after above
			if (this.castling == "") {
				this.castling = "-";
			}
		}

		if(piece == "pawn" || promotion || capture) {
			this.halfMoves = 0;
		} else {
			this.halfMoves++;
		}
		if (this.currentMove == "black")
			this.fullMoves++;
		this.switchMove();
	}
// Handles the castling
chessBoard.prototype.castle = 
	function(castling) {
		var line;
		if (this.currentMove == "white")
			line = 1;
		else
			line = 8;
			
		if (/^O-O\+?$/.test(castling)) {
			this.makeMove("e", line, "g", line);
			this.makeMove("h", line, "f", line);
		} else {
			this.makeMove("e", line, "c", line);
			this.makeMove("a", line, "d", line);
		}

		var castlestrip;

		if (this.currentMove == "white")
			castlestrip = /[KQ]/g;
		else
			castlestrip = /[kq]/g;

		this.enPassant = "-";
		this.halfMoves++;
		if (this.currentMove == "black")
			this.fullMoves++;
		this.castling = this.castling.replace(castlestrip, "");
		if (this.castling == "")
			this.castling = "-";
		this.switchMove();
	}
// Search for pieces by name, color and either (or both) of coordinates
// Returns an array of matches - corresponding indexes of pieces array
chessBoard.prototype.getPiece = 
	function(name, color, x, y) {
		var result = new Array();
		for(var i = 0; i < this.pieces.length; i++) {
			if (this.pieces[i].name == name && this.pieces[i].color == color && this.pieces[i].square != null && ((x && this.pieces[i].square.x == x) || !x) && ((y && this.pieces[i].square.y == y) || !y)) {
				result.push(i);
			}
		}
		return result;
	}
// Switches the current move
chessBoard.prototype.switchMove =
	function() {
		if (this.currentMove == "white")
			this.currentMove = "black";
		else
			this.currentMove = "white";
	}
// Simple move function with from&to variables
chessBoard.prototype.makeMove =
	function(fromX, fromY, toX, toY, capture) {
		var previousPiece = this.squares[fromX][fromY].piece;
		previousPiece.square = this.squares[toX][toY];
		if (capture && this.squares[toX][toY].piece != null) {
			this.squares[toX][toY].piece.square = null;
		}
		this.squares[toX][toY].piece = previousPiece;
		this.squares[fromX][fromY].piece = null;
	}
// Returns current FEN
chessBoard.prototype.currentFEN =
	function(reduced) {
		var FEN="";
		for (var num = 8; num >= 1; num--) {
			var emptyCounter = 0;
			for (var keyVar in this.squares) {
				if (this.squares[keyVar][num].piece != null) {
					if (emptyCounter != 0) {
						FEN += emptyCounter;
						emptyCounter = 0;
					}
					var pieceName = this.squares[keyVar][num].piece.name;
					var pieceColor = this.squares[keyVar][num].piece.color;
					switch (pieceName) {
						case "rook":
						name = "r";
						break;
					case "bishop":
						name = "b";
						break;
					case "queen":
						name = "q";
						break;
					case "king":
						name = "k";
						break;
					case "pawn":
						name = "p";
						break;
					case "knight":
						name = "n";
						break;
					default:
						break;
					}
					if (pieceColor == "white") {
						name = name.toUpperCase();
						FEN += name;
					}
					else 
						FEN += name;
				} else
					emptyCounter++;
			}
			if (emptyCounter != 0)
				FEN += emptyCounter;
			if (num != 1)
				FEN += "/";
		}
		FEN += " " + this.currentMove.substr(0,1);
		FEN += " " + this.castling;
		FEN += " " + this.enPassant;
		if (!reduced) {
			FEN += " " + this.halfMoves;
			FEN += " " + this.fullMoves;
		}
		return FEN;
	}

// Board Square

function boardSquare(x, y) {
	this.x = x;
	this.y = y;
	this.piece;
}

// Board Piece

function boardPiece(name, color) {
	// Each piece hold the reference to they square it's in
	this.square;
	this.name = name;
	this.color = color;
}