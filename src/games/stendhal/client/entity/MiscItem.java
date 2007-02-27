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

import marauroa.common.game.*;
import games.stendhal.client.*;

public class MiscItem extends Item {

	public MiscItem( RPObject object)
			throws AttributeNotFoundException {
		super( object);
	}

	@Override
	protected void loadSprite(RPObject object) {
		SpriteStore store = SpriteStore.get();

		String miscName = object.get("class") + "_" + object.get("subclass");
		sprite = store.getSprite("data/sprites/items/misc/"
				+ object.get("class") + "/" + miscName + ".png");

	}
}
