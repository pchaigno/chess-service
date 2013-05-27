package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import junit.framework.TestCase;

/**
 * Unit test to create the database.
 * @author Paul Chaignon
 */
public class TestForCreation extends TestCase {

	public static void testCreateResources() throws Exception {
		//fail();
		Connection dbConnect = null;
		Class.forName("org.sqlite.JDBC");
		dbConnect = DriverManager.getConnection("jdbc:sqlite:resources.db");
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
}