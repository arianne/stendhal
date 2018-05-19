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
	COLOR1(0.03f, 0.02f),
	COLOR2(0.03f, 0.35f),
	COLOR3(0.03f, 0.65f),
	COLOR4(0.03f, 0.98f),
	
	COLOR5(0.06f, 0.02f),
	COLOR6(0.06f, 0.35f),
	COLOR7(0.06f, 0.65f),
	COLOR8(0.06f, 0.98f),
	
	COLOR9(0.09f, 0.02f),
	COLOR10(0.09f, 0.35f),
	COLOR11(0.09f, 0.65f),
	COLOR12(0.09f, 0.98f),
	
	COLOR13(0.12f, 0.02f),
	COLOR14(0.12f, 0.35f),
	COLOR15(0.12f, 0.65f),
	COLOR16(0.12f, 0.98f);

	private final int color;

	/**
	 * Constructor that sets the value of SkinColor.
	 *
	 * @param hue
	 * 		Hue of the skin tone in HSL space
	 * @param lightness
	 * 		Lightness of the skin tone in HSL space
	 */
	SkinColor(float hue, float lightness) {
		float[] hsl = { hue, 0.75f, lightness };
		int[] rgb = new int[4];
		HSL.hsl2rgb(hsl, rgb);
		this.color = ARGB.mergeRgb(rgb);
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
	 * Find the skin color corresponding to an integer color value.
	 *
	 * @param color color as RGB int
	 * @return skin color corresponding to the integer value, or a default value
	 * 	if no skin color matches
	 */
	public static SkinColor fromInteger(int color) {
		for (SkinColor c : values()) {
			if (color == c.color) {
				return c;
			}
		}
		return COLOR1;
	}
}
