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

import parser.ChessBoard;
import parser.ChessParser;

/**
 * This class will handle the call from the client to the central server and will send the answer.
 */
@Path("/resource")
public class CentralServerResource {
	protected static final String NO_RESULT = "NULL";
	protected CentralServer server = new CentralServer();
	
	@Path("/rest/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("fen")String fen) throws UnsupportedEncodingException {
		fen = fen.replaceAll("\\$", "/");
		if(!fen.endsWith("-")){
			ChessBoard board = new ChessBoard();
			board.loadFEN(fen);
			System.out.println(board.currentFEN(true));
			
		}
		String move = null;// this.server.getBestMove(fen, -1);
		if(move==null) {
			move = NO_RESULT;
		}
		return respond(move);
	}
	
	@Path("/rest/{gameId: [0-9]+}")
	@DELETE
	public void endOfGame(@PathParam("gameId")int gameId) {
		//TODO get the real result (i set 0 for now)
		server.rewardResources(gameId, 0);
		GamesManager.removeGame(gameId);
	}
	
	@Path("/rest/{gameId: [0-9]+}/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		// TODO Check that this game id exists.
		
		fen = fen.replaceAll("\\$", "/");
		
		if(!fen.endsWith("-")){
			ChessParser parser = new ChessParser(fen);
			parser.verifyEnPassant();
			fen = parser.getFen(true);
		}
		String move = this.server.getBestMove(fen, gameId);
		GamesManager.updateGame(gameId, fen);
		if(move==null) {
			move = NO_RESULT;
		}
		return respond(move);
	}
	
	@Path("/rest")
	@POST
	@Produces("text/plain")
	public Response startGame(@DefaultValue("true")@FormParam("san")boolean san) {
		// TODO Add a parameter san to the games.
		int gameId = GamesManager.addNewGame();
		return respond(String.valueOf(gameId));
	}
	
	private static Response respond(String response) {
		ResponseBuilder builder = Response.ok(response);
		builder.header("Access-Control-Allow-Origin", "*");
		return builder.build();
	}
}