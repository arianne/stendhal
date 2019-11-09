/***************************************************************************
 *                   (C) Copyright 2018 - Stendhal                         *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.antivenom_ring;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;

public abstract class AVRQuestStage {

	/* NPC list */
	protected NPCList npcs = SingletonRepository.getNPCList();

	protected final String QUEST_SLOT;
	protected final String subquestName;

	protected final String npcName;

	public AVRQuestStage(final String npc, final String questSlot, final String subquest) {
		this.npcName = npc;
		this.QUEST_SLOT = questSlot;
		this.subquestName = subquest;

		addDialogue();
	}

	public AVRQuestStage(final String npc, final String questSlot) {
		this.npcName = npc;
		this.QUEST_SLOT = questSlot;
		this.subquestName = null;

		addDialogue();
	}

	protected abstract void addDialogue();
}
