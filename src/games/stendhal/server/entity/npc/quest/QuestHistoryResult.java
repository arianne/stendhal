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
package games.stendhal.server.entity.npc.quest;

import java.util.List;

import games.stendhal.server.entity.player.Player;


/**
 * Class for adding a customized result to BuiltQuest history.
 */
public interface QuestHistoryResult {

	/**
	 * Called when history is requested.
	 *
	 * @param player
	 *   Player for which history is requested.
	 * @param res
	 *   History items.
	 */
	abstract void apply(Player player, List<String> res);
}
