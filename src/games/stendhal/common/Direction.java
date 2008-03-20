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

	},
	UP(1, 0, -1) {
		@Override
		public Direction nextDirection() {
			return RIGHT;
		}
	},
	RIGHT(2, 1, 0) {
		@Override
		public Direction nextDirection() {
			return DOWN;
		}
	},
	DOWN(3, 0, 1) {
		@Override
		public Direction nextDirection() {
			return LEFT;
		}
	},
	LEFT(4, -1, 0) {
		@Override
		public Direction nextDirection() {
			return UP;
		}
	};

	private final int val;
	private final int dx;
	private final int dy;

	public static Direction build(int val) {
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
		return Direction.values()[Rand.rand(4)+1];
	}

	Direction(int val, int dx, int dy) {
		this.val = val;
		this.dx = dx;
		this.dy = dy;
	}

	public int get() {
		return val;
	}

	public Direction oppositeDirection() {
		switch (this) {
		case UP:
			return DOWN;
		case RIGHT:
			return LEFT;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		default:
			return STOP;
		}
	}

	abstract public Direction nextDirection();
}
