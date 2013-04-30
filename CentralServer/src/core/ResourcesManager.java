package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.sqlite.SQLiteConfig;

public class ResourcesManager {
	private static final String DATABASE_FILE = "resources.db";
	private static final String RESOURCES = "resources";
	private static final String RESOURCE_URI = "uri";
	private static final String RESOURCE_NAME = "name";
	private static final String RESOURCE_TRUST = "trust";
	private static final String RESOURCE_TYPE = "type";

	public static Set<Resource> getResources() {
		Set<Resource> resources = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "SELECT * FROM "+RESOURCES;
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			ResultSet results = statement.executeQuery();
			while(results.next()) {
				Resource resource;
				if(results.getInt("type")==Resource.DATABASE) {
					resource = new Bot(results.getString("uri"), results.getString("name"), results.getInt("trust"));
				} else {
					resource = new Database(results.getString("uri"), results.getString("name"), results.getInt("trust"));
				}
				resources.add(resource);
			}
			dbConnect.close();
		} catch (SQLException e) {
			System.err.println("getResources: "+e.getMessage());
		}
		
		return resources;
	}
	
	public static boolean addResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "INSERT INTO "+RESOURCES+"("+RESOURCE_TYPE+", "+RESOURCE_NAME+", "+RESOURCE_URI+", "+RESOURCE_TRUST+") VALUES(?, ?, ?, ?)";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			int type = resource.getClass().equals(Database.class)? Resource.DATABASE : Resource.BOT;
			statement.setInt(1, type);
			statement.setString(2, resource.getName());
			statement.setString(3, resource.getURI());
			statement.setInt(4, resource.getTrust());
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("addResource: "+e.getMessage());
		}
		return false;
	}
	
	public static boolean removeResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "DELETE FROM "+RESOURCES+" WHERE "+RESOURCE_URI+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setString(1, resource.getURI());
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("removeResource: "+e.getMessage());
		}
		return false;
	}
	
	public static Set<Resource> removeResources(Set<Resource> resources) {
		Set<Resource> notRemoved = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "DELETE FROM "+RESOURCES+" WHERE "+RESOURCE_URI+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(Resource resource: resources) {
				statement.setString(1, resource.getURI());
				statement.addBatch();
				// TODO Update notRemoved.
			}
			statement.executeBatch();
		} catch (SQLException e) {
			System.err.println("removeResources: "+e.getMessage());
		}
		return notRemoved;
	}
	
	public static boolean updateResource(Resource resource) {
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_NAME+" = ?, "+RESOURCE_TRUST+" = ?, "+RESOURCE_TYPE+" = ? WHERE "+RESOURCE_URI+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setString(1, resource.getName());
			statement.setInt(2, resource.getTrust());
			int type = resource.getClass().equals(Database.class)? Resource.DATABASE : Resource.BOT; 
			statement.setInt(3, type);
			statement.setString(4, resource.getURI());
			System.out.println(statement.executeUpdate());
			return true;
		} catch (SQLException e) {
			System.err.println("updateResource: "+e.getMessage());
		}
		return false;
	}
	
	public static Set<Resource> updateResourcesTrust(Set<Resource> resources) {
		Set<Resource> notUpdated = new HashSet<Resource>();
		Connection dbConnect = getConnection();
		String query = "UPDATE "+RESOURCES+" SET "+RESOURCE_TRUST+" = ? WHERE "+RESOURCE_URI+" = ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			for(Resource resource: resources) {
				if(resource.isChanged()) {
					statement.setInt(1, resource.getTrust());
					statement.setString(2, resource.getURI());
					statement.addBatch();
				}
				// TODO And if nothing has been changed?
			}
			statement.executeBatch();
			// TODO Update notUpdated.
		} catch (SQLException e) {
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
		} catch (SQLException e) {
			System.err.println("Impossible to connect to the database "+DATABASE_FILE+".");
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
		}
		return dbConnect;
	}
}