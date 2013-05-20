package core;

import java.sql.SQLException;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

/**
 * The listener interface for receiving events from the database.
 * A database event is generated when the file is changed or when data is recovered from it.
 * @author Paul Chaignon
 */
public interface DatabaseListener extends EventListener {
	
	/**
	 * Called when resources have been recovered from the database.
	 * @param active True if only the active resources have been recovered.
	 * @param resources The resources recovered.
	 */
	public void onResourcesRecovery(boolean active, Set<Resource> resources);

	/**
	 * Called when a resource has been added to the database.
	 * @param resource The resource added.
	 */
	public void onResourceAdded(Resource resource);
	
	/**
	 * Called when a resource has been removed from the database.
	 * @param resource The resource removed.
	 */
	public void onResourceRemoved(Resource resource);
	
	/**
	 * Called when resources have been removed from the database.
	 * @param resources The resources removed.
	 */
	public void onResourcesRemoved(Set<Resource> resources);
	
	/**
	 * Called when a resource has been updated in the database.
	 * @param resource The resource updated.
	 */
	public void onResourceUpdated(Resource resource);
	
	/**
	 * Called when the trust in resources has been updated.
	 * @param resourceInvolvements Involvement in the game for each resources updated.
	 * @param gameResult The result of the game.
	 */
	public void onResourcesTrustUpdated(Map<Integer, Double> resourceInvolvements, int gameResult);
	
	/**
	 * Called when the active parameters of resources have been updated.
	 * @param resources The resources whose active parameters have been updated.
	 */
	public void onResourcesActiveUpdated(Set<Resource> resources);
	
	/**
	 * Called when a game has been removed from the database along with all its moves.
	 * @param gameId The game id.
	 */
	public void onGameRemoved(int gameId);
	
	/**
	 * Called when a game has been added to the database.
	 * @param gameId The game id generated.
	 */
	public void onGameAdded(int gameId);
	
	/**
	 * Called when a game has been updated in the database.
	 * @param gameId The game id.
	 * @param fen The new last FEN requested.
	 */
	public void onGameUpdated(int gameId, String fen);
	
	/**
	 * Called when a move has been added to the database.
	 * @param gameId The id of the game that the move belong.
	 * @param resourcesConfidence The confidence of the resources in the move.
	 * @param moveNumber The move number in the game.
	 */
	public void onMoveAdded(int gameId, Map<Integer, Double> resourcesConfidence, int moveNumber);
	
	/**
	 * Called when resource involvements have been recovered from the database.
	 * @param gameId The game id.
	 * @param resourceInvolvements The resource involvements as a map.
	 */
	public void onResourceInvolvementsRecovery(int gameId, Map<Integer, Double> resourceInvolvements);
	
	/**
	 * Called when an exception is raised while accessing the database.
	 * Regroups the creation, connection and query cases.
	 * @param e The exception raised.
	 */
	public void onDatabaseError(Exception e);
	
	/**
	 * Called when an exception is raised while creating the database.
	 * @param e The exception raised.
	 */
	public void onCreateDatabaseError(Exception e);
	
	/**
	 * Called when an exception is raised while connecting to the database.
	 * @param e The exception raised.
	 */
	public void onConnectionError(Exception e);
	
	/**
	 * Called when an SQL exception is raised while querying the database.
	 * @param e The SQL exception raised.
	 */
	public void onQueryError(SQLException e);
}