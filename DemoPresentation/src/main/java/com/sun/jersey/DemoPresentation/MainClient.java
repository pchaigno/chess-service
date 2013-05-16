package com.sun.jersey.DemoPresentation;

import java.util.List;


import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;


public class MainClient {
	public static void main(String[] args) {
		Client c = Client.create();
		
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR%20w%20KQkq";
		
		WebResource r = c.resource("http://127.0.0.1/" +
									"rest/openings/" + 
									fen);
		
		String response = r.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
		
		Parser parsXml = new Parser(response);
		List<Move> moves = parsXml.getMoves();
		
		System.out.println(moves);
	}
}
