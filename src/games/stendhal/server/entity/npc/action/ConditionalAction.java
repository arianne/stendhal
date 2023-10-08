/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * executes an actions, if and only if, a condition is met.
 */
@Dev(category=Category.IGNORE)
public class ConditionalAction implements ChatAction {

	private final ChatCondition condition;
	private final ChatAction action;

	/**
	 * Creates a new ConditionalAction.
	 *
	 * @param action
	 *            action to execute
	 */
	public ConditionalAction(final ChatCondition condition, final ChatAction action) {
		this.condition = condition;
		this.action = action;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		if (condition.fire(player, sentence, player)) {
			action.fire(player, sentence, npc);
		}
	}

	@Override
	public String toString() {
		return "ConditionalAction <" + condition + ", " + action + ">";
	}

	@Override
	public int hashCode() {
		return 8363 * condition.hashCode() * action.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ConditionalAction)) {
			return false;
		}
		final ConditionalAction other = (ConditionalAction) obj;
		return condition.equals(other.condition) && action.equals(other.action);
	}

}
