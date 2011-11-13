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

import java.awt.Color;
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
	private enum Mode {
		BLEACH,
		MULTIPLY,
		SCREEN,
		TRUE_COLOR,
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
	/** Screen blend mode. */
	public static final Blend Screen = new Blend(Mode.SCREEN);

	/** Blending mode */
	private final Mode mode;
	/** Color for blend modes that need it. <code>null</code> for most. */
	private Color color;

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
		case BLEACH:
			return new BleachContext(color);
		case MULTIPLY:
			return new MultiplyContext();
		case SCREEN:
			return new ScreenContext();
		default:
			return new BlendContext(mode, srcColorModel, dstColorModel);
		}
	}
	
	/**
	 * Create a new Bleach blend for a color. The blend removes the effect of
	 * multiplying with the color, using the lightness of the source image
	 * as the degree to bleach the color.
	 * 
	 * @param color
	 * @return Bleach blend for color
	 */
	public static Blend createBleach(Color color) {
		Blend rval = new Blend(Mode.BLEACH);
		rval.color = color;
		return rval;
	}

	/**
	 * Blending mode contexts.
	 */
	private static class BlendContext implements CompositeContext {
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
			switch (mode) {
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
					splitRgb(dstData[x], dstPixel);
					// These are alpha preserving modes. Just skip any
					// transparent destination pixels
					if (dstPixel[ALPHA] != 0) {
						splitRgb(srcData[x], srcPixel);
						dstData[x] = composer.compose(srcPixel, dstPixel);
					}
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		// I presume this is meant to release native resources. The
		// documentation says nothing useful.
		public void dispose() {
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
	
	/**
	 * Blend context for removing effect of color multiply. This is yet another
	 * Stendhal specific blend mode that does not have an established name.
	 */
	private static class BleachContext implements CompositeContext {
		// Using floats is faster than doing everything in integer
		/** Color components of the color to be removed. */
		final float red, green, blue;
		/** Inverse of brightness of the bleached color. */
		final float lightFactor;
		
		/**
		 * Create a new BleachContext for a color.
		 * 
		 * @param c color to be bleached
		 */
		BleachContext(Color c) {
			int color = c.getRGB();
			red = Math.max(1, (color >> SHIFT_RED) & 0xff);
			green = Math.max(1, (color >> SHIFT_GREEN) & 0xff);
			blue = Math.max(1, (color >> SHIFT_BLUE) & 0xff);
			lightFactor = 256f / (red + green + blue + 1);
		}
		
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcData = new int[width];
			int[] dstData = new int[width];

			int[] a = new int[4];
			int[] b = new int[4];
			
			for (int y = 0; y < height; y++) {
				src.getDataElements(0, y, width, 1, srcData);
				dstIn.getDataElements(0, y, width, 1, dstData);

				for (int x = 0; x < width; x++) {
					splitRgb(dstData[x], a);
					splitRgb(srcData[x], b);
					float light = (b[RED] + b[GREEN] + b[BLUE]) * lightFactor;
					a[RED] = bleachComponent(light, a[RED], red);
					a[GREEN] = bleachComponent(light, a[GREEN], green);
					a[BLUE] = bleachComponent(light, a[BLUE], blue);
					dstData[x] = mergeRgb(a);
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}
		
		/**
		 * Bleach an individual color component.
		 * 
		 * @param light amount to be bleached. The relative brightness of the
		 * 	source image pixel
		 * @param bg bleached pixel
		 * @param color component value of the bleaching color
		 * @return bleached value of the color component
		 */
		private int bleachComponent(float light, int bg, float color) {
			float mod = (light * color) / 256f + 256f - light;
			return (int) (bg * 256f / mod);
		}

		public void dispose() {
		}
	}
	
	/**
	 * A fast, approximate multiply mode blend context. The results differ from
	 * correct multiply by factor of 255/256.
	 */
	private static class MultiplyContext implements CompositeContext {
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcData = new int[width];
			int[] dstData = new int[width];

			for (int y = 0; y < height; y++) {
				src.getDataElements(0, y, width, 1, srcData);
				dstIn.getDataElements(0, y, width, 1, dstData);

				for (int x = 0; x < width; x++) {
					/*
					 * The lighting code uses multiply composition heavily, so
					 * it'd better be fast. Avoid splitting the ARGB value to
					 * separate integers as far as possible. Also divide the
					 * multiplied result by 256 instead of 255, so that the
					 * division can be combined in the bit shifts. The shifting
					 * is done only once, when possible.
					 */
					int a = dstData[x];
					int b = srcData[x];
					// alpha
					int result = a & 0xff000000;
					
					// blue
					result |= ((a & 0xff) * (b & 0xff)) >> 8;
					// green
					result |= (((a & 0xff00) * (b & 0xff00)) >> 16) & 0xff00;
					// red would overflow without the first shift
					result |= ((((a & 0xff0000) >> 16) * (b & 0xff0000)) >> 8) & 0xff0000;

					dstData[x] = result;
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		public void dispose() {
		}
	}
	
	/**
	 * A context that implements the screen blend mode.
	 */
	private static class ScreenContext implements CompositeContext {
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());

			int[] srcData = new int[width];
			int[] dstData = new int[width];
			
			int[] srcRgb = new int[4];
			int[] dstRgb = new int[4];
			int[] result = new int[4];

			for (int y = 0; y < height; y++) {
				src.getDataElements(0, y, width, 1, srcData);
				dstIn.getDataElements(0, y, width, 1, dstData);

				for (int x = 0; x < width; x++) {
					int a = dstData[x];
					int b = srcData[x];
					splitRgb(a, dstRgb);
					splitRgb(b, srcRgb);
					
					result[ALPHA] = dstRgb[ALPHA];
					result[RED] = screenComponent(srcRgb[RED], dstRgb[RED]);
					result[GREEN] = screenComponent(srcRgb[GREEN], dstRgb[GREEN]);
					result[BLUE] = screenComponent(srcRgb[BLUE], dstRgb[BLUE]);

					dstData[x] = mergeRgb(result);
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}
		
		private int screenComponent(int a, int b) {
			return 0xff - (((0xff - a) * (0xff - b)) >> 8);
		}

		public void dispose() {
		}
	}
}
