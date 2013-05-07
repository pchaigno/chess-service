package core;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import parser.ChessParser;

/**
 * This class will handle the call from the client to the central server and will send the answer.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
@Path("/rest")
public class CentralServerResource {
	protected static final String NO_RESULT = "NULL";
	protected CentralServer server = new CentralServer();
	
	@Path("/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("fen")String fen) throws UnsupportedEncodingException {
		fen = fen.replaceAll("\\$", "/");
		if(!fen.endsWith("-")) {
			ChessParser parser = new ChessParser(fen);
			parser.checkEnPassant();
		}
		String move = this.server.getBestMove(fen, -1);
		if(move==null) {
			move = NO_RESULT;
		}
		return respond(move);
	}
	
	@Path("/{gameId: [0-9]+}")
	@DELETE
	public void endOfGame(@PathParam("gameId")int gameId) {
		//TODO get the real result (i set 0 for now)
		server.rewardResources(gameId, 0);
		GamesManager.removeGame(gameId);
	}
	
	@Path("/{gameId: [0-9]+}/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		if(GamesManager.exist(gameId)) {
			fen = fen.replaceAll("\\$", "/");
			
			if(!fen.endsWith("-")) {
				ChessParser parser = new ChessParser(fen);
				parser.checkEnPassant();
				fen = parser.getFEN(true);
			}
			String move = this.server.getBestMove(fen, gameId);
			GamesManager.updateGame(gameId, fen);
			if(move==null) {
				move = NO_RESULT;
			} else {
				Boolean san = GamesManager.isSAN(gameId);
				if(san!=null && !san) {
					ChessParser parser = new ChessParser(fen);
					move = parser.convertSANToLAN(move);
				}
			}
			
			return respond(move);
		}
		
		ResponseBuilder builder = Response.status(Status.NOT_FOUND);
		builder.header("Access-Control-Allow-Origin", "*");
		return builder.build();
	}
	
	@POST
	@Produces("text/plain")
	public Response startGame(@DefaultValue("true")@FormParam("san")boolean san) {
		int gameId = GamesManager.addNewGame(san);
		return respond(String.valueOf(gameId));
	}
	
	/**
	 * Build the response from a string.
	 * Allow us to put the right headers.
	 * @param response The string response.
	 * @return The response with headers.
	 */
	private static Response respond(String response) {
		ResponseBuilder builder = Response.ok(response);
		builder.header("Access-Control-Allow-Origin", "*");
		return builder.build();
	}
}