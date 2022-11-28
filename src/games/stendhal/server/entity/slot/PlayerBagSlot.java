/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.SlotOwner;


public class PlayerBagSlot extends PlayerSlot {

	public PlayerBagSlot(final String player) {
		super(player);
	}

	@Override
	public boolean isFull() {
		final SlotOwner owner = getOwner();
		if (!(owner instanceof Player)) {
			return super.isFull();
		}

		final Player player = (Player) owner;
		int maxSize;
		if (!player.hasFeature("bag")) {
			// default bag size
			maxSize = 12;
		} else {
			maxSize = player.getMaxSlotSize("bag");
		}
		return size() >= maxSize;
	}
}
