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

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * Server side path finder.
 */
class EntityPathfinder extends games.stendhal.server.core.pathfinder.Pathfinder {
	/**
	 * Distance at where another moving entity is considered to be close enough
	 * that it's considered a collision.
	 */
	private static final double COLLISION_DISTANCE_SQUARED = 0.1;
	/**
	 * The entity searching a path.
	 */
	private final Entity entity;

	/**
	 * The zone a path is searched.
	 */
	private final StendhalRPZone zone;

	private final boolean checkEntities;

	/**
	 * Contains the resistance data for entities.
	 */
	private ResistanceMap resistanceMap;

	EntityPathfinder(final Entity entity, final StendhalRPZone zone, final int startX, final int startY,
			final Rectangle2D destination, final double maxDist, final boolean checkEntities) {
		super(startX, startY, destination, maxDist);
		this.entity = entity;
		this.zone = zone;
		this.checkEntities = checkEntities;
	}

	@Override
	protected void init() {
		super.init();
		if (checkEntities) {
			createEntityCollisionMap();
		}
	}

	/**
	 * Creates resistance data for entities.
	 * <p>The positions with entities are only
	 * considered as not valid if they:
	 * <li> are next to the start position or
	 * <li> have stopped
	 */
	private void createEntityCollisionMap() {
		Point targetPoint = new Point(goalNode.getX(), goalNode.getY());
		resistanceMap = new ResistanceMap(zone.getWidth(), zone.getHeight());
		for (final RPObject obj : zone) {
			final Entity otherEntity = (Entity) obj;
			if (!entity.getID().equals(otherEntity.getID())
					&& (otherEntity.stopped()|| (otherEntity.squaredDistance(startNode.getX(), startNode.getY()) < COLLISION_DISTANCE_SQUARED))) {
				final Rectangle2D area = otherEntity.getArea();
				// Hack: Allow players to move onto portals as destination
				if ((entity instanceof Player) && (otherEntity instanceof Portal) && area.contains(targetPoint)) {
					continue;
				}
				int resistance = otherEntity.getResistance(entity);
				resistanceMap.addResistance(area, resistance);
			}
		}
	}

	@Override
	public TreeNode createNode(int x, int y) {
		return new PathTreeNode(x, y);
	}

	/**
	 * Pathfinder node
	 */
	private class PathTreeNode extends TreeNode {
		private final double cost;

		protected PathTreeNode(int x, int y) {
			super(x, y);

			/*
			 * Modify movement cost by resistance
			 */
			if (resistanceMap != null) {
				int resistance = resistanceMap.getResistance(x, y , entity.getWidth(), entity.getHeight());
				cost = 100.0 / (100 - resistance);
			} else {
				cost = 1.0;
			}
		}

		@Override
		protected double getCost() {
			return cost;
		}

		@Override
		public TreeNode createNode(int x, int y) {
			return new PathTreeNode(x, y);
		}

		@Override
		protected int createNodeID(int x, int y) {
			return x + y * zone.getWidth();
		}

		@Override
		public boolean isValid(int x, int y) {
			boolean result = !zone.simpleCollides(entity, x, y, entity.getWidth(), entity.getHeight());
			if (checkEntities && result) {
				result = !resistanceMap.collides(x, y, entity.getWidth(), entity.getHeight());
			}

			return result;
		}
	}

	/**
	 * Resistance data for entities.
	 */
	private static class ResistanceMap {
		/** Resistance that corresponds to collision */
		private static final int COLLISION = 100;
		/** Minimum resistance that is considered a collision */
		private static final int COLLIDE_THRESHOLD = 95;

		private final int width, height;
		private final int[][] map;

		/**
		 * Create a new ResistanceMap.
		 *
		 * @param width width of the area
		 * @param height height of the area
		 */
		public ResistanceMap(int width, int height) {
			this.width = width;
			this.height = height;
			map = new int[width][height];
		}

		/**
		 * Check if an area is impassable for the entity.
		 *
		 * @param x the x coordinate of the upper left corner of the rectangle to be checked
		 * @param y the y coordinate of the upper left corner of the rectangle to be checked
		 * @param w the width of the rectangle to be checked
		 * @param h the height of the rectangle to be checked
		 * @return <code>true</code> if area can not be occupied,
		 * 	<code>false</code> otherwise
		 */
		public boolean collides(final double x, final double y, double w, double h) {
			return getResistance(x, y, w, h) > COLLIDE_THRESHOLD;
		}

		/**
		 * Add resistance of an area to the entity.
		 *
		 * @param area affected area
		 * @param resistance value between 0 and 100
		 */
		public void addResistance(Rectangle2D area, int resistance) {
			final double x = area.getX();
			final double y = area.getY();
			double w = area.getWidth();
			double h = area.getHeight();

			final int startx = (int) Math.max(0, x);
			final int endx = (int) Math.min(width, x + w);
			final int starty = (int) Math.max(0, y);
			final int endy = (int) Math.min(height, y + h);

			// Fill the area
			for (int k = startx; k < endx; k++) {
				for (int i = starty; i < endy; i++) {
					/*
					 * There can be multiple entities covering an area. (Such
					 * as blood covering a grower). Can we have multiple
					 * non-zero resistances? Cover the case anyway, in case we
					 * want to give something like corpses some resistance to
					 * make it harder to wade through a pile of bodies.
					 */
					int old = map[k][i];
					/*
					 * Add up like probabilities. Several slightly resistant
					 * entities can still add up to a completely impassable
					 * barrier, when the resistance grows over
					 * COLLIDE_THRESHOLD.
					 */
					map[k][i] = 100 - ((100 - old) * (100 - resistance)) / 100;
				}
			}
		}

		/**
		 * Get resistance for placing the entity to an area.
		 *
		 * @param x the x coordinate of the upper left corner of the rectangle to be checked
		 * @param y the y coordinate of the upper left corner of the rectangle to be checked
		 * @param w the width of the rectangle to be checked
		 * @param h the height of the rectangle to be checked
		 * @return resistance
		 */
		public int getResistance(final double x, final double y, double w, double h) {
			if ((x < 0) || (x >= width)) {
				return COLLISION;
			}

			if ((y < 0) || (y >= height)) {
				return COLLISION;
			}

			final int startx = (int) Math.max(0, x);
			final int endx = (int) Math.min(width, x + w);
			final int starty = (int) Math.max(0, y);
			final int endy = (int) Math.min(height, y + h);

			final int entitySize = (int) (w * h);
			int resistance = 0;
			for (int k = startx; k < endx; k++) {
				for (int i = starty; i < endy; i++) {
					int r = map[k][i];
					if (r > COLLIDE_THRESHOLD) {
						/*
						 * A full collision is always collision, regardless of
						 * the other tiles.
						 */
						return COLLISION;
					} else {
						/*
						 * A large creature will find walking over partial
						 * collision easier than small one. It can step over it
						 * or just push through using force. On the other hand
						 * a smaller entity can possibly run between the
						 * resistant areas.
						 */
						resistance += r / entitySize;
					}
				}
			}

			return resistance;
		}
	}
}
