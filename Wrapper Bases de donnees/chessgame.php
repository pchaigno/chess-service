<?php

$version = "1.0s"; //constante

// fonction retournant une erreur 404
function redirectionErreur404() {
    header('HTTP/1.0 404 Not Found');
    exit;
}

// ouverture de la base de données contenant les tables d'ouverture et de fermeture
try {
	$bdd = new PDO('mysql:host=localhost;dbname=chessgames', 'root', 'root');
} catch(PDOException $e) {
	exit('Error: '.$e->getMessage());
}

// parseur de l'url    
$input = file_get_contents('php://input');
$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], -1, PREG_SPLIT_NO_EMPTY);

if(count($chars)==3) {
	if($chars[1]=='openings') {
		// selection selon le fen	
		try {
			$moves = $bdd->prepare("SELECT move, probatowin, probatonull, nb FROM openings WHERE fen LIKE ?");
			$moves->execute(array($chars[2]));
		} catch(PDOException $e) {
			exit('Error: '.$e->getMessage());
		}	
		header("Content-Type: application/json");
		//la boucle while est enlevée pour assurer la compatibilité avec Gson
		$arrayMoves = $moves->fetchAll();
		echo json_encode($arrayMoves);
		
    } elseif($chars[1]=='endings') {
		//selection selon le fen
		try {
			$moves = $bdd->prepare("SELECT move, probatowin, probatonull, nb FROM endings WHERE fen LIKE ?");
			$moves->execute(array($chars[2]));
		} catch(PDOException $e) {
			exit('Error: '.$e->getMessage());
		}
		header("Content-Type: application/json");
		//la boucle while est enlevée pour assurer la compatibilité avec Gson
		$arrayMoves = $moves->fetchAll();
		echo json_encode($arrayMoves);
		
    } else {
    	redirectionErreur404();
	}
        
} elseif(count($chars)==2 && $chars[1]=='version') {
	//envoie du numéro de version
	echo $version;
} else {
	redirectionErreur404();
}

//RewriteEngine On
//RewriteCond %{REQUEST_URI} ^/rest/.*$ [NC]
//RewriteRule ^(.*)$ /chessgame.php [L]