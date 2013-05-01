package tests;

import java.util.HashSet;
import java.util.Set;

import core.Bot;
import core.Database;
import core.Resource;
import core.ResourcesManager;
import junit.framework.TestCase;

public class TestResourcesManager extends TestCase {

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
		Resource database = new Database("test321.com", "TestDatabase", 60);
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
		database = new Database("test321.com", "Test Database", 0);
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
}