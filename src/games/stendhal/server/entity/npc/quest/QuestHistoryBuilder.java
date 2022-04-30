package games.stendhal.server.entity.npc.quest;

public class QuestHistoryBuilder {
	private String whenNpcWasMet;
	private String whenQuestWasRejected;
	private String whenQuestWasAccepted;
	private String whenTaskWasCompleted;
	private String whenQuestWasCompleted;

	public QuestHistoryBuilder whenNpcWasMet(String whenNpcWasMet) {
		this.whenNpcWasMet = whenNpcWasMet;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasRejected(String whenQuestWasRejected) {
		this.whenQuestWasRejected = whenQuestWasRejected;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasAccepted(String whenQuestWasAccepted) {
		this.whenQuestWasAccepted = whenQuestWasAccepted;
		return this;
	}

	public QuestHistoryBuilder whenTaskWasCompleted(String whenTaskWasCompleted) {
		this.whenTaskWasCompleted = whenTaskWasCompleted;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasCompleted(String whenQuestWasCompleted) {
		this.whenQuestWasCompleted = whenQuestWasCompleted;
		return this;
	}

	String getWhenNpcWasMet() {
		return whenNpcWasMet;
	}

	String getWhenQuestWasRejected() {
		return whenQuestWasRejected;
	}

	String getWhenQuestWasAccepted() {
		return whenQuestWasAccepted;
	}

	String getWhenTaskWasCompleted() {
		return whenTaskWasCompleted;
	}

	String getWhenQuestWasCompleted() {
		return whenQuestWasCompleted;
	}

}
