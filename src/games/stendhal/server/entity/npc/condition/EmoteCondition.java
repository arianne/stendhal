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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Is player emoting to npc?
 */
@Dev(category=Category.CHAT, label="\"\"?")
public class EmoteCondition implements ChatCondition {

	private final String playerAction;

	/**
	 * Creates a new AdminCondition for high level admins.
	 *
	 * @param playerAction an action the player must include in the emote, may be empty
	 */
	public EmoteCondition(final String playerAction) {
		this.playerAction = playerAction.trim();
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final String text = sentence.getOriginalText();
		return ((text.startsWith("!me")) &&
				(text.contains(playerAction)) &&
				(text.toLowerCase().contains(entity.getTitle().toLowerCase())));
	}

	@Override
	public String toString() {
		return "EmoteCondition";
	}

	@Override
	public int hashCode() {
		return 43649 * playerAction.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof EmoteCondition)) {
			return false;
		}
		EmoteCondition other = (EmoteCondition) obj;
		return playerAction.equals(other.playerAction);
	}

}
