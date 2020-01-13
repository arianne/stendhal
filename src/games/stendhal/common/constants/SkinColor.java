/***************************************************************************
 *                (C) Copyright 2005-2015 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

import games.stendhal.common.color.ARGB;
import games.stendhal.common.color.HSL;

/**
 * Acceptable colors that can be used for skin.
 *
 * @author AntumDeluge, kiheru
 */
public enum SkinColor {
	COLOR1(0.05f, 0.70f),
	COLOR2(0.05f, 0.55f),
	COLOR3(0.05f, 0.40f),
	COLOR4(0.05f, 0.25f),

	COLOR5(0.07f, 0.70f),
	COLOR6(0.07f, 0.55f),
	COLOR7(0.07f, 0.40f),
	COLOR8(0.07f, 0.25f),

	COLOR9(0.09f, 0.70f),
	COLOR10(0.09f, 0.55f),
	COLOR11(0.09f, 0.40f),
	COLOR12(0.09f, 0.25f),

	COLOR13(0.11f, 0.70f),
	COLOR14(0.11f, 0.55f),
	COLOR15(0.11f, 0.40f),
	COLOR16(0.11f, 0.25f);

	private static final float HUE_MIN = 0.05f;
	private static final float HUE_MAX = 0.11f;
	private static final float SAT_MIN = 0.25f;
	private static final float SAT_MAX = 0.70f;
	private final int color;

	public static final int DARK = 0x321c14;
	public static final int LIGHT = 0xffdab0;

	/**
	 * Constructor that sets the value of SkinColor.
	 *
	 * @param hue
	 * 		Hue of the skin tone in HSL space
	 * @param saturation
	 * 		Lightness of the skin tone in HSL space
	 */
	SkinColor(float hue, float saturation) {
		float[] hsl = { hue, saturation, 0.5f };
		int[] rgb = new int[4];
		HSL.hsl2rgb(hsl, rgb);
		this.color = ARGB.mergeRgb(rgb);
	}

	/**
	 * Find the nearest skin color corresponding to an integer color value.
	 * The lookup is done based on the hue and saturation, ignoring lightness.
	 *
	 * @param color color as RGB int
	 * @return skin color corresponding to the integer value
	 */
	public static SkinColor fromInteger(int color) {
		// Normalize lightness, and look up after the color after that
		int[] argb = new int[4];
		ARGB.splitRgb(color, argb);
		float hsl[] = new float[3];
		HSL.rgb2hsl(argb, hsl);
		float bestDelta = Float.MAX_VALUE;
		SkinColor result = null;
		for (SkinColor c : values()) {
			ARGB.splitRgb(c.color, argb);
			float[] stdHsl = new float[3];
			HSL.rgb2hsl(argb, stdHsl);
			float hDelta = hsl[0] - stdHsl[0];
			float sDelta = hsl[1] - stdHsl[1];
			float delta = hDelta * hDelta + sDelta * sDelta;
			if (delta < bestDelta) {
				result = c;
				bestDelta = delta;
			}
		}

		return result;
	}

	/**
	 * Get the corresponding color RGB.
	 *
	 * @return color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Check if an integer color corresponds to a valid skin color.
	 *
	 * @param color color to be checked
	 * @return <code>true</code> if the color is a valid skin color, otherwise
	 * 	<code>false</code>
	 */
	public static boolean isValidColor(int color) {
		float[] hsl = new float[3];
		int[] rgb = new int[4];
		ARGB.splitRgb(color, rgb);
		HSL.rgb2hsl(rgb, hsl);
		/*
		 * Lightness is not checked. We don't really want to limit it, other
		 * than the natural limits of preventing pure black and white, and
		 * those are already checked by having limits for saturation.
		 * Also we are more lenient at low lightness, as the color
		 * component resolution is there very low.
		 */
		float hueMargin = (1f - hsl[2]) / 4f + 0.02f;
		float satMargin = (1f - hsl[2]) / 3f + 0.0125f;
		return isInRange(hsl[0], HUE_MIN, HUE_MAX, hueMargin)
				&& isInRange(hsl[1], SAT_MIN, SAT_MAX, satMargin);
	}

	private static boolean isInRange(float val, float min, float max, float margin) {
		return (val + margin >= min) && (val - margin <= max);
	}
}
