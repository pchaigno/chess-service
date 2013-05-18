package core;

/**
 * An abstract adapter class for receiving central server events.
 * The methods in this class are empty.
 * This class exists as convenience for creating listener objects.
 * @author Paul Chaignon
 */
public class CentralServerAdapter implements CentralServerListener {

	@Override
	public void onDebugRequest(String fen) {}

	@Override
	public void onStartGameRequest(boolean san) {}

	@Override
	public void onEndOfGameRequest(int gameId, String fen) {}

	@Override
	public void onGetBestMoveRequest(int gameId, String fen) {}

	@Override
	public void onBestMoveSent(String bestMove) {}

	@Override
	public void onDebugInformationSent(String debug) {}

	@Override
	public void onGameIdSent(int gameId) {}
}