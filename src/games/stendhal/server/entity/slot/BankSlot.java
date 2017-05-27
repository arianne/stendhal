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
package games.stendhal.server.entity.slot;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;

/**
 * a bank slot.
 *
 * @author hendrik
 */
public class BankSlot extends PlayerSlot {
	private final Banks bank;

	/**
	 * Creates a new keyed slot.
	 *
	 * @param bank
	 *            Bank
	 */
	public BankSlot(final Banks bank) {
		super(bank.getSlotName());
		this.bank = bank;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		// Check if we are next to a chest which acts as an interface
		// to this bank slot
		final List<Entity> accessors = SingletonRepository.getBankAccessorManager().get(bank);
		boolean found = false;
		for (final Entity accessor : accessors) {
			if (entity.nextTo(accessor)) {
				found = true;
				break;
			}
		}

		if (!found) {
			// sorry, we are not near a personal chest
			return false;
		}

		// now check that it is the slot of the right player
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

}
