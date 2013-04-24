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
package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import marauroa.common.game.RPObject;

public class FlowerGrowerTransFormer implements Transformer {

	@Override
	public RPObject transform(final RPObject object) {
			String itemname = object.get("class");
			itemname = itemname.substring(itemname.lastIndexOf('/') + 1, itemname.length() - "_grower".length());
			return new FlowerGrower(object, itemname);
	}

}
