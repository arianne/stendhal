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
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

/**
 * The 2D view of a house portal.
 */

// we don't extend portal because that would remove Look from the list of actions.
// we want it at the top so we couldn't just add it back in.

class HousePortal2DView extends InvisibleEntity2DView<IEntity> {

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
		list.add(ActionType.USE.getRepresentation());
		list.add(ActionType.KNOCK.getRepresentation());
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {

		onAction(ActionType.LOOK);

	}

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
		case USE:
		case KNOCK:
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.PORTAL;
	}
}
