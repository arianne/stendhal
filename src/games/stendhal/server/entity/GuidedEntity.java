/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.pathfinder.Path;

import java.util.List;

import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	/**
	 * The path.
	 */
	private FixedPath	path;

	/** current position in the path */
	private int pathPosition;


	/**
	 * Create a guided entity.
	 */
	public GuidedEntity() {
	}


	/**
	 * Create a guided entity.
	 *
	 * @param	object		The source object.
	 */
	public GuidedEntity(final RPObject object) {
		super(object);

		update();
	}


	//
	// TEMP for Transition
	//

	/**
	 * Get the normal movement speed.
	 *
	 * @return	The normal speed when moving.
	 */
	public abstract double getBaseSpeed();


	//
	// GuidedEntity
	//

	/**
	 * Set a path for this entity to follow. Any previous path is cleared
	 * and the entity starts at the first node (so the first node should
	 * be its position, of course). The speed will be set to the default
	 * for the entity.
	 *
	 * TODO: Change to accept just 'Path' after everything is converted
	 *       to use opaque Path's rather than Node lists.
	 *
	 * @param	path		The path.
	 */
	public void setPath(final FixedPath path) {
		if((path != null) && !path.isFinished()) {
			this.path = path;
			this.pathPosition = 0;

			setSpeed(getBaseSpeed());
			Path.followPath(this);
		} else {
			clearPath();
		}
	}


	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		this.path = null;
		this.pathPosition = 0;
	}


	/**
	 * Determine if the entty has a path.
	 *
	 * @return	<code>true</code> if there is a path.
	 */
	public boolean hasPath() {
		return (path != null);
	}


	/**
	 * Get the path list.
	 */
	public List<Node> getPathList() {
		return (path != null) ? path.getNodeList() : null;
	}


	/**
	 * Is the path a loop.
	 */
	public boolean isPathLoop() {
		return (path != null) ? path.isLoop() : false;
	}


	/**
	 * Get the path nodes position.
	 */
	public int getPathPosition() {
		return pathPosition;
	}


	/**
	 * Set the path nodes position.
	 */
	public void setPathPosition(int pathPos) {
		this.pathPosition = pathPos;
	}


	//
	// ActiveEntity
	//

	/**
	 * Apply movement and process it's reactions.
	 */
	@Override
	public void applyMovement() {
		if (hasPath()) {
			Path.followPath(this);
			notifyWorldAboutChanges();
		}

		super.applyMovement();
	}
}
