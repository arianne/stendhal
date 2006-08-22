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
import games.stendhal.server.events.UseListener;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class UseAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(UseAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("use", new UseAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "use");

		// HACK: No item transfer in jail (we don't want a jailed player to
		//       create a new free character and give it all items.
		if (StendhalRPWorld.get().getRPZone(player.getID()).getID().getID().endsWith("_jail")) {
			player.sendPrivateText("For security reasons items may not be used in jail.");
			return;
		}

		// When use is casted over something in a slot
		if (action.has("baseitem") && action.has("baseobject")
				&& action.has("baseslot")) {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player
					.getID());

			int baseObject = action.getInt("baseobject");

			RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
			if (!zone.has(baseobjectid)) {
				return;
			}

			RPObject base = zone.get(baseobjectid);
			if (!(base instanceof Player || base instanceof Corpse || base instanceof Chest)) {
				// Only allow to use objects from players, corpses or chests
				return;
			}

			if (base instanceof Player && !player.getID().equals(base.getID())) {
				// Only allowed to use item of our own player.
				return;
			}

			Entity baseEntity = (Entity) base;

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
				Entity entity = (Entity) object;

				StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "use", entity.get("name"));

				if (object instanceof UseListener) {
					UseListener useListener = (UseListener) entity;
					useListener.onUsed(player);
					return;
				}
			}
		}
		// When use is cast over something on the floor
		else if (action.has("target")) {
			int usedObject = action.getInt("target");

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player
					.getID());
			RPObject.ID targetid = new RPObject.ID(usedObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);

				String name = object.get("type");
				if (object.has("name")) {
					name = object.get("name");
				}

				StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "use", name);

				if (object instanceof UseListener) {
					UseListener item = (UseListener) object;
					item.onUsed(player);
					return;
				}
			}
		}

		Log4J.finishMethod(logger, "use");
	}
}
