package centralserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage the list of resources.
 */
public class CentralServer {
	private List<Resource> resources;
	private boolean resources_changed;
	private static final String RESOURCES_FILE = "resources.txt";
	
	/**
	 * Constructor
	 */
	public CentralServer() {
		this.resources = new ArrayList<Resource>();
		this.restoreResources();
		this.resources_changed = false;
	}
	
	/**
	 * Restore the list of resources from a file (or a database file ?).
	 */
	private void restoreResources() {
		// TODO Read the list of resources from a file.
	}
	
	/**
	 * Save the list of resources in a file if there was changes.
	 */
	private void saveResources() {
		if(this.resources_changed) {
			// TODO Write the resources in a file.
		}
	}
	
	/**
	 * Get the suggestion of move from the resources and compute the best answer.
	 * @param fen The FEN.
	 * @return The best suggestion of move.
	 */
	public MoveSuggestion getBestMove(String fen) {
		this.updateResources(fen);
		// TODO Compute the best answer from all the answers from all resources.
		return null;
	}
	
	/**
	 * Ask for all resources to update their suggestions of move.
	 * Do it using multithreading.
	 * @param fen The FEN.
	 */
	private void updateResources(String fen) {
		// TODO Use Multithreading.
		for(Resource resource: this.resources) {
			resource.query(fen);
		}
	}
}
