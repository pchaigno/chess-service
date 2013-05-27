package com.sun.jersey.ServerResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/rest/openings/{fenNotation}")
public class PrintFen {
	
	@GET
	@Produces("text/xml")
	public String printFen(@PathParam("fenNotation") String fen){
		//In real : query to DB with fen
		//Generate xml and return it
		return fen;
	}

}
