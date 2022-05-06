/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import marauroa.common.Pair;

/**
 * requests that the player kills creatures
 *
 * @author hendrik
 */
public class KillCreaturesTask extends QuestTaskBuilder {
	private HashMap<String, Pair<Integer, Integer>> requestKill = new HashMap<>();

	/**
	 * request killing creatures
	 *
	 * @param count number of this creature
	 * @param name  name of a creature
	 * @return KillCreaturesTask
	 */
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
	ChatCondition buildQuestCompletedCondition(String questSlot) {
		return new KilledForQuestCondition(questSlot, 1);
	}

	@Override
	ChatAction buildQuestCompleteAction(String questSlot) {
		return null;
	}

}
