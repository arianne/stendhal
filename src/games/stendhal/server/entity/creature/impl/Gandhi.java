package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public class Gandhi implements AttackStrategy {

	public void attack(final Creature creature) {
		// do nothing

	}

	public boolean canAttackNow(final Creature creature) {
		return false;
	}

	public void findNewTarget(final Creature creature) {
		//do nothing
	}

	public void getBetterAttackPosition(final Creature creature) {
		// do nothing
	}

	public boolean hasValidTarget(final Creature creature) {
		return false;
	}

}
