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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was the trigger phrase a name of an NPC for an unstarted quest in the region? (Use with a ""-trigger in npc.add)
 */
public class TriggerIsNPCNameForUnstartedQuestCondition implements ChatCondition {
	private final String region;

	/**
	 * Creates a new TriggerIsNPCNameForUnstartedQuestCondition
	 * 
	 * @param region
	 */
	public TriggerIsNPCNameForUnstartedQuestCondition(final String region) {
		this.region=region;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		
		return SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region).contains(sentence.getTriggerExpression());
	}

	@Override
	public String toString() {
		return "Trigger is name of npc for unstarted quest in <" + region + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TriggerIsNPCNameForUnstartedQuestCondition.class);
	}
}
