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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Checks if a player has killed the specified creature at least the specified number of times
 * This can be with or without the help of other players. Note: This condition deals with kills
 * since the player was created. To check kills in the context of a quest, use KilledForQuestCondition.
 *
 * @author madmetzger
 */
@Dev(category=Category.KILLS, label="Kills?")
public class PlayerHasKilledNumberOfCreaturesCondition implements ChatCondition {

	private final Map<String, Integer> creatures;

	/**
	 * Constructor to use condition with only one creature
	 *
	 * @param creature creature
	 * @param numberOfKills number of kills
	 */
	public PlayerHasKilledNumberOfCreaturesCondition (String creature, Integer numberOfKills) {
		creatures = new HashMap<String, Integer>();
		creatures.put(creature, numberOfKills);
	}

	/**
	 * creates a condition to kill each creature with the name specified in the map and the number as value
	 *
	 * @param kills map of creature name to kill and number of that creature to kill
	 */
	@Dev
	public PlayerHasKilledNumberOfCreaturesCondition (Map<String, Integer> kills) {
		creatures = new HashMap<String, Integer>();
		creatures.putAll(kills);
	}

	/**
	 * Constructor to use when you want to let kill the same number of each specified creature
	 *
	 * @param number the desired number
	 * @param creatureNames the names of the creatures to kill
	 */
	public PlayerHasKilledNumberOfCreaturesCondition (Integer number, String... creatureNames) {
		creatures = new HashMap<String, Integer>();
		List<String> names = Arrays.asList(creatureNames);
		for (String name : names) {
			creatures.put(name, number);
		}
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for (Entry<String, Integer> entry : creatures.entrySet()) {
			int actualSharedKills = player.getSharedKill(entry.getKey());
			int actualSoloKills = player.getSoloKill(entry.getKey());
			int actualKills = actualSharedKills + actualSoloKills;
			if (entry.getValue().intValue() > actualKills) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 43913 * creatures.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasKilledNumberOfCreaturesCondition)) {
			return false;
		}
		PlayerHasKilledNumberOfCreaturesCondition other = (PlayerHasKilledNumberOfCreaturesCondition) obj;
		return creatures.equals(other.creatures);
	}
}
