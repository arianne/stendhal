/***************************************************************************
 *                    Copyright © 2025 - Faiumoni e. V.                    *
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
import games.stendhal.server.actions.CStatusAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;


public class UsingWebClientCondition implements ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		final String playerName = player.getName();
		return CStatusAction.clientList.containsKey(playerName)
				&& "webclient".equals(CStatusAction.clientList.get(playerName));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public int hashCode() {
		return 31 * getClass().getSimpleName().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof UsingWebClientCondition);
	}
}
