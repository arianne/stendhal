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

/**
 * Acceptable colors that can be used for skin
 * 
 * @author AntumDeluge
 */
public enum SkinColor {
	COLOR1(0xff513216),
	COLOR2(0xff827940),
	COLOR3(0xffd8d79a),
	COLOR4(0xff3b311c),
	COLOR5(0xffa29475),
	COLOR6(0xff804a2f),
	COLOR7(0xff6c5a33),
	COLOR8(0xffd8cc47),
	COLOR9(0xff3b190f),
	COLOR10(0xff602710),
	COLOR11(0xffac8121),
	COLOR12(0xff16120a),
	COLOR13(0xff764b00),
	COLOR14(0xffbfb4ae),
	COLOR15(0xff966c00),
	COLOR16(0xffb59d55);
	
	private final int color;
	
	/**
	 * Constructor that sets the value of SkinColor.
	 * 
	 * @param color
	 * 		Color to use for this skin tone
	 */
	private SkinColor(int color) {
		this.color = color;
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
