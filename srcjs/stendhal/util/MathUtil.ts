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

}
