package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>HousePortal</code> objects.
 */
public class HousePortalFactory extends AccessCheckingPortalFactory {

	//
	// StoredKeyedPortalFactory
	//

	/**
	 * Extract the portal key's doorId from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The key doorId.
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	protected String getKey(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("doorId");
	}

	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create a stored keyed portal.
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
		return new HousePortal(getKey(ctx));
	}
}
