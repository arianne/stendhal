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

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TutorialNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;

public class MoveAction implements ActionListener {

	private static final Logger logger = Log4J.getLogger(MoveAction.class);

	public static void register() {
		MoveAction move = new MoveAction();
		StendhalRPRuleProcessor.register("move", move);
		StendhalRPRuleProcessor.register("moveto", move);
	}

	public void onAction(Player player, RPAction action) {
		String type = action.get("type");

		if (type.equals("move")) {
			move(player, action);
		} else if (type.equals("moveto")) {
			moveTo(player, action);
		}
	}

	private void move(Player player, RPAction action) {
		if (action.has("dir")) {
			int dirval;

			if ((dirval = action.getInt("dir")) < 0) {
				player.removeClientDirection(Direction.build(-dirval));
			} else {
				player.addClientDirection(Direction.build(dirval));
			}

			player.applyClientDirection(true);
		}

		TutorialNotifier.move(player);
		player.notifyWorldAboutChanges();
	}

	private void moveTo(Player player, RPAction action) {
		if (!player.getZone().isMoveToAllowed()) {
			player.sendPrivateText("Mouse movement is not possible here. Use you keyboard");
			return;
		}

		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has("x") && action.has("y")) {
			int x = action.getInt("x");
			int y = action.getInt("y");
			if(!player.has("teleclickmode")) {
				//Walk
				List<Path.Node> path = Path.searchPath(player, x, y - 2);
				player.setPath(path, false);
			} else {
				//Teleport
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
				player.teleport(zone, x, y, null, null);
			}
		}

		player.applyClientDirection(false);
	}
}
