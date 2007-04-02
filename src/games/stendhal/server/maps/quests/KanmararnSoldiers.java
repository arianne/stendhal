package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.common.game.SlotIsFullException;


/**
 * QUEST: Soldiers in Kanmararn 
 *
 * NOTE:
 * It also starts a quest that needs NPC McPegleg that is created.
 * It doesn't harm if that script is missing, just that the IOU cannot be
 * delivered and hence the player can't get cash
 *
 * PARTICIPANTS:
 *  - Henry
 *  - Sergeant James
 *  - corpse of Tom
 *  - corpse of Charles
 *  - corpse of Peter
 *
 * STEPS:
 *  - optional: speak to Sergeant James to get the task to find the map
 *  - talk to Henry to get the task to find some prove that the other 3 soldiers are dead.
 *  - collect the item in each of the corpse of the three other soldiers
 *  - bring them back to Herny to get the map
 *  - bring the map to Sergeant James 
 *
 * REWARD:
 *  - IOU (for quest MCPeglegIOU.java)
 *  - steel boots
 *
 * REPETITIONS:
 * - None.
 */
public class KanmararnSoldiers extends AbstractQuest {

	private static final Logger logger = Log4J.getLogger(KanmararnSoldiers.class);

	private static final String QUEST_SLOT = "soldier_henry";
	
	/**
	 * The maximum time (in seconds) until plundered corpses will be filled
	 * again, so that other players can do the quest as well.
	 */
	private static final int CORPSE_REFILL_SECONDS = 60;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	/**
	 * A CorpseRefiller checks, in regular intervals, if the given corpse
	 * @author daniel
	 *
	 */
	private class CorpseRefiller implements TurnListener {
		private Corpse corpse;
		private String itemName;
		private String description;
		
		public CorpseRefiller (Corpse corpse, String itemName, String description) {
			this.corpse = corpse;
			this.itemName = itemName;
			this.description = description;
		}
		
		public void start() {
			TurnNotifier.get().notifyInTurns(1, this, null);
		}
		
		private boolean equalsExpectedItem(Item item) {
			return item.getName().equals(itemName) && item.getDescription().equals(description) && item.has("infostring") && item.get("infostring").equals(corpse.get("name")); 
		}
		
		public void onTurnReached(int currentTurn, String message) {
			boolean isStillFilled = false;
			// Check if the item is still in the corpse. Note that somebody
			// might have put other stuff into the corpse.
			for (RPObject object: corpse.getSlot("content")) {
				if (object instanceof Item) {
					Item item = (Item) object;
					if (equalsExpectedItem(item)) {
						isStillFilled = true;
					}
				}
			}
			try {
				if (! isStillFilled) {
					// recreate the item and fill the corpse
					Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemName);
					item.put("infostring", corpse.get("name"));
					item.setDescription(description);
					corpse.add(item);
					corpse.notifyWorldAboutChanges();
				}
			} catch (SlotIsFullException e) {
				// ignore, just don't refill the corpse until someone removes
				// the other items from the corpse
				logger.warn("Quest corpse is full: " + corpse.get("name"));
			}
			// continue the checking cycle
			TurnNotifier.get().notifyInSeconds(CORPSE_REFILL_SECONDS, this, null);
        }
	}

	private class HenryQuestAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			if(!player.isQuestCompleted("soldier_henry") && !"map".equals(player.getQuest("soldier_henry"))) {
				engine.say("Find my #group, Peter, Tom and Charles, prove it and I will reward you. Will you do it?");
			} else {
				engine.say("I'm so sad that most of my friends are dead.");
				engine.setCurrentState(1);
			}
		}
	}

	private class HenryQuestAcceptAction extends SpeakerNPC.ChatAction {
		public void fire(Player player, String text, SpeakerNPC engine) {
			player.setQuest("soldier_henry","start");
		}
	}

	private class HenryQuestStartedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && player.getQuest("soldier_henry").equals("start"));
		}
	}

	private class HenryQuestNotCompletedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (!player.hasQuest("soldier_henry") || player.getQuest("soldier_henry").equals("start"));
		}
	}

	private class HenryQuestCompletedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && !player.getQuest("soldier_henry").equals("start"));
		}
	}

	private class HenryQuestCompleteAction extends SpeakerNPC.ChatAction {
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

			if((questLeatherLegs != null) && (questNote != null) && (questScaleArmor != null)) {
				engine.say("Oh my! Peter, Tom and Charles are all dead? *cries*. Anyway, here is your reward. And keep the IOU.");
				player.addXP(2500);
				player.drop(questLeatherLegs);
				player.drop(questScaleArmor);
				Item map = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("map");
				map.put("infostring", engine.get("name"));
				map.setDescription("You see a hand drawn map, but no matter how you look at it, nothing on it looks familiar.");
				player.equip(map);
				player.setQuest("soldier_henry","map");
				engine.setCurrentState(1);
			} else {
				engine.say("You didn't prove that you have found them all!");
			}
		}
	}

	private class JamesQuestCompleteCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.hasQuest("soldier_henry") && player.getQuest("soldier_henry").equals("map"));
		}
	}

	private class JamesQuestCompletedCondition extends SpeakerNPC.ChatCondition {
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.isQuestCompleted("soldier_henry"));
		}
	}

	private class JamesQuestCompleteAction extends SpeakerNPC.ChatAction {
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
				
				Item item = StendhalRPWorld.get()
				.getRuleManager().getEntityManager().getItem(
						"steel_boots");
				item.put("bound", player.getName());
				item.put("infostring", engine.get("name"));
				player.equip(item);
				player.setQuest("soldier_henry","done");
				engine.setCurrentState(1);
			} else {
				engine.say("Well, where is the map?");
			}
		}
	}

	/**
	 * We create NPC Henry who will get us on the quest
	 */
	private void step_1() {
		SpeakerNPC henry = npcs.get("Henry");
		henry.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task"), null, ConversationStates.QUEST_OFFERED, null, new HenryQuestAction());
		henry.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES,null, ConversationStates.ATTENDING, "Thank you! I'll be waiting for your return.", new HenryQuestAcceptAction());
		henry.add(ConversationStates.QUEST_OFFERED, "group", null, ConversationStates.QUEST_OFFERED, "The General sent five of us to explore this area in search for #treasure.", null);
		henry.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.ATTENDING, "Ok. I understand. I'm scared of the #dwarves myself.", null);
		henry.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new HenryQuestStartedCondition(), ConversationStates.ATTENDING, null, new HenryQuestCompleteAction());
		henry.add(ConversationStates.ATTENDING, Arrays.asList("map", "group", "help"), new HenryQuestCompletedCondition(), ConversationStates.ATTENDING, "I'm so sad that most of my friends are dead.", null);
		henry.add(ConversationStates.ATTENDING, Arrays.asList("map"), new HenryQuestNotCompletedCondition(), ConversationStates.ATTENDING, "If you find my friends, i will give you the map", null);
	}

	/**
	 * add corpses of ex-NPCs.
	 */
	private void step_2() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-6_kanmararn_city"));

		// Now we create the corpse of the second NPC
		Corpse tom = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 5, 47);
		tom.setDegrading(false);
		tom.setStage(4);	// he died first
		tom.put("name", "Tom");
		tom.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.assignRPObjectID(tom);
		zone.add(tom);

		// Add a refiller to automatically fill the corpse of unlucky Tom
		CorpseRefiller tomRefiller = new CorpseRefiller(tom, "leather_legs", "You see torn leather legs that are heavily covered with blood.");
		tomRefiller.start();

		// Now we create the corpse of the third NPC
		Corpse charles = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 94, 5);
		charles.setDegrading(false);
		charles.setStage(3);	// he died second
		charles.put("name", "Charles");
		charles.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.assignRPObjectID(charles);
		zone.add(charles);
		// Add a refiller to automatically fill the corpse of unlucky Charles
		CorpseRefiller charlesRefiller = new CorpseRefiller(charles, "note", "You read: \"IOU 250 gold. (signed) McPegleg\"");
		charlesRefiller.start();

		// Now we create the corpse of the fourth NPC
		Corpse peter = new QuestKanmararn.QuestCorpse("youngsoldiernpc", 11, 63);
		peter.setDegrading(false);
		peter.setStage(2);	// he died recently
		peter.put("name", "Peter");
		peter.put("killer", "a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.assignRPObjectID(peter);
		zone.add(peter);
		// Add a refiller to automatically fill the corpse of unlucky Peter
		CorpseRefiller peterRefiller = new CorpseRefiller(peter, "scale_armor", "You see a slightly rusty scale armor. It is heavily deformed by several strong hammer blows.");
		peterRefiller.start();
	}

	/**
	 * add James 
	 */
	private void step_3() {
		SpeakerNPC james = npcs.get("Sergeant James");

		// quest related stuff
		james.addHelp("Think I need a little help myself. My #group got killed and #one of my men ran away. Too bad he had the #map.");
		james.addQuest("Find my fugitive soldier and bring him to me ... or at least the #map he's carrying.");
		james.add(ConversationStates.ATTENDING, Arrays.asList("group"), ConversationStates.ATTENDING, "We were five, three of us died. You probably passed their corpses.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("one", "henry"), ConversationStates.ATTENDING, "Yes, my youngest soldier. He ran away.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("map"), ConversationStates.ATTENDING, "The #treasure map that leads into the heart of the #dwarven #kingdom.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("treasure"), ConversationStates.ATTENDING, "A big treasure is rumored to be somewhere in this dungeon.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("dwarf", "dwarves", "dwarven"), ConversationStates.ATTENDING, "They are strong enemies! We're in their #kingdom.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("peter", "tom", "charles"), ConversationStates.ATTENDING, "He was a good soldier and fought bravely.", null);
		james.add(ConversationStates.ATTENDING, Arrays.asList("kingdom", "Kanmararn"), ConversationStates.ATTENDING, "Kanmararn, the legendary kingdom of the #dwarves.", null);

		james.add(ConversationStates.ATTENDING, Arrays.asList("map", "henry"), new JamesQuestCompleteCondition(), 1, null, new JamesQuestCompleteAction());
		james.add(ConversationStates.ATTENDING, Arrays.asList("map", "henry", "quest", "task", "help", "group", "one"), new JamesQuestCompletedCondition(), ConversationStates.ATTENDING, "Thanks again for bringing me the map!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}


}
