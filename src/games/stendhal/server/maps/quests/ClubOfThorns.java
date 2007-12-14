package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Club of Thorns
 * <p>
 * PARTICIPANTS:
 * <li> Orc Saman
 * <p>
 * STEPS:
 * <li> Orc Saman asks you to kill mountain orc chief in prison for revenge
 * <li> Go kill mountain orc chief in prison using key given by Saman to get in
 * <li> Return and you get Club of Thorns as reward
 * <p>
 * REWARD:
 * <li> 1000 XP
 * <p>
 * REPETITIONS:
 * <li> None.
 */
public class ClubOfThorns extends AbstractQuest {
	private static final String QUEST_SLOT = "club_thorns";

	private void step_1() {
		SpeakerNPC npc = npcs.get("Orc Saman");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Make revenge! Kill de Mountain Orc Chief! unnerstand?", null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, new QuestActiveCondition(
						QUEST_SLOT), ConversationStates.QUEST_OFFERED,
				"Make revenge! #Kill Mountain Orc Chief!", null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "Saman has revenged! dis Good!",
				null);

		List<ChatAction> start = new LinkedList<ChatAction>();
		start.add(new EquipItemAction("kotoch_prison_key", 1, true));
		start.add(new StartRecordingKillsAction("mountain_orc_chief"));
		start.add(new IncreaseKarmaAction(6.0));
		start.add(new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Take dat key. he in jail. Kill! Denn, say me #kill! Say me #kill!",
				new MultipleActions(start));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Ugg! i want hooman make #task, kill!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -6.0));
	}

	private void step_2() {
		// Go kill the mountain orc chief using key to get into prison.
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Orc Saman");

		List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("club_of_thorns", 1, true));
		reward.add(new IncreaseKarmaAction(3.0));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		// the player returns after having started the quest.
		// Saman checks if kill was made
		npc.add(ConversationStates.ATTENDING, "kill", new AndCondition(
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new KilledCondition("mountain_orc_chief")),
				ConversationStates.ATTENDING,
				"Revenge! Good! Take club of hooman blud.",
				new MultipleActions(reward));

		npc.add(ConversationStates.ATTENDING, "kill", new AndCondition(
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new NotCondition(new KilledCondition("mountain_orc_chief"))),
				ConversationStates.ATTENDING,
				"kill Mountain Orc Chief! Kotoch orcs nid revenge!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
