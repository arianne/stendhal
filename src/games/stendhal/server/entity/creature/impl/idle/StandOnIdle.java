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
package games.stendhal.server.entity.creature.impl.idle;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

public class StandOnIdle implements IdleBehaviour {
	@Override
	public void perform(final Creature creature) {
		retreatUnderFire(creature);
	}

	/**
	 * Run away if under ranged fire, and unable to attack back.
	 *
	 * @param creature The creature that should try to retreat.
	 * @return <code>true</code> if trying to escape, <code>false</code> if retreatin is not needed
	 */
	protected boolean retreatUnderFire(final Creature creature) {
		for (RPEntity attacker : creature.getAttackingRPEntities()) {
			if (attacker.canDoRangeAttack(creature, attacker.getMaxRangeForArcher())) {
				retreat(creature, attacker);

				return true;
			}
		}

		creature.setSpeed(0);

		return false;
	}

	/**
	 * Run away from an enemy.
	 *
	 * @param creature The creature that tries to retreat.
	 * @param enemy The enemy to run away from.
	 */
	private void retreat(final Creature creature, final Entity enemy) {
		creature.clearPath();
		creature.faceToward(enemy);
		creature.setDirection(creature.getDirection().oppositeDirection());

		if (creature.getZone().collides(creature, creature.getX() + creature.getDirection().getdx(),
				creature.getY() + creature.getDirection().getdy(), true)) {
			// running against a wall; try turning
			if (Rand.rand(2) == 0) {
				creature.setDirection(creature.getDirection().nextDirection());
			} else {
				creature.setDirection(creature.getDirection().nextDirection().oppositeDirection());
			}
		}

		creature.setSpeed(creature.getBaseSpeed());
		creature.applyMovement();
	}
}
