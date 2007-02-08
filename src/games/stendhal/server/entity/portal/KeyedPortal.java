/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortal.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.server.entity.RPEntity;
//import games.stendhal.server.entity.player.Player;

/**
 * A keyed portal is a special kind of portal which requires a key to pass it.
 * If the player carries the key with him, he can use the portal just
 * like a normal portal.
 */
public class KeyedPortal extends Portal {
	protected String	key;
	protected int		quantity;


	/**
	 * Creates a new keyed portal.
	 *
	 * @param key		The name of the required key.
	 */
	public KeyedPortal(String key) {
		this(key, 1);
	}


	/**
	 * Creates a new keyed portal.
	 *
	 * @param key		The name of the required key.
	 * @param quantity	The key quantity required.
	 */
	public KeyedPortal(String key, int quantity) {
		this.key = key;
		this.quantity = quantity;
	}


        /**
         * Use the portal, if allowed.
         */
        @Override
        public void onUsed(RPEntity user) {
		if(user.isEquipped(key, quantity))
			super.onUsed(user);
	}
}
