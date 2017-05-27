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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

public class ConditionAndActionPortal extends AccessCheckingPortal {

	private static final Logger logger = Logger.getLogger(ConditionAndActionPortal.class);

	private final ChatCondition condition;
	private final ChatAction action;

	/**
	 * Creates a ConditionCheckingPortal.
	 *
	 * @param condition optional condition to check
	 * @param action optional action to execute
	 */
	public ConditionAndActionPortal(ChatCondition condition, ChatAction action) {
		this(condition, "Why should i go down there?. It looks very dangerous.", action);
	}

	/**
	 * Creates a ConditionCheckingPortal with reject message.
	 *
	 * @param condition optional condition to check
	 * @param rejectMessage message to tell the player in case the condition is not met
	 * @param action optional action to execute
	 */
	public ConditionAndActionPortal(ChatCondition condition, String rejectMessage, ChatAction action) {
		super(rejectMessage);
		this.condition = condition;
		this.action = action;
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
		if (condition != null) {
			return condition.fire((Player) user, sentence, this);
		}
		return true;
	}

	@Override
	public boolean onUsed(RPEntity user) {
		boolean res = super.onUsed(user);

		if (res && (action != null)) {
			if (user instanceof Player) {
				action.fire((Player) user, ConversationParser.parse(user.get("text")), new EventRaiser(this));
			} else {
				logger.error("user is no instance of Player but: " + user, new Throwable());
			}
		}

		return res;
	}

}
