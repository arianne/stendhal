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

import { MathUtil } from "./MathUtil";


/**
 * Represents a set of values between two angles of degrees.
 */
export class AngleRange {
	[index: string]: number|Function;

	public readonly min: number;
	public readonly max: number;


	/**
	 * Creates a new range.
	 *
	 * @param min {number}
	 *   The lowest angle value (inclusive) this range represents.
	 * @param max
	 *   The highest angle value (inclusive) this range represents.
	 */
	constructor(min: number, max: number) {
		// normalize range to positive values
		this.min = MathUtil.normDeg(min);
		this.max = MathUtil.normDeg(max);
	}

	/**
	 * Checks if an angle of degrees is within range of angle values.
	 *
	 * @param angle {number}
	 *   Angle to be checked.
	 * @return {boolean}
	 *   `true` if "angle" is within "min" & "max" properties.
	 */
	public contains(angle: number): boolean {
		// normalize angle to positive value
		angle = MathUtil.normDeg(angle);
		// in degrees minimum can be a value greater that maximum
		if (this.min > this.max) {
			return angle >= this.min || angle <= this.max;
		}
		return angle >= this.min && angle <= this.max;
	}

	/**
	 * Retrieves number of degrees between the min & max values.
	 */
	public getSize(): number {
		if (this.min > this.max) {
			return 360 - this.min + this.max;
		}
		return this.max - this.min;
	}
}
