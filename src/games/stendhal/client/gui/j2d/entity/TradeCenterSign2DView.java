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

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Sign;

import java.util.List;

class TradeCenterSign2DView extends Sign2DView<Sign> {

	/* (non-Javadoc)
	 * @see games.stendhal.client.gui.j2d.entity.Entity2DView#buildActions(java.util.List)
	 */
	@Override
	protected void buildActions(List<String> list) {
		list.add(ActionType.USE.getRepresentation());
		super.buildActions(list);
	}

	/* (non-Javadoc)
	 * @see games.stendhal.client.gui.j2d.entity.Entity2DView#onAction(games.stendhal.client.entity.ActionType)
	 */
	@Override
	public void onAction(ActionType at) {
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
}
