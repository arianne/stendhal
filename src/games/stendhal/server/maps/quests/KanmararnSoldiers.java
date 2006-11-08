package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;
import games.stendhal.server.scripting.ScriptInGroovy;
import games.stendhal.server.scripting.ScriptingNPC;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPSlot;


/**
 * QUEST: 
 * Creates a 6 step quest in Kanmararn
 * Note: it also starts a quest that needs NPC McPegleg that is created.
 * It doesn't harm if that script is missing, just that the IOU cannot be
 * delivered and hence the player can't get cash
 *
 * PARTICIPANTS:
 *
 * STEPS:
 *
 * REWARD:
 *
 * REPETITIONS:
 * - None.
 */
public class KanmararnSoldiers extends AbstractQuest {

	private static final String QUEST_SLOT = "soldier_henry";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	class CorpseEmptyCondition extends ScriptCondition {
		Corpse corpse;
		
		public CorpseEmptyCondition (Corpse corpse) {
			this.corpse = corpse;
		}
		
		public boolean fire() {
			return corpse.size() < 1;
		}
	}

	class CorpseFillAction extends ScriptAction {
		Corpse corpse;
		ScriptInGroovy game;
		String itemName;
		String description;
		public CorpseFillAction (Corpse corpse, ScriptInGroovy game, String itemName, String description) {
			this.corpse = corpse;
			this.game = game;
			this.itemName = itemName;
			this.description = description;
		}
		
		public void fire() {
			Item item = game.getItem(itemName);
			item.put("infostring",corpse.get("name"));
			item.setDescription(description);
			corpse.add(item);
		}
	}

	class HenryQuestAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			if(!player.isQuestCompleted("soldier_henry") && !"map".equals(player.getQuest("soldier_henry"))) {
				engine.say("Find my #group, Peter, Tom and Charles, prove it and I will reward you. Will you do it?");
			} else {
				engine.say("I'm so sad that most of my friends are dead.");
				engine.setCurrentState(1);
			}
		}
	}

	class HenryQuestAcceptAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			player.setQuest("soldier_henry","start");
		}
	}

	class HenryQuestCompleteCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && player.getQuest("soldier_henry").equals("start"));
		}
	}

	class HenryQuestCompletedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && !player.getQuest("soldier_henry").equals("start"));
		}
	}

	class HenryQuestCompleteAction extends SpeakerNPC.ChatAction {

		ScriptInGroovy game;
		
		public HenryQuestCompleteAction (ScriptInGroovy game) {
			this.game = game;
		}
		
		public void fire(Player player, String text, SpeakerNPC engine) {

			List<Item> allLeatherLegs = player.getAllEquipped("leather_legs");
			Item questLeatherLegs = null;
			for (Item leatherLegs : allLeatherLegs) {
				if (leatherLegs.has("infostring") && "tom".equalsIgnoreCase(leatherLegs.get("infostring"))) {
					questLeatherLegs = leatherLegs;
					break;
				}
			}

			List<Item> allNotes	= player.getAllEquipped("note");
			Item questNote = null;
			for (Item note : allNotes) {
				if (note.has("infostring") && "charles".equalsIgnoreCase(note.get("infostring"))) {
					questNote = note;
					break;
				}
			}
			
			List<Item> allScaleArmors = player.getAllEquipped("scale_armor");
			Item questScaleArmor = null;
			for (Item scaleArmor : allScaleArmors) {
				if (scaleArmor.has("infostring") && "peter".equalsIgnoreCase(scaleArmor.get("infostring"))) {
					questScaleArmor = scaleArmor;
					break;
				}
			}

			if(questLeatherLegs != null && questNote != null && questScaleArmor != null) {
				engine.say("Oh my! Peter, Tom and Charles are all dead? *cries*. Anyway, here is your reward. And keep the IOU.");
				player.addXP(2500);
				player.drop(questLeatherLegs);
				player.drop(questScaleArmor);
				Item map = game.getItem("map");
				map.put("infostring",engine.get("name"));
				map.setDescription("You see a hand drawn map, but no matter how you look at it, nothing on it looks familiar.");
				RPSlot slot=player.getSlot("bag");
				slot.add(map);
				player.setQuest("soldier_henry","map");
				engine.setCurrentState(1);
			} else {
				engine.say("You didn't prove that you have found them all!");
			}
		}
	}

	class JamesQuestCompleteCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && player.getQuest("soldier_henry").equals("map"));
		}
	}

	class JamesQuestCompletedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.isQuestCompleted("soldier_henry"));
		}
	}

	class JamesQuestCompleteAction extends SpeakerNPC.ChatAction {

		ScriptInGroovy game;
		
		public JamesQuestCompleteAction (ScriptInGroovy game) {
			this.game = game;
		}
		
		public void fire(Player player, String text, SpeakerNPC engine) {
		
			List<Item> allMaps = player.getAllEquipped("map");
			Item questMap = null;
			for (Item map : allMaps) {
				if (map.has("infostring") && "henry".equalsIgnoreCase(map.get("infostring"))) {
					questMap = map;
					break;
				}
			}
			if (questMap != null) {
				engine.say("The map! Wonderful! Thank you. And here is your reward.");
				player.addXP(5000);
				player.drop(questMap);
				Item item = game.getItem("steel_boots");
				item.put("infostring", engine.get("name"));
				RPSlot slot=player.getSlot("bag");
				slot.add(item);
				player.setQuest("soldier_henry","done");
				engine.setCurrentState(1);
			} else {
				engine.say("Well, where is the map?");
			}
		}
	}

/*
	private void step_1() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-6_kanmararn_city"));
//		 The quest is set in the first level of Kanmararn
		// We create NPC Henry who will get us on the quest
		SpeakerNPC henry=new ScriptingNPC("Henry");
		henry.setDescription("You see a young soldier who appears to be afraid.");
		// Set an outfit for this player
		henry.setClass("youngsoldiernpc");

		// Set the NPC path with the help of a Groovy closure
		node = {x,y | new Path.Node(x,y)};
		henry.setPath([node(57, 112), node(59, 112), node(59, 114)]);
		
		// Adds all the behaviour chat
		henry.behave("greet", "Ssshh! Silence or you will attract more #dwarves.");
		henry.behave("job", "I'm a soldier in the army.");
		henry.behave("help", "I need help myself. I got seperated from my #group. Now I'm all alone.");
		henry.behave("bye", "Bye and be careful with all those dwarves around!");
		henry.behave(Arrays.asList("dwarf", "dwarves"), "They are everywhere! Their #kingdom must be close.");
		henry.behave(Arrays.asList("kingdom", "Kanmararn"), "Kanmararn, the legendary city of the #dwarves.");
		henry.behave("group", "The General sent five of us to explore this area in search for #treasure.");
		henry.behave("treasure", "A big treasure is rumored to be #somewhere in this dungeon.");
		henry.behave("somewhere", "If you #help me I might give you a clue.");
		// Add the quest dependent chat
		henry.add (1, Arrays.asList("quest", "task"), null, 60, null, new HenryQuestAction());
		henry.add(60, SpeakerNPC.YES_MESSAGES,null, 1, "Thank you! I'll be waiting for your return.", new HenryQuestAcceptAction());
		henry.add(60, "group", null, 60, "The General sent five of us to explore this area in search for #treasure.", null);
		henry.add(60, "no", null, 1, "Ok. I understand. I'm scared of the #dwarves myself.", null);
		henry.add(0, Arrays.asList("hi", "hello", "greetings", "hola"), new HenryQuestCompleteCondition(), 1, null, new HenryQuestCompleteAction(game));
		henry.add(1, Arrays.asList("map", "group", "help"), new HenryQuestCompletedCondition(), 1, "I'm so sad that most of my friends are dead.", null);

		// Adjust level/hp and add our new NPC to the game world
		henry.setLevel(5);
		henry.setHP((int) (henry.getBaseHP() * 20 / 100));
		game.add(henry);
		
	}
	
	private void step_2() {
		// Now we create the corpse of the second NPC
		Corpse tom = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 5, 47);
		tom.setDegrading(false);
		tom.setStage(4);	// he died first
		tom.put("name", "Tom");
		tom.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		game.add(tom);
		// Add a script to automatically fill the corpse of unlucky Tom
		game.add(new CorpseEmptyCondition(tom), 
				new CorpseFillAction(tom, game, "leather_legs", "You see torn leather legs that are heavily covered with blood."));

		// Now we create the corpse of the third NPC
		Corpse charles = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 94, 5);
		charles.setDegrading(false);
		charles.setStage(3);	// he died second
		charles.put("name", "Charles");
		charles.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		game.add(charles);
		// Add a script to automatically fill the corpse of unlucky Charles
		game.add(new CorpseEmptyCondition(charles), 
				new CorpseFillAction(charles, game, "note", "You read: \"IOU 250 gold. (signed) McPegleg\""));
	 
		// Now we create the corpse of the fourth NPC
		Corpse peter = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 11, 63);
		peter.setDegrading(false);
		peter.setStage(2);	// he died recently
		peter.put("name", "Peter");
		peter.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		game.add(peter);
		// Add a script to automatically fill the corpse of unlucky Peter
		game.add(new CorpseEmptyCondition(peter), 
				new CorpseFillAction(peter, game, "scale_armor", "You see a slightly rusty scale armor. It is heavily deformed by several strong hammer blows."));
	}
	
	private void step_3() {
		// We create NPC James, the chief and last survivor of the quintet
		SpeakerNPC james = new ScriptingNPC("Sergeant James");
		james.setDescription("You see an officer who bears many signs of recent battles.");
		// Set an outfit for this player
		james.setClass("royalguardnpc");
		// Set the NPC path with the help of a Groovy closure
		node = {x,y | new Path.Node(x,y)}
		james.setPath([node(66, 45), node(66, 47)])

		// Adds all the behaviour chat
		james.behave("greet", "Good day, adventurer!");
		james.behave("job", "I'm a Sergeant in the army.");
		james.behave("help", "Think I need a little help myself. My #group got killed and #one of my men ran away. Too bad he had the #map.");
		james.behave("quest", "Find my fugitive soldier and bring him to me ... or at least the #map he's carrying.");
		james.behave("bye", "Good luck and better watch your back with all those dwarves around!");
		james.behave("group", "We were five, three of us died. You probably passed their corpses.");
		james.behave(Arrays.asList("one", "henry"), "Yes, my youngest soldier. He ran away.");
		james.behave("map", "The #treasure map that leads into the heart of the #dwarven #kingdom.");
		james.behave("treasure", "A big treasure is rumored to be somewhere in this dungeon.");
		james.behave(Arrays.asList("dwarf", "dwarves", "dwarven"), "They are strong enemies! We're in their #kingdom.");
		james.behave(Arrays.asList("peter", "tom", "charles"), "He was a good soldier and fought bravely.");
		james.behave(Arrays.asList("kingdom", "Kanmararn"), "Kanmararn, the legendary kingdom of the #dwarves.");

		james.add(1, Arrays.asList("map", "henry"), new JamesQuestCompleteCondition(), 1, null, new JamesQuestCompleteAction(game));
		james.add(1, Arrays.asList("map", "henry", "quest", "task", "help", "group", "one"), new JamesQuestCompletedCondition(), 1, "Thanks again for bringing me the map!", null);

		// Adjust level/hp and add our new NPC to the game world
		james.setLevel(20);
		james.setHP((int) (james.getBaseHP() * 75 / 100));
		game.add(james);
	}
	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
*/

}
