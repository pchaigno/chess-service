<?php

$input = file_get_contents('php://input');
$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], -1, PREG_SPLIT_NO_EMPTY);

if(count($chars)==2 && $chars[0]=='chess') {
	$file = @file_get_contents('../moves.txt');
	if($file===false) {
		header("HTTP/1.1 500 Internal Server Error");
		header("Content-Type: text/html; charset=utf-8");
	} else {
		$moves = explode("\n", $file);
		$nb_moves = count($moves);
		for($i=0 ; $i<$nb_moves ; $i++) {
			$move = explode(':', $moves[$i]);
			if($move[0]==$chars[1]) {
				header("Content-Type: text/html; charset=utf-8");
				exit($move[1]);
			}
		}
		header("HTTP/1.1 400 Bad Request");
		header("Content-Type: text/html; charset=utf-8");
	}
} else {
	header("HTTP/1.1 404 Not Found");
	header("Content-Type: text/html; charset=utf-8");
}

// RewriteEngine On
// RewriteCond %{REQUEST_URI} ^/rest/openings/.*$ [NC]
// RewriteRule ^(.*)$ /index.php [L]

?>