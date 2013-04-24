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

import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.actions.equip.EquipUtil;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

class BuildSourceFromOldActionFormat implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		final Entity parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.BASE_OBJECT));

		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		final String slotName = action.get(EquipActionConsts.BASE_SLOT);
		if (!parent.hasSlot(slotName)) {
			data.setErrorMessage("");
			return;
		}
		final RPSlot slot = parent.getSlot(slotName);
		if (!(slot instanceof EntitySlot)) {
			data.setErrorMessage("");
			return;
		}

		final RPObject.ID baseItemId = new RPObject.ID(action.getInt(EquipActionConsts.BASE_ITEM), "");
		if (!slot.has(baseItemId)) {
			data.setErrorMessage("");
			return;
		}
		final Entity entity = (Entity) slot.get(baseItemId);

		data.addSourceItem(entity);
		data.setSourceRoot(parent);
		data.addSourceSlot((EntitySlot) slot);
	}
}
