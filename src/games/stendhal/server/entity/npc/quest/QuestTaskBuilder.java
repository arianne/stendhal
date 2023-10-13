/***************************************************************************
 *                 (C) Copyright 2022-2023 - Faiumoni e.V.                 *
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

import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.player.Player;

/**
 * abstact base class for QuestTasks
 *
 * @author hendrik
 */
public abstract class QuestTaskBuilder {

	// hide constructor
	QuestTaskBuilder() {
		super();
	}

	abstract void simulate(QuestSimulator simulator);

	ChatCondition buildQuestPreCondition(@SuppressWarnings("unused") String questSlot) {
		return new AlwaysTrueCondition();
	}

	abstract ChatAction buildStartQuestAction(String questSlot);

	ChatAction buildRejectQuestAction(@SuppressWarnings("unused") String questSlot) {
		return null;
	}

	abstract ChatCondition buildQuestCompletedCondition(String questSlot);

	abstract ChatAction buildQuestCompleteAction(String questSlot);

	boolean isCompleted(Player player, String questSlot) {
		return buildQuestCompletedCondition(questSlot).fire(player, null, null);
	}

	List<String> calculateHistoryProgress(
			@SuppressWarnings("unused") QuestHistoryBuilder history,
			@SuppressWarnings("unused") Player player,
			@SuppressWarnings("unused") String questSlot) {
		return null;
	}

}
