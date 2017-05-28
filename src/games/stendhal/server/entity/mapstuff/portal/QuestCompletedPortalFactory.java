/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
	protected String getQuest(final ConfigurableFactoryContext ctx) {
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
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		return new QuestCompletedPortal(getQuest(ctx));
	}
}
