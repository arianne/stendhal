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
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Moving of items around on the ground
 */
public class DisplaceAction implements ActionListener {

	private static final Logger logger = Log4J.getLogger(DisplaceAction.class);

	/**
	 * register this action
	 */
	public static void register() {
		StendhalRPRuleProcessor.register("displace", new DisplaceAction());
	}

	/**
	 * handle movement of items
	 */
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "displace");
		if (action.has("baseitem")) {
			int targetObject = action.getInt("baseitem");

			StendhalRPZone zone = player.getZone();
			RPObject.ID targetid = new RPObject.ID(targetObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);
				if (object instanceof PassiveEntity) {
					if (action.has("x") && action.has("y")) {
						int x = action.getInt("x");
						int y = action.getInt("y");

						PassiveEntity entity = (PassiveEntity) object;

						if (player.nextTo(entity) && (player.squaredDistance(x, y) < 8 * 8)
						        && !zone.simpleCollides(entity, x, y)) {
							StendhalRPRuleProcessor.get()
							        .addGameEvent(player.getName(), "displace", entity.get("type"));

							entity.setX(x);
							entity.setY(y);
							entity.notifyWorldAboutChanges();
							if (entity instanceof Item) {
								Item item = (Item) entity;
								item.onRemoveFromGround();
								item.onPutOnGround(player);
							}
						}
					}
				}
			}
		}

		Log4J.finishMethod(logger, "displace");
	}
}
