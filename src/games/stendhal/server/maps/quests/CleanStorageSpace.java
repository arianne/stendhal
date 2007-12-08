package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: CleanStorageSpace
 * <p>
 * PARTICIPANTS:
 * <li> Eonna
 * <p>
 * STEPS:
 * <li> Eonna asks you to clean her storage_space.
 * <li> You go kill at least a rat, a cave rat and a cobra.
 * <li> Eoanna checks your kills and then thanks you.
 * <p>
 * REWARD:
 * <li> 25 XP
 * <p>
 * REPETITIONS:
 * <li> None.
 */
public class CleanStorageSpace extends AbstractQuest {
	private static final String QUEST_SLOT = "clean_storage";

	private void step_1() {
		SpeakerNPC npc = npcs.get("Eonna");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"My #basement is absolutely crawling with rats. Will you help me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Thanks again! I think it's still clear down there.", null);

		List<SpeakerNPC.ChatAction> start = new LinkedList<SpeakerNPC.ChatAction>();
		start.add(new StartRecordingKillsAction("rat", "caverat", "snake"));
		start.add(new IncreaseKarmaAction(2.0));
		start.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!",
				new MultipleActions(start));

		npc.add(ConversationStates.QUEST_OFFERED, "no", null,
				ConversationStates.ATTENDING,
				"*sigh* Oh well, maybe someone else will be my hero...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -2.0));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("basement", "storage_space"),
				null,
				ConversationStates.QUEST_OFFERED,
				"Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?",
				null);
	}

	private void step_2() {
		// Go kill at least a rat, a cave rat and a snake.
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Eonna");
		
		List<SpeakerNPC.ChatAction> reward = new LinkedList<SpeakerNPC.ChatAction>();
		reward.add(new IncreaseKarmaAction(3.0));
		reward.add(new IncreaseXPAction(25));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		// the player returns to Eonna after having started the quest.
		// Eonna checks if the player has killed one of each animal race.
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new KilledCondition("rat", "caverat", "snake")),
				ConversationStates.ATTENDING, "A hero at last! Thank you!",
				new MultipleActions(reward));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new KilledCondition("rat", "caverat", "snake"))),
				ConversationStates.QUEST_STARTED,
				"Don't you remember promising to clean out the rats from my #basement?",
				null);

		npc.add(
				ConversationStates.QUEST_STARTED,
				"basement",
				null,
				ConversationStates.ATTENDING,
				"Down the stairs, like I said. Please get rid of all those rats, and see if you can find the snake as well!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
