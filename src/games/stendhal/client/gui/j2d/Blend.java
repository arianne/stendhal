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
package games.stendhal.client.gui.j2d;

import static games.stendhal.common.color.ARGB.ALPHA;
import static games.stendhal.common.color.ARGB.BLUE;
import static games.stendhal.common.color.ARGB.GREEN;
import static games.stendhal.common.color.ARGB.RED;
import static games.stendhal.common.color.ARGB.mergeRgb;
import static games.stendhal.common.color.ARGB.splitRgb;
import static games.stendhal.common.color.HSL.hsl2rgb;
import static games.stendhal.common.color.HSL.rgb2hsl;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import games.stendhal.client.MemoryCache;

/**
 * Blending composite modes.
 */
public class Blend implements Composite {
	/** Cached composers. */
	private static final MemoryCache<Object, CompositeContext> cache = new MemoryCache<Object, CompositeContext>();

	/**
	 * Possible blending modes.
	 */
	private enum Mode {
		BLEACH,
		GENERIC_LIGHT,
		MULTIPLY,
		SCREEN,
		SOFT_LIGHT,
		TRUE_COLOR,
	}

	// This is a pretty non-standard blend mode, and as far as I know, it has no
	// common name (if it is even implemented by anyone else)
	/**
	 * A blending mode that colors the destination image with the source color.
	 * Also adjusts the middle lightness values up or down, depending on the
	 * lightness of the source image.
	 */
	public static final Blend TrueColor = new Blend(Mode.TRUE_COLOR, null);
	/**
	 * A generic lighting blend. Supports only white light and is not as
	 * accurate as Bleach for zones using multiply blend. Those should use
	 * Bleach instead, but this can be used for others.
	 */
	public static final Blend GenericLight = new Blend(Mode.GENERIC_LIGHT, null);
	/** A blending mode that multiplies the underlying image with the above one. */
	public static final Blend Multiply = new Blend(Mode.MULTIPLY, null);
	/** Screen blend mode. */
	public static final Blend Screen = new Blend(Mode.SCREEN, null);
	/**
	 * Softlight blend mode. Note that this is similar to the GIMP layer mode
	 * with the same name.
	 */
	public static final Blend SoftLight = new Blend(Mode.SOFT_LIGHT, null);

	/** Blending mode */
	private final Mode mode;
	/** Color for blend modes that need it. <code>null</code> for most. */
	private final Color color;
	/** Cached name for toString() */
	private String name;

	/**
	 * Create a new Blend.
	 *
	 * @param mode Blending mode
	 * @param color Color for modes that need it
	 */
	private Blend(Mode mode, Color color) {
		this.mode = mode;
		this.color = color;
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel,
			ColorModel dstColorModel,
			RenderingHints arg2) {
		switch (mode) {
		case MULTIPLY:
			return new MultiplyContext();
		// Modes with significant creation overhead (lookup tables). Cache those
		case SOFT_LIGHT:
			CompositeContext ctx = cache.get(Mode.SOFT_LIGHT);
			if (ctx == null) {
				ctx = new BlendContext(mode, color);
				cache.put(Mode.SOFT_LIGHT, ctx);
			}
			return ctx;
		default:
			return new BlendContext(mode, color);
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
		return new Blend(Mode.BLEACH, color);
	}

	@Override
	public String toString() {
		// Sprite cache uses blend names, so give it something constant
		if (name == null) {
			String colorName = "";
			if (color != null) {
				colorName = "(" + Integer.toHexString(color.getRGB()) + ")";
			}
			name = mode + colorName;
		}
		return name;
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
		 * @param color Color for modes that need it
		 */
		BlendContext(Mode mode, Color color) {
			switch (mode) {
			case BLEACH: composer = new BleachComposer(color);
			break;
			case GENERIC_LIGHT: composer = new GenericLightComposer();
			break;
			case SOFT_LIGHT: composer = new SoftLightComposer();
			break;
			case SCREEN: composer = new ScreenComposer();
			break;
			case TRUE_COLOR: composer = new TrueColorComposer();
			break;
			// Can not really happen, but the compiler is too dumb to know that
			default: composer = null;
			}
		}

		@Override
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
						// Skip transparent source pixels.
						if (srcPixel[ALPHA] != 0) {
							dstData[x] = composer.compose(srcPixel, dstPixel);
						}
					}
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		// I presume this is meant to release native resources. The
		// documentation says nothing useful.
		@Override
		public void dispose() {
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

	/**
	 * Blend composer for removing effect of color multiply. This is yet another
	 * Stendhal specific blend mode that does not have an established name. Both
	 * the images are supposed to have been multiplied by the same color.
	 */
	private static class BleachComposer implements Composer {
		// Using floats is faster than doing everything in integer
		/** Color components of the color to be removed. */
		final float red, green, blue;

		/**
		 * Create a new BleachComposer for a color.
		 *
		 * @param c color to be bleached
		 */
		BleachComposer(Color c) {
			int color = c.getRGB();
			int[] components = new int[4];
			splitRgb(color, components);
			red = Math.max(1, components[RED]);
			green = Math.max(1, components[GREEN]);
			blue = Math.max(1, components[BLUE]);
		}

		/**
		 * Blend 2 pixels. Removes the effect of color multiplication on the
		 * underlaying image, using the brightness of the original version of the
		 * overlaying image as the amount of bleaching. The brightness of the
		 * overlaying image is adjusted so that the color of each pixel is
		 * assumed to be result of (yet another) multiplication with color
		 * <b>A</b>. The added brightness to the underlaying image is multiplied
		 * with the hypothesized color <b>A</b>, so that the overall effect
		 * looks like colored light.
		 *
		 * @param srcPixel upper pixel color data
		 * @param dstPixel lower pixel color data
		 * @return blended pixel
		 */
		@Override
		public int compose(int[] srcPixel, int[] dstPixel) {
			// Original values of the overlay pixel. [0, 1]
			float srcRed = srcPixel[RED] / red;
			float srcGreen = srcPixel[GREEN] / green;
			float srcBlue = srcPixel[BLUE] / blue;

			float light = limitMin(Math.max(srcRed, Math.max(srcGreen, srcBlue)));
			/*
			 * Treat lightness of color like it was grey scale, but had been
			 * multiplied with the color of the light.
			 */
			float multRed = limitMin(srcRed / light);
			float multGreen = limitMin(srcGreen / light);
			float multBlue = limitMin(srcBlue / light);

			dstPixel[RED] = bleachComponent(light, dstPixel[RED], red, multRed);
			dstPixel[GREEN] = bleachComponent(light, dstPixel[GREEN], green, multGreen);
			dstPixel[BLUE] = bleachComponent(light, dstPixel[BLUE], blue, multBlue);

			return mergeRgb(dstPixel);
		}

		/**
		 * Limit a color value to > 0, so that when it is used as a divisor the
		 * result will not overflow.
		 *
		 * @param colorComponent
		 * @return colorComponent, or at least 0.001
		 */
		private float limitMin(float colorComponent) {
			return Math.max(0.001f, colorComponent);
		}

		/**
		 * Bleach an individual color component.
		 *
		 * @param light amount to be bleached. The relative brightness of the
		 * 	source image pixel
		 * @param bg bleached pixel
		 * @param color component value of the bleaching color
		 * @param multColor color of the source image (the value of the
		 * 	source pixel = light × multColor)
		 *
		 * @return bleached value of the color component
		 */
		private int bleachComponent(float light, int bg, float color, float multColor) {
			float mod = (light * color) / 255f + 1f - light;
			float change = bg / mod - bg;
			return (int) (bg + change * multColor);
		}
	}

	/**
	 * A composer for the generic lighting blend.
	 */
	private static class GenericLightComposer implements Composer {
		/**
		 * Divider for the added light. Larger means dimmer effect. 0xff is
		 * very bright.
		 */
		private static final int DIMMING_FACTOR = 0x200;

		@Override
		public int compose(int[] srcPixel, int[] dstPixel) {
			int sum = 0;
			for (int i = RED; i <= BLUE; i++) {
				sum += srcPixel[i];
			}
			// average color
			int avg = sum / 3;
			for (int i = RED; i <= BLUE; i++) {
				dstPixel[i] = composeComponent(avg, dstPixel[i]);
			}

			return mergeRgb(dstPixel);
		}

		/**
		 * Apply composition to an individual color component of a pixel.
		 *
		 * @param a
		 * @param b
		 * @return composed color value
		 */
		private int composeComponent(int a, int b) {
			return Math.min(b + b * a / DIMMING_FACTOR, 0xff);
		}
	}

	/**
	 * Composer for simple blends that only need the individual source and
	 * destination color components to calculate the new color component.
	 */
	private static abstract class SimpleComposer implements Composer {
		@Override
		public int compose(int[] srcPixel, int[] dstPixel) {
			for (int i = RED; i <= BLUE; i++) {
				dstPixel[i] = composeComponent(srcPixel[i], dstPixel[i]);
			}

			return mergeRgb(dstPixel);
		}

		/**
		 * Apply composition to an individual color component of a pixel.
		 *
		 * @param a upper layer color value
		 * @param b lower layer color value
		 * @return composed color value
		 */
		abstract int composeComponent(int a, int b);
	}

	/**
	 * A composer that implements the gimp style soft light blend mode.
	 */
	private static class SoftLightComposer implements Composer {
		/** Lookup table. */
		private final byte[] table;

		/**
		 * Create a SoftLightComposer. Initializes the lookup table.
		 */
		public SoftLightComposer() {
			table = new byte[256 * 256];
			for (int a = 0; a < 256; a++) {
				for (int b = 0; b < 256; b++) {
					table[(a * 256) + b] = (byte) ((b + 2 * a * (0xff - b) / 0xff) * b / 0xff);
				}
			}
		}

		@Override
		public int compose(int[] srcPixel, int[] dstPixel) {
			int rval = 0xff000000 & (dstPixel[ALPHA] << 24);
			for (int i = RED; i <= BLUE; i++) {
				// Stupid signed bytes. "& 0xff" is to prevent sign changes in
				// the result
				rval |= ((table[(256 * srcPixel[i]) + dstPixel[i]]) & 0xff) << (8 * (3 - i));
			}

			return rval;
		}
	}

	/**
	 * A composer that implements the screen blend mode.
	 */
	private static class ScreenComposer extends SimpleComposer {
		@Override
		int composeComponent(int a, int b) {
			return 0xff - (((0xff - a) * (0xff - b)) >> 8);
		}
	}

	/**
	 * Composer for the special Stendhal color blend.
	 */
	private static class TrueColorComposer implements Composer {
		private final float[] srcHsl = new float[3];
		private final float[] dstHsl = new float[3];
		private final int[] result = new int[4];

		/**
		 * Blend 2 pixels, taking the color from upper, and lightness from
		 * the lower pixel. Adjusts the lightness slightly by the lightness of
		 * the upper pixel.
		 *
		 * @param srcPixel upper pixel color data
		 * @param dstPixel lower pixel color data
		 * @return blended pixel
		 */
		@Override
		public int compose(int[] srcPixel, int[] dstPixel) {
			rgb2hsl(srcPixel, srcHsl);
			rgb2hsl(dstPixel, dstHsl);

			// Adjust the brightness
			float adj = srcHsl[2] - 0.5f; // [-0.5, 0.5]
			float tmp = dstHsl[2] - 0.5f; // [-0.5, 0.5]
			// tweaks the middle lights either upward or downward, depending
			// on if source lightness is high or low
			float l = dstHsl[2] - 2.0f * adj * ((tmp * tmp) - 0.25f);
			srcHsl[2] = l;
			hsl2rgb(srcHsl, result);
			result[ALPHA] = dstPixel[ALPHA];

			return mergeRgb(result);
		}
	}

	/**
	 * A fast, approximate multiply mode blend context. The results differ from
	 * correct multiply by factor of 1 - 255/256.
	 */
	private static class MultiplyContext implements CompositeContext {
		@Override
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
					// Ignore transparent source pixels
					if ((b & 0xff000000) != 0) {
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
				}
				dstOut.setDataElements(0, y, width, 1, dstData);
			}
		}

		@Override
		public void dispose() {
		}
	}
}
