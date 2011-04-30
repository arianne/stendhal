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
 * builds parts of an EquipmentActionData
 *
 * @author hendrik
 */
interface PartialBuilder {

	/**
	 * builds parts of an EquipmentActionData object
	 *
	 * @param data the EquipmentActionData to build
	 * @param player player sending the action
	 * @param action action to process
	 */
	public void build(EquipmentActionData data, Player player, RPAction action);
}
