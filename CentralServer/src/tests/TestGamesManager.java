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

	public void test() {
		ArrayList<Integer> games = new ArrayList<Integer>();
		
		games.add(GamesManager.addNewGame(true));
		assertTrue(games.get(0)>0);
	
		//TODO load 3 resources (id 1, 2, 3)
		Map<Integer, Double> resourcesMove1 = new HashMap<Integer, Double>();
		resourcesMove1.put(1, (double)1);
		
		Map<Integer, Double> resourcesMove2 = new HashMap<Integer, Double>();
		resourcesMove2.put(1, (double)1);
		
		Map<Integer, Double> resourcesMove3 = new HashMap<Integer, Double>();
		resourcesMove3.put(2, (double)1);
		resourcesMove3.put(3, 0.7);
		
		Map<Integer, Double> resourcesMove4 = new HashMap<Integer, Double>();
		resourcesMove4.put(1, (double)1);
		resourcesMove4.put(3, 0.4);
		
		assertTrue(GamesManager.addMoves(games.get(0), resourcesMove1, 1));
		assertTrue(GamesManager.addMoves(games.get(0), resourcesMove2, 2));
		assertTrue(GamesManager.addMoves(games.get(0), resourcesMove3, 3));
		assertTrue(GamesManager.addMoves(games.get(0), resourcesMove4, 4));
		
		assertTrue(GamesManager.updateGame(games.get(0), "fen3coups"));
		
		assertTrue(GamesManager.getResourcesInvolvement(games.get(0)).get(1)==3);
		assertTrue(GamesManager.getResourcesInvolvement(games.get(0)).get(3)==1.1);
		
		assertTrue(GamesManager.removeGame(games.get(0)));
	}
	
	public void testForeignKeys() {
		// TODO Add foreign keys tests.
	}
}