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

public class Item extends PassiveEntity {
	@Override
	public ActionType defaultAction() {
		return ActionType.USE;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		list.add(ActionType.USE.getRepresentation());
		super.buildOfferedActions(list);
	}
}
