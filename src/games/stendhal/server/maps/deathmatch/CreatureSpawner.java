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
package games.stendhal.server.maps.deathmatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.ArenaCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.creature.LevelBasedComparator;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.player.Player;


/**
 * This class spawns creatures during a deathmatch session.
 *
 * @author hendrik
 */
public class CreatureSpawner  {

	static final Logger logger = Logger.getLogger(CreatureSpawner.class);

	// spawn a new monster each 20 seconds
	static final long SPAWN_DELAY = 20000;
	static final long NUMBER_OF_CREATURES = 10;

	private final List<Creature> sortedCreatures = new LinkedList<Creature>();
	private final List<DeathMatchCreature> spawnedCreatures = new ArrayList<DeathMatchCreature>();

	/**
	 * Creates a new CreatureSpawner.
	 */
	public CreatureSpawner() {
		final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
		for (Creature creature : creatures) {
			if (!creature.isAbnormal()) {
				sortedCreatures.add(creature);
			}
		}
		Collections.sort(sortedCreatures, new LevelBasedComparator());
	}

	/**
	 * Removes the critters that the player was supposed to kill.
	 */
	public void removePlayersMonsters() {
		for (final Creature creature : spawnedCreatures) {
			final StendhalRPZone monsterZone = creature.getZone();

			if (monsterZone != null) {
				monsterZone.remove(creature);
			}
		}
	}

	/**
	 * Checks if all our enemies are dead.
	 *
	 * @return true if all are dead, false otherwise
	 */
	boolean areAllCreaturesDead() {
		for (final Creature creature : spawnedCreatures) {
			if (creature.getHP() > 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gives the daily quest creature to the player,
	 * if he hasn't found it yet, to be nice to the player.
	 * @param player the player taking the Deathmatch
	 * @param dmInfo the Deathmatch's Info
	 */
	void spawnDailyMonster(final Player player, final DeathmatchInfo dmInfo) {
		String dailyInfo = player.getQuest("daily", 0);
		if (dailyInfo != null) {
		    if (dailyInfo.startsWith("done")) {
		        return;
		    }
			boolean questDone = new KilledForQuestCondition("daily", 0).fire(player, null, null);
			if (!questDone) {
				final String[] dTokens = dailyInfo.split(",");
				if (dTokens.length > 0) {
					final String daily = dTokens[0];

					for (final Creature creature : sortedCreatures) {
						if (creature.getName().equals(daily)) {
							spawnNewCreature(creature, player, dmInfo);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Calculates which type of creature should be spawned next.
	 *
	 * @param questLevel level of creature / deathmatch status
	 * @return creature template
	 */
	public Creature calculateNextCreature(final int questLevel) {
		final List<Creature> possibleCreaturesToSpawn = new ArrayList<Creature>();
		int lastLevel = 0;

		for (final Creature creature : sortedCreatures) {
			if (creature.getLevel() > questLevel) {
				break;
			}

			if (creature.getLevel() > lastLevel) {
				possibleCreaturesToSpawn.clear();
				lastLevel = creature.getLevel();
			}

			possibleCreaturesToSpawn.add(creature);
		}

		Creature creatureToSpawn = null;

		if (possibleCreaturesToSpawn.size() == 0) {
			creatureToSpawn = sortedCreatures.get(sortedCreatures.size() - 1);
		} else {
			Collections.shuffle(possibleCreaturesToSpawn);
			creatureToSpawn = possibleCreaturesToSpawn.get(0);
		}

		return creatureToSpawn;
	}

	/**
	 * Creates a new creature of the named type and adds it to the world.
	 *
	 * @param template Creature to create
	 * @param player the player who takes the deatchmatch
	 * @param deathmatchInfo the deatchmatch's info
	 * @return Creature or <code>null</code> in case it cannot be created
	 */
	DeathMatchCreature spawnNewCreature(final Creature template, final Player player, final DeathmatchInfo deathmatchInfo) {
		DeathMatchCreature creature = new DeathMatchCreature(
		        new ArenaCreature(template.getNewInstance(), deathmatchInfo.getArena().getShape()), deathmatchInfo);

		if (StendhalRPAction.placeat(deathmatchInfo.getZone(), creature, player.getX(), player.getY(), deathmatchInfo.getArena().getShape())) {
			creature.clearDropItemList();
			creature.setTarget(player);
			creature.setPlayerToReward(player);

			spawnedCreatures.add(creature);
		} else {
			logger.info(" could not add a creature: " + creature);
			creature = null;
		}

		return creature;
	}

	DeathMatchCreature spawnNewCreature(final int questLevel , final Player player, final DeathmatchInfo deathmatchInfo) {
		return spawnNewCreature(calculateNextCreature(questLevel), player,  deathmatchInfo);
	}

}
