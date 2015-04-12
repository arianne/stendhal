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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.item.Corpse;

/**
 * Checks if the item is put into a corpse although it was not in one before.
 *
 * @author hendrik
 */
class IsPutIntoCorpse implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {
		// players sometimes accidentally drop items into corpses, so inform about all drops into a corpse
		// which aren't just a movement from one corpse to another.
		// we could of course specifically preclude dropping into corpses, but that is undesirable.

		boolean sourceIsCorpse = (data.getSourceRoot() instanceof Corpse);
		boolean targetIsCorpse = (data.getTargetRoot() instanceof Corpse);
		if (!sourceIsCorpse && targetIsCorpse) {
			data.setWarningMessage("For your information, you just dropped "
				+ Grammar.quantityplnounWithMarker(data.getQuantity(), data.getItemName(), '§')
				+ " into a corpse next to you.");
		}

		return true;
	}

}
