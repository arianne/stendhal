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

import games.stendhal.server.core.engine.SingletonRepository;
import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class StackableItem extends Item implements Stackable {

	private int quantity = 1;

	private static Logger logger = Logger.getLogger(StackableItem.class);

	public StackableItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		update();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public StackableItem(final StackableItem item) {
		super(item);
		this.setQuantity(item.getQuantity());
		update();
	}

	@Override
	public void update() {
		super.update();
		if (has("quantity")) {
			setQuantity(getInt("quantity"));
		}
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int amount) {
		if (amount <= 0) {
			logger.error("Trying to set invalid quantity: " + amount,
					new Throwable());
			amount = 1;
		}
		quantity = amount;
		put("quantity", getQuantity());
	}

	public int sub(final int amount) {
		quantity = getQuantity() - amount;
		return getQuantity();
	}

	public int add(final Stackable other) {
		setQuantity(other.getQuantity() + getQuantity());
		return getQuantity();
	}

	public StackableItem splitOff(final int amountToSplitOff) {
		if ((getQuantity() <= 0) || (amountToSplitOff <= 0)) {
			return null;
		}

		if (getQuantity() >= amountToSplitOff) {
			final StackableItem newItem = (StackableItem) SingletonRepository.getEntityManager().getItem(
					getName());

			newItem.setQuantity(amountToSplitOff);

			final String[] attributesToCopyOnSplit = new String[] { "infostring",
					"description", "bound", "persistent", "undroppableondeath",
					"amount", "frequency", "regen", "atk", "range" };
			for (final String attribute : attributesToCopyOnSplit) {
				if (has(attribute)) {
					newItem.put(attribute, get(attribute));
				}
			}
			
			sub(amountToSplitOff);

			if (getQuantity() > 0) {
				if (isContained()) {
					// We modify the base container if the object change.
					RPObject base = getContainer();
					while (base.isContained()) {
						base = base.getContainer();
					}
					SingletonRepository.getRPWorld().modify(base);
				} else {
					try {
						notifyWorldAboutChanges();
					} catch (final Exception e) {
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

	public boolean isStackable(final Stackable other) {
		final StackableItem otheri = (StackableItem) other;

		if (!getItemClass().equals(otheri.getItemClass())
				|| !getItemSubclass().equals(otheri.getItemSubclass())) {
			return false;
		}

		final String[] importantAttributes = new String[] { "infostring", "bound",
				"persistent", "undroppableondeath", "amount", "frequency",
				"regen", "atk", "range" };
		for (final String iAtt : importantAttributes) {
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
