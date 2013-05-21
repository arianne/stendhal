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

//
//

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;

import java.util.List;

/**
 * The 2D view of a useable item.
 */
class UseableItem2DView extends StackableItem2DView<StackableItem> {

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
		if (!entity.getRPObject().has("menu")) {
			list.add(ActionType.USE.getRepresentation());
		}

		super.buildActions(list);
	}

	@Override
	public StendhalCursor getCursor() {
		boolean doubleClick = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("ui.doubleclick", "false"));
		if (doubleClick) {
			return StendhalCursor.ACTIVITY;
		} else {
			return StendhalCursor.ITEM_USE; 
		}
	}
}
