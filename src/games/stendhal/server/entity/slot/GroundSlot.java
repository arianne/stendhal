package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;

/**
 * A pseudo slot representing the ground
 * (so that the ground has not to be treated special in equipment code).
 *
 * @author brummermann
 */
public class GroundSlot implements Slot {
	private Item item;

	/**
	 * generates a new GroundSlot
	 *
	 * @param item item
	 */
	public GroundSlot(Item item) {
		this.item = item;
	}

	public boolean isItemSlot() {
		return true;
	}

	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		// Is this a target slot?
		if (isDestinationSlot()) {
			return false;
		}

		// TODO: Check distance
		return false;
	}

	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		// Is this a source slot?
		if (isSourceSlot()) {
			return false;
		}

		// TODO: Check distance
		return false;
	}

	private boolean isSourceSlot() {
		return item != null;
	}

	private boolean isDestinationSlot() {
		return item == null;
	}
}
