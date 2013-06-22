package core;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import parser.ChessParser;
import parser.IncorrectAlgebraicNotationException;
import parser.IncorrectFENException;

/**
 * Represent a chess engine.
 * The chess engine used for now is Crafty.
 * It returns a score and a computation depth.
 * A bot contains BotSuggestion as move suggestions.
 * @author Paul Chaignon
 */
public class Bot extends Resource {
	private List<BotSuggestion> moves;
	protected static final String JSON_DEPTH = "depth";
	protected static final String JSON_ENGINE_SCORE = "score";
	
	/**
	 * Constructor
	 * Initialize the bot with default trust.
	 * @param uri The URI.
	 * @param name The name.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public Bot(String uri, String name, boolean active, int id) {
		super(uri, name, DEFAULT_TRUST, active, id);
		this.moves = new LinkedList<BotSuggestion>();
	}
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this bot.
	 * @param active True if the resource is active.
	 * @param id The resource id.
	 */
	public Bot(String uri, String name, int trust, boolean active, int id) {
		super(uri, name, trust, active, id);
		this.moves = new LinkedList<BotSuggestion>();
	}

	@Override
	public List<BotSuggestion> getMoveSuggestions() {
		return this.moves;
	}

	@Override
	protected void clearSuggestions() {
		this.moves.clear();
	}

	@Override
	protected void parseJSONMove(String response, String fen) {
		JSONArray jsonArray = new JSONArray(response);
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String move = json.getString(JSON_MOVE);
			if(!this.san) {
				ChessParser parser;
				try {
					parser = new ChessParser(fen);
					move = parser.convertLANToSAN(move);
				} catch(IncorrectFENException e) {
					System.err.println("parseJSONMove: "+e.getMessage());
					continue;
				} catch(IncorrectAlgebraicNotationException e) {
					System.err.println("parseJSONMove: "+e.getMessage());
					continue;
				}
			}
			int depth = json.getInt(JSON_DEPTH);
			double engineScore = json.getDouble(JSON_ENGINE_SCORE);
			BotSuggestion suggestion = new BotSuggestion(move, depth, engineScore);
			this.moves.add(suggestion);
		}
	}
}