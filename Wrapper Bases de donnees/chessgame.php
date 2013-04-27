<?php

$version = '1.0s'; // Cconstante

// Fonction retournant une erreur 404.
function redirectionErreur404() {
    header('HTTP/1.0 404 Not Found');
    exit;
}

// Ouverture de la base de données contenant les tables d'ouverture et de fermeture.
try {
	$bdd = new PDO('mysql:host=localhost;dbname=chessgames', 'root', 'root');
} catch(PDOException $e) {
	exit('Error: '.$e->getMessage());
}

// Parseur de l'url.
$input = file_get_contents('php://input');
$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], -1, PREG_SPLIT_NO_EMPTY);

if(count($chars)==3) {
	if($chars[1]=='openings') {
		// Selection selon le fen.
		try {
			$moves = $bdd->prepare("SELECT move, probatowin, probatonull, nb FROM openings WHERE fen LIKE ?");
			$moves->execute(array($chars[2]));
		} catch(PDOException $e) {
			exit('Error: '.$e->getMessage());
		}	
		header("Content-Type: application/json");
		// La boucle while est enlevee pour assurer la compatibilite avec Gson.
		$arrayMoves = $moves->fetchAll();
		echo json_encode($arrayMoves);
		
    } elseif($chars[1]=='endings') {
		// Selection selon le fen.
		try {
			$moves = $bdd->prepare("SELECT move, probatowin, probatonull, nb FROM endings WHERE fen LIKE ?");
			$moves->execute(array($chars[2]));
		} catch(PDOException $e) {
			exit('Error: '.$e->getMessage());
		}
		header("Content-Type: application/json");
		// La boucle while est enlevee pour assurer la compatibilite avec Gson.
		$arrayMoves = $moves->fetchAll();
		echo json_encode($arrayMoves);
		
    } else {
    	redirectionErreur404();
	}
        
} elseif(count($chars)==2 && $chars[1]=='version') {
	// Envoie du numéro de version
	echo $version;
} else {
	redirectionErreur404();
}

//RewriteEngine On
//RewriteCond %{REQUEST_URI} ^/rest/.*$ [NC]
//RewriteRule ^(.*)$ /chessgame.php [L]