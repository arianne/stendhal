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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.RPAction;

public class Item extends PassiveEntity {
	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.USE;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at =handleAction(action);

		switch (at) {
			case USE:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();

				if (params.length > 0) {
					rpaction.put("baseobject", params[0]);
					rpaction.put("baseslot", params[1]);
					rpaction.put("baseitem", id);
				} else {
					rpaction.put("target", id);
				}

				at.send(rpaction);
				break;

			default:
				super.onAction(at, params);
				break;
		}

	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new Item2DView(this);
	}
}
