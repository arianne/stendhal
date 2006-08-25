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

import games.stendhal.server.StendhalRPWorld;

import java.util.Map;

public class StackableItem extends Item implements Stackable {
	private int quantity = 1;

	public StackableItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		update();
	}

	@Override
	public void update() throws AttributeNotFoundException {
		super.update();
		if (has("quantity")) {
			quantity = getInt("quantity");
		}
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

	@Override
	public void removeOne() {
		if (quantity > 1) {
			add(-1);
			if (isContained()) {
				// We modify the base container if the object change.
				RPObject base = getContainer();

				while (base.isContained()) {
					base = base.getContainer();
				}

				StendhalRPWorld.get().modify(base);
			} else {
				notifyWorldAboutChanges();
			}
		} else {
			/* If quantity=1 then it means that item has to be removed */
			super.removeOne();
		}

	}
}
