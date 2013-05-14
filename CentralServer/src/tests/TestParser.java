package tests;

import parser.ChessParser;
import parser.IncorrectFENException;
import junit.framework.TestCase;

/**
 * Unit test for ChessParser.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
public class TestParser extends TestCase {

	/**
	 * Test conversions from LAN to SAN.
	 */
	public void testSANParser() {
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "b1c3");
		this.testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d2d4");
		this.testLANToSAN("rnbqkbnr/ppp1pp1p/6p1/3p4/5P2/3P1N2/PPP1P1PP/RNBQKB1R b KQkq - 1 3", "f8g7", "Bg7");
		this.testLANToSAN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3", "0-0", "O-O");
	}
	
	/**
	 * Test conversions from SAN to LAN.
	 */
	public void testLANParser() {
		this.testSANToLAN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3", "Nf6");
		this.testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d4");
		this.testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d3");
		this.testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Nf3");
		this.testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Qe2", "d1e2");
		this.testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Nc3");
		this.testSANToLAN("rnbqkbnr/pp1ppppp/2p5/8/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq - 0 2", "d5");
		this.testSANToLAN("rnbqkbnr/ppp1pp1p/6p1/3p4/5P2/3P1N2/PPP1P1PP/RNBQKB1R b KQkq - 1 3", "Bg7");
		this.testSANToLAN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3", "O-O", "0-0");
	}
	
	/**
	 * Test a few FEN parsing.
	 */
	public void testFENParser() {
		this.testFEN("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -");
		this.testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
		this.testFEN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3");
		this.testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
	}
	
	public void testGetColor() throws IncorrectFENException {
		assertEquals('w', ChessParser.getColor("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2"));
		assertEquals('b', ChessParser.getColor("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3"));
		assertEquals('b', ChessParser.getColor("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -"));
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
	 * Test a conversion from SAN to LAN.
	 * @param fen The FEN.
	 * @param san The Short Algebraic Notation.
	 * @param lan The Long Algebraic Notation.
	 */
	private void testSANToLAN(String fen, String san, String lan) {
		ChessParser parser = new ChessParser(fen);
		assertEquals(lan, parser.convertSANToLAN(san));
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
	 * Test a conversion from LAN to SAN.
	 * @param fen The FEN.
	 * @param lan The Long Algebraic Notation.
	 * @param san The Short Algebraic Notation.
	 */
	private void testLANToSAN(String fen, String lan, String san) {
		ChessParser parser = new ChessParser(fen);
		assertEquals(san, parser.convertLANToSAN(lan));
	}
	
	/**
	 * Give the FEN to the chess parser and retake it to compare.
	 * @param fen The FEN.
	 */
	private void testFEN(String fen) {
		ChessParser parser = new ChessParser(fen);
		assertEquals(fen, parser.getFEN(false));
	}
}