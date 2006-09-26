/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.creature.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.server.StendhalRPZone;

import org.apache.log4j.Logger;

// Creating the Stendhal Deathmatch Game
logger.debug("Starting Stendhal Deathmatch Script") 
			 

class DeathmatchCondition extends ScriptCondition {
	Player player;
	ScriptInGroovy game;
	public DeathmatchCondition (Player player, ScriptInGroovy game) {
		this.player = player;
		this.game = game;
	}
	public boolean fire() {
		if("cancel".equals(player.getQuest("deathmatch"))) {
			return false;
		}
		if(player.getQuest("deathmatch").startsWith("done")) {
			return false;
		}
		if(game.playerIsInZone(player, "int_semos_deathmatch")) {
			return (true);
		}
		else {
			player.setQuest("deathmatch", "cancel");
			return(true);
		}
		return false;
	}
}

class DeathmatchAction extends ScriptAction {
	ScriptInGroovy game;
	Player player;
	List sortedCreatures;
	List spawnedCreatures = [];
	public DeathmatchAction (Player player, ScriptInGroovy game) {
		this.player = player;
		this.game = game;
		List creatures = game.getCreatures().toList();
		sortedCreatures = creatures.sort { it.getLevel() }
	}
	public void fire() {
		String questInfo = player.getQuest("deathmatch")
		List tokens = (questInfo + ";0;0").tokenize(";");
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
				game.transferPlayer(player,	"0_semos_plains_n", 100, 115);
			}
		}
		if("cancel".equals(questState)) {
			// remove the critters that the player was supposed to kill
			for (creature in spawnedCreatures) {
				game.remove(creature);
			}
			// and finally remove this ScriptAction 
			game.remove(this);
			return;
		}
		// save a little processing time and do things only every spawnDelay miliseconds 
		if(questLast != null && (new Date()).getTime() - new Long( questLast) > spawnDelay )
			{
			int currentLevel = new Integer( questLevel );
			if(currentLevel > player.getLevel() + 7) {
				boolean done = true;
				// check if all our enemies are dead
				for (creature in spawnedCreatures) {
					if(creature.getHP()>0) {
						done = false;
					}
				}
				if(done) {
					// be nice to the player and give him his daily quest creature
					// if he hasn't found it yet
					String dailyInfo = player.getQuest("daily");
					if(dailyInfo != null) {
						List dTokens = dailyInfo.tokenize(";");
						String daily = dTokens[0];
						if(!player.hasKilled(daily)) {
							for (creature in sortedCreatures) {
								if (creature.getName().equals(daily)) {
									int x = player.getX() + 1; 
									int y = player.getY() + 1;
									game.add(creature, x, y);
									break;
									}
							}
						}
					}
					questState = "victory";
					// remove this ScriptAction since we're done
					game.remove(this);
				}
			} else {
				// spawn the next stronger creature
				int k = new Integer(questLevel);
				List possibleCreaturesToSpawn = new ArrayList();
				int lastLevel = 0;
				for (creature in sortedCreatures) {
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
				Creature mycreature = game.add(creatureToSpawn, x, y);
				if (mycreature != null) {
					mycreature.clearDropItemList();
					mycreature.attack(player);
					spawnedCreatures.add(mycreature);
					questLevel = currentLevel + 1;
				}
			}			
			player.setQuest("deathmatch", questState + ";" + questLevel + ";" + (new Date()).getTime());
		}
	}
}



class StartAction extends SpeakerNPC.ChatAction {
	ScriptInGroovy game;
	public StartAction ( ScriptInGroovy game) {
		this.game = game;
	}
	public void fire(Player player, String text, SpeakerNPC engine) {
		engine.say("Have fun!");
		int level = player.getLevel() - 2;
		if(level < 1) {
			level = 1;
		}
		player.setQuest("deathmatch", "start;"+ level + ";" + (new Date()).getTime());
		game.add(new DeathmatchCondition(player, game), new DeathmatchAction(player, game));
		return;
	}
}

class DoneAction extends SpeakerNPC.ChatAction {
	ScriptInGroovy game;
	public DoneAction ( ScriptInGroovy game) {
		this.game = game;
	}
	public void fire(Player player, String text, SpeakerNPC engine) {		
		engine.say("You think you did it?");
		String questInfo = player.getQuest("deathmatch")
		List tokens = (questInfo+";0;0").tokenize(";");
		String questState = tokens[0];
		String questLevel = tokens[1];
		String questLast	= tokens[2];
		if("victory".equals(questState)) {
			boolean isNew = false;
				// We assume that the player only carries one trophy helmet.
			Item helmet	= player.getFirstEquipped("trophy_helmet");
			if(helmet == null) {
				helmet = game.getItem("trophy_helmet");
				engine.say("Congratulations! Here is your special trophy helmet. Enjoy it. Now, tell me if you want to #leave.");
				isNew = true;
			}
			else {
				engine.say("Congratulations! And your helmet has been magically strengthened. Now, tell me if you want to #leave.")
			}
			int defense = 1;
			if(helmet.has("def")) {
				defense = new Integer(helmet.get("def"));
			}
			defense++;
			int maxdefense = 5 + (player.getLevel() / 5).intValue();
			if(defense > maxdefense) {
					engine.say("Congratulations! However, I'm sorry to inform you, the maximum defense for your helmet at your current level is " + maxdefense)
					helmet.put("def",""+maxdefense)					
					
			}
			else {
					helmet.put("def",""+defense)					
			}
			helmet.put("infostring",player.getName())
			helmet.put("persistent",1)
			helmet.setDescription("This is " + player.getName() +	"'s grand prize for Deathmatch winners. Wear it with pride.")
			if(isNew) {
				player.equip(helmet, true);
			}
			player.updateItemAtkDef();
			player.setQuest("deathmatch", "done");
		}
		else {
			engine.say("C'm on, don't lie to me! All you can do now is #bail or win.")
		}
		return;
	}
}

class LeaveAction extends SpeakerNPC.ChatAction {
	ScriptInGroovy game;
	public LeaveAction ( ScriptInGroovy game) {
		this.game = game;
	}
	public void fire(Player player, String text, SpeakerNPC engine) {	 
		if("done".equals(player.getQuest("deathmatch"))) {
			game.transferPlayer(player,	"0_semos_plains_n", 100, 115);
		} else {
			engine.say("I don't think you claimed your #victory yet.");
		}
		return;
	}
}

class BailAction extends SpeakerNPC.ChatAction {
	ScriptInGroovy game;
	public BailAction ( ScriptInGroovy game) {
		this.game = game;
	}
	public void fire(Player player, String text, SpeakerNPC engine) {
		String questInfo = player.getQuest("deathmatch");
		if (questInfo == null) {
				engine.say("Coward, you haven't even #started!");
				return;
		}
		List tokens = (questInfo+";0;0").tokenize(";");
		String questState = tokens[0];
		String questLevel = tokens[1];
		String questLast	= tokens[2];
		if(!"start".equals(questState)) {
			engine.say("Coward, we haven't even #started!")
			return;
		}
		player.setQuest("deathmatch", "bail;"+ questLevel + ";" + (new Date()).getTime());
		// We assume that the player only carries one trophy helmet.
		Item helmet	= player.getFirstEquipped("trophy_helmet");
		if(helmet != null) {
			engine.say("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.")
		}
		else {
			engine.say("Coward! You're not as experienced as you used to be.")	
		}
		return;
	}
}


myZone = "int_semos_deathmatch"

if(!game.setZone(myZone))	 // if zone doesn't exist
	{
	game.addZone(myZone)		 // add it
	}

if(game.setZone(myZone))		// if zone exists
	{	 
	// show the player the potential trophy
	Item helmet = game.getItem("trophy_helmet");
	helmet.put("def","20")
	helmet.setDescription("This is the grand prize for Deathmatch winners.")
	helmet.setX(17)
	helmet.setY(4)
	helmet.put("persistent",1)
	game.add(helmet);
	
	// We create an NPC
	npc=new ScriptingNPC("Deathmatch Assistant")
	npc.setClass("darkwizardnpc")
	node = {x,y | new Path.Node(x,y)}
	npc.setPath([node(17,11)])
	game.add(npc)

	npc.behave("greet","Welcome to Semos deathmatch! Do you need #help?")
	npc.behave("job","I'm the deathmatch assistant. Tell me, if you need #help on that.")
	npc.behave("help","Say '#start' when you're ready! Keep killing #everything that #appears. Say 'victory' when you survived.")
	npc.behave(["everything","appears"],"Each round you will face stronger enemies. Defend well, kill them or tell me if you want to #bail!")
	npc.behave(["trophy","helm","helmet"],"If you win the deathmatch, we reward you with a trophy helmet. Each #victory will strengthen it.")
	npc.behave("bye","I hope you enjoy the Deathmatch!")	
	
	// 'start' command will start spawning creatures
	npc.add (1,[ "start", "go", "fight" ],null,1,null, new StartAction(game));
	
	// 'victory' command will scan, if all creatures are killed and reward the player
	npc.add (1,[ "victory", "done", "yay" ],null,1,null, new DoneAction(game));
	
	// 'leave' command will send the victorious player home
	npc.add (1,[ "leave", "home" ],null,1,null, new LeaveAction(game));
	
	// 'bail' command will teleport the player out of it
	npc.add (1,[ "bail", "flee", "run", "exit" ],null,1,null, new BailAction(game));
	
	}
else
	{
 logger.error("Cannot set Zone " + myZone)
	}

logger.debug("Finished Stendhal Deathmatch Script")
