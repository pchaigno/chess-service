package core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.event.EventListenerList;

import org.sqlite.SQLiteConfig;

/**
 * A master class for all database manager.
 * Regroups the methods used by all database manager and the location of the database file.
 * @author Paul Chaignon
 */
public class DatabaseManager {
	protected static String DATABASE_FILE = PropertiesManager.getProperty(PropertiesManager.PROPERTY_DATABASE);

	/**
	 * The list of event listener for the database.
	 * EventListenerList is used for a better multithread safety.
	 */
	private static final EventListenerList listeners = new EventListenerList();

	/**
	 * Add a database listener to the listeners.
	 * @param listener The new listener.
	 */
	public static void addDatabaseListener(DatabaseListener listener) {
		listeners.add(DatabaseListener.class, listener);
	}
	
	/**
	 * Remove a database listener from the listeners.
	 * @param listener The new listener.
	 */
	public static void removeDatabaseListener(DatabaseListener listener) {
		listeners.remove(DatabaseListener.class, listener);
	}
	
	/**
	 * @return The database listeners.
	 */
	public static DatabaseListener[] getDatabaseListeners() {
		return listeners.getListeners(DatabaseListener.class);
	}
	
	/**
	 * Change the location of the current database file.
	 * @param newDatabase The new database file used.
	 */
	public static void changeDatabase(String newDatabase) {
		DATABASE_FILE = newDatabase;
	}
	
	/**
	 * @return The location of the database file.
	 */
	public static String getDatabaseFile() {
		return DATABASE_FILE;
	}
	
	/**
	 * Get a connection to the SQLite database.
	 * Configure the database to add foreign keys support.
	 * @return The connection.
	 */
	protected static Connection getConnection() {
		Connection dbConnect = null;
		File databaseFile = new File(DATABASE_FILE);
		
		if(!databaseFile.exists()) {
			System.err.println("Database ("+DATABASE_FILE+") doesn't exists, creating a new one");
			try {
				databaseFile.createNewFile();
				init();
			} catch(IOException e) {
				System.err.println("Unable to create the database ("+DATABASE_FILE+")");
				fireCreateDatabaseError(e);
			} catch(SQLException e) {
				System.err.println("SQLException: "+e.getMessage());
				fireCreateDatabaseError(e);
			}
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
	        config.enforceForeignKeys(true);
			dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_FILE, config.toProperties());
		} catch(SQLException e) {
			System.err.println("Impossible to connect to the database "+DATABASE_FILE+".");
			System.err.println(e.getMessage());
			fireConnectionError(e);
		} catch(ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
			fireConnectionError(e);
		}
		return dbConnect;
	}
	
	/**
	 * Create all the tables needed for the central server.
	 * @throws SQLException If an SQL exception happens, when the request is wrong.
	 */
	private static void init() throws SQLException {
		Connection dbConnect = getConnection();
		String query = "CREATE TABLE resources(id INTEGER PRIMARY KEY, name TEXT NOT NULL, uri TEXT NOT NULL UNIQUE, trust INTEGER, type INTEGER, active INTEGER);";
		PreparedStatement statement = dbConnect.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
		query = "CREATE TABLE games(id INTEGER PRIMARY KEY, fen TEXT, san INTEGER);";
		statement = dbConnect.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
		query = "CREATE TABLE moves(resource INTEGER REFERENCES resources(id), game INTEGER REFERENCES games(id), num_move INTEGER, move_trust DOUBLE, PRIMARY KEY(resource, game, num_move))";
		statement = dbConnect.prepareStatement(query);
		statement.executeUpdate();
		statement.close();
		dbConnect.close();
	}

	/**
	 * Fire a create database error event for all database listeners.
	 * @param e The exception raised.
	 */
	private static void fireCreateDatabaseError(Exception e) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onCreateDatabaseError(e);
			listener.onDatabaseError(e);
		}
	}

	/**
	 * Fire a connection error event for all database listeners.
	 * @param e The exception raised.
	 */
	private static void fireConnectionError(Exception e) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onConnectionError(e);
			listener.onDatabaseError(e);
		}
	}

	/**
	 * Fire a query error event for all database listeners.
	 * @param e The SQL exception raised.
	 */
	protected static void fireQueryError(SQLException e) {
		for(DatabaseListener listener: getDatabaseListeners()) {
			listener.onQueryError(e);
			listener.onDatabaseError(e);
		}
	}
}
