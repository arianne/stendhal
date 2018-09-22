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
package games.stendhal.server.entity.item;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.consumption.Feeder;
import games.stendhal.server.entity.item.consumption.FeederFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.RPObject;

/**
 * Represents everything that can be consumed by RPentity. Including food,
 * poison, antidote, ...
 *
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class ConsumableItem extends StackableItem implements Comparable<ConsumableItem> {

	private final static Logger logger = Logger.getLogger(ConsumableItem.class);

	/** How much of this item has not yet been consumed. */
	private int left;
	protected final Feeder feeder;

	@Override
	public void put(final String attribute, final double value) {
		super.put(attribute, value);
		checkAmount(attribute, value);
	}

	private void checkAmount(final String attribute, final double value) {
		if ("amount".equals(attribute)) {
			left = (int) value;
		}
	}

	@Override
	public void put(final String attribute, final int value) {
		super.put(attribute, value);
		checkAmount(attribute, value);
	}

	@Override
	public void put(final String attribute, final String value) {
		if ("amount".equals(attribute)) {
			left = Integer.parseInt(value);
		}
		super.put(attribute, value);
	}

	public ConsumableItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		left = getAmount();
		feeder = FeederFactory.get(this);
	}

	/**
	 * copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public ConsumableItem(final ConsumableItem item) {
		super(item);
		this.left = item.left;
		this.feeder = item.feeder;
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
	 *
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
	 *
	 * @return true iff this item has been consumed
	 */
	public boolean consumed() {
		return left == 0;
	}

	/**
	 * Verifies item is near to player. if so splits one single item of and
	 * calls consumeItem of the player.
	 * @param user the eating player
	 * @return true if consumption can be started
	 */
	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			final Player player = (Player) user;

			if (isContained()) {
				// We modify the base container if the object change.
				RPObject base = getContainer();

				while (base.isContained()) {
					base = base.getContainer();
				}

				if (!user.nextTo((Entity) base)) {
					user.sendPrivateText("The consumable item is too far away");
					return false;
				}
			} else {
				if (!nextTo(user)) {
					user.sendPrivateText("The consumable item is too far away");
					return false;
				}
			}
			feeder.feed(this, player);
			player.notifyWorldAboutChanges();
			return true;
		} else {
			logger.error("user is no instance of Player but: " + user, new Throwable());
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final ConsumableItem other) {

		final float result = (float) other.getRegen() / (float) other.getFrecuency()
				- (float) getRegen() / (float) getFrecuency();
		return (int) Math.signum(result);
	}

	/*
	 * Sub-classes that use immunizations should override this.
	 */
	public Set<StatusType> getImmunizations() {
		return Collections.emptySet();
	}
}
