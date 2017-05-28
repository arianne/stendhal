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

import java.awt.geom.Rectangle2D;

import games.stendhal.common.CollisionDetection;
import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * A simple and stupid path finder that checks just the collision map,
 * but does not require adding an entity to the map before using.
 */
public class SimplePathfinder extends Pathfinder {
	final CollisionDetection collision;

	/**
	 * Create a new <code>SimplePathfinder</code>.
	 *
	 * @param zone The zone used for path finding
	 * @param startX Starting point x coordinate
	 * @param startY Starting point y coordinate
	 * @param destination destination area
	 * @param maxDist maximum search distance
	 */
	public SimplePathfinder(final StendhalRPZone zone, final int startX, final int startY,
			final Rectangle2D destination, final double maxDist) {
		super(startX, startY, destination, maxDist);
		collision = zone.collisionMap;
	}

	@Override
	public TreeNode createNode(int x, int y) {
		return new SimpleTreeNode(x, y);
	}

	private class SimpleTreeNode extends TreeNode {
		protected SimpleTreeNode(int x, int y) {
			super(x, y);
		}

		@Override
		public TreeNode createNode(int x, int y) {
			return new SimpleTreeNode(x, y);
		}

		@Override
		protected int createNodeID(int x, int y) {
			return x + y * collision.getWidth();
		}

		@Override
		public boolean isValid(int x, int y) {
			return !collision.collides(x, y);
		}
	}
}
