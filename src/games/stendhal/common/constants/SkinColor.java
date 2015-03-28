package games.stendhal.common.constants;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


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
	COLOR8(0xffd8c600);
	
	private final int color;
	
	public static final List<Color> ALLOWED_COLORS = Arrays.asList(
			COLOR1.getColor(),
			COLOR2.getColor(),
			COLOR3.getColor(),
			COLOR4.getColor(),
			COLOR5.getColor(),
			COLOR6.getColor(),
			COLOR7.getColor(),
			COLOR8.getColor()
			);
	
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
	 * Retrieve the color of a SkinColor.
	 * 
	 * @return
	 * 		Color value of SkinColor
	 */
	public Color getColor() {
		return new Color(this.color);
	}
	
	/**
	 * Check if a Color can be used for skin.
	 * 
	 * @param color
	 * 		Color to be tested
	 * @return
	 * 		Color can be used
	 */
	public static Boolean isAllowed(Color targetColor) {
		return isAllowed(targetColor.hashCode());
	}
	
	/**
	 * Use a color's hash code to check if it allowed for skin.
	 * 
	 * @param targetHash
	 * 		The hash code of the target color's integer value
	 * @return
	 * 		Color can be used
	 */
	public static Boolean isAllowed(int targetHash) {
		Boolean allowed = false;
		
		// If color is found in allowed list return true
		Iterator<Color> itr = ALLOWED_COLORS.iterator();
		while (itr.hasNext()) {
			if (targetHash == itr.next().hashCode()) {
				allowed = true;
			}
		}
		
		// Throw an error if the desired color is not allowed
		if (!allowed) {
			throw new IllegalArgumentException("Color "
					+ Integer.toString(targetHash) + " ("
					+ Integer.toHexString(targetHash) + ")"
					+ " cannot be used for skin color.");
		}
		
		return allowed;
	}
}
