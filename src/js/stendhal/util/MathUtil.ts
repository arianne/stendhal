/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Point } from "./Point";


/**
 * Helper class for mathmatical calculations & conversions.
 */
export class MathUtil {

	/** Epsilon value used for coordinate change checks. */
	public static readonly EPSILON = 0.001;

	/**
	 * Compares to floating point values.
	 *
	 * @param d1
	 *            first value
	 * @param d2
	 *            second value
	 * @param diff
	 *            acceptable diff
	 * @return true if they are within diff
	 */
	public static compareDouble(d1: number, d2: number, diff = MathUtil.EPSILON) {
		return Math.abs(d1 - d2) < diff;
	}

	/**
	 * Converts radians to angle of degrees.
	 *
	 * @param rad {number}
	 *   Radians.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static radToDeg(rad: number): number {
		return rad * 180 / Math.PI;
	}

	/**
	 * Converts angle of degrees to radians.
	 *
	 * @param angle {number}
	 *   Angle of degrees.
	 * @return {number}
	 *   Radians.
	 */
	static degToRad(angle: number): number {
		return angle * Math.PI / 180;
	}

	/**
	 * Converts an X/Y coordinate pair to radians.
	 *
	 * @param point {util.Point.Point}
	 *   Point object containing X/Y coordinates.
	 * @param x {number}
	 *   Coordinate on X axis relative to center 0,0.
	 * @param y {number}
	 *   Coordinate on Y axis relative to center 0,0.
	 * @return {number}
	 *   Radians.
	 */
	static pointToRad(point: Point): number;
	static pointToRad(x: number, y: number): number;
	static pointToRad(x: number|Point, y?: number): number {
		if (x instanceof Point) {
			y = x.y;
			x = x.x;
		}
		return Math.atan2(y!, x);
	}

	/**
	 * Converts an X/Y coordinate pair to angle of degrees.
	 *
	 * @param point {util.Point.Point}
	 *   Point object containing X/Y coordinates.
	 * @param x {number}
	 *   Coordinate on X axis relative to center 0,0.
	 * @param y {number}
	 *   Coordinate on Y axis relative to center 0,0.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static pointToDeg(point: Point): number;
	static pointToDeg(x: number, y: number): number;
	static pointToDeg(x: number|Point, y?: number): number {
		if (x instanceof Point) {
			y = x.y;
			x = x.x;
		}
		return MathUtil.radToDeg(MathUtil.pointToRad(x, y!));
	}

	/**
	 * Normalizes number value to positive angle of degrees.
	 *
	 * @param n {number}
	 *   Value to be normalized.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static normDeg(n: number): number {
		// get angle of degrees represented by value (round to 2nd decimal point)
		n = Number((n % 360).toFixed(2)) || 0;
		// normalize to positive value
		// NOTE: JavaScript has signed 0 value so get absolute just for safety
		return Math.abs(n < 0 ? n + 360 : n);
	}
}
