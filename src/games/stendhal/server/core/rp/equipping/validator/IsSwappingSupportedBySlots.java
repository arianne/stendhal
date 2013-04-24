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
package games.stendhal.server.core.rp.equipping.validator;

import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.slot.EntitySlot;

/**
 * Checks if the source and target slots support swapping of items.
 *
 * @author hendrik
 */
class IsSwappingSupportedBySlots implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {

		// Is the source owned by the player?
		if (data.getSourceItems().get(0).getContainerBaseOwner() != data.getPlayer()) {
			return false;
		}

		// Is the target slot owned by the player?
		EntitySlot targetSlot = data.getTargetSlot();
		if (!targetSlot.hasAsAncestor(data.getPlayer())) {
			return false;
		}

		// Target slot needs to be a single item slot
		return targetSlot.getCapacity() == 1;
	}

}
