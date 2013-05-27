package core;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.ChessParser;
import parser.IncorrectFENException;

/**
 * Represent a database of endings.
 * @author Paul Chaignon
 */
public class EndingsDatabase extends Resource {
	private List<EndingSuggestion> moves;
	private static final String JSON_RESULT = "result";
	private static final String JSON_NB_MOVES = "nb_moves";

	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public EndingsDatabase(String uri, String name, boolean active, int id) {
		super(uri, name, DEFAULT_TRUST, active, id);
		this.moves = new LinkedList<EndingSuggestion>();
	}
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public EndingsDatabase(String uri, String name, int trust, boolean active, int id) {
		super(uri, name, trust, active, id);
		this.moves = new LinkedList<EndingSuggestion>();
	}

	@Override
	public List<EndingSuggestion> getMoveSuggestions() {
		return this.moves;
	}
	
	@Override
	protected void parseJSONMove(String response, String fen) {
		JSONArray jsonArray = new JSONArray(response);
		for(int i=0 ; i<jsonArray.length() ; i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String move = json.getString(JSON_MOVE);
			if(!this.san) {
				ChessParser parser;
				try {
					parser = new ChessParser(fen);
					move = parser.convertLANToSAN(move);
				} catch (IncorrectFENException e) {
					// Shouldn't happen !
					System.err.println("parseJSONMove :"+e.getMessage());
				}
			}
			int result = json.getInt(JSON_RESULT);
			int nbMoves = json.getInt(JSON_NB_MOVES);
			EndingSuggestion suggestion = new EndingSuggestion(move, result, nbMoves);
			this.moves.add(suggestion);
		}
	}

	@Override
	protected void clearSuggestions() {
		this.moves.clear();
	}
}