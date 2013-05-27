function parser() {

	var letter = [];
	letter["a"] = 1;
	letter["b"] = 2;
	letter["c"] = 3;
	letter["d"] = 4;
	letter["e"] = 5;
	letter["f"] = 6;
	letter["g"] = 7;
	letter["h"] = 8;

	this.rules = new chessRules();

	// Convert UCI style move into PGN style move
	this.UCItoPGN =
	function(moveArray, board) {
		// GE is "empty" move, indicates the line end (as game populating this line ended there)
		if (moveArray["Move"] == "GE") {
			return "GE";
		}

		var ucimove = moveArray["Move"];
		var fromX = ucimove.charAt(0);
		var fromY = ucimove.charAt(1);
		var toX = ucimove.charAt(2)
		var toY = ucimove.charAt(3);
		var piece = board.squares[fromX][fromY].piece.name;
		var capture = false;
		var promotion = false;

		if (board.squares[toX][toY].piece != undefined) {
			capture = true;
		}
		

		// Castling
		if (piece == "king" && Math.abs(letter[fromX] - letter[toX]) == 2) {
			if (toX == "g") {
				return "O-O";
			} else {
				return "O-O-O";
			}
		}

		var pgnfromX = "";
		var pgnfromY = "";

		// Determine if we need fromX/fromY coordinates in PGN move
		if (eval("this.rules." + piece + "(board, '', '', toX, toY, capture);")[0]) {
			pgnfromX = "";
			pgnfromY = "";
		} else if (eval("this.rules." + piece + "(board, fromX, '', toX, toY, capture);")[0]) {
			pgnfromX = fromX;
			pgnfromY = "";
		} else if (eval("this.rules." + piece + "(board, '', fromY, toX, toY, capture);")[0]) {
			pgnfromX = "";
			pgnfromY = fromY;
		} else if (eval("this.rules." + piece + "(board, fromX, fromY, toX, toY, capture);")[0]) {
			pgnfromX = fromX;
			pgnfromY = fromY;
		}

		var pgnpiece;
		if (piece == "knight") {
			pgnpiece = "N";
		} else if (piece == "pawn") {
			pgnpiece = "";
		} else {
			pgnpiece = piece.charAt(0).toUpperCase();
		}

		// En passant capture
		if (toX + toY == board.enPassant && piece == "pawn") {
			capture = true;
		}

		var pgncapture = "";
		if (capture) {
			pgncapture = "x";
		}
		if (capture && piece == "pawn") {
			pgnfromX = fromX;
		}

		return pgnpiece + pgnfromX + pgnfromY + pgncapture + toX + toY;
	}


	// Parses the PGN file, splits it for every game and creates game objects
	this.parsePGN = 
	function(PGN) {
		// IE uses \r\n, Gecko browsers use \n
		PGN = PGN.replace(/\r\n/g, "\n");
		// Replace bracket {} commentaries because the can mess up how we determine the game edges, store them in array
		PGN = PGN.replace(/\{([\W\w]*?)\}/g, "");

		// Replace strings (because they can contain the semicolon comments,
		// which could once again cripple our ability to parse the games correctly) and store them in array
		var tagStrings = [];
		PGN = PGN.replace(/"([^"\\\r\n]*(?:\\.[^"\\\r\n]*)*)"/g, "");

		// Replace semicolon this.commentaries and store them in array
		PGN = PGN.replace(/(;[\W\w]*?)\n/, "");
		
		// Split the Games
		var game = tree.game;

		var notation = PGN.replace(/(\[[\W\w]*?\])/g, "");
		
		// Strip numbers notation 
		notation = notation.replace(/\b[\d]+[\s]*[\.]+/g, "");

		// Strip variations
		notation = notation.replace(/\([^\(\)]*?\)/g, "");

		// Strip the result (ending)
		notation = notation.replace(/[\s]+(?:0-1|1-0|1\/2-1\/2|\*)[\s]+/, "");

		// Assign notation to game object for later parsing
		game.notation = notation;

		game.displayNotation = [];
	}

	// Parses the notation of given game. Uses board object
	this.parseNotation = 
	function (board, game) {
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
	this.parseNotationTokens =
	function (board, game, notation) {
		var notationTokens = notation.split(/[\s]+/);
		var token;
		// Loop through notation tokens
		for(var i = 0; i < notationTokens.length; i++) {
			token = notationTokens[i];
			// Regular move
			if (/[RBQKPN]?[a-h]?[1-8]?[x]?[a-h][1-8][=]?[QNRB]?[+#]?/.test(token)) {
				this.parseMove(board, game, token);
			// Castling
			} else if (/(O-O-O|O-O)\+?/.test(token)) {
				this.castle(board, game, token);
			}
		}
	}
	
	this.parseMove =
	function(board, game, token) {
		var moveArray = token.match(/([RBQKPN])?([a-h])?([1-8])?([x])?([a-h])([1-8])([=]?)([QNRB]?)([+#]?)/);
		var piece;
		if (moveArray[1]) {
			switch (moveArray[1].toLowerCase()) {
			case "r":
				piece = "rook";
				break;
			case "b":
				piece = "bishop";
				break;
			case "q":
				piece = "queen";
				break;
			case "n":
				piece = "knight";
				break;
			case "k":
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
			promoteTo = '';
		}

		// Determine the location of the piece to move using chess rules and incomplete information about it
		var pieceXY = eval("this.rules." + piece + "(board, fromX, fromY, toX, toY, capture);");

		var dispMove = [];
		dispMove["type"] = "regular";
		dispMove["token"] = token;
		dispMove["color"] = board.currentMove;
		dispMove["fromto"] = {fromX: pieceXY[0], fromY: pieceXY[1], toX: toX, toY: toY};

		// Make piece move
		board.moveHandler(piece, pieceXY[0], pieceXY[1], toX, toY, capture, promotion, promoteTo);

		// Add FEN to game.FENs
		game.FENs.push(board.currentFEN());

		dispMove["num"] = board.fullMoves;
		dispMove["fenlink"] = game.FENs.length - 1;
		game.displayNotation.push(dispMove);
	}
	
	this.castle =
	function(board, game, token) {
		var line;
		if (board.currentMove == "white")
			line = 1;
		else
			line = 8;

		var dispMove = [];
		dispMove["type"] = "regular";
		dispMove["token"] = token;
		dispMove["color"] = board.currentMove;

		// Add move to game.moves[]
		if (/^O-O\+?$/.test(token)) {
			dispMove["fromto"] = {fromX: "e", fromY: line, toX: "g", toY: line};
			
		} else {
			dispMove["fromto"] = {fromX: "e", fromY: line, toX: "c", toY: line};
		}

		// Castle on board
		board.castle(token);

		// Add FEN to game.FENs
		game.FENs.push(board.currentFEN());

		dispMove["num"] = board.fullMoves;
		dispMove["fenlink"] = game.FENs.length - 1;
		game.displayNotation.push(dispMove);
	}
}