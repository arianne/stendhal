/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * A portal which can be "used" by the player. Use a Door if you want some
 * sprites for it.
 */
public class Portal extends InvisibleEntity {

	private boolean hidden = false;


	@Override
	public ActionType defaultAction() {
		if (!hidden) {
			return ActionType.USE;
		} else {
			return null;
		}
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
			case USE:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();
				rpaction.put("target", id);
				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		// do not call super method because we do not want the
		// Look menu until some nice text are there to be looked
		// at.
		// super.buildOfferedActions(list);

		if (!hidden) {
			list.add(ActionType.USE.getRepresentation());
			if (User.isAdmin()) {
				list.remove(ActionType.ADMIN_ALTER.getRepresentation());
			}
		}
	}

	@Override
	public void init(final RPObject object) {
		super.init(object);

		this.hidden = object.has("hidden");
	}
}
