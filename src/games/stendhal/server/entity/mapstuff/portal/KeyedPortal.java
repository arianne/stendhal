/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortal.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.entity.RPEntity;

/**
 * A keyed portal is a special kind of portal which requires a key to pass it.
 * If the player carries the key with him, he can use the portal just like a
 * normal portal.
 */
public class KeyedPortal extends AccessCheckingPortal {
	/**
	 * The key item needed.
	 */
	protected String key;

	/**
	 * The minimum number of items.
	 */
	protected int quantity;

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key
	 *            The name of the required key.
	 */
	public KeyedPortal(final String key) {
		this(key, 1);
	}

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key
	 *            The name of the required key.
	 * @param quantity
	 *            The key quantity required.
	 */
	public KeyedPortal(final String key, final int quantity) {
		this(key, quantity, null);
	}

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key
	 *            The name of the required key.
	 * @param quantity
	 *            The key quantity required.
	 * @param rejectMessage
	 *            The message given when rejected.
	 */
	public KeyedPortal(final String key, final int quantity, final String rejectMessage) {
		super(rejectMessage);

		this.key = key;
		this.quantity = quantity;
	}

	//
	// AccessCheckingPortal
	//

	/**
	 * Determine if this portal can be used.
	 *
	 * @param user
	 *            The user to be checked.
	 *
	 * @return <code>true</code> if the user can use the portal.
	 */
	@Override
	protected boolean isAllowed(final RPEntity user) {
		return user.isEquipped(key, quantity);
	}
}
