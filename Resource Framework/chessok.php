<?php

require('resourcewrapper.class.php');

$wrapper = new ResourceWrapper();
$wrapper->setDatabaseOpenings('http://chessok.com/onlineserv/opening/connection.php?timestamp='.time(), '1.0', false, 'parserOpeningsToJSON', false);
$wrapper->setDatabaseEndings('http://chessok.com/onlineserv/endbase/connection.php?timestamp='.time(), '1.0', true, 'parserEndingsToJson', true);
$wrapper->rest();

// Transforme le xml renvoye par chessok en JSon et l'affiche.
function parserOpeningsToJSON($xmlstr, $fen) {
	// On regarde qui va jouer (blancs ou noirs).
	preg_match("/^[^ ]* ([bw]) .*$/", $fen, $matches);
	if(count($matches)==2) {
		$whiteToPlay = $matches[1]=='w';
	}
		
	$moves = new SimpleXMLElement($xmlstr);
	
	// On calcule toutes les stats necessaires pour le serveur central.
	$movesArray = array();
	foreach($moves->MoveList->Item as $item) {
		$nbWWhite = $item->WhiteWins;
		$nbWBlack = $item->BlackWins;
		$nbDraws = $item->Draws;
		$nb = $nbWWhite + $nbWBlack + $nbDraws;
		// Cast pour eviter de se retouver avec un SimpleXMLObject au lieu d'un string.
		$move = (string)($item->Move);

		if($nb>0) {
			if($whiteToPlay) {
				$probaToWin = round($nbWWhite/$nb, 3);
			} else {
				$probaToWin = round($nbWBlack/$nb, 3);
			}
			$movesArray[] = array('move'=>$move, 'probatowin'=>$probaToWin, 'probatonull'=>round($nbDraws/$nb, 3), 'nb'=>$nb);
		} else {
			// On met -1 pour les probas quand on ne peut pas les calculer (nombre de parties nulles)
			$movesArray[] = array('move'=>$move, 'probatowin'=>-1, 'probatonull'=>-1, 'nb'=>$nb);
		}
	}
	return json_encode($movesArray);
}

// Parse the text result from the distant website to get a JSON document.
function parserEndingsToJSON($text, $fen) {
	$moves = array();
	$xml=new SimpleXMLElement($text);
	$attributes = $xml->attributes();
	
	if(((string)$attributes['status'])=="ok"){
		$result=0;
		
		if(((int)$attributes['eval'])>0)
			$result=1;
		if(((int)$attributes['eval'])<0)
			$result=-1;
		
		$moves_list = (string)$attributes['line'];
		$move_list = explode(' ', $moves_list);
		$nb_moves = round(count($move_list)/2);
		
		if($nb_moves > 0){
			$moves[] = array('move'=>$move_list[0], 'result'=>$result, 'nb_moves'=>$nb_moves);
		}
	}

	return json_encode($moves);
}
?>