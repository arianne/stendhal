/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

import java.awt.geom.Rectangle2D;

/**
 * a direction to face or walk to
 *
 * @author hendrik
 */
public enum Direction {
	/** do not move */
	STOP(0, 0, 0) {
		@Override
		public Direction nextDirection() {
			return LEFT;
		}
		@Override
		public Direction oppositeDirection() {
			return STOP;
		}

	},
	/** up, away from the screen */
	UP(1, 0, -1) {
		@Override
		public Direction nextDirection() {
			return RIGHT;
		}
		@Override
		public Direction oppositeDirection() {
			return DOWN;
		}
	},
	/** to the right */
	RIGHT(2, 1, 0) {
		@Override
		public Direction nextDirection() {
			return DOWN;
		}
		@Override
		public Direction oppositeDirection() {
			return LEFT;
		}
	},
	/** down, facing the player */
	DOWN(3, 0, 1) {
		@Override
		public Direction nextDirection() {
			return LEFT;
		}
		@Override
		public Direction oppositeDirection() {
			return UP;
		}
	},
	/** to the left */
	LEFT(4, -1, 0) {
		@Override
		public Direction nextDirection() {
			return UP;
		}
		@Override
		public Direction oppositeDirection() {
			return RIGHT;
		}
	};

	private final int val;
	private final int dx;
	private final int dy;

	/**
	 * converts an integer to a Direction
	 *
	 * @param val int
	 * @return Direction
	 */
	public static Direction build(final int val) {
		switch (val) {
		case 1:
			return UP;

		case 2:
			return RIGHT;

		case 3:
			return DOWN;

		case 4:
			return LEFT;

		default:
			return STOP;

		}
	}

	/**
	 * gets the delta on the x-axis
	 *
	 * @return delta x
	 */
	public int getdx() {
		return dx;
	}

	/**
	 * gets the delta on the y-axis
	 *
	 * @return dy
	 */
	public int getdy() {
		return dy;
	}

	/**
	 * gets a random direction
	 *
	 * @return Direction
	 */
	public static Direction rand() {
		return Direction.values()[Rand.rand(4) + 1];
	}

	/**
	 * creates a direction
	 *
	 * @param val index
	 * @param dx  delta on the x-axis
	 * @param dy  delta on the y-axis
	 */
	Direction(final int val, final int dx, final int dy) {
		this.val = val;
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * gets the index
	 *
	 * @return index
	 */
	public int get() {
		return val;
	}

	/**
	 * Compares two area and return the direction of area2 towards area1. So if area2 is left of
	 * area1, it will return Direction.LEFT
	 * @param area1 The area to compare with
	 * @param area2 The area to be compared
	 * @return The Direction of area2 as seen from area1
	 */
	public static Direction getAreaDirectionTowardsArea(final Rectangle2D area1, final Rectangle2D area2) {
		final double x = area2.getCenterX() - area1.getCenterX();
		final double y = area2.getCenterY() - area1.getCenterY();
		if (Math.abs(x) > Math.abs(y)) {
			if (x > 0) {
				return RIGHT;
			} else {
				return LEFT;
			}
		} else {
			if (y > 0) {
				return DOWN;
			} else {
				return UP;
			}
		}
	}

	/**
	 * gets the opposite direction
	 *
	 * @return Direction
	 */
	public abstract Direction oppositeDirection();

	/**
	 * gets the next direction clockwise
	 *
	 * @return Direction
	 */
	public abstract Direction nextDirection();
}
