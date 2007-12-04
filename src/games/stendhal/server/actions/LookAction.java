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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class LookAction implements ActionListener {


	public static void register() {
		CommandCenter.register("look", new LookAction());
	}

	public void onAction(Player player, RPAction action) {


		StendhalRPWorld world = StendhalRPWorld.get();

		// When look is casted over something in a slot
		if (action.has("baseitem") && action.has("baseobject") && action.has("baseslot")) {
			StendhalRPZone zone = player.getZone();

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
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "look", name);
			player.sendPrivateText(entity.describe());
			world.modify(player);
			return;
		} else if (action.has("target")) {
			//	use is cast over something on the floor
			int usedObject = action.getInt("target");

			StendhalRPZone zone = player.getZone();
			RPObject.ID targetid = new RPObject.ID(usedObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);
				// It is always an entity
				Entity entity = (Entity) object;
				String name = entity.get("type");
				if (entity.has("name")) {
					name = entity.get("name");
				}
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "look", name);
				player.sendPrivateText(entity.describe());
				world.modify(player);
			}
		}


	}
}
