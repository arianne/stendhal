/* $Id$
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
package games.stendhal.server.entity.item.scroll;

import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Map;

/**
 * Represents a scroll with a context stackable infostring.
 */
public class InfoStringScroll extends Scroll {

	/**
	 * Creates a new infostring stackable scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public InfoStringScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public InfoStringScroll(InfoStringScroll item) {
		super(item);
	}

	// TODO: Move up to stackable item?
	@Override
	public boolean isStackable(Stackable other) {
		StackableItem otheri = (StackableItem) other;

		// Same types?
		if (!super.isStackable(other)) {
			return false;
		}

		// scroll can be stacked if they have the same infostring
		String infostring = getInfoString();
		String oinfostring = otheri.getInfoString();

		if (infostring != null) {
			return infostring.equals(oinfostring);
		} else {
			// scrolls without infostring can be stacked as well
			return (oinfostring == null);
		}
	}
}
