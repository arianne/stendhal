/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
			// explicitly exclude the wolf as this was possible to summon with a summon scroll once
			// and the twilight slime isn't what we mean by rare
			if (creature.isRare() && !"big bad wolf".equals(creature.getName()) && !"twilight slime".equals(creature.getName())) {
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
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				KilledRareCreatureCondition.class);
	}
}
