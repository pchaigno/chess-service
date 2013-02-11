package centralserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage the list of resources.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;

public class CentralServer {
	private List<Resource> resources;
	private boolean resources_changed;
	private static final String DATABASES_RESOURCE_TABLE = "databases";
	private static final String BOTS_RESOURCE_TABLE = "bots";
	
	/**
	 * Constructor
	 */
	public CentralServer() {
		this.resources = new ArrayList<Resource>();
		try {
			this.restoreResources();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Throw an excpetion that tells "it doesn't work"
			e.printStackTrace();
		}
		this.resources_changed = false;
	}
	
	/**
	 * Restore the list of resources from a file (or a database file ?).
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	private void restoreResources() throws ClassNotFoundException, SQLException {
		// TODO Read the list of resources from a file.
		Class.forName("org.sqlite.JDBC");
		Connection dbConnect = DriverManager.getConnection("jdbc:sqlite:resources.db");
		
		String baseRequest = "SELECT * FROM "; 
		
		PreparedStatement databasesState = dbConnect.prepareStatement(baseRequest + DATABASES_RESOURCE_TABLE);
		//We make the query to get databases names
		ResultSet dbResultSet = databasesState.executeQuery();
		
		//We create the databases Resources
		while(dbResultSet.next()){
			this.resources.add(new Database(dbResultSet.getString("uri"), dbResultSet.getString("name"), dbResultSet.getInt("trust")));
		}
		
		//Now the same with bots
		PreparedStatement botsState = dbConnect.prepareStatement(baseRequest + BOTS_RESOURCE_TABLE);
		ResultSet botResultSet = botsState.executeQuery();
		
		while(botResultSet.next()){
			this.resources.add(new Bot(botResultSet.getString("uri"), botResultSet.getString("name")));
		}
		
		System.out.println("Nombre de resources : " + resources.size());
	}
	
	/**
	 * Save the list of resources in a file if there was changes.
	 */
	private void saveResources() {
		if(this.resources_changed) {
			// TODO Write the resources in a file.
		}
	}
	
	/**
	 * Get the suggestion of move from the resources and compute the best answer.
	 * @param fen The FEN.
	 * @return The best suggestion of move.
	 */
	public MoveSuggestion getBestMove(String fen) {
		this.updateResources(fen);
		// TODO Compute the best answer from all the answers from all resources.
		return null;
	}
	
	/**
	 * Ask for all resources to update their suggestions of move.
	 * Do it using multithreading.
	 * @param fen The FEN.
	 */
	private void updateResources(String fen) {
		// TODO Use Multithreading.
		for(Resource resource: this.resources) {
			resource.query(fen);
		}
	}
	
	/**
	 * For testing (maybe an other class after)
	 * @param args
	 */
	public static void main(String[] args) {
		CentralServer server1 = new CentralServer();
	  }
}