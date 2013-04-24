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
 * Ensures that the builders completed and did that without error message.
 *
 * @author hendrik
 */
class IsDataBuiltCompletely implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {
		if (data.getErrorMessage() != null) {
			return false;
		}

		if (data.getItemName() == null) {
			return false;
		}
		if (data.getSourceItems().isEmpty() || data.getTargetSlot() == null) {
			return false;
		}

		return true;
	}

}
