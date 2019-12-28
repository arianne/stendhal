/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.marriage;

import games.stendhal.server.entity.player.Player;

public class MarriageQuestInfo {
	private static final String QUEST_SLOT = "marriage";

	/**
	 * Name of the spouse slot.
	 * The spouse's name is stored in one of the player's quest slots.
	 * This is necessary to disallow polygamy.
	 */
	private static final String SPOUSE_QUEST_SLOT = "spouse";

	/**
	 * Get the name of the quest slot.
	 *
	 * @return quest slot name
	 */
	public String getQuestSlot() {
		return QUEST_SLOT;
	}

	/**
	 * Get the name of the spouse slot.
	 *
	 * @return spouse slot name
	 */
	public String getSpouseQuestSlot() {
		return SPOUSE_QUEST_SLOT;
	}

	/**
	 * Check if a player is married to another player.
	 *
	 * @param player the player to be checked
	 * @return true iff the player is married
	 */
	public boolean isMarried(final Player player) {
		return player.hasQuest(SPOUSE_QUEST_SLOT);
	}

	/**
	 * Check if a player is engaged with another player.
	 *
	 * @param player the player to check
	 * @return true iff the player is engaged
	 */
    public boolean isEngaged(final Player player) {
        return (player.hasQuest(QUEST_SLOT)
        		&& (player.getQuest(QUEST_SLOT).startsWith("engaged") || player.getQuest(QUEST_SLOT).startsWith("forging;")));
    }
}
