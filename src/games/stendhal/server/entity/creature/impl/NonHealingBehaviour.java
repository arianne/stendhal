package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

final class NonHealingBehaviour implements HealerBehavior {

	
	public void heal(final Creature creature) {
		// does not heal;
	}

	public void init(final String healingProfile) {
		// does not need init
		
	}

}
