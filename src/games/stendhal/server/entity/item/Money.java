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
package games.stendhal.server.entity.item;

import java.util.Map;

public class Money extends StackableItem {

	// WARNING: Don't use this constructur! (Unless you know what you're
	// doing...)
	// If you use it e.g. in a quest, the variable possibleSlots will not
	// be set, and the server will crash with a NullPointerException
	// when RPEntity.equip() is called with your Money object.
	// Instead, you can use this:
	// StackableItem money = (StackableItem)
	// world.getRuleManager().getEntityManager().getItem("money");
	// where world is a StendhalRPWorld.
	public Money(final Map<String, String> attributes) {
		super("money", "money", "gold", attributes);
	}

	public Money(final int quantity) {
		super("money", "money", "gold", null);
		setQuantity(quantity);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public Money(final Money item) {
		super(item);
	}

	@Override
	public String describe() {
		return "You see " + getQuantity() + " §money.";
	}
}
