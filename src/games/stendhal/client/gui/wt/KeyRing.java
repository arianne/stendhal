/**
 * @(#) src/games/stendhal/client/gui/wt/KeyRing.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.events.FeatureChangeListener;

/**
 * A key ring.
 */
@SuppressWarnings("serial")
public final class KeyRing extends EntityContainer implements FeatureChangeListener {

	/**
	 * Create a key ring.
	 * 
	 * @param client
	 *            The stendhal client.
	 */
	public KeyRing() {
		// Remember if you change these numbers change also a number in
		// src/games/stendhal/server/entity/RPEntity.java
		super("keyring", 2, 4);

		setLocation(4, 200);
	}

	//
	// FeatureChangeListener
	//

	/**
	 * A feature was disabled.
	 * 
	 * @param name
	 *            The name of the feature.
	 */
	public void featureDisabled(String name) {
		if (name.equals("keyring")) {
			setVisible(false);
		}
	}

	/**
	 * A feature was enabled.
	 * 
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            Optional feature specific data.
	 */
	public void featureEnabled(String name, String value) {
		if (name.equals("keyring")) {
			setVisible(true);
		}
	}

	/**
	 * Destroy the panel.
	 */
	@Override
	public void dispose() {
		// TODO: Could be cleaner reference
		StendhalClient.get().removeFeatureChangeListener(this);

		super.dispose();
	}

}
