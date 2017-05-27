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

package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.PoisonStatus;

/**
 * <p>Represents the behaviour of a NPC who is able to heal a player. This can
 * either be done for free or paid in a lump sum, or for a price depending on
 * level of the player</p>
 *
 * <p>Use SpeakerNPC.addHealer() to assign this behaviour to an NPC.</p>
 */
public class HealerBehaviour extends SellerBehaviour {

	/**
	 * Creates a new HealerBehaviour.
	 *
	 * @param cost
	 *            The lump sum that is required to heal
	 */
	public HealerBehaviour(final int cost) {
		super();
		priceCalculator.addCoveredItem("heal", cost);
	}

	/**
	 * Restores the given player's health to the maximum possible at the
	 * player's current level.
	 *
	 * @param player
	 *            The player who should be healed.
	 */
	public void heal(final Player player) {
		player.heal();
		player.getStatusList().removeAll(PoisonStatus.class);
		SingletonRepository.getRPWorld().modify(player);
	}
}
