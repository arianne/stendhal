package games.stendhal.server.entity.creature.impl;

import java.util.List;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

/**
 * A profile for creature that always tries to kill the weakest enemy first.
 */
public class AttackWeakest extends HandToHand {
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
		if (creature.isAttacking()) {
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
			/*
			 * A bit of a hack - ignore creatures so that players can not
			 * confuse creatures with summon scrolls.
			 */
			if (enemy instanceof Creature) {
				continue;
			}

			if (creature.nextTo(enemy) && !enemy.isInvisibleToCreatures()) {
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
