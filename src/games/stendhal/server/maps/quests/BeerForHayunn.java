package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Beer For Hayunn
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Hayunn Naratha (the veteran warrior in Semos)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Hayunn asks you to buy a beer from Margaret.</li>
 * <li>Margaret sells you a beer.</li>
 * <li>Hayunn sees your beer, asks for it and then thanks you.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>20 gold coins</li>
 * <li>Karma: 10</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class BeerForHayunn extends AbstractQuest {

	public static final String QUEST_SLOT = "beer_hayunn";

	

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if (("start".equals(questState) && player.isEquipped("beer"))
				|| "done".equals(questState)) {
			res.add("FOUND_ITEM");
		}
		if ("done".equals(questState)) {
			res.add("DONE");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"Thanks all the same, but I don't want to get too heavily into drinking; I'm still on duty, you know! I'll need my wits about me if a student shows up...",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thanks! I'll be right here, waiting. And guarding, of course.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh, well forget it then. I guess I'll just hope for it to start raining, and then stand with my mouth open.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"tavern",
			null,
			ConversationStates.QUEST_OFFERED,
			"If you don't know where the inn is, you could ask old Monogenes; he's good with directions. Are you going to help?",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"beer",
			null,
			ConversationStates.QUEST_OFFERED,
			"A bottle of cool beer from #Margaret will be more than enough. So, will you do it?",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Margaret",
			null,
			ConversationStates.QUEST_OFFERED,
			"Margaret is the pretty maid in the tavern, of course! Quite a looker, too... heh. Will you go for me?",
			null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestActiveCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("beer")),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Hey! Is that beer for me?", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestActiveCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("beer"))),
			ConversationStates.ATTENDING, 
			"Hey, I'm still waiting for that beer, remember? Anyway, what can I do for you?",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("beer"));
		reward.add(new EquipItemAction("money", 20));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(10));
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("beer"),
			ConversationStates.ATTENDING,
			"*glug glug* Ah! That hit the spot. Let me know if you need anything, ok?",
			new MultipleActions(reward));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Drat! You remembered that I asked you for one, right? I could really use it right now.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "BeerForHayunn";
	}

	public String getTitle() {
		
		return "Beer for Hayunn";
	}
	

}
