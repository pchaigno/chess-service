package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sqlite.SQLiteConfig;

/**
 * Handle all the accesses to the SQLite database for the resources.
 */
public class ResourcesManager {
	public static String DATABASE_FILE = PropertiesManager.getProperty(PropertiesManager.PROPERTY_DATABASE);
	private static final String RESOURCES = "resources";
	private static final String RESOURCE_ID = "id";
	private static final String RESOURCE_URI = "uri";
	private static final String RESOURCE_NAME = "name";
	private static final String RESOURCE_TRUST = "trust";
	private static final String RESOURCE_TYPE = "type";

	/**
	 * Change the current database.
	 * @param newDatabase The new database used.
	 */
	public static void changeDatabase(String newDatabase){
		DATABASE_FILE = newDatabase;
	}
	
	/**
	 * @return The database file.
	 */
	public static String getDatabaseFile(){
		return DATABASE_FILE;
	}
	
	/**
	 * @return All resources from the database.
	 */
	public static Set<Resource> getResources() {
		Set<Resource> resources = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "SELECT * FROM "+RESOURCES;
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				Resource resource;
				if(results.getInt("type")==Resource.OPENINGS_DATABASE) {
					resource = new OpeningsDatabase(results.getString("uri"), results.getString("name"), results.getInt("trust"));
				} else if(results.getInt("type")==Resource.ENDINGS_DATABASE) {
					resource = new EndingsDatabase(results.getString("uri"), results.getString("name"), results.getInt("trust"));
				} else {
					resource = new Bot(results.getString("uri"), results.getString("name"), results.getInt("trust"));
				}
				resource.setId(results.getInt(RESOURCE_ID));
				resources.add(resource);
			}
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("getResources: "+e.getMessage());
		}
		
		return resources;
	}
	
	/**
	 * Add a resource to the database and set the resource id with the one used in the sql table
	 * @param resource The resource to add.
	 * @return True if the operation succeed, false otherwise.
	 */
	public static boolean addResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "INSERT INTO "+RESOURCES+"("+RESOURCE_TYPE+", "+RESOURCE_NAME+", "+RESOURCE_URI+", "+RESOURCE_TRUST+") VALUES(?, ?, ?, ?)";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
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
				dbConnect.close();
				return false;
			} else {
				String queryLastId = "SELECT last_insert_rowid() AS last_id";
				statement = dbConnect.prepareStatement(queryLastId);
				ResultSet res = statement.executeQuery();
				res.next();
				resource.setId(res.getInt("last_id"));
				return true;
			}
		} catch(SQLException e) {
			System.err.println("addResource: "+e.getMessage());
		}
		return false;
	}
	
	/**
	 * Remove a resource from the database.
	 * @param resource The resource to remove.
	 * @return True if the operation succeed, false otherwise.
	 */
	public static boolean removeResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "DELETE FROM "+RESOURCES+" WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, resource.getId());
			if(statement.executeUpdate()!=1) {
				dbConnect.close();
				return false;
			}
			dbConnect.close();
			return true;
		} catch(SQLException e) {
			System.err.println("removeResource: "+e.getMessage());
		}
		return false;
	}
	
	/**
	 * Remove resources from the database.
	 * @param resources The resources to remove.
	 * @return The resources that weren't removed.
	 */
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
			for(int i=0 ; i<results.length ; i++) {
				if(results[i]==0) {
					notRemoved.add(resourcesToRemove.get(i));
				}
			}
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("removeResources: "+e.getMessage());
		}
		return notRemoved;
	}
	
	/**
	 * Update a resource in the database.
	 * All fields except the URI can be updated.
	 * @param resource The resource to update.
	 * @return True if the update succeed, false otherwise.
	 */
	public static boolean updateResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_NAME+" = ?, "+RESOURCE_TRUST+" = ?, "+RESOURCE_TYPE+" = ? WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setString(1, resource.getName());
			statement.setInt(2, resource.getTrust());
			int type = Resource.BOT;
			if(resource.getClass()==OpeningsDatabase.class) {
				type = Resource.OPENINGS_DATABASE;
			} else if(resource.getClass()==EndingsDatabase.class) {
				type = Resource.ENDINGS_DATABASE;
			}
			statement.setInt(3, type);
			statement.setInt(4, resource.getId());
			if(statement.executeUpdate()!=1) {
				dbConnect.close();
				return false;
			}
			dbConnect.close();
			return true;
		} catch(SQLException e) {
			System.err.println("updateResource: "+e.getMessage());
		}
		return false;
	}
	
	/**
	 * Update the trust parameter of resources.
	 * @param resources The resources whose trust is to update.
	 * @return The resources that weren't updated.
	 */
	public static Set<Resource> updateResourcesTrust(Set<Resource> resources) {
		Set<Resource> notUpdated = new HashSet<Resource>();
		List<Resource> resourcesToUpdate = new ArrayList<Resource>();
		resourcesToUpdate.addAll(resources);
		Connection dbConnect = getConnection();
		boolean changed = false;
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_TRUST+" = ? WHERE "+RESOURCE_ID+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(Resource resource: resourcesToUpdate) {
				if(resource.isChanged()) {
					changed=true;
					statement.setInt(1, resource.getTrust());
					statement.setInt(2, resource.getId());
					statement.addBatch();
				}
			}
			if(changed) {
				int[] results = statement.executeBatch();
				for(int i=0 ; i<results.length ; i++) {
					if(results[i]==0) {
						notUpdated.add(resourcesToUpdate.get(i));
					}
				}
				dbConnect.close();
			}
		} catch(SQLException e) {
			System.err.println("updateResourcesTrust: "+e.getMessage());
		}
		return notUpdated;
	}
	
	/**
	 * Get a connection to the SQLite database.
	 * Configure the database to add foreign keys support.
	 * @return The connection.
	 */
	private static Connection getConnection() {
		Connection dbConnect = null;
		try {
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
	        config.enforceForeignKeys(true);
			dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_FILE, config.toProperties());
		} catch(SQLException e) {
			System.err.println("Impossible to connect to the database "+DATABASE_FILE+".");
			System.err.println(e.getMessage());
		} catch(ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
		}
		return dbConnect;
	}
}