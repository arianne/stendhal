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
 * validates an equipment action
 *
 * @author hendrik
 */
public class EquipmentActionValidator {
	Validator[] validators = new Validator[]{
		new IsDataBuiltCompletely(),
		new AreSlotsReachable(),
		new IsPutIntoCorpse(),
		new IsBoundStatusRespected(),
		new IsQuantityInRangeOfSourceItems(),
		new IsThereEnoughSpace()
	};

	/**
	 * validates an equipment action
	 *
	 * @param data the EquipmentActionData, errorMessage and warningMessage may be set.
	 * @return true, if the action may be performed, false otherwise
	 */
	public boolean validate(EquipmentActionData data) {
		for (Validator validator : validators) {
			if (!validator.validate(data)) {
				return false;
			}
		}

		return true;
	}
}
