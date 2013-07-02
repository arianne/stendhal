/*
 * @(#) src/games/stendhal/server/entity/portal/QuestCheckingPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>QuestCheckingPortal</code> objects.
 */
public class QuestCheckingPortalFactory extends AccessCheckingPortalFactory {
    
    //
    // QuestCheckingPortalFactory
    //

    String requiredQuest;
    String requiredState;
    String rejectMessage;
    
	/**
	 * Extract the quest name from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The quest name.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected void setQuest(final ConfigurableFactoryContext ctx) {
        requiredQuest = ctx.getRequiredString("quest");
	    requiredState = ctx.getString("state", null);
	    if (requiredState != null) {
	        rejectMessage = ctx.getRequiredString("rejected");
	    }
	}

	//
	// AccessCheckingPortalFactory
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
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
	    setQuest(ctx);
	    
	    if (requiredState != null) {
	        return new QuestCheckingPortal(requiredQuest, requiredState, rejectMessage);
	    }
	    
		return new QuestCheckingPortal(requiredQuest);
	}
}
