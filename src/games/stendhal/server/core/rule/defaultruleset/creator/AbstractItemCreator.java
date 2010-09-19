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

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.Item;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Base item creator (using a constructor).
 */
abstract class AbstractItemCreator extends AbstractCreator<Item>{
	
	static final Logger logger = Logger.getLogger(AbstractItemCreator.class);

	/**
	 * 
	 */
	final DefaultItem defaultItem;
	
	public AbstractItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(construct, "Item");
		this.defaultItem = defaultItem;
	}
}