package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TriggerMatchesQuestSlotCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;


/**
 * QUEST: Coded Message from Finn Farmer
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Finn Farmer, a little boy playing in the backyard</li>
 * <li>George, a child in Ados park</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Finn Farmer tells you a coded message</li>
 * <li>Repeat the coded message to George</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>XP +200</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it each 2 days.</li>
 * </ul>
 * 
 * @author kymara, hendrik
 */
public class CodedMessageFromFinnFarmer extends AbstractQuest {


	private static String QUEST_SLOT = "coded_message";

	@Override
	public String getSlotName() {
		return "coded_message";
	}

	@Override
	public List<String> getHistory(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Coded Message from Finn Farmer";
	}

	private String[][] TEMPLATES = new String[][] {
		new String[] {
			"The banana",
			"The swallow",
			"The elephant",
			"The teady bear",
			"The sun"
		},
		new String[] {
			"rests in",
			"is rising from",
			"has left",
			"has entered",
			"is flying over",
			"walks to"
		},
		new String[] {
			"the fireplace.",
			"the building.",
			"the hole.",
			"the city.",
			"the ship",
			"the cave",
			"the forest"
		},
	};

	/**
	 * generates a coded message
	 *
	 * @return a coded message
	 */
	String generateRandomMessage() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < TEMPLATES.length; i++) {
			res.append(TEMPLATES[i][Rand.rand(TEMPLATES[i].length)]);
			res.append(" ");
		}
		return res.toString().trim();
	}

	/**
	 * prepare Finn Farmer
	 */
	private void step1() {
		final SpeakerNPC npc = npcs.get("Finn Farmer");

		// TODO: check repeatability
		npc.add(ConversationStates.ATTENDING, 
				ConversationPhrases.QUEST_MESSAGES,
				ConversationStates.QUEST_OFFERED,
				"I have an urgent message for George! It's really important! But my parents don't let me wander around the city alone. As if I were a small kid! Could you please deliver a message to her?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED, 
				"george",
				null,
				ConversationStates.QUEST_OFFERED,
				"Just find Tommy. Perhaps in Ados Park. George won't be far away. Could you please deliver a message to her?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES,
				ConversationStates.IDLE,
				"Okay, then I better don't tell you no secrets.",
				new MultipleActions(
						new DecreaseKarmaAction(10),
						new SetQuestAction(QUEST_SLOT, 0, "rejected")
				));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING,
				"",
				new MultipleActions(
					new CreateAndSayCodedMessage(),
					new SetQuestAction(QUEST_SLOT, 0, "deliver_to_george")
				));
	}

	/**
	 * let George accept the 
	 */
	private void step2() {
		final SpeakerNPC npc = npcs.get("George");

		MultipleActions reward = new MultipleActions(
			new SetQuestAction(QUEST_SLOT, 0, "done"),
			new IncreaseXPAction(200)
			// TODO
		);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "deliver_to_george"),
				ConversationStates.ATTENDING,
				"I am not allowed to talk to strangers, but you seem to have something important to says. What is it?",
				null);

		npc.add(ConversationStates.ATTENDING, 
				"", 
				new AndCondition(
					new TriggerMatchesQuestSlotCondition(QUEST_SLOT, 1),
					new QuestInStateCondition(QUEST_SLOT, 0, "deliver_to_george")
				), 
				ConversationStates.ATTENDING,
				"Oh, thank you. This is indead quite interesting.",
				reward);

		npc.add(ConversationStates.ATTENDING, 
				"", 
				new AndCondition(
					new QuestInStateCondition(QUEST_SLOT, 0, "deliver"),
					new NotCondition(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, 1)),
					new TriggerMightbeACodedMessageCondition()
				),
				ConversationStates.ATTENDING,
				"Oh? That doesn't make any sense at all!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step1();
		step2();
	}


	/**
	 * creates, stores and says a coded message
	 *
	 * @author hendrik
	 */
	public class CreateAndSayCodedMessage implements ChatAction {

		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			String codedMessage = generateRandomMessage();
			player.setQuest(QUEST_SLOT, 1, codedMessage);
			npc.say(codedMessage);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof CreateAndSayCodedMessage;
		}

		@Override
		public int hashCode() {
			return -47;
		}

		@Override
		public String toString() {
			return "codedmessage!";
		}
	}

	/**
	 * does the sentence look like a possible coded message?
	 *
	 * @author hendrik
	 */
	public class TriggerMightbeACodedMessageCondition implements ChatCondition {

		public boolean fire(Player player, Sentence sentence, Entity npc) {
			String originalText = sentence.getOriginalText();
			int counter = 0;
			for (int i = 0; i < originalText.length(); i++) {
				if (originalText.charAt(i) == ' ') {
					counter++;
				}
			}
			return counter >= 4;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof TriggerMightbeACodedMessageCondition;
		}

		@Override
		public int hashCode() {
			return -37;
		}

		@Override
		public String toString() {
			return "codedmessage?";
		}
	}
}
