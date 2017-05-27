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

import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;

import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.GroundSlot;
import marauroa.common.game.RPAction;

class BuildTargetFromGround implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		final int x = action.getInt(X);
		final int y = action.getInt(Y);
		data.setTargetSlot(new GroundSlot(player.getZone(), x, y));
	}
}
