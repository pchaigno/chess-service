package centralserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

/**
 * This class deploies the CentralServerResource on a Grizzly Server
 * @author clemgaut
 *
 */
/*
Maybe change the version to the latest : jersey 1.17 (now it is 1.6)
*/
public class CentralServerResourceDeployer {

	public static void main(String[] args) throws IOException {
        
        final String baseUri = "http://localhost:9998/";
        final Map<String, String> initParams = 
	                       new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", 
                "centralserver");

       System.out.println("Starting grizzly...");
       SelectorThread threadSelector = 
	          GrizzlyWebContainerFactory.create(baseUri, initParams);
       System.out.println(String.format(
         "Jersey app started with WADL available at %sapplication.wadl\n" + 
         "Try out %sresource/rest/openings/fendebut\nThe answer is always e4 (for now)\nHit enter to stop it...", baseUri, baseUri));
       System.in.read();
       threadSelector.stopEndpoint();
       System.exit(0);
   }    
	
}
