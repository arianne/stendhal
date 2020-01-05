/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks if the player is next to NPC.
 */
public class PlayerNextToCondition implements ChatCondition {

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return player.nextTo(npc);
	}

	@Override
	public String toString() {
		return "PlayerNextToCondition";
	}

	@Override
	public int hashCode() {
		// FIXME: needs own implementation
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PlayerNextToCondition;
	}
}
