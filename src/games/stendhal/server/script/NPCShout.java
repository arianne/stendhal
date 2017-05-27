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
 * Impersonate a NPC to shout a message to all players.
 *
 * @author hendrik
 */
public class NPCShout extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		if (args.size() < 2) {
			admin.sendPrivateText("Usage: /script NPCShout.class npc text");
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(args.get(0));
			sb.append(" shouts: ");
			boolean first = true;
			for (final String word : args) {
				if (first) {
					first = false;
				} else {
					sb.append(word);
					sb.append(" ");
				}
			}
			final String text = sb.toString();

			SingletonRepository.getRuleProcessor().tellAllPlayers(NotificationType.PRIVMSG, text);
		}
	}

}
