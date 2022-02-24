/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
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

/**
 * Money Pouch
 *
 * @author Antum Deluge
 */
public class PlayerMoneyPouchSlot extends PlayerSlot {

	/**
	 * Creates a new PlayerSlot.
	 *
	 * @param player player
	 */
	public PlayerMoneyPouchSlot(final String player) {
		super(player);
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!mayAccessPouch(entity)) {
			setErrorMessage("You don't own a money pouch. You should look for someone who can create one for you.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		if (!mayAccessPouch(entity)) {
			setErrorMessage("You don't own a money pouch. You should look for someone who can create one for you.");
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
	private boolean mayAccessPouch(Entity entity) {
		if (!(entity instanceof Player)) {
			return false;
		}
		Player player = (Player) entity;
		return (player.getFeature("pouch") != null);
	}
}
