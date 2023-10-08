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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * executes a random action from a list of actions.
 * It calls fire() on one of the actions, when its own
 * fire() is called.
 */
@Dev(category=Category.IGNORE)
public class RandomAction implements ChatAction {

	private final List<ChatAction> actions;

	/**
	 * Creates a new RandomAction.
	 *
	 * @param action
	 *            action to execute
	 */
	public RandomAction(final ChatAction... action) {
		this.actions = Arrays.asList(action);
	}

	/**
	 * Creates a new RandomAction.
	 *
	 * @param actions
	 *            list of actions to execute
	 */
	public RandomAction(final List<ChatAction> actions) {
		this.actions = new LinkedList<ChatAction>(actions);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		Rand.rand(actions).fire(player, sentence, npc);
	}

	@Override
	public String toString() {
		return "Random <" + actions.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 7247 * actions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RandomAction)) {
			return false;
		}
		final RandomAction other = (RandomAction) obj;
		return actions.equals(other.actions);
	}

}
