/***************************************************************************
 *                (C) Copyright 2003-2013 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

/**
 * poison status
 */
public class PoisonStatus extends Status {
	private int amount;
	private int frequency;
	private int regen;
	private int left;

	/**
	 * Poison
	 *
	 * @param amount     total amount
	 * @param frequency  frequency of events
	 * @param regen      hp change on each event
	 */
	public PoisonStatus(int amount, int frequency, int regen) {
		super("poison");
		this.amount = amount;
		this.frequency = frequency;
		this.regen = regen;
		this.left = amount;
	}

	/**
	 * gets the total ammount
	 *
	 * @return amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * gets the frequency of events
	 *
	 * @return frequency
	 */
	public int getFrecuency() {
		return frequency;
	}

	/**
	 * gets the amount of change per event
	 *
	 * @return regen
	 */
	public int getRegen() {
		return regen;
	}

	/**
	 * Consumes a part of this status.
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

	@Override
	public StatusType getStatusType() {
		return StatusType.POISONED;
	}

}
