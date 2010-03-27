package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

public class RangeAttack implements AttackStrategy {

	public void attack(final Creature creature) {

		if ((SingletonRepository.getRuleProcessor().getTurn() % 5 == creature.getAttackTurn())) {
			creature.attack();
			creature.tryToPoison();
		}
	}

	public boolean canAttackNow(final Creature creature) {
		if (creature.getAttackTarget() != null) {

			return !((creature.squaredDistance(creature.getAttackTarget()) > 50) || creature.getZone().collidesOnLine(creature.getX(),
					creature.getY(), creature.getAttackTarget().getX(), creature.getAttackTarget().getY()));
		} else {
			return false;
		}
	}

	public void findNewTarget(final Creature creature) {
		final RPEntity enemy = creature.getNearestEnemy(7);
		if (enemy != null) {
			creature.setTarget(enemy);
		}
	}

	public void getBetterAttackPosition(final Creature creature) {

		final games.stendhal.server.entity.Entity target = creature.getAttackTarget();
		final double distance = creature.squaredDistance(target);
		if (distance > 25) {
			creature.setMovement(target, 0, 1, 20.0);
			creature.faceToward(creature.getAttackTarget());
		} else if (distance < 16) {
			creature.faceToward(creature.getAttackTarget());
			creature.setDirection(creature.getDirection().oppositeDirection());
			if (creature.getZone().collides(creature, creature.getX() + creature.getDirection().getdx(), creature.getY() + creature.getDirection().getdy(), true)) {
				if (!canAttackNow(creature)) {
					creature.setMovement(target, 0, 1, 15.0);
					creature.faceToward(creature.getAttackTarget());
				} else {
					creature.faceToward(creature.getAttackTarget());
					creature.setSpeed(0);
				}
			} else {
				creature.setSpeed(creature.getBaseSpeed());
			}
			
		} else {
			if (!canAttackNow(creature)) {
				creature.setMovement(target, 0, 1, 20.0);
				creature.faceToward(creature.getAttackTarget());
			} else {
				creature.clearPath();
				creature.stop();
				creature.faceToward(creature.getAttackTarget());
			}

		}
	}

	public boolean hasValidTarget(final Creature creature) {
		if (!creature.isAttacking()) {
			return false;
		}

		final RPEntity victim = creature.getAttackTarget();
		if (victim.isInvisibleToCreatures()) {
			return false;
		}
		if (!victim.getZone().equals(creature.getZone())) {
			return false;
		}

		if (!creature.getZone().has(victim.getID())) {
			return false;
		}
		return creature.squaredDistance(victim) < 144;
	}

}
