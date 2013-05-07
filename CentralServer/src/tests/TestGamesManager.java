package tests;

import java.util.ArrayList;
import java.util.HashSet;

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
		HashSet<Integer> resourcesMove1 = new HashSet<Integer>();
		resourcesMove1.add(1);
		resourcesMove1.add(2);
		
		HashSet<Integer> resourcesMove2 = new HashSet<Integer>();
		resourcesMove2.add(1);
		
		HashSet<Integer> resourcesMove3 = new HashSet<Integer>();
		resourcesMove3.add(1);
		resourcesMove3.add(2);
		
		HashSet<Integer> resourcesMove4 = new HashSet<Integer>();
		resourcesMove4.add(3);
		
		/*assertTrue(GamesManager.addMove(games.get(0), resourcesMove1, 1));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove2, 2));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove3, 3));
		assertTrue(GamesManager.addMove(games.get(0), resourcesMove4, 4));*/
		
		assertTrue(GamesManager.updateGame(games.get(0), "fen3coups"));
		
		assertTrue(GamesManager.getResourcesStats(games.get(0)).get(1)==0.75);
		assertTrue(GamesManager.getResourcesStats(games.get(0)).get(2)==0.5);
		
		assertTrue(GamesManager.removeGame(games.get(0)));
	}
	
	public void testForeignKeys() {
		// TODO Add foreign keys tests.
	}
}