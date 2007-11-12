package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Armor for Dagobert
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Dagobert, the consultant at the bank of Semos</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Dagobert asks you to find a leather_cuirass.</li>
 * <li>You get a leather_cuirass, e.g. by killing a cyclops.</li>
 * <li>Dagobert sees your leather_cuirass and asks for it and then thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>80 gold</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class ArmorForDagobert extends AbstractQuest {

	private static final String QUEST_SLOT = "armor_dagobert";

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
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("leather_cuirass"))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void prepareRequestingStep() {
		SpeakerNPC npc = npcs.get("Dagobert");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?",
			null);

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"Thank you very much for the armor, but I don't have any other task for you.",
			null);

		// player is willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Once I had a nice #leather_cuirass, but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.",
			new SetQuestAction(QUEST_SLOT, "start"));

		// player is not willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Well, then I guess I'll just duck and cover.",
			new SetQuestAction(QUEST_SLOT, "rejected"));

		// player wants to know what a leather_cuirass is
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("leather_cuirass", "leather", "cuirass"),
			null,
			ConversationStates.ATTENDING,
			"A leather_cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.",
			null);
	}

	private void prepareBringingStep() {
		SpeakerNPC npc = npcs.get("Dagobert");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("leather_cuirass")),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Excuse me, please! I have noticed the leather_cuirass you're carrying. Is it for me?",
			null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("leather_cuirass"))),
			ConversationStates.ATTENDING, 
			"Luckily I haven't been robbed while you were away. I would be glad to receive a leather_cuirass. Anyway, how can I #help you?",
			null);

		List<SpeakerNPC.ChatAction> reward = new LinkedList<SpeakerNPC.ChatAction>();
		reward.add(new DropItemAction("leather_cuirass"));
		reward.add(new EquipItemAction("money", 80));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the armor
			// away and then saying "yes"
			new PlayerHasItemWithHimCondition("leather_cuirass"), 
			ConversationStates.ATTENDING, "Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere.",
			new MultipleActions(reward));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Well then, I hope you find another one which you can give to me before I get robbed again.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareRequestingStep();
		prepareBringingStep();
	}
}
