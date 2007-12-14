/*
 * @(#) src/games/stendhal/client/gui/styled/swing/StyledJPopupMenu.java
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
import java.awt.Insets;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.border.Border;

/**
 * A styled JPopupMenu.
 */
public class StyledJPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = -1607102841664745919L;

	/*
	 * Style used.
	 */
	private Style style;

	/**
	 * Create a styled JPopupMenu.
	 * 
	 * 
	 */
	public StyledJPopupMenu(Style style, String label) {
		super(label);

		setDefaultLightWeightPopupEnabled(false);

		this.style = style;

		applyStyle(style, this);
	}

	//
	// StyledJPopupMenu
	//

	/**
	 * Apply style information to a menu item.
	 * 
	 */
	protected void applyStyle(Style style, JMenuItem item) {
		Color color = style.getForeground();
		Border border = style.getBorder();
		Font font = style.getFont();

		if (style.getBackground() != null) {
			item.setOpaque(false);
		}

		if (border != null) {
			item.setBorder(border);

			/*
			 * TODO: XXX - Does this help??
			 */
			item.setMargin(new Insets(0, 0, 0, 0));
		}

		if (color != null) {
			item.setForeground(color);
		}

		if (font != null) {
			item.setFont(font);
		}
	}

	/**
	 * Apply style information to a menu.
	 * 
	 */
	protected void applyStyle(Style style, JPopupMenu menu) {
		Border border = style.getBorder();

		if (border != null) {
			menu.setBorder(border);
		}

		for (MenuElement item : menu.getSubElements()) {
			applyStyle(style, (JMenuItem) item.getComponent());
		}
	}

	//
	// JPopupMenu
	//

	/**
	 * Add a menu item, applying style.
	 * 
	 */
	@Override
	public JMenuItem add(JMenuItem item) {
		applyStyle(style, item);

		return super.add(item);
	}

	//
	// Component
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
