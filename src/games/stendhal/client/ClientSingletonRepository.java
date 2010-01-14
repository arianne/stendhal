package games.stendhal.client;

import marauroa.client.ClientFramework;

/**
 * keeps instances of singletons that may depend on the context
 *
 * @author hendrik
 */
public class ClientSingletonRepository {

	private static ClientFramework clientFramework;

	/**
	 * gets the ClientFramework
	 * @return ClientFramework
	 */
	public static ClientFramework getClientFramework() {
		return clientFramework;
	}

	/**
	 * sets the ClientFramework
	 *
	 * @param clientFramework ClientFramework
	 */
	public static void setClientFramework(ClientFramework clientFramework) {
		ClientSingletonRepository.clientFramework = clientFramework;
	}

}
