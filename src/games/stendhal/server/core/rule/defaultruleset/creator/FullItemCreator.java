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
package games.stendhal.server.core.rule.defaultruleset.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.Item;

/**
 * Create an item class via the full arguments (<em>name, clazz,
 * subclazz, attributes</em>)
 * constructor.
 */
public class FullItemCreator extends AbstractItemCreator {

	public FullItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(defaultItem, construct);
	}

	@Override
	protected Item createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return (Item) construct.newInstance(new Object[] { this.defaultItem.getItemName(), this.defaultItem.getItemClass(), this.defaultItem.getItemSubclass(),
				this.defaultItem.getAttributes() });
	}
}