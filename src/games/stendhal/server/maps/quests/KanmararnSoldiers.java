package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerOwnsItemIncludingBankCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.SlotIsFullException;

import org.apache.log4j.Logger;

/**
 * QUEST:
 * <p>
 * Soldiers in Kanmararn.
 *
 * NOTE:
 * <p>
 * It also starts a quest that needs NPC McPegleg that is created. It doesn't
 * harm if that script is missing, just that the IOU cannot be delivered and
 * hence the player can't get cash
 *
 * PARTICIPANTS:
 * <li> Henry
 * <li> Sergeant James
 * <li> corpse of Tom
 * <li> corpse of Charles
 * <li> corpse of Peter
 *
 * STEPS:
 * <li> optional: speak to Sergeant James to get the task to find the map
 * <li> talk to Henry to get the task to find some proof that the other 3
 * soldiers are dead.
 * <li> collect the item in each of the corpses of the three other soldiers
 * <li> bring them back to Henry to get the map - bring the map to Sergeant
 * James
 *
 * REWARD:
 * <p>
 * from Henry:
 * <li> you can keep the IOU paper (for quest MCPeglegIOU)
 * <li> 2500 XP
 * <p>
 * from Sergeant James
 * <li> steel boots
 *
 * REPETITIONS:
 * <li> None.
 *
 * @see McPeglegIOU
 */
public class KanmararnSoldiers extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(KanmararnSoldiers.class);

	private static final String QUEST_SLOT = "soldier_henry";

	/**
	 * The maximum time (in seconds) until plundered corpses will be filled
	 * again, so that other players can do the quest as well.
	 */
	private static final int CORPSE_REFILL_SECONDS = 60;



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	/**
	 * A CorpseRefiller checks, in regular intervals, if the given corpse.
	 *
	 * @author daniel
	 *
	 */
	static class CorpseRefiller implements TurnListener {
		private final Corpse corpse;

		private final String itemName;

		private final String description;

		public CorpseRefiller(final Corpse corpse, final String itemName, final String description) {
			this.corpse = corpse;
			this.itemName = itemName;
			this.description = description;
		}

		public void start() {
			SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
		}

		private boolean equalsExpectedItem(final Item item) {
			if (!item.getName().equals(itemName)) {
				return false;
			}

			if (!item.getDescription().equals(description)) {
				return false;
			}

			return corpse.getName().equals(item.getInfoString());
		}

		public void onTurnReached(final int currentTurn) {
			boolean isStillFilled = false;
			// Check if the item is still in the corpse. Note that somebody
			// might have put other stuff into the corpse.
			for (final RPObject object : corpse.getSlot("content")) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (equalsExpectedItem(item)) {
						isStillFilled = true;
					}
				}
			}
			try {
				if (!isStillFilled) {
					// recreate the item and fill the corpse
					final Item item = SingletonRepository.getEntityManager().getItem(
							itemName);
					item.setInfoString(corpse.getName());
					item.setDescription(description);
					corpse.add(item);
					corpse.notifyWorldAboutChanges();
				}
			} catch (final SlotIsFullException e) {
				// ignore, just don't refill the corpse until someone removes
				// the other items from the corpse
				logger.warn("Quest corpse is full: " + corpse.getName());
			}
			// continue the checking cycle
			SingletonRepository.getTurnNotifier().notifyInSeconds(CORPSE_REFILL_SECONDS, this);
		}
	}

	static class HenryQuestAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			if (!player.isQuestCompleted(QUEST_SLOT)
					&& !"map".equals(player.getQuest(QUEST_SLOT))) {
				npc.say("Find my #group, Peter, Tom, and Charles, prove it and I will reward you. Will you do it?");
			} else {
				npc.say("I'm so sad that most of my friends are dead.");
				npc.setCurrentState(ConversationStates.ATTENDING);
			}
		}
	}

	static class HenryQuestNotCompletedCondition implements ChatCondition {
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("start"));
		}
	}

	static class HenryQuestCompletedCondition implements ChatCondition {
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return (player.hasQuest(QUEST_SLOT) && !player.getQuest(QUEST_SLOT).equals("start"));
		}
	}

	static class HenryQuestCompleteAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {

			final List<Item> allLeatherLegs = player.getAllEquipped("leather legs");
			Item questLeatherLegs = null;
			for (final Item leatherLegs : allLeatherLegs) {
				if ("tom".equalsIgnoreCase(leatherLegs.getInfoString())) {
					questLeatherLegs = leatherLegs;
					break;
				}
			}

			final List<Item> allNotes = player.getAllEquipped("note");
			Item questNote = null;
			for (final Item note : allNotes) {
				if ("charles".equalsIgnoreCase(note.getInfoString())) {
					questNote = note;
					break;
				}
			}

			final List<Item> allScaleArmors = player.getAllEquipped("scale armor");
			Item questScaleArmor = null;
			for (final Item scaleArmor : allScaleArmors) {
				if ("peter".equalsIgnoreCase(scaleArmor.getInfoString())) {
					questScaleArmor = scaleArmor;
					break;
				}
			}

			if ((questLeatherLegs != null) && (questNote != null)
					&& (questScaleArmor != null)) {
				npc.say("Oh my! Peter, Tom, and Charles are all dead? *cries*. Anyway, here is your reward. And keep the IOU.");
				player.addXP(2500);
				player.addKarma(15);
				player.drop(questLeatherLegs);
				player.drop(questScaleArmor);
				new GiveMapAction(false).fire(player, sentence, npc);
				npc.setCurrentState(ConversationStates.ATTENDING);
			} else {
				npc.say("You didn't prove that you have found them all!");
			}
		}
	}

	static class GiveMapAction implements ChatAction {
		private boolean bind = false;

		public GiveMapAction(boolean bind) {
			this.bind = bind;
		}

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			final Item map = SingletonRepository.getEntityManager().getItem("map");
			map.setInfoString(npc.getName());
			map.setDescription("You see a hand drawn map, but no matter how you look at it, nothing on it looks familiar.");
			if (bind) {
				map.setBoundTo(player.getName());
			}
			player.equipOrPutOnGround(map);
			player.setQuest(QUEST_SLOT, "map");
		}
	}


	static class JamesQuestCompleteAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {

			final List<Item> allMaps = player.getAllEquipped("map");
			Item questMap = null;
			for (final Item map : allMaps) {
				if ("henry".equalsIgnoreCase(map.getInfoString())) {
					questMap = map;
					break;
				}
			}
			if (questMap != null) {
				npc.say("The map! Wonderful! Thank you. And here is your reward.");
				player.addXP(5000);
				player.addKarma(15);
				player.drop(questMap);

				final Item item = SingletonRepository.getEntityManager().getItem(
						"steel boots");
				item.setBoundTo(player.getName());
				// Is this infostring really needed?
				item.setInfoString(npc.getName());
				player.equipToInventoryOnly(item);
				player.setQuest(QUEST_SLOT, "done");
				npc.setCurrentState(ConversationStates.ATTENDING);
			} else {
				npc.say("Well, where is the map?");
			}
		}
	}

	/**
	 * We create NPC Henry who will get us on the quest.
	 */
	private void prepareCowardSoldier() {
		final SpeakerNPC henry = npcs.get("Henry");

		henry.addGreeting("Ssshh! Silence or you will attract more #dwarves.");
		henry.addJob("I'm a soldier in the army.");
		henry.addGoodbye("Bye and be careful with all those dwarves around!");
		henry.addHelp("I need help myself. I got separated from my #group. Now I'm all alone.");
		henry.addReply(Arrays.asList("dwarf", "dwarves"),
			"They are everywhere! Their #kingdom must be close.");
		henry.addReply(Arrays.asList("kingdom", "Kanmararn"),
			"Kanmararn, the legendary city of the #dwarves.");
		henry.addReply("group",
			"The General sent five of us to explore this area in search for #treasure.");
		henry.addReply("treasure",
			"A big treasure is rumored to be #somewhere in this dungeon.");
		henry.addReply("somewhere", "If you #help me I might give you a clue.");

		henry.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null, new HenryQuestAction());

		henry.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you! I'll be waiting for your return.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		henry.add(
			ConversationStates.QUEST_OFFERED,
			"group",
			null,
			ConversationStates.QUEST_OFFERED,
			"The General sent five of us to explore this area in search for #treasure. So, will you help me find them?",
			null);

        henry.add(
				  ConversationStates.QUEST_OFFERED,
				  "treasure",
				  null,
				  ConversationStates.QUEST_OFFERED,
				  "A big treasure is rumored to be #somewhere in this dungeon. Will you help me find my group?",
				  null);

		henry.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"OK. I understand. I'm scared of the #dwarves myself.", 
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		henry.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			null, new HenryQuestCompleteAction());

		henry.add(ConversationStates.ATTENDING, Arrays.asList("map", "group", "help"), 
				new OrCondition(
					new	QuestCompletedCondition(QUEST_SLOT), 
					new AndCondition(new HenryQuestCompletedCondition(),
					new PlayerOwnsItemIncludingBankCondition("map"))),
			ConversationStates.ATTENDING,
			"I'm so sad that most of my friends are dead.", null);

		henry.add(ConversationStates.ATTENDING, Arrays.asList("map"), 
			new AndCondition(
				new	QuestNotCompletedCondition(QUEST_SLOT), 
				new HenryQuestCompletedCondition(),
				new NotCondition(new PlayerOwnsItemIncludingBankCondition("map"))),
			ConversationStates.ATTENDING,
			"Luckily i draw a copy of the map, but please don't lose this one.", 
			new GiveMapAction(true));


		henry.add(ConversationStates.ATTENDING, Arrays.asList("map"),
			new HenryQuestNotCompletedCondition(),
			ConversationStates.ATTENDING,
			"If you find my friends, I will give you the map", null);
	}

	/**
	 * add corpses of ex-NPCs.
	 */
	private void prepareCorpses() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("-6_kanmararn_city");

		// Now we create the corpse of the second NPC
		final Corpse tom = new Corpse("youngsoldiernpc", 5, 47);
		// he died first
		tom.setStage(4); 
		tom.setName("Tom");
		tom.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(tom);

		// Add a refiller to automatically fill the corpse of unlucky Tom
		final CorpseRefiller tomRefiller = new CorpseRefiller(tom, "leather legs",
				"You see torn leather legs that are heavily covered with blood.");
		tomRefiller.start();

		// Now we create the corpse of the third NPC
		final Corpse charles = new Corpse("youngsoldiernpc", 94, 5);
		// he died second
		charles.setStage(3); 
		charles.setName("Charles");
		charles.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(charles);
		// Add a refiller to automatically fill the corpse of unlucky Charles
		final CorpseRefiller charlesRefiller = new CorpseRefiller(charles, "note",
				"You read: \"IOU 250 money. (signed) McPegleg\"");
		charlesRefiller.start();

		// Now we create the corpse of the fourth NPC
		final Corpse peter = new Corpse("youngsoldiernpc", 11, 63);
		// he died recently
		peter.setStage(2); 
		peter.setName("Peter");
		peter.setKiller("a Dwarven patrol");
		// Add our new Ex-NPC to the game world
		zone.add(peter);
		// Add a refiller to automatically fill the corpse of unlucky Peter
		final CorpseRefiller peterRefiller = new CorpseRefiller(
				peter,
				"scale armor",
				"You see a slightly rusty scale armor. It is heavily deformed by several strong hammer blows.");
		peterRefiller.start();
	}

	/**
	 * add James.
	 */
	private void prepareSergeant() {
		final SpeakerNPC james = npcs.get("Sergeant James");

		// quest related stuff
		james.addHelp("Think I need a little help myself. My #group got killed and #one of my men ran away. Too bad he had the #map.");
		james.addQuest("Find my fugitive soldier and bring him to me ... or at least the #map he's carrying.");
		james.addReply("group",
			"We were five, three of us died. You probably passed their corpses.");
		james.addReply(Arrays.asList("one", "henry"),
			"Yes, my youngest soldier. He ran away.");
		james.addReply("map",
			"The #treasure map that leads into the heart of the #dwarven #kingdom.");
		james.addReply("treasure",
			"A big treasure is rumored to be somewhere in this dungeon.");
		james.addReply(Arrays.asList("dwarf", "dwarves", "dwarven", "dwarven kingdom"),
			"They are strong enemies! We're in their #kingdom.");
		james.addReply(Arrays.asList("peter", "tom", "charles"),
			"He was a good soldier and fought bravely.");
		james.addReply(Arrays.asList("kingdom", "kanmararn"),
			"Kanmararn, the legendary kingdom of the #dwarves.");

		james.add(ConversationStates.ATTENDING, Arrays.asList("map", "henry"),
			new QuestInStateCondition(QUEST_SLOT, "map"),
			ConversationStates.ATTENDING, null,
			new JamesQuestCompleteAction());
		
		james.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks again for bringing me the map!", null);
		
		james.add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks again for bringing me the map!", null);
		
		james.add(ConversationStates.ATTENDING, Arrays.asList("map", "henry",
			 "group", "one"),
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thanks again for bringing me the map!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareCowardSoldier();
		prepareCorpses();
		prepareSergeant();
	}

	@Override
	public String getName() {
		return "KanmararnSoldiers";
	}
	
	@Override
	public int getMinLevel() {
		return 40;
	}
}
