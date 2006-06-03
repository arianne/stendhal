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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Look extends ActionListener {
	private static final Logger logger = Log4J.getLogger(Look.class);

	public static void register() {
		StendhalRPRuleProcessor.register("look", new Look());
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "look");

		// When look is casted over something in a slot
		if (action.has("baseitem") && action.has("baseobject") && action.has("baseslot")) {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());

			int baseObject = action.getInt("baseobject");

			RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
			if (!zone.has(baseobjectid)) {
				return;
			}

			RPObject base = zone.get(baseobjectid);
			if (!(base instanceof Entity)) {
				// Shouldn't really happen because everything is an entity
				return;
			}

			Entity baseEntity = (Entity) base;
			Entity entity = baseEntity;

			if (baseEntity.hasSlot(action.get("baseslot"))) {
				RPSlot slot = baseEntity.getSlot(action.get("baseslot"));

				if (slot.size() == 0) {
					return;
				}

				RPObject object = null;
				int item = action.getInt("baseitem");
				// scan through the slot to find the requested item
				for (RPObject rpobject : slot) {
					if (rpobject.getID().getObjectID() == item) {
						object = rpobject;
						break;
					}
				}

				// no item found...we take the first one
				if (object == null) {
					object = slot.iterator().next();
				}

				// It is always an entity
				entity = (Entity) object;
			}

			String name = entity.get("type");
			if (entity.has("name")) {
				name = entity.get("name");
			}
			rules.addGameEvent(player.getName(), "look", name);
			player.setPrivateText(entity.describe());
			world.modify(player);
			rules.removePlayerText(player);
			return;
		}
		// When use is cast over something on the floor
		else if (action.has("target")) {
			int usedObject = action.getInt("target");

			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
					.getID());
			RPObject.ID targetid = new RPObject.ID(usedObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);
				// It is always an entity
				Entity entity = (Entity) object;
				String name = entity.get("type");
				if (entity.has("name")) {
					name = entity.get("name");
				}
				rules.addGameEvent(player.getName(), "look", name);
				player.setPrivateText(entity.describe());
				world.modify(player);
				rules.removePlayerText(player);
			}
		}

		Log4J.finishMethod(logger, "look");
	}
}
