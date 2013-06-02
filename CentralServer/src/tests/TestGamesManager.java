package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.GamesManager;

import junit.framework.TestCase;

/**
 * Unit tests for GamesManager.
 * @author Clement Gautrais
 */
public class TestGamesManager extends TestCase {

	/**
	 * General tests for GamesManager.
	 */
	public static void test() {
		ArrayList<Integer> games = new ArrayList<Integer>();
		
		games.add(GamesManager.addNewGame(true));
		assertTrue(games.get(0)>0);
	
		Map<Integer, Double> resourcesMove1 = new HashMap<Integer, Double>();
		resourcesMove1.put(1, 1.0);
		
		Map<Integer, Double> resourcesMove2 = new HashMap<Integer, Double>();
		resourcesMove2.put(1, 1.0);
		
		Map<Integer, Double> resourcesMove3 = new HashMap<Integer, Double>();
		resourcesMove3.put(2, 1.0);
		resourcesMove3.put(3, 0.7);
		
		Map<Integer, Double> resourcesMove4 = new HashMap<Integer, Double>();
		resourcesMove4.put(1, 1.0);
		resourcesMove4.put(3, 0.4);
		
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove1, 1));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove2, 2));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove3, 3));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove4, 4));
		
		assertTrue(GamesManager.updateGame(games.get(0), "fen3coups"));
		
		assertTrue(GamesManager.getResourceInvolvements(games.get(0)).get(1)==3);
		assertTrue(GamesManager.getResourceInvolvements(games.get(0)).get(3)==1.1);
		
		assertTrue(GamesManager.removeGame(games.get(0)));
	}
}