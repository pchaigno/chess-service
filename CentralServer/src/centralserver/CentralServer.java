package centralserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Manage the list of resources.
 */
/**
 * @author clemgaut
 *
 */
/**
 * @author clemgaut
 *
 */
/**
 * @author clemgaut
 *
 */
public class CentralServer {
	private List<Resource> resources;
	private boolean resources_changed;
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
			// TODO Throw an exception that tells "it doesn't work"
			e.printStackTrace();
		} catch(SQLException e) {
			// TODO Throw an exception that tells "it doesn't work"
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
		Connection dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);
		
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
			this.resources.add(new Bot(botResultSet.getString("uri"), botResultSet.getString("name"), botResultSet.getInt("trust")));
		}
		
		System.out.println("Nombre de resources : " + resources.size());
	}
	
	/**
	 * Save the list of resources in a file if there was changes.
	 */
	private void saveResources() {
		for(Resource resource : resources){
			if(resource.isChanged()){
				try {
					saveResource(resource);
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * save a resource in the corresponding table
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public void saveResource(Resource r) throws ClassNotFoundException, SQLException{
		String database;
		String query = new String();
		
		if(r instanceof Bot){
			database = BOTS_RESOURCE_TABLE;
			query = "INSERT OR REPLACE INTO " + database + " VALUES (NULL, '" + r.getURI() + "', '" + r.getName() + "' , " + r.getTrust() + ")";
		}
		else if(r instanceof Database){
			database = DATABASES_RESOURCE_TABLE;
			query = "INSERT OR REPLACE INTO " + database + " VALUES (NULL, '" + r.getURI() + "', '" + r.getName() + "', " + r.getTrust() + ")";
		}
		
		Class.forName("org.sqlite.JDBC");
		Connection dbConnect = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);		
		PreparedStatement insertStmt = dbConnect.prepareStatement(query);
		
		insertStmt.executeUpdate();		
	}
	
	/**
	 * Get the suggestion of move from the resources and compute the best answer.
	 * @param fen The FEN.
	 * @return The best suggestion of move.
	 */
	public MoveSuggestion getBestMove(String fen) {
		this.updateResources(fen);
		// TODO Compute the best answer from all the answers from all resources.
		//The hashMap contains al the moves and the score associated 
		HashMap<String, Double> moves = new HashMap<String, Double>();
		
		for(Resource resource : this.resources){
			for(MoveSuggestion move : resource.getMoveSuggestions()){
				//TODO avoid this cast in a second time
				double moveScore = computeScoreDatabase((DatabaseSuggestion)move, resource);
				includeScore(moves, move, moveScore);
			}
		}
		return bestMove(moves);
	}
	
	
	/**
	 * @param moves
	 * @return the best move (with the highest score) among all moves
	 */
	private MoveSuggestion bestMove(HashMap<String, Double> moves) {
		//TODO do best and cleaner ;) (i'm sick)
		double max = -1;
		String move = "error";
		
		for(Map.Entry<String, Double> entry : moves.entrySet()){
			if(entry.getValue() > max){
				max = entry.getValue();
				move = entry.getKey();
			}
		}
		
		
		return new MoveSuggestion(move);
	}
	

	/**
	 * Include the move in the HashMap :
	 * 	if the move already exist, we add the score
	 * 	otherwise we create a new one in the HashMap
	 * @param moves
	 * @param move
	 * @param moveScore
	 */
	private void includeScore(HashMap<String, Double> moves, MoveSuggestion move, double moveScore) {
		if(moves.containsKey(move.getMove())){
			moves.put(move.getMove(), moves.get(move.getMove())+moveScore);
		}
		else{
			moves.put(move.getMove(), moveScore);
		}
	}

	/**
	 * @param move
	 * @param resource
	 * @return The score computed according to the formulas we defined
	 */
	private double computeScoreDatabase(DatabaseSuggestion move, Resource resource) {
		//TODO change the formula
		return move.getprobatowin()*move.getnb()*resource.getTrust();
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
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		CentralServer server1 = new CentralServer();		
	  }
}
