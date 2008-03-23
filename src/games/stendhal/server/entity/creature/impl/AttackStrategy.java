package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public interface AttackStrategy {

	boolean hasValidTarget(Creature creature);

	void findNewTarget(Creature creature);

	void getBetterAttackPosition(Creature creature);

	boolean canAttackNow(Creature creature);

	void attack(Creature creature);



}
