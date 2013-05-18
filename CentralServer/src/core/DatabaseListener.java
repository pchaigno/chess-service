package core;

import java.util.Map;
import java.util.Set;

/**
 * The listener interface for receiving events from the database.
 * A database event is generated when the file is changed or when data is recovered from it.
 * @author Paul Chaignon
 */
public interface DatabaseListener {
	
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
	 * @param resource The resources whose trust has been updated.
	 */
	public void onResourcesTrustUpdated(Resource resource);
	
	/**
	 * Called when the active parameters of resources have been updated.
	 * @param resource The resources whose active parameter has been updated.
	 */
	public void onResourcesActiveUpdated(Resource resource);
	
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
}