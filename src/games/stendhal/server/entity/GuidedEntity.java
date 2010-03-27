/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import java.util.List;

import games.stendhal.server.core.pathfinder.EntityGuide;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	protected double baseSpeed;

	private final EntityGuide guide = new EntityGuide();

	/**
	 * Create a guided entity.
	 */
	public GuidedEntity() {
		baseSpeed = 0;
		guide.guideMe(this);
	}

	/**
	 * Create a guided entity.
	 * 
	 * @param object
	 *            The source object.
	 */
	public GuidedEntity(final RPObject object) {
		super(object);
		baseSpeed = 0;
		guide.guideMe(this);
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
	public final double getBaseSpeed() {
		return baseSpeed;

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
	 * @param path
	 *            The path.
	 */
	public final void setPath(final FixedPath path) {
		if ((path != null) && !path.isFinished()) {
			setSpeed(getBaseSpeed());
			guide.path = path;
			guide.pathPosition = 0;
			guide.followPath(this);
		} else {
			guide.clearPath();
		}
	}

	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		guide.clearPath();

	}

	/**
	 * Determine if the entity has a path.
	 * 
	 * @return <code>true</code> if there is a path.
	 */
	public boolean hasPath() {
		return (guide.path != null);
	}


	/**
	 * Is the path a loop.
	 * @return true if running in circles
	 */
	public boolean isPathLoop() {
		if (guide.path == null) {
			return false;
		} else {
			return guide.path.isLoop();
		}
	}

	/**
	 * Get the path nodes position.
	 * @return position in path
	 */
	public int getPathPosition() {
		return guide.pathPosition;
	}

	/**
	 * Set the path nodes position.
	 * @param pathPos 
	 */
	public void setPathPosition(final int pathPos) {
		guide.pathPosition = pathPos;
	}
	
	/**
	 * Plan a new path to the old destination.
	 */
	public void reroute() {
		if (hasPath()) {
			Node node = guide.path.getDestination();
			final List<Node> path = Path.searchPath(this, node.getX(), node.getY());
		
			if (path.size() >= 1) {
				setPath(new FixedPath(path, false));
			} else {
				/*
				 * It can happen that some other entity goes to occupy the
				 * target position after the path has been planned. Just 
				 * stop if that happens and we are next to the goal. 
				 */
				clearPath();
				stop();
			}
		}
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

	public EntityGuide getGuide() {
		return guide;
	}

	@Override
	protected void handleObjectCollision() {
		stop();
		clearPath();
	}

}
