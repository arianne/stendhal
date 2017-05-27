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
 * A positioning strategy that prefers getting in to melee, but allows attacking
 * from distance.
 */
class DualAttackPositioningStrategy implements PositioningStrategy {
	@Override
	public void getBetterAttackPosition(Creature creature) {
		AttackStrategy strategy = creature.getAttackStrategy();

		if (creature.hasTargetMoved()) {
			creature.setMovement(creature.getAttackTarget(), 0, 1, creature.getMovementRange());
		}

		if (!strategy.canAttackNow(creature)) {
			// First try switching targets, in case the new one is attackable
			strategy.findNewTarget(creature);
			creature.setMovement(creature.getAttackTarget(), 0, 1, creature.getMovementRange());
			if (!creature.hasPath() && !strategy.canAttackNow(creature)) {
				// Fall back to idling. The possible target is unreachable
				creature.stopAttack();
				return;
			}
		} else if (!creature.hasPath()) {
			/*
			 * Stay at position where attacking is possible. Keeping moving
			 * could take the creature to a place that has blocked view to the
			 * target.
			 */
			creature.stop();
		}
		creature.faceToward(creature.getAttackTarget());
	}
}
