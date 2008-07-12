package games.stendhal.server.entity;

import marauroa.common.game.RPObject;

/**
 * An entity that doesn't move on it's own, but can be moved.
 */
public abstract class PassiveEntity extends Entity {
	/**
	 * Create a passive entity.
	 */
	public PassiveEntity() {
		setResistance(0);
	}

	/**
	 * Create a passive entity.
	 * 
	 * @param object
	 *            The template object.
	 */
	public PassiveEntity(final RPObject object) {
		super(object);

		setResistance(0);
	}
}
