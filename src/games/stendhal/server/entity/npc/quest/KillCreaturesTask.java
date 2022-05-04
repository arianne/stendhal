package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

public class KillCreaturesTask extends QuestTaskBuilder {
	private HashMap<String, Pair<Integer, Integer>> requestKill = new HashMap<>();

	public KillCreaturesTask requestKill(int count, String name) {
		requestKill.put(name, new Pair<Integer, Integer>(0, count));
		return this;
	}

	@Override
	void simulate(QuestSimulator simulator) {
		for (Map.Entry<String, Pair<Integer, Integer>> entry : requestKill.entrySet()) {
			simulator.info("Player killed " + entry.getValue().first() + "+" + entry.getValue().second() + " " + entry.getKey() + ".");
		}
		simulator.info("");
	}

	@Override
	ChatAction buildStartQuestAction(String questSlot) {
		if (!requestKill.isEmpty()) {
			return new StartRecordingKillsAction(questSlot, 1, requestKill);
		}
		return null;
	}

	@Override
	public ChatCondition buildQuestCompletedCondition(String questSlot) {
		return new KilledForQuestCondition(questSlot, 1);
	}

	@Override
	public ChatAction buildQuestCompleteAction(String questSlot) {
		return null;
	}

	@Override
	boolean isCompleted(Player player, String questSlot) {
		return buildQuestCompletedCondition(questSlot).fire(player, null, null);
	}

}
