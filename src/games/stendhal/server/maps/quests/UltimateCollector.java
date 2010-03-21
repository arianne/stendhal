package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

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
 * REWARD: <ul><li> You can sell black items to Balduin <li> XP </ul>
 * 
 * REPETITIONS: <ul><li> None. </ul>
 */
public class UltimateCollector extends AbstractQuest {

	private static final String QUEST_SLOT = "ultimate_collector";
	private static final String CLUB_THORNS_QUEST_SLOT = "club_thorns"; // kotoch
	private static final String VAMPIRE_SWORD_QUEST_SLOT = "vs_quest"; // dwarf blacksmith
	private static final String OBSIDIAN_KNIFE_QUEST_SLOT = "obsidian_knife"; // dwarf blacksmith
	private static final String IMMORTAL_SWORD_QUEST_SLOT = "immortalsword_quest"; // kotoch
	private static final String MITHRIL_CLOAK_QUEST_SLOT = "mithril_cloak"; // mithril
	private static final String MITHRIL_SHIELD_QUEST_SLOT = "mithrilshield_quest"; // mithril
	private static final String CLOAKSCOLLECTOR2_QUEST_SLOT = "cloakscollector2"; // cloaks
	private static final String CLOAKS_FOR_BARIO_QUEST_SLOT = "cloaks_for_bario"; // cloaks
	// private static final String HELP_TOMI_QUEST_SLOT = "help_tomi"; don't require
	private static final String ELVISH_ARMOR_QUEST_SLOT = "elvish_armor"; // specific for this one
	private static final String KANMARARN_QUEST_SLOT = "soldier_henry"; // specific for this one
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
			"Well, you've certainly proved to the residents of Faiumoni that you could be the ultimate collector, but I have one last challenge for you.",
			new SetQuestAction(QUEST_SLOT,"start"));
		
	}


	

	@Override
	public void addToWorld() {
		super.addToWorld();

		checkCollectingQuests();

	}

	@Override
	public String getName() {
		return "UltimateCollector";
	}

}
