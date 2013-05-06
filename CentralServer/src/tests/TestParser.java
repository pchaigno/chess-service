package tests;

import parser.ChessParser;
import junit.framework.TestCase;

public class TestParser extends TestCase {

	public void testNotationsParser() {
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "b1c3");
		
		this.testSANToLAN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3", "Nf6");
		
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d2d4");
	}
	
	/**
	 * Test a conversion from SAN to LAN.
	 * @param fen The FEN.
	 * @param san The Short Algebraic Notation.
	 */
	private void testSANToLAN(String fen, String san) {
		ChessParser parser = new ChessParser(fen);
		String lan = parser.convertSANToLAN(san);
		assertEquals(san, parser.convertLANToSAN(lan));
	}
	
	/**
	 * Test a conversion from LAN to SAN.
	 * @param fen The FEN.
	 * @param lan The Long Algebraic Notation.
	 */
	private void testLANToSAN(String fen, String lan) {
		ChessParser parser = new ChessParser(fen);
		String san = parser.convertLANToSAN(lan);
		assertEquals(lan, parser.convertSANToLAN(san));
	}
}