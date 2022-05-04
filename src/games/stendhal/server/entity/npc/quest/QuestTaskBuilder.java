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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * abstact base class for QuestTasks
 *
 * @author hendrik
 */
public abstract class QuestTaskBuilder {

	abstract void simulate(QuestSimulator simulator);

	abstract ChatAction buildStartQuestAction(String questSlot);

	abstract ChatCondition buildQuestCompletedCondition(String questSlot);

	abstract ChatAction buildQuestCompleteAction(String questSlot);

	boolean isCompleted(Player player, String questSlot) {
		return buildQuestCompletedCondition(questSlot).fire(player, null, null);
	}

}
