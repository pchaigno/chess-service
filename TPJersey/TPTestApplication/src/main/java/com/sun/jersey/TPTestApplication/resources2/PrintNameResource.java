package com.sun.jersey.TPTestApplication.resources2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/test/{username}")
public class PrintNameResource {
	
	@GET
	@Produces("text/plain")
	public String getUser(@PathParam("username") String userName){
		return userName;
	}
}
