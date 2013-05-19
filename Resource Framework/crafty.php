<?php

require('resourcewrapper.class.php');

$wrapper = new ResourceWrapper();
$wrapper->setMiddleGame('getMiddle', '1.0', true, true);
$wrapper->rest();

// Variables
$originalpath = 'Crafty.ini';
$inputpath = 'input.txt';
$outputpath = 'log.001';

// Get the middle moves according to the FEN.
function getMiddle($fen) {
	
	$originalpath = 'Crafty.ini';
	$inputpath = 'input.txt';
	$outputpath = 'log.001';

	// Step 1 : Create "input.txt" file 
	// ----------------------------------------------
	if (copy($originalpath, $inputpath)) {
		$inputfile = fopen($inputpath, "a"); // "a" = Start writing at the end of the file
			fwrite($inputfile, "\nsetboard ".$fen." b \n");
			fwrite($inputfile, "go\n"); // *Important* Write \n at the end to launch search
		fclose($inputfile);
	} else {
		echo "Failed to copy $file...\n";
	}


	// Step 2 : Crafty process the move
	// ----------------------------------------------
	exec('Crafty.bat'); // use system() to display all console output, use exec() to hide everything


	// Step 3 : Get moves, depths and scores from "log.001"
	// ----------------------------------------------
	if(file_exists($outputpath)) {
		$outputfile = fopen($outputpath, "r");
		$moves = array();
		
		while($lines[] = fgets($outputfile));
		$lines = preg_grep('#^\s*(\d+)\s*(\d+\.\d{2})\s*(-?(\d+\.\d{2}|Mate))\s*1\.\s\.\.\.\s(.)+\s#', $lines); //lines /depth time score variation/

		foreach($lines as $line){
			preg_match('#1\.\s\.\.\.\s*(.*?)\s#', $line, $match); // move
			$move = $match[1];
			$moves[$move]['move'] = $move;
			preg_match('#\d+\.\d{2}.*?(-?\d+\.\d{2})#', $line, $match); // score
			$moves[$move]['score'] = $match[1];
			preg_match('#\d+\.\d{2}.*?(-?)Mate#', $line, $match); // score when mate
			$moves[$move]['score'] = $match[1] + "1000";
			preg_match('#\s(\d+)\s#', $line, $match); // depth
			$moves[$move]['depth'] = $match[1];
		}

		fclose($outputfile);

		// Keep a backup and then delete output file after use, in order to avoid conflict with next moves
		copy($outputpath, $outputpath.".bak");
		unlink($outputpath);

		return json_encode(array_values($moves));
	} else {
		echo "Failed to get output file...\n";
	}
	
}



?>