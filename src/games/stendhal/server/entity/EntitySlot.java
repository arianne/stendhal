package games.stendhal.server.entity;

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

}
