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

import games.stendhal.server.core.engine.StendhalRPWorld;

import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class StackableItem extends Item implements Stackable {

	private int quantity = 1;

	private static Logger logger = Logger.getLogger(StackableItem.class);

	public StackableItem(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		update();
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public StackableItem(StackableItem item) {
		super(item);
		this.quantity = item.quantity;
		update();
	}

	@Override
	public void update() {
		super.update();
		if (has("quantity")) {
			quantity = getInt("quantity");
		}
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int amount) {
		if (amount < 0) {
			logger.error("Trying to set invalid quantity: " + amount,
					new Throwable());
			amount = 1;
		}
		quantity = amount;
		put("quantity", quantity);
	}

	public int sub(int amount) {
		setQuantity(quantity - amount);
		return quantity;
	}

	public int add(Stackable other) {
		setQuantity(other.getQuantity() + quantity);
		return quantity;
	}

	public StackableItem splitOff(int amountToSplitOff) {
		if ((quantity <= 0) || (amountToSplitOff <= 0)) {
			return null;
		}

		if (quantity >= amountToSplitOff) {
			StackableItem newItem = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
					getName());

			newItem.setQuantity(amountToSplitOff);

			String[] attributesToCopyOnSplit = new String[] { "infostring",
					"description", "bound", "persistent", "undroppableondeath",
					"amount", "frequency", "regen", "atk", "range" };
			for (String attribute : attributesToCopyOnSplit) {
				if (has(attribute)) {
					newItem.put(attribute, get(attribute));
				}
			}

			sub(amountToSplitOff);

			if (quantity > 0) {
				if (isContained()) {
					// We modify the base container if the object change.
					RPObject base = getContainer();
					while (base.isContained()) {
						base = base.getContainer();
					}
					StendhalRPWorld.get().modify(base);
				} else {
					try {
						notifyWorldAboutChanges();
					} catch (Exception e) {
						logger.warn("isContained() returned false on contained object (bank chest bug): "
								+ e);
					}
				}
			} else {
				/* If quantity=0 then it means that item has to be removed */
				super.removeFromWorld();
			}

			return newItem;
		}
		return null;
	}

	@Override
	public void removeOne() {
		splitOff(1);
	}

	public boolean isStackable(Stackable other) {
		StackableItem otheri = (StackableItem) other;

		if (!getItemClass().equals(otheri.getItemClass())
				|| !getItemSubclass().equals(otheri.getItemSubclass())) {
			return false;
		}

		// TODO: look at InfoStringScroll.java

		String[] importantAttributes = new String[] { "infostring", "bound",
				"persistent", "undroppableondeath", "amount", "frequency",
				"regen", "atk", "range" };
		for (String iAtt : importantAttributes) {
			if (!has(iAtt) && !otheri.has(iAtt)) {
				continue;
			}
			if (has(iAtt) && otheri.has(iAtt)
					&& get(iAtt).equals(otheri.get(iAtt))) {
				continue;
			}
			return false;
		}
		return true;
	}
}
