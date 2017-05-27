/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

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
	 * @param ctx
	 *            The configuration context.
	 * @return The key name.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getKey(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("key");
	}

	/**
	 * Extract the portal key quantity from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The required key quantity.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected int getQuantity(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("quantity", 1);
	}

	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create a keyed portal.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The portal.
	 *
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		return new KeyedPortal(getKey(ctx), getQuantity(ctx));
	}
}
