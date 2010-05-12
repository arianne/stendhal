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

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

public class ConditionCheckingPortal extends AccessCheckingPortal {
	private final ChatCondition condition;

	/**
	 * Creates a ConditionCheckingPortal
	 *
	 * @param condition condition to check
	 */
	public ConditionCheckingPortal(ChatCondition condition) {
		this(condition, "Why should i go down there?. It looks very dangerous.");
	}

	/**
	 * Creates a ConditionCheckingPortal
	 *
	 * @param condition condition to check
	 * @param rejectMessage message to tell the player in case the condition is not met
	 */
	public ConditionCheckingPortal(ChatCondition condition, String rejectMessage) {
		super(rejectMessage);

		this.condition = condition;
	}

	/**
	 * Determine if this portal can be used.
	 * 
	 * @param user
	 *            The user to be checked.
	 * 
	 * @return <code>true</code> if the user can use the portal.
	 */
	@Override
	protected boolean isAllowed(final RPEntity user) {
		Sentence sentence = ConversationParser.parse(user.get("text"));
		return condition.fire((Player) user, sentence, this);
	}
}
