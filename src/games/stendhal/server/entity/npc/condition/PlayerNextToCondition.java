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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks if the player is next to an entity. If an entity name is not supplied
 * it will check if the player is next to the NPC checking the condition.
 */
public class PlayerNextToCondition implements ChatCondition {

	final String entityName;

	public PlayerNextToCondition() {
		entityName = null;
	}

	public PlayerNextToCondition(final String name) {
		entityName = name;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		if (entityName == null) {
			return player.nextTo(npc);
		}

		Entity entity = SingletonRepository.getNPCList().get(entityName);
		if (entity == null) {
			entity = StendhalRPRuleProcessor.get().getPlayer(entityName);
			if (entity == null) {
				return false;
			}
		}

		return player.nextTo(entity);
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
