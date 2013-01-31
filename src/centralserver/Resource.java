package centralserver;

/**
 * The model for all resources (databases and bots).
 */
public abstract class Resource {
	private String uri;
	private String name;
	
	/**
	 * Constructor
	 * @param uri The URI of the resource.
	 * @param name The name of the resource.
	 */
	public Resource(String uri, String name) {
		this.uri = uri;
		this.name = name;
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
}