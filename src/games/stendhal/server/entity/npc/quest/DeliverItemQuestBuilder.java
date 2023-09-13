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

public class DeliverItemQuestBuilder extends QuestBuilder<DeliverItemTask, DeliverItemQuestCompleteBuilder, DeliverItemQuestHistoryBuilder> {

	public DeliverItemQuestBuilder() {
		super(new DeliverItemTask());
		complete = new DeliverItemQuestCompleteBuilder(task());
		history = new DeliverItemQuestHistoryBuilder();
	}

}
