/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.equipping.builder;

import java.util.Iterator;
import java.util.List;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.MathHelper;
import games.stendhal.server.actions.equip.EquipUtil;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.GroundSlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

class BuildSourceFromPath implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		List<String> path = action.getList(EquipActionConsts.SOURCE_PATH);
		Iterator<String> it = path.iterator();

		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, MathHelper.parseInt(it.next()));
		Entity root = parent;
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// Walk the slot path
		Entity entity = parent;
		String slotName = null;
		while (it.hasNext()) {
			slotName = it.next();
			if (!entity.hasSlot(slotName)) {
				data.setErrorMessage("");
				return;
			}

			final RPSlot slot = entity.getSlot(slotName);
			if (slot == null) {
				data.setErrorMessage("");
				return;
			}
			if (!it.hasNext()) {
				data.setErrorMessage("");
				return;
			}
			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				data.setErrorMessage("");
				return;
			}

			entity = (Entity) slot.get(itemId);
		}

		// if the item is not contained, the item is on the ground
		if (parent == entity) {
			data.addSourceItem(entity);
			data.addSourceSlot(new GroundSlot(player.getZone(), entity));
		} else {
			data.addSourceItem(entity);
			data.setSourceRoot(root);
			data.addSourceSlot((EntitySlot) entity.getContainerSlot());
		}
	}
}
