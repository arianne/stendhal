package games.stendhal.server.entity.npc.quest;

public class QuestInfoBuilder {
	private String name = "<unnamed>";
	private String description = "";
	private String internalName = null;
	private boolean repeatable = false;
	private int minLevel = 0;
	private String region = "somewhere";
	private String questGiverNpc = null;

	public QuestInfoBuilder name(String name) {
		this.name = name;
		return this;
	}

	public QuestInfoBuilder description(String description) {
		this.description = description;
		return this;
	}

	public QuestInfoBuilder internalName(String internalName) {
		this.internalName = internalName;
		return this;
	}

	public QuestInfoBuilder repeatable(boolean repeatable) {
		this.repeatable = repeatable;
		return this;
	}

	public QuestInfoBuilder minLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	public QuestInfoBuilder region(String region) {
		this.region = region;
		return this;
	}

	public QuestInfoBuilder questGiverNpc(String questGiverNpc) {
		this.questGiverNpc = questGiverNpc;
		return this;
	}

	String getQuestGiverNpc() {
		return questGiverNpc;
	}

	void simulate(QuestSimulator simulator) {
		simulator.info("Quest: " + name + " (internal: " + internalName + ")");
		simulator.info(description);
		simulator.info("");

		// TODO
	}

}
