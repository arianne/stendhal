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
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.GHOSTMODE;
import static games.stendhal.common.constants.Actions.INVISIBLE;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.dbcommand.SetOnlineStatusCommand;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;

/**
 * changes the ghostmode flag of admins
 */
public class GhostModeAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(GHOSTMODE, new GhostModeAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (alreadyInRequestedMode(player, action)) {
			return;
		}

		if (player.isGhost()) {
			deactivateGhost(player);
		} else {
			activateGhostmode(player);
		}

		/* Notify database that the player is in Ghost mode */
		DBCommand command = new SetOnlineStatusCommand(player.getName(), !player.isGhost());
		DBCommandQueue.get().enqueue(command);

		/* Notify players about admin going into ghost mode. */
		StendhalRPRuleProcessor.get().notifyOnlineStatus(!player.isGhost(), player);

		player.notifyWorldAboutChanges();
	}

	/**
	 * is the player already in teh requested mode?
	 *
	 * @param player Player
	 * @param action request
	 * @return true, if the requested and the current mode match, false otherwise
	 */
	private boolean alreadyInRequestedMode(final Player player, final RPAction action) {
		if (!action.has("mode")) {
			return false;
		}

		boolean requestedMode = Boolean.parseBoolean(action.get("mode"));
		return (requestedMode == player.isGhost());
	}

	/**
	 * deactivates ghostmode
	 *
	 * @param player the admin
	 */
	private void deactivateGhost(final Player player) {
		player.setGhost(false);
		new GameEvent(player.getName(), GHOSTMODE, "off").raise();
	}


	/**
	 * activtes ghostmode and makes the player invisible to monsters.
	 *
	 * @param player the admin
	 */
	public static void activateGhostmode(final Player player) {
		player.setInvisible(true);
		new GameEvent(player.getName(), INVISIBLE, "on").raise();

		player.setGhost(true);
		new GameEvent(player.getName(), GHOSTMODE, "on").raise();
	}

}
