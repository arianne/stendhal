/*
 * @(#) src/games/stendhal/server/pathfinder/DirectionPath.java
 *
 * $Id$
 */

package games.stendhal.server.pathfinder;

//
//

import games.stendhal.common.Direction;
import games.stendhal.server.entity.ActiveEntity;

/**
 * A path using a single direction.
 */
public class DirectionPath extends Path {
	/**
	 * The movement direction.
	 */
	protected Direction	direction;


	/**
	 * Create an empty fixed path.
	 */
	public DirectionPath(final Direction direction) {
		this.direction = direction;
	}


	//
	// DirectionPath
	//

	/**
	 * Get the direction.
	 *
	 * @return	The direction to follow.
	 */
	public Direction getDirection() {
		return direction;
	}


	//
	// Path
	//

	/**
	 * Follow this path. This will face the entity into the proper
	 * direction to reach it's next path goal.
	 *
	 * @param	entity		The entity to direct along the path.
	 *
	 * @return	<code>true</code> if something to follow,
	 *		<code>false</code> if complete.
	 */
	@Override
	public boolean follow(final ActiveEntity entity) {
		entity.setDirection(direction);
		return true;
	}


	/**
	 * Get the final destination point.
	 *
	 * @return	The destination node, or <code>null</code> if there
	 *		is none (i.e. no path, or unbound/infinite movement).
	 */
	@Override
	public Node getDestination() {
		return null;
	}


	/**
	 * Determine if the path has finished.
	 *
	 * @return	<code>true</code> if there is no more path to follow.
	 */
	public boolean isFinished() {
		return false;
	}
}
