package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

abstract class Healingbehaviour {
	private static NonHealingBehaviour nb = new NonHealingBehaviour();

	abstract void heal(Creature creature);

	static Healingbehaviour get(String healingProfile) {
		if (healingProfile == null) {
			return nb;
		}
		return new Healer(healingProfile);
	}
}
