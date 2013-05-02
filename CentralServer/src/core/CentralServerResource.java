package core;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This class will handle the call from the client to the central server and will send the answer.
 */
// The stage is opening, middle or ending (may not be this path)
@Path("/resource/rest/{stage}/{fenNotation}")
public class CentralServerResource {
	protected static final String NO_RESULT = "NULL";
	//protected CentralServer server = new CentralServer();
	
	@GET
	@Produces("text/plain")
	public String getBestMove(@PathParam("stage")String stageInGame, @PathParam("fenNotation")String fen) throws UnsupportedEncodingException {
		/*String move = this.server.getBestMove(fen).getMove();
		if(move==null) {
			return NO_RESULT;
		} else {
			return move;
		}*/
		return "e4";
	}
}