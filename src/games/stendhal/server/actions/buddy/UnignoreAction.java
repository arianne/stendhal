/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * removes a player from the ignore list
 */
class UnignoreAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);
			if (player.getIgnore(who) == null) {
				player.sendPrivateText(who + " 没有被你屏闭。");
			} else if (player.removeIgnore(who)) {
				player.sendPrivateText(who + " 已从你的屏闭名单中删除");
			}
		}

	}

}
