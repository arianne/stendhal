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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.TriggerList;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Was the trigger phrase a name of an NPC for an unstarted quest in the region? (Use with a ""-trigger in npc.add)
 */
@Dev(category=Category.IGNORE)
public class TriggerIsNPCNameForUnstartedQuestCondition implements ChatCondition {

	private final List<String> regions;

	/**
	 * Creates a new TriggerIsNPCNameForUnstartedQuestCondition
	 *
	 * @param region region
	 */
	public TriggerIsNPCNameForUnstartedQuestCondition(final String region) {
		this.regions=Arrays.asList(region);
	}

	/**
	 * Creates a new TriggerIsNPCNameForUnstartedQuestCondition
	 *
	 * @param regions list of regions
	 */
	public TriggerIsNPCNameForUnstartedQuestCondition(final List<String> regions) {
		this.regions = ImmutableList.copyOf(regions);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		List<String> npcs = new LinkedList<String>();
		for (String region: regions) {
			npcs.addAll(SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region));
		}
		return (new TriggerList(npcs)).contains(sentence.getTriggerExpression());
	}

	@Override
	public String toString() {
		return "Trigger is name of npc for unstarted quest in <" + regions.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 5023 * regions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TriggerIsNPCNameForUnstartedQuestCondition)) {
			return false;
		}
		TriggerIsNPCNameForUnstartedQuestCondition other = (TriggerIsNPCNameForUnstartedQuestCondition) obj;
		return regions.equals(other.regions);
	}
}
