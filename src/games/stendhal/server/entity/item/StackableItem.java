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

import marauroa.common.game.*;
import java.util.Map;

public class StackableItem extends Item implements Stackable {
	private int quantity;

	public StackableItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		update();
	}

	@Override
	public void update() throws AttributeNotFoundException {
		if (has("quantity"))
			quantity = getInt("quantity");
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int amount) {
		quantity = amount;
		put("quantity", quantity);
	}

	public int add(int amount) {
		setQuantity(amount + quantity);
		return quantity;
	}

	public int add(Stackable other) {
		setQuantity(other.getQuantity() + quantity);
		return quantity;
	}

	public boolean isStackable(Stackable other) {
		StackableItem otheri = (StackableItem) other;

		return getItemClass().equals(otheri.getItemClass())
				&& getItemSubclass().equals(otheri.getItemSubclass());
	}
}
