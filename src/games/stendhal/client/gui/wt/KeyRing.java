/**
 * @(#) src/games/stendhal/client/gui/wt/KeyRing.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalClient;

/**
 * A key ring.
 */
public class KeyRing extends EntityContainer {
	/**
	 * Create a key ring.
	 */
	public KeyRing(StendhalClient client) {
		super(client, "keyring", 2, 3);
	}


	//
	// KeyRing
	//

	public void update() {
		// Do 4 vs 6 size handling
	}
}
