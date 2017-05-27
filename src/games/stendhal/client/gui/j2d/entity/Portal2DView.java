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
import games.stendhal.client.entity.Portal;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

/**
 * The 2D view of a portal.
 *
 * @param <T> type of Portal
 */
class Portal2DView<T extends Portal> extends InvisibleEntity2DView<T> {

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
		Portal portal = entity;
		if ((portal != null) && !portal.isHidden()) {
			list.add(ActionType.USE.getRepresentation());

			super.buildActions(list);
			list.remove(ActionType.LOOK.getRepresentation());
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		if (!entity.isHidden()) {
			onAction(ActionType.USE);
		}
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
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}


	@Override
	public boolean isInteractive() {
		return entity.isUseable();
	}

	@Override
	public StendhalCursor getCursor() {
		if (isInteractive()) {
			return StendhalCursor.ACTIVITY;
		} else {
			return StendhalCursor.PORTAL;
		}
	}

}
