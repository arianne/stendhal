/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.styled;

import java.awt.Graphics;

import javax.swing.UIManager;

import games.stendhal.client.sprite.Sprite;

public class StyleUtil {
	/**
	 * Get the current <code>Style</code>, or <code>null</code> if it
	 * has not been set in the UIManager.
	 *
	 * @return Current Style, or <code>null</code> if no Style is in use
	 */
	public static Style getStyle() {
		Object obj = UIManager.get("StendhalStyle");
		if (obj instanceof Style) {
			return (Style) obj;
		}

		return null;
	}

	/**
	 * Fill an area with the background sprite of a {@link Style}.
	 *
	 * @param style the style to be used
	 * @param graphics
	 * @param x left x coordinate
	 * @param y top y coordinate
	 * @param width width of the area
	 * @param height height of the area
	 */
	static void fillBackground(Style style, Graphics graphics, int x,
			int y, int width, int height) {
		// Prepare clipping
		graphics = graphics.create();
		graphics.clipRect(x, y, width, height);

		Sprite image = style.getBackground();

		for (int i = x; i < x + width; i += image.getWidth()) {
			for (int j = y; j < y + height; j += image.getHeight()) {
				image.draw(graphics, i, j);
			}
		}
		graphics.dispose();
	}

	/**
	 * Paint disabled text using a style's highlight and shadow colors.
	 *
	 * @param style style to be used
	 * @param g graphics
	 * @param text painted string
	 * @param x left x coordinate
	 * @param y baseline y coordinate
	 */
	static void paintDisabledText(Style style, Graphics g, String text, int x, int y) {
		g.setColor(style.getHighLightColor());
		g.drawString(text, x + 1, y + 1);
		g.setColor(style.getShadowColor());
		g.drawString(text, x, y);
	}
}
