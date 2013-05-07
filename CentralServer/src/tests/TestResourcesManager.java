package tests;

import java.util.HashSet;
import java.util.Set;

import core.Bot;
import core.OpeningsDatabase;
import core.Resource;
import core.ResourcesManager;
import junit.framework.TestCase;

/**
 * Unit tests for ResourcesManager.
 * @author Paul Chaignon
 */
public class TestResourcesManager extends TestCase {

	/**
	 * Test the methods of ResourcesManager.
	 */
	public void test() {
		// Backup to compare after.
		Set<Resource> oldResources = ResourcesManager.getResources();

		// Add a new bot:
		Resource bot = new Bot("test123.com", "TestBot", 50);
		ResourcesManager.addResource(bot);
		Set<Resource> resources = ResourcesManager.getResources();
		assertTrue(resources.containsAll(oldResources));
		assertTrue(resources.contains(bot));
		
		// Add a new database:
		Resource database = new OpeningsDatabase("test321.com", "TestDatabase", 60);
		ResourcesManager.addResource(database);
		resources = ResourcesManager.getResources();
		assertTrue(resources.containsAll(oldResources));
		assertTrue(resources.contains(bot));
		assertTrue(resources.contains(database));
		
		// Update the bot:
		bot = new Bot("test123.com", "Test Bot", 10);
		ResourcesManager.updateResource(bot);
		resources = ResourcesManager.getResources();
		assertTrue(resources.contains(bot));
		boolean found = false;
		for(Resource resource: resources) {
			if(resource.equals(bot) && resource.getClass().equals(bot.getClass()) && resource.getName().equals(bot.getName()) && resource.getTrust()==bot.getTrust()) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		// Update the database:
		database = new OpeningsDatabase("test321.com", "Test Database", 0);
		ResourcesManager.updateResource(database);
		resources = ResourcesManager.getResources();
		assertTrue(resources.contains(database));
		found = false;
		for(Resource resource: resources) {
			if(resource.equals(database) && resource.getClass().equals(database.getClass()) && resource.getName().equals(database.getName()) && resource.getTrust()==database.getTrust()) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		// Update the resources' trust:
		bot.setTrust(50);
		database.setTrust(60);
		Set<Resource> newResources = new HashSet<Resource>();
		newResources.add(bot);
		newResources.add(database);
		ResourcesManager.updateResourcesTrust(newResources);
		resources = ResourcesManager.getResources();
		for(Resource resource: resources) {
			if(resource.equals(bot)) {
				assertEquals(bot.getTrust(), resource.getTrust());
			} else if(resource.equals(database)) {
				assertEquals(database.getTrust(), resource.getTrust());
			}
		}
		
		// Remove the resources:
		assertEquals(0, ResourcesManager.removeResources(newResources).size());
		resources = ResourcesManager.getResources();
		assertEquals(oldResources, resources);
	}
	
	/**
	 * Test the primary key of the database.
	 */
	public void testPrimaryKey() {
		ResourcesManager.addResource(new Bot("test123.com", "TestBot", 50));
		assertFalse(ResourcesManager.addResource(new OpeningsDatabase("test123.com", "TestDatabase", 50)));
		assertFalse(ResourcesManager.addResource(new Bot("test123.com", "TestBot", 50)));
		ResourcesManager.removeResource(new Bot("test123.com", "TestBot", 50));
	}
	
	/**
	 * Test the concurrency by doing a lot of simultaneous write actions.
	 * @throws InterruptedException 
	 */
	public void testConcurrency() throws InterruptedException {
		class MyThread extends Thread {
			private Resource resource;
			
			public MyThread(Resource resource) {
				this.resource = resource;
			}
			
			@Override
			public void run() {
				if(ResourcesManager.addResource(this.resource)) {
					System.out.println(this.resource+" added successfully!");
				} else {
					System.out.println(this.resource+" failed to be added.");
				}
			}
		}
		
		Set<Resource> resources = new HashSet<Resource>();
		for(int i=0 ; i<30 ; i++) {
			resources.add(new Bot("uri"+i+".com", "bot"+i, 1));
		}
		
		Set<MyThread> threads = new HashSet<MyThread>();
		for(Resource resource: resources) {
			threads.add(new MyThread(resource));
		}
		for(MyThread thread: threads) {
			thread.start();
		}
		for(MyThread thread: threads) {
			thread.join();
		}
		
		resources = ResourcesManager.removeResources(resources);
		System.out.println(resources);
	}
}