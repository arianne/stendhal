package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;

class Healer implements HealerBehavior {

	private int amount;
	private int frequency;

	public Healer(String healingProfile) {
		init(healingProfile);
	}

	public void init(String healingProfile) {
		String[] healingAttributes = healingProfile.split(",");
		amount = Integer.parseInt(healingAttributes[0]);
		frequency = Integer.parseInt(healingAttributes[1]);
	}

	public void heal(Creature creature) {
		if ((SingletonRepository.getRuleProcessor().getTurn() % frequency == 0)
				&& (creature.getHP() > 0)) {
			creature.heal(amount);
		}
		
	}

}
