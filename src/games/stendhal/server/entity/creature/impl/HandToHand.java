package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;

class HandToHand implements AttackStrategy {

	private static final int followRadius = 144;

	public void attack(Creature creature) {

		if ((SingletonRepository.getRuleProcessor().getTurn() % 5 == creature.getAttackTurn())) {
			StendhalRPAction.attack(creature, creature.getAttackTarget());
			creature.tryToPoison();
		}
	}

	public boolean canAttackNow(Creature creature) {
		if (creature.getAttackTarget() != null) {
			
			return creature.nextTo(creature.getAttackTarget());
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
		if (creature.hasTargetMoved()) {
			creature.setMovement(target, 0, 1, 20.0);
		}
		if (!creature.hasPath()) {
			if (!creature.nextTo(target)) {
				creature.stopAttack();
				return;
			}
		}
		creature.faceToward(creature.getAttackTarget());
		

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
		return creature.squaredDistance(victim) < followRadius;
	}

}
