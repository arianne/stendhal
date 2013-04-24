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

/**
 * checks if there is enough space at the target
 *
 * @author hendrik
 */
public class IsThereEnoughSpace implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {

		// Simple cases: enough empty slots
		int distinctItems = data.getSourceItems().size();
		int emptySlots = data.getTargetSlot().getCapacity() - data.getTargetSlot().size();
		if (distinctItems <= emptySlots) {
			return true;
		}

		// TODO: is full?
		// TODO: support for stackable items
		// TODO: support for limited stackable items
		// TODO: support for swapping

		return false;
	}

}
