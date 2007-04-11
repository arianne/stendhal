/**
 * @(#) src/games/stendhal/client/gui/wt/KeyRing.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.User;

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
		User user = User.get();

		if(user != null) {
			if(user.hasFeature("keyring")) {
				if(!isMinimizeable()) {
					setMinimizeable(true);
					setMinimized(false);
				}
			} else {
				if(isMinimizeable()) {
					setMinimizeable(false);
					setMinimized(true);
				}
			}
		}
	}
}
