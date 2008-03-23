package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

class Healer extends Healingbehaviour {

	private int amount;
	private int frequency;

	public Healer(String healingProfile) {
		String[] healingAttributes = healingProfile.split(",");
		amount = Integer.parseInt(healingAttributes[0]);
		frequency = Integer.parseInt(healingAttributes[1]);
	}

	@Override
	public void heal(Creature creature) {
		creature.healSelf(amount, frequency);
	}

}
