/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.GuidedEntity;

public abstract class Path {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Path.class);

	/**
	 * Get a reasonable maximum path length to search
	 *
	 * @param entity
	 *        moving Entity
	 * @param x
	 *        destination x
	 * @param y
	 *        destination y
	 * @return distance
	 */
	private static int defaultMaximumDistance(final Entity entity, final int x, final int y) {
		/*
		 * Pathfinding can be expensive for long distances,
		 * so don't allow arbitrary length searches.
		 */
		int manhattan = Math.abs(x - entity.getX()) + Math.abs(y - entity.getY());
		return Math.max(4 * manhattan, 80);
	}

	//
	// Path
	//

	/**
	 * Finds a path for the Entity <code>entity</code>.
	 *
	 * @param entity
	 *            the Entity
	 * @param ex
	 *            destination x
	 * @param ey
	 *            destination y
	 *
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(final Entity entity, final int ex, final int ey) {
		return searchPath(entity, entity.getX(), entity.getY(), entity.getArea(
				ex, ey), defaultMaximumDistance(entity, ex, ey));
	}

	/**
	 * Finds a path for the Entity <code>entity</code>.
	 *
	 * @param entity
	 *            the Entity
	 * @param x
	 *            start x
	 * @param y
	 *            start y
	 * @param destination
	 *            the destination area
	 * @param maxDistance
	 *            the maximum distance (as the crow flies) a possible path may
	 *            be
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(final Entity entity, final int x, final int y,
			final Rectangle2D destination, final double maxDistance) {
		return searchPath(entity, null, x, y, destination, maxDistance, true);
	}

	/**
	 * Finds a path for the Entity <code>entity</code>.
	 *
	 * @param sourceEntity
	 *            the Entity
	 * @param zone
	 *            the zone, if null the current zone of entity is used.
	 * @param x
	 *            start x
	 * @param y
	 *            start y
	 * @param destination
	 *            the destination area
	 * @param maxDistance
	 *            the maximum distance (air line) a possible path may be
	 * @param withEntities
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(final Entity sourceEntity,
			StendhalRPZone zone, final int x, final int y, final Rectangle2D destination,
			final double maxDistance, final boolean withEntities) {

		if (zone == null) {
			zone = sourceEntity.getZone();
		}

		//
		// long startTimeNano = System.nanoTime();
		final long startTime = System.currentTimeMillis();

		final EntityPathfinder pathfinder = new EntityPathfinder(sourceEntity, zone, x, y,
				destination, maxDistance, withEntities);

		final List<Node> resultPath = pathfinder.getPath();
		if (logger.isDebugEnabled()
				&& (pathfinder.getStatus() == Pathfinder.PATH_NOT_FOUND)) {
			logger.debug("Pathfinding aborted: " + zone.getID() + " "
					+ sourceEntity.getTitle() + " (" + x + ", " + y + ") "
					+ destination + " Pathfinding time: "
					+ (System.currentTimeMillis() - startTime));
		}

		return resultPath;
	}

	/**
	 * Find an one tile wide path. Entities on the map are ignored.
	 *
	 * @param zone zone to search
	 * @param startX x coordinate of the starting point
	 * @param startY y coordinate of the starting point
	 * @param destX x coordinate of the destination
	 * @param destY y coordinate of the destination
	 * @param maxDistance maximum search distance
	 *
	 * @return found path, or an empty list if no path was found
	 */
	public static List<Node> searchPath(final StendhalRPZone zone, final int startX, final int startY, final int destX,
			final int destY, final double maxDistance) {
		final Pathfinder pathfinder = new SimplePathfinder(zone, startX, startY, new Rectangle(destX, destY, 1, 1), maxDistance);
		return pathfinder.getPath();
	}

	/**
	 * Finds a path for the Entity <code>entity</code> to the other Entity
	 * <code>dest</code>.
	 *
	 * @param entity
	 *            the Entity (also start point)
	 * @param dest
	 *            the destination Entity
	 * @param maxDistance
	 *            the maximum distance (air line) a possible path may be
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(final Entity entity, final Entity dest,
			final double maxDistance) {
		/*
		 * Choose destination area so that the result corresponds to
		 * any part of the entities being next to each other
		 */
		final Rectangle2D area = new Rectangle((int) (dest.getX() - entity.getWidth()),
				(int) (dest.getY() - entity.getHeight()),
				(int) (dest.getWidth() + entity.getWidth() + 1),
				(int) (dest.getHeight() + entity.getHeight() + 1));

		return searchPath(entity, entity.getX(), entity.getY(), area, maxDistance);
	}

	/**
	 * Follow the current path (if any) by pointing the direction toward the
	 * next destination point.
	 *
	 * @param entity
	 *            The entity to point.
	 * @return true if done with path
	 */
	static boolean followPath(final GuidedEntity entity) {
		final List<Node> path = entity.getGuide().path.getNodeList();

		if (path == null) {
			return true;
		}

		int pos = entity.getPathPosition();
		Node actual = path.get(pos);

		if ((actual.getX() == entity.getX())
				&& (actual.getY() == entity.getY())) {
			logger.debug("Completed waypoint(" + pos + ")(" + actual.getX()
					+ "," + actual.getY() + ") on Path");
			pos++;
			if (pos < path.size()) {
				entity.setPathPosition(pos);
				actual = path.get(pos);
				logger.debug("Moving to waypoint(" + pos + ")(" + actual.getX()
						+ "," + actual.getY() + ") on Path from ("
						+ entity.getX() + "," + entity.getY() + ")");
				entity.faceto(actual.getX(), actual.getY());
				return false;
			} else {
				if (entity.isPathLoop()) {
					entity.setPathPosition(0);
				} else {
					entity.stop();
					entity.clearPath();
				}
				entity.onFinishedPath();
				return true;
			}
		} else {
			logger.debug("Moving to waypoint(" + pos + ")(" + actual.getX()
					+ "," + actual.getY() + ") on Path from (" + entity.getX()
					+ "," + entity.getY() + ")");
			entity.faceto(actual.getX(), actual.getY());
			return false;
		}
	}
}
