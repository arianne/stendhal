/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.slot.ContainerItemSlot;
import marauroa.common.game.RPSlot;

/**
 * Implementation of container items, such as bags and key rings.
 */
public class Container extends Item {
	/** Default name of the container slot. */
	private static final String DEFAULT_SLOT_NAME = "content";
	/** Default size of the container slot. */
	private static final int DEFAULT_SLOT_SIZE = 8;

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
		/*
		 * Note! must be after addSlot(), because addSlot() overwrites the slot
		 * capacity from the value in RPClass, which would result in infinitely
		 * large containers.
		 */
		determineSlotCapacity(slot);
	}

	/**
	 * Copy constructor. Needed by the bank chests.
	 *
	 * @param item copied container
	 */
	public Container(Container item) {
		super(item);
		RPSlot slot = getSlot(DEFAULT_SLOT_NAME);
		determineSlotCapacity(slot);
	}

	/**
	 * Determine the correct size of the container slot, instead of the infinite
	 * that is defined in RPClass. Defaults to DEFAULT_SLOT_SIZE unless the
	 * object has attribute slot_size.
	 *
	 * @param slot
	 */
	private void determineSlotCapacity(RPSlot slot) {
		String slotSize = get("slot_size");
		int size = DEFAULT_SLOT_SIZE;
		if (slotSize == null) {
			put("slot_size", DEFAULT_SLOT_SIZE);
		} else {
			size = MathHelper.parseIntDefault(slotSize, DEFAULT_SLOT_SIZE);
		}
		slot.setCapacity(size);
	}
}
