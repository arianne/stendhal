/*
 * @(#) src/games/stendhal/client/gui/styled/swing/StyledJPanel.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled.swing;

//
//

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.sprite.Sprite;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A styled JPanel.
 */
public class StyledJPanel extends JPanel {
	private static final long serialVersionUID = -1607102841664745919L;

	/*
	 * Style used.
	 */
	private Style style;

	/**
	 * Create a styled JPanel.
	 * @param style to be applied
	 * 
	 * 
	 */
	public StyledJPanel(Style style) {
		this.style = style;

		applyStyle(style, this);
	}

	//
	// StyledJPanel
	//

	/**
	 * Apply style information to a component.
	 * @param style to be applied
	 * @param panel the style is to be applied to
	 * 
	 */
	protected void applyStyle(Style style, JPanel panel) {
		Border border = style.getBorder();
		Font font = style.getFont();

		if (border != null) {
			panel.setBorder(border);
		}

		if (font != null) {
			panel.setFont(font);
		}
	}

	//
	// JComponent
	//

	/**
	 * Paint the component background.
	 * 
	 * 
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Sprite texture = style.getBackground();

		if (texture != null) {
			int twidth;
			int theight;
			int width;
			int height;
			int x;
			int y;

			twidth = texture.getWidth();
			theight = texture.getHeight();

			width = getWidth();
			height = getHeight();

			for (x = 0; x < width; x += twidth) {
				for (y = 0; y < height; y += theight) {
					texture.draw(g, x, y);
				}
			}
		} else {
			super.paintComponent(g);
		}
	}
}
