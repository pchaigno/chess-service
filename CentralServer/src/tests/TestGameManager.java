package tests;

import java.util.ArrayList;

import core.GamesManager;

import junit.framework.TestCase;

public class TestGameManager extends TestCase {

	public void test() {
		ArrayList<Integer> games = new ArrayList<Integer>();
		games.add(GamesManager.addNewGame());
		
		GamesManager.addMove(games.get(0), 1, "e4", 1);
		GamesManager.addMove(games.get(0), 1, "e5", 2);
		GamesManager.addMove(games.get(0), 1, "e6", 3);
		GamesManager.addMove(games.get(0), 2, "e4", 1);
		GamesManager.addMove(games.get(0), 2, "d4", 2);
		
		GamesManager.updateGame(games.get(0), "fen3coups", 3);
		
		System.out.println(GamesManager.getResourcesStats(games.get(0)).toString());
	}
}
