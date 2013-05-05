package core;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This class will handle the call from the client to the central server and will send the answer.
 */
public class CentralServerResource {
	protected static final String NO_RESULT = "NULL";
	protected CentralServer server = new CentralServer();
	
	@Path("/resource/rest/{fen}")
	@GET
	@Produces("text/plain")
	public String getBestMove(@PathParam("fen")String fen) throws UnsupportedEncodingException {
		String move = this.server.getBestMove(fen, -1);
		if(move==null) {
			return NO_RESULT;
		} else {
			return move;
		}
	}
	
	@Path("/resource/rest/{gameId: [0-9]+}")
	@DELETE
	public void endOfGame(@PathParam("gameId")int gameId) {
		GamesManager.removeGame(gameId);
		// TODO Reward resources.
	}
	
	@Path("/resource/rest/{gameId: [0-9]+}/{fen}")
	@GET
	@Produces("text/plain")
	public String getBestMove(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		String move = this.server.getBestMove(fen, gameId);
		GamesManager.updateGame(gameId, fen);
		if(move==null) {
			return NO_RESULT;
		} else {
			return move;
		}
	}
	
	@Path("/resource/rest/")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String startGame(@DefaultValue("true")@FormParam("san")boolean san) {
		// TODO Add a parameter san to the games.
		int gameId = GamesManager.addNewGame();
		return String.valueOf(gameId);
	}
}