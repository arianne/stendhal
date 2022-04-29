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

}
