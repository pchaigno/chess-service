package core;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * The model for all resources (databases and bots).
 */
public abstract class Resource {
	public final static int DATABASE = 0;
	public final static int BOT = 1;
	protected final static int CONNECT_TIMEOUT = Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_CONNECT_TIMEOUT));
	protected final static int READ_TIMEOUT = Integer.parseInt(PropertiesManager.getProperty(PropertiesManager.PROPERTY_READ_TIMEOUT));
	protected String uri;
	protected String name;
	protected int trust;
	protected boolean changed;
	protected boolean san;
	protected String version;
	protected boolean connected;

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

	public boolean isSAN() {
		return this.san;
	}

	/**
	 * @return The version
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
	 * @return Connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Complete the version and the san parameters.
	 */
	public void checkVersion() {
		Client client = Client.create();
		String tmp_uri = this.uri.substring(0,this.uri.lastIndexOf("/"));
		tmp_uri = tmp_uri.substring(0,tmp_uri.lastIndexOf("/")) + "/version";
		WebResource webresource = client.resource(tmp_uri);
		client.setConnectTimeout(CONNECT_TIMEOUT);
		client.setReadTimeout(READ_TIMEOUT);
		ClientResponse clientresponse = webresource.get(ClientResponse.class);
		int status = clientresponse.getStatus();
		if(status == 408) {
			connected = false;
		}else{
			connected = true;
			String response = clientresponse.getEntity(String.class);
			this.san = ('s' == response.charAt(response.length()-1));
			this.version = response.substring(0, response.length()-1);
		}
	}

	/**
	 * @return The suggestions of move.
	 */
	public abstract List<? extends MoveSuggestion> getMoveSuggestions();

	/**
	 * Query the resource on the network and update the suggestions of move.
	 * @param fen The FEN representing the current position of the chessboard.
	 */
	public abstract void query(String fen);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}
}