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
	 * The destination point.
	 */
	protected Node		destination;

	/**
	 * The movement direction.
	 */
	protected Direction	direction;

	/**
	 * Whether the path is finished.
	 */
	protected boolean	finished;


	/**
	 * Create an empty fixed path.
	 *
	 * @param	direction	The movement direction.
	 * @param	destination	The destination point (or null).
	 */
	public DirectionPath(final Direction direction, final Node destination) {
		this.direction = direction;
		this.destination = destination;

		finished = false;
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
		if(finished) {
			return false;
		}

		if(destination != null) {
			/*
			 * Reached the destination?
			 */
			if((destination.getX() == entity.getX()) && (destination.getY() == entity.getY())) {
				finished = true;

				return false;
			}
		}

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
		return destination;
	}


	/**
	 * Determine if the path has finished.
	 *
	 * @return	<code>true</code> if there is no more path to follow.
	 */
	@Override
	public boolean isFinished() {
		return finished;
	}


	//
	// Object
	//

	/**
	 * Get the string representation.
	 *
	 * @return	The string representation.
	 */
	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();

		sbuf.append("DirectionPath[");
		sbuf.append(getDirection());
		sbuf.append("->");
		sbuf.append(getDestination());
		sbuf.append(']');

		return sbuf.toString();
	}
}
