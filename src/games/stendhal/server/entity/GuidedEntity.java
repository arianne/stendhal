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
	 * The current path.
	 */
	private Path		path;


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
	 * @param	path		The path.
	 */
	public void setPath(final Path path) {
		if((path != null) && !path.isFinished()) {
			this.path = path;

			setSpeed(getBaseSpeed());
			path.follow(this);
		} else {
			clearPath();
		}
	}


	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		this.path = null;
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
	 * Get the path.
	 *
	 * @return	The current path, or <code>null</code> if none.
	 */
	public Path getPath() {
		return path;
	}


	/**
	 * Get the path list.
	 * TODO: Not depend on FixedPath's.
	 *
	 * @return	A list of path points.
	 */
	public List<Node> getPathList() {
		if(path instanceof FixedPath) {
			return ((FixedPath) path).getNodeList();
		} else {
			return null;
		}
	}


	/**
	 * Get the path nodes position.
	 * TODO: Not assume FixedPath's.
	 *
	 * @return	The current position in a fixed path.
	 */
	public int getPathPosition() {
		if(path instanceof FixedPath) {
			return ((FixedPath) path).getPosition();
		} else {
			return 0;
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
			if(!path.follow(this)) {
				stop();
			}

			notifyWorldAboutChanges();
		}

		// TODO: Eventually only call super if a path is set,
		// once GuidedEntity only uses Path for movement. 
		super.applyMovement();
	}


	/**
	 * Stops entity movement.
	 */
	@Override
	public void stop() {
		setPath(null);
		super.stop();
	}
}
