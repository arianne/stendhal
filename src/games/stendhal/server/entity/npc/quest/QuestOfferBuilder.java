package games.stendhal.server.entity.npc.quest;

public class QuestOfferBuilder {
	private String respondToRequest = null;
	private String respondToRepeatedRequest = "Thanks for your help. I have no new task for you.";
	private String respondToAccept = "Thank you";
	private String respondToReject = "Ohh. Too bad";
	private String remind = "Please keep your promise";

	
	public QuestOfferBuilder respondToRequest(String respondToRequest) {
		this.respondToRequest = respondToRequest;
		return this;
	}

	public QuestOfferBuilder respondToRepeatedRequest(String respondToRepeatedRequest) {
		this.respondToRepeatedRequest = respondToRepeatedRequest;
		return this;
	}

	public QuestOfferBuilder respondToAccept(String respondToAccept) {
		this.respondToAccept = respondToAccept;
		return this;
	}

	public QuestOfferBuilder respondToReject(String respondToReject) {
		this.respondToReject = respondToReject;
		return this;
	}

	
	public QuestOfferBuilder respondTo(String string, String string2) {
		// TODO Auto-generated method stub
		return this;
	}

	// TODO: This should be somewhere else
	public QuestOfferBuilder saying(String string) {
		// TODO Auto-generated method stub
		return this;
	}

	public QuestOfferBuilder remind(String remind) {
		this.remind = remind;
		return this;
	}

	void simulateFirst(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRequest);
		simulator.playerSays("no");
		simulator.npcSays(npc, respondToReject);
		simulator.playerSays("bye");
		simulator.info("");

		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRequest);
		simulator.playerSays("yes");
		simulator.npcSays(npc, respondToAccept);
		simulator.playerSays("bye");
		simulator.info("");

		simulator.playerSays("hi");
		simulator.npcSays(npc, remind);
		simulator.info("");
	}

	void simulateRepeat(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRepeatedRequest);
	}
}
