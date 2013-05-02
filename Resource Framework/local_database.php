<?php

require('resourcewrapper.class.php');

$wrapper = new ResourceWrapper();
$wrapper->setCustomOpenings('getOpenings', '1.0', true);
$wrapper->setCustomEndings('getEndings', '1.0', true);
$wrapper->rest();

// Open the connection to the local mysql database.
function connectToDatabase() {
	try {
		$db = new PDO('mysql:host=localhost;dbname=chessgames', 'root', '');
	} catch(PDOException $e) {
		exit('Error: '.$e->getMessage());
	}
	return $db;
}

// Get the openings moves according to the FEN.
function getOpenings($fen) {
	$db = connectToDatabase();
	
	try {
		$moves = $db->prepare("SELECT move, probatowin, probatonull, nb FROM openings WHERE fen LIKE ?");
		$moves->execute(array($fen));
	} catch(PDOException $e) {
		exit('Error: '.$e->getMessage());
	}
	$arrayMoves = $moves->fetchAll();
	return json_encode($arrayMoves);
}

// Get the endings moves according to the FEN.
function getEndings($fen) {
	$db = connectToDatabase();
	
	try {
		$moves = $db->prepare("SELECT move, probatowin, probatonull, nb FROM endings WHERE fen LIKE ?");
		$moves->execute(array($fen));
	} catch(PDOException $e) {
		exit('Error: '.$e->getMessage());
	}
	$arrayMoves = $moves->fetchAll();
	return json_encode($arrayMoves);
}

?>