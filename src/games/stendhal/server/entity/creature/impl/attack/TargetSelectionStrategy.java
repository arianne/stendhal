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

/**
 * Strategy for choosing from the available targets, or switching targets.
 */
interface TargetSelectionStrategy {
	/**
	 * Does the creature have a valid target at the moment?
	 *
	 * @param creature Creature which is attacking
	 * @return true, if it has a valid target; false otherwise
	 */
	public boolean hasValidTarget(Creature creature);

	/**
	 * find a new target.
	 *
	 * @param creature creature doing the attack
	 */
	public void findNewTarget(Creature creature);
}
