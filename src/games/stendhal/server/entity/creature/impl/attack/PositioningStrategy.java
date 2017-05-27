/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import games.stendhal.server.entity.creature.Creature;

interface PositioningStrategy {
	/**
	 * Find a better position to do the attack, for example melees
	 * should walk closelyto their target but archers should stay away.
	 *
	 * @param creature creature doing the attack
	 */
	// TODO: rename void-method not to start with get
	void getBetterAttackPosition(Creature creature);
}
