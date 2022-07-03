/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import games.stendhal.client.entity.Item;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtWindowManager;


class UseableGenericItem2DView extends Item2DView<Item> {

	@Override
	public StendhalCursor getCursor() {
		boolean doubleClick = WtWindowManager.getInstance().getPropertyBoolean("ui.doubleclick", false);
		if (doubleClick) {
			return StendhalCursor.ACTIVITY;
		} else {
			return StendhalCursor.ITEM_USE;
		}
	}
}
