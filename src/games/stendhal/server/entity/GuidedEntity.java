/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.server.pathfinder.FixedPath;


import games.stendhal.server.pathfinder.EntityGuide;


import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	protected double BASE_SPEED ;
	private EntityGuide guide = new EntityGuide();

	/**
	 * Create a guided entity.
	 */
	public GuidedEntity() {
		BASE_SPEED = 0;
	}

	/**
	 * Create a guided entity.
	 *
	 * @param object
	 *            The source object.
	 */
	public GuidedEntity(final RPObject object) {
		super(object);
		BASE_SPEED=0;
		update();
	}

	//
	// TEMP for Transition
	//

	/**
	 * Get the normal movement speed.
	 *
	 * @return The normal speed when moving.
	 */
	final public double getBaseSpeed(){
		return BASE_SPEED;

	}

	//
	// GuidedEntity
	//

	/**
	 * Set a path for this entity to follow. Any previous path is cleared and
	 * the entity starts at the first node (so the first node should be its
	 * position, of course). The speed will be set to the default for the
	 * entity.
	 *
	 * TODO: Change to accept just 'Path' after everything is converted to use
	 * opaque Path's rather than Node lists.
	 *
	 * @param path
	 *            The path.
	 */
	public void setPath(final FixedPath path) {
		if ((path != null) && !path.isFinished()) {
			guide.path = path;
			guide.pathPosition = 0;

			setSpeed(getBaseSpeed());
			followPath();
		} else {
			clearPath();
		}
	}

	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		guide.clearPath();

	}

	/**
	 * Determine if the entty has a path.
	 *
	 * @return <code>true</code> if there is a path.
	 */
	public boolean hasPath() {
		return (guide.path != null);
	}


	public int getPathsize() {
		return guide.getPathsize();
	}

	/**
	 * Is the path a loop.
	 */
	public boolean isPathLoop() {
		return (guide.path  != null) ? guide.path .isLoop() : false;
	}

	/**
	 * Get the path nodes position.
	 */
	public int getPathPosition() {
		return guide.pathPosition;
	}

	/**
	 * Set the path nodes position.
	 */
	public void setPathPosition(int pathPos) {
		guide.pathPosition = pathPos;
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
			followPath();
			notifyWorldAboutChanges();
		}

		super.applyMovement();
	}

	public boolean followPath() {
		return guide.followPath(this);
	}

public 	EntityGuide getGuide() {
		return guide;
	}



}
