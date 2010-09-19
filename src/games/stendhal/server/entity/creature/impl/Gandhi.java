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
package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public class Gandhi implements AttackStrategy {

	public void attack(final Creature creature) {
		// do nothing

	}

	public boolean canAttackNow(final Creature creature) {
		return false;
	}

	public void findNewTarget(final Creature creature) {
		//do nothing
	}

	public void getBetterAttackPosition(final Creature creature) {
		// do nothing
	}

	public boolean hasValidTarget(final Creature creature) {
		return false;
	}

}
