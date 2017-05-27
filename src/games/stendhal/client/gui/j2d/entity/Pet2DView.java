/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import java.util.List;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Pet;
import games.stendhal.client.entity.User;

/**
 * The 2D view of a pet.
 */
class Pet2DView extends DomesticAnimal2DView<Pet> {
	/**
	 * The weight that a pet becomes fat (big).
	 */
	private static final int BIG_WEIGHT = 20;

	//
	// DomesticAnimal2DView
	//

	/**
	 * Get the weight at which the animal becomes big.
	 *
	 * @return A weight.
	 */
	@Override
	protected int getBigWeight() {
		return BIG_WEIGHT;
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);
		User user = User.get();
		Pet pet = entity;
		if (user != null) {
			if (!user.hasPet()) {
				list.add(ActionType.OWN.getRepresentation());
			} else if ((pet != null) && (user.getPetID() == pet.getID().getObjectID())) {
				list.add(ActionType.LEAVE_PET.getRepresentation());
			}
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		if (isReleased()) {
			return;
		}
		switch (at) {
		case LEAVE_PET:
			at.send(at.fillTargetInfo(entity));
			break;
		default:
			super.onAction(at);
			break;
		}
	}
}
