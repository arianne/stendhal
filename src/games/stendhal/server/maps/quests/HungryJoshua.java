package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Hungry Joshua PARTICIPANTS: - Xoderos - Joshua
 * 
 * STEPS: - Talk with Xoderos to activate the quest. - Make 5 sandwiches. - Talk
 * with Joshua to give him the sandwiches. - Return to Xoderos with a message
 * from Joshua.
 * 
 * REWARD: - 200 XP - ability to use the keyring
 * 
 * REPETITIONS: - None.
 */
public class HungryJoshua extends AbstractQuest {
	private static final int FOOD_AMOUNT = 5;

	private static final String QUEST_SLOT = "hungry_joshua";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "joshua", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("sandwich",
				FOOD_AMOUNT))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("start")
				&& !player.isEquipped("sandwich", FOOD_AMOUNT)) {
			res.add("LOST_ITEM");
		}
		if (questState.equals("joshua")) {
			res.add("BROUGHT_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {

		SpeakerNPC npc = npcs.get("Xoderos");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("My brother has enough food now, many thanks.");
						} else {
							engine.say("I'm worried about my brother who lives in Ados. I need someone to take some #food to him.");
						}
					}
				});

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING, "food",
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"My brother has enough sandwiches now, thank you.", null);

		/** If quest is not started yet, start it. */
		npc.add(
				ConversationStates.ATTENDING,
				"food",
				new QuestNotStartedCondition(QUEST_SLOT),

				ConversationStates.QUEST_OFFERED,
				"I think five sandwiches would be enough. My brother is called #Joshua.",
				null);

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you. Please tell him #food or #sandwich so he knows you're not just a customer.",
				new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"So you'd just let him starve! I'll have to hope someone else is more charitable.",
				null);

		npc.add(
				ConversationStates.QUEST_OFFERED,
				"Joshua",
				null,
				ConversationStates.QUEST_OFFERED,
				"He's the goldsmith in Ados. They're so short of supplies. Will you help?",
				null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("food", "sandwich",
				"sandwiches"), new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"#Joshua will be getting hungry! Please hurry!", null);

		npc.add(ConversationStates.ATTENDING, "Joshua",
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"My brother, the goldsmith in Ados.", null);
	}

	private void step_2() {
		SpeakerNPC npc = npcs.get("Joshua");

		/** If player has quest and has brought the food, ask for it */
		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("food", "sandwich", "sandwiches"),
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasItemWithHimCondition("sandwich",
								FOOD_AMOUNT)),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Oh great! Did my brother Xoderos send you with those sandwiches?",
				null);

		List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("sandwich", FOOD_AMOUNT));
		reward.add(new IncreaseXPAction(150));
		reward.add(new SetQuestAction(QUEST_SLOT, "joshua"));

		npc.add(
				ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("sandwich", FOOD_AMOUNT),
				ConversationStates.ATTENDING,
				"Thank you! Please let Xoderos know that I am fine. Say my name, Joshua, so he knows that you saw me. He will probably give you something in return.",
				new MultipleActions(reward));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES, new NotCondition(
						new PlayerHasItemWithHimCondition("sandwich",
								FOOD_AMOUNT)), ConversationStates.ATTENDING,
				"Hey! Where did you put the sandwiches?", null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Oh dear, I'm so hungry, please say #yes they are for me.",
				null);
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Xoderos");

		/** Complete the quest */
		npc.add(
				ConversationStates.ATTENDING,
				"Joshua",
				new QuestInStateCondition(QUEST_SLOT, "joshua"),
				ConversationStates.ATTENDING,
				"I'm glad Joshua is well. Now, what can I do for you? I know, I'll fix that broken key ring that you're carrying ... there, it should work now!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						player.addXP(50);
						// ideally, make it so that this slot being done means
						// you get a keyring object instead what we currently
						// have - a button in the settings panel
						player.setFeature("keyring", true);
						player.notifyWorldAboutChanges();
						player.setQuest(QUEST_SLOT, "done");
					}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
