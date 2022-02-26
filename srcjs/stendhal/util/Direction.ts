/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

/**
 * a direction to face or walk to
 *
 * @author hendrik
 */
export class Direction {

	public static readonly STOP  = new Direction(0, 0, 0);
	public static readonly UP    = new Direction(1, 0, -1);
	public static readonly RIGHT = new Direction(2, 1, 0);
	public static readonly DOWN  = new Direction(3, 0, 1);
	public static readonly LEFT  = new Direction(4, -1, 0);

	public static readonly VALUES = [Direction.STOP, Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT];

	constructor(
		public readonly val: number,
		public readonly dx: number,
		public readonly dy: number) {
	}

}
