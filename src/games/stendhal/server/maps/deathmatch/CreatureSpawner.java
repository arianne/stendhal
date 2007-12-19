package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.ArenaCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.creature.LevelBasedComparator;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


import org.apache.log4j.Logger;



public class CreatureSpawner implements TurnListener {
static final Logger logger = Logger.getLogger(CreatureSpawner.class);
	static final long SPAWN_DELAY = 15000; // spawn a new monster each 15 seconds
	static final long NUMBER_OF_CREATURES = 10;
	private List<Creature> sortedCreatures = new LinkedList<Creature>();
	private List<DeathMatchCreature> spawnedCreatures = new ArrayList<DeathMatchCreature>();
	CreatureSpawner() {
		Collection<Creature> creatures = StendhalRPWorld.get().getRuleManager().getEntityManager().getCreatures();
		sortedCreatures.addAll(creatures);
		Collections.sort(sortedCreatures, new LevelBasedComparator());
	}
	/**
	 * remove the critters that the player was supposed to kill
	 */
	public void removePlayersMonsters() {
		for (Creature creature : spawnedCreatures) {
			StendhalRPZone monsterZone = creature.getZone();

			if (monsterZone != null) {
				monsterZone.remove(creature);
			}
		}
	}
	/**
	 * check if all our enemies are dead
	 *
	 * @return true if all are dead, false otherwise
	 */
	boolean areAllCreaturesDead() {
		for (Creature creature : spawnedCreatures) {
			if (creature.getHP() > 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * be nice to the player and give him his daily quest creature
	 * if he hasn't found it yet
	 * @param player the player taking the Deathmatch
	 * @param dmInfo the Deathmatch's Info
	 */
	void spawnDailyMonster(Player player, DeathmatchInfo dmInfo) {
		String dailyInfo = player.getQuest("daily");
		if (dailyInfo != null) {
			String[] dTokens = dailyInfo.split(";");
			String daily = dTokens[0];

			if (!player.hasKilled(daily)) {
				for (Creature creature : sortedCreatures) {
					if (creature.getName().equals(daily)) {
						spawnNewCreature(creature, player, dmInfo);
						break;
					}
				}
			}
		}
	}
	/**
	 * Calculate which type of creature should be spawned next
	 *
	 * @param questLevel level of creature / deathmatch status
	 * @return creature template
	 */
	private Creature calculateNextCreature(int questLevel) {
		List<Creature> possibleCreaturesToSpawn = new ArrayList<Creature>();
		int lastLevel = 0;

		for (Creature creature : sortedCreatures) {
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
	 * creates a new creature of the named type and adds it to the world
	 *
	 * @param template Creature to create
	 * @param player the player who takes the deatchmatch
	 * @param deathmatchInfo the deatchmatch's info
	 * @return Creature or <code>null</code> in case it cannot be created
	 */
	DeathMatchCreature spawnNewCreature(Creature template, Player player, DeathmatchInfo deathmatchInfo) {
		DeathMatchCreature creature = new DeathMatchCreature(
		        new ArenaCreature(template.getInstance(), deathmatchInfo.getArena().getShape()));

		if (StendhalRPAction.placeat(deathmatchInfo.getZone(), creature, player.getX(), player.getY(), deathmatchInfo.getArena().getShape())) {
			creature.clearDropItemList();
			creature.attack(player);
			creature.setPlayerToReward(player);

			spawnedCreatures.add(creature);
		} else {
			logger.info(" could not add a creature: " + creature);
			creature = null;
		}

		return creature;
	}

	DeathMatchCreature spawnNewCreature(int questLevel , Player player, DeathmatchInfo deathmatchInfo) {
		return spawnNewCreature(calculateNextCreature(questLevel), player,  deathmatchInfo);
	}

	int calculatePoints() {
		int sum = 0;

		for (DeathMatchCreature creature : spawnedCreatures) {
			sum += creature.getDMPoints();
		}

		return sum;
	}
	public void onTurnReached(int currentTurn) {
		// TODO Auto-generated method stub

	}
}
