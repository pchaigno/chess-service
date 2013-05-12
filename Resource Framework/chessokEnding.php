<?php

require('resourcewrapper.class.php');

$wrapper = new ResourceWrapper(true);
$wrapper->setDatabaseEndings('http://chessok.com/onlineserv/endbase/connection.php?timestamp='.time(), '1.0', true, 'parserEndingsToJson');
$wrapper->rest();

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