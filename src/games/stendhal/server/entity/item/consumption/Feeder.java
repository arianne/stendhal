package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

public interface Feeder {

	/**
	 * 
	 * @param item
	 * @param player
	 * @return true iff feeding is possible
	 */
	boolean feed(ConsumableItem item, Player player);

}
