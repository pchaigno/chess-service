package centralserver;

/**
 * Represent a database.
 */
public class Database extends Resource {
	private int trust;
	
	/**
	 * Constructor
	 * @param uri The URI.
	 * @param name The name.
	 * @param trust The trust in this database.
	 */
	public Database(String uri, String name, int trust) {
		super(uri, name);
		this.trust = trust;
	}
	
	/**
	 * @return The trust.
	 */
	public int getTrust() {
		return this.trust;
	}
	
	/**
	 * Update the trust.
	 * @param trust The new trust
	 */
	public void setTrust(int trust) {
		this.trust = trust;
	}
}