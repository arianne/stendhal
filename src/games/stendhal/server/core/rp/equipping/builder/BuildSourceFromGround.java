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

import static games.stendhal.common.constants.Actions.BASEITEM;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.GroundSlot;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

class BuildSourceFromGround implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		final StendhalRPZone zone = player.getZone();
		final int objectid = action.getInt(BASEITEM);
		final Entity object = EntityHelper.entityFromZoneByID(objectid, zone);
		if (!(object instanceof PassiveEntity)) {
			return;
		}
		data.addSourceItem(object);
		data.addSourceSlot(new GroundSlot(zone, object));
	}
}
