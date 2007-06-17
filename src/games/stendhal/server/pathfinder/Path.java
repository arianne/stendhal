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
package games.stendhal.server.pathfinder;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.RPEntity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public abstract class Path {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Path.class);


	//
	// Path
	//

	/**
	 * Follow this path.
	 *
	 * @param	entity		The entity to direct along the path.
	 *
	 * @return	<code>true</code> if something to follow,
	 *		<code>false</code> if complete.
	 */
	public abstract boolean follow(ActiveEntity entity);


	/**
	 * Get the final destination point.
	 *
	 * @return	The destination node, or <code>null</code> if there
	 *		is none (i.e. no path, or unbound/infinite movement).
	 */
	public abstract Node getDestination();


	/**
	 * Determine if the path has finished.
	 *
	 * @return	<code>true</code> if there is no more path to follow.
	 */
	public abstract boolean isFinished();


	private static StepCallback callback;

	public static int steps;

	public static class Node {

		public int x;

		public int y;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}


		/**
		 * Get the X coordinate.
		 *
		 * @return	The X coordinate.
		 */
		public int getX() {
			return x;
		}


		/**
		 * Get the Y coordinate.
		 *
		 * @return	The Y coordinate.
		 */
		public int getY() {
			return y;
		}


		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	/**
	 * Sets the step-callback. This will be called after each step. <b>Note:
	 * </b> This is a debug method and not part of the 'official api'.
	 */
	public static void setCallback(StepCallback callback) {
		Path.callback = callback;
	}

	protected static void faceto(ActiveEntity entity, int x, int y) {
		int rndx = x - entity.getX();
		int rndy = y - entity.getY();

		if (Math.abs(rndx) > Math.abs(rndy)) {
			if (rndx < 0.0) {
				entity.setDirection(Direction.LEFT);
			} else {
				entity.setDirection(Direction.RIGHT);
			}
		} else {
			if (rndy < 0.0) {
				entity.setDirection(Direction.UP);
			} else {
				entity.setDirection(Direction.DOWN);
			}
		}
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
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(Entity entity, int x, int y, Rectangle2D destination) {
		return searchPath(entity, x, y, destination, -1.0);
	}

	public static List<Node> searchPath(Entity entity, int ex, int ey) {
		return searchPath(entity, entity.getX(), entity.getY(), entity.getArea(ex, ey), -1.0);
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
	 *            the maximum distance (air line) a possible path may be
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(Entity entity, int x, int y, Rectangle2D destination, double maxDistance) {
		return searchPath(entity, null, x, y, destination, maxDistance, true);
	}

	/**
	 * Finds a path for the Entity <code>entity</code>.
	 * 
	 * @param entity
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
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(Entity entity, StendhalRPZone zone, int x, int y, Rectangle2D destination,
	        double maxDistance, boolean withEntities) {

		if (zone == null) {
			zone = entity.getZone();
		}

		// Log4J.startMethod(logger, "searchPath");
		//		long startTimeNano = System.nanoTime(); 
		long startTime = System.currentTimeMillis();

		Pathfinder path = new Pathfinder();
		StendhalNavigable navMap;
		if (withEntities) {
			navMap = new StendhalNavigableEntities(entity, zone, x, y, destination);
		} else {
			navMap = new StendhalNavigable(entity, zone, x, y, destination);
		}

		// The most expensive path is the not existing path
		if (navMap.unrechable()) {
			return new LinkedList<Node>();
		}

		path.setNavigable(navMap);
		path.setStart(new Pathfinder.Node(x, y));

		/* 
		 * if the destination is an area
		 * set the destination node for pathfinding to the center of the area
		 */
		if ((destination.getWidth() > 2) || (destination.getHeight() > 2)) {
			path.setGoal(new Pathfinder.Node((int) (destination.getCenterX()), (int) (destination.getCenterY())));
		} else {
			path.setGoal(new Pathfinder.Node((int) destination.getX(), (int) destination.getY()));
		}

		steps = 0;
		path.init();

		while (path.getStatus() == Pathfinder.IN_PROGRESS) {
			path.doStep();
			steps++;
			if (callback != null) {
				callback.stepDone(path.getBestNode());
			}
		}

		long endTime = System.currentTimeMillis();
		if (false && logger.isDebugEnabled()) {
			logger.debug("Route (" + x + "," + y + ")-(" + destination + ") S:" + steps + " OL:"
			        + path.getOpen().size() + " CL:" + path.getClosed().size() + " in " + (endTime - startTime) + "ms");
		}
		// 		logger.info("status: " + path.getStatus());
		if (path.getStatus() == Pathfinder.PATH_NOT_FOUND) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pathfinding aborted: " + zone.getID() + " " + entity.get("name") + " (" + x + ", " + y
				        + ") " + destination + " Pathfinding time: " + (System.currentTimeMillis() - startTime)
				        + "  steps: " + steps);
			}
			return new LinkedList<Node>();
		}
		//		time = time + System.nanoTime() - startTimeNano;
		//		counter++;

		List<Node> list = new LinkedList<Node>();
		Pathfinder.Node node = path.getBestNode();
		while (node != null) {
			list.add(0, new Node(node.getX(), node.getY()));
			node = node.getParent();
		}

		// Log4J.finishMethod(logger, "searchPath");
		return list;
	}

// TODO: Re-enable/(refactor?) later if async needed.
//	/**
//	 * Finds a path for the Entity <code>entity</code> to the other Entity
//	 * <code>dest</code>.
//	 * 
//	 * @param entity
//	 *            the Entity (also start point)
//	 * @param dest
//	 *            the destination Entity
//	 */
//	public static void searchPathAsynchonous(RPEntity entity, Entity dest) {
//		StendhalRPWorld.get().checkPathfinder();
//
//		boolean result = StendhalRPWorld.get().getPathfinder().queuePath(
//		        new QueuedPath(new SimplePathListener(entity), entity, entity.getX(), entity.getY(), dest.getArea(dest
//		                .getX(), dest.getY())));
//
//		if (!result) {
//			logger.warn("Pathfinder queue is full...path not added");
//		}
//	}

	/**
	 * Finds a path for the Entity <code>entity</code> to (or next to)
	 * the other Entity <code>dest</code>.
	 * 
	 * @param entity
	 *            the Entity (also start point)
	 * @param dest
	 *            the destination Entity
	 * @return a list with the path nodes or an empty list if no path is found
	 */
	public static List<Node> searchPath(Entity entity, Entity dest) {
		return searchPath(entity, dest, -1.0);
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
	public static List<Node> searchPath(Entity entity, Entity dest, double maxDistance) {
		Rectangle2D area = dest.getArea(dest.getX(), dest.getY());

		/*
		 * Expand area by surounding tiles.
		 */
		return searchPath(entity, entity.getX(), entity.getY(), new Rectangle(((int) area.getX()) - 1, ((int) area
		        .getY()) - 1, ((int) area.getWidth()) + 2, ((int) area.getHeight()) + 2), maxDistance);
	}

	/**
	 * Follow the current path (if any) by pointing the direction toward
	 * the next destination point.
	 *
	 * @param	entity		The entity to point.
	 */
	public static boolean followPath(final GuidedEntity entity) {
		List<Node> path = entity.getPathList();

		if (path == null) {
			return true;
		}

		int pos = entity.getPathPosition();
		Node actual = path.get(pos);

		if((actual.x == entity.getX()) && (actual.y == entity.getY())) {
			logger.debug("Completed waypoint(" + pos + ")(" + actual.x + "," + actual.y + ") on Path");
			pos++;
			if (pos < path.size()) {
				entity.setPathPosition(pos);
				actual = path.get(pos);
				logger.debug("Moving to waypoint(" + pos + ")(" + actual.x + "," + actual.y + ") on Path from ("
				        + entity.getX() + "," + entity.getY() + ")");
				faceto(entity, actual.x, actual.y);
				return false;
			} else {
				if (entity.isPathLoop()) {
					entity.setPathPosition(0);
				} else {
					entity.stop();
					entity.clearPath();
				}

				return true;
			}
		} else {
			logger.debug("Moving to waypoint(" + pos + ")(" + actual.x + "," + actual.y + ") on Path from ("
			        + entity.getX() + "," + entity.getY() + ")");
			faceto(entity, actual.x, actual.y);
			return false;
		}
	}

	/** this callback is called after every A* step. */
	public interface StepCallback {

		public void stepDone(Pathfinder.Node lastNode);
	}

// TODO: Re-enable/(refactor?) later if async needed.
//	/** the threaded-pathfinder callback */
//	private static class SimplePathListener implements PathListener {
//
//		/** the entity the path belongs to */
//		private RPEntity entity;
//
//		/**
//		 * creates a new instance of SimplePathLister
//		 * 
//		 * @param entity
//		 *            the entity the path belongs to
//		 */
//		public SimplePathListener(RPEntity entity) {
//			this.entity = entity;
//		}
//
//		/** simply appends the calculated path to the entitys path */
//		public void onPathFinished(QueuedPath path, PathState state) {
//			if (state == PathState.PATH_FOUND) {
//				entity.addToPath(path.getPath());
//			}
//		}
//	}
}
