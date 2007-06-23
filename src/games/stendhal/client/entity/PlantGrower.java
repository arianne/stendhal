package games.stendhal.client.entity;

public class PlantGrower extends Entity {
	//
	// Entity
	//

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> the entity can not enter this
	 *		entity's area.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		return false;
	}
}
