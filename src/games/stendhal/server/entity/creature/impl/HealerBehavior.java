package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

public interface HealerBehavior {
	void init(String healingProfile);
	void heal(Creature creature);

}
