/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import java.util.Collections;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.EntityGuide;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.Entity;

public class PassiveNPC extends NPC {

	// used for entities with fixed path to check if movement is possible
	private boolean pathBlocked = false;

	public PassiveNPC() {
		put("title_type", "npc");

		baseSpeed = 0.2;
		createPath();
		setSize(1, 1);

		// NOTE: sub-classes must call updateModifiedAttributes()
	}

	/**
	 * Create path for the NPC. Sub classes can implement this method.
	 */
	protected void createPath() {
	}

	/**
	 * Changed the entity's path to walk in the opposite direction
	 */
	public void reversePath() {
		final EntityGuide guide = getGuide();
	    if (!usesRandomPath() && guide.path.isLoop()) {
	        List<Node> reverseNodes = guide.path.getNodeList();

	        // Sets the position for the reversed path
	        int reversePosition = (guide.path.getNodeList().size() - 1) - guide.getPreviousPosition();

	        Collections.reverse(reverseNodes);
	        setPath(new FixedPath(reverseNodes, guide.path.isLoop()), reversePosition);
	        }
	    else {
	    	stop();
	    }
	}

	/**
	 * Checks if entity can move.
	 *
	 * For entities with fixed path, checks if collision occurred with previous movement.
	 * For other entities, checks adjacent nodes for collision.
	 */
	private boolean pathIsBlocked() {
		if (!usesRandomPath()) {
			return pathBlocked;
		}

		final StendhalRPZone zone = getZone();
		final List<Node> adjacentNodes = getAdjacentNodes();
		for (final Node node: adjacentNodes) {
			if (zone.collides(node.getX(), node.getY())) {
				continue;
			} else {
				final List<Entity> entities = zone.getEntitiesAt(node.getX(), node.getY());

				boolean blocked = false;
				for (final Entity entity: entities) {
					if (entity.getResistance() >= 100) {
						blocked = true;
						break;
					}
				}

				if (!blocked) {
					return false;
				}
			}
		}

		// all directions are blocked
		return true;
	}

	/**
	 * Plan a new path to the old destination.
	 */
	@Override
	public void reroute() {
		if (getPath().isLoop()) {
			// If entity cannot be rerouted use reversePath
			reversePath();
		} else {
			super.reroute();
		}
	}

	@Override
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		super.onMoved(oldX, oldY, newX, newY);

		if (pathBlocked) {
			// was able to move
			pathBlocked = false;
		}
	}

	@Override
	protected void handleObjectCollision() {
		if (pathIsBlocked()) {
			stop();
			return;
		}

		CollisionAction action = getCollisionAction();

		if (usesRandomPath()) {
			setRandomPathFrom(getX(), getY(), getMovementRange() / 2);
		} else if (action == CollisionAction.REVERSE) {
			reversePath();
		} else if (action == CollisionAction.REROUTE) {
			reroute();
		} else {
			stop();
		}

		pathBlocked = true;
	}

	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		CollisionAction action = getCollisionAction();
		if (!ignoresCollision()) {
			if (usesRandomPath()) {
				setRandomPathFrom(getX(), getY(), getMovementRange() / 2);
			} else if (action == CollisionAction.REROUTE) {
				reroute();
			} else {
				stop();
			}
		}

		pathBlocked = true;
	}
}
