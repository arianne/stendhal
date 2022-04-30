package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Pair;

public class QuestTaskBuilder {
	private HashMap<String, Pair<Integer, Integer>> requestKill = new HashMap<>();
	private HashMap<String, Integer> requestItem = new HashMap<>();
	private HashMap<String, Integer> alternativeItem = new HashMap<>();

	public QuestTaskBuilder requestKill(int count, String name) {
		requestKill.put(name, new Pair<Integer, Integer>(0, count));
		return this;
	}

	public QuestTaskBuilder requestItem(int count, String name) {
		requestItem.put(name, count);
		return this;
	}

	public QuestTaskBuilder alternativeItem(int count, String name) {
		alternativeItem.put(name, count);
		return this;
	}

	void simulate(QuestSimulator simulator) {
		for (Map.Entry<String, Pair<Integer, Integer>> entry : requestKill.entrySet()) {
			simulator.info("Player killed " + entry.getValue().first() + "+" + entry.getValue().second() + " " + entry.getKey() + ".");
		}
		for (Map.Entry<String, Integer> entry : requestItem.entrySet()) {
			simulator.info("Player obtained " + entry.getValue() + " " + entry.getKey() + ".");
		}
		simulator.info("");
	}
}
