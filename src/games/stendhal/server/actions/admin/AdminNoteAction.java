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
package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class AdminNoteAction extends AdministrationAction {
	@Override
	protected void perform(final Player player, final RPAction action) {
		if (action.has("target")) {
			String target = action.get("target");
			String adminnote = action.get("note");

			Logger.getLogger(AdminNoteAction.class).info(player.getName() + " has added an adminnote to " + target + " saying: " + adminnote);
			new GameEvent(player.getName(), "adminnote",  target, adminnote).raise();				
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
					player.getName() + " has added an adminnote to " + target
					+ " saying: " + adminnote);
		}
	}
	public static void register() {
		CommandCenter.register("adminnote", new AdminNoteAction(), 100);
	}
}
