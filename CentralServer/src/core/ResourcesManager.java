package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handle all the accesses to the SQLite database for the resources.
 * Contains static methods to delete, add and retrieve resources from the database.
 * Fire events to the database listeners.
 * Extends DatabaseManager that contains the database listeners and the connection methods.
 * @author Paul Chaignon
 */
public class ResourcesManager extends DatabaseManager {
	private static final String RESOURCES = "resources";
	private static final String RESOURCE_ID = "id";
	private static final String RESOURCE_URI = "uri";
	private static final String RESOURCE_NAME = "name";
	private static final String RESOURCE_TRUST = "trust";
	private static final String RESOURCE_TYPE = "type";
	private static final String RESOURCE_ACTIVE = "active";
	
	/**
	 * Get the resources from the database.
	 * It's possible to select only the active resources.
	 * Fire a resource recovery event.
	 * @param active If true then this method will only return the active resources.
	 * @return All resources from the database.
	 */
	@SuppressWarnings("resource")
	public static Set<Resource> getResources(boolean active) {
		Set<Resource> resources = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "SELECT * FROM "+RESOURCES;
		if(active) {
			query += " WHERE active = 1";
		}
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				Resource resource;
				if(results.getInt(RESOURCE_TYPE)==Resource.OPENINGS_DATABASE) {
					resource = new OpeningsDatabase(results.getString(RESOURCE_URI), results.getString(RESOURCE_NAME), results.getInt(RESOURCE_TRUST), results.getBoolean(RESOURCE_ACTIVE), results.getInt(RESOURCE_ID));
				} else if(results.getInt(RESOURCE_TYPE)==Resource.ENDINGS_DATABASE) {
					resource = new EndingsDatabase(results.getString(RESOURCE_URI), results.getString(RESOURCE_NAME), results.getInt(RESOURCE_TRUST), results.getBoolean(RESOURCE_ACTIVE), results.getInt(RESOURCE_ID));
				} else {
					resource = new Bot(results.getString(RESOURCE_URI), results.getString(RESOURCE_NAME), results.getInt(RESOURCE_TRUST), results.getBoolean(RESOURCE_ACTIVE), results.getInt(RESOURCE_ID));
				}
				resource.setId(results.getInt(RESOURCE_ID));
				resources.add(resource);
			}
			statement.close();
			results.close();
			
			// Notify the database listeners about the operation.
			fireResourcesRecovery(active, resources);
			
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("getResources: "+e.getMessage());
			fireQueryError(e);
		}
		
		return resources;
	}
	
	/**
	 * Add a resource to the database and set the resource id with the one used in the SQL table.
	 * Fire a resource added event.
	 * @param resource The resource to add.
	 * @return The resource added with the id set or null if an error occurred.
	 */
	@SuppressWarnings("resource")
	public static Resource addResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "INSERT INTO "+RESOURCES+"("+RESOURCE_TYPE+", "+RESOURCE_NAME+", "+RESOURCE_URI+", "+RESOURCE_TRUST+", "+RESOURCE_ACTIVE+") VALUES(?, ?, ?, ?, 1)";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setQueryTimeout(20);
			int type = Resource.BOT;
			if(resource.getClass()==OpeningsDatabase.class) {
				type = Resource.OPENINGS_DATABASE;
			} else if(resource.getClass()==EndingsDatabase.class) {
				type = Resource.ENDINGS_DATABASE;
			}
			statement.setInt(1, type);
			statement.setString(2, resource.getName());
			statement.setString(3, resource.getURI());
			statement.setInt(4, resource.getTrust());
			if(statement.executeUpdate()!=1) {
				statement.close();
				dbConnect.close();
				return null;
			}
			statement.close();
			String queryLastId = "SELECT last_insert_rowid() AS last_id";
			statement = dbConnect.prepareStatement(queryLastId);
			ResultSet res = statement.executeQuery();
			if(res.next()) {
				resource.setId(res.getInt("last_id"));
				res.close();
				statement.close();
				
				// Notify the database listeners about the operation.
				fireResourceAdded(resource);
				
				return resource;
			}
			res.close();
			statement.close();
		} catch(SQLException e) {
			System.err.println("addResource: "+e.getMessage());
			fireQueryError(e);
		}
		return null;
	}
	
	/**
	 * Remove a resource from the database.
	 * As always, the resource is identified by its id.
	 * Fire a resource removed event.
	 * @param resource The resource to remove.
	 * @return True if the operation succeed, false otherwise.
	 */
	@SuppressWarnings("resource")
	public static boolean removeResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "DELETE FROM "+RESOURCES+" WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, resource.getId());
			if(statement.executeUpdate()!=1) {
				statement.close();
				dbConnect.close();
				return false;
			}
			statement.close();
			dbConnect.close();
			
			// Notify the database listeners that the resource has been removed.
			fireResourceRemoved(resource);
			
			return true;
		} catch(SQLException e) {
			System.err.println("removeResource: "+e.getMessage());
			fireQueryError(e);
		}
		return false;
	}
	
	/**
	 * Remove resources from the database.
	 * As always, the resources are identified by their id.
	 * Fire a resources removed event.
	 * @param resources The resources to remove.
	 * @return The resources that weren't removed.
	 */
	@SuppressWarnings("resource")
	public static Set<Resource> removeResources(Set<Resource> resources) {
		List<Resource> resourcesToRemove = new ArrayList<Resource>();
		resourcesToRemove.addAll(resources);
		Set<Resource> notRemoved = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "DELETE FROM "+RESOURCES+" WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(Resource resource: resourcesToRemove) {
				statement.setInt(1, resource.getId());
				statement.addBatch();
			}
			int[] results = statement.executeBatch();
			statement.close();
			notRemoved = computeResourceResults(resourcesToRemove, results);
			
			// Notify the database listeners about the operation:
			resources.removeAll(notRemoved);
			fireResourcesRemoved(resources);
			
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("removeResources: "+e.getMessage());
			fireQueryError(e);
		}
		return notRemoved;
	}
	
	/**
	 * Update a resource in the database.
	 * All fields except the URI are updated.
	 * Fire a resource updated event.
	 * @param resource The resource to update.
	 * @return True if the update succeed, false otherwise.
	 */
	@SuppressWarnings("resource")
	public static boolean updateResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_URI+" = ?, "+RESOURCE_NAME+" = ?, "+RESOURCE_TRUST+" = ?, "+RESOURCE_TYPE+" = ?, "+RESOURCE_ACTIVE+" = ? WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setString(1, resource.getURI());
			statement.setString(2, resource.getName());
			statement.setInt(3, resource.getTrust());
			int type = Resource.BOT;
			if(resource.getClass()==OpeningsDatabase.class) {
				type = Resource.OPENINGS_DATABASE;
			} else if(resource.getClass()==EndingsDatabase.class) {
				type = Resource.ENDINGS_DATABASE;
			}
			statement.setInt(4, type);
			statement.setBoolean(5, resource.isActive());
			statement.setInt(6, resource.getId());
			if(statement.executeUpdate()!=1) {
				statement.close();
				dbConnect.close();
				System.out.println("ResourcesManager.updateResource()");
				return false;
			}
			statement.close();
			dbConnect.close();
			
			// Notify the database listeners about the operation.
			fireResourceUpdated(resource);
			
			return true;
		} catch(SQLException e) {
			System.err.println("updateResource: "+e.getMessage());
			fireQueryError(e);
		}
		return false;
	}
	
	/**
	 * Update the trust parameter of resources.
	 * The trust is increased with the resource involvement value if the game was won, decreased if the game was lost.
	 * Fire a resources trust updated event.
	 * @param resourceInvolvements A map with the id of the resources as key and the value that need to be add to the trust of the resource.
	 * @param gameResult The result of the game: -1 for lose, 1 for win, 0 for draw.
	 * @return The id of the resources that weren't updated.
	 */
	@SuppressWarnings("resource")
	public static Set<Integer> updateResourcesTrust(Map<Integer, Double> resourceInvolvements, int gameResult) {
		Set<Integer> notUpdated = new HashSet<Integer>();
		List<Integer> resourcesToUpdate = new ArrayList<Integer>();
		resourcesToUpdate.addAll(resourceInvolvements.keySet());
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_TRUST+" = "+RESOURCE_TRUST+" + ? WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(int resourceId: resourcesToUpdate) {
				double reward = gameResult*resourceInvolvements.get(resourceId);
				statement.setInt(1, (int)reward);
				statement.setInt(2, resourceId);
				statement.addBatch();
			}
			int[] results = statement.executeBatch();
			statement.close();
			dbConnect.close();
			
			notUpdated = computeIdResults(resourcesToUpdate, results);
			
			// Notify the database listeners about the operation:
			for(int resource: notUpdated) {
				resourceInvolvements.remove(resource);
			}
			fireResourcesTrustUpdated(resourceInvolvements, gameResult);
		} catch(SQLException e) {
			System.err.println("updateResourcesTrust: "+e.getMessage());
			fireQueryError(e);
		}
		return notUpdated;
	}
	
	/**
	 * Update the active parameter of resources.
	 * Fire a resources active updated event.
	 * @param resources The resources whose active parameter is to update.
	 * @return The resources that weren't updated.
	 */
	@SuppressWarnings("resource")
	public static Set<Resource> updateResourcesActive(Set<Resource> resources) {
		Set<Resource> notUpdated = new HashSet<Resource>();
		List<Resource> resourcesToUpdate = new ArrayList<Resource>();
		resourcesToUpdate.addAll(resources);
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_ACTIVE+" = ? WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(Resource resource: resourcesToUpdate) {
				statement.setBoolean(1, resource.isActive());
				statement.setInt(2, resource.getId());
				statement.addBatch();
			}
			int[] results = statement.executeBatch();
			statement.close();
			dbConnect.close();
			
			notUpdated = computeResourceResults(resourcesToUpdate, results);
			
			// Notify the database listeners about the operation:
			resources.removeAll(notUpdated);
			fireResourcesActiveUpdated(resources);
		} catch(SQLException e) {
			System.err.println("updateResourcesActive: "+e.getMessage());
			fireQueryError(e);
		}
		return notUpdated;
	}
	
	/**
	 * Get the resources that weren't successfully submitted from the list of resources submitted and the results from the database.
	 * @param resources The resources submitted.
	 * @param results Results of each operations: one operation per resource.
	 * @return The resources that weren't successfully submitted.
	 */
	private static Set<Resource> computeResourceResults(List<Resource> resources, int[] results) {
		Set<Resource> notSubmitted = new HashSet<Resource>();
		for(int i=0; i<results.length; i++) {
			if(results[i]==0) {
				notSubmitted.add(resources.get(i));
			}
		}
		return notSubmitted;
	}
	
	/**
	 * Get the resources that weren't successfully submitted from the list of resources submitted and the results from the database.
	 * @param resources The id of the resources submitted.
	 * @param results Results of each operations: one operation per resource.
	 * @return The resource ids that weren't successfully submitted.
	 */
	private static Set<Integer> computeIdResults(List<Integer> resources, int[] results) {
		Set<Integer> notSubmitted = new HashSet<Integer>();
		for(int i=0; i<results.length; i++) {
			if(results[i]==0) {
				notSubmitted.add(resources.get(i));
			}
		}
		return notSubmitted;
	}
	
	/**
	 * Fire a resources recovery event for all database listeners.
	 * @param active True if only the active resources have been recovered.
	 * @param resources The resources recovered.
	 */
	private static void fireResourcesRecovery(boolean active, Set<Resource> resources) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourcesRecovery(active, resources);
		}
	}

	/**
	 * Fire a resource added event for all database listeners.
	 * @param resource The resource added.
	 */
	private static void fireResourceAdded(Resource resource) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourceAdded(resource);
		}
	}

	/**
	 * Fire a Resources Active Updated event for all database listeners.
	 * @param resource The resource removed.
	 */
	private static void fireResourceRemoved(Resource resource) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourceRemoved(resource);
		}
	}

	/**
	 * Fire a resources removed event for all database listeners.
	 * @param resources The resources removed.
	 */
	private static void fireResourcesRemoved(Set<Resource> resources) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourcesRemoved(resources);
		}
	}

	/**
	 * Fire a resource updated event for all database listeners.
	 * @param resource The resource updated.
	 */
	private static void fireResourceUpdated(Resource resource) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourceUpdated(resource);
		}
	}

	/**
	 * Fire a resources trust updated event for all database listeners.
	 * @param resourceInvolvements Involvement in the game for each resources updated.
	 * @param gameResult The result of the game.
	 */
	private static void fireResourcesTrustUpdated(Map<Integer, Double> resourceInvolvements, int gameResult) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourcesTrustUpdated(resourceInvolvements, gameResult);
		}
	}

	/**
	 * Fire a Resources Active Updated event for all database listeners.
	 * @param resources The resources whose active parameters have been updated.
	 */
	private static void fireResourcesActiveUpdated(Set<Resource> resources) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onResourcesActiveUpdated(resources);
		}
	}
}