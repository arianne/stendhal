/**
 *
 */
package games.stendhal.client.util;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.MockUserInterface;

/**
 * Utilities for initialzing UI parts for tests
 *
 * @author madmetzger
 */
public class UserInterfaceTestHelper {

	/**
	 * Init UserInterface if needed
	 */
	public static void initUserInterface() {
		if(ClientSingletonRepository.getUserInterface() == null) {
			ClientSingletonRepository.setUserInterface(new MockUserInterface());
		}
	}

	public static void resetUserInterface() {
		ClientSingletonRepository.setUserInterface(new MockUserInterface());
	}

}
