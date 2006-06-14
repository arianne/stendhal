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
	public Money(Map<String, String> attributes) {
		super("money", "money", "gold", attributes);
	}

	public Money(int quantity) {
		super("money", "money", "gold", null);
		setQuantity(quantity);
	}

	@Override
	public boolean isStackable(Stackable other) {
		return (other.getClass() == Money.class);
	}
}
