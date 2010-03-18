package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;


/**
 * A factory for <code>RandomDestinationPortal</code> objects.
 */
public class RandomDestinationPortalFactory extends QuestCompletedPortalFactory {
	/**
	 * Create a portal with random destination.
	 * 
	 * @param ctx The configuration context.
	 * @return The portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the class attribute is missing.
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		return new RandomDestinationPortal();
	}
}
