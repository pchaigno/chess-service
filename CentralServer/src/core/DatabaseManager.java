package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

/**
 * A master class for all database manager.
 * @author Paul Chaignon
 */
public class DatabaseManager {
	protected static String DATABASE_FILE = PropertiesManager.getProperty(PropertiesManager.PROPERTY_DATABASE);

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
	 * Get a connection to the SQLite database.
	 * Configure the database to add foreign keys support.
	 * @return The connection.
	 */
	protected static Connection getConnection() {
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
