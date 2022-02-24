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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.DebugInterface;

public class StackableItem extends Item implements Stackable<StackableItem> {

	private int quantity = 1;
	private int capacity = 1;

	private static Logger logger = Logger.getLogger(StackableItem.class);

	private static final String[] IMPORTANT_ATTRIBUTES = new String[] { "infostring",
			"bound",
			"persistent", "undroppableondeath", "amount", "frequency",
			"regen", "atk", "range" };

	private static final String[] ATTRIBUTES_TO_COPY_ON_SPLIT = initializeAttributeNamesToCopy();

	private static String[] initializeAttributeNamesToCopy() {
		Collection<String> attsToCopy = new LinkedList<String>(Arrays.asList(IMPORTANT_ATTRIBUTES));
		attsToCopy.add("description");
		return attsToCopy.toArray(new String[attsToCopy.size()]);
	}

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
		this.setCapacity(item.getCapacity());
		update();
	}

	@Override
	public void update() {
		super.update();
		if (has("quantity")) {
			setQuantity(getInt("quantity"));
		}
		if (has("max_quantity")) {
			setCapacity(getInt("max_quantity"));
		}
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(final int amount) {
		if (amount <= 0) {
			logger.error("Trying to set invalid quantity: " + amount,
					new Throwable());
			quantity = 1;
		} else {
			quantity = amount;
		}
		put("quantity", getQuantity());
	}

	/**
	 * Reduces Item's amount by amount.
	 *
	 * @param amount of reduction, negative numbers will be ignored.
	 * @return remaining amount
	 */
	public int sub(final int amount) {
		if (amount < 0) {
			return getQuantity();
		}
		quantity = getQuantity() - amount;
		put("quantity", getQuantity());
		return getQuantity();
	}

	@Override
	public int add(final StackableItem other) {
		if (this.isStackable(other)) {
			setQuantity(other.getQuantity() + getQuantity());
			// set flag to false to prevent abuse by adding to a stackable in a corpse
			// leading to a too high number awarded when looting
			this.setFromCorpse(false);
			DebugInterface.get().onRPOBjectInteraction(this, other);
		}
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

			for (final String attribute : ATTRIBUTES_TO_COPY_ON_SPLIT) {
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
		} else {
			return null;
		}
	}

	@Override
	public void removeOne() {
		splitOff(1);
	}

	@Override
	public boolean isStackable(final StackableItem onTop) {
		if (this == onTop) {
			return false;
		}

		if (!getItemClass().equals(onTop.getItemClass())
				|| !getItemSubclass().equals(onTop.getItemSubclass())) {
			return false;
		}


		for (final String iAtt : IMPORTANT_ATTRIBUTES) {
			if (has(iAtt)) {
				if (!onTop.has(iAtt) || !get(iAtt).equals(onTop.get(iAtt))) {
					return false;
				}
			} else if (onTop.has(iAtt)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
