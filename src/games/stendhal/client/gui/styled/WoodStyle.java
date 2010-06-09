/*
 * @(#) src/games/stendhal/client/gui/styled/WoodStyle.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled;

//
//

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

/**
 * The wood style.
 */
public class WoodStyle implements Style {

	/**
	 * A shared instance.
	 */
	private static Style sharedInstance;

	/**
	 * The background texture.
	 */
	protected Sprite background;

	/**
	 * The border.
	 */
	protected Border border;
	/**
	 * Downwards border (for buttons etc)
	 */
	protected Border borderDown;

	/**
	 * The default font.
	 */
	protected Font font;

	public WoodStyle() {
		/*
		 * Load the texture
		 */
		final SpriteStore st = SpriteStore.get();
		background = st.getSprite("data/gui/panelwood003.jpg");

		border = new SoftBevelBorder(BevelBorder.RAISED, new Color(0.6f, 0.5f,
				0.2f), new Color(0.3f, 0.25f, 0.1f));
		borderDown = new SoftBevelBorder(BevelBorder.LOWERED, new Color(0.6f, 0.5f,
				0.2f), new Color(0.3f, 0.25f, 0.1f));

		font = new Font("Dialog", Font.PLAIN, 12);
	}

	//
	// WoodStyle
	//

	/**
	 * Get a shared instance.
	 * 
	 * @return A shared instance.
	 */
	public static synchronized Style getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new WoodStyle();
		}

		return sharedInstance;
	}

	//
	// Style
	//

	/**
	 * Get the background texture.
	 * 
	 * @return A texture sprite.
	 */
	public Sprite getBackground() {
		return background;
	}

	/**
	 * Get component border.
	 * 
	 * @return A border, or <code>null</code> to use default.
	 */
	public Border getBorder() {
		return border;
	}
	
	/**
	 * Get lowered component border.
	 * 
	 * @return A border, or <code>null</code> to use default.
	 */
	public Border getBorderDown() {
		return borderDown;
	}

	/**
	 * Get the normal font.
	 * 
	 * @return A font.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Get the foreground color appropriate for the background texture.
	 * 
	 * @return A color.
	 */
	public Color getForeground() {
		return Color.white;
	}
}
