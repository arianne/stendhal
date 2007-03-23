package games.stendhal.client.update;


/**
 * read the configuration file for the client.
 *
 * @author hendrik
 */
public class ClientGameConfiguration {

	private static ClientGameConfiguration instance = null;
	
	private ClientGameConfiguration() {
		// Singleton pattern, hide constructor
	}
	
	private void init() {
		if (instance == null) {
			instance = new ClientGameConfiguration();
		}
	}
	
}
