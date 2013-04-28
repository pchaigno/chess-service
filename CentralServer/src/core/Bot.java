package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a bot.
 */
public class Bot extends Resource {
	private List<BotSuggestion> moves;
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this bot.
	 */
	public Bot(String uri, String name, int trust) {
		super(uri, name, trust);
	}

	@Override
	public List<BotSuggestion> getMoveSuggestions() {
		return this.moves;
	}

	@Override
	public void query(String fen) {
		this.moves = new ArrayList<BotSuggestion>();
		// TODO Query the bot and update this.moves with the reponses.
	}
}