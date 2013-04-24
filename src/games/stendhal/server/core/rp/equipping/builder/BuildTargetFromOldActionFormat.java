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
import marauroa.common.game.RPAction;

class BuildTargetFromOldActionFormat implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.TARGET_OBJECT));
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// get slot
		String slotName = action.get(EquipActionConsts.TARGET_SLOT);
		if (!parent.hasSlot(slotName)) {
			data.setErrorMessage("");
			return;
		}
		data.setTargetRoot(parent);
		data.setTargetSlot(parent.getEntitySlot(slotName));
	}
}
