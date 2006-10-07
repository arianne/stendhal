/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import org.apache.log4j.Logger;

import marauroa.common.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;

import marauroa.common.Log4J;

public class FaceAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(FaceAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("face", new FaceAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "face");

		if (action.has("dir")) {
			player.stop();
			player.setDirection(Direction.build(action.getInt("dir")));
			player.notifyWorldAboutChanges();
		}

		Log4J.finishMethod(logger, "face");
	}
}
