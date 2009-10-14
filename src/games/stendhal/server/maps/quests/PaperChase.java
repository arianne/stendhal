package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A kind of paper chase.
 *
 * @author hendrik
 */
public class PaperChase extends AbstractQuest {
	private static final String QUEST_SLOT = "paper_chase";

	private List<String> points = Arrays.asList("Hayunn Naratha",
			"Sister Benedicta", /*"Thanatos", "Margaret", "Vonda", "Zara", "Phalk", 
			"Jef", "Orc Saman", "Blacksheep Harry", "Covester", "Femme Fatale", 
			"PDiddi", "Vulcanus", "Haizen", "Monogenes",*/ "Saskia");

	private Map<String, String> texts = new HashMap<String, String>();



	private void setupTexts() {
		texts.put("Hayunn Naratha", "Please ask Hayunn Naratha.");
		texts.put("Sister Benedicta", "Please talk to Sister Benedicta.");
		texts.put("Thanatos", "Please talk to Thanatos.");
		texts.put("Margaret", "Please talk to Margaret.");
		texts.put("Vonda", "Please talk to Vonda.");
		texts.put("Zara", "Please talk to Zara.");
		texts.put("Phalk", "Please talk to Phalk.");
		texts.put("Jef", "Please talk to Jef.");
		texts.put("Orc Saman", "Please talk to Orc Saman.");
		texts.put("Blacksheep Harry", "Please talk to Blacksheep Harry."); 
		texts.put("Covester", "Please talk to Covester.");
		texts.put("Femme Fatale", "Please talk to Femme Fatale.");
		texts.put("PDiddi", "Please talk to PDiddi.");
		texts.put("Vulcanus", "Please talk to Vulcanus.");
		texts.put("Haizen", "Please talk to Haizen.");
		texts.put("Monogenes", "Please talk to Monogenes.");
		texts.put("Saskia", "The final person to talk to, is the one, who started all this.");
	}
	
	/**
	 * Handles all normal points in this paper chase (without the first and last.
	 * one)
	 */
	private class PaperChasePoint implements ChatAction {
		private final int idx;

		PaperChasePoint(final int idx) {
			this.idx = idx;
		}

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String state = points.get(idx);
			final String next = points.get(idx + 1);
			final String questState = player.getQuest(QUEST_SLOT, 0);

			// player does not have this quest or finished it
			if (questState == null) {
				engine.say("Please talk to Saskia in the Semos Mine Town to start the paper chase.");
				return;
			}

			final String nextNPC = questState;

			// is the player supposed to speak to another NPC?
			if (!nextNPC.equals(state)) {
				engine.say("What do you say? \"" + texts.get(nextNPC) + "\" That's obviously not me.");
				return;
			}

			// send player to the next NPC and record it in quest state
			engine.say("Good, you found me. The next hint is: " + texts.get(next) + " Good luck.");
			player.setQuest(QUEST_SLOT, 0, next);
		}

	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * Adds the task to the specified NPC. Note that the start and end of this
	 * quest have to be coded specially.
	 *
	 * @param idx
	 *            index of way point
	 */
	private void addTaskToNPC(final int idx) {
		final String state = points.get(idx);
		final SpeakerNPC npc = npcs.get(state);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), null,
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		
		setupTexts();
		
		SpeakerNPC npc = npcs.get("Saskia");

		// Saskia introduces the quests
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I have nothing to do for you. But thanks for asking",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Those, who had to stay at home because of their duties, have prepared a #paper #chase.",
			null);
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("paper", "chase"),
			null,
			ConversationStates.ATTENDING,
			"Good luck. Please ask this person about the #paper #chase: " +  texts.get(points.get(0)),
			new SetQuestAction(QUEST_SLOT, points.get(0) + ";" + System.currentTimeMillis()));


		// add normal way points (without first and last)
		for (int i = 0; i < points.size() - 1; i++) {
			addTaskToNPC(i);
		}

		// Saskia does the post processing of this quest
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, "Oh, that is a nice #quest.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, 0, "Saskia")),
			ConversationStates.ATTENDING, "I guess you still have to talk to some people.", null);

		ChatAction reward = new MultipleActions(
			new IncreaseKarmaAction(15), 
			new IncreaseXPAction(400), 
			new SetQuestAction(QUEST_SLOT, 0, "done"),
			new EquipItemAction("empty scroll", 10));
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"), 
			new QuestInStateCondition(QUEST_SLOT, 0, "Saskia"),
			ConversationStates.ATTENDING, 
			"Very good. You did the complete quest, talking to all those people around the world. Here are some magic scrolls as reward. They will help you on further travels.",
			reward);
	}


	@Override
	public String getName() {
		return "PaperChase";
	}
}
