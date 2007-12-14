package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;

/**
 * A pseudo slot representing the ground as source for an item (so that the
 * ground has not to be treated special in equipment code).
 * 
 * @author hendrik
 */
public class GroundSourceSlot implements Slot {
	private Item item;

	/**
	 * generates a new GroundSlot
	 * 
	 * @param item
	 *            item
	 */
	public GroundSourceSlot(Item item) {
		this.item = item;
	}

	public boolean isItemSlot() {
		return true;
	}

	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		return item.nextTo(entity);
	}

	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {

		// this is a source slot
		return false;
	}
}
