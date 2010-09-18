package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 * 
 * Abused slots of players which contain one RPObject used as hashmap.
 * 
 * @author hendrik
 */
public class KeyedSlot extends EntitySlot {

	/**
	 * Creates a new keyed slot.
	 * 
	 * @param name
	 *            name of slot
	 */
	public KeyedSlot(final String name) {
		super(name);
	}

	@Override
	public boolean isItemSlot() {
		return false;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		setErrorMessage("This " + getName() + " is not for items.");
		return false;
	}

}
