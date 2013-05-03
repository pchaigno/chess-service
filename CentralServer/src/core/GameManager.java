package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

public class GameManager {
	private static final String FIRST_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq -";
	private static final String DATABASES_DIRECTORY = "databases/";
	private static final String DATABASE_NAME = "games.db";
	private static final String DATABASE_FILE = DATABASES_DIRECTORY + DATABASE_NAME;
	private static final String GAMES = "current_games";
	private static final String MOVES = "current_moves";
	private static final String GAME_ID = "id_game";
	private static final String FEN = "fen";
	private static final String NUMBER_OF_MOVES = "nb_moves";
	private static final String RESOURCE_ID = "id_resource";
	private static final String MOVE = "move";
	private static final String MOVE_NUMBER = "num_move";
	
	
	/**
	 * Remove all traces of the game id_game
	 * @param game_id the id of the game to remove
	 * @return true if the game has been removed, false otherwise
	 */
	public static boolean removeGame(int game_id){
		Connection dbConnect = getConnection();
		String queryGames="DELETE FROM " + GAMES + " WHERE " + GAME_ID + "= ?";
		try{
			PreparedStatement statement = dbConnect.prepareStatement(queryGames);
			statement.setInt(1, game_id);
			statement.executeUpdate();
		}catch(SQLException e){
			System.err.println("removeGame: "+e.getMessage());
			return false;
		}
		String queryMoves = "DELETE FROM " + MOVES + " WHERE " + GAME_ID + "= ?";
		try{
			PreparedStatement statement = dbConnect.prepareStatement(queryMoves);
			statement.setInt(1, game_id);
			statement.executeUpdate();
			dbConnect.close();
		}catch(SQLException e){
			System.err.println("removeGame: "+e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Create a new game and return the id created
	 * @return the id of the new game
	 */
	public static int addNewGame(){
		int id=-1;
		do{
			id=generateGameId();
		}while(id<0);
		
		try{
			Connection dbConnect = getConnection();
			String query = "INSERT INTO "+GAMES+" ("+GAME_ID+", "+FEN+", "+NUMBER_OF_MOVES+") VALUES(?, ?, ?)";
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, id);
			statement.setString(2, FIRST_FEN);
			statement.setInt(3, 0);
			statement.executeUpdate();
			dbConnect.close();
		}catch(SQLException e){
			System.err.println("addNewGame: "+e.getMessage());
		}
		return id;
	}
	
	/**
	 * Update a game
	 * @param game_id the id of the game to update
	 * @param fen the new fen
	 * @param nb_moves the number of moves
	 * @return
	 */
	public static boolean updateGame(int game_id, String fen, int nb_moves){
		if(gameExist(game_id)){
			Connection dbConnect = getConnection();
			String query = "UPDATE "+GAMES+" SET "+FEN+" = ?, "+NUMBER_OF_MOVES+" = ? WHERE "+GAME_ID+" = ?";
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setString(1, fen);
				statement.setInt(2,nb_moves);
				statement.setInt(3, game_id);
				statement.executeUpdate();
				dbConnect.close();
				return true;
			}catch(SQLException e){
				System.err.println("updateGame: "+e.getMessage());
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	/**
	 * Return the number of moves in the game id_game
	 * @param game_id the id of the game concerned
	 * @return the number of moves, -1 if problem encountered
	 */
	public static int getNumberOfMoves(int game_id){
		if(gameExist(game_id)){
			Connection dbConnect = getConnection();
			String query = "SELECT "+NUMBER_OF_MOVES+" FROM "+GAMES+" WHERE "+GAME_ID+"= ?";
			try {
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setInt(1, game_id);
				ResultSet set = statement.executeQuery();
				set.next();
				dbConnect.close();
				return set.getInt(NUMBER_OF_MOVES);
			}catch(SQLException e){
				System.err.println("getNumberOfMoves: "+e.getMessage());
				return -1;
			}
		}
		else{
			return -1;
		}
	}
	
	/**
	 * Generate a id that isn't used
	 * @return the id
	 */
	private static int generateGameId(){
		int id;
		
		do{
			id = 1 + (int)(Math.random()*500000);
		}while(gameExist(id));
		
		return id;
	}
	
	private static boolean gameExist(int game_id){
		Connection dbConnect = getConnection();
		String query = "SELECT " + GAME_ID + " FROM " + GAMES + " WHERE " + GAME_ID + "= ?";
		try{
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.setInt(1, game_id);
			ResultSet set = statement.executeQuery();
			return set.next();
		}catch(SQLException e){
			System.err.println("gameExist: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Add a new move for reward
	 * @param id_game
	 * @param id_resource
	 * @param move
	 * @param move_number
	 * @return true if added, false otherwise
	 */
	public static boolean addMove(int id_game, int id_resource, String move, int move_number){
		if(gameExist(id_game)){
			Connection dbConnect = getConnection();
			String query = "INSERT INTO "+MOVES+" ("+GAME_ID+", "+RESOURCE_ID+", "+MOVE+", "+MOVE_NUMBER+") VALUES(?, ?, ?, ?)";
			try{
				PreparedStatement statement = dbConnect.prepareStatement(query);
				statement.setInt(1, id_game);
				statement.setInt(2, id_resource);
				statement.setString(3, move);
				statement.setInt(4, move_number);
				statement.executeUpdate();
				dbConnect.close();
				return true;
			}catch(SQLException e){
				System.err.println("addMove: "+e.getMessage());
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	//TODO recuperer les resources correspondant a une partie, avec le nombre de fois qu'elle apparait
	
	
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
