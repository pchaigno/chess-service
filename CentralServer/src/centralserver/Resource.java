package centralserver;

import java.util.List;

/**
 * The model for all resources (databases and bots).
 * Note: Was forced to use a generic class because apparently getMoveSuggestions in Database and Bot can't
 * override if the return type is List<MoveSuggestion>. Test on your side.
 */
public abstract class Resource {
	protected String uri;
	protected String name;
	protected int trust;
	protected boolean changed;
	
	/**
	 * Constructor
	 * @param uri The URI of the resource.
	 * @param name The name of the resource.
	 * @param trust The trust in the resource.
	 */
	public Resource(String uri, String name, int trust) {
		this.uri = uri;
		this.name = name;
		this.changed = false;
		this.trust = trust;
	}
	
	public int getTrust() {
		return trust;
	}

	public void setTrust(int trust) {
		this.trust = trust;
		this.changed = true;
	}

	public boolean isChanged() {
		return changed;
	}

	/**
	 * @return The URI.
	 */
	public String getURI() {
		return this.uri;
	}
	
	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The suggestions of move.
	 */
	public abstract List<? extends MoveSuggestion> getMoveSuggestions();
	
	/**
	 * Query the resource on the network and update the suggestions of move.
	 * @param fen The FEN reprensenting the current position of the chessboard.
	 */
	public abstract void query(String fen);
}