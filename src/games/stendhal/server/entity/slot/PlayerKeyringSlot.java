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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.SlotOwner;

/**
 * Keyring slots of players which contain items.
 *
 * @author hendrik
 */
public class PlayerKeyringSlot extends PlayerSlot {

	/**
	 * Creates a new PlayerSlot.
	 *
	 * @param player player
	 */
	public PlayerKeyringSlot(final String player) {
		super(player);
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!mayAccessKeyRing(entity)) {
			setErrorMessage("Your keyring is broken. You should look for someone who can fix it.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		if (!mayAccessKeyRing(entity)) {
			setErrorMessage("Your keyring is broken. You should look for someone who can fix it.");
			return false;
		}
		return super.isReachableForThrowingThingsIntoBy(entity);
	}

	/**
	 * checks whether the entity may access the key ring
	 *
	 * @param entity Entity
	 * @return true, if the keyring may be accessed, false otherwise
	 */
	private boolean mayAccessKeyRing(Entity entity) {
		if (!(entity instanceof Player)) {
			return false;
		}
		Player player = (Player) entity;
		return (player.getFeature("keyring") != null);
	}

	@Override
	public boolean isFull() {
		final SlotOwner owner = getOwner();
		if (!(owner instanceof Player)) {
			return super.isFull();
		}

		int maxSize = ((Player) owner).getMaxSlotSize("keyring");
		return size() >= maxSize;
	}
}
