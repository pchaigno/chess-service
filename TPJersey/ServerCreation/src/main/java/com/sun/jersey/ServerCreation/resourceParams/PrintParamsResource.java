package com.sun.jersey.ServerCreation.resourceParams;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;


@Path("/test/{statut}")
public class PrintParamsResource {
	
	@GET
	@Produces("text/plain")
	public String printParams(@DefaultValue("clement") @QueryParam("nom") String nom,
							  @DefaultValue("20") @QueryParam("age") int age,
							  @PathParam("statut") String statut){
		return "Statut : " + statut +"\nJe m'appelle " + nom + " et j'ai " + age;
	}

}
