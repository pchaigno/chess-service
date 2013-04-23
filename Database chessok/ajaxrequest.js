function ajaxRequest() {
	this.send =
	function(fen) {
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
		
		xmlreq.onreadystatechange = 
		function() {
			if(xmlreq.readyState == 4) {
				if(xmlreq.status == 200) {
					tree.respondLoadTree(xmlreq.responseXML);
				} else {
					alert("Unable to fetch Tree file");
				}
			}
		}
		// We are going to use timestamp in request parameters to avoid cache hits (bad for broadcasts)
		var date = new Date();
		
		var query = "fen=" + encodeURIComponent(fen);

		xmlreq.open("POST", "connection.php?timestamp=" + date.getTime(), true);
		xmlreq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		xmlreq.setRequestHeader("Content-length", query.length);
		xmlreq.setRequestHeader("Connection", "close");
		xmlreq.send(query);
	}
}