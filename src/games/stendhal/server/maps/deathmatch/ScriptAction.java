package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.ArenaCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.util.Area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObjectNotFoundException;

import org.apache.log4j.Logger;

class ScriptAction implements TurnListener {
	private static Logger logger = Logger.getLogger(ScriptAction.class);
	private final Player player;
	private final Area arena;
	private final String zoneName;
	private final StendhalRPZone zone;
	
	private List<Creature> sortedCreatures = new LinkedList<Creature>();
	private List<Creature> spawnedCreatures = new ArrayList<Creature>();
	private boolean keepRunning = true;

	/**
	 * Creates a new ScriptAction to handle the deathmatch logic.
	 *
	 * @param player Player for whom this match is created
	 * @param deathmatchInfo Information about the place of the deathmatch
	 */
	public ScriptAction(Player player, DeathmatchInfo deathmatchInfo) {
		this.player = player;
		this.arena = deathmatchInfo.getArena();
		this.zoneName = deathmatchInfo.getZoneName();
		this.zone = deathmatchInfo.getZone();
		Collection<Creature> creatures = StendhalRPWorld.get().getRuleManager().getEntityManager().getCreatures();
		sortedCreatures.addAll(creatures);
		Collections.sort(sortedCreatures, new Comparator<Creature>() {
			public int compare(Creature o1, Creature o2) {
				return o1.getLevel() - o2.getLevel();
			}
		});
	}

	public boolean condition() {
		if("cancel".equals(player.getQuest("deathmatch"))) {
			return false;
		}
		if(player.getQuest("deathmatch").startsWith("done")) {
			return false;
		}
		
		if (arena.contains(player)) {
			return true;
		} else {
			player.setQuest("deathmatch", "cancel");
			return true;
		}
	}

	public void onTurnReached(int currentTurn, String message) {
		if (condition()) {
			action();
		}
		if (keepRunning) {
			TurnNotifier.get().notifyInTurns(0, this, null);
		}
	}

	public void action() {
		String questInfo = player.getQuest("deathmatch");
		String[] tokens = (questInfo + ";0;0").split(";");
		String questState = tokens[0];
		String questLevel = tokens[1];
		String questLast	= tokens[2];
		long bailDelay = 2000;		// wait 2 seconds before bail takes effect
		long spawnDelay = 15000;	// spawn a new monster each 15 seconds
		// the player wants to leave the game
		// this is delayed so the player can see the taunting
		if("bail".equals(questState)) {
			if((questLast != null) && ((new Date()).getTime() - Long.parseLong(questLast) > bailDelay) ) {
				questState = "cancel";
				player.setQuest("deathmatch", questState);
				// We assume that the player only carries one trophy helmet.
				Item helmet	= player.getFirstEquipped("trophy_helmet");
				if(helmet != null) {
					int defense = 1;
					if(helmet.has("def")) {
						defense = helmet.getInt("def");
					}
					defense--;
					helmet.put("def",""+defense);
					player.updateItemAtkDef();
				}
				else {
					int xp = player.getLevel() * 80;
					if(xp > player.getXP()) {
						xp = player.getXP();
					}
					player.addXP(-xp);
				}	
				// send the player back to the entrance area
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(zoneName);
				player.teleport(zone, 96, 75, null, player);
				removePlayersMonsters(spawnedCreatures);
				keepRunning = false;
				return;
			}
		}
		if("cancel".equals(questState)) {
			removePlayersMonsters(spawnedCreatures);
			
			// and finally remove this ScriptAction 
			keepRunning = false;
			return;
		}

		
		// save a little processing time and do things only every spawnDelay miliseconds 
		if((questLast != null) && ((new Date()).getTime() - Long.parseLong(questLast) > spawnDelay) )
			{
			int currentLevel = Integer.parseInt(questLevel);
			if(currentLevel > player.getLevel() + 7) {
				boolean done = true;
				// check if all our enemies are dead
				for (Creature creature : spawnedCreatures) {
					if(creature.getHP()>0) {
						done = false;
					}
				}
				if(done) {
					// be nice to the player and give him his daily quest creature
					// if he hasn't found it yet
					String dailyInfo = player.getQuest("daily");
					if(dailyInfo != null) {
						String[] dTokens = dailyInfo.split(";");
						String daily = dTokens[0];
						if(!player.hasKilled(daily)) {
							for (Creature creature : sortedCreatures) {
								if (creature.getName().equals(daily)) {
									int x = player.getX() + 1; 
									int y = player.getY() + 1;
									add(zone, creature, x, y, player);
									break;
								}
							}
						}
					}
					questState = "victory";
					// remove this ScriptAction since we're done
					keepRunning = false;
				}
			} else {
				// spawn the next stronger creature
				int k = Integer.parseInt(questLevel);
				List<Creature> possibleCreaturesToSpawn = new ArrayList<Creature>();
				int lastLevel = 0;
				for (Creature creature : sortedCreatures) {
					if (creature.getLevel() > k) {
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
				} else if (possibleCreaturesToSpawn.size() == 1) {
					creatureToSpawn = possibleCreaturesToSpawn.get(0);
				} else {
					creatureToSpawn = possibleCreaturesToSpawn.get((int) (Math.random() * possibleCreaturesToSpawn.size()));
				}
				int x = player.getX(); 
				int y = player.getY();
				DeathMatchCreature mycreature = add(zone, creatureToSpawn, x, y, player);
				if (mycreature != null) {
					spawnedCreatures.add(mycreature);
					questLevel = Integer.toString(currentLevel + 1);
				}
			}			
			player.setQuest("deathmatch", questState + ";" + questLevel + ";" + (new Date()).getTime());
		}
	}

	private DeathMatchCreature add(StendhalRPZone zone, Creature template, int x, int y, Player player) {
		DeathMatchCreature creature = new DeathMatchCreature(new ArenaCreature(template.getInstance(), arena.getShape()));
		zone.assignRPObjectID(creature);
		if (StendhalRPAction.placeat(zone, creature, x, y, arena.getShape())) {
			zone.add(creature);
			StendhalRPRuleProcessor.get().addNPC(creature);

			creature.clearDropItemList();
			creature.attack(player);

		} else {
			logger.info(" could not add a creature: " + creature);
			creature = null;
		}
		return creature;
	}

	/**
	 * remove the critters that the player was supposed to kill
	 *
	 * @param spawnedCreatures list of creatures created for this deathmatch
	 */
	public void removePlayersMonsters(List<Creature> spawnedCreatures) {
		for (Creature creature : spawnedCreatures) {
			String id = creature.getID().getZoneID();
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(id);
			try {
				StendhalRPRuleProcessor.get().removeNPC(creature);
				zone.getNPCList().remove(creature);
				if (zone.has(creature.getID())) {
					zone.remove(creature);
				}
			} catch (RPObjectNotFoundException e) {
				logger.error(e, e);
			}
		}
	}

}
