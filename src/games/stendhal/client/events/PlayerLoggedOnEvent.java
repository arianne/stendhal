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
package games.stendhal.client.events;

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;

import org.apache.log4j.Logger;

import games.stendhal.client.World;
import games.stendhal.client.actions.MoveContinuousAction;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.wt.core.WtWindowManager;

class PlayerLoggedOnEvent extends Event<Entity> {
	private static final Logger logger = Logger.getLogger(PlayerLoggedOnEvent.class);

	// FIXME: Called twice when player logs on?
	@Override
	public void execute() {
		String playerName = event.get("name");
		logger.debug("Executing logon event for "+playerName);
		World.get().addPlayerLoggingOn(playerName);

		if (entity instanceof Player) {
			// Continuous movement setting.
			boolean moveContinuous = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty(MOVE_CONTINUOUS, "false"));
			if (moveContinuous) {
				WtWindowManager.getInstance().setProperty(MOVE_CONTINUOUS, "true");
				new MoveContinuousAction().sendAction(true, false);
			}
		}
	}
}
