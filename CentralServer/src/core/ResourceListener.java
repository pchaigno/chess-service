package core;

import java.util.EventListener;

/**
 * The listener interface for receiving events from the resources.
 * A resource event is generated when a request is sent to the resource or when a response is received.
 * @author Paul Chaignon
 */
public interface ResourceListener extends EventListener {

	/**
	 * Called when a query has been sent to the resource to get a suggestions list.
	 * @param resource The resource.
	 * @param fen The FEN sent.
	 */
	public void onQueryRequest(Resource resource, String fen);
	
	/**
	 * Called when move suggestions have been received from the resource.
	 * @param resource The resource.
	 * @param suggestions The suggestions received as a JSON document.
	 */
	public void onSuggestionsReceived(Resource resource, String suggestions);
	
	/**
	 * Called when move suggestions have been added to the resource.
	 * Happens just after the parsing from JSON.
	 * @param resource The resource whose move suggestions have been updated.
	 */
	public void onSuggestionsUpdated(Resource resource);
	
	/**
	 * Called when a version request has been sent to the resource to know its version.
	 * @param resource The resource.
	 */
	public void onVersionRequest(Resource resource);
	
	/**
	 * Called when a response has been received after a version request.
	 * @param resource The resource.
	 * @param status The response status, an HTTP code.
	 * @param response The response body: the version number.
	 */
	public void onVersionReceived(Resource resource, int status, String response);
	
	/**
	 * Called when the version of the resource has been updated.
	 * Concerns the version and san parameters.
	 * @param resource The resource.
	 */
	public void onVersionUpdated(Resource resource);
}