package core;

/**
 * The listener interface for receiving the events from the central server.
 * A central server event is generated when a request is received or when a response is sent.
 * @author Paul Chaignon
 */
public interface CentralServerListener {

	/**
	 * Called when a debug request has been received.
	 * @param fen The FEN received.
	 */
	public void onDebugRequest(String fen);
	
	/**
	 * Called when a Start Game request has been received.
	 * @param san
	 */
	public void onStartGameRequest(boolean san);
	
	/**
	 * Called when a End of Game request has been received.
	 * @param gameId The id of the game in question.
	 * @param fen The FEN received.
	 */
	public void onEndOfGameRequest(int gameId, String fen);
	
	/**
	 * Called when a Get Best Move request has been received.
	 * Concerns the Get Best Move requests within a game or without.
	 * @param gameId The id of the game in question.
	 * @param fen The FEN received.
	 */
	public void onGetBestMoveRequest(int gameId, String fen);
	
	/**
	 * Called when a response has been sent after a Get Best Move request.
	 * @param bestMove The best move sent.
	 */
	public void onBestMoveSent(String bestMove);
	
	/**
	 * Called when a response has been sent after a debug request.
	 * @param debug The debug information sent as an HTML document.
	 */
	public void onDebugInformationSent(String debug);
}