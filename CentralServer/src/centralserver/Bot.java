package centralserver;

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
	 */
	public Bot(String uri, String name) {
		super(uri, name);
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