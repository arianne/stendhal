package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>QuestCompletedPortal</code> objects.
 */
public class QuestCompletedPortalFactory extends AccessCheckingPortalFactory {

	//
	// QuestCompletedPortalFactory
	//

	/**
	 * Extract the quest name from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The quest name.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected String getQuest(ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("quest");
	}

	//
	// AccessCompletedPortalFactory
	//

	/**
	 * Create a quest checking portal.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A Portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * 
	 * @see LevelCheckingPortal
	 */
	@Override
	protected AccessCheckingPortal createPortal(ConfigurableFactoryContext ctx) {
		return new QuestCompletedPortal(getQuest(ctx));
	}
}
