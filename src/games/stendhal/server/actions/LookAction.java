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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class LookAction implements ActionListener {

	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String _TARGET = "target";
	private static final String _BASESLOT = "baseslot";
	private static final String _BASEOBJECT = "baseobject";
	private static final String _BASEITEM = "baseitem";
	private static final String _LOOK = "look";

	public static void register() {
		CommandCenter.register(_LOOK, new LookAction());
	}

	public void onAction(Player player, RPAction action) {

		StendhalRPWorld world = StendhalRPWorld.get();

		// When look is cast over something in a slot
		if (action.has(_BASEITEM) && action.has(_BASEOBJECT)
				&& action.has(_BASESLOT)) {
			StendhalRPZone zone = player.getZone();

			int baseObject = action.getInt(_BASEOBJECT);

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

			if (baseEntity.hasSlot(action.get(_BASESLOT))) {
				RPSlot slot = baseEntity.getSlot(action.get(_BASESLOT));

				if (slot.size() == 0) {
					return;
				}

				RPObject object = null;
				int item = action.getInt(_BASEITEM);
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

			String name = entity.get(ATTR_TYPE);
			if (entity.has(ATTR_NAME)) {
				name = entity.get(ATTR_NAME);
			}
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _LOOK,
					name);
			
			String text = entity.describe();
			if (entity instanceof Sign) {
				player.sendPrivateText(NotificationType.RESPONSE, text);
			} else {
				player.sendPrivateText(text);
			}
			world.modify(player);
			return;
		} else if (action.has(_TARGET)) {
			// evaluate the target parameter
			Entity entity = EntityHelper.entityFromTargetName(
					action.get(_TARGET), player.getZone());

			if (entity != null) {
				String name = entity.get(ATTR_TYPE);
				if (entity.has(ATTR_NAME)) {
					name = entity.get(ATTR_NAME);
				}
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						_LOOK, name);
				String text = entity.describe();
				if (entity instanceof Sign) {
					player.sendPrivateText(NotificationType.RESPONSE, text);
				} else {
					player.sendPrivateText(text);
				}
				world.modify(player);
			}
		}
	}
}
