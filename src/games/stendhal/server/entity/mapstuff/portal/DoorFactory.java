/*
 * @(#) src/games/stendhal/server/entity/portal/DoorFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A base factory for <code>Door</code> objects.
 */
public abstract class DoorFactory implements ConfigurableFactory {

	/**
	 * Extract the door class from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 *
	 * @return The class name.
	 *
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getClass(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("class");
	}

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
}
