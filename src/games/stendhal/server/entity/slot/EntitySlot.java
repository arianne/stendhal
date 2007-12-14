package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPSlot;

/**
 * Stendhal specific information about this slot
 * 
 * @author hendrik
 */
public class EntitySlot extends RPSlot implements Slot {

	/**
	 * creates an uninitialized EntitySlot
	 * 
	 */
	public EntitySlot() {
		super();
	}

	/**
	 * Creates a new EntitySlot
	 * 
	 * @param name
	 *            name of slot
	 */
	public EntitySlot(String name) {
		super(name);
	}

	public boolean isReachableForTakingThingsOutOfBy(
			@SuppressWarnings("unused")
			Entity entity) {
		return false;
	}

	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		return isReachableForTakingThingsOutOfBy(entity);
	}

	public boolean isItemSlot() {
		return true;
	}
}
