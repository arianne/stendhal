package games.stendhal.server.entity;



import marauroa.common.game.RPObject;

public abstract class PassiveEntity extends Entity {

	public PassiveEntity() {
		super();
	}

	public PassiveEntity(RPObject object) {
		super(object);
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>false</code>.
	 */
	@Override
	public boolean isObstacle(Entity entity) {
		return false;
	}
}
