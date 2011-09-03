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
		COLOR,
		TRUE_COLOR,
		MULTIPLY
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

	/**
	 * A blending mode that colors the destination image with the source color.
	 */
	public static final Blend Color = new Blend(Mode.COLOR);
	// This is a pretty non-standard blend mode, and as far as I know, it has no
	// common name (if it is even implemented by anyone else)
	/**
	 * A blending mode that colors the destination image with the source color.
	 * Also adjusts the middle lightness values up or down, depending on the
	 * lightness of the source image.
	 */
	public static final Blend TrueColor = new Blend(Mode.TRUE_COLOR);
	/** A blending mode that multiplies the underlying image with the above one. */
	public static final Blend Multiply = new Blend(Mode.MULTIPLY);

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
		return new BlendContext(mode, srcColorModel, dstColorModel);
	}

	/**
	 * Blending mode contexts.
	 */
	private static class BlendContext implements CompositeContext {
		final int maxAlpha;
		final Composer composer;

		/**
		 * Create a new ColorBlendContext. The result is treated as a bitmask
		 * transparency image, if either of the images does not have alpha.
		 * 
		 * @param mode blending mode
		 * @param srcColorModel
		 * @param dstColorModel
		 */
		BlendContext(Mode mode, ColorModel srcColorModel, ColorModel dstColorModel) {
			maxAlpha = srcColorModel.getAlpha(0xffffffff);
			
			switch (mode) {
			case COLOR: composer = new ColorComposer();
			break;
			case MULTIPLY: composer = new MultiplyComposer();
			break;
			case TRUE_COLOR: composer = new TrueColorComposer();
			break;
			// Can not really happen, but the compiler is too dumb to know that
			default: composer = null;
			}
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcPixel = new int[4];
			int[] dstPixel = new int[4];
			int[] srcData = new int[width];
			int[] dstData = new int[width];

			for (int y = 0; y < height; y++) {
				src.getDataElements(0, y, width, 1, srcData);
				dstIn.getDataElements(0, y, width, 1, dstData);

				for (int x = 0; x < width; x++) {
					splitRgb(srcData[x], srcPixel);
					splitRgb(dstData[x], dstPixel);
					dstData[x] = composer.compose(srcPixel, dstPixel);
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		// I presume this is meant to release native resources. The
		// documentation says nothing useful.
		public void dispose() {
		}
		
		/**
		 * Composer for COLOR blending mode.
		 */
		private static class ColorComposer implements Composer {
			/**
			 * Blend 2 pixels, taking the color from upper, and lightness from
			 * the lower pixel.
			 * 
			 * @param srcPixel upper pixel color data
			 * @param dstPixel lower pixel color data
			 * @return blended pixel
			 */
			public int compose(int[] srcPixel, int[] dstPixel) {
				// jvm should be smart enough to allocate these on the stack
				float[] srcHsl = new float[3];
				float[] dstHsl = new float[3];
				int[] result = new int[4];
				float[] hslResult = new float[3];

				rgb2hsl(srcPixel, srcHsl);
				rgb2hsl(dstPixel, dstHsl);

				// not all components are needed, so this could be optimized a
				// bit
				hslResult[0] = srcHsl[0];
				hslResult[1] = srcHsl[1];
				hslResult[2] = dstHsl[2];
				hsl2rgb(hslResult, result);
				result[ALPHA] = dstPixel[ALPHA];
				
				return mergeRgb(result);
			}
		}
		
		/**
		 * Composer for the special Stendhal color blend.
		 */
		private static class TrueColorComposer implements Composer {
			/**
			 * Blend 2 pixels, taking the color from upper, and lightness from
			 * the lower pixel.
			 * 
			 * @param srcPixel upper pixel color data
			 * @param dstPixel lower pixel color data
			 * @return blended pixel
			 */
			public int compose(int[] srcPixel, int[] dstPixel) {
				// jvm should be smart enough to allocate these on the stack
				float[] srcHsl = new float[3];
				float[] dstHsl = new float[3];
				int[] result = new int[4];
				float[] hslResult = new float[3];

				rgb2hsl(srcPixel, srcHsl);
				rgb2hsl(dstPixel, dstHsl);

				// Adjust the brightness
				float adj = srcHsl[2] - 0.5f; // [-0.5, 0.5]
				float tmp = dstHsl[2] - 0.5f; // [-0.5, 0.5]
				// tweaks the middle lights either upward or downward, depending
				// on if source lightness is high or low
				float l = dstHsl[2] - 2.0f * adj * ((tmp * tmp) - 0.25f);
				hslResult[0] = srcHsl[0];
				hslResult[1] = srcHsl[1];
				hslResult[2] = l;
				hsl2rgb(hslResult, result);
				result[ALPHA] = dstPixel[ALPHA];

				return mergeRgb(result);
			}
		}

		
		/**
		 * A composer for MULTIPLY blending mode.
		 */
		private class MultiplyComposer implements Composer {
			/**
			 * Blend 2 pixels by multiplying their values together.
			 * 
			 * @param srcPixel upper pixel color data
			 * @param dstPixel lower pixel color data
			 * @return blended pixel
			 */
			public int compose(int[] srcPixel, int[] dstPixel) {
				int[] result = new int[4];
				result[ALPHA] = dstPixel[ALPHA] * srcPixel[ALPHA] / maxAlpha;
				result[RED] = dstPixel[RED] * srcPixel[RED] / 255;
				result[GREEN] = dstPixel[GREEN] * srcPixel[GREEN] / 255;
				result[BLUE] = dstPixel[BLUE] * srcPixel[BLUE] / 255;

				return mergeRgb(result);
			}
		}
		
		/**
		 * Interface for blending 2 pixels.
		 */
		private interface Composer {
			/**
			 * Blend 2 pixels.
			 * 
			 * @param srcPixel upper pixel color data
			 * @param dstPixel lower pixel color data
			 * @return blended pixel
			 */
			int compose(int[] srcPixel, int[] dstPixel);
		}
	}
	
	/**
	 * Split ARGB color to its components.
	 * 
	 * @param rgb
	 * @param result
	 */
	public static void splitRgb(int rgb, int[] result) {
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
	public static int mergeRgb(int[] rgbData) {
		int rgb = rgbData[ALPHA] << SHIFT_ALPHA;
		rgb |= rgbData[RED] << SHIFT_RED;
		rgb |= rgbData[GREEN] << SHIFT_GREEN;
		rgb |= rgbData[BLUE] << SHIFT_BLUE;

		return rgb;
	}

	/**
	 * Transform ARGB color vector to HSL space. Transparency is dropped.
	 * All returned components are in range [0, 1].
	 *  
	 * @param rgb
	 * @param hsl
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
	 * @param hsl
	 * @param rgb
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

			r = Math.min(255, Math.max(0, (int) (255 * rf)));
			g = Math.min(255, Math.max(0, (int) (255 * gf)));
			b = Math.min(255, Math.max(0, (int) (255 * bf)));
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
