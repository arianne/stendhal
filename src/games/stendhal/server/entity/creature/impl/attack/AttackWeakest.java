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

import java.util.List;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;

/**
 * A profile for creature that always tries to kill the weakest enemy first.
 */
public class AttackWeakest extends HandToHand {
	/**
	 * Check if the target is something worth attacking.
	 * (basically a Player or Pet, to be maximally annoying)
	 *
	 * @param target the target to be checked
	 * @return <code>true</code> iff the target is a good candidate to be killed
	 */
	private boolean isPreferredTarget(RPEntity target) {
		/*
		 * A bit of a hack - ignore scroll creatures so that players can not
		 * confuse creatures with summon scrolls.
		 */
		return !(target instanceof AttackableCreature);
	}
	/**
	 * Attack the weakest enemy next to the creature.
	 *
	 * @param creature
	 *            the creature checking for the optimal target
	 * @return <code>true</code> iff a good target was found. That includes
	 *         keeping the current target if that is the optimal one
	 */
	private boolean attackWeakest(Creature creature) {
		// create list of possible enemies
		final List<RPEntity> enemyList = creature.getEnemyList();
		if (enemyList.isEmpty()) {
			return false;
		}

		RPEntity target = null;

		if (creature.isAttacking() && isPreferredTarget(creature.getAttackTarget())) {
			target = creature.getAttackTarget();
		}

		int level;
		if (target != null) {
			level = target.getLevel();
		} else {
			// just something above anything
			level = 1000;
		}

		for (final RPEntity enemy : enemyList) {
			if (!isPreferredTarget(enemy)) {
				continue;
			}

			if (creature.getAttackStrategy().canAttackNow(creature, enemy)
					&& !enemy.isInvisibleToCreatures()) {
				/*
				 * Use level as an approximation of the strength. Prefer keeping
				 * the current target if the enemies are equally strong.
				 */
				if (enemy.getLevel() < level) {
					target = enemy;
					level = enemy.getLevel();
				}
			}
		}

		if (target != null) {
			if (target != creature.getAttackTarget()) {
				creature.setTarget(target);
			}
			return true;
		}

		return false;
	}

	@Override
	public void findNewTarget(final Creature creature) {
		if (!attackWeakest(creature)) {
			// Fall back to the default behavior
			super.findNewTarget(creature);
		}
	}

	@Override
	public boolean hasValidTarget(final Creature creature) {
		// Change target if there's a weaker creature around
		attackWeakest(creature);
		return super.hasValidTarget(creature);
	}
}
