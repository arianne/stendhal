/**
 * @(#) src/games/stendhal/client/gui/wt/KeyRing.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.listener.FeatureChangeListener;

/**
 * A key ring.
 */
public class KeyRing extends EntityContainer implements FeatureChangeListener {
	/**
	 * Create a key ring.
	 * 
	 * @param gameScreen
	 * 
	 */
	public KeyRing(final IGameScreen gameScreen) {
		// Remember if you change these numbers change also a number in
		// src/games/stendhal/server/entity/RPEntity.java
		super("keyring", 2, 4, gameScreen);

		// Hide by default
		setMinimizeable(false);
		setMinimized(true);
	}

	//
	// KeyRing
	//

	/**
	 * Disable the keyring.
	 */
	private void disable() {
		/*
		 * You can not really lose a keyring for now, but
		 * a disable message is received at every map change.
		 * Just ignore it. (And after keyrings are made to
		 * real items, this whole file will be obsolete anyway).
		 */
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
	public void featureDisabled(final String name) {
		if (name.equals("keyring")) {
			disable();
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
	public void featureEnabled(final String name, final String value) {
		if (name.equals("keyring")) {
			if (!isMinimizeable()) {
				setMinimizeable(true);
				setMinimized(false);
			}
		}
	}

	//
	// WtPanel
	//

	/**
	 * Destroy the panel.
	 * 
	 * @param gameScreen
	 */
	@Override
	public void destroy(final IGameScreen gameScreen) {
		StendhalClient.get().removeFeatureChangeListener(this);

		super.destroy(gameScreen);
	}
}
