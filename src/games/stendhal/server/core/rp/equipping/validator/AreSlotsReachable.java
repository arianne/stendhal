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
 * are the source and target slots reachable?
 *
 * @author hendrik
 */
public class AreSlotsReachable implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {

		// source slots
		for (EntitySlot slot : data.getSourceSlots()) {
			if (!slot.isReachableForTakingThingsOutOfBy(data.getPlayer())) {
				data.setErrorMessage(slot.getErrorMessage());
				return false;
			}
		}

		// target slots
		return data.getTargetSlot().isReachableForThrowingThingsIntoBy(data.getPlayer());
	}

}
