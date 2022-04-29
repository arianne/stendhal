package games.stendhal.server.entity.npc.quest;

public class QuestBuilder {

	private QuestInfoBuilder info = new QuestInfoBuilder();
	private QuestHistoryBuilder history = new QuestHistoryBuilder();
	private QuestOfferBuilder offer = new QuestOfferBuilder();
	private QuestTaskBuilder task = new QuestTaskBuilder();
	private QuestCompleteBuilder complete = new QuestCompleteBuilder();

	public QuestInfoBuilder info() {
		return info;
	}
	public QuestHistoryBuilder history() {
		return history;
	}
	public QuestOfferBuilder offer() {
		return offer;
	}
	public QuestTaskBuilder task() {
		return task;
	}
	public QuestCompleteBuilder complete() {
		return complete;
	}

}
