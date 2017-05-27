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

import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.equip.EquipUtil;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

class BuildTargetFromPath implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		List<String> path = action.getList(Actions.TARGET_PATH);
		Iterator<String> it = path.iterator();

		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, MathHelper.parseInt(it.next()));
		Entity root = parent;
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// Walk the slot path
		EntitySlot slot = null;
		while (it.hasNext()) {
			String slotName = it.next();
			if (!parent.hasSlot(slotName)) {
				data.setErrorMessage("");
				return;
			}
			slot = parent.getEntitySlot(slotName);
			if (!it.hasNext()) {
				break;
			}

			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				data.setErrorMessage("");
				return;
			}
			parent = (Entity) slot.get(itemId);
		}
		data.setTargetRoot(root);
		data.setTargetSlot(slot);
	}
}
