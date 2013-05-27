function GUI() {
	var numbers = [8, 7, 6, 5, 4, 3, 2, 1];
	var letters = ["a", "b", "c", "d", "e", "f", "g", "h"];
	var letter = [];
	letter["a"] = 1;
	letter["b"] = 2;
	letter["c"] = 3;
	letter["d"] = 4;
	letter["e"] = 5;
	letter["f"] = 6;
	letter["g"] = 7;
	letter["h"] = 8;
	
	if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/MSIE 6\./)) {
		var ie6 = true;
	}

	
	try {
		var canvas = Raphael("board_svg", 432, 418);
	} catch (e) {
		
	}
	this.arrow;
	this.arrow_over;
	this.startPoint;
	this.startPoint_over;

	// If board is flipped
	this.flipped = false;

	this.drawBoardPosition =
	function(game, board) {
		var mtable = document.getElementById("moves_table");
		mtable.innerHTML = "";
		var square;
		for(var rows = 1; rows <= 8; rows++) {
			for(var cols = 1; cols <= 8; cols++) {
				square = document.getElementById("square" + rows + cols);
				while (square.childNodes[0]) {
					square.removeChild(square.childNodes[0]);
				}
			}
		}
		var GUIsquare;
		var piece;
		var square;
		var shortName;
		for(var i = 0; i < board.pieces.length; i++) {
			var piece = board.pieces[i];
			var square = piece.square;
			if (square != undefined) {
				GUIsquare = this.getGUISquare(square.x, square.y);
				if (piece.name != "knight")
					shortName = piece.name.charAt(0);
				else
					shortName = "n";
				var pieceImg = document.createElement("IMG");
				pieceImg.id = "piece" + square.x + square.y;
				pieceImg.src = "images/pieces/classic/45" + (ie6? "gif" : "") + "/" + piece.color.charAt(0) + shortName + (ie6? ".gif" : ".png");
				GUIsquare.appendChild(pieceImg);
			}
		}
		
		this.drawCoordinates();
		if (this.arrow) {
			this.arrow.remove();
			this.arrow = undefined;
		}

		if (this.startPoint) {
			this.startPoint.remove();
			this.startPoint = undefined;
		}
		
		if (this.arrow_over) {
			this.arrow_over.remove();
			this.arrow_over = undefined;
		}

		if (this.startPoint_over) {
			this.startPoint_over.remove();
			this.startPoint_over = undefined;
		}

		var id = game.notationMove;

		if(id != "start") {
			var fromX = game.displayNotation[id]["fromto"].fromX;
			var fromY = game.displayNotation[id]["fromto"].fromY;
			var toX = game.displayNotation[id]["fromto"].toX;
			var toY = game.displayNotation[id]["fromto"].toY;

			var knight = (game.displayNotation[id]["token"].charAt(0) == "N" ? true : false);

			this.drawArrow(fromX, fromY, toX, toY, knight);
		} else {
			if (this.arrow) {
				this.arrow.remove();
				this.arrow = undefined;
			}
		}

		var cfen = document.getElementById("currfen");
		cfen.value = board.currentFEN();
	}
	
	this.loadTreeMoves =
	function(moves, pgnmoves, header) {
		// Table header
		var result = "<table cellpadding='0' cellspacing='0'>\n";
		result += "<tr class='header'><td>Move</td><td>Eval.</td><td>Games</td><td>W/won</td><td>B/won</td><td>Draws</td><td>W/ELO</td><td>B/ELO</td><td>Year</td></tr>\n";
		for (var i = 0; i < moves.length; i++) {
			var totalGames = parseInt(moves[i]["WhiteWins"]) + parseInt(moves[i]["BlackWins"]) + parseInt(moves[i]["Draws"]);
			if(moves[i]["Move"] == "GE" || totalGames == 0) {
				result += "<tr>\n";
			} else {
				result += "<tr onmouseover='tree.gui.moveover(" + i + ")' onmouseout='tree.gui.moveout()' onclick=\"tree.proceed(" + i + ")\">\n";
			}
			result += "<td><b>" + pgnmoves[i] + "</td></b>";
			result += "<td>" + (moves[i]["Evaluation"] ? moves[i]["Evaluation"] : "") + "</td>";

			if (totalGames != 0) {
				result += "<td>" + totalGames + "</td>";
				result += "<td>" + Math.round(moves[i]["WhiteWins"] / totalGames * 1000) / 10 + "% </td>";
				result += "<td>" + Math.round(moves[i]["BlackWins"] / totalGames * 1000) / 10 + "% </td>";
				result += "<td>" + Math.round(moves[i]["Draws"] / totalGames * 1000) / 10 + "% </td>";
				result += "<td>" + moves[i]["WhiteRating"] + "</td>";
				result += "<td>" + moves[i]["BlackRating"] + "</td>";
				result += "<td>" + moves[i]["Year"] + "</td>";
			} else {
					result += "<td>0</td><td></td><td></td><td></td><td></td><td></td><td></td>\n";
			}
			result += "</tr>\n";
		}

		result += "</table><br />\n";

		if (header["GameResult"]) {
			var gameCom;
			gameCom = header["GameResult"] + ", " + header["WhiteName"] + " - " + header["BlackName"] + ", " + header["Site"] + ", " + header["Year"];
			result += "<span class=\"gamecomment\">" + gameCom + " </span>";
		}
		
		var mtable = document.getElementById("moves_table");
		if (moves.length) {
			mtable.innerHTML = result;
		} else {
			mtable.innerHTML = "No data";
		}
	}
	
	this.moveover =
	function(id) {
		this.drawCoordinates();
		if (this.arrow_over) {
			this.arrow_over.remove();
			this.arrow_over = undefined;
		}

		var ucimove = tree.movesTree[id]["Move"];
		var fromX = ucimove.charAt(0);
		var fromY = ucimove.charAt(1);
		var toX = ucimove.charAt(2)
		var toY = ucimove.charAt(3);

		var knight = (tree.board.squares[fromX][fromY].piece.name == "knight" ? true : false);

		this.drawArrow(fromX, fromY, toX, toY, knight, true);
	}
	
	this.moveout =
	function() {
		if (this.arrow_over) {
			this.arrow_over.remove();
			this.arrow_over = undefined;
		}
	}

	// Rotates the board
	this.flipBoard =
	function() {
		document.getElementById("flipicon").blur();
		if (this.flipped)
			this.flipped = false;
		else
			this.flipped = true;
		this.drawBoardPosition(tree.game, tree.board);
	}

	// Draws the coordinates according to this.flipped state
	this.drawCoordinates=
	function() {
		var lttr;
		var nmbr;
		for (var i = 0; i < letters.length; i++) {
			if (this.flipped) {
				lttr = letters[7 - i];
				nmbr = 8 - i;
			} else {
				lttr = letters[i];
				nmbr = i + 1;
			}
			var holder = document.getElementById("top" + letters[i]);
			holder.firstChild.nodeValue = lttr.toUpperCase();
			holder = document.getElementById("bottom" + letters[i]);
			holder.firstChild.nodeValue = lttr.toUpperCase();
			holder = document.getElementById("left" + parseInt(i + 1));
			holder.firstChild.nodeValue = nmbr;
			holder = document.getElementById("right" + parseInt(i + 1));
			holder.firstChild.nodeValue = nmbr;
		}
	}

	this.drawNotation =
	function(game) {
		var notationBox = document.getElementById("notation_box");
		var bracket1 = "<span class='invisible'>{</span>";
		var bracket2 = "<span class='invisible'>}</span>";
		var notation = "<span class='notmove' id='movestart'><a href='javascript: tree.loadMove(\"start\")'>[" + "<span class='invisible'>Void </span>" + "<span class='invisible'>\"</span>#<span class='invisible'>\"</span>" +"]</a></span>";

		for (var i = 0; i < game.displayNotation.length; i++) {
			var token = game.displayNotation[i];
			if (token["type"] == "regular") {
				if (token["color"] == "white") {
					notation += "<span class='num'> " + token["num"] + ". </span>\n";
					notation += "<span class='notmove' id='move" + i + "'><a href='javascript: tree.loadMove(" + i + ")'>" + token["token"] + "</a></span>\n";
				} else if (token["color"] == "black") {
					notation += " <span class='notmove' id='move" + i + "'><a href='javascript: tree.loadMove(" + i + ")'>" + token["token"] + "</a></span>\n";
				}
			}

			if (token["fenlink"] != undefined) {
				var corrFEN = game.FENs[token["fenlink"]];
				if (game.currPosition == game.FENs[0]) {
					currToken = "start";
				} else if (corrFEN == game.currPosition) {
					var currToken = i;
				}
			}
		}
		
		if (currToken == undefined) {
			currToken = "start";
		}
		notation += "<span class='invisible'> " + "*" + " </span>" + "&nbsp;" + bracket1 + "<span class='notmove'>[*]</span>" + bracket2;
		notationBox.innerHTML = notation;
		var notMove = document.getElementById("move" + currToken);
		game.notationMove = currToken;
		this.addClass(notMove, "selected");
	}

	this.displayMove =
	function(game, board, oldid) {
		var oldMove = document.getElementById("move" + oldid);
		var notMove = document.getElementById("move" + game.notationMove);
		if (this.hasClass(oldMove, "selected")) {
			this.removeClass(oldMove, "selected");
		}
		this.addClass(notMove, "selected");

		notMove.childNodes[0].blur();
		notMove.childNodes[0].focus();
		this.drawBoardPosition(game, board);
	}
	
	this.drawArrow =
	function(fromX, fromY, toX, toY, knight, over) {
		//alert(fromX + fromY + toX + toY);
		var fX = letter[fromX];
		var fY = 9 - fromY;
		var tX = letter[toX];
		var tY = 9 - toY;
		if (this.flipped) {
			fX = 9 - fX;
			fY = 9 - fY
			tX = 9 - tX;
			tY = 9 - tY
		}

		fX = fX * 50 - 25 + 17;
		fY = fY * 50 - 25 + 21;
		tX = tX * 50 - 25 + 17;
		tY = tY * 50 - 25 + 21;
		
		var arrow;
		var startPoint;

		var lX = Math.abs(tX - fX);
		var lY = Math.abs(tY - fY);
		var tL = Math.sqrt(lX*lX + lY*lY);
		var mod = -1*lX / (tX - fX);

		if (lX == 0) {
			var rotationAngle = 0;
		} else {
			var rotationAngle = - Math.atan((tX - fX)/(tY - fY));
		}
		
		if (lY == 0) {
			rotationAngle = - rotationAngle;
		} else if (tY > fY) {
			rotationAngle = Math.PI + rotationAngle;
		}

		rotationAngle = rotationAngle / Math.PI * 180;
		
		var color = "#ff0";
		if (over) {
			var pcolor = tree.board.squares[fromX][fromY].piece.color;
		}
		
		if (over && pcolor == "white") {
			color = "#0f0";
		} else if (over) {
			color = "#00f";
		}

		try {
			//var c = canvas.path({fill: "#ff0", stroke: "#000", opacity: 0.66}).moveTo(fX, fY).lineTo(fX - 5, fY - tL + 15);
			if (knight) {
				var sq = Math.sqrt(50*50/2);
				//this.arrow = canvas.path({fill: "#ff0", stroke: "#000", opacity: 0.6}).moveTo(fX, fY).qcurveTo(fX - 5 + mod*sq, fY - 1.5*sq, fX - 5 + mod*sq, fY - 3*sq + 15).lineTo(fX - 15 + mod*sq, fY - 3*sq + 20).lineTo(fX + mod*sq, fY - 3*sq).lineTo(fX + 15 + mod*sq, fY - 3*sq + 20).lineTo(fX + 5 + mod*sq, fY - 3*sq + 15).qcurveTo(fX + 5 + mod*sq, fY - 1.5*sq, fX, fY);
				arrow = canvas.path({fill: color, stroke: "#000", opacity: 0.6}).moveTo(fX, fY).curveTo(fX - 2 + 0.5*mod*sq, fY - sq, fX - 5 + mod*sq, fY - 1.5*sq, fX - 5 + mod*sq, fY - 3*sq + 15).lineTo(fX - 15 + mod*sq, fY - 3*sq + 20).lineTo(fX + mod*sq, fY - 3*sq).lineTo(fX + 15 + mod*sq, fY - 3*sq + 20).lineTo(fX + 5 + mod*sq, fY - 3*sq + 15).curveTo(fX + 5 + mod*sq, fY - 1.5*sq, fX + 2 + 0.5*mod*sq, fY - sq, fX, fY);


				var knightAngle = Math.atan(1/3) / Math.PI * 180;
				arrow.rotate(rotationAngle - mod*knightAngle, fX, fY);
				//alert(/Math.PI * 180);
			} else {
				arrow = canvas.path({fill: color, stroke: "#000", opacity: 0.6}).moveTo(fX, fY).lineTo(fX - 5, fY - tL + 15).lineTo(fX - 15, fY - tL + 20).lineTo(fX, fY - tL).lineTo(fX + 15, fY - tL + 20).lineTo(fX + 5, fY - tL + 15).lineTo(fX, fY);
				arrow.rotate(rotationAngle, fX, fY);
			}
			if (!over) {
				startPoint = canvas.circle(fX, fY, 10);
				startPoint.attr({fill: color, stroke: "#000", opacity: 0.25});
			}
		} catch (e) {
			
		}
		if (over) {
			this.arrow_over = arrow;
		} else {
			this.arrow = arrow;
			this.startPoint = startPoint;
		}
	}

	// Returns the GUI square reference given the board coordinates;
	this.getGUISquare =
	function(x, y) {
		if (!this.flipped) {
			x = letter[x];
			y = 9 - y;
		} else {
			x = 9 - letter[x];
		}
		return document.getElementById("square" + x + y);
	}

	// Returns the board coordinates given the id of GUI square
	this.getSquare =
	function(id) {
		if (id.match(/piece/)) {
			return {x: id.charAt(5), y: id.charAt(6)};
		}
		if (!this.flipped) {
			x = letters[id.charAt(6) - 1];
			y = 9 - id.charAt(7);
		} else {
			x = letters[8 - id.charAt(6)];
			y = id.charAt(7);
		}
		return {x: x, y: y};
	}


	this.addClass =
	function(target, classValue) {
		if (!this.hasClass(target, classValue))
			if (target.className == "")
				target.className = classValue;
			else
				target.className += " " + classValue;
		return true;
	}

	this.removeClass =
	function(target, classValue) {
		if(this.hasClass(target, classValue)) {
			var removedClass = target.className;
			var pattern = new RegExp("(^| )" + classValue + "( |$)");
			removedClass = removedClass.replace(pattern, "$1");
			removedClass = removedClass.replace(/ $/, "");
			target.className = removedClass;
		}
		return true;
	}

	this.hasClass =
	function(target, classValue) {
		var pattern = new RegExp("(^| )" + classValue + "( |$)");    
		if (target.className.match(pattern))
			return true;
		return false;
	}
}