package games.stendhal.server.entity.npc.quest;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;

public class QuestCompleteBuilder {
	private String greet = "Thank you";
	private String respondToReject = null;
	private String respondToAccept = null;
	private List<ChatAction> rewardWith = new LinkedList<>();

	public QuestCompleteBuilder greet(String greet) {
		this.greet = greet;
		return this;
	}

	public QuestCompleteBuilder respondToReject(String respondToReject) {
		this.respondToReject = respondToReject;
		return this;
	}

	public QuestCompleteBuilder respondToAccept(String respondToAccept) {
		this.respondToAccept = respondToAccept;
		return this;
	}

	
	public QuestCompleteBuilder rewardWith(ChatAction action) {
		this.rewardWith.add(action);
		return this;
	}


	void simulate(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.npcSays(npc, greet);

		if (respondToReject != null || respondToAccept != null) {
			simulator.playerSays("no");
			simulator.npcSays(npc, respondToReject);
			simulator.playerSays("bye");
			simulator.info("");

			simulator.playerSays("hi");
			simulator.npcSays(npc, greet);
			simulator.playerSays("yes");
			simulator.npcSays(npc, respondToAccept);
			
		}
		simulator.info("Player was rewarded with " + this.rewardWith.toString());
		simulator.info("");
	}

}
