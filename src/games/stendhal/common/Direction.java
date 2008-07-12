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
package games.stendhal.common;

public enum Direction {
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

	public int getdx() {
		return dx;
	}

	public int getdy() {
		return dy;
	}

	public static Direction rand() {
		return Direction.values()[Rand.rand(4) + 1];
	}

	Direction(final int val, final int dx, final int dy) {
		this.val = val;
		this.dx = dx;
		this.dy = dy;
	}

	public int get() {
		return val;
	}

	public abstract Direction oppositeDirection();

	public abstract Direction nextDirection();
}
