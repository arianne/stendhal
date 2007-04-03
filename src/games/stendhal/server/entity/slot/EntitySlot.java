package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPSlot;

/**
 * Stendhal specific information about this slot
 *
 * @author hendrik 
 */
public class EntitySlot extends RPSlot {

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
	 * @param name name of slot
	 */
	public EntitySlot(String name) {
		super(name);
	}

	/**
	 * Is this slot reachable?
	 *
	 * @param entity Entity which may be able to reach this slot
	 * @return true, if it is reachable, false otherwise
	 */
	public boolean isReachableBy(@SuppressWarnings("unused") Entity entity) {
		return false;
	}

	/**
	 * Can this slot contain items?
	 *
	 * @return true, if it can contains items, false otherwise 
	 */
	public boolean isItemSlot() {
		return true;
	}
}
