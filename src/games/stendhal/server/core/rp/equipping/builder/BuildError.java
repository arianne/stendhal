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

import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * builds an error
 *
 * @author hendrik
 */
public class BuildError implements PartialBuilder {

	@Override
	public void build(EquipmentActionData data, Player player, RPAction action) {
		data.setErrorMessage("");
	}

}
