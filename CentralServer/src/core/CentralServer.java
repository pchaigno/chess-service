package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manage the list of resources.
 */
public class CentralServer {
	private Set<Resource> resources;
	private static final String version = "1.0";

	/**
	 * Constructor
	 */
	public CentralServer() {
		this.resources = new HashSet<Resource>();
		this.restoreResources();
		this.checkVersion();
	}

	/**
	 * Restore the list of resources from the database file.
	 */
	private void restoreResources() {
		this.resources = ResourcesManager.getResources();
	}

	/**
	 * Check resources version.
	 */
	private void checkVersion() {
		String centralserveur_version = CentralServer.version.substring(0, CentralServer.version.indexOf('.'));
		Set<Resource> incompatibleResources = new HashSet<Resource>();

		for(Resource resource: this.resources) {
			resource.checkVersion();
			String resource_version = resource.getVersion().substring(0, resource.getVersion().indexOf("."));

			if(!resource.isConnected()) {
				incompatibleResources.add(resource);
			} else {
				if(!centralserveur_version.equals(resource_version)) {
					incompatibleResources.add(resource);
				}
			}
		}

		// TODO Save incompatible resources.

		for(Resource resource: incompatibleResources) {
			this.resources.remove(resource);
		}
	}

	/**
	 * save a resource in the corresponding table
	 */
	public void saveResourcesTrust(Set<Resource> resources) {
		ResourcesManager.updateResourcesTrust(resources);	
	}

	/**
	 * Get the suggestion of move from the resources and compute the best answer.
	 * @param fen The FEN.
	 * @return The best move or null if no suggestion.
	 */
	public String getBestMove(String fen) {
		this.updateResources(fen);
		// This map contains all the moves and the scores associated except the ending moves.
		Map<String, Double> scores = new HashMap<String, Double>();
		// This map contains the ending moves with their scores.
		Map<String, Double> ends = new HashMap<String, Double>();

		for(Resource resource : this.resources) {
			for(MoveSuggestion move : resource.getMoveSuggestions()) {
				if(move.getClass()==EndingSuggestion.class) {
					ends.put(move.getMove(), move.getScore());	
				} else {
					if(scores.containsKey(move.getMove())) {
						double newScore = scores.get(move) + resource.getTrust()*move.getScore();
						scores.put(move.getMove(), newScore);
					}
				}
			}
		}
		return this.bestMove(scores, ends);
	}

	/**
	 * Get the best move by comparing the scores among all moves suggested.
	 * @param moves The map containing all the moves and the scores associated.
	 * @param ends The map containing all the ending moves and their scores.
	 * @return The best move among all moves or null if no suggestion.
	 */
	private String bestMove(Map<String, Double> moves, Map<String, Double> ends) {
		String move = null;
		if(ends.size()>0) {
			double min = Double.MAX_VALUE;
			for(Map.Entry<String, Double> entry: ends.entrySet()) {
				if(entry.getValue() < min) {
					min = entry.getValue();
					move = entry.getKey();
				}
			}
			if(min > 0) {
				// Use an end suggestion only if we'll win.
				return move;
			}
		}
		double max = Double.MIN_VALUE;
		move = null;
		for(Map.Entry<String, Double> entry: moves.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				move = entry.getKey();
			}
		}
		return move;
	}

	/**
	 * Ask for all resources to update their suggestions of move.
	 * Do it using multithreading.
	 * Wait for the end of all updates.
	 * @param fen The FEN.
	 */
	private void updateResources(final String fen) {
		Set<Thread> threads = new HashSet<Thread>();
		for(final Resource resource: this.resources) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					resource.query(fen);
				}
			});
			thread.start();
			threads.add(thread);
		}
		for(Thread thread: threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// Shouldn't happen.
				System.err.println("The thread was interrupted: "+e.getMessage());
			}
		}
	}
}