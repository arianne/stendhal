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

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.Entity;

public class RangeAttack implements AttackStrategy {
	/** Maximum range at which the archer will consider a target valid, squared. */
	private static final int MAX_RANGE_SQUARED = 144;
	/** Archer range, if not specified otherwise. */
	private static final int DEFAULT_RANGE = 7;
	private final int range;
	
	RangeAttack(String range) {
		this.range = MathHelper.parseIntDefault(range, DEFAULT_RANGE);
	}

	public void attack(final Creature creature) {

		if ((SingletonRepository.getRuleProcessor().getTurn() % 5 == creature.getAttackTurn())) {
			creature.attack();
			creature.tryToPoison();
		}
	}

	public boolean canAttackNow(final Creature creature) {
		if (creature.getAttackTarget() != null) {

			return !((creature.squaredDistance(creature.getAttackTarget()) > range * range) || creature.getZone().collidesOnLine(creature.getX(),
					creature.getY(), creature.getAttackTarget().getX(), creature.getAttackTarget().getY()));
		} else {
			return false;
		}
	}

	public void findNewTarget(final Creature creature) {
		final RPEntity enemy = creature.getNearestEnemy(creature.getPerceptionRange()+2);
		if (enemy != null) {
			creature.setTarget(enemy);
		}
	}

	public void getBetterAttackPosition(final Creature creature) {
		final Entity target = creature.getAttackTarget();
		final double distance = creature.squaredDistance(target);
		// if too far away from enemy
		if (distance > longRangeSquared()) {
			creature.setMovement(target, 0, 1, creature.getMovementRange());
			creature.faceToward(creature.getAttackTarget());
		// if too close to enemy
		} else if (distance < shortRangeSquared()) {
			// turn creature around
			creature.faceToward(creature.getAttackTarget());
			creature.setDirection(creature.getDirection().oppositeDirection());
			// check if creature cant move away from enemy
			if (creature.getZone().collides(creature, creature.getX() + creature.getDirection().getdx(), creature.getY() + creature.getDirection().getdy(), true)) {
				if (!canAttackNow(creature)) {
					// go back to enemy
					creature.setMovement(target, 0, 1, creature.getMovementRange()*0.75);
					creature.faceToward(creature.getAttackTarget());
				} else {
					// will attack from here
					creature.clearPath();
					creature.stop();
					creature.faceToward(creature.getAttackTarget());
				}
			} else {
				// give to creature good kick
				creature.setSpeed(creature.getBaseSpeed());
			}
		// good distance to attack 
		} else {
			// cant attack enemy, going to him
			if (!canAttackNow(creature)) {
				creature.setMovement(target, 0, 1, creature.getMovementRange());
				creature.faceToward(creature.getAttackTarget());
			} else {
				// this is good position to attack enemy
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
		return creature.squaredDistance(victim) < MAX_RANGE_SQUARED;
	}

	/**
	 * Get the shortest range that is considered optimal.
	 * 
	 * @return shortest optimal range
	 */
	private int shortRangeSquared() {
		int tmp = range / 2 + (range % 2);
		
		return tmp * tmp;
	}
	
	/**
	 * Get the longest range that is considered optimal.
	 * 
	 * @return longest optimal range
	 */
	private int longRangeSquared() {
		int tmp = range / 2 + (range % 2) + 1;
		
		return tmp * tmp;
	}
	
	public int getRange() {
		return range;
	}
}
