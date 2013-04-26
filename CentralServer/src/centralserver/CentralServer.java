package centralserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Manage the list of resources.
 */
public class CentralServer {
	private List<Resource> resources;
	private static final String DATABASES_RESOURCE_TABLE = "databases";
	private static final String BOTS_RESOURCE_TABLE = "bots";
	private static final String DB_NAME = "testResources.db";
	
	/**
	 * Constructor
	 */
	public CentralServer() {
		this.resources = new ArrayList<Resource>();
		try {
			this.restoreResources();
		} catch(ClassNotFoundException e) {
			//TODO
		} catch(SQLException e) {
			//TODO
		}
	}
	
	/**
	 * Restore the list of resources from a file (or a database file ?).
	 * @throws ClassNotFoundException TODO
	 * @throws SQLException TODO
	 */
	private void restoreResources() throws ClassNotFoundException, SQLException {
		// TODO Read the list of resources from a file.
		Class.forName("org.sqlite.JDBC");
		Connection dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);
		
		String baseRequest = "SELECT * FROM "; 
		
		PreparedStatement databasesState = dbConnect.prepareStatement(baseRequest + DATABASES_RESOURCE_TABLE);
		// We make the query to get databases names
		ResultSet dbResultSet = databasesState.executeQuery();
		
		// We create the databases Resources
		while(dbResultSet.next()) {
			this.resources.add(new Database(dbResultSet.getString("uri"), dbResultSet.getString("name"), dbResultSet.getInt("trust")));
		}
		
		// Now the same with bots
		PreparedStatement botsState = dbConnect.prepareStatement(baseRequest + BOTS_RESOURCE_TABLE);
		ResultSet botResultSet = botsState.executeQuery();
		
		while(botResultSet.next()) {
			this.resources.add(new Bot(botResultSet.getString("uri"), botResultSet.getString("name"), botResultSet.getInt("trust")));
		}
		
		System.out.println("Nombre de resources : " + resources.size());
	}
	
	/**
	 * Save the list of resources in a file if there was changes.
	 */
	private void saveResources() {
		for(Resource resource: this.resources) {
			if(resource.isChanged()) {
				try {
					saveResource(resource);
				} catch(ClassNotFoundException e) {
					// TODO
				} catch(SQLException e) {
					// TODO
				}
			}
		}
	}
	
	/**
	 * save a resource in the corresponding table
	 * @throws ClassNotFoundException TODO
	 * @throws SQLException TODO
	 */
	public void saveResource(Resource r) throws ClassNotFoundException, SQLException{
		String database;
		String query = new String();
		
		if(r instanceof Bot) {
			database = BOTS_RESOURCE_TABLE;
			query = "INSERT OR REPLACE INTO " + database + " VALUES (NULL, '" + r.getURI() + "', '" + r.getName() + "' , " + r.getTrust() + ")";
		} else if(r instanceof Database) {
			database = DATABASES_RESOURCE_TABLE;
			query = "INSERT OR REPLACE INTO " + database + " VALUES (NULL, '" + r.getURI() + "', '" + r.getName() + "', " + r.getTrust() + ")";
		}
		
		Connection dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);		
		PreparedStatement insertStmt = dbConnect.prepareStatement(query);
		
		insertStmt.executeUpdate();		
	}
	
	/**
	 * Get the suggestion of move from the resources and compute the best answer.
	 * @param fen The FEN.
	 * @return The best suggestion of move or null if no suggestion.
	 */
	public MoveSuggestion getBestMove(String fen) {
		this.updateResources(fen);
		// TODO Compute the best answer from all the answers from all resources.
		// The hashMap contains all the moves and the score associated 
		Map<MoveSuggestion, Double> moves = new HashMap<MoveSuggestion, Double>();
		
		for(Resource resource : this.resources) {
			for(MoveSuggestion move : resource.getMoveSuggestions()) {
				if(move.getClass().equals(DatabaseSuggestion.class)) {
					double moveScore = computeScoreDatabase((DatabaseSuggestion)move, resource);
					this.includeScore(moves, move, moveScore);
				}
			}
		}
		return bestMove(moves);
	}
	
	/**
	 * TODO
	 * @param moves TODO
	 * @return The best move (with the highest score) among all moves or null if no suggestion.
	 */
	private MoveSuggestion bestMove(Map<MoveSuggestion, Double> moves) {
		double max = -1;
		MoveSuggestion move = null;
		
		for(Map.Entry<MoveSuggestion, Double> entry : moves.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				move = entry.getKey();
			}
		}
		return move;
	}
	

	/**
	 * Include the move in the HashMap:
	 * if the move already exist, we add the score
	 * otherwise we create a new one in the HashMap
	 * @param moves TODO
	 * @param move TODO
	 * @param moveScore TODO
	 */
	private void includeScore(Map<MoveSuggestion, Double> moves, MoveSuggestion move, double moveScore) {
		if(moves.containsKey(move.getMove())) {
			moves.put(move, moves.get(move)+moveScore);
		} else {
			moves.put(move, moveScore);
		}
	}

	/**
	 * TODO
	 * @param move TODO
	 * @param resource TODO
	 * @return The score computed according to the formulas we defined
	 */
	private double computeScoreDatabase(DatabaseSuggestion move, Resource resource) {
		// TODO change the formula
		return move.getprobatowin()*move.getnb()*resource.getTrust();
	}

	/**
	 * Ask for all resources to update their suggestions of move.
	 * Do it using multithreading.
	 * Wait for the end of all updates.
	 * @param fen The FEN.
	 */
	private void updateResources(final String fen) {
		Set<Thread> threads = new HashSet<Thread>();
		for(final Resource resource: this.resources) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					resource.query(fen);
				}
			});
			thread.start();
			threads.add(thread);
		}
		for(Thread thread: threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// Shouldn't happen.
				System.err.println("The thread was interrupted: "+e.getMessage());
			}
		}
	}
}