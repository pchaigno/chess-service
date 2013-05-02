package core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

/**
 * This class deploies the CentralServerResource on a Grizzly Server.
 * TODO Maybe change the version to the latest : jersey 1.17 (now it is 1.6)
 */
public class CentralServerResourceDeployer {
	
	private static SelectorThread threadSelector=null;
	private static final String BASE_URI = "http://localhost:9998/";
	private static final String RESOURCE_PACKAGE = "core";
	
	/**
	 * Start the server on the BASE_URI using resources located in RESOURCE_PACKAGE
	 * @return true if the server started normally, false otherwise
	 */
	public static boolean start(){
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", RESOURCE_PACKAGE);
		try {
			threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
		} catch (IllegalArgumentException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stop the server if he's running
	 * @return true if the server is stopped or was stopped, false otherwise
	 */
	public static boolean stop(){
		if(threadSelector==null)
			return false;
		else if(!threadSelector.isRunning())
			return true;
		else{
			threadSelector.stopEndpoint();
			return true;
		}
	}

	public static void main(String[] args) throws IOException {
		start();
		System.in.read();
		stop();
	}
}