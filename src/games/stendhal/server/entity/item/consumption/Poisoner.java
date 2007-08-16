package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Poisoner implements Feeder {

	public boolean feed(ConsumableItem item, Player player) {

		player.poison((ConsumableItem) item.splitOff(1));
		return true;
	}

}
