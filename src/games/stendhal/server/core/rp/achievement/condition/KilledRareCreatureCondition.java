/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.condition;

import java.util.Collection;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Did the player ever kill a rare creature?
 *
 * @author kymara
 */
public class KilledRareCreatureCondition implements ChatCondition {
	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
		for (Creature creature : creatures) {
			if (creature.isRare()) {
				if (player.hasKilled(creature.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "KilledRareCreatureCondition";
	}

	@Override
	public int hashCode() {
		return -1273981;
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof KilledRareCreatureCondition);
	}
}
