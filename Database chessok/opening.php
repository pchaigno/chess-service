<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en-US">

<head profile="http://gmpg.org/xfn/11">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="css/main.css" />
	<script type="text/javascript" src="js/tree.js"></script>
	<script type="text/javascript" src="js/ajaxrequest.js"></script>
	<script type="text/javascript" src="js/parser.js"></script>
	<script type="text/javascript" src="js/board.js"></script>
	<script type="text/javascript" src="js/rules.js"></script>
	<script type="text/javascript" src="js/gui.js"></script>
	<script type="text/javascript" src="js/raphael.js"></script>
</head>
<body>

<center>
<table cellpadding="0" cellspacing="0" id="opening_env">
<tr>
	<td id="board">
	<div id="board_svg"></div>
	<div class="shadow1">
		<div class="shadow2">
			<table id="game_board" cellpadding="0" cellspacing="0">
			<tr>
				<td class="corner"></td>
				<td id="topa" class="lettertop">A</td>
				<td id="topb" class="lettertop">B</td>
				<td id="topc" class="lettertop">C</td>
				<td id="topd" class="lettertop">D</td>
				<td id="tope" class="lettertop">E</td>
				<td id="topf" class="lettertop">F</td>
				<td id="topg" class="lettertop">G</td>
				<td id="toph" class="lettertop">H</td>
				<td class="corner"></td>
			</tr>
			<tr>
				<td id="left8" class="numberleft">8</td>
				<td class="white" id="square11"></td>
				<td class="black" id="square21"></td>
				<td class="white" id="square31"></td>
				<td class="black" id="square41"></td>
				<td class="white" id="square51"></td>
				<td class="black" id="square61"></td>
				<td class="white" id="square71"></td>
				<td class="black" id="square81"></td>
				<td id="right8" class="numberright">8</td>
			</tr>
			<tr>
				<td id="left7" class="numberleft">7</td>
				<td class="black" id="square12"></td>
				<td class="white" id="square22"></td>
				<td class="black" id="square32"></td>
				<td class="white" id="square42"></td>
				<td class="black" id="square52"></td>
				<td class="white" id="square62"></td>
				<td class="black" id="square72"></td>
				<td class="white" id="square82"></td>
				<td id="right7" class="numberright">7</td>
			</tr>
			<tr>
				<td id="left6" class="numberleft">6</td>
				<td class="white" id="square13"></td>
				<td class="black" id="square23"></td>
				<td class="white" id="square33"></td>
				<td class="black" id="square43"></td>
				<td class="white" id="square53"></td>
				<td class="black" id="square63"></td>
				<td class="white" id="square73"></td>
				<td class="black" id="square83"></td>
				<td id="right6" class="numberright">6</td>
			</tr>
			<tr>
				<td id="left5" class="numberleft">5</td>
				<td class="black" id="square14"></td>
				<td class="white" id="square24"></td>
				<td class="black" id="square34"></td>
				<td class="white" id="square44"></td>
				<td class="black" id="square54"></td>
				<td class="white" id="square64"></td>
				<td class="black" id="square74"></td>
				<td class="white" id="square84"></td>
				<td id="right5" class="numberright">5</td>
			</tr>
			<tr>
				<td id="left4" class="numberleft">4</td>
				<td class="white" id="square15"></td>
				<td class="black" id="square25"></td>
				<td class="white" id="square35"></td>
				<td class="black" id="square45"></td>
				<td class="white" id="square55"></td>
				<td class="black" id="square65"></td>
				<td class="white" id="square75"></td>
				<td class="black" id="square85"></td>
				<td id="right4" class="numberright">4</td>
			</tr>
			<tr>
				<td id="left3" class="numberleft">3</td>
				<td class="black" id="square16"></td>
				<td class="white" id="square26"></td>
				<td class="black" id="square36"></td>
				<td class="white" id="square46"></td>
				<td class="black" id="square56"></td>
				<td class="white" id="square66"></td>
				<td class="black" id="square76"></td>
				<td class="white" id="square86"></td>
				<td id="right3" class="numberright">3</td>
			</tr>
			<tr>
				<td id="left2" class="numberleft">2</td>
				<td class="white" id="square17"></td>
				<td class="black" id="square27"></td>
				<td class="white" id="square37"></td>
				<td class="black" id="square47"></td>
				<td class="white" id="square57"></td>
				<td class="black" id="square67"></td>
				<td class="white" id="square77"></td>
				<td class="black" id="square87"></td>
				<td id="right2" class="numberright">2</td>
			</tr>
			<tr>
				<td id="left1" class="numberleft">1</td>
				<td class="black" id="square18"></td>
				<td class="white" id="square28"></td>
				<td class="black" id="square38"></td>
				<td class="white" id="square48"></td>
				<td class="black" id="square58"></td>
				<td class="white" id="square68"></td>
				<td class="black" id="square78"></td>
				<td class="white" id="square88"></td>
				<td id="right1" class="numberright">1</td>
			</tr>
			<tr>
				<td class="corner"></td>
				<td id="bottoma" class="letterbottom">A</td>
				<td id="bottomb" class="letterbottom">B</td>
				<td id="bottomc" class="letterbottom">C</td>
				<td id="bottomd" class="letterbottom">D</td>
				<td id="bottome" class="letterbottom">E</td>
				<td id="bottomf" class="letterbottom">F</td>
				<td id="bottomg" class="letterbottom">G</td>
				<td id="bottomh" class="letterbottom">H</td>
				<td class="corner"><center><a title="Flip board" id="flipicon" href="javascript: tree.gui.flipBoard();"><img src="images/board/flip.jpg" /></a></center></td>
			</tr>
			</table>
		</div>
	</div>
	</td>
	<td width="10px;"></td>
	<td id="moves">
	<div class="shadow1">
		<div class="shadow2">
			<div id="moves_box">
				<div class="curr_fen">
					<input type="button" class="treebutton" onclick="tree.loadFEN('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1');" value="Clear board"/>
					<input type="button" class="treebutton" onclick="tree.takeback();" value="Takeback"/>
					<textarea id="loadpgn"/>paste PGN here</textarea>
					<input type="button" class="treebutton" onclick="tree.loadPGN();" value="Load PGN"/>
					<div style="clear: both; height: 4px;"></div>
					FEN: <input type="text" id="currfen" onclick="this.select()"/>
					<input type="button" class="treebutton" onclick="tree.loadFEN();" value="Load FEN"/>
				</div>
				<div style="clear: both"></div>
				<div class="curr_pgn">PGN: <span id="notation_box"></span></div>
				<div style="clear: both"></div>
				<div id="moves_table"></div>
			</div>
		</div>
	</div>
	</td>
	</tr>
</table>
</center>
<script>
var fen = "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
	if (!fen) {
		fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	}
	var tree = new tree();
</script>
</body>
</html>