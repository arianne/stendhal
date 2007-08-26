/*
 * @(#) src/games/stendhal/server/entity/WalkBlocker.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.entity.area.AreaEntity;

/**
 * An entity that just acts as an obsticle. This is a temporary workaround to
 * allow items to be placed, but not players/entities, until multi-level
 * collisions can be added.
 */
public class WalkBlocker extends AreaEntity {
	/**
	 * Create a walk blocker.
	 */
	public WalkBlocker() {
		super(1, 1);

		setResistance(0);
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if the other entity is an RPEntity,
	 *		otherwise the default.
	 */
	@Override
	public boolean isObstacle(Entity entity) {
		if (entity instanceof RPEntity) {
			return true;
		}

		return super.isObstacle(entity);
	}
}
