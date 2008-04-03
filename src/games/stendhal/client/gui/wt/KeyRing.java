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
	 */
	public KeyRing() {
		super("Keyring", 2, 4, false);
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
