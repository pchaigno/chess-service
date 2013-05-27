package core;

/**
 * An abstract adapter class for receiving resource events.
 * The methods in this class are empty.
 * This class exists as convenience for creating listener objects.
 * @author Paul Chaignon
 */
public class ResourceAdapter implements ResourceListener {

	@Override
	public void onQueryRequest(Resource resource, String fen) {}

	@Override
	public void onSuggestionsReceived(Resource resource, String suggestions) {}

	@Override
	public void onSuggestionsUpdated(Resource resource) {}

	@Override
	public void onVersionRequest(Resource resource) {}

	@Override
	public void onVersionReceived(Resource resource, int status, String response) {}

	@Override
	public void onVersionUpdated(Resource resource) {}
}