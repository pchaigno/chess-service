package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sqlite.SQLiteConfig;

/**
 * Handle all the accesses to the game SQLite table.
 */
public class GamesManager {
	private static final String FIRST_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq -";
	private static final String GAMES = "games";
	private static final String MOVES = "moves";
	private static final String GAME_ID = "id";
	private static final String GAME_FEN = "fen";
	private static final String MOVE_GAME = "id_game";
	private static final String MOVE_RESOURCE = "id_resource";
	private static final String MOVE_NUMBER = "num_move"; // The number of the move in the game
	
	
	/**
	 * Remove all traces of the game id_game.
	 * @param game_id The id of the game to remove.
	 * @return True if the game has been removed, false otherwise.
	 */
	public static boolean removeGame(int game_id) {
		Connection dbConnect = getConnection();
		String queryMoves = "DELETE FROM "+MOVES+" WHERE "+MOVE_GAME+"= ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(queryMoves);
			statement.setInt(1, game_id);
			statement.executeUpdate();
		} catch(SQLException e) {
			System.err.println("removeGame: "+e.getMessage());
			return false;
		}
		// TODO See for a cascade deletion.
		String queryGames = "DELETE FROM "+GAMES+" WHERE "+GAME_ID+"= ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(queryGames);
			statement.setInt(1, game_id);
			if(statement.executeUpdate()!=1){
				dbConnect.close();
				return false;
			}
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("removeGame: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Create a new game and return the id created.
	 * @return The id of the new game, -1 if an error occurred.
	 */
	public static int addNewGame() {
		int id = generateGameId();
		try {
			Connection dbConnect = getConnection();
			String query = "INSERT INTO "+GAMES+" ("+GAME_ID+", "+GAME_FEN+") VALUES(?, ?)";
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, id);
			statement.setString(2, FIRST_FEN);
			if(statement.executeUpdate()!=1){
				dbConnect.close();
				return -1;
			}
			dbConnect.close();
		} catch(SQLException e) {
			System.err.println("addNewGame: "+e.getMessage());
			return -1;
		}
		return id;
	}
	
	/**
	 * Update a game.
	 * @param game_id The id of the game to update.
	 * @param fen The new fen.
	 * @return True if the update succeed. It can fail if the game doesn't exist in the database.
	 */
	public static boolean updateGame(int game_id, String fen) {
		if(gameExist(game_id)) {
			Connection dbConnect = getConnection();
			String query = "UPDATE "+GAMES+" SET "+GAME_FEN+" = ? WHERE "+GAME_ID+" = ?";
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setString(1, fen);
				statement.setInt(2, game_id);
				if(statement.executeUpdate()!=1){
					dbConnect.close();
					return false;
				}
				dbConnect.close();
				return true;
			} catch(SQLException e) {
				System.err.println("updateGame: "+e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return the number of moves in the game id_game.
	 * @param game_id The id of the game concerned.
	 * @return The number of moves, -1 if problem encountered.
	 */
	public static int getNumberOfMoves(int game_id) {
		if(gameExist(game_id)) {
			Connection dbConnect = getConnection();
			String query = "SELECT MAX("+MOVE_NUMBER+") AS max FROM "+MOVES+" WHERE "+MOVE_GAME+"= ?";
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setInt(1, game_id);
				ResultSet set = statement.executeQuery();
				set.next();
				int nbMoves = set.getInt("max");
				dbConnect.close();
				return nbMoves;
			} catch(SQLException e) {
				System.err.println("getNumberOfMoves: "+e.getMessage());
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	/**
	 * Generate an id that isn't used.
	 * @return The id.
	 */
	private static int generateGameId() {
		int id;
		do {
			id = 1 + (int)(Math.random()*500000);
		} while(gameExist(id));
		return id;
	}
	
	/**
	 * @param game_id The game id.
	 * @return True if the game exists.
	 */
	private static boolean gameExist(int game_id) {
		Connection dbConnect = getConnection();
		String query = "SELECT "+GAME_ID+" FROM "+GAMES+" WHERE "+GAME_ID+"= ?";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, game_id);
			ResultSet set = statement.executeQuery();
			dbConnect.close();
			return set.next();
		} catch(SQLException e) {
			System.err.println("gameExist: "+e.getMessage());
			return false; // TODO That's kind of dangerous...
		}
	}
	
	/**
	 * Add a new move for reward.
	 * @param id_game The id of the game.
	 * @param id_resource The id of the resource.
	 * @param move_number The move number.
	 * @return True if added, false otherwise.
	 */
	public static boolean addMove(int id_game, Set<Integer> resources_id, int move_number) {
		if(gameExist(id_game)) {
			Connection dbConnect = getConnection();
			String query = "INSERT INTO "+MOVES+" ("+MOVE_GAME+", "+MOVE_RESOURCE+", "+MOVE_NUMBER+") VALUES(?, ?, ?)";
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				for(Integer resource_id : resources_id){
					statement.setInt(1, id_game);
					statement.setInt(2, resource_id);
					statement.setInt(3, move_number);
					statement.addBatch();
				}
				int[] results = statement.executeBatch();
				for(int i=0; i<results.length; i++){
					if(results[i]!=1){
						dbConnect.close();
						return false;
					}
				}
				dbConnect.close();
				return true;
			} catch(SQLException e) {
				System.err.println("addMove: "+e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Get the ration of played move for each resources in a game.
	 * Return a map with the resource id as key and the ration of played move as value.
	 * @param id_game The id of the game to scan.
	 * @return The map or null if an error occurred.
	 */
	public static Map<Integer,Double> getResourcesStats(int id_game) {
		Map<Integer, Double> stats = new HashMap<Integer, Double>();
		// TODO Isn't it possible to get that info after?
		int nbTotalMoves = getNumberOfMoves(id_game);
		if(nbTotalMoves<=0) {
			return stats;
		}
		if(gameExist(id_game)) {
			Connection dbConnect = getConnection();
			String query = "SELECT COUNT(DISTINCT "+MOVE_NUMBER+") AS moveNumber, "+MOVE_RESOURCE+" FROM "+MOVES+" WHERE "+MOVE_GAME+"= ? GROUP BY "+MOVE_RESOURCE;
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setInt(1, id_game);
				ResultSet set = statement.executeQuery();
				while(set.next()) {
					stats.put(set.getInt(MOVE_RESOURCE), (Double)set.getDouble("moveNumber")/nbTotalMoves);
				}
				dbConnect.close();
				return stats;
			} catch(SQLException e) {
				System.err.println("getResourcesStats: "+e.getMessage());
				return null;
			}
		} else {
			return null;
		}
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
			dbConnect = DriverManager.getConnection("jdbc:sqlite:"+ResourcesManager.DATABASE_FILE, config.toProperties());
		} catch (SQLException e) {
			System.err.println("Impossible to connect to the database "+ResourcesManager.DATABASE_FILE+".");
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
		}
		return dbConnect;
	}
}