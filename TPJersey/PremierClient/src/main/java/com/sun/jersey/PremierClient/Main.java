package com.sun.jersey.PremierClient;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = Client.create();
		
		WebResource r = c.resource("http://127.0.0.1/rest/openings/rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR%20w%20KQkq");
		
		String reponse = r.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
		
		Parser parsXml = new Parser(reponse);
		List<Coup> coups = parsXml.getCoups();
		
		System.out.println(coups);
	}
}
