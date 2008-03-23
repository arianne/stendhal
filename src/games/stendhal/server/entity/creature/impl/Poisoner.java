package games.stendhal.server.entity.creature.impl;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Poisoner implements Attacker {
	ConsumableItem poison;
	private int probability;

	public Poisoner(int probability, ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}

	public Poisoner() {

	}

	public boolean attack(RPEntity victim) {
		int roll = Rand.roll1D100();
		if (roll <= probability) {
			if (victim instanceof Player) {
				Player player = (Player) victim;
				if (player.poison(new ConsumableItem(poison))) {
					return true;
				}
			}
		}
		return false;
	}
}
