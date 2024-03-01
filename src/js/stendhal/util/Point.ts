/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Representation of a 2-dimension plotted point on a plane.
 */
export class Point {
	[key: string]: number|Function;

	/**
	 * Creates a new Point.
	 *
	 * @param x {number}
	 *   Coordinate on X axis.
	 * @param y {number}
	 *   Coordinate on Y axis.
	 */
	constructor(public x: number, public y: number) {};

	/**
	 * Converts to JSON formatted string.
	 */
	public toString(): string {
		return JSON.stringify({x: this.x, y: this.y});
	}
}
