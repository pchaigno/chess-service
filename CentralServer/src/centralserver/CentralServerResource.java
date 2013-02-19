package centralserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


/**
 * This class will handle the call from the client to the central server and will send the answer
 * @author clemgaut
 *
 */

//The stage is opening, middle or ending (may not be this path)
@Path("/rest/{stage}/{fenNotation}")

public class CentralServerResource {
	
	protected CentralServer server;
	
	@GET
	@Produces("text/plain")
	public String getBestMove(@PathParam("stage") String stageInGame,
							  @PathParam("fenNotation") String fen){
		//TODO call the central server to get the best move and valid parameters (stage and fen)
		return "e4";
	}

}
