/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d;

import games.stendhal.common.math.Algebra;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Blending composite modes.
 */
public class Blend implements Composite {
	/**
	 * Possible blending modes.
	 */
	public enum Mode {
		COLOR;
	}

	// ARGB format
	/** Position of alpha component. */
	private static final int ALPHA = 0;
	/** Position of red component. */
	private static final int RED = 1;
	/** Position of green component. */
	private static final int GREEN = 2;
	/** Position of blue component. */
	private static final int BLUE = 3;

	/** Amount to need shifting to access alpha at an integer */
	private static final int SHIFT_ALPHA = (24 - ALPHA * 8);
	/** Amount to need shifting to access red at an integer */
	private static final int SHIFT_RED = (24 - RED * 8);
	/** Amount to need shifting to access green at an integer */
	private static final int SHIFT_GREEN = (24 - GREEN * 8);
	/** Amount to need shifting to access blue at an integer */
	private static final int SHIFT_BLUE = (24 - BLUE * 8);

	/** A blending mode that colors the underlying image with the above one. */
	public static final Blend Color = new Blend(Mode.COLOR);

	/** Blending mode */
	private final Mode mode;

	/**
	 * Create a new Blend.
	 * 
	 * @param mode blending mode
	 */
	private Blend(Mode mode) {
		this.mode = mode;
	}

	public CompositeContext createContext(ColorModel srcColorModel,
			ColorModel dstColorModel,
			RenderingHints arg2) {
		switch (mode) {
		case COLOR:
			return new ColorBlendContext(srcColorModel, dstColorModel);
		}
		return null;
	}

	/**
	 * Color blending mode. Similar to the gimp "color" layer mode.
	 */
	private static class ColorBlendContext implements CompositeContext {
		final boolean alpha;

		/**
		 * Create a new ColorBlendContext. The result is treated as a bitmask
		 * transparency image, if either of the images does not have alpha.
		 * 
		 * @param srcColorModel
		 * @param dstColorModel
		 */
		ColorBlendContext(ColorModel srcColorModel, ColorModel dstColorModel) {
			alpha = (srcColorModel.getTransparency() == Transparency.TRANSLUCENT) 
			&& (dstColorModel.getTransparency() == Transparency.TRANSLUCENT);
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcPixel = new int[4];
			int[] dstPixel = new int[4];
			int[] srcData = new int[width];
			int[] dstData = new int[width];

			float[] srcHsl = new float[3];
			float[] dstHsl = new float[3];
			int[] result = new int[4];
			float[] hslResult = new float[3];

			for (int y = 0; y < height; y++) {
				src.getDataElements(0, y, width, 1, srcData);
				dstIn.getDataElements(0, y, width, 1, dstData);

				for (int x = 0; x < width; x++) {
					splitRgb(srcData[x], srcPixel);
					splitRgb(dstData[x], dstPixel);
					rgb2hsl(srcPixel, srcHsl);
					rgb2hsl(dstPixel, dstHsl);

					// not all components are needed, so this could be optimized a
					// bit
					hslResult[0] = srcHsl[0];
					hslResult[1] = srcHsl[1];
					hslResult[2] = dstHsl[2];


					hsl2rgb(hslResult, result);
					if (alpha) {
						result[ALPHA] = Math.min(255, srcPixel[ALPHA] + dstPixel[ALPHA]);
					} else {
						result[ALPHA] = dstPixel[ALPHA];
					}
					dstData[x] = mergeRgb(result);
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		public void dispose() {
		}
	}

	/**
	 * Split ARGB color to its components.
	 * 
	 * @param rgb
	 * @param result
	 */
	private static void splitRgb(int rgb, int[] result) {
		result[ALPHA] = (rgb >> SHIFT_ALPHA) & 0xff;
		result[RED] = (rgb >> SHIFT_RED) & 0xff;
		result[GREEN] = (rgb >> SHIFT_GREEN) & 0xff;
		result[BLUE] = (rgb >> SHIFT_BLUE) & 0xff;
	}

	/**
	 * Merge ARGB color components to one integer element.
	 * 
	 * @param rgbData data to be merged
	 * @return ARGB as an integer
	 */
	private static int mergeRgb(int[] rgbData) {
		int rgb = rgbData[ALPHA] << SHIFT_ALPHA;
		rgb |= rgbData[RED] << SHIFT_RED;
		rgb |= rgbData[GREEN] << SHIFT_GREEN;
		rgb |= rgbData[BLUE] << SHIFT_BLUE;

		return rgb;
	}

	/**
	 * Transform ARGB color vector to HSL space. Transparency is dropped.
	 *  
	 * @param rgb
	 * @param hsl
	 */
	private static void rgb2hsl(int[] rgb, float[] hsl) {
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

			if (maxVar == RED) {
				h = (g - b) / diff;
			} else if (maxVar == GREEN) {
				h = 2f + (b - r) / diff;
			} else {
				h = 4f + (r - g) / diff;
			}
		}
		hsl[0] = h;
		hsl[1] = s;
		hsl[2] = l;
	}

	/**
	 * Transform HSL color vector to ARGB space. Alpha is kept at 0 for
	 * everything.
	 * 
	 * @param hsl
	 * @param rgb
	 */
	static void hsl2rgb(float[] hsl, int[] rgb) {
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

			float hNorm = h / 6;
			//float hNorm = h;
			float rf = hue2rgb(limitHue(hNorm + 1f/3f), tmp2, tmp1);
			float gf = hue2rgb(limitHue(hNorm), tmp2, tmp1);
			float bf = hue2rgb(limitHue(hNorm - 1f/3f), tmp2, tmp1);

			r = (int) (255 * rf);
			g = (int) (255 * gf);
			b = (int) (255 * bf);
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
		if (hue < 0) {
			hue += 1f;
		} else if (hue > 1f) {
			hue -= 1f;
		}
		return hue;
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
		if (6f * hue < 1f) {
			hue = val1 + (val2 - val1) * 6f * hue;
		} else if (2f * hue < 1f) {
			hue = val2;
		} else if (3f * hue < 2f) {
			hue = val1 + (val2 - val1) * (2f/3f - hue) * 6f;
		} else {
			hue = val1;
		}

		return hue;
	}
}
