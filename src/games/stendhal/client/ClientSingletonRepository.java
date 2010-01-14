package games.stendhal.client;

import games.stendhal.client.gui.UserInterface;
import marauroa.client.ClientFramework;

/**
 * keeps instances of singletons that may depend on the context
 *
 * @author hendrik
 */
public class ClientSingletonRepository {

	private static ClientFramework clientFramework;
	private static UserInterface userInterface;

	/**
	 * gets the ClientFramework
	 *
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


	/**
	 * gets the user interface
	 *
	 * @return UserInterface
	 */
	public static UserInterface getUserInterface() {
		return userInterface;
	}

	/**
	 * sets the user interface
	 *
	 * @param userInterface UserInterface
	 */
	public static void setUserInterface(UserInterface userInterface) {
		ClientSingletonRepository.userInterface = userInterface;
	}
}
