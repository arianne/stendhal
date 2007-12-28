package games.stendhal.server.entity.slot;

import marauroa.common.game.RPSlot;
import games.stendhal.server.entity.Entity;

/**
 * A slot which can contain items
 * 
 * @author hendrik
 */
public interface Slot {

	/**
	 * Is this slot reachable to take things out of?
	 * 
	 * @param entity
	 *            Entity which may be able to reach this slot
	 * @return true, if it is reachable, false otherwise
	 */
	boolean isReachableForTakingThingsOutOfBy(Entity entity);

	/**
	 * Is this slot reachable to put things into?
	 * 
	 * @param entity
	 *            Entity which may be able to reach this slot
	 * @return true, if it is reachable, false otherwise
	 */
	boolean isReachableForThrowingThingsIntoBy(Entity entity);

	/**
	 * Can this slot contain items?
	 * 
	 * @return true, if it can contains items, false otherwise
	 */
	boolean isItemSlot();

	/**
	 * returns the slot which must be used for write access
	 *
	 * @return RPSlot which should be used
	 */
	public RPSlot getWriteableSlot();
}
