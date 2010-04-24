package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: Zekiels practical test
 */
public class ThePracticalTestQuest extends AbstractQuest {

	private static final int REQUIRED_IRON = 2;

	private static final int REQUIRED_BEESWAX = 6;

	private static final String QUEST_SLOT = "candle_quest";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void prepareQuestOfferingStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"First you need six magic candles. When you bring me six pieces of #beeswax and two pieces of #iron, then I will summon the candles for you, during you do the practical test.",
				new SetQuestAction(QUEST_SLOT,"start"));
		
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"You have stand the practical test. Use the teleport stone, or instead I can #teleport you to the spire.",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"You havent brought me the #ingredients for the magic candles.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "candles_done"),
				ConversationStates.ATTENDING, 
				"You havent finished the practical test. Are you ready to #start with it, or do you want to know more about the #wizards first?",
				null);

		npc.addReply("beeswax", "I will summon magic candles for you, but I will need Beeswax for that. Beekeepers sell beeswax mostly.");

		npc.addReply("iron", "The candlestick needs to be made of iron. The blacksmith in semos can help you.");

		npc.addReply("ingredients", "I will need six pieces of #beeswax and two pieces of #iron to summon the candles.");
	}

	private void bringItemsStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
			new QuestInStateCondition(QUEST_SLOT,"start"),
			new NotCondition(new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
			new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON)),
			ConversationStates.ATTENDING, "Greetings, I see you have the iron, but I still need six pieces of beeswax. Please come back, when u have all #ingredients with you.", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
			new QuestInStateCondition(QUEST_SLOT,"start"),
			new NotCondition(new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON)),
			new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
			ConversationStates.ATTENDING, "Greetings, I see you have the beeswax, but I still need two pieces of iron. Please come back, when u have all #ingredients with you.", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
			new QuestInStateCondition(QUEST_SLOT,"start"),
			new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON),
			new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
			ConversationStates.ATTENDING,
			"Greetings, finally you brought me all ingredients that I need to summon the magic candles. Now you can #start with the practical test.",
			new MultipleActions(new SetQuestAction(QUEST_SLOT,"candles_done"),
			new DropItemAction("beeswax", 6),
			new DropItemAction("iron", 2),
			new IncreaseXPAction(4000),
			new IncreaseKarmaAction(10)));
	}

	private void practicalTestStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			ConversationStates.ATTENDING, 
			"Greetings, I guess you came back to #start with the practical test.",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("start"),
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			ConversationStates.ATTENDING, 
			"Great, I will #send you to the first step, if you want it now. But first you should #know some important things about the test.",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("know"),
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			ConversationStates.ATTENDING, 
			"At each step there is a northern, southern, eastern and western cell, which contains a creature."+
			" Choose the creature, that you associate with the #wizards domain and history, by using the magical spot"+
			" between the two warlock statues in front of the cell. Don't worry, you don't have to fight the creature"+
			" that you choose. If you choose wisely, then I will summon a candle for you, if not you will be teleported"+
			" back to me. Use the candle at the shimmering corner of the hexagramm and the step is done. If you want to"+
			" leave the practical test, just use the magical spot in the middle of the hexagramm."+
			" I will tell you in the step, which wizard turns next. So if you are ready, I will #send you to the first step.",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("send"),
			new AndCondition(
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			new PlayerHasItemWithHimCondition("candle")),
			ConversationStates.ATTENDING, 
			"Before I can send you to the first step, you have to drop all candles from you.",
			null);

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("send"),
			new AndCondition(
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			new NotCondition(new PlayerHasItemWithHimCondition("candle"))),
			ConversationStates.ATTENDING, 
			"Good luck!",
			new TeleportAction("int_semos_wizards_tower_1", 15, 16, Direction.DOWN));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareQuestOfferingStep();
		bringItemsStep();
		practicalTestStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("beeswax", REQUIRED_BEESWAX))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}

		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	@Override
	public String getName() {
		return "ZekielsPracticalTest";
	}
}
