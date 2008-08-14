package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Poisoner implements Attacker {
	ConsumableItem poison;
	private int probability;

	public Poisoner(final int probability, final ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}

	public Poisoner() {
		// standard constructor
	}

	public boolean attack(final RPEntity victim) {
		final int roll = Rand.roll1D100();
		if (roll <= probability) {
			if (victim instanceof Player) {
				final Player player = (Player) victim;
				if (player.poison(new ConsumableItem(poison))) {
					return true;
				}
			}
		}
		return false;
	}
}
