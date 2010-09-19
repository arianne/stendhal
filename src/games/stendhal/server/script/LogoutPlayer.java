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
package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

import org.apache.log4j.Logger;

/**
 * Logs a player out.
 * 
 * @author hendrik
 */
public class LogoutPlayer extends ScriptImpl {

	private static Logger logger = Logger.getLogger(LogoutPlayer.class);

	@Override
	public void execute(final Player admin, final List<String> args) {

		// help text
		if (args.size() == 0) {
			admin.sendPrivateText("/script LogoutPlayer.class <playername> logs a player out");
			return;
		}

		try {
			// see processLogoutEvent in
			// marauroa-1.34/src/marauroa/server/game/GameServerManager.java

			final PlayerEntryContainer playerContainer = PlayerEntryContainer.getContainer();
			final PlayerEntry entry = playerContainer.get(args.get(0));
			if (entry == null) {
				admin.sendPrivateText(args.get(0) + " not found");
				return;
			}

			final Player player = (Player) entry.object;
			SingletonRepository.getRuleProcessor().getRPManager().disconnectPlayer(
					player);
			admin.sendPrivateText(args.get(0) + " has been logged out");
		} catch (final Exception e) {
			logger.error(e, e);
		}

	}

}
