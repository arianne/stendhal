package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Eater implements Feeder {

	public boolean feed(final ConsumableItem item, final Player player) {
		if (player.isFull()) {
			player.sendPrivateText("You can't consume anymore");
			return false;
		} else {
			player.eat((ConsumableItem) item.splitOff(1));
			return true;
		}
	}

}
