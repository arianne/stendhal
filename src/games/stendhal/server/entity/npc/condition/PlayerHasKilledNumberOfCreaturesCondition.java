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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.constants.KillType;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

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

	private final KillType killType;

	/**
	 * Constructor to use condition with only one creature
	 *
	 * @param creature creature
	 * @param numberOfKills number of kills
	 */
	public PlayerHasKilledNumberOfCreaturesCondition(final String creature, final Integer numberOfKills) {
		creatures = new HashMap<String, Integer>();
		creatures.put(creature, numberOfKills);
		killType = KillType.ANY;
	}

	/**
	 * Constructor to use condition with only one creature.
	 *
	 * @param creature
	 * 		Creature name.
	 * @param numberOfKills
	 * 		Required number of kills.
	 * @param killType
	 * 		Required kill type: solo, shared, or either.
	 */
	public PlayerHasKilledNumberOfCreaturesCondition(final String creature, final Integer numberOfKills, final KillType killType) {
		creatures = new HashMap<String, Integer>();
		creatures.put(creature, numberOfKills);
		this.killType = killType;
	}

	/**
	 * creates a condition to kill each creature with the name specified in the map and the number as value
	 *
	 * @param kills map of creature name to kill and number of that creature to kill
	 */
	@Dev
	public PlayerHasKilledNumberOfCreaturesCondition(final Map<String, Integer> kills) {
		creatures = new HashMap<String, Integer>();
		creatures.putAll(kills);
		killType = KillType.ANY;
	}

	/**
	 * Creates a condition to kill each creature with the name specified in
	 * the map and the number as value
	 *
	 * @param kills
	 * 		Map of creature name to kill and number of that creature to kill.
	 * @param killType
	 * 		Required kill type: solo, shared, or either.
	 */
	public PlayerHasKilledNumberOfCreaturesCondition(final Map<String, Integer> kills, final KillType killType) {
		creatures = new HashMap<String, Integer>();
		creatures.putAll(kills);
		this.killType = killType;
	}

	/**
	 * Constructor to use when you want to let kill the same number of each specified creature
	 *
	 * @param number the desired number
	 * @param creatureNames the names of the creatures to kill
	 */
	public PlayerHasKilledNumberOfCreaturesCondition(final Integer number, final String... creatureNames) {
		creatures = new HashMap<String, Integer>();
		List<String> names = Arrays.asList(creatureNames);
		for (String name : names) {
			creatures.put(name, number);
		}
		killType = KillType.ANY;
	}

	/**
	 * Constructor to use when you want to let kill the same number of each
	 * specified creature.
	 *
	 * @param number
	 * 		Required number of kills.
	 * @param killType
	 * 		Required kill type: solo, shared, or either.
	 * @param creatureNames
	 * 		The names of the creatures to kill.
	 */
	public PlayerHasKilledNumberOfCreaturesCondition(final Integer number, final KillType killType, final String... creatureNames) {
		creatures = new HashMap<String, Integer>();
		List<String> names = Arrays.asList(creatureNames);
		for (String name : names) {
			creatures.put(name, number);
		}
		this.killType = killType;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		for (Entry<String, Integer> entry : creatures.entrySet()) {
			final int soloKills = player.getSoloKill(entry.getKey());
			final int sharedKills = player.getSharedKill(entry.getKey());

			if (killType.solo()) {
				if (entry.getValue().intValue() > soloKills) {
					return false;
				}
			} else if (killType.shared()) {
				if (entry.getValue().intValue() > sharedKills) {
					return false;
				}
			} else {
				if (entry.getValue().intValue() > (soloKills + sharedKills)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return 43913 * (creatures.hashCode() + killType.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasKilledNumberOfCreaturesCondition)) {
			return false;
		}
		PlayerHasKilledNumberOfCreaturesCondition other = (PlayerHasKilledNumberOfCreaturesCondition) obj;
		return creatures.equals(other.creatures) && killType.equals(other.killType);
	}
}
