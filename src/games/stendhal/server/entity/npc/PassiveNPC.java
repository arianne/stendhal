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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.core.pathfinder.EntityGuide;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;

public abstract class PassiveNPC extends NPC {

	private boolean teleports = false;


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
	 * Retrieves the coordinates of the next position on the entity's path or `null`.
	 */
	private Point getPosFront() {
		if (hasPath()) {
			final Node next = getGuide().nextNode();
			final Rectangle2D area = new Rectangle.Double();
			area.setRect(next.getX(), next.getY(), 1, 1);
			final Direction dir = getDirectionToward(area);

			return new Point(getX() + dir.getdx(), getY() + dir.getdy());
		}

		return null;
	}

	/**
	 * Retrieves the coordinates of the previous position on the entity's path or `null`.
	 */
	private Point getPosBehind() {
		if (hasPath()) {
			final Node prev = getGuide().prevNode();
			final Rectangle2D area = new Rectangle.Double();
			area.setRect(prev.getX(), prev.getY(), 1, 1);
			final Direction dir = getDirectionToward(area);

			return new Point(getX() + dir.getdx(), getY() + dir.getdy());
		}

		return null;
	}

	/**
	 * Checks if entity can move.
	 */
	private boolean pathIsBlocked() {
		if (!usesRandomPath()) {
			/* for entities with fixed path we check collision of adjacent nodes
			 * on entity's path only
			 */
			return !canMoveTo(getPosFront()) && !canMoveTo(getPosBehind());
		}

		/* for entities with random path we check collision of all adjacent nodes */
		for (final Node node: getAdjacentNodes()) {
			if (canMoveTo(node.getX(), node.getY())) {
				return false;
			}
		}

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
	}

	public void setTeleportsFlag(final boolean teleports) {
		this.teleports = teleports;
	}

	public boolean isTeleporter() {
		return teleports;
	}
}
