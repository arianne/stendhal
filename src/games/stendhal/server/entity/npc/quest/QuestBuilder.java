package games.stendhal.server.entity.npc.quest;

public class QuestBuilder<T extends QuestTaskBuilder> {

	private QuestInfoBuilder info = new QuestInfoBuilder();
	private QuestHistoryBuilder history = new QuestHistoryBuilder();
	private QuestOfferBuilder offer = new QuestOfferBuilder();
	private T task = null;
	private QuestCompleteBuilder complete = new QuestCompleteBuilder();

	public QuestBuilder(T task) {
		this.task = task;
	}

	public QuestInfoBuilder info() {
		return info;
	}
	public QuestHistoryBuilder history() {
		return history;
	}
	public QuestOfferBuilder offer() {
		return offer;
	}
	public T task() {
		return task;
	}
	public QuestCompleteBuilder complete() {
		return complete;
	}

	public void simulate() {
		QuestSimulator simulator = new QuestSimulator();
		info.simulate(simulator);
		String npc = info.getQuestGiverNpc();
		offer.simulateFirst(npc, simulator);
		task.simulate(simulator);
		complete.simulate(npc, simulator);
		offer.simulateRepeat(npc, simulator);
	}

}
