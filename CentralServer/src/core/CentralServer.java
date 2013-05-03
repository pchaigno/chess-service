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

		for(Resource resource : incompatibleResources) {
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
	 * @return The best suggestion of move or null if no suggestion.
	 */
	public MoveSuggestion getBestMove(String fen) {
		this.updateResources(fen);
		// The hashMap contains all the moves and the score associated 
		Map<MoveSuggestion, Double> moves = new HashMap<MoveSuggestion, Double>();

		for(Resource resource : this.resources) {
			for(MoveSuggestion move : resource.getMoveSuggestions()) {
				if(move.getClass()==OpeningSuggestion.class) {
					double moveScore = computeScoreDatabase((OpeningSuggestion)move, resource);
					this.includeScore(moves, move, moveScore);
				}
			}
		}
		return bestMove(moves);
	}

	/**
	 * TODO
	 * @param moves TODO
	 * @return The best move (with the highest score) among all moves or null if no suggestion.
	 */
	private MoveSuggestion bestMove(Map<MoveSuggestion, Double> moves) {
		double max = -1;
		MoveSuggestion move = null;

		for(Map.Entry<MoveSuggestion, Double> entry : moves.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				move = entry.getKey();
			}
		}
		return move;
	}


	/**
	 * Include the move in the HashMap:
	 * if the move already exist, we add the score
	 * otherwise we create a new one in the HashMap
	 * @param moves TODO
	 * @param move TODO
	 * @param moveScore TODO
	 */
	private void includeScore(Map<MoveSuggestion, Double> moves, MoveSuggestion move, double moveScore) {
		if(moves.containsKey(move.getMove())) {
			moves.put(move, moves.get(move)+moveScore);
		} else {
			moves.put(move, moveScore);
		}
	}

	/**
	 * TODO
	 * @param move TODO
	 * @param resource TODO
	 * @return The score computed according to the formulas we defined
	 */
	private double computeScoreDatabase(OpeningSuggestion move, Resource resource) {
		// TODO change the formula
		return move.getProbaWin()*move.getNbPlay()*resource.getTrust();
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