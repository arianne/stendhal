/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;


public class ItemsForTinny implements QuestManuscript {

	@Override
	public BringItemQuestBuilder story() {
		final BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Items for Tinny")
			.internalName("items_for_tinny")
			.description("Tinny the leather crafter needs to restock his shelves.")
			.region(Region.DENIRAN_CITY)
			.questGiverNpc("Tinny");

		quest.history();

		quest.offer()
			.respondToRequest("Well, as a matter of fact I do have a task for you. I am in need of some"
					+ " supplies. Would you be interested in helping me?")
			.respondToReject("Ah well, maybe I can find another brave soul to help me.")
			.respondToAccept("Good! There is a few things that I need to restock my shelves. I would"
					+ " reward you nicely, I do love working with leather.");

		quest.task();

		quest.complete();

		return quest;
	}
}
