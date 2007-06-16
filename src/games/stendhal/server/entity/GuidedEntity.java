/*
 * @(#) src/games/stendhal/server/entity/GuidedEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.RPObject;

/**
 * An entity that has speed/direction and is guided via a Path.
 */
public abstract class GuidedEntity extends ActiveEntity {
	/** the path */
	private List<Path.Node> path;

	/** current position in the path */
	private int pathPosition;

	/** true if the path is a loop */
	private boolean pathLoop;


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
	 *       to use Path's rather than Node lists.
	 * 
	 * @param	path		The path.
	 */
	public void setPath(final FixedPath path) {
		if(path != null) {
			setPath(path.getNodeList(), path.isLoop());
		} else {
			clearPath();
		}
	}


	/**
	 * Set a path for this entity to follow. Any previous path is cleared
	 * and the entity starts at the first node (so the first node should
	 * be its position, of course). The speed will be set to the default
	 * for the entity.
	 * 
	 * TODO: Deprecate and merge with setPath(Path), but not yet.
	 * 
	 * @param	path		List of connected nodes
	 * @param	cycle		If true, the entity will resume at
	 *				the start of the path when finished;
	 *				If false, it will stop at the last
	 *				node (and clear the path).
	 * SOON: @deprecated
	 */
	public void setPath(final List<Path.Node> path, final boolean cycle) {
		if((path != null) && !path.isEmpty()) {
			this.path = path;
			this.pathPosition = 0;
			this.pathLoop = cycle;

			setSpeed(getBaseSpeed());
			Path.followPath(this);
		} else {
			clearPath();
		}
	}


	/**
	 * Adds some nodes to the path to follow for this entity. The current
	 * path-position is kept.
	 */
	public void addToPath(List<Path.Node> pathNodes) {
		if (path == null) {
			path = new ArrayList<Path.Node>();
		}

		path.addAll(pathNodes);
	}


	/**
	 * Sets the loop-flag of the path. Note that the path should be closed.
	 */
	public void setPathLoop(boolean loop) {
		this.pathLoop = loop;
	}


	/**
	 * Clear the entity's path.
	 */
	public void clearPath() {
		this.path = null;
		this.pathPosition = 0;
		this.pathLoop = false;
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
	 */
	public List<Path.Node> getPath() {
		return path;
	}


	/**
	 * Is the path a loop.
	 */
	public boolean isPathLoop() {
		return pathLoop;
	}


	/**
	 * Get the path nodes position.
	 */
	public int getPathPosition() {
		return pathPosition;
	}


	/**
	 * Determine if the path had completed.
	 */
	public boolean pathCompleted() {
		return (path != null) && (pathPosition == path.size() - 1);
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
