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

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.pathfinder.Path.Node;

import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class OwnAction implements ActionListener {

	private static final Logger logger = Log4J.getLogger(OwnAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("own", new OwnAction());
	}

	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "own");

		// BUG: This features is potentially abusable right now. Consider
		// removing it...
		if (player.hasSheep() && action.has("target") && (action.getInt("target") == -1)) // Allow release of sheep
		{
			Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
			player.removeSheep(sheep);

			sheep.setOwner(null);
			StendhalRPRuleProcessor.get().addNPC(sheep);

			// HACK: Avoid a problem on database
			if (sheep.has("#db_id")) {
				sheep.remove("#db_id");
			}

			player.notifyWorldAboutChanges();
			return;
		}

		if (player.hasSheep()) {
			return;
		}

		if (action.has("target")) {
			int targetObject = action.getInt("target");

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
			RPObject.ID targetid = new RPObject.ID(targetObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);
				if (object instanceof Sheep) {
					Sheep sheep = (Sheep) object;
					if (sheep.getOwner() == null) {

						List<Node> path = Path.searchPath(player, player.getX(), player.getY(), sheep.getArea(sheep
						        .getX(), sheep.getY()), 7);
						if (!path.isEmpty()) {

							sheep.setOwner(player);
							StendhalRPRuleProcessor.get().removeNPC(sheep);

							player.setSheep(sheep);
							player.notifyWorldAboutChanges();
						} else {
							// There is no path between sheep and player so it
							// is unreachable. But don't tell the player 
							// anything about pathfinding.
							player.sendPrivateText("That sheep is too far away.");
						}
					} else {
						player.sendPrivateText("This sheep is owned by " + sheep.getOwner().getName());
					}
				}
			}
		}

		Log4J.finishMethod(logger, "own");
	}
}
