package tests;

import junit.framework.TestCase;
import core.Bot;
import core.OpeningsDatabase;
import core.Resource;

/**
 * Unit tests for Resource.
 * @author Paul Chaignon
 */
public class TestResource extends TestCase {

	/**
	 * Test the equals method.
	 */
	public void testEquals() {
		Resource bot1 = new Bot("test12.com", "TestBot", 50);
		bot1.setId(1);
		Bot bot2 = new Bot("test123.com", "Test Bot", 70);
		bot2.setId(1);
		Resource bot3 = new Bot("test12.com", "Test Bot", 70);
		bot3.setId(2);
		assertEquals(bot1, bot2);
		assertFalse(bot3.equals(bot1));
		
		Resource database1 = new OpeningsDatabase("test1234.com", "TestDatabase", 60);
		database1.setId(1);
		OpeningsDatabase database2 = new OpeningsDatabase("test1.com", "Test Database", 70);
		database2.setId(1);
		Resource database3 = new OpeningsDatabase("test12.com", "Test Database", 70);
		//database3.setId(3);
		assertTrue(bot1.equals(database1));
		assertEquals(database1, database2);
		assertFalse(database3.equals(database1));
	}
}