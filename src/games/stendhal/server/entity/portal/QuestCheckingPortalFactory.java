/*
 * @(#) src/games/stendhal/server/entity/portal/QuestCheckingPortalFactory.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.common.ConfigurableFactoryContext;

/**
 * A factory for <code>QuestCheckingPortal</code> objects.
 */
public class QuestCheckingPortalFactory extends DoorFactory {

	//
	// QuestCheckingPortalFactory
	//

	/**
	 * Extract the quest name from a context.
	 *
	 * @param	ctx		The configuration context.
	 *
	 * @return	The quest name.
	 *
	 * @throws	IllegalArgumentException
	 *				If the quest attribute is missing.
	 */
	protected String getQuest(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		String s;

		if ((s = ctx.getAttribute("quest")) == null) {
			throw new IllegalArgumentException("Required attribute 'quest' missing");
		}

		return s;
	}

	//
	// ConfigurableFactory
	//

	/**
	 * Create a locked door.
	 *
	 * @param	ctx		Configuration context.
	 *
	 * @return	A QuestCheckingPortal.
	 *
	 * @throws	IllegalArgumentException
	 *				If there is a problem with the
	 *				attributes. The exception message
	 *				should be a value sutable for
	 *				meaningful user interpretation.
	 *
	 * @see		QuestCheckingPortal
	 */
	public Object create(ConfigurableFactoryContext ctx) throws IllegalArgumentException {
		return new QuestCheckingPortal(getQuest(ctx));
	}
}
