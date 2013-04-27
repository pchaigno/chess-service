<?php

$version = '1.0'; // Constante

// Fonction retournant une erreur 404.
function redirectionErreur404() {
    header('HTTP/1.0 404 Not Found');
    exit;
}

// Transforme le xml renvoye par chessok en JSon et l'affiche.
function xmlToJson($xmlstr, $whiteToPlay) {

	$moves = new SimpleXMLElement($xmlstr);

	// On calcule toutes les stats necessaires pour le serveur central.
	foreach($moves->MoveList->Item as $item){
		$nbWWhite = $item->WhiteWins;
		$nbWBlack = $item->BlackWins;
		$nbDraws = $item->Draws;
		$nb = $nbWWhite + $nbWBlack + $nbDraws;
		// Cast pour eviter de se retouver avec un SimpleXMLObject au lieu d'un string.
		$move = (string)($item->Move);
		
		if($whiteToPlay) {
			$probaToWin = round($nbWWhite/$nb,3);
		} else {
			$probaToWin = round($nbWBlack/$nb,3);
		}
		
		$movesArray[]=array('move'=>$move, 'probatowin'=>$probaToWin, 'probatonull'=>round($nbDraws/$nb,3), 'nb'=>$nb);
	}
	echo json_encode($movesArray);
}

// Parseur de l'url.
$input = file_get_contents('php://input');
$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], 4, PREG_SPLIT_NO_EMPTY); // Remplacer 4 par -1 (pour gerer les slahs).

if(count($chars)==4 && $chars[1]=='rest') {
	if($chars[2]=='openings') {
		// Interrogation du site chessok et recuperation des coups.
		$fen = rawurlencode(rawurldecode($chars[3]));
		
		// On regarde qui va jouer (blancs ou noirs).
		preg_match("/^[^ ]* ([bw]) .*$/", rawurldecode($fen), $matches);
		if(count($matches)==2) {
			$whiteToPlay = ($matches[1]=='w');
		}
		
		// On coupe la chaine jusqu'au dernier tiret.
		$fen = substr($fen, 0, strpos($fen, '-')+1);
		$query = 'fen='.$fen;
		
		$curlRequest = curl_init();
		curl_setopt($curlRequest, CURLOPT_HTTPHEADER, array("Content-Type: application/x-www-form-urlencoded; charset=utf-8", "Content-length: ".strlen($query), "Connection: close"));
		curl_setopt($curlRequest, CURLOPT_URL, 'http://chessok.com/onlineserv/opening/connection.php?timestamp='.time());
		curl_setopt($curlRequest, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($curlRequest, CURLOPT_POST, 1); 
		curl_setopt($curlRequest, CURLOPT_POSTFIELDS, $query);

		$result = curl_exec($curlRequest);
		curl_close($curlRequest);
		
		header("Content-Type: application/json");
		xmlToJson($result, $whiteToPlay);
		
    } else {
    	redirectionErreur404();
	}
      
} elseif(count($chars)==3 && $chars[1]=='rest' && $chars[2]=='version') {
	// Envoie du numero de version
	echo $version;
} else {
	redirectionErreur404();
}

//RewriteEngine On
//RewriteCond %{REQUEST_URI} ^/rest/.*$ [NC]
//RewriteRule ^(.*)$ /requete_chessok.php [L]

?>