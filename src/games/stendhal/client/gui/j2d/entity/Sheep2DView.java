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
import games.stendhal.client.entity.Sheep;
import games.stendhal.client.entity.User;

/**
 * The 2D view of a sheep.
 */
class Sheep2DView extends DomesticAnimal2DView<Sheep> {
	/**
	 * The weight that a sheep becomes fat (big).
	 */
	private static final int BIG_WEIGHT = 60;

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
		Sheep sheep = entity;

		if (user != null) {
			if (!user.hasSheep()) {
				list.add(ActionType.OWN.getRepresentation());
			} else if ((sheep != null) && (user.getSheepID() == sheep.getID()
					.getObjectID())) {
				list.add(ActionType.LEAVE_SHEEP.getRepresentation());
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
		case LEAVE_SHEEP:
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
