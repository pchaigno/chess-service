package core;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.ChessParser;
import parser.IncorrectAlgebraicNotationException;
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
	 * Initialize the resource with the default trust.
	 * @param uri The URI.
	 * @param name The name.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public OpeningsDatabase(String uri, String name, boolean active, int id) {
		super(uri, name, DEFAULT_TRUST, active, id);
		this.moves = new LinkedList<OpeningSuggestion>();
	}
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public OpeningsDatabase(String uri, String name, int trust, boolean active, int id) {
		super(uri, name, trust, active, id);
		this.moves = new LinkedList<OpeningSuggestion>();
	}

	@Override
	public List<OpeningSuggestion> getMoveSuggestions() {
		return this.moves;
	}
	
	@Override
	protected void parseJSONMove(String response, String fen) {
		JSONArray jsonArray = new JSONArray(response);
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String move = json.getString(JSON_MOVE);
			if(!this.san) {
			// The resource sends us LAN so it need to be converted.
				ChessParser parser;
				try {
					parser = new ChessParser(fen);
					move = parser.convertLANToSAN(move);
				} catch (IncorrectFENException e) {
					System.err.println("parseJSONMove :"+e.getMessage());
					continue;
				} catch(IncorrectAlgebraicNotationException e) {
					System.err.println("parseJSONMove: "+e.getMessage());
					continue;
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
}