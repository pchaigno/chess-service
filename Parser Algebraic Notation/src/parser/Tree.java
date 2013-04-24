package parser;

public class Tree {
	// Ajax request object, used to fetch broadcast lists and/or pgn files (broadcasts)
	AjaxRequest ajaxRequest;

	// Load PGN parser
	Parser parser;
	
	// Stores parsed/unparsed games
	ChessGame game;
	
	// Handles the logics of chess board
	Board board;
	
	// Handles the display and layout
	GUI gui;
	
	public Tree() {
		this.ajaxRequest = new AjaxRequest();
		this.parser = new Parser();
		this.game = new ChessGame();
		this.game.FENs[0] = fen;
		this.game.currPosition = fen;
		this.board = new chessBoard();
		this.board.loadFEN(fen);
		this.gui = new GUI();
		this.gui.drawBoardPosition(this.game, this.board);
		this.gui.drawNotation(this.game);
	}

	// Fetches the tree
	public void loadTree(String fen) {
		this.ajaxRequest.send(fen);
	}
	
	this.movesTree = [];
	
	this.loadTree(this.board.currentFEN(true));

	public void respondLoadTree(xmlTree) {
		var tree = xmlTree;
		if (tree.getElementsByTagName("error")[0]) {
			alert(tree.getElementsByTagName("error")[0].firstChild.nodeValue);
			return;
		} else {
			this.movesTree = [];
			var pgnMoves = [];
			var gameHeader = [];
			
			// If game info is available
			var ginfo = tree.getElementsByTagName("GameHeader");
			if (ginfo) {
				for (var i = 0; i < ginfo.length; i++) {
					for (var j = 0; j < ginfo[i].childNodes.length; j++) {
						gameHeader[ginfo[i].childNodes[j].nodeName] = ginfo[i].childNodes[j].firstChild.nodeValue;
					}
				}
			}

			// Loop through moves
			var items = tree.getElementsByTagName("Item");
			for (var i = 0; i < items.length; i++) {
				this.movesTree[i] = [];
				for (var j = 0; j < items[i].childNodes.length; j++) {
					// Save move details
					if (items[i].childNodes[j].firstChild) {
						this.movesTree[i][items[i].childNodes[j].nodeName] = items[i].childNodes[j].firstChild.nodeValue;
					}
				}
				pgnMoves.push(this.parser.UCItoPGN(this.movesTree[i], this.board));
			}

			this.gui.loadTreeMoves(this.movesTree, pgnMoves, gameHeader);
		}
	}

	public void loadFEN(lfen) {
		if (!lfen) {
			lfen = document.getElementById("currfen").value;
		}
		this.game = new chessGame;
		this.game.FENs[0] = lfen;
		this.game.currPosition = lfen;
		this.board.loadFEN(lfen);
		this.gui.drawBoardPosition(this.game, this.board);
		this.gui.drawNotation(this.game);
		this.loadTree(this.board.currentFEN(true));
	}
	
	public void loadPGN() {
		var PGN = document.getElementById("loadpgn").value;
		this.parser.parsePGN(PGN);
		this.parser.parseNotation(this.board, this.game);
		this.game.currPosition = this.board.currentFEN();
		this.game.notationMove = this.game.FENs.length - 2;
		this.gui.drawBoardPosition(this.game, this.board);
		this.gui.drawNotation(this.game);
		this.loadTree(this.board.currentFEN(true));
	}

	public void loadMove(id) {
		this.game.notationMove = id;
		var lfen = this.game.FENs[this.game.displayNotation[id]["fenlink"]];
		this.game.currPosition = lfen;
		this.board.loadFEN(lfen);
		this.gui.drawBoardPosition(this.game, this.board);
		this.gui.drawNotation(this.game);
		this.loadTree(this.board.currentFEN(true));
	}

	public void nextMove() {
		var id;
		if (this.game.notationMove == "start") {
			id = 0;
		} else {
			id = this.game.notationMove + 1;
		}
		if (id < this.game.FENs.length - 1) {
			this.game.notationMove = id;
			var lfen = this.game.FENs[this.game.displayNotation[id]["fenlink"]];
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			this.gui.drawBoardPosition(this.game, this.board);
			this.gui.drawNotation(this.game);
			this.loadTree(this.board.currentFEN(true));
		}
	}

	public void previousMove() {
		var id;
		if (this.game.notationMove == 0 || this.game.notationMove == "start") {
			id = "start";
		} else {
			id = this.game.notationMove - 1;
		}
		if (id != this.game.notationMove) {
			this.game.notationMove = id;
			var lfen = this.game.FENs[this.game.displayNotation[id]["fenlink"]];
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			this.gui.drawBoardPosition(this.game, this.board);
			this.gui.drawNotation(this.game);
			this.loadTree(this.board.currentFEN(true));
		}
	}
	
	public void takeback() {
		var id;
		if (this.game.notationMove == 0 || this.game.notationMove == "start") {
			id = "start";
		} else {
			id = this.game.notationMove - 1;
		}
		if (id != this.game.notationMove) {
			this.game.notationMove = id;
			var lfen = this.game.FENs[this.game.displayNotation[id]["fenlink"]];
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			this.game.displayNotation.pop();
			this.gui.drawBoardPosition(this.game, this.board);
			this.gui.drawNotation(this.game);
			this.loadTree(this.board.currentFEN(true));
		}
	}
	
	
	public void proceed(id) {
		var move = this.movesTree[id];
		var pgnmove = this.parser.UCItoPGN(this.movesTree[id], this.board);
		if (this.game.notationMove == "start") { 
			var len_mod = -1;
		} else {
			var len_mod = this.game.notationMove;
		}
		var length = this.game.displayNotation.length - 1 - len_mod;
		for (var i = 0; i < length; i++) {
			this.game.FENs.pop();
			this.game.displayNotation.pop();
			
		}

		this.parser.parseNotationTokens(this.board, this.game, pgnmove);
		if (this.game.notationMove == "start") {
			this.game.notationMove = 0;
		} else {
			this.game.notationMove++;
		}
		this.game.currPosition = this.board.currentFEN();
		this.gui.drawBoardPosition(this.game, this.board);
		this.gui.drawNotation(this.game);
		this.loadTree(this.board.currentFEN(true));
		
	}
}