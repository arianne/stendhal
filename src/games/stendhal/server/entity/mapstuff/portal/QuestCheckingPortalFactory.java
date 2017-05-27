/***************************************************************************
 *                (C) Copyright 2007-2013 - Faiumoni e. V.                 *
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


import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>QuestCheckingPortal</code> objects.
 */
public class QuestCheckingPortalFactory extends AccessCheckingPortalFactory {

	private String requiredQuest;
	private String requiredState;
	private String rejectMessage;

	/**
	 * Extract the quest name from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected void setQuest(final ConfigurableFactoryContext ctx) {
		requiredQuest = ctx.getRequiredString("quest");
		requiredState = ctx.getString("state", null);
		rejectMessage = ctx.getString("rejected", "Why should i go there?");
	}

	/**
	 * Create a quest checking portal.
	 *
	 * @param ctx Configuration context.
	 * @return A Portal.
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * @see QuestCheckingPortal
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		setQuest(ctx);
		return new QuestCheckingPortal(requiredQuest, requiredState, rejectMessage);
	}
}
