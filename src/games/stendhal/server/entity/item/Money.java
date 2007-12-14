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
	// TODO: I think it's a bug; possibleSlots should be set even when this
	// constructor is used.
	public Money(Map<String, String> attributes) {
		super("money", "money", "gold", attributes);
	}

	public Money(int quantity) {
		super("money", "money", "gold", null);
		setQuantity(quantity);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public Money(Money item) {
		super(item);
	}

	@Override
	public boolean isStackable(Stackable other) {
		return (other.getClass() == Money.class);
	}

	@Override
	public String describe() {
		return "You see " + getQuantity() + " money.";
	}
}
