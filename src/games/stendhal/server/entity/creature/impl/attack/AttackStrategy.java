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

/**
 * a strategy for choosing an attack target and a mean of attack.
 *
 * @author durkham
 */
public interface AttackStrategy extends PositioningStrategy, TargetSelectionStrategy {
	/**
	 * Can the specified creature do an attack now?
	 *
	 * @param creature creature doing the attack
	 * @return true, if it can attack, false otherwise
	 */
	public boolean canAttackNow(Creature creature);

	/**
	 * Can the specified creature do an attack against a specified target?
	 *
	 * @param attacker creature doing the attack
	 * @param target potential target
	 *
	 * @return true, if it can attack, false otherwise
	 */
	boolean canAttackNow(Creature attacker, RPEntity target);

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
