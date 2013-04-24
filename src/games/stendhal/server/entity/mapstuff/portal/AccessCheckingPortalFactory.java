/*
 * @(#) src/games/stendhal/server/entity/portal/AccessCheckingPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>AccessCheckingPortal</code> objects.
 */
abstract class AccessCheckingPortalFactory implements
		ConfigurableFactory {
	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create the access portal implementation.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * 
	 * @return The portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected abstract AccessCheckingPortal createPortal(
			ConfigurableFactoryContext ctx);

	/**
	 * Extract the rejected message from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The rejected message, or <code>null</code> if none.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getRejectedMessage(final ConfigurableFactoryContext ctx) {
		return ctx.getString("rejected", null);
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a keyed portal.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A KeyedPortal.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * 
	 * @see KeyedPortal
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final AccessCheckingPortal portal = createPortal(ctx);

		final String message = getRejectedMessage(ctx);

		if (message != null) {
			portal.setRejectedMessage(message);
		}

		return portal;
	}
}
