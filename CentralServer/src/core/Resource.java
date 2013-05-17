package core;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.runtime.URIUtil;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * The model for all resources (databases and bots).
 * @author Paul Chaignon
 * @author Clement Gautrais
 * @author Benoit Travers
 */
public abstract class Resource {
	public final static int OPENINGS_DATABASE = 0;
	public final static int BOT = 1;
	public final static int ENDINGS_DATABASE = 2;
	protected final static int CONNECT_TIMEOUT = Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_CONNECT_TIMEOUT));
	protected final static int READ_TIMEOUT = Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_READ_TIMEOUT));
	protected Integer id;
	protected String uri;
	protected String name;
	protected int trust;
	protected boolean changed;
	protected boolean san;
	protected String version;
	protected boolean connected;
	protected boolean active;
	protected static final String JSON_MOVE = "move";

	/**
	 * Constructor
	 * Add a slash at the end of the URI if there isn't already one.
	 * @param uri The URI of the resource.
	 * @param name The name of the resource.
	 * @param trust The trust in the resource.
	 * @param active True if the resource is active.
	 */
	public Resource(String uri, String name, int trust, boolean active) {
		if(!uri.endsWith("/")) {
			uri += "/";
		}
		this.uri = uri;
		this.name = name;
		this.changed = false;
		this.trust = trust;
		this.id = -1;
		this.active = active;
	}

	/**
	 * @return The trust.
	 */
	public int getTrust() {
		return trust;
	}

	/**
	 * Update the trust.
	 * @param trust The new trust value.
	 */
	public void setTrust(int trust) {
		this.trust = trust;
		this.changed = true;
	}

	/**
	 * @return True if the resource was changed.
	 */
	public boolean isChanged() {
		return this.changed;
	}
	
	/**
	 * Clear the suggestions of moves.
	 */
	protected abstract void clearSuggestions();

	/**
	 * Query the resource on the network and update the suggestions of move.
	 * The resource need to send moves in a JSON document.
	 * To be send to the resource, the slashes in the FEN are replaced by dollars.
	 * Indead, slashes are a special character for URL.
	 * @param fen The FEN representing the current position of the chessboard.
	 */
	public void query(String fen) {
		this.clearSuggestions();

		// Replace the slashes in the FEN by dollars.
		String fenEncoded = fen.replaceAll("/", "\\$");

		// Convert the FEN into a url encoded string:
		try {
			fenEncoded = URIUtil.fromString(fenEncoded).toASCIIString();
		} catch (URISyntaxException e) {
			System.err.println("FEN incorrect: "+fen);
		}
		
		// Prepare the request (timeouts and URL):
		Client c = Client.create();
		System.out.println(this.uri+fenEncoded); // TODO Call listener instead.
		WebResource r = c.resource(this.uri+fenEncoded);
		c.setConnectTimeout(CONNECT_TIMEOUT);
		c.setReadTimeout(READ_TIMEOUT);
		
		// Launch the request and parse the result:
		try {
			String response = r.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
			this.parseJSONMove(response, fen);
		} catch(ClientHandlerException e) {
			// It just do nothing if the resource isn't connected.
		}
	}

	/**
	 * Parse the JSON moves to openings move.
	 * Convert the LAN to SAN if it's necessary.
	 * @param response The JSON moves.
	 * @param fen The FEN.
	 */
	protected abstract void parseJSONMove(String response, String fen);

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
	 * @return True if the resource send SAN moves.
	 */
	public boolean isSAN() {
		return this.san;
	}

	/**
	 * @return The version.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param v The version of the resource.
	 */
	public void setVersion(String v) {
		this.version = v;
	}
	
	/**
	 * @return True if the resource is active.
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Enable the resource.
	 */
	public void enable() {
		this.active = true;
	}
	
	/**
	 * Disable the resource.
	 */
	public void disable() {
		this.active = false;
	}

	/**
	 * @return True if the resource if connected.
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * @return The resource id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id The new id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Complete the version and the san parameters by calling the resource.
	 */
	public void checkVersion() {
		// Prepare the request:
		Client client = Client.create();
		WebResource webresource = client.resource(this.uri + "/version");
		client.setConnectTimeout(CONNECT_TIMEOUT);
		client.setReadTimeout(READ_TIMEOUT);
		
		// Launch the request and complete the params:
		try {
			ClientResponse clientresponse = webresource.get(ClientResponse.class);
			int status = clientresponse.getStatus();
			if(status != 200) {
				this.connected = false;
			} else {
				this.connected = true;
				String response = clientresponse.getEntity(String.class);
				this.san = ('s' == response.charAt(response.length()-1));
				this.version = response.substring(0, response.length()-1);
			}
		} catch(ClientHandlerException e) {
			this.connected = false;
		}
	}

	/**
	 * @return The suggestions of move.
	 */
	public abstract List<? extends MoveSuggestion> getMoveSuggestions();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null)? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Resource)) {
			return false;
		}
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}