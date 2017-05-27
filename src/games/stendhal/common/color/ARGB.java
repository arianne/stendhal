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
package games.stendhal.common.color;

/**
 * Convenience methods for dealing with ARGB colors.
 */
public class ARGB {
	// ARGB format
	/** Position of alpha component. */
	public static final int ALPHA = 0;
	/** Position of red component. */
	public static final int RED = 1;
	/** Position of green component. */
	public static final int GREEN = 2;
	/** Position of blue component. */
	public static final int BLUE = 3;


	/** Amount to need shifting to access alpha at an integer */
	private static final int SHIFT_ALPHA = (24 - ALPHA * 8);
	/** Amount to need shifting to access red at an integer */
	private static final int SHIFT_RED = (24 - RED * 8);
	/** Amount to need shifting to access green at an integer */
	private static final int SHIFT_GREEN = (24 - GREEN * 8);
	/** Amount to need shifting to access blue at an integer */
	private static final int SHIFT_BLUE = (24 - BLUE * 8);

	/**
	 * Split ARGB color to its 8 bit color components.
	 *
	 * @param rgb red, green, blue value
	 * @param result array of length 4.
	 */
	public static void splitRgb(int rgb, int[] result) {
		result[ALPHA] = (rgb >> SHIFT_ALPHA) & 0xff;
		result[RED] = (rgb >> SHIFT_RED) & 0xff;
		result[GREEN] = (rgb >> SHIFT_GREEN) & 0xff;
		result[BLUE] = (rgb >> SHIFT_BLUE) & 0xff;
	}


	/**
	 * Merge 8 bit ARGB color components to one integer color.
	 *
	 * @param rgbData data to be merged. Array of length 4.
	 * @return ARGB as an integer
	 */
	public static int mergeRgb(int[] rgbData) {
		int rgb = rgbData[ALPHA] << SHIFT_ALPHA;
		rgb |= rgbData[RED] << SHIFT_RED;
		rgb |= rgbData[GREEN] << SHIFT_GREEN;
		rgb |= rgbData[BLUE] << SHIFT_BLUE;

		return rgb;
	}
}
