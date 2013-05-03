package core;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.ChessParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Represent a database.
 */
public class OpeningsDatabase extends Resource {
	private List<OpeningSuggestion> moves;
	private static final String JSON_MOVE = "move";
	private static final String JSON_NB_PLAY = "nb";
	private static final String JSON_PROBA_WIN = "probatowin";
	private static final String JSON_PROBA_DRAW = "probatonull";
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 */
	public OpeningsDatabase(String uri, String name, int trust) {
		super(uri, name, trust);
		this.moves = new LinkedList<OpeningSuggestion>();
	}

	@Override
	public List<OpeningSuggestion> getMoveSuggestions() {
		return this.moves;
	}

	@Override
	public void query(String fen) {
		this.moves.clear();
		
		// We call the client
		Client c = Client.create();
		// TODO handle the last slash
		WebResource r = c.resource(this.uri+fen);
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(READ_TIMEOUT);
		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		
		this.parseJSONMove(response, fen);
	}
	
	/**
	 * Parse the JSON moves to openings move.
	 * Convert the LAN to SAN if it's necessary.
	 * @param response The JSON moves.
	 * @param fen The FEN.
	 */
	private void parseJSONMove(String response, String fen) {
		JSONArray jsonArray = new JSONArray(response);
		for(int i=0 ; i<jsonArray.length() ; i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String move = json.getString(JSON_MOVE);
			if(!this.san) {
				ChessParser parser = new ChessParser(fen);
				move = parser.convertLANToSAN(move);
			}
			double probaWin = json.getDouble(JSON_PROBA_WIN);
			int nb = json.getInt(JSON_NB_PLAY);
			double probaDraw = json.getDouble(JSON_PROBA_DRAW);
			OpeningSuggestion suggestion = new OpeningSuggestion(move, nb, probaWin, probaDraw);
			this.moves.add(suggestion);
		}
	}
	
	/**
	 * For testing (maybe another class after)
	 * @param args Unused
	 */
	public static void main(String[] args) {
		OpeningsDatabase db1 = new OpeningsDatabase("http://localhost/1.0/rest/openings/", "Db1", 1);
		db1.query("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR%20w%20KQkq%20-%200%20");
    }
}