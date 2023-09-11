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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * defines how the NPC react after the player completes the quest
 *
 * @author hendrik
 */
public abstract class QuestCompleteBuilder {

	abstract void simulate(String npc, QuestSimulator simulator);

	abstract void build(SpeakerNPC npc, String questSlot, ChatCondition questCompletedCondition, ChatAction questCompleteAction);

}
