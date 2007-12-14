package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Stuffer implements Feeder {

	public boolean feed(ConsumableItem item, Player player) {
		player.heal(((ConsumableItem) item.splitOff(1)).getAmount(), true);
		return true;
	}

}
