package core;

import javax.swing.event.EventListenerList;
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
import parser.IncorrectAlgebraicNotationException;
import parser.IncorrectFENException;

/**
 * This class will handle calls from the client to the central server and will send the answers.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
@Path("/rest") // Shared path.
public class CentralServerResource {
	private static final String NO_RESULT = "NULL";
	private CentralServer server = new CentralServer();
	
	/**
	 * The list of event listener for the central server.
	 * EventListenerList is used for a better multithread safety.
	 */
	private static final EventListenerList listeners = new EventListenerList();

	/**
	 * Add a central server listener to the listeners.
	 * @param listener The new listener.
	 */
	public static void addCentralServerListener(CentralServerListener listener) {
		listeners.add(CentralServerListener.class, listener);
	}
	
	/**
	 * Remove a central server listener from the listeners.
	 * @param listener The new listener.
	 */
	public static void removeCentralServerListener(CentralServerListener listener) {
		listeners.remove(CentralServerListener.class, listener);
	}
	
	/**
	 * @return The central server listeners.
	 */
	public static CentralServerListener[] getCentralServerListeners() {
		return listeners.getListeners(CentralServerListener.class);
	}
	
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
		
		// Notify the central server listeners about the request.
		fireGetBestMoveRequest(-1, fen);
		
		if(!ChessParser.isCorrectFEN(fen)) {
			return respondBadRequest("FEN incorrect.");
		}
		
		if(!fen.endsWith("-")) {
			try {
				ChessParser parser = new ChessParser(fen);
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
		
		// Notify the central server listeners about the response sent.
		fireBestMoveSent(move);
		
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
	public static Response endOfGame(@PathParam("gameId")int gameId, @PathParam("fen")String fen) {
		fen = fen.replaceAll("\\$", "/");
		
		// Notify the central server listeners about the request.
		fireEndOfGameRequest(gameId, fen);
		
		if(!GamesManager.exist(gameId)) {
			return respondNotFound("Game id not found in the database.");
		}
		
		if(!ChessParser.isCorrectFEN(fen)) {
			return respondBadRequest("FEN incorrect.");
		}
		
		int reward;
		try {
			ChessParser parser = new ChessParser(fen);
			reward = parser.result(GamesManager.getColor(gameId));
		} catch(IncorrectFENException e) {
			// Shouldn't happen !
			System.err.println(e.getMessage());
			return respondBadRequest(e.getMessage());
		}
		
		CentralServer.rewardResources(gameId, reward);
		GamesManager.removeGame(gameId);
		
		return respondOK("");
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
		fen = fen.replaceAll("\\$", "/");
		
		// Notify the central server listeners about the request.
		fireGetBestMoveRequest(gameId, fen);
		
		if(!GamesManager.exist(gameId)) {
			return respondNotFound("Game id not found in the database.");
		}
		
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
				} catch(IncorrectFENException e) {
					System.err.println(e.getMessage());
					return respondBadRequest(e.getMessage());
				} catch(IncorrectAlgebraicNotationException e) {
					System.err.println(e.getMessage());
					return respondBadRequest(e.getMessage());
				}
			}
		}
		
		// Notify the central server listeners about the response sent.
		fireBestMoveSent(move);

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
		
		// Notify the central server listeners about the request.
		fireDebugRequest(fen);
		
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
		
		// Notify the central server listeners about the debug information sent.
		fireDebugInformationSent(debug);

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
	public static Response startGame(@DefaultValue("true")@FormParam("san")boolean san) {
		// Notify the central server listeners about the request.
		fireStartGameRequest(san);
		
		int gameId = GamesManager.addNewGame(san);
		
		// Notify the central server listeners about the game id sent.
		fireGameIdSent(gameId);
		
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
	public static Response preflightStartGame(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		ResponseBuilder builder = Response.ok();
		builder.header("Access-Control-Allow-Origin", "*");
		builder.header("Access-Control-Allow-Headers", requestH);
		return builder.build();
	}
	
	/**
	 * This method is only called to respond to HTTP OPTIONS requests send by AJAX scripts.
	 * AJAX scripts send a preflight request to determine if the request to come is allowed by the server.
	 * @param requestH Headers to check.
	 * @return Headers to specify that the headers requested are allowed.
	 */
	@Path("/{gameId: [0-9]+}/{fen}")
	@OPTIONS
	@Produces("text/plain")
	public static Response preflightEndOfGame(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		ResponseBuilder builder = Response.ok();
		builder.header("Access-Control-Allow-Origin", "*");
		builder.header("Access-Control-Allow-Methods", "DELETE, GET, OPTIONS");
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
	
	/**
	 * Fire the debug request event for all central server listeners.
	 * @param fen The FEN received.
	 */
	private static void fireDebugRequest(String fen) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onDebugRequest(fen);
		}
	}
	
	/**
	 * Fire a Start Game request event for all central server listeners.
	 * @param san True if the client want to receive SAN moves.
	 */
	private static void fireStartGameRequest(boolean san) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onStartGameRequest(san);
		}
	}
	
	/**
	 * Fire a End Of Game request event for all central server listeners.
	 * @param gameId The id of the game in question.
	 * @param fen The FEN received.
	 */
	private static void fireEndOfGameRequest(int gameId, String fen) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onEndOfGameRequest(gameId, fen);
		}
	}
	
	/**
	 * Fire a Get Best Move request event for all central server listeners.
	 * @param gameId The id of the game in question.
	 * @param fen The FEN received.
	 */
	private static void fireGetBestMoveRequest(int gameId, String fen) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onGetBestMoveRequest(gameId, fen);
		}
	}
	
	/**
	 * Fire a Best Move sent event for all central server listeners.
	 * @param bestMove The best move sent.
	 */
	private static void fireBestMoveSent(String bestMove) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onBestMoveSent(bestMove);
		}
	}
	
	/**
	 * Fire a debug information sent event for all central server listeners.
	 * @param debug The debug information sent as an HTML document.
	 */
	private static void fireDebugInformationSent(String debug) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onDebugInformationSent(debug);
		}
	}
	
	/**
	 * Fire a game id sent event for all central server listeners.
	 * @param gameId The game id sent.
	 */
	private static void fireGameIdSent(int gameId) {
		for(CentralServerListener listener: getCentralServerListeners()) {
			listener.onGameIdSent(gameId);
		}
	}
}