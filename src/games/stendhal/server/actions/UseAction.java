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
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class UseAction implements ActionListener {

	public static void register() {
		CommandCenter.register("use", new UseAction());
	}

	public void onAction(Player player, RPAction action) {


		// When use is casted over something in a slot
		if (action.has("baseitem") && action.has("baseobject") && action.has("baseslot")) {
			StendhalRPZone zone = player.getZone();

			int baseObject = action.getInt("baseobject");

			RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
			if (!zone.has(baseobjectid)) {
				return;
			}

			RPObject base = zone.get(baseobjectid);
			if (!((base instanceof Player) || (base instanceof Corpse) || (base instanceof Chest))) {
				// Only allow to use objects from players, corpses or chests
				return;
			}

			if ((base instanceof Player) && !player.getID().equals(base.getID())) {
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

				invokeUseListener(player, object);
			}
		} else if (action.has("target")) {
			//	use is cast over something on the floor
			int usedObject = action.getInt("target");

			StendhalRPZone zone = player.getZone();
			RPObject.ID targetid = new RPObject.ID(usedObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);

				invokeUseListener(player, object);
			}
		}


	}

	private void invokeUseListener(Player player, RPObject object) {

		// HACK: No item transfer in jail (we don't want a jailed player to
		//       use items like home scroll.
		String zonename = player.getZone().getName();

		if ((object instanceof Item) && (zonename.endsWith("_jail"))) {
			player.sendPrivateText("For security reasons items may not be used in jail.");
			return;
		}

		String name = object.get("type");
		if (object.has("name")) {
			name = object.get("name");
		}
		String infostring = "";
		if (object.has("infostring")) {
			infostring = object.get("infostring");
		}

		StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "use", name, infostring);

		if (object instanceof UseListener) {
			UseListener listener = (UseListener) object;
			// Make sure nobody uses items bound to someone else.
			if (listener instanceof Item) {
				Item item = (Item) listener;
				if (item.has("bound") && !item.get("bound").equals(player.getName())) {
					player.sendPrivateText("This " + ((Item) listener).getName() + " is a special reward for " + item.get("bound")
							+ ". You do not deserve to use it.");
					return;
				}
			}
			listener.onUsed(player);
		}
	}
}
