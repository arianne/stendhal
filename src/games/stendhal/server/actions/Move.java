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
import marauroa.server.game.*;
import games.stendhal.common.*;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.pathfinder.Path;

import java.util.List;

import marauroa.common.Log4J;

public class Move extends ActionListener {
	private static final Logger logger = Log4J.getLogger(Move.class);

	public static void register() {
		Move move = new Move();
		StendhalRPRuleProcessor.register("move", move);
		StendhalRPRuleProcessor.register("moveto", move);
	}

	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "move");

		String type = action.get("type");

		if (type.equals("move")) {
			move(world, rules, player, action);
		} else if (type.equals("moveto")) {
			moveto(world, rules, player, action);
		}
	}

	private void move(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "move");

		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has("dir")) {
			player.setDirection(Direction.build(action.getInt("dir")));
			player.setSpeed(1);
		}

		world.modify(player);

		Log4J.finishMethod(logger, "move");
	}

	private void moveto(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
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

		Log4J.finishMethod(logger, "moveto");
	}
}
