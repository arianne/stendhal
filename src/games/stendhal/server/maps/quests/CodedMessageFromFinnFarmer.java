package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToFutureRandomTimeStampAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.TimeReachedCondition;
import games.stendhal.server.entity.npc.condition.TriggerMatchesQuestSlotCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;


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
 * <li>Relay the coded message to George, who will tell you a new coded message as answer</li>
 * <li>Relay the answer to Finn Farmer</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>XP +200</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *   <li>You can repeat it once per day.</li>
 * </ul>
 *
 * @author kymara, hendrik
 */
public class CodedMessageFromFinnFarmer extends AbstractQuest {


	private static final String QUEST_SLOT = "coded_message";
	private static final int QUEST_INDEX_STATUS = 0;
	private static final int QUEST_INDEX_MESSAGE = 1;
	private static final int QUEST_INDEX_REPEATED = 2;
	private static final int QUEST_INDEX_TIME = 3;
	private static final int REQUIRED_MINUTES = 24 * 60;

	@Override
	public List<String> getHistory(Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I talked to a little boy called Finn Farmer in Ados.");
		res.add("Finn asked me to deliver a secret message to George. I can find him near his dog Tommy in Ados park.");

		final String questState = player.getQuest(QUEST_SLOT, 0);
		if (questState.equals("rejected")) {
			res.add("But I rejected his request.");
			return res;
		}

		res.add("I agreed to deliver the message.");
		if (questState.equals("deliver_to_george")) {
			return res;
		}

		res.add("After talking to George he gave me another secret message, which I need to deliver to Finn.");
		if (questState.equals("deliver_to_finn")) {
			return res;
		}

		res.add("I completed my mission as messenger. Finn Farmer went almost crazy with joy when I told him George's answer.");
		return res;
	}

	private String[][] TEMPLATES = new String[][] {
		new String[] {
			"The banana",
			"The swallow",
			"The elephant",
			"The teddy bear",
			"The moon",
			"Jupiter",
			"Delta"
		},

		new String[] {
			"rests in",
			"is rising from",
			"has left",
			"has entered",
			"is flying over",
			"walks into",
			"sleeps in"
		},

		new String[] {
			"the fireplace.",
			"the building.",
			"the hole.",
			"the city.",
			"the ship.",
			"the cave.",
			"the forest."
		},
	};

	/**
	 * generates a coded message
	 *
	 * @return a coded message
	 */
	String generateRandomMessage() {
		StringBuilder res = new StringBuilder();
		for (String[] element : TEMPLATES) {
			res.append(element[Rand.rand(element.length)]);
			res.append(" ");
		}
		return res.toString().trim();
	}

	/**
	 * prepare Finn Farmer to start the quest
	 */
	private void step1() {
		final SpeakerNPC npc = npcs.get("Finn Farmer");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_george"),
				ConversationStates.ATTENDING,
				"Thank you for agreeing to tell George this message:",
				new SayTextAction("[quest.coded_message:1]"));


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestNotActiveCondition(QUEST_SLOT),
						new NotCondition(
							new TimeReachedCondition(QUEST_SLOT, QUEST_INDEX_TIME))),
				ConversationStates.ATTENDING,
				"Perhaps, I have another message tomorrow.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestNotActiveCondition(QUEST_SLOT),
						new TimeReachedCondition(QUEST_SLOT, QUEST_INDEX_TIME)),
				ConversationStates.QUEST_OFFERED,
				"I have an urgent message for #George! It's really important! But my parents don't let me wander around the city alone. As if I were a small kid! Could you please deliver a message to him?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"george",
				null,
				ConversationStates.QUEST_OFFERED,
				"Just find Tommy. Perhaps in Ados Park. George won't be far away. Could you please deliver a message to him?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				ConversationStates.IDLE,
				"Okay, then I better don't tell you no secrets.",
				new MultipleActions(
						new DecreaseKarmaAction(10),
						new SetQuestAction(QUEST_SLOT, QUEST_INDEX_STATUS, "rejected")
				));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
					new CreateAndSayCodedMessage(),
					new SetQuestAction(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_george")
				));
	}

	/**
	 * let George accept the message and answer with a new coded message
	 */
	private void step2() {
		final SpeakerNPC npc = npcs.get("George");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_george"),
				ConversationStates.ATTENDING,
				"I am not allowed to talk to strangers, but you seem to have something important to says. What is it?",
				null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_finn"),
				ConversationStates.IDLE,
				"Thank you for agreeing to tell Finn this message:",
				new SayTextAction("[quest.coded_message:1]"));

		npc.add(ConversationStates.ATTENDING,
				"",
				new AndCondition(
					new TriggerMatchesQuestSlotCondition(QUEST_SLOT, QUEST_INDEX_MESSAGE),
					new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_george")
				),
				ConversationStates.IDLE,
				"This is indeed quite interesting. Please let Finn know:",
				new MultipleActions(
					new CreateAndSayCodedMessage(),
					new SetQuestAction(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_finn")
				));

		npc.add(ConversationStates.ATTENDING,
				"",
				new AndCondition(
					new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_george"),
					new NotCondition(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, 1)),
					new TriggerMightbeACodedMessageCondition()
				),
				ConversationStates.ATTENDING,
				"Oh? That doesn't make any sense at all!",
				null);
	}

	/**
	 * let Finn Farmer accept the answer
	 */
	private void step3() {
		final SpeakerNPC npc = npcs.get("Finn Farmer");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, QUEST_INDEX_STATUS, "deliver_to_finn"),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"And, what did George say?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"",
				new TriggerMatchesQuestSlotCondition(QUEST_SLOT, QUEST_INDEX_MESSAGE),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, QUEST_INDEX_STATUS, "done"),
					new IncrementQuestAction(QUEST_SLOT, QUEST_INDEX_REPEATED, 1),
					new SetQuestToTimeStampAction(QUEST_SLOT, QUEST_INDEX_TIME),
					new SetQuestToFutureRandomTimeStampAction(QUEST_SLOT, QUEST_INDEX_TIME, REQUIRED_MINUTES, REQUIRED_MINUTES),
					new IncreaseXPAction(200),
					new IncreaseKarmaAction(10),
					new SayTextAction("Oh, thank you for telling George!"),
					new SayTextAction("!me dances around happily."),
					new SayTextAction("This was really important!"),
					new SayTextAction("And his answer is super interesting!"),
					new SayTextAction("I will be on the watch!"),
					new SayTextAction("Perhaps, I have another message for you tomorrow.")
				));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"",
				new AndCondition(
					new NotCondition(new TriggerMatchesQuestSlotCondition(QUEST_SLOT, QUEST_INDEX_MESSAGE)),
					new TriggerMightbeACodedMessageCondition()
				),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Oh? That doesn't make any sense at all!",
				null);
	}

	@Override
	public void addToWorld() {
		step1();
		step2();
		step3();
		fillQuestInfo(
			"Coded Message from Finn Farmer",
			"Finn Farmer has important, secret information for George.",
			false);
	}

	@Override
	public String getSlotName() {
		return "coded_message";
	}

	@Override
	public String getName() {
		return "Coded Message from Finn Farmer";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Finn Farmer";
	}

	@Override
	public boolean isRepeatable(Player player) {
		return new TimeReachedCondition(QUEST_SLOT, QUEST_INDEX_TIME).fire(player,null, null);
	}



	/**
	 * creates, stores and says a coded message
	 *
	 * @author hendrik
	 */
	public class CreateAndSayCodedMessage implements ChatAction {

		@Override
        public void fire(Player player, Sentence sentence, EventRaiser npc) {
			String codedMessage = generateRandomMessage();
			player.setQuest(QUEST_SLOT, QUEST_INDEX_MESSAGE, codedMessage);
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

		@Override
        public boolean fire(Player player, Sentence sentence, Entity npc) {
			String originalText = sentence.getOriginalText();
			int counter = 0;
			for (int i = 0; i < originalText.length(); i++) {
				if (originalText.charAt(i) == ' ') {
					counter++;
				}
			}
			return counter >= 3;
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
