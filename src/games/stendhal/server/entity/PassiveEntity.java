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
		setObstacle(false);
	}

	/**
	 * Create a passive entity.
	 * 
	 * @param object
	 *            The template object.
	 */
	public PassiveEntity(RPObject object) {
		super(object);

		setObstacle(false);
	}
}
