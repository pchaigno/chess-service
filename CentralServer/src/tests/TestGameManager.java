package tests;

import java.util.ArrayList;

import core.GameManager;

import junit.framework.TestCase;

public class TestGameManager extends TestCase {

	public void test() {
		ArrayList<Integer> games = new ArrayList<Integer>();
		games.add(GameManager.addNewGame());
		
		GameManager.addMove(games.get(0), 1, "e4", 1);
		GameManager.addMove(games.get(0), 1, "e5", 2);
		GameManager.addMove(games.get(0), 1, "e6", 3);
		GameManager.addMove(games.get(0), 2, "e4", 1);
		GameManager.addMove(games.get(0), 2, "d4", 2);
		
		GameManager.updateGame(games.get(0), "fen3coups", 3);
		
		System.out.println(GameManager.getResourcesStats(games.get(0)).toString());
	}
}
