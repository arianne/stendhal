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

import static games.stendhal.common.color.ARGB.BLUE;
import static games.stendhal.common.color.ARGB.GREEN;
import static games.stendhal.common.color.ARGB.RED;

import games.stendhal.common.math.Algebra;

/**
 * Methods for transforming between 32 bit ARGB and floating point HSL color
 * spaces.
 */
public class HSL {
	/**
	 * Transform ARGB color vector to HSL space. Transparency is dropped.
	 * All returned components are in range [0, 1].
	 *
	 * @param rgb red, green, blue value
	 * @param hsl hue, saturation, lightness
	 */
	public static void rgb2hsl(int[] rgb, float[] hsl) {
		float h, s, l;
		int maxVar;

		float max, min;
		float r = rgb[RED] / 255f;
		float g = rgb[GREEN] / 255f;
		float b = rgb[BLUE] / 255f;

		// Find the max and minimum colors, and remember which one it was
		if (r > g) {
			max = r;
			min = g;
			maxVar = RED;
		} else {
			max = g;
			min = r;
			maxVar = GREEN;
		}
		if (b > max) {
			max = b;
			maxVar = BLUE;
		} else if (b < min) {
			min = b;
		}

		// lightness
		l = (max + min) / 2;

		// saturation
		float diff = max - min;
		if (diff < Algebra.EPSILON) {
			s = 0;
			// hue not really defined, but set it to something reasonable
			h = 0;
		} else {
			if (l < 0.5f) {
				s = diff / (max + min);
			} else {
				s = diff / (2 - max - min);
			}

			// hue
			if (maxVar == RED) {
				h = (g - b) / diff;
			} else if (maxVar == GREEN) {
				h = 2f + (b - r) / diff;
			} else {
				h = 4f + (r - g) / diff;
			}
			// Normalize to range [0, 1]. It's more useful than the usual 360
			h /= 6f;
		}
		hsl[0] = h;
		hsl[1] = s;
		hsl[2] = l;
	}

	/**
	 * Transform HSL color vector to ARGB space. Alpha is kept at 0 for
	 * everything. All HSL should be scaled to range [0, 1].
	 *
	 * @param rgb red, green, blue value
	 * @param hsl hue, saturation, lightness
	 */
	public static void hsl2rgb(float[] hsl, int[] rgb) {
		int r, g, b;
		float h = hsl[0];
		float s = hsl[1];
		float l = hsl[2];

		if (s < Algebra.EPSILON) {
			r = g = b = (int) (255 * l);
		} else {
			float tmp1, tmp2;
			if (l < 0.5f) {
				tmp1 = l * (1f + s);
			} else {
				tmp1 = l + s - l * s;
			}
			tmp2 = 2f * l - tmp1;

			float rf = hue2rgb(limitHue(h + 1f/3f), tmp2, tmp1);
			float gf = hue2rgb(limitHue(h), tmp2, tmp1);
			float bf = hue2rgb(limitHue(h - 1f/3f), tmp2, tmp1);

			r = ((int) (255 * rf)) & 0xff;
			g = ((int) (255 * gf)) & 0xff;
			b = ((int) (255 * bf)) & 0xff;
		}

		rgb[RED] = r;
		rgb[GREEN] = g;
		rgb[BLUE] = b;
	}

	/**
	 * Keep hue value within [0, 1] circle.
	 *
	 * @param hue
	 * @return normalized hue
	 */
	private static float limitHue(float hue) {
		float res = hue;
		if (res < 0) {
			res += 1f;
		} else if (res > 1f) {
			res -= 1f;
		}
		return res;
	}

	/**
	 * Transform a hue to RGB.
	 *
	 * @param hue
	 * @param val1 color dependent value
	 * @param val2 color dependent value
	 * @return R, G, or B value
	 */
	private static float hue2rgb(float hue, float val1, float val2) {
		float res = hue;
		if (6f * hue < 1f) {
			res = val1 + (val2 - val1) * 6f * hue;
		} else if (2f * hue < 1f) {
			res = val2;
		} else if (3f * hue < 2f) {
			res = val1 + (val2 - val1) * (2f/3f - hue) * 6f;
		} else {
			res = val1;
		}

		return res;
	}
}
