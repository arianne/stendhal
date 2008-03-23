package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public abstract class Healingbehaviour {
	private static NonHealingBehaviour nb = new NonHealingBehaviour();

	public abstract void heal(Creature creature);

	public static Healingbehaviour get(String healingProfile) {
		if (healingProfile == null) {
			return nb;
		}
		return new Healer(healingProfile);
	}
}
