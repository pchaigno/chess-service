package tests;

import parser.ChessParser;
import junit.framework.TestCase;

public class TestParser extends TestCase {

	/**
	 * Test a few conversions from LAN to SAN or the inverse.
	 */
	public void testNotationsParser() {
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "b1c3");
		this.testSANToLAN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3", "Nf6");
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d2d4");
	}
	
	/**
	 * Test a few FEN parsing.
	 */
	public void testFENParser() {
		this.testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
		this.testFEN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3");
		this.testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
		this.testFEN("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -");
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
	
	/**
	 * Give the FEN to the chess parser and retake it to compare.
	 * @param fen The FEN.
	 */
	private void testFEN(String fen) {
		ChessParser parser = new ChessParser(fen);
		assertEquals(fen, parser.getFen(false));
	}
}