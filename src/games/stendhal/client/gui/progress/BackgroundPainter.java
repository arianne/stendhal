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
package games.stendhal.client.gui.progress;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics;

/**
 * A painter for a background image that consists of 9 32x32 tiles, with the
 * center tile being repeated. The rest of the tiles are the corners and borders
 * in natural order. The sides will be painted with preference to adhering to
 * painted area borders rather than trying to preserve the pattern. Therefore
 * the image should be such that it can tolerate miss tiling at the borders, if
 * the painted area dimensions can not be guaranteed to be multiples of 32.  
 */
class BackgroundPainter {
	private static final int TILE_SIZE = 32;
	
	final Sprite[] images;
	
	/**
	 * Create a new BackgroundPainter.
	 * 
	 * @param image image name. The image should be of size 96x96.
	 */
	BackgroundPainter(String image) {
		SpriteStore store = SpriteStore.get();
		Sprite mother = store.getSprite(image);
		images = new Sprite[9];
		int i = 0;
		for (int y = 0; y < 3 * TILE_SIZE; y += TILE_SIZE) {
			for (int x = 0; x < 3 * TILE_SIZE; x += TILE_SIZE) {
				images[i] = store.getTile(mother, x, y, TILE_SIZE, TILE_SIZE);
				i++;
			}
		}
	}

	/**
	 * Paint an area with the image pattern.
	 * 
	 * @param g graphics
	 * @param width width of the painted area
	 * @param height height of the painted area
	 */
	public void paint(Graphics g, int width, int height) {
		// Paint the center part. That covers the whole area
		Sprite sprite = images[4];
		for (int y = 0; y < height; y += TILE_SIZE) {
			for (int x = 0; x < width; x += TILE_SIZE) {
				sprite.draw(g, x, y);
			}
		}
		// Sides
		// Top row.
		sprite = images[1];
		for (int x = TILE_SIZE; x < width - TILE_SIZE; x += TILE_SIZE) {
			sprite.draw(g, x, 0);
		}
		// left side
		sprite = images[3];
		for (int y = TILE_SIZE; y < height - TILE_SIZE; y += TILE_SIZE) {
			sprite.draw(g, 0, y);
		}
		/*
		 * The rest of the sides will not tile properly, but the background
		 * pattern is subtle enough that it will not be immediately noticeable. 
		 */
		// right side
		sprite = images[5];
		// Do not draw over the left side, but let the scroll overflow from the
		// right if there's no space
		int rightX = Math.max(width - TILE_SIZE, TILE_SIZE);
		for (int y = TILE_SIZE; y < height - TILE_SIZE; y += TILE_SIZE) {
			sprite.draw(g, rightX, y);
		}
		// bottom
		sprite = images[7];
		// Do not draw over the top border, but let the scroll overflow from the
		// bottom if there's no space
		int bottomY = Math.max(height - TILE_SIZE, TILE_SIZE);
		for (int x = TILE_SIZE; x < width - TILE_SIZE; x += TILE_SIZE) {
			sprite.draw(g, x, bottomY);
		}
		
		// Corners. Again, only the first one will tile properly
		// Top left corner
		sprite = images[0];
		sprite.draw(g, 0, 0);
		// Top right corner
		sprite = images[2];
		sprite.draw(g, rightX, 0);
		// Bottom left corner
		sprite = images[6];
		sprite.draw(g, 0, bottomY);
		// Bottom right corner
		sprite = images[8];
		sprite.draw(g, rightX, bottomY);
	}
}
