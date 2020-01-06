/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks if a quest is registered in the world.
 */
public class QuestRegisteredCondition implements ChatCondition {

	final private String questSlot;
	final private boolean isSlotName;

	/**
	 * Creates a new QuestRegisteredCondition using a quest slot.
	 *
	 * @param questSlot
	 * 		Slot string.
	 */
	public QuestRegisteredCondition(final String questSlot) {
		this.questSlot = questSlot;
		this.isSlotName = true;
	}

	/**
	 * Creates a new QuestRegisteredCondition using either a quest slot or name.
	 *
	 * @param questSlot
	 * 		Slot or name string.
	 * @param isSlotName
	 * 		If <code>true</code>, checks against registered quests' slot strings,
	 * 		otherwise checks against their name string.
	 */
	public QuestRegisteredCondition(final String questSlot, final boolean isSlotName) {
		this.questSlot = questSlot;
		this.isSlotName = isSlotName;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		if (isSlotName) {
			return StendhalQuestSystem.get().getQuestFromSlot(questSlot) != null;
		}

		return StendhalQuestSystem.get().getQuest(questSlot) != null;
	}

	@Override
	public String toString() {
		return "QuestRegisteredCondition: " + questSlot;
	}

	@Override
	public int hashCode() {
		return questSlot.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestRegisteredCondition)) {
			return false;
		}

		final QuestRegisteredCondition other = (QuestRegisteredCondition) obj;
		return questSlot.equals(other.questSlot) && isSlotName == other.isSlotName;
	}
}
