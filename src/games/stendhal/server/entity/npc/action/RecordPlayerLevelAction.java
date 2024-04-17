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
package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


/**
 * Action to store player's level in a quest slot.
 */
public class RecordPlayerLevelAction implements ChatAction {

	/** Quest string identifier. */
	private final String questSlot;
	/** Slot index at which player level is stored. */
	private Integer index;


	/**
	 * Creates a new RecordPlayerLevelAction.
	 *
	 * @param questSlot
	 *   Quest string identifier.
	 */
	public RecordPlayerLevelAction(final String questSlot) {
		this.questSlot = checkNotNull(questSlot);
	}

	/**
	 * Creates a new RecordPlayerLevelAction.
	 *
	 * @param questSlot
	 *   Quest string identifier.
	 * @param index
	 *   Slot index at which player level is stored.
	 */
	public RecordPlayerLevelAction(final String questSlot, final int index) {
		this.questSlot = checkNotNull(questSlot);
		this.index = index;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		if (index != null) {
			player.setQuest(questSlot, index, String.valueOf(player.getLevel()));
			return;
		}
		player.setQuest(questSlot, String.valueOf(player.getLevel()));
	}

	@Override
	public String toString() {
		String value = RecordPlayerLevelAction.class.getSimpleName() + "(" + questSlot;
		if (index != null) {
			value += ", " + index;
		}
		return value + ")";
	}

	@Override
	public int hashCode() {
		int hash = 5009;
		hash = 5011 * hash + questSlot.hashCode();
		hash = 5021 * hash + (index != null ? index : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RecordPlayerLevelAction)) {
			return false;
		}
		final RecordPlayerLevelAction other = (RecordPlayerLevelAction) obj;
		return this.questSlot.equals(other.questSlot) && this.index == other.index;
	}
}
