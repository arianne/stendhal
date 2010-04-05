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
package games.stendhal.server.actions.move;

import static games.stendhal.common.constants.Actions.MOVETO;
import static games.stendhal.common.constants.Actions.TELECLICKMODE;
import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.RPAction;

public class MoveToAction implements ActionListener {


	public static void register() {
		final MoveToAction moveTo = new MoveToAction();
		CommandCenter.register(MOVETO, moveTo);
	}

	public void onAction(final Player player, final RPAction action) {
		if (!player.getZone().isMoveToAllowed()) {
			player.sendPrivateText("Mouse movement is not possible here. Use your keyboard.");
			return;
		}
		
		if (player.isPoisoned()) {
			player.sendPrivateText("Poison has disoriented you and you cannot move normally. You only seem able to walk backwards and cannot plan out any route in advance.");
			return;
		}
		
		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has(X)
				&& action.has(Y)) {
			final int x = action.getInt(X);
			final int y = action.getInt(Y);
			if (player.has(TELECLICKMODE) && action.has("double_click")) {
				// Teleport
				final StendhalRPZone zone = player.getZone();
				player.teleport(zone, x, y, null, null);
			} else {
				// Walk
				final List<Node> path = Path.searchPath(player, x, y);
				path.add(new Node(path.get(path.size() - 1).getX() + 1, path.get(path.size() - 1).getY()));
				player.setPath(new FixedPath(path, false));
			}
		}

		player.applyClientDirection(false);
		player.notifyWorldAboutChanges();
	}
}
