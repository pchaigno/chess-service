<?php

require('resourcewrapper.class.php');

$wrapper = new ResourceWrapper();
$wrapper->setMiddleGame('getMiddle', '1.0', true);
$wrapper->rest();

// Variables
$originalpath = 'Crafty.ini';
$inputpath = 'input.txt';
$outputpath = 'game.001';

// Get the middle moves according to the FEN.
function getMiddle($fen) {
	
	$originalpath = 'Crafty.ini';
	$inputpath = 'input.txt';
	$outputpath = 'game.001';

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


	// Step 3 : Get best move from "game.001"
	// ----------------------------------------------
	// Note : the best move is the last line of the output file
	if(file_exists($outputpath)) {
		$outputfile = fopen($outputpath, "r");
			fseek($outputfile, -1, SEEK_END); // Place reading at end file
			$pos = ftell($outputfile);
			$lastLine = "";
			// Loop backword util a space is found.
			while(($C = fgetc($outputfile))!=' ' && $pos>0) {
			    $lastLine = $C.$lastLine;
			    fseek($outputfile, $pos--);
			}
			$lastLine = trim($lastLine);
			$bestmove = array ('move' => $lastLine);
			return json_encode($bestmove);
		fclose($outputfile);

		// Keep a backup and then delete output file after use, in order to avoid conflict with next moves
		copy($outputpath, $outputpath.".bak");
		unlink($outputpath);
	} else {
		echo "Failed to get output file...\n";
	}
	
}



?>