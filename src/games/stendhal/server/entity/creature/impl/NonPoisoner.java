package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.RPEntity;

class NonPoisoner implements Attacker {

	public NonPoisoner() {
		super();
	}

	public boolean attack(final RPEntity victim) {
		return false;
	}

}
