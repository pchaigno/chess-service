package centralserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a database.
 */
public class Database extends Resource{
	private List<DatabaseSuggestion> moves;
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 */
	public Database(String uri, String name, int trust) {
		super(uri, name, trust);
	}

	@Override
	public List<DatabaseSuggestion> getMoveSuggestions() {
		return this.moves;
	}

	@Override
	public void query(String fen) {
		this.moves = new ArrayList<DatabaseSuggestion>();
		// TODO Query the database and update this.moves with the reponses.
	}
}