package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.action.StateRequiredItemAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


/**
 * QUEST: Ultimate Collector
 * <p>
 * PARTICIPANTS: <ul><li> Balduin  </ul>
 * 
 * STEPS: 
 * <ul><li> Balduin challenges you to be the ultimate weapons collector 
 *     <li> Balduin asks you to complete each quest where you win a rare item
 *	   <li> Balduin asks you to bring him one extra rare item from a list
 *</ul>
 * 
 * REWARD: <ul>
 * <li> You can sell black items to Balduin 
 * <li> 100000 XP </ul>
 * 
 * REPETITIONS: <ul><li> None. </ul>
 */
public class UltimateCollector extends AbstractQuest {

	/** Quest slot for this quest, the Ultimate Collector */
	private static final String QUEST_SLOT = "ultimate_collector";
	
	/** Club of Thorns in Kotoch: The Orc Saman is the NPC */
	private static final String CLUB_THORNS_QUEST_SLOT = "club_thorns"; // kotoch
	
	/** Vampire Sword quest: Hogart is the NPC */
	private static final String VAMPIRE_SWORD_QUEST_SLOT = "vs_quest"; // dwarf blacksmith
	
	/** Obsidian Knife quest: Alrak is the NPC */
	private static final String OBSIDIAN_KNIFE_QUEST_SLOT = "obsidian_knife"; // dwarf blacksmith
	
	/** Immortal Sword Quest in Kotoch: Vulcanus is the NPC */
	private static final String IMMORTAL_SWORD_QUEST_SLOT = "immortalsword_quest"; // kotoch
	
	/** Mithril Cloak quest: Ida is the NPC */
	private static final String MITHRIL_CLOAK_QUEST_SLOT = "mithril_cloak"; // mithril
	
	/** Mithril Shield quest: Baldemar is the NPC */
	private static final String MITHRIL_SHIELD_QUEST_SLOT = "mithrilshield_quest"; // mithril
	
	/** Cloak Collector 2nd quest: Josephine is the NPC (Completing 2nd requires 1st) */
	private static final String CLOAKSCOLLECTOR2_QUEST_SLOT = "cloakscollector2"; // cloaks
	
	/** Cloaks For Bario (Freezing Dwarf) quest: Bario is the NPC  */
	private static final String CLOAKS_FOR_BARIO_QUEST_SLOT = "cloaks_for_bario"; // cloaks
	
	// private static final String HELP_TOMI_QUEST_SLOT = "help_tomi"; don't require
	
	/** Elvish Armor quest: Lupos is the NPC */
	private static final String ELVISH_ARMOR_QUEST_SLOT = "elvish_armor"; // specific for this one
	
	/** Kanmararn Soldiers quest: Henry is the NPC  */
	private static final String KANMARARN_QUEST_SLOT = "soldier_henry"; // specific for this one
	
	/** Weapons Collector 2nd quest: Balduin is the NPC (Completing 2nd requires 1st) */
	private static final String WEAPONSCOLLECTOR2_QUEST_SLOT = "weapons_collector2";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
			return res;
		}
		res.add("QUEST_ACCEPTED");
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private void checkCollectingQuests() {
		final SpeakerNPC npc = npcs.get("Balduin");


		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Greetings old friend. I have another collecting #challenge for you.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new OrCondition(new QuestNotCompletedCondition(CLUB_THORNS_QUEST_SLOT),
							 new QuestNotCompletedCondition(IMMORTAL_SWORD_QUEST_SLOT))),
			ConversationStates.ATTENDING, 
			"There is still a quest in the Kotoch area which you have not completed. Explore thoroughly and you will be on your way to becoming the ultimate collector!",
			null);


		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new OrCondition(new QuestNotCompletedCondition(MITHRIL_CLOAK_QUEST_SLOT),
							 new QuestNotCompletedCondition(MITHRIL_SHIELD_QUEST_SLOT))),
			ConversationStates.ATTENDING, 
			"You are missing a special mithril item which you can win if you help the right person, you cannot be an ultimate collector without it.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new OrCondition(new QuestNotCompletedCondition(OBSIDIAN_KNIFE_QUEST_SLOT),
							 new QuestNotCompletedCondition(VAMPIRE_SWORD_QUEST_SLOT))),
			ConversationStates.ATTENDING, 
			"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new OrCondition(new QuestNotCompletedCondition(CLOAKSCOLLECTOR2_QUEST_SLOT),
							 new QuestNotCompletedCondition(CLOAKS_FOR_BARIO_QUEST_SLOT))),
			ConversationStates.ATTENDING, 
			"A special item will be yours if you collect many cloaks, whether to fulfil another's vanity or keep them warm, it's a task you must complete.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new QuestNotCompletedCondition(ELVISH_ARMOR_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favour for him, you cannot be the ultimate collector.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge", 
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					 new QuestNotStartedCondition(QUEST_SLOT),
					 new QuestNotCompletedCondition(KANMARARN_QUEST_SLOT)),
			ConversationStates.ATTENDING, 
			"You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
			null);
		
	}
	
	private void requestItem() {
		
		final SpeakerNPC npc = npcs.get("Balduin");
		final Map<String,Integer> items = new HashMap<String, Integer>();
		
		// the numbers are based on depo's metric for rarity (bigger number = more rare) which may be out of date https://sourceforge.net/tracker/?func=detail&aid=2066597&group_id=1111&atid=973767
		// nothing rarer than a demon fire sword, and not included items which are quest rewards elsewhere
		items.put("nihonto",1); // 5169
		items.put("magic twoside axe",1); // 1010
		items.put("imperator sword",1); // 2393
		items.put("durin axe",1); // 4331
		items.put("vulcano hammer",1); // 4474
		items.put("xeno sword",1); // 1347
		items.put("black scythe",1); // 3918 (pretty sure this is rarer now but a lot of old ones about to buy)
		items.put("chaos dagger",1); // 1691
		items.put("black sword",1); // 6285
		
		// If all quests are completed, ask for an item
		npc.add(ConversationStates.ATTENDING,
				"challenge", 
				new AndCondition(
						new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT),
						new QuestCompletedCondition(KANMARARN_QUEST_SLOT),
						new QuestCompletedCondition(ELVISH_ARMOR_QUEST_SLOT),
						new QuestCompletedCondition(CLOAKSCOLLECTOR2_QUEST_SLOT),
						new QuestCompletedCondition(CLOAKS_FOR_BARIO_QUEST_SLOT),
						new QuestCompletedCondition(OBSIDIAN_KNIFE_QUEST_SLOT),
						new QuestCompletedCondition(VAMPIRE_SWORD_QUEST_SLOT),
						new QuestCompletedCondition(MITHRIL_CLOAK_QUEST_SLOT),
						new QuestCompletedCondition(MITHRIL_SHIELD_QUEST_SLOT),
						new QuestCompletedCondition(CLUB_THORNS_QUEST_SLOT),
						new QuestCompletedCondition(IMMORTAL_SWORD_QUEST_SLOT)),
				ConversationStates.ATTENDING, 
				null,
				new StartRecordingRandomItemCollectionAction(QUEST_SLOT, items, "Well, you've certainly proved to the residents of Faiumoni " +
						"that you could be the ultimate collector, but I have one more task for you. Please bring me [quplnoun(amount,name)]."));
	}
	
	private void collectItem() {
		
		final SpeakerNPC npc = npcs.get("Balduin");
		
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUEST_ITEM_QUESTION, 
				"Did you bring me that very rare item I asked you for?",
				null);
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES, 
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT))),
				ConversationStates.ATTENDING, 
				null,
				new StateRequiredItemAction(QUEST_SLOT, "Hm, no, you don't have [item], don't try to fool me!"));
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES, 
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, 
				"Wow, it's incredible to see this close up! Many thanks. Now, perhaps we can #deal together.",
				new MultipleActions(new DropRecordedItemAction(QUEST_SLOT), 
									new SetQuestAction(QUEST_SLOT, "done"),
									new IncreaseXPAction(100000)));
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new StateRequiredItemAction(QUEST_SLOT, "Very well, come back when you have [the item] with you."));
		
		
	}

	private void offerSteps() {
  		final SpeakerNPC npc = npcs.get("Balduin");

		// player returns after finishing the quest and says offer
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I buy black items, but I can only afford to pay you modest prices.",
				null);


		// player returns when the quest is in progress and says offer
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'll buy black items from you when you have completed each #challenge I set you.", null);
	}

	
	@Override
	public void addToWorld() {
		super.addToWorld();

		checkCollectingQuests();
		requestItem();
		collectItem();
		offerSteps();

	}

	@Override
	public String getName() {
		return "UltimateCollector";
	}
	
	// This is the max level of the min levels for the other quests
	@Override
	public int getMinLevel() {
		return 100;
	}
}
