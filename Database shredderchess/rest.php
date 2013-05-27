<?php

$version = '1.0s'; // Cconstante (SAN version)

// Fonction retournant une erreur 404.
function redirectionErreur404() {
    header('HTTP/1.0 404 Not Found');
    exit;
}

// Parse the text result from the distant website to get a JSON document.
function parserTextToJSON($text) {
	$lines = explode("\n", $text);
	$moves = array();
	foreach($lines as $line) {
		$infos = explode('#', $line);
		if(count($infos)==14) {
			$move = explode('...', $infos[0])[1];
			$percentage = $infos[7]/1000;
			$wins = round($infos[11]/$infos[6], 3);
			$draws = round($infos[12]/$infos[6], 3);
			$looses = round($infos[13]/$infos[6], 3);
			$nb_play = (int)$infos[6];
			$moves[] = array('move'=>$move, /*'value'=>$infos[4], 'percentage'=>$percentage, 'elo'=>$infos[8], 'performance'=>$infos[9],*/ 'probatowin'=>$wins, 'probatonull'=>$draws, /*'looses'=>$looses,*/ 'nb'=>$nb_play);
		}
	}
	return json_encode($moves);
}

// Parseur de l'url.
$input = file_get_contents('php://input');
$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], -1, PREG_SPLIT_NO_EMPTY);

if(count($chars)==3) {
	if($chars[1]=='openings') {
		// Get the FEN:
		// $ are replaced by /.
		$fen = rawurldecode($chars[2]);
		$fen = str_replace('$', '/', $fen);
		
		// Get the results from shredderchess.com:
		$curlRequest = curl_init('http://www.shredderchess.com/online/playshredder/fetch.php?action=book&fen='.urlencode($fen).'&la=en&bookcode=p40');
		curl_setopt($curlRequest, CURLOPT_RETURNTRANSFER, true);

		$result = curl_exec($curlRequest);
		curl_close($curlRequest);
		
		// Display the result as a JSON document:
		header("Content-Type: application/json");
		echo parserTextToJSON($result);
		
    } else {
    	redirectionErreur404();
	}
      
} elseif(count($chars)==2 && $chars[1]=='version') {
	// Envoie du numero de version
	echo $version;
} else {
	redirectionErreur404();
}

//RewriteEngine On
//RewriteCond %{REQUEST_URI} ^/rest/.*$ [NC]
//RewriteRule ^(.*)$ /rest.php [L]

/*
FEN test 0 : rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 1
			rnbqkbnr$pp1ppppp$8$2p5$4P3$5N2$PPPP1PPP$RNBQKB1R b KQkq - 0 1
FEN test 1 : rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq -
			rnbqkbnr$pppppppp$8$8$3P4$8$PPP1PPPP$RNBQKBNR b KQkq -
FEN test 2 : rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq -
			rnbqkb1r$pppppppp$5n2$8$2PP4$8$PP2PPPP$RNBQKBNR b KQkq -
*/
	
?>