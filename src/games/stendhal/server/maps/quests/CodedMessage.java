package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TriggerMatchesQuestSlotCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;


public class CodedMessage extends AbstractQuest {


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
		return "Coded Message";
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
			"is flying over"
		},
		new String[] {
			"the fireplace.",
			"the building.",
			"the hole.",
			"the city."
		},
	};
	
	public String generateRandomMessage() {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < TEMPLATES.length; i++) {
			res.append(TEMPLATES[i][Rand.rand(TEMPLATES[i].length)]);
			res.append(" ");
		}
		return res.toString().trim();
	}

	private void step1() {
		// TODO
	}
	
	private void step2() {
		final SpeakerNPC npc = npcs.get("lil johnnnny");

		MultipleActions reward = new MultipleActions(
			new SetQuestAction(QUEST_SLOT, 0, "done")
			// TODO
		);
		npc.add(ConversationStates.ATTENDING, 
			"", 
			new AndCondition(
				new TriggerMatchesQuestSlotCondition(QUEST_SLOT, 1),
				new QuestInStateCondition(QUEST_SLOT, 0, "deliver")
			), 
			ConversationStates.ATTENDING,
			"",
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
