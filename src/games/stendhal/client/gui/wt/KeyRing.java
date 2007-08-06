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
import games.stendhal.client.events.FeatureChangeListener;

/**
 * A key ring.
 */
public class KeyRing extends EntityContainer implements FeatureChangeListener {
	/**
	 * Create a key ring.
	 *
	 * @param	client		The stendhal client.
	 */
	public KeyRing(StendhalClient client) {
		super(client, "keyring", 2, 4);

		// Not yet
		// client.addFeatureChangeListener(this);
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


	//
	// FeatureChangeListener
	//

	/**
	 * A feature was disabled.
	 *
	 * @param	name		The name of the feature.
	 */
	public void featureDisabled(String name) {
		if(name.equals("keyring")) {
			if(isMinimizeable()) {
				setMinimizeable(false);
				setMinimized(true);
			}
		}
	}


	/**
	 * A feature was enabled.
	 *
	 * @param	name		The name of the feature.
	 * @param	value		Optional feature specific data.
	 */
	public void featureEnabled(String name, String value) {
		if(name.equals("keyring")) {
			if(!isMinimizeable()) {
				setMinimizeable(true);
				setMinimized(false);
			}
		}
	}
}
