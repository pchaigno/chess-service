package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

public class TestForCreation extends TestCase {

	public void testCreateResources() {
		fail();
		Connection dbConnect = null;
		try {
			Class.forName("org.sqlite.JDBC");
			dbConnect = DriverManager.getConnection("jdbc:sqlite:resources.db");
		} catch (SQLException e) {
			System.err.println("Impossible to connect to the database resources.db.");
		} catch (ClassNotFoundException e) {
			System.err.println("Driver missing for SQLite JDBC.");
		}
		String query = "CREATE TABLE resources(id INTEGER PRIMARY KEY, name TEXT NOT NULL, uri TEXT NOT NULL, trust INTEGER, type INTEGER);";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
			dbConnect.close();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
		query = "CREATE TABLE games(id INTEGER PRIMARY KEY, fen TEXT, nb_moves INTEGER);";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
			dbConnect.close();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
		query = "CREATE TABLE moves(resource INTEGER REFERENCES resources(id), game INTEGER REFERENCES games(id), num_move INTEGER, PRIMARY KEY(resource, game, num_move))";
		try {
			PreparedStatement statement = dbConnect.prepareStatement(query);
			statement.executeUpdate();
			dbConnect.close();
		} catch (SQLException e) {
			System.err.println("SQLException: "+e.getMessage());
		}
	}
}