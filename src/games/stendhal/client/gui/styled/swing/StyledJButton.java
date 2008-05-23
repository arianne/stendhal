/*
 * @(#) src/games/stendhal/client/gui/styled/swing/StyledJButton.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled.swing;

//
//

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.sprite.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * A styled JButton.
 */
public class StyledJButton extends JButton {
	/*
	 * Style used.
	 */
	private Style style;
	private static final long serialVersionUID = -1607102841664745919L;

	/**
	 * Create a styled JButton.
	 * 
	 * 
	 */
	public StyledJButton(Style style) {
		this.style = style;

		applyStyle(style, this);
	}

	//
	// StyledJButton
	//

	/**
	 * Apply style information to a component.
	 * 
	 */
	protected void applyStyle(Style style, JButton panel) {
		Border border = style.getBorder();
		Font font = style.getFont();
		Color color = style.getForeground();

		if (border != null) {
			panel.setBorder(border);
		}

		if (font != null) {
			panel.setFont(font);
		}

		if (color != null) {
			panel.setForeground(color);
		}

		setContentAreaFilled(false);
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
		}

		super.paintComponent(g);
	}
}
