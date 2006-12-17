package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.ArenaCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.Area;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObjectNotFoundException;

import org.apache.log4j.Logger;


/**
 * Creating the Stendhal Deathmatch Game
 */
// TODO: Split this class into the Ados part and the live deathmatch and clean that up
public class Deathmatch {
	private static final String ZONE_NAME = "0_ados_wall_n";

	protected static Logger logger = Logger.getLogger(Deathmatch.class);
	private NPCList npcs = NPCList.get();
	private StendhalRPZone zone = null;
	protected Area arena = null;

	class ScriptAction implements TurnListener {
		private Player player;
		private List<Creature> sortedCreatures = new LinkedList<Creature>();
		private List<Creature> spawnedCreatures = new ArrayList<Creature>();
		private boolean keepRunning = true;
		public ScriptAction(Player player) {
			this.player = player;
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
				if(questLast != null && (new Date()).getTime() - new Long( questLast) > bailDelay ) {
					questState = "cancel";
					player.setQuest("deathmatch", questState);
					// We assume that the player only carries one trophy helmet.
					Item helmet	= player.getFirstEquipped("trophy_helmet");
					if(helmet != null) {
						int defense = 1;
						if(helmet.has("def")) {
							defense = new Integer(helmet.get("def"));
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
					StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(ZONE_NAME);
					player.teleport(zone, 96, 75, null, player);
				}
			}
			if("cancel".equals(questState)) {
				// remove the critters that the player was supposed to kill
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
				// and finally remove this ScriptAction 
				keepRunning = false;
				return;
			}
			// save a little processing time and do things only every spawnDelay miliseconds 
			if(questLast != null && (new Date()).getTime() - new Long( questLast) > spawnDelay )
				{
				int currentLevel = new Integer( questLevel );
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
										add(zone, creature, x, y);
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
					int k = new Integer(questLevel);
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
					Creature mycreature = add(zone, creatureToSpawn, x, y);
					if (mycreature != null) {
						mycreature.clearDropItemList();
						mycreature.attack(player);
						spawnedCreatures.add(mycreature);
						questLevel = Integer.toString(currentLevel + 1);
					}
				}			
				player.setQuest("deathmatch", questState + ";" + questLevel + ";" + (new Date()).getTime());
			}
		}
	}



	class StartAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			engine.say("Have fun!");
			int level = player.getLevel() - 2;
			if(level < 1) {
				level = 1;
			}
			player.setQuest("deathmatch", "start;"+ level + ";" + (new Date()).getTime());
			ScriptAction scriptingAction = new ScriptAction(player);
			TurnNotifier.get().notifyInTurns(0, scriptingAction, null);
		}
	}

	class DoneAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {		
			engine.say("You think you did it?");
			String questInfo = player.getQuest("deathmatch");
			String[] tokens = (questInfo+";0;0").split(";");
			String questState = tokens[0];
			String questLevel = tokens[1];
			String questLast	= tokens[2];
			if("victory".equals(questState)) {
				boolean isNew = false;
					// We assume that the player only carries one trophy helmet.
				Item helmet	= player.getFirstEquipped("trophy_helmet");
				if(helmet == null) {
					helmet = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("trophy_helmet");
					engine.say("Congratulations! Here is your special trophy helmet. Enjoy it. Now, tell me if you want to #leave.");
					isNew = true;
				}
				else {
					engine.say("Congratulations! And your helmet has been magically strengthened. Now, tell me if you want to #leave.");
				}
				int defense = 1;
				if(helmet.has("def")) {
					defense = new Integer(helmet.get("def"));
				}
				defense++;
				int maxdefense = 5 + (player.getLevel() / 5);
				if(defense > maxdefense) {
						engine.say("Congratulations! However, I'm sorry to inform you, the maximum defense for your helmet at your current level is " + maxdefense);
						helmet.put("def",""+maxdefense);					
						
				}
				else {
						helmet.put("def",""+defense);				
				}
				helmet.put("infostring",player.getName());
				helmet.put("persistent",1);
				helmet.setDescription("This is " + player.getName() +	"'s grand prize for Deathmatch winners. Wear it with pride.");
				if(isNew) {
					player.equip(helmet, true);
				}
				player.updateItemAtkDef();
				player.setQuest("deathmatch", "done");
			}
			else {
				engine.say("C'm on, don't lie to me! All you can do now is #bail or win.");
			}
			return;
		}
	}

	class LeaveAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {	 
			if("done".equals(player.getQuest("deathmatch"))) {
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_plains_n");
				player.teleport(zone, 100, 115, null, player);				
			} else {
				engine.say("I don't think you claimed your #victory yet.");
			}
			return;
		}
	}

	class BailAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			String questInfo = player.getQuest("deathmatch");
			if (questInfo == null) {
					engine.say("Coward, you haven't even #started!");
					return;
			}
			String[] tokens = (questInfo+";0;0").split(";");
			String questState = tokens[0];
			String questLevel = tokens[1];
			String questLast	= tokens[2];
			if(!"start".equals(questState)) {
				engine.say("Coward, we haven't even #started!");
				return;
			}
			player.setQuest("deathmatch", "bail;"+ questLevel + ";" + (new Date()).getTime());
			// We assume that the player only carries one trophy helmet.
			Item helmet	= player.getFirstEquipped("trophy_helmet");
			if(helmet != null) {
				engine.say("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.");
			}
			else {
				engine.say("Coward! You're not as experienced as you used to be.");
			}
			return;
		}
	}

	private void createArena(StendhalRPZone zone) {
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setRect(88, 77, 112-88+1, 93-77+1);
		arena = new Area(zone, shape);
	}

	private void createHelmet(StendhalRPZone zone) {

		// show the player the potential trophy
		Item helmet = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("trophy_helmet");
		zone.assignRPObjectID(helmet);
		helmet.put("def", "20");
		helmet.setDescription("This is the grand prize for Deathmatch winners.");
		helmet.setX(102);
		helmet.setY(75);
		helmet.put("persistent", 1);
		zone.add(helmet);
	}

	private void createNPC(StendhalRPZone zone) {
		
		// We create an NPC
		SpeakerNPC npc=new SpeakerNPC("Thanatos") {

			@Override
			protected void createPath() {
				setPath(new ArrayList<Path.Node>(), false);
			}

			@Override
			protected void createDialog() {

				// player is outside the fence
				add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES,
						new StandardInteraction.Not(new StandardInteraction.PlayerInAreaCondition(arena)),
						ConversationStates.INFORMATION_1, "Welcome to Ados Deathmatch! Please talk to #Thonatus if you want to join", null);
				add(ConversationStates.INFORMATION_1, "Thonatus", null, ConversationStates.INFORMATION_1,
						"Thonatus is the official Deathmatch Recrutor. He is in the swamp south west of Ados.", null);


				// player is inside
				add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES, new StandardInteraction.PlayerInAreaCondition(arena),
						ConversationStates.ATTENDING, "Welcome to Ados Deathmatch! Do you need #help?", null);
				addJob("I'm the deathmatch assistant. Tell me, if you need #help on that.");
				addHelp("Say '#start' when you're ready! Keep killing #everything that #appears. Say 'victory' when you survived.");
				addGoodbye("I hope you enjoy the Deathmatch!");

				add(ConversationStates.ATTENDING, Arrays.asList("everything", "appears"), ConversationStates.ATTENDING, 
						"Each round you will face stronger enemies. Defend well, kill them or tell me if you want to #bail!", null);
				add(ConversationStates.ATTENDING, Arrays.asList("trophy","helm","helmet"), ConversationStates.ATTENDING,
						"If you win the deathmatch, we reward you with a trophy helmet. Each #victory will strengthen it.", null);

				// 'start' command will start spawning creatures
				add(ConversationStates.ATTENDING, Arrays.asList("start", "go", "fight"), null, 
						ConversationStates.ATTENDING, null, new StartAction());
				
				// 'victory' command will scan, if all creatures are killed and reward the player
				add(ConversationStates.ATTENDING, Arrays.asList("victory", "done", "yay"), null,
						ConversationStates.ATTENDING, null, new DoneAction());
				
				// 'leave' command will send the victorious player home
				add(ConversationStates.ATTENDING, Arrays.asList("leave", "home"), null, 
						ConversationStates.ATTENDING, null, new LeaveAction());
				
				// 'bail' command will teleport the player out of it
				add(ConversationStates.ATTENDING, Arrays.asList("bail", "flee", "run", "exit"), null,
						ConversationStates.ATTENDING, null, new BailAction());
			}

		};

		
		npc.put("class", "darkwizardnpc");
		npc.set(98, 75);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		zone.addNPC(npc);
	}

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(ZONE_NAME));
	
		createArena(zone);
		createHelmet(zone);
		createNPC(zone);
	}

	private Creature add(StendhalRPZone zone, Creature template, int x, int y) {
		Creature creature = new ArenaCreature(template.getInstance(), arena.getShape());
		zone.assignRPObjectID(creature);
		if (StendhalRPAction.placeat(zone, creature, x, y, arena.getShape())) {
			zone.add(creature);
			StendhalRPRuleProcessor.get().addNPC(creature);
		} else {
			logger.info(" could not add a creature: " + creature);
			creature = null;
		}
		return creature;
	}

}
