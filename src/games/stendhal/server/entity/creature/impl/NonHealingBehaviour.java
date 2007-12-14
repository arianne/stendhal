package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

final class NonHealingBehaviour extends Healingbehaviour {

	@Override
	public void heal(Creature creature) {
		// does not heal;
	}

}
