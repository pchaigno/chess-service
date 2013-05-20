package core;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * An abstract adapter class for receiving database events.
 * The methods in this class are empty.
 * This class exists as convenience for creating listener objects.
 * @author Paul Chaignon
 */
public class DatabaseAdapter implements DatabaseListener {

	@Override
	public void onResourcesRecovery(boolean active, Set<Resource> resources) {}

	@Override
	public void onResourceAdded(Resource resource) {}

	@Override
	public void onResourceRemoved(Resource resource) {}

	@Override
	public void onResourcesRemoved(Set<Resource> resources) {}

	@Override
	public void onResourceUpdated(Resource resource) {}

	@Override
	public void onResourcesTrustUpdated(Map<Integer, Double> resourceInvolvements, int gameResult) {}

	@Override
	public void onResourcesActiveUpdated(Set<Resource> resources) {}

	@Override
	public void onGameRemoved(int gameId) {}

	@Override
	public void onGameAdded(int gameId) {}

	@Override
	public void onGameUpdated(int gameId, String fen) {}

	@Override
	public void onMoveAdded(int gameId, Map<Integer, Double> resourcesConfidence, int moveNumber) {}

	@Override
	public void onResourceInvolvementsRecovery(int gameId, Map<Integer, Double> resourceInvolvements) {}

	@Override
	public void onDatabaseError(Exception e) {}

	@Override
	public void onCreateDatabaseError(Exception e) {}

	@Override
	public void onConnectionError(Exception e) {}

	@Override
	public void onQueryError(SQLException e) {}
}