/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.slot.ContainerItemSlot;

import java.util.Map;

import marauroa.common.game.RPSlot;

/**
 * Implementation of container items, such as bags and key rings.
 */
public class Container extends Item {
	private static final String DEFAULT_SLOT_NAME = "content";
	
	/**
	 * Creates a new Container.
	 *
	 * @param name
	 *            name of container
	 * @param clazz
	 *            class (or type) of item
	 * @param subclass
	 *            subclass of this item
	 * @param attributes
	 *            attributes (like container slot name). may be empty or <code>null</code>
	 */
	public Container(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		String slotName = get("slot_name");
		if (slotName == null) {
			slotName = DEFAULT_SLOT_NAME;
		}
		
		RPSlot slot = new ContainerItemSlot(DEFAULT_SLOT_NAME, slotName);
		addSlot(slot);
	}
}
