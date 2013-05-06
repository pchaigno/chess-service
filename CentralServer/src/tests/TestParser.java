package tests;

import parser.ChessParser;
import junit.framework.TestCase;

public class TestParser extends TestCase {

	public void testNotationsParser() {
		String fen = "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
		// LAN from 127.0.0.1/rest/openings/rnbqkbnr$pppp1ppp$4p3$8$4P3$8$PPPP1PPP$RNBQKBNR w KQkq - 0 2
		String lan = "b1c3";
		ChessParser parser = new ChessParser(fen);
		String san = parser.convertLANToSAN(lan);
		assertEquals(lan, parser.convertSANToLAN(san));
		
		fen = "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3";
		san = "Nf6";
		parser = new ChessParser(fen);
		lan = parser.convertSANToLAN(san);
		assertEquals(san, parser.convertLANToSAN(lan));
	}
}
