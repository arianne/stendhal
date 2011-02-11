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

import games.stendhal.server.entity.creature.Creature;

/**
 * a strategy for choosing an attack target and a mean of attack.
 *
 * @author durkham
 */
public interface AttackStrategy {

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

	/**
	 * Find a better position to do the attack, for example melees 
	 * should walk closelyto their target but archers should stay away.
	 * 
	 * @param creature creature doing the attack
	 */
	// TODO: rename void-method not to start with get
	public void getBetterAttackPosition(Creature creature);

	/**
	 * Can the specified creature do an attack now?
	 *
	 * @param creature creature doing the attack
	 * @return true, if it can attack, false otherwise
	 */
	public boolean canAttackNow(Creature creature);

	/**
	 * attacks the target.
	 *
	 * @param creature creature doing the attack
	 */
	public void attack(Creature creature);

	/**
	 * Get the maximum range of the attacking creature.
	 * 
	 * @return maximum range 
	 */
	public int getRange();
}
