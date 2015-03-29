package games.stendhal.common.constants;

import java.awt.Color;
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
	private final int colorHash;
	
	public static final List<Integer> ALLOWED_COLORS = Arrays.asList(
			COLOR1.colorHash,
			COLOR2.colorHash,
			COLOR3.colorHash,
			COLOR4.colorHash,
			COLOR5.colorHash,
			COLOR6.colorHash,
			COLOR7.colorHash,
			COLOR8.colorHash,
			COLOR9.colorHash,
			COLOR10.colorHash,
			COLOR11.colorHash,
			COLOR12.colorHash,
			COLOR13.colorHash,
			COLOR14.colorHash,
			COLOR15.colorHash,
			COLOR16.colorHash
			);
	
	/**
	 * Constructor that sets the value of SkinColor.
	 * 
	 * @param color
	 * 		Color to use for this skin tone
	 */
	private SkinColor(int color) {
		this.color = color;
		this.colorHash = Integer.hashCode(color);
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
		
		// If color's hash is found in allowed list return true
		Iterator<Integer> itr = ALLOWED_COLORS.iterator();
		while (itr.hasNext()) {
			if (targetHash == itr.next()) {
				return true; // No need to continue iterations
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
