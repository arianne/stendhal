package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public class Gandhi implements AttackStrategy {

	public void attack(Creature creature) {
		// do nothing

	}

	public boolean canAttackNow(Creature creature) {
		return false;
	}

	public void findNewTarget(Creature creature) {
		//do nothing
	}

	public void getBetterAttackPosition(Creature creature) {
		// do nothing
	}

	public boolean hasValidTarget(Creature creature) {
		return false;
	}

}
