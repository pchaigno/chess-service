<?php

class ResourceWrapper {
	private $complete_fen; // False to remove the end of the FEN before submit.
	
	private $openings; // True to support opengings.
	private $open_version; // Version of the openings' code.
	private $open_url; // URL of the website for openings.
	private $open_parser; // The function to convert the result for openings requested to a JSON document.
	private $open_post; // True if the website need a POST request for openings.
	private $open_function; // The function for custom openings.
	
	private $endings; // True to support endings.
	private $end_version; // Version of the endings' code.
	private $end_url; // URL of the website for openings.
	private $end_parser; // The function to convert the result for endings requested to a JSON document.
	private $end_post; // True if the website need a POST request for endings.
	private $end_function; // The function for custom endings.
	
	private $middle; // True to support middle game.
	private $middle_version; // Version of the middle game's code.
	private $middle_function; // The function for the middle game (to call the chess engine).
	

	// Constructor.
	public function __construct($complete_fen = true) {
		$this->complete_fen = $complete_fen;
		$this->openings = false;
		$this->endings = false;
		$this->middle = false;
	}
	
	// Configure the openings part for typical database requests.
	public function setDatabaseOpenings($url, $version, $san, $parser = null) {
		$this->openings = true;
		$this->open_version = $version;
		if($san) {
			$this->open_version .= 's'; // SAN version.
		} else {
			$this->open_version .= 'l'; // LAN version.
		}
		$this->open_url = $url;
		$this->open_post = strstr($url, '$url_fen$')===false;
		$this->open_parser = $parser;
		$this->open_function = null;
	}
	
	// Configure the openings part with a custom function.
	public function setCustomOpenings($function, $version, $san) {
		$this->openings = true;
		$this->open_version = $version;
		if($san) {
			$this->open_version .= 's'; // SAN version.
		} else {
			$this->open_version .= 'l'; // LAN version.
		}
		$this->open_function = $function;
		$this->open_parser = null;
	}
	
	// Configure the endings part for typical database requests.
	public function setDatabaseEndings($url, $version, $san, $parser = null) {
		$this->endings = true;
		$this->end_version = $version;
		if($san) {
			$this->end_version .= 's'; // SAN version.
		} else {
			$this->end_version .= 'l'; // LAN version.
		}
		$this->end_url = $url;
		$this->end_post = strstr($url, '$url_fen$')===false;
		$this->end_parser = $parser;
		$this->end_function = null;
	}
	
	// Configure the openings part with a custom function.
	public function setCustomEndings($function, $version, $san) {
		$this->endings = true;
		$this->end_version = $version;
		if($san) {
			$this->end_version .= 's'; // SAN version.
		} else {
			$this->end_version .= 'l'; // LAN version.
		}
		$this->end_function = $function;
		$this->end_parser = null;
	}
	
	// Configure the middle game part with a custom function.
	public function setMiddleGame($function, $version, $san) {
		$this->middle = true;
		$this->middle_version = $version;
		if($san) {
			$this->middle_version .= 's'; // SAN version.
		} else {
			$this->middle_version .= 'l'; // LAN version.
		}
		$this->middle_function = $function;
	}
	
	// REST structure.
	public function rest() {
		// URL parser:
		$input = file_get_contents('php://input');
		$chars = preg_split('/\//', $_SERVER['REQUEST_URI'], -1, PREG_SPLIT_NO_EMPTY);

		if(count($chars)==3) {
			if($this->openings && $chars[1]=='openings') {
				if($chars[2]=='version') {
					// Return the version number.
					echo $this->open_version;
				} else {
					// Get the FEN:
					// $ are replaced by /.
					$fen = rawurldecode($chars[2]);
					$fen = str_replace('$', '/', $fen);
					
					if($this->open_function!=null) {
						$result = call_user_func($this->open_function, $fen);
					} else if($this->open_post) {
						$result = $this->curlPost($this->open_url, $fen);
					} else {
						$result = $this->curlGet($this->open_url, $fen);
					}
					
					// Display the result as a JSON document:
					header("Content-Type: application/json");
					if($this->open_parser==null) {
						echo $result;
					} else {
						echo call_user_func($this->open_parser, $result, $fen);
					}
				}
			} else if($this->endings && $chars[1]=='endings') {
				if($chars[2]=='version') {
					// Return the version number.
					echo $this->end_version;
				} else {
					// Get the FEN:
					// $ are replaced by /.
					$fen = rawurldecode($chars[2]);
					$fen = str_replace('$', '/', $fen);
					
					if($this->end_function!=null) {
						$result = call_user_func($this->end_function, $fen);
					} else if($this->end_post) {
						$result = $this->curlPost($this->end_url, $fen);
					} else {
						$result = $this->curlGet($this->end_url, $fen);
					}
					
					// Display the result as a JSON document:
					header("Content-Type: application/json");
					if($this->end_parser==null) {
						echo $result;
					} else {
						echo call_user_func($this->end_parser, $result, $fen);
					}
				}
			} else {
				redirectionErreur404();
			}
		} elseif(count($chars)==2) {
			if($chars[1]=='version') {
				// Return the version number.
				echo $this->middle_version;
			} else if($this->middle) {
				// Get the FEN:
				// $ are replaced by /.
				$fen = rawurldecode($chars[1]);
				$fen = str_replace('$', '/', $fen);
				
				$result = call_user_func($this->middle_function, $fen);
				
				// Display the result as a JSON document:
				header("Content-Type: application/json");
				echo $result;
			}
		} else {
			redirectionErreur404();
		}
	}

	// Make an HTTP GET request using cURL.
	private function curlGet($url, $fen) {
		if(!$this->complete_fen) {
			$fen = substr($fen, 0, strpos($fen, '-')+1);
		}
		$fen = rawurlencode($fen);
		$url = str_replace('$url_fen$', $fen, $url);
		$curlRequest = curl_init($url);
		curl_setopt($curlRequest, CURLOPT_RETURNTRANSFER, true);

		$result = curl_exec($curlRequest);
		curl_close($curlRequest);
		
		return $result;
	}
	
	// Make an HTTP POST request using cURL.
	private function curlPost($url, $fen) {
		if(!$this->complete_fen) {
			$fen = substr($fen, 0, strpos($fen, '-')+1);
		}
		$query = 'fen='.rawurlencode($fen);

		$curlRequest = curl_init($url);
		curl_setopt($curlRequest, CURLOPT_HTTPHEADER, array("Content-Type: application/x-www-form-urlencoded; charset=utf-8", "Content-length: ".strlen($query), "Connection: close"));
		curl_setopt($curlRequest, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($curlRequest, CURLOPT_POST, true);
		curl_setopt($curlRequest, CURLOPT_POSTFIELDS, $query);

		$result = curl_exec($curlRequest);
		curl_close($curlRequest);
		
		return $result;
	}

	// 404 error.
	private static function error404Redirection() {
		header('HTTP/1.0 404 Not Found');
		exit;
	}
}

?>