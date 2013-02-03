package com.sun.jersey.ServerCreation.resourceExtractionNom;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/testUser/{username: [A-Z].*}")
public class PrintNameResource {
	
	@GET
	@Produces("text/plain")
	public String getUser(@PathParam("username") String userName){
		return userName;
	}
}
