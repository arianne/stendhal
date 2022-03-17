/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rule.defaultruleset.creator;

import java.lang.reflect.Constructor;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.Item;

/**
 * Base item creator (using a constructor).
 */
abstract class AbstractItemCreator extends AbstractCreator<Item>{

	/**
	 *
	 */
	final DefaultItem defaultItem;

	public AbstractItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(construct, "Item");
		this.defaultItem = defaultItem;
	}
}
