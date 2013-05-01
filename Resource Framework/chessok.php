<?php

require('resource_framework.php');

$wrapper = new ResourceWrapper('1.0', false, false);
$wrapper->setDatabaseOpenings('http://chessok.com/onlineserv/opening/connection.php?timestamp='.time(), 'parserXMLToJSON');
$wrapper->rest();

// Transforme le xml renvoye par chessok en JSon et l'affiche.
function parserXMLToJSON($xmlstr, $fen) {
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

?>