/*
 * @(#) src/games/stendhal/client/gui/styled/Style.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled;

//
//

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;

import games.stendhal.client.Sprite;

/**
 * Style information.
 */
public interface Style {
	/**
	 * Get the background texture.
	 *
	 * @return	A texture sprite.
	 */
	public Sprite getBackground();


	/**
	 * Get component border.
	 *
	 * @return	A border, or <code>null</code> to use default.
	 */
	public Border getBorder();


	/**
	 * Get the normal font.
	 *
	 * @return	A font.
	 */
	public Font getFont();


	/**
	 * Get the foreground color appropriete for the background texture.
	 *
	 * @return	A color.
	 */
	public Color getForeground();
}
