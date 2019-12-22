/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.pathfinder;

import java.util.List;

import games.stendhal.server.entity.GuidedEntity;

/**
 * the guide dog of an Entity. this class takes the goals where an Entity shall
 * move to via a path. and keeps control over the steps.
 *
 * The guided Entity can always change the destination. Like a blind person can
 * tell its guide dog.
 *
 * @author astrid
 *
 */
public class EntityGuide {

	public void guideMe(final GuidedEntity ge) {
		// empty default implementation
	}

	/**
	 * The path.
	 */
	public FixedPath path;
	/**
	 * current position in the path.
	 */
	public int pathPosition;

	public boolean followPath(final GuidedEntity entity) {
		return Path.followPath(entity);
	}

	public void clearPath() {
		path = null;
		pathPosition = 0;

	}

	/**
	 * Turn the entity face the next node on the path.
	 * If the entity following the path just reached the
	 * last node, stop the entity.
	 *
	 * @param entity the guided entity
	 */
	public void faceNext(GuidedEntity entity) {
		if (path != null && nodeReached(entity)) {
			Node next = nextNode();
			if (next != null) {
				entity.faceto(next.getX(), next.getY());
			} else {
				// There is no next path; Stop the entity so that the
				// client gets the information in time
				entity.stop();
			}
		}
	}

	/**
	 * @return
	 *         Entity's last position
	 */
	public int getPreviousPosition() {
	    int prevPos = pathPosition - 1;
	    if (prevPos < 0) {
	        prevPos = path.getNodeList().size() - 1;
	    }

	    return prevPos;
	}

	/**
	 * Get the next node on the path.
	 *
	 * @return The next <code>Node</code>, or <code>null</code> if there is no next node.
	 */
	public Node nextNode() {
		final List<Node> nodes = path.getNodeList();
		int nextPos = pathPosition + 1;
		Node next = null;

		if (nextPos < nodes.size()) {
			next = nodes.get(nextPos);
		} else if (path.isLoop()) {
			next = nodes.get(0);
		}

		return next;
	}

	/**
	 * Get the previous node on the path.
	 *
	 * @return The previous <code>Node</code>.
	 */
	public Node prevNode() {
		return path.getNodeList().get(getPreviousPosition());
	}

	/**
	 * Check if the entity has reached the current goal.
	 *
	 * @param entity the guided entity
	 * @return true iff the current goal node has been reached
	 */
	private boolean nodeReached(GuidedEntity entity) {
		Node previous = path.getNodeList().get(pathPosition);

		return ((previous.getX() == entity.getX())
			&& (previous.getY() == entity.getY()));
	}
}
