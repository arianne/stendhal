/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>KeyedPortal</code> objects.
 */
public class KeyedPortalFactory extends AccessCheckingPortalFactory {
	//
	// KeyedPortalFactory
	//

	/**
	 * Extract the portal key from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The key name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected String getKey(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("key")) == null) {
			throw new IllegalArgumentException("Required attribute 'key' missing");
		}

		return s;
	}

	/**
	 * Extract the portal key quantity from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The required key quantity.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	protected int getQuantity(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("quantity")) == null) {
			return 1;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid 'quantity' attribute: " + s);
		}
	}


	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create a keyed portal.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The portal.
	 *
	 * @throws	IllegalArgumentException
	 *				If the class attribute is missing.
	 */
	@Override
	protected AccessCheckingPortal createPortal(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new KeyedPortal(getKey(ctx), getQuantity(ctx));
	}
}
