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

import games.stendhal.server.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * 
 */

/*
 * TODO: bug: calling consume() on a stack of ConsumableItems uses up all
 * items in the stack, not only a single one.
 * 
 * Quote from Player.java:
 * 
 * NOTE: We have a bug when consuming a stackableItem as when the first
 * item runs out the other ones also runs out. Perhaps this must be
 * fixed inside StackableItem itself
 */
public class ConsumableItem extends StackableItem implements UseListener {

	/** How much of this item has not yet been consumed. */
	private int left;

	public ConsumableItem(String name, String clazz, String subclass, Map<String, String> attributes) {

		super(name, clazz, subclass, attributes);

		left = getAmount();
	}

	public int getAmount() {
		return getInt("amount");
	}

	public int getFrecuency() {
		return getInt("frequency");
	}

	public int getRegen() {
		return getInt("regen");
	}

	/**
	 * Consumes a part of this item.
	 * @return The amount that has been consumed
	 */
	public int consume() {
		// note that amount and regen are negative for poison
		int consumedAmount;
		if (Math.abs(left) < Math.abs(getRegen())) {
			consumedAmount = left;
			left = 0;
		} else {
			consumedAmount = getRegen();
			left -= getRegen();
		}
		return consumedAmount;
	}

	/**
	 * Checks whether this item has already been fully consumed.
	 * @return true iff this item has been consumed
	 */
	public boolean consumed() {
		return left == 0;
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;
		player.consumeItem(this);
		player.notifyWorldAboutChanges();
	}
}