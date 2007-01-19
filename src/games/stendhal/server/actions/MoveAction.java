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
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;

import java.util.List;

import marauroa.common.Log4J;

public class MoveAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(MoveAction.class);

	public static void register() {
		MoveAction move = new MoveAction();
		StendhalRPRuleProcessor.register("move", move);
		StendhalRPRuleProcessor.register("moveto", move);
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "move");

		String type = action.get("type");

		if (type.equals("move")) {
			move(player, action);
		} else if (type.equals("moveto")) {
			moveTo(player, action);
		}
	}

	private void move(Player player, RPAction action) {
		Log4J.startMethod(logger, "move");

		if (action.has("dir")) {
			int	dirval;


			if((dirval = action.getInt("dir")) < 0) {
				player.removeClientDirection(
					Direction.build(-dirval));
			} else {
				player.addClientDirection(
					Direction.build(dirval));
			}

			player.applyClientDirection(true);
		}

		player.notifyWorldAboutChanges();

		Log4J.finishMethod(logger, "move");
	}


	private void moveTo(Player player, RPAction action) {
		Log4J.startMethod(logger, "moveto");

		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has("x") && action.has("y")) {
			int x = action.getInt("x");
			int y = action.getInt("y");

			List<Path.Node> path = Path.searchPath(player, x, y - 2);
			player.setPath(path, false);
		}

		player.applyClientDirection(false);

		Log4J.finishMethod(logger, "moveto");
	}
}
