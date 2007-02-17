/*
 * @(#) src/games/stendhal/server/entity/WalkBlocker.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;

/**
 * An entity that just acts as an obsticle. This is a temporary workaround
 * to allow items to be placed, but not players/entities, until multi-level
 * collisions can be added.
 */
public class WalkBlocker extends Entity {
	/**
	 * The logger instance.
	 */
	private static final Logger	logger =
					Log4J.getLogger(WalkBlocker.class);

	/**
	 * Create a walk blocker.
	 */
	public WalkBlocker() throws AttributeNotFoundException {
		put("type", "walk_blocker");
		put("server-only", "");
	}


	//
	// Entity
	//

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}


	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> if the other entity is an RPEntity.
	 */
	public boolean isObstacle(Entity entity) {
		return (entity instanceof RPEntity);
	}
}
