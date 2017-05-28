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
package games.stendhal.server.entity.npc.action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * executes a list of actions in the order they have been added.
 * It calls fire() of each action added, when its own
 * fire() is called.
 */
@Dev(category=Category.IGNORE)
public class MultipleActions implements ChatAction {

	private final List<ChatAction> actions;

	/**
	 * Creates a new MultipleActions.
	 *
	 * @param action
	 *            action to execute
	 */
	public MultipleActions(final ChatAction... action) {
		this.actions = Arrays.asList(action);
	}

	/**
	 * Creates a new MultipleActions.
	 *
	 * @param actions
	 *            list of actions to execute
	 */
	public MultipleActions(final List<ChatAction> actions) {
		this.actions = new LinkedList<ChatAction>(actions);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		for (final ChatAction action : actions) {
			action.fire(player, sentence, npc);
		}
	}

	@Override
	public String toString() {
		return actions.toString();
	}

	@Override
	public int hashCode() {
		return 5323 * actions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof MultipleActions)) {
			return false;
		}
		final MultipleActions other = (MultipleActions) obj;
		return actions.equals(other.actions);
	}

}
