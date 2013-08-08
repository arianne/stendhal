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
package games.stendhal.server.entity.status;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class PoisonAttacker implements StatusAttacker {
	ConsumableItem poison;
	private int probability;

	public PoisonAttacker(final int probability, final ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}

	public PoisonAttacker() {
		// standard constructor
	}

	@Override
	public boolean attack(final RPEntity victim) {
		final int roll = Rand.roll1D100();
		if (roll <= probability) {
			if (victim instanceof Player) {
				final Player player = (Player) victim;
				if (player.poison(new ConsumableItem(poison))) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void applyAntistatus(double antipoison) {
		/*
		 * invert the value for multiplying
		 */
		antipoison = (1 - antipoison);
		this.probability *= antipoison;
	}
	
	@Override
	public int getProbability() {
		return this.probability;
	}
	
	@Override
	public void setProbability(int p) {
		this.probability = p;
	}
}
