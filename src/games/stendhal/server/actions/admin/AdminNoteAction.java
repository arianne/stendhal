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

import org.apache.log4j.Logger;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class AdminNoteAction extends AdministrationAction {
	@Override
	protected void perform(final Player player, final RPAction action) {
		String sender = player.getName();
		if (action.has("sender") && (player.getName().equals("postman"))) {
			sender = action.get("sender");
		}

		if (action.has("target")) {
			String target = action.get("target");
			String adminnote = action.get("note");

			Logger.getLogger(AdminNoteAction.class).info(sender + " has added an adminnote to " + target + " saying: " + adminnote);
			new GameEvent(sender, "adminnote",  target, adminnote).raise();
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
					sender + " has added an adminnote to " + target
					+ " saying: " + adminnote);
		}
	}

	public static void register() {
		CommandCenter.register("adminnote", new AdminNoteAction(), 100);
	}
}
