package core;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Represent a database.
 */
public class Database extends Resource {
	private List<OpeningSuggestion> moves;
	
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
	public List<OpeningSuggestion> getMoveSuggestions() {
		return this.moves;
	}

	@Override
	public void query(String fen) {
		this.moves = new ArrayList<OpeningSuggestion>();
		
		// We call the client
		Client c = Client.create();
		// TODO handle the last slash
		WebResource r = c.resource(this.uri+fen);
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(READ_TIMEOUT);
		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		
		// We parse the JSON response with Gson:
		// TODO Convert LAN to SAN.
		Gson gson = new Gson();
		Type collectionType = new TypeToken<ArrayList<OpeningSuggestion>>(){}.getType();
		this.moves = gson.fromJson(response, collectionType);	
	}
	
	/**
	 * For testing (maybe another class after)
	 * @param args Unused
	 */
	public static void main(String[] args) {
		Database db1 = new Database("http://localhost/1.0/rest/openings/", "Db1", 1);
		db1.query("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR%20w%20KQkq%20-%200%20");
    }
}