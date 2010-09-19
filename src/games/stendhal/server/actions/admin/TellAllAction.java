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

import static games.stendhal.common.constants.Actions.TELLALL;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class TellAllAction extends AdministrationAction {


	public static void register() {
		CommandCenter.register(TELLALL, new TellAllAction(), 200);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TEXT)) {
			final String message = "Administrator SHOUTS: " + action.get(TEXT);
			
			new GameEvent(player.getName(), TELLALL, action.get(TEXT)).raise();

			SingletonRepository.getRuleProcessor().tellAllPlayers(message);
		}
	}

}
