package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public interface Idlebehaviour {
	void startIdleness(Creature creature);
	void perform(Creature creature);
}
