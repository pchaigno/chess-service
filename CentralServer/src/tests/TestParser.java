package tests;

import core.PlayerColor;
import parser.ChessParser;
import parser.IncorrectAlgebraicNotationException;
import parser.IncorrectFENException;
import junit.framework.TestCase;

/**
 * Unit tests for ChessParser.
 * @author Clement Gautrais
 * @author Paul Chaignon
 */
public class TestParser extends TestCase {

	/**
	 * Test conversions from LAN to SAN.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	public static void testSANParser() throws IncorrectFENException, IncorrectAlgebraicNotationException {
		testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "b1c3");
		testLANToSAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d2d4");
		testLANToSAN("rnbqkbnr/ppp1pp1p/6p1/3p4/5P2/3P1N2/PPP1P1PP/RNBQKB1R b KQkq - 1 3", "f8g7", "Bg7");
		testLANToSAN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3", "O-O", "O-O");
	}
	
	/**
	 * Test conversions from SAN to LAN.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	public static void testLANParser() throws IncorrectFENException, IncorrectAlgebraicNotationException {
		testSANToLAN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3", "Nf6");
		testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d4");
		testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "d3");
		testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Nf3");
		testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Qe2", "d1e2");
		testSANToLAN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", "Nc3");
		testSANToLAN("rnbqkbnr/pp1ppppp/2p5/8/2P1P3/8/PP1P1PPP/RNBQKBNR b KQkq - 0 2", "d5");
		testSANToLAN("rnbqkbnr/ppp1pp1p/6p1/3p4/5P2/3P1N2/PPP1P1PP/RNBQKB1R b KQkq - 1 3", "Bg7");
		testSANToLAN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3", "O-O", "O-O");
	}
	
	/**
	 * Test a few FEN parsing.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	public static void testFENParser() throws IncorrectFENException {
		testFEN("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -");
		testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
		testFEN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3");
		testFEN("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
	}
	
	/**
	 * Test the result color.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	public static void testGetColor() throws IncorrectFENException {
		assertEquals(PlayerColor.WHITE, ChessParser.getColor("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2"));
		assertEquals(PlayerColor.BLACK, ChessParser.getColor("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3"));
		assertEquals(PlayerColor.BLACK, ChessParser.getColor("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -"));
	}
	
	/**
	 * Test if the FEN structure is correct.
	 */
	public static void testIsCorrectFEN() {
		assertTrue(ChessParser.isCorrectFEN("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3"));
		assertTrue(ChessParser.isCorrectFEN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 1 3"));
		assertFalse(ChessParser.isCorrectFEN("rnbqkbnr/pp2pppp/3p4/1Bp5/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq"));
	}
	
	/**
	 * Test if the current color is check.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	public static void testCheck() throws IncorrectFENException {
		ChessParser parser0 = new ChessParser("k7/8/1Q2n3/2K5/8/8/8/8 w - - 0 1"); // white king is in check
		ChessParser parser1 = new ChessParser("k7/8/1Q6/2K5/8/8/8/8 b - - 0 1"); // black are stalemate
		ChessParser parser2 = new ChessParser("k7/8/P1N3p1/2K3Pp/7P/8/8/8 b - - 0 1"); // black are stalemate
		ChessParser parser3 = new ChessParser("8/r7/8/K1k5/5b2/8/8/8 w - - 0 1"); // white lose
		assertTrue(parser0.check(true));
		assertFalse(parser1.check(false));
		assertFalse(parser2.check(false));
		assertTrue(parser3.check(true));
	}
	
	/**
	 * Test a conversion from SAN to LAN.
	 * @param fen The FEN.
	 * @param san The Short Algebraic Notation.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	private static void testSANToLAN(String fen, String san) throws IncorrectFENException, IncorrectAlgebraicNotationException {
		ChessParser parser = new ChessParser(fen);
		String lan = parser.convertSANToLAN(san);
		assertEquals(san, parser.convertLANToSAN(lan));
	}
	
	/**
	 * Test a conversion from SAN to LAN.
	 * @param fen The FEN.
	 * @param san The Short Algebraic Notation.
	 * @param lan The Long Algebraic Notation.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	private static void testSANToLAN(String fen, String san, String lan) throws IncorrectFENException, IncorrectAlgebraicNotationException {
		ChessParser parser = new ChessParser(fen);
		assertEquals(lan, parser.convertSANToLAN(san));
	}
	
	/**
	 * Test a conversion from LAN to SAN.
	 * @param fen The FEN.
	 * @param lan The Long Algebraic Notation.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	private static void testLANToSAN(String fen, String lan) throws IncorrectFENException, IncorrectAlgebraicNotationException {
		ChessParser parser = new ChessParser(fen);
		String san = parser.convertLANToSAN(lan);
		assertEquals(lan, parser.convertSANToLAN(san));
	}
	
	/**
	 * Test a conversion from LAN to SAN.
	 * @param fen The FEN.
	 * @param lan The Long Algebraic Notation.
	 * @param san The Short Algebraic Notation.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 * @throws IncorrectAlgebraicNotationException If the algebraic notation is incorrect.
	 */
	private static void testLANToSAN(String fen, String lan, String san) throws IncorrectFENException, IncorrectAlgebraicNotationException {
		ChessParser parser = new ChessParser(fen);
		assertEquals(san, parser.convertLANToSAN(lan));
	}
	
	/**
	 * Give the FEN to the chess parser and retake it to compare.
	 * @param fen The FEN.
	 * @throws IncorrectFENException If the FEN is incorrect.
	 */
	private static void testFEN(String fen) throws IncorrectFENException {
		ChessParser parser = new ChessParser(fen);
		assertEquals(fen, parser.getFEN(false));
	}
	
}