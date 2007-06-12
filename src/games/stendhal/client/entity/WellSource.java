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

import marauroa.common.game.RPAction;

/**
 * @author daniel
 *
 */
public class WellSource extends Entity {
	@Override
	public ActionType defaultAction() {
		return ActionType.WISH;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		list.add(ActionType.WISH.getRepresentation());
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		switch (at) {
			case WISH:
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


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	@Override
	protected Entity2DView createView() {
		return new WellSource2DView(this);
	}


	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> the entity can not enter this
	 *		entity's area.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		return false;
	}
}
