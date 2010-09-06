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
package games.stendhal.server.entity.equip;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPAction;

/**
 * Identifies an item in a slot, in a nested slot or on the ground.
 *
 * @author hendrik
 */
public class ItemIdentificationPath {

	private EntitySlot slot;
	private Item item;
	private int quantity = -1;

	/**
	 * creates a new ItemIdentificationPath
	 *
	 * @param action RPAction which has baseitem, baseitem+baseslot+baseobject or itempath
	 */
	public ItemIdentificationPath(RPAction action) {
		this(action, true);
	}

	/**
	 * creates a new ItemIdentificationPath
	 *
	 * @param source true, if the RPAction is for the base/source, false if it is for the target
	 * @param action RPAction which has baseitem, baseitem+baseslot+baseobject or itempath, if source=true. targetobject+targetslot or x+y if source=false
	 */
	public ItemIdentificationPath(RPAction action, boolean source) {
		// TODO: implement me
	}

	/**
	 * gets the Slot
	 *
	 * @return EntitySlot
	 */
	public EntitySlot getSlot() {
		return slot;
	}

	/**
	 * gets the item
	 *
	 * @return Item, or <code>null</code>, if not specified (target slot)
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * gets the desired quantity
	 *
	 * @return quantity, or <code>-1/<code> if not specified
	 */
	public int getQuantity() {
		return quantity;
	}
}
