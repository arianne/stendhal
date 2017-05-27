/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TEXT;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.group.Group;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Sends a chat message to all members of the same group
 *
 * @author hendrik
 */
public class GroupMessageAction implements ActionListener {

	/**
	 * sends a message to the group
	 *
	 * @param player Player
	 * @param action RPAction
	 */
	@Override
	public void onAction(Player player, RPAction action) {

		// check that the player is not gagged and not jailed
		if (GagManager.checkIsGaggedAndInformPlayer(player)) {
			return;
		}

		if (Jail.isInJail(player)) {
			player.sendPrivateText("The strong anti telepathy aura prevents you from getting through. Use /support <text> to contact an admin!");
			return;
		}

		// is the player in a group?
		Group group = SingletonRepository.getGroupManager().getGroup(player.getName());
		if (group == null) {
			player.sendPrivateText(NotificationType.ERROR, "You are not in a group");
			return;
		}

		if (validateAction(action)) {
			group.sendGroupMessage(player.getName(), QuoteSpecials.quote(action.get(TEXT)));
		}
	}

	/**
	 * checks that all required parameters exist
	 *
	 * @param action RPAction to check
	 * @return true, if and onlyif all parameters are there
	 */
	private boolean validateAction(final RPAction action) {
		return action.has(TEXT);
	}

}
