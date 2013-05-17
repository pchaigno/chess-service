package core;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import parser.ChessParser;
import parser.IncorrectFENException;

/**
 * This class will handle the call from the client to the central server and will send the answer.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
@Path("/rest") // Shared path.
public class CentralServerResource {
	protected static final String NO_RESULT = "NULL";
	protected CentralServer server = new CentralServer();
	
	/**
	 * Receive a FEN from a client and return the best move corresponding after
	 * requesting all the resources.
	 * The client calls this method with an HTTP GET request on /rest/{fen}.
	 * @param fen The FEN send via the URL.
	 * @return The best move computed.
	 */
	@Path("/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("fen")String fen) {
		fen = fen.replaceAll("\\$", "/");
		if(!ChessParser.isCorrectFEN(fen)) {
			return respondBadRequest("FEN incorrect.");
		}
		
		if(!fen.endsWith("-")) {
			ChessParser parser;
			try {
				parser = new ChessParser(fen);
				parser.checkEnPassant();
			} catch (IncorrectFENException e) {
				// Shouldn't happen !
				System.err.println(e.getMessage());
				return respondBadRequest(e.getMessage());
			}
		}
		
		String move = this.server.getBestMove(fen, -1);
		if(move==null) {
			move = NO_RESULT;
		}
		return respondOK(move);
	}
	
	/**
	 * End a game and reward the resources depending on the result of the game.
	 * One the resources are rewarded, the game is deleted from the database.
	 * The client calls this method with an HTTP DELETE request on /rest/{gameId}.
	 * @param gameId The id of the game to end.
	 * @param fen The final FEN.
	 * @return Ok if the game is deleted from the database.
	 */
	@Path("/{gameId: [0-9]+}/{fen}")
	@DELETE
	public Response endOfGame(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		try {
			int reward;
			reward = ChessParser.result(fen, GamesManager.getColor(gameId));
			server.rewardResources(gameId, reward);
			GamesManager.removeGame(gameId);
			ResponseBuilder builder = Response.ok();
			builder.header("Access-Control-Allow-Origin", "*");
			return builder.build();
		} catch(IncorrectFENException ife) {
			System.err.println(ife.getMessage());
			return respondBadRequest(ife.getMessage());
		}
	}
	
	/**
	 * Receive a FEN from a client and return the best move corresponding after
	 * requesting all the resources.
	 * This method also keeps statistics about the moves suggested to reward the resources at the end of the game.
	 * The client calls this method with an HTTP GET request on /rest/{gameId}/{fen}.
	 * @param gameId The id of the game.
	 * @param fen The current FEN.
	 * @return The best move computed in the requested format (SAN or LAN).
	 */
	@Path("/{gameId: [0-9]+}/{fen}")
	@GET
	@Produces("text/plain")
	public Response getBestMove(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		if(!GamesManager.exist(gameId)) {
			return respondNotFound("Game id not found in the database.");
		}
		
		fen = fen.replaceAll("\\$", "/");
		
		if(!ChessParser.isCorrectFEN(fen)) {
			return respondBadRequest("FEN incorrect.");
		}
			
		if(!fen.endsWith("-")) {
			ChessParser parser;
			try {
				parser = new ChessParser(fen);
				parser.checkEnPassant();
				fen = parser.getFEN(true);
			} catch (IncorrectFENException e) {
				// Shouldn't happen !
				System.err.println(e.getMessage());
				return respondBadRequest(e.getMessage());
			}
		}
		
		String move = this.server.getBestMove(fen, gameId);
		GamesManager.updateGame(gameId, fen);
		
		if(move==null) {
			move = NO_RESULT;
		} else {
			Boolean san = GamesManager.isSAN(gameId);
			
			if(san!=null && !san) {
				ChessParser parser;
				try {
					parser = new ChessParser(fen);
					move = parser.convertSANToLAN(move);
				} catch (IncorrectFENException e) {
					// Shouldn't happen !
					System.err.println(e.getMessage());
					return respondBadRequest(e.getMessage());
				}	
			}
		}

		return respondOK(move);
	}
	
	/**
	 * Receive an FEN from a client and return all the information used in a normal computation.
	 * It will return all intermediate scores as an HTML document.
	 * The client calls this method with a HTTP GET request on /rest/debug/{fen}.
	 * @param fen The current FEN.
	 * @return All the information on the computation as an HTML document.
	 */
	@Path("/debug/{fen}")
	@GET
	@Produces("text/html")
	public Response debug(@PathParam("fen")String fen) {
		fen = fen.replaceAll("\\$", "/");
		
		if(!ChessParser.isCorrectFEN(fen)) {
			return respondBadRequest("FEN incorrect.");
		}
			
		if(!fen.endsWith("-")) {
			ChessParser parser;
			try {
				parser = new ChessParser(fen);
				parser.checkEnPassant();
				fen = parser.getFEN(true);
			} catch (IncorrectFENException e) {
				// Shouldn't happen !
				System.err.println(e.getMessage());
				return respondBadRequest(e.getMessage());
			}
		}
		
		String debug = this.server.getDebugInformation(fen);

		return respondOK(debug);
	}
	
	/**
	 * Create a new game with a random generated id.
	 * The client may indicate at this moment if he want that the server returns it SAN or LAN moves.
	 * The client calls this method with an HTTP POST request on /rest.
	 * @param san The boolean send in the request. If true, the move sent by the central server will be in SAN. Is true by default.
	 * @return The id of the game created.
	 */
	@POST
	@Produces("text/plain")
	public Response startGame(@DefaultValue("true")@FormParam("san")boolean san) {
		int gameId = GamesManager.addNewGame(san);
		return respondOK(String.valueOf(gameId));
	}
	
	/**
	 * This method is only called to respond to HTTP OPTIONS requests send by AJAX scripts.
	 * AJAX scripts send a preflight request to determine if the request to come is allowed by the server.
	 * @param requestH Headers to check.
	 * @return Headers to specify that the headers requested are allowed.
	 */
	@OPTIONS
	@Produces("text/plain")
	public Response startGame(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		ResponseBuilder builder = Response.ok();
		builder.header("Access-Control-Allow-Origin", "*");
		builder.header("Access-Control-Allow-Headers", requestH);
		return builder.build();
	}
	
	/**
	 * Build the response from a string.
	 * Put the right headers: notify the client that all origin are allowed (for AJAX use).
	 * @param response The string response.
	 * @return The response with headers.
	 */
	private static Response respondOK(String response) {
		ResponseBuilder builder = Response.ok(response);
		builder.header("Access-Control-Allow-Origin", "*");
		return builder.build();
	}
	
	/**
	 * Build a 400 Bad request response.
	 * Put the right headers: notify the client that all origin are allowed (for AJAX use).
	 * @return The 400 response with headers.
	 */
	private static Response respondBadRequest(String message) {
		ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
		builder.header("Access-Control-Allow-Origin", "*");
		builder.entity(message);
		return builder.build();
	}
	
	/**
	 * Build a 404 Not Found response.
	 * Put the right headers: notify the client that all origin are allowed (for AJAX use).
	 * @return The 404 response with headers.
	 */
	private static Response respondNotFound(String message) {
		ResponseBuilder builder = Response.status(Status.NOT_FOUND);
		builder.header("Access-Control-Allow-Origin", "*");
		builder.entity(message);
		return builder.build();
	}
}