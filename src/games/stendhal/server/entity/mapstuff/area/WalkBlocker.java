/*
 * @(#) src/games/stendhal/server/entity/WalkBlocker.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

//
//

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * An entity that just acts as an obstacle. This is a temporary workaround to
 * allow items to be placed, but not players/entities, until multi-level
 * collisions can be added.
 */
public class WalkBlocker extends AreaEntity {
	/**
	 * Create a walk blocker.
	 */
	public WalkBlocker() {
		super(1, 1);

		setRPClass("walkblocker");
		put("type", "walkblocker");
		// Count as collision for the client and pathfinder
		setResistance(100);
	}


	public static void generateRPClass() {
		final RPClass blocker = new RPClass("walkblocker");
		blocker.isA("area");
		blocker.addAttribute("class", Type.STRING);
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if the other entity is an RPEntity, otherwise
	 *         the default.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		if (entity instanceof RPEntity) {
			return true;
		}

		return super.isObstacle(entity);
	}
}
