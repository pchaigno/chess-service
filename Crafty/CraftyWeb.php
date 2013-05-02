<?php
/* 3 steps :
 	1- Get the FEN notation passed through the URI and write it down in a copy of "Crafty.ini", named "input.txt" [mandatory]
 	2- The shell script "Crafty.bat" launches "CraftyCall.exe", which get the parameters from "input.txt" and send them to
 		crafty-22.0-win32.exe. This last one process the datas and write the best move in the file "game.001"
 	3- Coming back here, we read the file "game.001" and return the best move.
*/


// Variables
$originalpath = 'Crafty.ini';
$inputpath = 'input.txt';
$outputpath = 'game.001';


// Step 1 : Get FEN and create "input.txt" file 
// ----------------------------------------------
if (copy($originalpath, $inputpath)) {
	$inputfile = fopen($inputpath, "a"); // "a" = Start writing at the end of the file
		$fen = "";
		if(isset($_GET['fen'])) {
			$fen = htmlspecialchars($_GET["fen"]);
		} else {
			// Just for test.
			$fen = 'rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR';
		}
		fwrite($inputfile, "\nsetboard ".$fen." b \n");
		fwrite($inputfile, "go\n"); // *Important* Write \n at the end to launch search
	fclose($inputfile);

} else {
    echo "Failed to copy $file...\n";
}


// Step 2 : Crafty process the move
// ----------------------------------------------
echo("<pre>");
system('Crafty.bat'); // use system() to display all console output, use exec() to hide everything
echo("</pre>");

echo("<hr/>");

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
		$bestmove = $lastLine;
		echo($bestmove);
	fclose($outputfile);

	// Keep a backup and then delete output file after use, in order to avoid conflict with next moves
	copy($outputpath, $outputpath.".bak");
	unlink($outputpath);

} else {
	echo "Failed to get output file...\n";
}

?>