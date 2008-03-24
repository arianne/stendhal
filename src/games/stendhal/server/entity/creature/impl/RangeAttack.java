package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

public class RangeAttack implements AttackStrategy {

	public void attack(Creature creature) {

		if ((SingletonRepository.getRuleProcessor().getTurn() % 5 == creature.getAttackTurn())) {
			creature.attack();
			creature.tryToPoison();
		}
	}

	public boolean canAttackNow(Creature creature) {
		if (creature.getAttackTarget() != null) {

			return !(creature.squaredDistance(creature.getAttackTarget()) > 50 || creature.getZone().collidesOnLine(creature.getX(),
					creature.getY(), creature.getAttackTarget().getX(), creature.getAttackTarget().getY()));
		} else {
			return false;
		}
	}

	public void findNewTarget(Creature creature) {
		RPEntity enemy = creature.getNearestEnemy(7);
		if (enemy != null) {
			creature.setTarget(enemy);
		}
	}

	public void getBetterAttackPosition(Creature creature) {

		games.stendhal.server.entity.Entity target = creature.getAttackTarget();
		double distance = creature.squaredDistance(target);
		if (distance > 25) {
			creature.setMovement(target, 0, 1, 20.0);
			creature.faceToward(creature.getAttackTarget());
		} else if (distance < 16) {
			//TODO: handle collision
			creature.faceToward(creature.getAttackTarget());
			creature.setDirection(creature.getDirection().oppositeDirection());
			creature.setSpeed(creature.getBaseSpeed());

		} else {
			creature.clearPath();
			creature.stop();
			creature.faceToward(creature.getAttackTarget());
		}
		

	}

	public boolean hasValidTarget(Creature creature) {
		if (!creature.isAttacking()) {
			return false;
		}

		RPEntity victim = creature.getAttackTarget();
		if (victim.isInvisible()) {
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
