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
	COLOR1(0x513216),
	COLOR2(0x827940),
	COLOR3(0xd8d79a),
	COLOR4(0x3b311c),
	COLOR5(0xa29475),
	COLOR6(0x804a2f),
	COLOR7(0x6c5a33),
	COLOR8(0xd8c600);
	
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
	 * Check if a color code can be used for skin color.
	 * 
	 * @param colorValue
	 * 		The hex color code
	 * @return
	 * 		Color can be used
	 */
	public static Boolean isAllowed(int colorValue) {
		Boolean allowed = false;
		final Color testedColor = new Color(colorValue);
		
		// If color is found in allowed list return true
		Iterator<Color> itr = new ArrayList<Color>().iterator();
		while (itr.hasNext()) {
			if (itr.next().equals(testedColor)) {
				allowed = true;
			}
		}
		
		return allowed;
	}
}
