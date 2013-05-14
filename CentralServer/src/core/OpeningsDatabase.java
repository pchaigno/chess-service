package core;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.ChessParser;
import parser.IncorrectFENException;

/**
 * Represent a database of openings.
 * @author Paul Chaignon
 * @author Clement Gautrais
 */
public class OpeningsDatabase extends Resource {
	private List<OpeningSuggestion> moves;
	protected static final String JSON_NB_PLAY = "nb";
	protected static final String JSON_PROBA_WIN = "probatowin";
	protected static final String JSON_PROBA_DRAW = "probatonull";
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 * @param active True if the resource is active.
	 */
	public OpeningsDatabase(String uri, String name, int trust, boolean active) {
		super(uri, name, trust, active);
		this.moves = new LinkedList<OpeningSuggestion>();
	}

	@Override
	public List<OpeningSuggestion> getMoveSuggestions() {
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
			double probaWin = json.getDouble(JSON_PROBA_WIN);
			int nb = json.getInt(JSON_NB_PLAY);
			double probaDraw = json.getDouble(JSON_PROBA_DRAW);
			OpeningSuggestion suggestion = new OpeningSuggestion(move, nb, probaWin, probaDraw);
			this.moves.add(suggestion);
		}
	}

	@Override
	protected void clearSuggestions() {
		this.moves.clear();
	}
	
	/**
	 * For testing (maybe another class after)
	 * @param args Unused
	 */
	public static void main(String[] args) {
		OpeningsDatabase db1 = new OpeningsDatabase("http://localhost/1.0/rest/openings/", "Db1", 1, true);
		db1.query("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR%20w%20KQkq%20-%200%20");
    }
}