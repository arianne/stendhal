package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Poisoner implements Feeder {

	public boolean feed(final ConsumableItem item, final Player player) {

		return player.poison((ConsumableItem) item.splitOff(1));

	}

}
