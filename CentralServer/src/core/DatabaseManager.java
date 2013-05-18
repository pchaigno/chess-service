package core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

/**
 * A master class for all database manager.
 * Regroups the methods used by all database manager and the location of the database file.
 * @author Paul Chaignon
 */
public class DatabaseManager {
	protected static String DATABASE_FILE = PropertiesManager.getProperty(PropertiesManager.PROPERTY_DATABASE);

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
			} catch (IOException e) {
				System.err.println("Unable to create the database ("+DATABASE_FILE+")");
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
		} catch(ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
		}
		return dbConnect;
	}
	
	/**
	 * Create all the tables needed for the central server
	 */
	protected static void init(){
		Connection dbConnect = getConnection();
		String query = "CREATE TABLE resources(id INTEGER PRIMARY KEY, name TEXT NOT NULL, uri TEXT NOT NULL UNIQUE, trust INTEGER, type INTEGER, active INTEGER);";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
		query = "CREATE TABLE games(id INTEGER PRIMARY KEY, fen TEXT, san INTEGER);";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
		query = "CREATE TABLE moves(resource INTEGER REFERENCES resources(id), game INTEGER REFERENCES games(id), num_move INTEGER, move_trust DOUBLE, PRIMARY KEY(resource, game, num_move))";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
			dbConnect.close();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
	}
}
