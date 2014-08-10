package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
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

	public static void main(String[] args) {
		CodedMessage quest = new CodedMessage();
		for (int i = 0; i < 100; i++) {
			System.out.println(quest.generateRandomMessage());
		}
	}
}
