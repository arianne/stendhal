/*
 * @(#) src/games/stendhal/client/gui/styled/Style.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;

//
//

import games.stendhal.client.sprite.Sprite;

/**
 * Style information.
 */
public interface Style {

	/**
	 * Get the background texture.
	 *
	 * @return A texture sprite.
	 */
	Sprite getBackground();

	/**
	 * Get component border.
	 *
	 * @return A border, or <code>null</code> to use default.
	 */
	Border getBorder();

	/**
	 * Get component border for lowered borders.
	 *
	 * @return border, or <code>null</code> if none defined.
	 */
	Border getBorderDown();

	/**
	 * Get the normal font.
	 *
	 * @return A font.
	 */
	Font getFont();

	/**
	 * Get the foreground color appropriate for the background texture.
	 *
	 * @return A color.
	 */
	Color getForeground();

	/**
	 * Get a light color used for highlighting.
	 *
	 * @return A color.
	 */
	Color getHighLightColor();

	/**
	 * Get a dark color used for darkening.
	 *
	 * @return A color.
	 */
	Color getShadowColor();

	/**
	 * Get a color that roughly represents the background.
	 *
	 * @return plain color
	 */
	Color getPlainColor();
}
