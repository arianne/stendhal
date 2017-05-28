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

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

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
			final Player player = SingletonRepository.getRuleProcessor().getPlayer(args.get(0));
			if (player == null) {
				admin.sendPrivateText("Player is not online");
				return;
			}
			SingletonRepository.getRuleProcessor().getRPManager().disconnectPlayer(player);
			admin.sendPrivateText(args.get(0) + " has been logged out");
		} catch (final Exception e) {
			logger.error(e, e);
		}

	}

}
