package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Tree {
	// Ajax request object, used to fetch broadcast lists and/or pgn files (broadcasts)
	//AjaxRequest ajaxRequest;

	// Load PGN parser
	Parser parser;
	
	// Stores parsed/unparsed games
	ChessGame game;
	
	// Handles the logics of chess board
	ChessBoard board;
	
	// Handles the display and layout
	//GUI gui;
	
	List<Map<String, String>> movesTree;
	
	public Tree(String fen) {
		//this.ajaxRequest = new AjaxRequest();
		
		this.parser = new Parser();
		
		this.game = new ChessGame();
		this.game.FENs.push(fen);
		this.game.currPosition = fen;
		
		this.board = new ChessBoard();
		this.board.loadFEN(fen);
		
		//this.gui = new GUI();
		//this.gui.drawBoardPosition(this.game, this.board);
		//this.gui.drawNotation(this.game);
		
		this.movesTree = new LinkedList<Map<String, String>>();
	
		//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
	}

	// TODO Unused.
	// Fetches the tree
	/*public void loadTree(String fen) {
		//this.ajaxRequest.send(fen);
	}*/

	// TODO Unused.
	/*public void respondLoadTree(XMLObject xmlTree) {
		XMLObject tree = xmlTree;
		if(tree.getElementsByTagName("error")[0]) {
			alert(tree.getElementsByTagName("error")[0].firstChild.nodeValue);
			return;
		}
		
		this.movesTree = new LinkedList<Map<String, String>>();
		Stack<String> pgnMoves = new Stack<String>();
		Map<String, String> gameHeader = new HashMap<String, String>();
		
		// If game info is available
		XMLObject ginfo = tree.getElementsByTagName("GameHeader");
		if(ginfo) {
			for(int i=0 ; i<ginfo.length ; i++) {
				for(int j=0 ; j<ginfo[i].childNodes.length ; j++) {
					gameHeader.push(ginfo[i].childNodes[j].nodeName, ginfo[i].childNodes[j].firstChild.nodeValue);
				}
			}
		}

		// Loop through moves
		XMLObject items = tree.getElementsByTagName("Item");
		for(int i=0 ; i<items.length ; i++) {
			this.movesTree.add(i, new HashMap<String, String>());
			for(int j=0 ; j<items[i].childNodes.length ; j++) {
				// Save move details
				if(items[i].childNodes[j].firstChild) {
					this.movesTree.get(i).put(items[i].childNodes[j].nodeName, items[i].childNodes[j].firstChild.nodeValue);
				}
			}
			pgnMoves.push(this.parser.UCItoPGN(this.movesTree.get(i), this.board));
		}

		//this.gui.loadTreeMoves(this.movesTree, pgnMoves, gameHeader);
	}*/

	public void loadFEN(String lfen) {
		this.game = new ChessGame();
		this.game.FENs.add(lfen);
		this.game.currPosition = lfen;
		this.board.loadFEN(lfen);
		//this.gui.drawBoardPosition(this.game, this.board);
		//this.gui.drawNotation(this.game);
		//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
	}
	
	public void loadPGN(String PGN) {
		//String PGN = document.getElementById("loadpgn").value; // TODO Replaced
		this.parser.parsePGN(PGN);
		this.parser.parseNotation(this.board, this.game);
		this.game.currPosition = this.board.currentFEN(false);
		this.game.notationMove = this.game.FENs.size() - 2;
		//this.gui.drawBoardPosition(this.game, this.board);
		//this.gui.drawNotation(this.game);
		//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
	}

	public void loadMove(int id) {
		this.game.notationMove = id;
		String lfen = this.game.FENs.get(this.game.displayNotation.get(id).fenlink);
		this.game.currPosition = lfen;
		this.board.loadFEN(lfen);
		//this.gui.drawBoardPosition(this.game, this.board);
		//this.gui.drawNotation(this.game);
		//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
	}

	public void nextMove() {
		int id;
		if (this.game.notationMove!=-1) {
			id = 0;
		} else {
			id = this.game.notationMove + 1;
		}
		if (id < this.game.FENs.size() - 1) {
			this.game.notationMove = id;
			String lfen = this.game.FENs.get(this.game.displayNotation.get(id).fenlink);
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			//this.gui.drawBoardPosition(this.game, this.board);
			//this.gui.drawNotation(this.game);
			//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
		}
	}

	public void previousMove() {
		int id;
		if (this.game.notationMove==0 || this.game.notationMove!=-1) {
			id = -1;
		} else {
			id = this.game.notationMove - 1;
		}
		if (id != this.game.notationMove) {
			this.game.notationMove = id;
			String lfen = this.game.FENs.get(this.game.displayNotation.get(id).fenlink);
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			//this.gui.drawBoardPosition(this.game, this.board);
			//this.gui.drawNotation(this.game);
			//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
		}
	}
	
	public void takeback() {
		int id;
		if (this.game.notationMove==0 || this.game.notationMove==-1) {
			id = -1;
		} else {
			id = this.game.notationMove - 1;
		}
		if (id != this.game.notationMove) {
			this.game.notationMove = id;
			String lfen = this.game.FENs.get(this.game.displayNotation.get(id).fenlink);
			this.game.currPosition = lfen;
			this.board.loadFEN(lfen);
			this.game.displayNotation.pop();
			//this.gui.drawBoardPosition(this.game, this.board);
			//this.gui.drawNotation(this.game);
			//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
		}
	}
	
	public void proceed(int id) {
		//String move = this.movesTree.get(id); // TODO Useless?
		String pgnmove = this.parser.UCItoPGN(this.movesTree.get(id), this.board);
		int len_mod = -1;
		if(this.game.notationMove!=-1) { 
			len_mod = this.game.notationMove;
		}
		int length = this.game.displayNotation.size() - 1 - len_mod;
		for (int i=0 ; i<length ; i++) {
			this.game.FENs.pop();
			this.game.displayNotation.pop();
		}

		this.parser.parseNotationTokens(this.board, this.game, pgnmove);
		if (this.game.notationMove!=-1) {
			this.game.notationMove = 0;
		} else {
			this.game.notationMove++;
		}
		this.game.currPosition = this.board.currentFEN(false);
		//this.gui.drawBoardPosition(this.game, this.board);
		//this.gui.drawNotation(this.game);
		//this.loadTree(this.board.currentFEN(true)); // TODO Unused.
	}
}