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
package games.stendhal.server.entity.creature.impl.attack;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

class Gandhi implements AttackStrategy {

	@Override
	public void attack(final Creature creature) {
		// do nothing
	}

	@Override
	public boolean canAttackNow(final Creature creature) {
		return false;
	}

	@Override
	public void findNewTarget(final Creature creature) {
		//do nothing
	}

	@Override
	public void getBetterAttackPosition(final Creature creature) {
		// do nothing
	}

	@Override
	public boolean hasValidTarget(final Creature creature) {
		return false;
	}

	@Override
	public int getRange() {
		return 0;
	}

	@Override
	public boolean canAttackNow(Creature attacker, RPEntity target) {
		return false;
	}
}
