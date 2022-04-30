package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

// TODO: We might want subclasses for KillCreaturesTaskBuilder, BringSingleItemTaskBuilder
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

	ChatAction buildStartQuestAction(String questSlot) {
		if (!requestKill.isEmpty()) {
			return new StartRecordingKillsAction(questSlot, 1, requestKill);
		}
		return null;
	}

	public ChatCondition buildQuestCompletedCondition(String questSlot) {
		List<ChatCondition> conditions = new LinkedList<>();
		if (!requestKill.isEmpty()) {
			conditions.add(new KilledForQuestCondition(questSlot, 1));
		}
		// TODO: items
		return new AndCondition(conditions);
	}

	public ChatAction buildQuestCompleteAction(String questSlot) {
		// TODO: drop items
		return null;
	}

	boolean isCompleted(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

}
