<?php

	$fen = $_GET['fen'];
	
	$curl = curl_init('http://www.shredderchess.com/online/playshredder/fetch.php?action=book&fen='.urlencode($fen).'&la=en&bookcode=p40');
	curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

	$result = curl_exec($curl);
	
	curl_close($curl);
	
	$lines = explode("\n", $result);
	$moves = array();
	foreach($lines as $line) {
		$infos = explode('#', $line);
		if(count($infos)==14) {
			$move = explode('...', $infos[0])[1];
			$percentage = $infos[7]/1000;
			$moves[] = array('move'=>$move, 'value'=>$infos[4], 'nb_play'=>$infos[6], 'percentage'=>$percentage, 'elo'=>$infos[8], 'performance'=>$infos[9], 'wins'=>$infos[11], 'draws'=>$infos[12], 'losses'=>$infos[13]);
		}
	}
	var_dump($moves);
	
	// FEN start : rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 1
	// FEN test 1 : rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq -
	// FEN test 2 : rnbqkb1r/pppppppp/5n2/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq -
	
?>