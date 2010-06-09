package games.stendhal.client.gui.styled;

import java.awt.Graphics;

import games.stendhal.client.sprite.Sprite;

class StyleUtil {
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
	public static void fillBackground(Style style, Graphics graphics, int x, 
			int y, int width, int height) {
		// Prepare clipping
		graphics = graphics.create();
		graphics.setClip(x, y, width, height);
		
		Sprite image = style.getBackground();
		
		for (int i = x; i < x + width; i += image.getWidth()) {
			for (int j = y; j < y + height; j += image.getHeight()) {
				image.draw(graphics, i, j);
			}
		}
		graphics.dispose();
	}
}
