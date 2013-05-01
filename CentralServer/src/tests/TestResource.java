package tests;

import junit.framework.TestCase;
import core.Bot;
import core.Database;
import core.Resource;

/**
 * Test Resource.
 */
public class TestResource extends TestCase {

	/**
	 * Test the equals method.
	 */
	public void testEquals() {
		Resource bot1 = new Bot("test123.com", "TestBot", 50);
		Bot bot2 = new Bot("test123.com", "Test Bot", 70);
		Resource bot3 = new Bot("test12.com", "Test Bot", 70);
		assertEquals(bot1, bot2);
		assertFalse(bot3.equals(bot1));
		
		Resource database1 = new Database("test123.com", "TestDatabase", 50);
		Database database2 = new Database("test123.com", "Test Database", 70);
		Resource database3 = new Database("test12.com", "Test Database", 70);
		assertTrue(bot1.equals(database1));
		assertEquals(database1, database2);
		assertFalse(database3.equals(database1));
	}
}
