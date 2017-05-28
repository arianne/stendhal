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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Kills the server the hard way without doing a normal shutdown. Do not use it
 * unless the server has already crashed. You should warn connected players to
 * logout if that is still possible.
 *
 * If the server is started in a loop, it will come up again: while sleep 60; do
 * java -jar marauroa -c marauroa.ini -l; done
 *
 * @author hendrik
 */
public class ServerReset extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {

		try {
			final String text = admin.getTitle()
					+ " started emergency shutdown of the server.";
			SingletonRepository.getRuleProcessor().tellAllPlayers(NotificationType.SUPPORT, text);

		} catch (final Throwable e) {
			// Yes, i know that you are not supposed to catch Throwable
			// because of ThreadDeath. But we are here because of an
			// emergency situation and don't know what went wrong. So we
			// try very hard to reach the following line.
		}

		Runtime.getRuntime().halt(1);
	}
}
