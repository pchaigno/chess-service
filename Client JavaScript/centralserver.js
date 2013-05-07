/** Game's ID **/
var game_id = -1;

/** Central server's URI **/
var central_server_uri = "http://127.0.0.1:9998/rest";

/** Get the next move **/
function getNextMove(p4d, fen) {
	if(game_id==-1) {
		getFirstMoveFromCentralServer(p4d, fen);
	} else {
		getMoveFromCentralServer(p4d, fen);
	}
}

/** Create an XML HTTP request object **/
function createXMLHttpRequest() {
	if(window.XMLHttpRequest){
		xmlreq=new XMLHttpRequest();
		if(xmlreq.overrideMimeType){
			xmlreq.overrideMimeType('text/xml');
		}
	} else if(window.ActiveXObject){
		try{
			xmlreq=new ActiveXObject("Msxml2.XMLHTTP");
		} catch(e) {
			try{
				xmlreq=new ActiveXObject("Microsoft.XMLHTTP");
			} catch(e){
			}
		}
	}
	return xmlreq;
}

/** Ask for the central server to create the game and get the first move **/
function getFirstMoveFromCentralServer(p4d, fen) {
	xmlreq = createXMLHttpRequest();
	
	xmlreq.onreadystatechange = function() {
		if(xmlreq.readyState == 4) {
			if(xmlreq.status == 200) {
				game_id = xmlreq.responseText;
				getMoveFromCentralServer(p4d, fen);
			} else {
				alert('Error create: '+xmlreq.status);
			}
		}
	}

	var query = "san=false";
	xmlreq.open("POST", central_server_uri+"/", true);
	xmlreq.setRequestHeader("Content-length", query.length);
	xmlreq.send(query);
}

/** Call the central server to get the next move **/
function getMoveFromCentralServer(p4d, fen) {
	xmlreq = createXMLHttpRequest();
	
	xmlreq.onreadystatechange = function() {
		if(xmlreq.readyState == 4) {
			if(xmlreq.status == 200) {
				move = convertToDigits(xmlreq.responseText);
				p4d.move(move[0], move[1]);
			} else {
				alert('Error get: '+xmlreq.status);
			}
		}
	}

	fen = fen.replace(/\//g, '$').replace(' w ', ' b ');
	xmlreq.open("GET", central_server_uri+"/"+game_id+"/"+fen, true);
	xmlreq.send();
}

/** Convert the LAN to a 4 digits number for p4d **/
function convertToDigits(lan) {
	var move = new Array();
	if(lan=='O-O') {
		move[0] = 95;
		move[1] = 97;
		return move;
	}
	if(lan=='O-O-O') {
		move[0] = 95;
		move[1] = 93;
		return move;
	}

	var letters = {'a':1, 'b':2, 'c':3, 'd':4, 'e':5, 'f':6, 'g':7, 'h':8};
	move[0] = (parseInt(lan[1])+1)+''+letters[lan[0]];
	move[1] = (parseInt(lan[3])+1)+''+letters[lan[2]];
	return move;
}