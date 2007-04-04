/*
 * @(#) src/games/stendhal/client/gui/styled/swing/StyledJButton.java
 *
 * $Id$
 */

package games.stendhal.client.gui.styled.swing;

//
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.border.Border;

import games.stendhal.client.Sprite;
import games.stendhal.client.gui.styled.Style;

/**
 * A styled JButton.
 */
public class StyledJButton extends JButton {
	/*
	 * Style used.
	 */
	private Style style;

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
		Border border;
		Font font;
		Color color;


		if ((border = style.getBorder()) != null) {
			panel.setBorder(border);
		}

		if ((font = style.getFont()) != null) {
			panel.setFont(font);
		}

		if ((color = style.getForeground()) != null) {
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
		Sprite texture;

		if ((texture = style.getBackground()) != null) {
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
